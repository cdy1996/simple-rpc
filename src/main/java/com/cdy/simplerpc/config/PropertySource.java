package com.cdy.simplerpc.config;

/**
 * 配置接口
 * Created by 陈东一
 * 2019/5/25 0025 16:09
 */
public interface PropertySource {


    String resolveProperty(String key);
    
}
