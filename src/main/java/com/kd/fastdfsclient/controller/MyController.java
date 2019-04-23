package com.kd.fastdfsclient.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kd.fastdfsclient.entity.FileInfo;
import com.kd.fastdfsclient.fastdfs.FastDFSClient;
import com.kd.fastdfsclient.mapper.FileInfoMapper;
import com.kd.fastdfsclient.service.FileInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.*;

@CrossOrigin(maxAge = 3600)     //解决跨域
@Controller
@Api(tags = "fileController", description = "文件系统后台管理")
public class MyController {
    private static Logger logger = LoggerFactory.getLogger(MyController.class);

    @Autowired
    FileInfoService fileInfoService;

    @Autowired
    FileInfoMapper fileInfoMapper;

    @GetMapping("/")
    public String index() {
        return "do";
    }

    @GetMapping("/status")
    public String uploadStatus() {
        return "status";
    }


    @ApiOperation("上传文件")
    @PostMapping("/upload")
    @ResponseBody
    public Map singleFileUpload(@RequestParam("file") MultipartFile file,
                                RedirectAttributes redirectAttributes) {
        Map msg = new HashMap<String, Integer>();
        long realSize = file.getSize();
        String filename = file.getOriginalFilename();
        FileInfo fileByName = fileInfoService.findFileByName(filename);
        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "Please select a file to upload");
            msg.put("msg", 4);
            return msg;
            //return "redirect:status";
        } else if (fileByName != null) {
            redirectAttributes.addFlashAttribute("message", "文件名重复");
            msg.put("msg", 1);
            return msg;
            //return "redirect:status";
        }

        // count file size for human
        double fileSize = (double) realSize;
        String hFileSize = fileSize + "B";
        if (fileSize > 1024 && fileSize / 1024 <= 1024) {
            BigDecimal bd = new BigDecimal(fileSize / 1024);
            hFileSize = bd.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue() + "KB";
        }
        if (fileSize / 1024 > 1024) {
            BigDecimal bd = new BigDecimal(fileSize / 1024 / 1024);
            hFileSize = bd.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue() + "MB";
        }

        // 随机选择一个operator
        String[] operators = {"方程","武瑞敏","魏健东","向凌吉","江天","汪垚","曲广昊","崔志臣","卢荟芳","梅峥",
                "张飞","司小贺","王鑫","海军","杨喻强","王梓任","尚丽志","高洪伟","王旭伟","栾希鹏"};
        Random random = new Random();//默认构造方法
        int i = random.nextInt(operators.length);
        String operator = operators[i];
        try {
            // Get the file and save it somewhere
            String[] strings = FastDFSClient.saveFile(file);
            String path = strings[0];
            FileInfo fileInfo = new FileInfo(filename, strings[1], strings[2], new Date(), hFileSize, realSize,
                    1.0, operator);
            fileInfoService.save(fileInfo);
            redirectAttributes.addFlashAttribute("message",
                    "You successfully uploaded '" + file.getOriginalFilename() + "'");
            redirectAttributes.addFlashAttribute("path",
                    "file path url '" + path + "'");
        } catch (Exception e) {
            logger.error("upload file failed", e);
            msg.put("msg", 2);
        }

        msg.put("msg", 3);
        return msg;
        //return "redirect:status";
    }

    @GetMapping("/findByName")
    @ResponseBody
    public FileInfo findByName(String filename) {
        return fileInfoService.findFileByName(filename);
    }

    @ApiOperation("单个或批量删除文件")
    @PostMapping("/delete")
    @ResponseBody
    public String delete(@RequestBody Map<String,String[]> json/*,
                      RedirectAttributes redirectAttributes*/) throws Exception {
//        System.out.println("json"+json);
        String[] fileNameList = json.get("fileNameList");
//        System.out.println(Arrays.toString(fileNameList));
        for (int i = 0; i<fileNameList.length; i++) {
            String filename = fileNameList[i];
            FileInfo fileByName = fileInfoService.findFileByName(filename);
            if (null == fileByName) {
                return "file not found";
            } else {
                FastDFSClient.deleteFile(fileByName.getGroupName(), fileByName.getRemoteFileName());
                fileInfoService.deleteByFileName(filename);
                //redirectAttributes.addFlashAttribute("message", message);
            }
        }
        //return "redirect:status";
        return "success";
    }

    @ApiOperation("下载文件")
    @GetMapping("/downFile")
    public void downFile(String fileName, HttpServletResponse response) {
        FileInfo fileByName = fileInfoService.findFileByName(fileName);
        InputStream input = FastDFSClient.downFile(fileByName.getGroupName(), fileByName.getRemoteFileName());
        int index;
        byte[] bytes = new byte[1024];
        OutputStream outputStream = null;
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
        } finally {
            try {
                input.close();
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * @param current 当前的页数
     * @param size    每页的大小
     * @return 文件信息列表
     */
    @ApiOperation("分页获取文件分类信息或按文件名模糊搜索")
    @GetMapping("/listPage")
    @ResponseBody
    public Map listPage(@RequestParam(value = "category", defaultValue = "all") String category,
                        @RequestParam(value = "current", defaultValue = "1") long current,
                        @RequestParam(value = "size", defaultValue = "10") long size,
                        @RequestParam(value = "order", defaultValue = "upload_date") String order,
                        @RequestParam(value = "asc", defaultValue = "true") boolean asc,
                        @RequestParam(value = "fileName",defaultValue = "") String fileName) {
        Map map = new HashMap();
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

    public Map<String, Object> categoryToSuffix(String category) {
        Map map = new HashMap<String, Object>();
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