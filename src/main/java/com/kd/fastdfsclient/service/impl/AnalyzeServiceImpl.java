package com.kd.fastdfsclient.service.impl;

import com.kd.fastdfsclient.mapper.FileInfoMapper;
import com.kd.fastdfsclient.service.AnalyzeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;


/**
 * Author: www.chuckfang.top
 * Date: 2019/5/15 16:48
 */
@Service
public class AnalyzeServiceImpl implements AnalyzeService {
    @Autowired
    FileInfoMapper fileInfoMapper;

    @Override
    public int countUpload(LocalDateTime date) {
        return fileInfoMapper.countUpload(date, date.plus(1, ChronoUnit.HOURS));
    }
}
