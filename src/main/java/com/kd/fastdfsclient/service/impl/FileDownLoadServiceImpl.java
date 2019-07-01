package com.kd.fastdfsclient.service.impl;

import com.kd.fastdfsclient.fastdfs.FastDFSClient;
import com.kd.fastdfsclient.service.FileDownLoadService;
import com.kd.fastdfsclient.service.FileInfoService;
import lombok.Cleanup;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Service(value = "default")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class FileDownLoadServiceImpl implements FileDownLoadService {

    private final FileInfoService fileInfoService;

    @Override
    @SneakyThrows //可以对受检异常进行捕捉并抛出
    public String downFile(String groupName, String remoteFileName, HttpServletResponse response) {
        String fileName = fileInfoService.findFileByGroupAndRemoteFileName(groupName, remoteFileName);
        if (fileName == null) {
            log.error("File not found!!!");
            return "File not found!!!";
        }
        // @Cleanup 自动关闭资源，针对实现了java.io.Closeable接口的对象有效
        @Cleanup InputStream inputStream = FastDFSClient.downFile(groupName, remoteFileName);
        if (inputStream == null) {
            log.error("从FastDFS获取流失败！！！");
            return "获取流失败！";
        }
        int index;
        byte[] bytes = new byte[1024];
        @Cleanup ServletOutputStream outputStream = null;
        try {
            response.setHeader("Content-type", "application/octet-stream");
            response.setHeader("Content-disposition", "attachment;fileName="
                    + new String(fileName.getBytes(), StandardCharsets.ISO_8859_1));
            outputStream = response.getOutputStream();
            //浏览器真正响应是从这里开始
            log.info("Join download queue...");
            while ((index = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, index);
                outputStream.flush();
            }
            log.info("download succeed!");
        } catch (IOException e) {
            log.error("downloading may make a mistake！");
            System.out.println("cause by ：" + e.getCause().toString());
            return "downloading may make a mistake！";
        }
        return "success!";
    }

}
