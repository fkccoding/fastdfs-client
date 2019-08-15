package com.kd.fastdfsclient.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Date;


/**
 * Author: www.chuckfang.top
 * Date: 2019/3/26 11:02
 */
@Data
@Builder
public class FileInfo implements Serializable {

    private static final long serialVersionUID = 5637320250798932740L;

    @NotBlank(message = "文件ID不能为空")
    private String fileId;
    /**
     * 文件名
     */
    private String fileName;

    /**
     * 存储在FastDFS的分区组名
     */
    private String groupName;

    /**
     * 存储在FastDFS的文件名
     */
    @TableId
    private String remoteFileName;

    /**
     * 文件上传时间
     */
    private Date uploadDate;

    /**
     * 文件大小（用适合人类阅读的方式展现）
     */
    private String fileSize;

    /**
     * 文件的真实大小，以字节为单位
     */
    private Long realSize;

    /**
     * 文件的版本号，暂时没用到，应对以后文件版本的问题
     */
    private double version;

    /**
     * 表示目前的文件是否最新版，最新版该值为1，否则为0
     */
    private int isCurrent;

    /**
     * 文件的上传人
     */
    private String operator;

    public FileInfo() {
    }

    public FileInfo(@NotBlank(message = "文件ID不能为空") String fileId, String fileName, String groupName, String remoteFileName, Date uploadDate, String fileSize, Long realSize, double version, int isCurrent, String operator) {
        this.fileId = fileId;
        this.fileName = fileName;
        this.groupName = groupName;
        this.remoteFileName = remoteFileName;
        this.uploadDate = uploadDate;
        this.fileSize = fileSize;
        this.realSize = realSize;
        this.version = version;
        this.isCurrent = isCurrent;
        this.operator = operator;
    }
}
