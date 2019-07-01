package com.kd.fastdfsclient.service.impl;

import com.kd.fastdfsclient.fastdfs.FastDFSClient;
import com.kd.fastdfsclient.service.FileDownLoadService;
import com.kd.fastdfsclient.service.FileInfoService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Service(value = "NIO")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class FileDownLoadServiceImplNIO implements FileDownLoadService {
    private final FileInfoService fileInfoService;

    @Override
    @SneakyThrows //可以对受检异常进行捕捉并抛出
    public String downFile(String groupName, String remoteFileName, HttpServletResponse response) {
        String fileName = fileInfoService.findFileByGroupAndRemoteFileName(groupName, remoteFileName);
        if (fileName == null) {
            log.error("File not found!!!");
            return "File not found!!!";
        }
        StorageClient storageClient = getTrackerClient();
        byte[] fileByte = storageClient.download_file(groupName, remoteFileName);

        return "success!";
    }


    private static StorageClient getTrackerClient() throws IOException {
        TrackerServer trackerServer = getTrackerServer();
        StorageClient storageClient = new StorageClient(trackerServer, null);
        return  storageClient;
    }
    private static TrackerServer getTrackerServer() throws IOException {
        TrackerClient trackerClient = new TrackerClient();
        TrackerServer trackerServer = trackerClient.getConnection();
        return  trackerServer;
    }
}
