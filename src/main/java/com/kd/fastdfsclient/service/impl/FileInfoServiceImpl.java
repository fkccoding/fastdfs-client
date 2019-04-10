package com.kd.fastdfsclient.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kd.fastdfsclient.entity.FileInfo;
import com.kd.fastdfsclient.mapper.FileInfoMapper;
import com.kd.fastdfsclient.service.FileInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author: www.chuckfang.top
 * @Date: 2019/3/26 11:13
 */
@Service
public class FileInfoServiceImpl extends ServiceImpl<FileInfoMapper, FileInfo> implements FileInfoService {
    @Autowired
    FileInfoMapper fileInfoMapper;

    @Override
    public FileInfo findFileByName(String filename) {
        FileInfo fileInfo = fileInfoMapper.findFileByName(filename);
        return fileInfo;
    }

    @Override
    public void deleteByFileName(String filename){
        fileInfoMapper.deleteByFileName(filename);
    }
}
