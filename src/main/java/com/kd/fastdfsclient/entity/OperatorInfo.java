package com.kd.fastdfsclient.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: www.chuckfang.top
 * @Date: 2019/5/21 15:08
 */
@Data
public class OperatorInfo implements Serializable {

    private static final long serialVersionUID = 7708485434924084010L;
    public String operator;
    public String ip;
}
