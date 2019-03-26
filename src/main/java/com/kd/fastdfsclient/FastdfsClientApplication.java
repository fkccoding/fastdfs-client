package com.kd.fastdfsclient;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.kd.fastdfsclient.mapper")
public class FastdfsClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(FastdfsClientApplication.class, args);
    }

}
