package com.cdy.simplerpc.config;

import com.cdy.simplerpc.annotation.Order;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * 元注解配置
 *
 * Created by 陈东一
 * 2019/5/25 0025 16:09
 */
@Slf4j
@Order(1)
public class BootstrapPropertySource implements PropertySource{
    
    @Getter
    private final Map<String,String> map; //不变的
    
    public BootstrapPropertySource() {
        this.map = new HashMap<>();
    }
    
    @Override
    public String resolveProperty(String key){
        return map.get(key);
    
    }
    
    public void addProperty(Map<String, String> map) {
        this.map.putAll(map);
    }

}
