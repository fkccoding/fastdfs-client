package com.kd.fastdfsclient.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;


/**
 * @Author: www.chuckfang.top
 * @Date: 2019/3/26 11:02
 */
@Data
public class FileInfo implements Serializable {
    String fileName;
    String groupName;
    String remoteFileName;
    Date uploadDate;
    String fileSize;
    double version;

    public FileInfo() {
    }

    public FileInfo(String fileName, String groupName, String remoteFileName, Date uploadDate, String fileSize, double version) {
        this.fileName = fileName;
        this.groupName = groupName;
        this.remoteFileName = remoteFileName;
        this.uploadDate = uploadDate;
        this.fileSize = fileSize;
        this.version = version;
    }
}
