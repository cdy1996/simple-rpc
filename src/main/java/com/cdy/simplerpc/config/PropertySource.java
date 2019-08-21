package com.cdy.simplerpc.config;

import com.cdy.simplerpc.util.StringUtil;

/**
 * 配置接口
 * Created by 陈东一
 * 2019/5/25 0025 16:09
 */
public interface PropertySource {


    String resolveProperty(String key);

    default String resolveProperty(String key, String defaultValue) {
        String string = resolveProperty(key);
        return StringUtil.isBlank(string) ? defaultValue : string;
    }

}
