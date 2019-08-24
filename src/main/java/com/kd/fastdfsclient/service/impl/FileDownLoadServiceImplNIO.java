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
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

@Service(value = "NIO")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class FileDownLoadServiceImplNIO implements FileDownLoadService {
    private final FileInfoService fileInfoService;

    @Override
    public void downFile(String groupName, String remoteFileName, HttpServletResponse response) throws Exception{
        String fileName = fileInfoService.findFileByGroupAndRemoteFileName(groupName, remoteFileName);
        if (fileName == null) {
            log.error("File not found!!!");
            throw new Exception("File not found!!!");
        }
        FileInputStream fileInputStream = FastDFSClient.downFileNio(groupName, remoteFileName);
        FileChannel fileChannel = fileInputStream.getChannel();
        // 6x128 KB = 768KB byte buffer
        ByteBuffer buff = ByteBuffer.allocateDirect(786432);
        int bufferSize = 131072;
        byte[] byteArr = new byte[bufferSize];
        int nRead, nGet;

        try {
            while ((nRead = fileChannel.read(buff)) != -1) {
                if (nRead == 0) {
                    continue;
                }
                buff.position(0);
                buff.limit(nRead);
                while (buff.hasRemaining()) {
                    nGet = Math.min(buff.remaining(), bufferSize);
                    // read bytes from disk
                    buff.get(byteArr, 0, nGet);
                    // write bytes to output
                    response.getOutputStream().write(byteArr);
                }
                buff.clear();

                log.debug("download succeed! ---"+remoteFileName);

            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            buff.clear();
            fileChannel.close();
            fileInputStream.close();
        }
    }

    @Override
    public String zipDownFile(String[] groupNameList, String[] remoteFileNameList, HttpServletResponse response) {
        return null;
    }
}
