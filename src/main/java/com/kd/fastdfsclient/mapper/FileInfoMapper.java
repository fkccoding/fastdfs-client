package com.kd.fastdfsclient.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kd.fastdfsclient.entity.FileInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author: www.chuckfang.top
 * @Date: 2019/3/26 11:15
 */
@Mapper
public interface FileInfoMapper extends BaseMapper<FileInfo> {

    FileInfo findFileByName(String filename);

    void deleteByFileName(String filename);

    int selectCountByREGEXP(@Param("suffix") String suffix, @Param("other") String other);

    List<FileInfo> selectListByREGEXP(@Param("suffix") String suffix, @Param("current") long current,
                                      @Param("size") long size, @Param("order") String order,
                                      @Param("other") String other, @Param("sequence") String sequence);

}
