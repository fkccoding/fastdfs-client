package com.kd.fastdfsclient.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kd.fastdfsclient.entity.FileInfo;

/**
 * @Author: www.chuckfang.top
 * @Date: 2019/3/26 11:15
 */
public interface FileInfoMapper extends BaseMapper<FileInfo> {
    FileInfo findFileByName(String filename);

    void deleteByFileName(String filename);
}
