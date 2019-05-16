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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;

/**
 * @Author: www.chuckfang.top
 * @Date: 2019/4/3 14:51
 */
@CrossOrigin(maxAge = 3600)    // 解决跨域问题
@RestController
@Api(tags = "fileController", description = "文件系统后台管理")
public class FileController {
    private static Logger logger = LoggerFactory.getLogger(FileController.class);

    @Autowired
    FileInfoService fileInfoService;

    @Autowired
    FileInfoMapper fileInfoMapper;

    @ApiOperation("上传文件(同时支持单文件和多文件)")
    @PostMapping("/upload")
    public int[] fileUpload(@RequestParam("file") MultipartFile[] fileList, HttpServletRequest request) {
        // Prevent conflicts when multiple files are uploaded at the same time
        List<Integer> result = new LinkedList<>();
        for (MultipartFile multipartFile : fileList) {
            synchronized (this) {
                fileInfoService.updateVersion(multipartFile.getOriginalFilename());
                int i = fileInfoService.saveFile(multipartFile, request.getRemoteAddr());
                result.add(i);
            }
        }
        return result.stream().mapToInt(Integer::intValue).toArray();
    }

    /**
     * 上传文件之前先检查一遍是否有重名文件
     * @param fileName
     * @return
     */
    @ApiOperation("查看是否已经存在文件")
    @GetMapping("/findFile")
    public boolean findFile(String fileName) {
        return fileInfoService.findFileByName(fileName) == null;
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

    @ApiOperation("删除某个历史文件")
    @GetMapping("/deleteHistory")
    public boolean deleteHistory(String groupName, String remoteFileName) {
        return fileInfoService.deleteHistory(groupName, remoteFileName);
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
        @Cleanup InputStream input = FastDFSClient.downFile(groupName, remoteFileName);
        int index;
        byte[] bytes = new byte[1024];
        @Cleanup ServletOutputStream outputStream = null;
        try {
            response.setHeader("Content-type", "application/octet-stream");
            response.setHeader("Content-disposition", "attachment;fileName=" + new String(fileName.getBytes(), "ISO-8859-1"));
            outputStream = response.getOutputStream();
            logger.info("Join download queue...");
            while ((index = input.read(bytes)) != -1) {
                outputStream.write(bytes, 0, index);
                outputStream.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "success!";
    }

    /**
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
            count = fileInfoService.selectCount(category);
            fileInfoList = fileInfoService.selectList(category, current, size, order, asc);
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