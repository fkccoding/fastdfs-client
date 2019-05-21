package com.kd.fastdfsclient.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kd.fastdfsclient.entity.OperatorInfo;
import org.springframework.stereotype.Repository;

/**
 * @Author: www.chuckfang.top
 * @Date: 2019/5/21 15:10
 */
@Repository
public interface OperatorInfoMapper extends BaseMapper<OperatorInfo> {
    OperatorInfo findByIP(String ip);
}
