package com.kd.fastdfsclient.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * @Author: www.chuckfang.top
 * @Date: 2019/1/3 14:51
 */
@Configuration
public class viewController extends WebMvcConfigurerAdapter {
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("/do");
        registry.addViewController("/status").setViewName("/status");
    }
}
