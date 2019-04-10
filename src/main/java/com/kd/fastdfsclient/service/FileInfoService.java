package com.kd.fastdfsclient.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kd.fastdfsclient.entity.FileInfo;

/**
 * @Author: www.chuckfang.top
 * @Date: 2019/3/26 11:12
 */
public interface FileInfoService extends IService<FileInfo> {
    FileInfo findFileByName(String filename);
    public void deleteByFileName(String filename);
}
