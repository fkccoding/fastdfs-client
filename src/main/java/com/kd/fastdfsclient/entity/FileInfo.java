package com.kd.fastdfsclient.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;


/**
 * @Author: www.chuckfang.top
 * @Date: 2019/3/26 11:02
 */
@Data
public class FileInfo implements Serializable {

    private static final long serialVersionUID = 5637320250798932740L;
    private String fileName;
    private String groupName;
    @TableId
    private String remoteFileName;
    private Date uploadDate;
    private String fileSize;
    private Long realSize;
    private double version;
    private String operator;
    private int isCurrent;


    public FileInfo() {
    }

    public FileInfo(String fileName) {
        this.fileName = fileName;
    }

    public FileInfo(String fileName, String groupName, String remoteFileName, Date uploadDate, String fileSize, Long realSize, double version, String operator, int isCurrent) {
        this.fileName = fileName;
        this.groupName = groupName;
        this.remoteFileName = remoteFileName;
        this.uploadDate = uploadDate;
        this.fileSize = fileSize;
        this.realSize = realSize;
        this.version = version;
        this.operator = operator;
        this.isCurrent = isCurrent;
    }
}
