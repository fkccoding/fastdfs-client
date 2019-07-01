package com.kd.fastdfsclient.service;

import com.kd.fastdfsclient.entity.OperatorInfo;
import org.springframework.cache.annotation.Cacheable;

/**
 * Author: www.chuckfang.top
 * Date: 2019/5/21 15:16
 */
public interface OperatorService {
    /**
     * 通过操作人的ip找到这个人的其他信息
     * @param ip
     * @return
     */
    @Cacheable(value = "ip",key = "#ip")
    OperatorInfo findByIP(String ip);
}
