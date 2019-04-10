package com.kd.fastdfsclient.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kd.fastdfsclient.entity.FileInfo;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Author: www.chuckfang.top
 * @Date: 2019/3/26 11:15
 */
@Mapper
public interface FileInfoMapper extends BaseMapper<FileInfo> {
    FileInfo findFileByName(String filename);

    void deleteByFileName(String filename);
}
