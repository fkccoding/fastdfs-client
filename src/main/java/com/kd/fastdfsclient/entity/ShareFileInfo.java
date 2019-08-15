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
public class ShareFileInfo implements Serializable {

    private static final long serialVersionUID = 5637320250798932741L;

    @NotBlank(message = "文件ID不能为空")
    private String fileId;
    /**
     * 文件名
     */
    private String fileName;

    /**
     * 文件上传时间
     */
    private Date uploadDate;

    /**
     * 文件大小（用适合人类阅读的方式展现）
     */
    private String fileSize;

    /**
     * 文件的上传人
     */
    private String operator;

}
