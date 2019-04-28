package com.kd.fastdfsclient.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kd.fastdfsclient.entity.FileInfo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * @Author: www.chuckfang.top
 * @Date: 2019/3/26 11:15
 */
@Repository
public interface FileInfoMapper extends BaseMapper<FileInfo> {

    List<FileInfo> findAllFileByName(String fileName);

    FileInfo findCurrentFileByName(String fileName);

    FileInfo findFileByRemoteFileName(String remoteFileName);

    String findFileByGroupAndRemoteFileName(@Param("groupName") String groupName, @Param("remoteFileName") String remoteFileName);

    void deleteByFileName(String fileName);

    int selectCountByREGEXP(@Param("suffix") String suffix, @Param("other") String other);

    List<FileInfo> selectListByREGEXP(@Param("suffix") String suffix, @Param("current") long current,
                                      @Param("size") long size, @Param("order") String order,
                                      @Param("other") String other, @Param("sequence") String sequence);

    List<FileInfo> selectListForChinese(@Param("suffix") String suffix, @Param("current") long current,
                                        @Param("size") long size, @Param("order") String order,
                                        @Param("other") String other, @Param("sequence") String sequence);

    int searchCount(@Param("fileName") String fileName);

    List<FileInfo> searchPage(@Param("fileName") String fileName,@Param("current") long current,
                              @Param("size") long size, @Param("order") String order,
                              @Param("sequence") String sequence);

    List<FileInfo> searchPageForChinese(@Param("fileName") String fileName,@Param("current") long current,
                                        @Param("size") long size, @Param("order") String order,
                                        @Param("sequence") String sequence);

    int updateVersionToOldByFileName(@Param("fileName") String fileName);

    int updateVersionToCurrentByRemoteFileName(@Param("remoteFileName")String remoteFileName);
}
