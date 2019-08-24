package com.kd.fastdfsclient.controller;

import com.kd.fastdfsclient.entity.FileInfo;
import com.kd.fastdfsclient.entity.ShareFileInfo;
import com.kd.fastdfsclient.fastdfs.FastDFSClient;
import com.kd.fastdfsclient.service.FileDownLoadService;
import com.kd.fastdfsclient.service.FileInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.Cleanup;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Author: www.chuckfang.top
 * Date: 2019/4/3 14:51
 */
@CrossOrigin(maxAge = 3600)    // 解决跨域问题
@RestController
@Api(tags = "fileController", description = "文件系统后台管理")
// 使用lombok的注解，避免在类的成员上使用@Autowired注解，符合Spring规范
// 而是自动生成构造方法注入对象的代码，并且可以防止注入静态对象时出现问题
// @RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class FileController {
    @Autowired
    private  FileInfoService fileInfoService;

    @Autowired
    @Qualifier("default")
    private  FileDownLoadService fileDownLoadService;

    @ApiOperation("上传文件(同时支持单文件和多文件)")
    @PostMapping("/upload")
    public List<Integer> fileUpload(@RequestParam("file") MultipartFile[] fileList, HttpServletRequest request) {
        // Prevent conflicts when multiple files are uploaded at the same time
        // 因为LinkedList插入元素时会新建Node对象
        List<Integer> result = new ArrayList<>();
        for (MultipartFile multipartFile : fileList) {
            synchronized (this) {
                fileInfoService.updateVersion(multipartFile.getOriginalFilename());
                int i = fileInfoService.saveFile(multipartFile, request.getRemoteAddr());
                result.add(i);
            }
        }
//        return result.stream().mapToInt(Integer::intValue).toArray();
        return result;
    }

    /**
     * @param groupName
     * @param remoteFileName
     * @param response
     * @return
     */
    @ApiOperation("下载文件")
    @GetMapping("/downFile")
    public String downFile(String groupName, String remoteFileName, HttpServletResponse response) {
        try {
            fileDownLoadService.downFile(groupName,remoteFileName,response);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return "success!";
    }

    /**
     * 当用户选中多个文件时，可以把多个文件打包成一个压缩包，用户下载的是压缩包，而不是一个一个的文件
     * @param groupNameList
     * @param remoteFileNameList
     * @param response
     * @return
     */
    @ApiOperation("批量下载文件")
    @GetMapping("/zipDownFile")
    public String zipDownFile(String[] groupNameList, String[] remoteFileNameList, HttpServletResponse response) {
        if (groupNameList == null || remoteFileNameList == null) {
            return "字段传输错误！";
        }
        return fileDownLoadService.zipDownFile(groupNameList,remoteFileNameList,response);
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
        for (String fileName : fileNameList) {
            //delete file include the old version
            List<FileInfo> allFileByName = fileInfoService.findAllFileByName(fileName);
            for (FileInfo fileByName : allFileByName) {
                if (null == fileByName) {
                    log.error("File not found!!!");
                    return "file not found";
                } else {
                    String deleteFileMsg = FastDFSClient.deleteFile(fileByName.getGroupName(), fileByName.getRemoteFileName());
                    log.info(deleteFileMsg);
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
            count = fileInfoService.searchCount("%" + fileName + "%");
            fileInfoList = fileInfoService.fuzzySearch("%" + fileName + "%",
                    (current - 1) * size, size, order, asc);
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
        return fileInfoService.findAllFileByName(fileName);
    }

    @ApiOperation("回退历史版本")
    @GetMapping("/revert")
    public void revert(String fileName, String remoteFileName) {
        fileInfoService.revert(fileName, remoteFileName);
    }


    @ApiOperation("分享文件获取文件信息")
    @GetMapping("/s/{fileId}")
    public FileInfo share(@Valid @PathVariable("fileId") String fileId){
        return fileInfoService.findFileById(fileId);

    }
}