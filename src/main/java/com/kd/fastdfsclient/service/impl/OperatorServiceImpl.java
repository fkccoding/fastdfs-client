package com.kd.fastdfsclient.service.impl;

import com.kd.fastdfsclient.entity.OperatorInfo;
import com.kd.fastdfsclient.mapper.OperatorInfoMapper;
import com.kd.fastdfsclient.service.OperatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author: www.chuckfang.top
 * @Date: 2019/5/21 15:17
 */
@Service
public class OperatorServiceImpl implements OperatorService {
    @Autowired
    OperatorInfoMapper operatorInfoMapper;

    @Override
    public OperatorInfo findByIP(String ip) {
        return operatorInfoMapper.findByIP(ip);
    }
}
