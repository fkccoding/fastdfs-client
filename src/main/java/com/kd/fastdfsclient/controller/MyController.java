package com.kd.fastdfsclient.controller;

import com.kd.fastdfsclient.fastdfs.FastDFSClient;
import com.kd.fastdfsclient.fastdfs.FastDFSFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

@Controller
public class MyController {
    private static Logger logger = LoggerFactory.getLogger(MyController.class);

    @GetMapping("/")
    public String index() {
        return "do";
    }

    @PostMapping("/upload") //new annotation since 4.3
    public String singleFileUpload(@RequestParam("file") MultipartFile file,
                                   RedirectAttributes redirectAttributes) {
        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "Please select a file to upload");
            return "redirect:status";
        }
        try {
            // Get the file and save it somewhere
            String path=saveFile(file);
            redirectAttributes.addFlashAttribute("message",
                    "You successfully uploaded '" + file.getOriginalFilename() + "'");
            redirectAttributes.addFlashAttribute("path",
                    "file path url '" + path + "'");
        } catch (Exception e) {
            logger.error("upload file failed",e);
        }
        return "redirect:/status";
    }

    @GetMapping("/status")
    public String uploadStatus() {
        return "status";
    }

    /**
     * @param multipartFile
     * @return
     * @throws IOException
     */
    public String saveFile(MultipartFile multipartFile) throws IOException {
        String[] fileAbsolutePath={};
        String fileName=multipartFile.getOriginalFilename();
        String ext = fileName.substring(fileName.lastIndexOf(".") + 1);
        byte[] file_buff = null;
        InputStream inputStream=multipartFile.getInputStream();
        if(inputStream!=null){
            int len1 = inputStream.available();
            file_buff = new byte[len1];
            inputStream.read(file_buff);
        }
        inputStream.close();
        FastDFSFile file = new FastDFSFile(fileName, file_buff, ext);
        try {
            fileAbsolutePath = FastDFSClient.upload(file);  //upload to fastdfs
        } catch (Exception e) {
            logger.error("upload file Exception!",e);
        }
        if (fileAbsolutePath==null) {
            logger.error("upload file failed,please upload again!");
        }
        String path=FastDFSClient.getTrackerUrl()+fileAbsolutePath[0]+ "/"+fileAbsolutePath[1];
        return path;
    }

    @PostMapping("/delete")
    public String delete(@RequestParam("filename") String filename, RedirectAttributes redirectAttributes) throws Exception {
        String message = FastDFSClient.deleteFile("group1", "M00/00/00/" + filename);
        redirectAttributes.addFlashAttribute("message",message);
        return "redirect:/status";
    }

    @GetMapping("/downFile")
    public void downFile(String filename, HttpServletResponse response) {
        InputStream input = FastDFSClient.downFile("group1", "M00/00/00/" + filename);
        int index;
        byte[] bytes = new byte[1024];
        OutputStream outputStream = null;
        try {
            response.setHeader("Content-type", "application/octet-stream");
            response.setHeader("Content-disposition", "attachment;fileName="+ new String(filename.getBytes(),"utf-8"));
            outputStream = response.getOutputStream();
            while ((index = input.read(bytes)) != -1) {
                outputStream.write(bytes, 0, index);
                outputStream.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                input.close();
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}