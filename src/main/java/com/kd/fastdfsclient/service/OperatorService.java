package com.kd.fastdfsclient.service;

import com.alicp.jetcache.anno.Cached;
import com.kd.fastdfsclient.entity.OperatorInfo;

/**
 * @Author: www.chuckfang.top
 * @Date: 2019/5/21 15:16
 */
public interface OperatorService {
    @Cached(expire = 3600)
    OperatorInfo findByIP(String ip);
}
