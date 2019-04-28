package com.kd.fastdfsclient.service.impl;

import com.alicp.jetcache.anno.Cached;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kd.fastdfsclient.controller.FileController;
import com.kd.fastdfsclient.entity.FileInfo;
import com.kd.fastdfsclient.mapper.FileInfoMapper;
import com.kd.fastdfsclient.service.FileInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @Author: www.chuckfang.top
 * @Date: 2019/3/26 11:13
 */
@Service
public class FileInfoServiceImpl extends ServiceImpl<FileInfoMapper, FileInfo> implements FileInfoService {

    private static Logger logger = LoggerFactory.getLogger(FileInfoServiceImpl.class);

    @Autowired
    FileInfoMapper fileInfoMapper;

    @Override
    @Cached(name = "FileInfoService.getUserById", expire = 3600)
    public FileInfo findFileByName(String fileName) {
        return fileInfoMapper.findCurrentFileByName(fileName);
    }

    @Override
    public void deleteByFileName(String fileName) {
        fileInfoMapper.deleteByFileName(fileName);
    }

    @Override
    public int selectCountByREGEXP(String suffix, boolean other) {
        int count;
        if (!other) {
            count = fileInfoMapper.selectCountByREGEXP(suffix,"");
        } else {
            count = fileInfoMapper.selectCountByREGEXP(suffix,"NOT");
        }
        return count;
    }

    @Override
    public List<FileInfo> selectListByREGEXP(String suffix, boolean other, long current, long size, String order, boolean asc) {
        List<FileInfo> fileInfoList;
        if ("file_name".equals(order) || "operator".equals(order)) {
            if (!other) {
                if (asc) {
                    fileInfoList = fileInfoMapper.selectListForChinese(suffix, (current - 1) * size, size, order,
                            "", "ASC");
                } else {
                    fileInfoList = fileInfoMapper.selectListForChinese(suffix, (current - 1) * size, size, order,
                            "", "DESC");
                }
            } else {
                if (asc) {
                    fileInfoList = fileInfoMapper.selectListForChinese(suffix, (current - 1) * size, size, order,
                             "NOT","ASC");
                } else {
                    fileInfoList = fileInfoMapper.selectListForChinese(suffix, (current - 1) * size, size, order,
                            "NOT","DESC");
                }
            }
        } else {
            if (!other) {
                if (asc) {
                    fileInfoList = fileInfoMapper.selectListByREGEXP(suffix, (current - 1) * size, size, order,
                            "", "ASC");
                } else {
                    fileInfoList = fileInfoMapper.selectListByREGEXP(suffix, (current - 1) * size, size, order,
                            "", "DESC");
                }
            } else {
                if (asc) {
                    fileInfoList = fileInfoMapper.selectListByREGEXP(suffix, (current - 1) * size, size, order,
                            "NOT","ASC");
                } else {
                    fileInfoList = fileInfoMapper.selectListByREGEXP(suffix, (current - 1) * size, size, order,
                            "NOT","DESC");
                }
            }
        }

        return fileInfoList;
    }

    @Override
    public List<FileInfo> searchPage(String fileName, long current, long size, String order, boolean asc) {
        List<FileInfo> fileInfoList;
        // 如果是按中文排序
        if ("file_name".equals(order) || "operator".equals(order)) {
            if (asc) {
                fileInfoList = fileInfoMapper.searchPageForChinese(fileName, current, size, order, "ASC");
            } else {
                fileInfoList = fileInfoMapper.searchPageForChinese(fileName, current, size, order, "DESC");
            }
        } else {
            if (asc) {
                fileInfoList = fileInfoMapper.searchPage(fileName, current, size, order, "ASC");
            } else {
                fileInfoList = fileInfoMapper.searchPage(fileName, current, size, order, "DESC");
            }
        }
        return fileInfoList;
    }

    // 如果数据库查到已经存在该文件名的文件，那么把其中is_current=1的文件的is_current更新为0，
    // 不管怎样都插入新纪录，且把新纪录的is_current置1
    @Override
    public void updateVersion(String fileName) {
        if (null != findFileByName(fileName)) {
            fileInfoMapper.updateVersionToOldByFileName(fileName);
            logger.info("The file name is occupied, we are already update the version to new.");
        }
    }

    @Override
    @Transactional
    public void revert(String fileName, String remoteFileName) {
        fileInfoMapper.updateVersionToOldByFileName(fileName);
        fileInfoMapper.updateVersionToCurrentByRemoteFileName(remoteFileName);
        logger.info("revert success ！");
    }

}
