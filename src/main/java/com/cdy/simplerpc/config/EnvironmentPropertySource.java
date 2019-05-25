package com.cdy.simplerpc.config;

import com.cdy.simplerpc.annotation.Order;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * 环境变量配置
 *
 * Created by 陈东一
 * 2019/5/25 0025 16:09
 */
@Slf4j
@Order(2)
public class EnvironmentPropertySource  implements PropertySource {
    
    @Override
    public String resolveProperty(String key){
        String object = System.getenv(key);
        return object;
    
    }
    
}
