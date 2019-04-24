package com.kd.fastdfsclient.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kd.fastdfsclient.entity.FileInfo;

import java.util.List;

/**
 * @Author: www.chuckfang.top
 * @Date: 2019/3/26 11:12
 */
public interface FileInfoService extends IService<FileInfo> {
    FileInfo findFileByName(String fileName);

    void deleteByFileName(String fileName);

    int selectCountByREGEXP(String suffix, boolean other);

    List<FileInfo> selectListByREGEXP(String suffix, boolean other, long current, long size, String order, boolean asc);

    List<FileInfo> searchPage(String fileName, long current, long size, String order, boolean asc);
}
