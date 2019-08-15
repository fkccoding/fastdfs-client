package com.kd.fastdfsclient.service.impl;

import com.kd.fastdfsclient.fastdfs.FastDFSClient;
import com.kd.fastdfsclient.service.FileDownLoadService;
import com.kd.fastdfsclient.service.FileInfoService;
import lombok.Cleanup;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

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

    @Override
    public String zipDownFile(String[] groupNameList, String[] remoteFileNameList, HttpServletResponse response) {
        if (groupNameList.length >=10 || remoteFileNameList.length>=10){
            return "一次性下载文件不能超过10个！";
        }

        String groupName = groupNameList[0];
        String remoteFileName = remoteFileNameList[0];
        String fileName = fileInfoService.findFileByGroupAndRemoteFileName(groupName, remoteFileName);
        if (fileName == null) {
            log.error(remoteFileName + " not found!!!");
            return remoteFileName + " not found!!!";
        }
        String zipFileName = fileName.substring(0, fileName.indexOf('.')) + "等文件" + ".zip";
        response.setHeader("Content-type", "application/octet-stream");
        response.setHeader("Content-disposition", "attachment;fileName="
                + new String(zipFileName.getBytes(), StandardCharsets.ISO_8859_1));
        // 获取压缩文件输出流
        ZipOutputStream zipos = null;
        DataOutputStream os = null;

        try {
            // 设置压缩流：直接写入response，实现边压缩边下载
            zipos = new ZipOutputStream(new BufferedOutputStream(response.getOutputStream()));

            // 设置压缩方法
            zipos.setMethod(ZipOutputStream.DEFLATED);

            for (int i = 0; i < groupNameList.length; i++) {
                if (i != 0) {
                    groupName = groupNameList[i];
                    remoteFileName = remoteFileNameList[i];
                    fileName = fileInfoService.findFileByGroupAndRemoteFileName(groupName, remoteFileName);
                    if (fileName == null) {
                        log.error(remoteFileName + " not found!!!");
                        return remoteFileName + " not found!!!";
                    }
                }

                // 获取FastDFS的文件输入流
                InputStream is = FastDFSClient.downFile(groupName, remoteFileName);
                if (is == null) {
                    log.error("从FastDFS获取流失败！！！");
                    return "从FastDFS获取流失败！！！";
                }

                int index;
                byte[] bytes = new byte[2048];

                // 往压缩文件中写入每个文件的文件名
                zipos.putNextEntry(new ZipEntry(fileName));

                // 获取封装好的浏览器输出流
                os = new DataOutputStream(zipos);

                // 浏览器真正响应是从这里开始
                log.info("Join download queue...");

                // 把输入流写出到输入流
                while ((index = is.read(bytes)) != -1) {
                    os.write(bytes, 0, index);
                }
                is.close();
                zipos.closeEntry();
            }
        } catch (IOException e) {
            log.error("downloading may make a mistake！");
            System.out.println("cause by ：" + e.getCause().toString());
            return "downloading may make a mistake！";
        } finally {
            try {
                assert os != null;
                os.flush();
                os.close();
                zipos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            log.info("download succeed!");
        }
        return "压缩文件下载成功";
    }


}
