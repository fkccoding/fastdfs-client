package com.kd.fastdfsclient;

import com.alicp.jetcache.anno.config.EnableCreateCacheAnnotation;
import com.alicp.jetcache.anno.config.EnableMethodCache;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@MapperScan("com.kd.fastdfsclient.mapper")
@EnableMethodCache(basePackages = "com.kd.fastdfsclient")
@EnableCreateCacheAnnotation
public class FastdfsClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(FastdfsClientApplication.class, args);
    }

}
