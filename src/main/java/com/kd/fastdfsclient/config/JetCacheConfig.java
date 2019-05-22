package com.kd.fastdfsclient.config;

import com.alicp.jetcache.anno.support.SpringConfigProvider;
import com.alicp.jetcache.support.CacheStat;
import com.alicp.jetcache.support.StatInfo;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Date;
import java.util.List;
import java.util.function.Consumer;

/**
 * @Author: www.chuckfang.top
 * @Date: 2019/5/6 20:56
 */
//@Configuration
public class JetCacheConfig {
    @Bean
    public SpringConfigProvider springConfigProvider() {
        return new SpringConfigProvider() {
            @Override
            public Consumer<StatInfo> statCallback() {
                return new Consumer<StatInfo>() {
                    @Override
                    public void accept(StatInfo stat) {
                        List<CacheStat> stats = stat.getStats();
                        if (stats != null && stats.size() > 0) {
                            for (CacheStat c : stats) {
                                System.out.println(new Date());
                                System.out.println("mycount............");
                                String info = "";
                                info += c.getCacheName() + ",";
                                info += c.getGetCount() + ",";
                                info += c.getGetHitCount() + ",";
                                info += c.getGetFailCount();
                                System.out.println(info);
                            }
                        }
                    }
                };
            }
        };
    }
}
