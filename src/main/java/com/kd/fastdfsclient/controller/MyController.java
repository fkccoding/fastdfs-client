package com.kd.fastdfsclient.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kd.fastdfsclient.entity.FileInfo;
import com.kd.fastdfsclient.fastdfs.FastDFSClient;
import com.kd.fastdfsclient.mapper.FileInfoMapper;
import com.kd.fastdfsclient.service.FileInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.Cleanup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.*;

@CrossOrigin(maxAge = 3600)     //解决跨域
@RestController
@Api(tags = "fileController", description = "文件系统后台管理")
public class MyController {
    private static Logger logger = LoggerFactory.getLogger(MyController.class);

    @Autowired
    FileInfoService fileInfoService;

    @Autowired
    FileInfoMapper fileInfoMapper;

    private Map<String, String> operators = new HashMap<>();

//    private String tempFileName;

    MyController(){
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
    public Map<String, Integer> singleFileUpload(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
        Map<String, Integer> msg = new HashMap<>();
        String fileName = file.getOriginalFilename();
        long realSize = file.getSize();

        synchronized (this) {
            if (fileInfoService.findFileByName(fileName) != null) {
                msg.put("msg", 1);
                logger.info("The file name is occupied");
                return msg;
            }
            fileInfoMapper.insert(new FileInfo(fileName));
        }

        // count file size for human
        String hFileSize = getHumanSize(realSize);
        String operator = operators.get(request.getRemoteAddr());
        if (null == operator) {     //If the user's IP is illegal
            operator = "Hacker";
        }
        try {
            // Get the file and save it somewhere
            String[] strings = FastDFSClient.saveFile(file);
            String path = strings[0];
            FileInfo fileInfo = new FileInfo(fileName, strings[1], strings[2], new Date(), hFileSize, realSize, 1.0, operator);
//            fileInfoService.save(fileInfo);
            fileInfoMapper.updateById(fileInfo);
        } catch (Exception e) {
            fileInfoMapper.deleteByFileName(fileName);
            logger.error("upload file failed", e);
            msg.put("msg", 2);
            return msg;
        }
        msg.put("msg", 3);
        return msg;
    }

    private String getHumanSize(double realSize) {
        double fileSize = realSize;
        String hFileSize = fileSize + "B";
        if (fileSize > 1024 && fileSize / 1024 <= 1024) {
            BigDecimal bd = new BigDecimal(fileSize / 1024);
            hFileSize = bd.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue() + "KB";
        }
        if (fileSize / 1024 > 1024) {
            BigDecimal bd = new BigDecimal(fileSize / 1024 / 1024);
            hFileSize = bd.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue() + "MB";
        }
        return hFileSize;
    }

    @GetMapping("/findByName")
    public FileInfo findByName(String filename) {
        return fileInfoService.findFileByName(filename);
    }

    @ApiOperation("单个或批量删除文件")
    @PostMapping("/delete")
    public String delete(@RequestBody Map<String, String[]> json) throws Exception {
        String[] fileNameList = json.get("fileNameList");
        for (int i = 0; i < fileNameList.length; i++) {
            String fileName = fileNameList[i];
            FileInfo fileByName = fileInfoService.findFileByName(fileName);
            if (null == fileByName) {
                return "file not found";
            } else {
                String deleteFileMsg = FastDFSClient.deleteFile(fileByName.getGroupName(), fileByName.getRemoteFileName());
                logger.info(deleteFileMsg);
                fileInfoService.deleteByFileName(fileName);
            }
        }
        return "success";
    }

    @ApiOperation("下载文件")
    @GetMapping("/downFile")
    public String downFile(String fileName, HttpServletResponse response) {
        FileInfo fileByName = fileInfoService.findFileByName(fileName);
        if (null == fileByName) {
            logger.error("File not found!!!");
            return "File not found!!!";
        }
        @Cleanup InputStream input = FastDFSClient.downFile(fileByName.getGroupName(), fileByName.getRemoteFileName());
        int index;
        byte[] bytes = new byte[1024];
        @Cleanup OutputStream outputStream;
        try {
            response.setHeader("Content-type", "application/octet-stream");
//            response.setHeader("Content-disposition", "attachment;fileName=" + URLEncoder.encode(filename,"UTF-8"));
            response.setHeader("Content-disposition", "attachment;fileName=" + new String(fileName.getBytes(), "ISO-8859-1"));
            outputStream = response.getOutputStream();
            while ((index = input.read(bytes)) != -1) {
                outputStream.write(bytes, 0, index);
                outputStream.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        logger.info("Join download queue");
        return "success!";
    }

    /**
     * @param current 当前的页数
     * @param size    每页的大小
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
            String suffix = (String) categoryToSuffix(category).get("suffix");
            boolean other = (boolean) categoryToSuffix(category).get("other");
            count = fileInfoService.selectCountByREGEXP(suffix, other);
            fileInfoList = fileInfoService.selectListByREGEXP(suffix, other, current, size, order, asc);
        }

        map.put("total", count);
        map.put("fileList", fileInfoList);
        return map;
    }

    Map<String, Object> categoryToSuffix(String category) {
        Map<String, Object> map = new HashMap<String, Object>();
        String suffix;
        boolean other = false;
        if ("image".equals(category)) {
            suffix = ".jpg|.bmp|.gif|.ico|.pcx|.jpeg|.tif|.png|.raw|.tga|.svg|.webp";
        } else if ("doc".equals(category)) {
            suffix = ".doc|.docx|.dot|.dotx|.dotm|.rtf|.xls|.xlsx|.ppt|.pptx|.txt|.pdf";
        } else if ("video".equals(category)) {
            suffix = ".avi|.mp4|.rmvb|.mpeg|.mov|.mkv|.wmv|.flv|.webm";
        } else if ("music".equals(category)) {
            suffix = ".mp3|.aac|.wav|.flav|.ape|.alac|.flac";
        } else if ("all".equals(category)) {
            suffix = ".";
        } else {
            suffix = ".jpg|.bmp|.gif|.ico|.pcx|.jpeg|.tif|.png|.raw|.tga|.svg|.webp" +
                    ".doc|.docx|.dot|.dotx|.dotm|.rtf|.xls|.xlsx|.ppt|.pptx|.txt|.pdf|" +
                    ".avi|.mp4|.rmvb|.mpeg|.mov|.mkv|.wmv|.flv|.webm|" +
                    ".mp3|.aac|.wav|.flav|.ape|.alac|.flac";
            other = true;
        }
        map.put("suffix", suffix);
        map.put("other", other);
        return map;
    }
}