package com.kd.fastdfsclient.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kd.fastdfsclient.entity.OperatorInfo;
import org.springframework.stereotype.Repository;

/**
 * Author: www.chuckfang.top
 * Date: 2019/5/21 15:10
 */
@Repository
public interface OperatorInfoMapper extends BaseMapper<OperatorInfo> {

    /**
     * 使用ip查询操作人的姓名，在局域网便于管理用户
     * @param ip 操作人的ip
     * @return 返回操作者的信息
     */
    OperatorInfo findByIP(String ip);
}
