package com.kd.fastdfsclient.service.impl;

import com.alicp.jetcache.anno.Cached;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kd.fastdfsclient.entity.FileInfo;
import com.kd.fastdfsclient.mapper.FileInfoMapper;
import com.kd.fastdfsclient.service.FileInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author: www.chuckfang.top
 * @Date: 2019/3/26 11:13
 */
@Service
public class FileInfoServiceImpl extends ServiceImpl<FileInfoMapper, FileInfo> implements FileInfoService {

    @Autowired
    FileInfoMapper fileInfoMapper;

    @Override
    @Cached(name = "FileInfoService.getUserById", expire = 3600)
    public FileInfo findFileByName(String filename) {
        FileInfo fileInfo = fileInfoMapper.findFileByName(filename);
        return fileInfo;
    }

    @Override
    public void deleteByFileName(String filename) {
        fileInfoMapper.deleteByFileName(filename);
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
                            "NOT", "ASC");
                } else {
                    fileInfoList = fileInfoMapper.selectListForChinese(suffix, (current - 1) * size, size, order,
                            "NOT", "DESC");
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
                            "NOT", "ASC");
                } else {
                    fileInfoList = fileInfoMapper.selectListByREGEXP(suffix, (current - 1) * size, size, order,
                            "NOT", "DESC");
                }
            }
        }

        return fileInfoList;
    }

}
