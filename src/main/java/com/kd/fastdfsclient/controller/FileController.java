package com.kd.fastdfsclient.controller;

import com.kd.fastdfsclient.entity.FileInfo;
import com.kd.fastdfsclient.fastdfs.FastDFSClient;
import com.kd.fastdfsclient.mapper.FileInfoMapper;
import com.kd.fastdfsclient.service.FileInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.Cleanup;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;
@CrossOrigin(maxAge = 3600)     //解决跨域
@RestController
@Api(tags = "fileController", description = "文件系统后台管理")
public class FileController {
    private static Logger logger = LoggerFactory.getLogger(FileController.class);

    @Autowired
    FileInfoService fileInfoService;

    @Autowired
    FileInfoMapper fileInfoMapper;

    private Map<String, String> operators = new HashMap<>();

    FileController(){
        operators.put("192.100.1.210", "曲广昊");
        operators.put("192.100.1.211", "方程");
        operators.put("192.100.1.213", "崔志臣");
        operators.put("192.100.1.230", "武瑞敏");
        operators.put("192.100.1.231", "汪垚");
        operators.put("192.100.1.232", "向凌吉");
        operators.put("192.100.1.233", "江天");
        operators.put("192.100.1.237", "卢荟芳");
        operators.put("192.100.1.123", "魏健东");
    }

    @ApiOperation("上传文件")
    @PostMapping("/upload")
    @Transactional
    public Map<String, Integer> singleFileUpload(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
        Map<String, Integer> msg = new HashMap<>();
        String fileName = file.getOriginalFilename();
        long realSize = file.getSize();

        // Prevent conflicts when multiple files are uploaded at the same time
        synchronized (this) {
            fileInfoService.updateVersion(fileName);
        }
        try {// Get the file and save it to FastDFS somewhere
            String[] strings = FastDFSClient.saveFile(file);
            // Save FileInfo to mysql
            //      count file size for human
            String hFileSize = fileInfoService.getHumanSize(realSize);
            String operator = operators.get(request.getRemoteAddr());
            if (null == operator) {     //If the user's IP is illegal
                operator = "Hacker";
            }
            fileInfoService.save(new FileInfo(fileName,strings[1],strings[2],new Date(),hFileSize,realSize,1.0,operator,1));
            logger.info("upload path is " + strings[0]);
        } catch (Exception e) {
            logger.error("upload file failed", e);
            msg.put("msg", 2);
            return msg;
        }
        msg.put("msg", 3);
        return msg;
    }


    @ApiOperation("单个或批量删除文件")
    @PostMapping("/delete")
    public String delete(@RequestBody Map<String, String[]> json) throws Exception {
        String[] fileNameList = json.get("fileNameList");
        for (int i = 0; i < fileNameList.length; i++) {
            String fileName = fileNameList[i];
            //delete file include the old version
            List<FileInfo> allFileByName = fileInfoMapper.findAllFileByName(fileName);
            for (FileInfo fileByName: allFileByName) {
                if (null == fileByName) {
                    logger.error("File not found!!!");
                    return "file not found";
                } else {
                    String deleteFileMsg = FastDFSClient.deleteFile(fileByName.getGroupName(), fileByName.getRemoteFileName());
                    logger.info(deleteFileMsg);
                    fileInfoService.deleteByFileName(fileName);
                }
            }
        }
        return "success";
    }

    /**
     * @param groupName
     * @param remoteFileName
     * @param response
     * @return
     */
    @ApiOperation("下载文件")
    @GetMapping("/downFile")
    @SneakyThrows //可以对受检异常进行捕捉并抛出
    public String downFile(@RequestParam("groupName") String groupName, @RequestParam("remoteFileName") String remoteFileName, HttpServletResponse response) {
        String fileName = fileInfoMapper.findFileByGroupAndRemoteFileName(groupName, remoteFileName);
        if (null == fileName) {
            logger.error("File not found!!!");
            return "File not found!!!";
        }
        // @Cleanup 自动关闭资源，针对实现了java.io.Closeable接口的对象有效
        @Cleanup BufferedInputStream input = (BufferedInputStream) FastDFSClient.downFile(groupName, remoteFileName);
        int index;
        byte[] bytes = new byte[1024];
        @Cleanup ServletOutputStream outputStream = null;
        try {
            response.setHeader("Content-type", "application/octet-stream");
          //response.setHeader("Content-disposition", "attachment;fileName=" + URLEncoder.encode(filename,"UTF-8"));
            response.setHeader("Content-disposition", "attachment;fileName=" + new String(fileName.getBytes(), "ISO-8859-1"));
            outputStream = response.getOutputStream();
            while ((index = input.read(bytes)) != -1) {
                outputStream.write(bytes, 0, index);
                outputStream.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        logger.info("Join download queue...");
        return "success!";
    }

    /**
     *
     * @param category 分类
     * @param current 当前的页数
     * @param size 每页的大小
     * @param order 排序规则
     * @param asc 是否正序
     * @param fileName 文件名，如果非空则表示使用模糊搜索功能
     * @return 文件信息列表
     */
    @ApiOperation("分页获取文件分类信息或按文件名模糊搜索")
    @GetMapping("/listPage")
    public Map<String, Object> listPage(@RequestParam(value = "category", defaultValue = "all") String category,
                                        @RequestParam(value = "current", defaultValue = "1") long current,
                                        @RequestParam(value = "size", defaultValue = "10") long size,
                                        @RequestParam(value = "order", defaultValue = "upload_date") String order,
                                        @RequestParam(value = "asc", defaultValue = "true") boolean asc,
                                        @RequestParam(value = "fileName", defaultValue = "") String fileName) {
        Map<String, Object> map = new HashMap<>();
        List<FileInfo> fileInfoList;
        int count;
        // 如果有非空文件名传过来，说明使用模糊搜索的功能，否则使用分类功能
        if (null != fileName && !"".equals(fileName)) {
            count = fileInfoMapper.searchCount("%" + fileName + "%");
            fileInfoList = fileInfoService.searchPage("%" + fileName + "%", (current - 1) * size, size, order, asc);
        } else {
            String suffix = (String) fileInfoService.categoryToSuffix(category).get("suffix");
            boolean other = (boolean) fileInfoService.categoryToSuffix(category).get("other");
            count = fileInfoService.selectCountByREGEXP(suffix, other);
            fileInfoList = fileInfoService.selectListByREGEXP(suffix, other, current, size, order, asc);
        }

        map.put("total", count);
        map.put("fileList", fileInfoList);
        return map;
    }

    @ApiOperation("获取历史列表")
    @GetMapping("/showHistory")
    public List<FileInfo> showHistory(String fileName){
        return fileInfoMapper.findAllFileByName(fileName);
    }

    @ApiOperation("回退历史版本")
    @GetMapping("/revert")
    public void revert(String fileName, String remoteFileName) {
        fileInfoService.revert(fileName, remoteFileName);
    }
}