package com.kd.fastdfsclient.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kd.fastdfsclient.entity.FileInfo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @Author: www.chuckfang.top
 * @Date: 2019/3/26 11:15
 */
@Repository
public interface FileInfoMapper extends BaseMapper<FileInfo> {

    /**
     * 通过文件名找到所有文件
     * @param fileName
     * @return
     */
    List<FileInfo> findAllFileByName(String fileName);

    /**
     * 找到某个文件的当前版本
     * @param fileName
     * @return
     */
    FileInfo findCurrentFileByName(String fileName);

    /**
     * 根据文件在FastDFS上存储的组名和文件名找到文件，并返回在数据库中存储的文件名
     * @param groupName
     * @param remoteFileName
     * @return
     */
    String findFileByGroupAndRemoteFileName(@Param("groupName") String groupName, @Param("remoteFileName") String remoteFileName);

    /**
     * 根据文件名删除所有文件
     * @param fileName
     */
    void deleteByFileName(String fileName);

    /**
     * 根据后缀名使用正则表达式查询符合正则表达式的文件数量
     * @param suffix
     * @param other
     * @return
     */
    int selectCountByREGEXP(@Param("suffix") String suffix, @Param("other") boolean other);

    /**
     * 使用正则表达式查询文件列表
     * @param suffix
     * @param current
     * @param size
     * @param order
     * @param other
     * @param asc
     * @return
     */
    List<FileInfo> selectListByREGEXP(@Param("suffix") String suffix, @Param("current") long current,
                                      @Param("size") long size, @Param("order") String order,
                                      @Param("other") boolean other, @Param("asc") boolean asc);

    /**
     * 为中文排序
     * @param suffix
     * @param current
     * @param size
     * @param order
     * @param other
     * @param asc
     * @return
     */
    List<FileInfo> selectListForChinese(@Param("suffix") String suffix, @Param("current") long current,
                                        @Param("size") long size, @Param("order") String order,
                                        @Param("other") boolean other, @Param("asc") boolean asc);

    /**
     * 查询文件名包含关键字的最新文件数量，而不包括历史版本
     * @param fileName
     * @return
     */
    int searchCount(@Param("fileName") String fileName);

    /**
     * 查询文件名包含关键字的最新文件列表，而不包括历史版本
     * @param fileName
     * @param current
     * @param size
     * @param order
     * @param asc
     * @return
     */
    List<FileInfo> searchPage(@Param("fileName") String fileName,@Param("current") long current,
                              @Param("size") long size, @Param("order") String order,
                              @Param("asc") boolean asc);

    /**
     * 根据中文名排序获取的文件列表
     * @param fileName
     * @param current
     * @param size
     * @param order
     * @param asc
     * @return
     */
    List<FileInfo> searchPageForChinese(@Param("fileName") String fileName,@Param("current") long current,
                                        @Param("size") long size, @Param("order") String order,
                                        @Param("asc") boolean asc);

    /**
     * 把当前版本变为历史版本
     * @param fileName
     * @return
     */
    int updateVersionToOldByFileName(@Param("fileName") String fileName);

    /**
     * 根据文件唯一标识符回退版本
     * @param remoteFileName
     * @return
     */
    int updateVersionToCurrentByRemoteFileName(@Param("remoteFileName")String remoteFileName);

    /**
     * 根据唯一标识符删除文件
     * @param groupName
     * @param remoteFileName
     */
    void deleteByRemoteFileName(@Param("groupName") String groupName, @Param("remoteFileName") String remoteFileName);

    /**
     * 统计每个小时的数据
     * @param beforeTime
     * @param afterTime
     * @return
     */
    int countUpload(@Param("beforeTime") LocalDateTime beforeTime, @Param("afterTime") LocalDateTime afterTime);
}
