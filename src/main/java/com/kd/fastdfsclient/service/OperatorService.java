package com.kd.fastdfsclient.service;

import com.kd.fastdfsclient.entity.OperatorInfo;
import org.springframework.cache.annotation.Cacheable;

/**
 * @Author: www.chuckfang.top
 * @Date: 2019/5/21 15:16
 */
public interface OperatorService {
    @Cacheable
    OperatorInfo findByIP(String ip);
}
