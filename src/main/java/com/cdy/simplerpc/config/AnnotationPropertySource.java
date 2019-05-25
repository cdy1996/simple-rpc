package com.cdy.simplerpc.config;

import com.cdy.simplerpc.annotation.Order;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * 元注解配置
 *
 * Created by 陈东一
 * 2019/5/25 0025 16:09
 */
@Slf4j
@Order(1)
public class AnnotationPropertySource implements PropertySource{
    
    private Map<String,String> map;
    
    
    public AnnotationPropertySource(Map<String,String> map) {
        this.map = map;
    }
    
    @Override
    public String resolveProperty(String key){
        return map.get(key);
    
    }
    
    public void addProperty(Map<String, String> map) {
        this.map.putAll(map);
    }

}
