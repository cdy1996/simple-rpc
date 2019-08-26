package com.cdy.simplerpc.config;

import com.cdy.simplerpc.annotation.Order;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;

/**
 *
 * spring配置
 *
 * Created by 陈东一
 * 2019/8/29 0025 16:09
 */
@Slf4j
@Order(50)
public class SpringPropertySource implements PropertySource {

    final Environment environment;

    public SpringPropertySource(Environment environment) {
        this.environment = environment;
    }

    @Override
    public String resolveProperty(String key){
        return environment.getProperty("rpc."+key);
    }
    
}
