package com.kd.fastdfsclient;

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
//import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Author: www.chuckfang.top
 * Date: 2019/4/3 14:51
 */
@SpringBootApplication
@EnableTransactionManagement    //分页插件需要这个注解
@MapperScan("com.kd.fastdfsclient.mapper")
//@EnableMethodCache(basePackages = "com.kd.fastdfsclient")
//@EnableCreateCacheAnnotation
@EnableCaching
//@EnableEurekaClient
public class FastdfsClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(FastdfsClientApplication.class, args);
    }

    /**
     * Mybatis-Plus分页插件
     */
    @Bean
    public PaginationInterceptor paginationInterceptor() {
        return new PaginationInterceptor();
    }
}
