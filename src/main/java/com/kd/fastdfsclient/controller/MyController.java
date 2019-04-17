package com.kd.fastdfsclient.controller;

import com.kd.fastdfsclient.entity.FileInfo;
import com.kd.fastdfsclient.fastdfs.FastDFSClient;
import com.kd.fastdfsclient.fastdfs.FastDFSFile;
import com.kd.fastdfsclient.service.FileInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.Date;

@Controller
public class MyController {
    private static Logger logger = LoggerFactory.getLogger(MyController.class);

    @Autowired
    FileInfoService fileInfoService;

    @GetMapping("/")
    public String index() {
        return "do";
    }

    @PostMapping("/upload")
    public String singleFileUpload(@RequestParam("file") MultipartFile file,
                                   RedirectAttributes redirectAttributes) {
        double fileSize = file.getSize();
        String hFileSize = "";
        // count file size
        if (fileSize > 1024 && fileSize / 1024 <= 1024) {
            BigDecimal bd = new BigDecimal(fileSize / 1024);
            hFileSize = bd.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue() + "KB";
        }
        if (fileSize / 1024 > 1024) {
            BigDecimal bd = new BigDecimal(fileSize / 1024 / 1024);
            hFileSize = bd.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue() + "MB";
        }
        String filename = file.getOriginalFilename();
//        System.out.println("filename = " + filename);
        FileInfo fileByName = fileInfoService.findFileByName(filename);
        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "Please select a file to upload");
            return "redirect:status";
        } else if (fileByName != null) {
            redirectAttributes.addFlashAttribute("message", "文件名重复，请重新上传！");
            return "redirect:status";
        }
        try {
            // Get the file and save it somewhere
            String[] strings = FastDFSClient.saveFile(file);
            String path = strings[0];
            FileInfo fileInfo = new FileInfo(filename, strings[1], strings[2], new Date(),hFileSize, 1.0);
            fileInfoService.save(fileInfo);
            redirectAttributes.addFlashAttribute("message",
                    "You successfully uploaded '" + file.getOriginalFilename() + "'");
            redirectAttributes.addFlashAttribute("path",
                    "file path url '" + path + "'");
        } catch (Exception e) {
            logger.error("upload file failed", e);
        }
        return "redirect:status";
    }

    @GetMapping("/status")
    public String uploadStatus() {
        return "status";
    }

    @ResponseBody
    @GetMapping("/findByName")
    public FileInfo findByName(String filename){
        return fileInfoService.findFileByName(filename);
    }

    @GetMapping("/delete")
    public String delete(@RequestParam("filename") String filename,
                         RedirectAttributes redirectAttributes) throws Exception {
        FileInfo fileByName = fileInfoService.findFileByName(filename);
        String message = FastDFSClient.deleteFile(fileByName.getGroupName(), fileByName.getRemoteFileName());
        fileInfoService.deleteByFileName(filename);
        redirectAttributes.addFlashAttribute("message", message);
        return "redirect:status";
    }

    @GetMapping("/downFile")
    public void downFile(String filename, HttpServletResponse response) {
        FileInfo fileByName = fileInfoService.findFileByName(filename);
        InputStream input = FastDFSClient.downFile(fileByName.getGroupName(), fileByName.getRemoteFileName());
        int index;
        byte[] bytes = new byte[1024];
        OutputStream outputStream = null;
        try {
            response.setHeader("Content-type", "application/octet-stream");
//            response.setHeader("Content-disposition", "attachment;fileName=" + URLEncoder.encode(filename,"UTF-8"));
            response.setHeader("Content-disposition", "attachment;fileName=" + new String(filename.getBytes(), "ISO-8859-1"));
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
}