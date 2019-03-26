package com.kd.fastdfsclient.entity;

import lombok.Data;

import java.util.Date;


/**
 * @Author: www.chuckfang.top
 * @Date: 2019/3/26 11:02
 */
@Data
public class FileInfo {
    String fileName;
    String groupName;
    String remoteFileName;
    Date uploadDate;
    double version;

    public FileInfo() {
    }

    public FileInfo(String fileName, String groupName, String remoteFileName, Date uploadDate, double version) {
        this.fileName = fileName;
        this.groupName = groupName;
        this.remoteFileName = remoteFileName;
        this.uploadDate = uploadDate;
        this.version = version;
    }
}
