package com.cdy.simplerpc.config;

import com.cdy.simplerpc.annotation.Order;
import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.PropertyResourceBundle;

/**
 * todo
 * Created by 陈东一
 * 2019/5/25 0025 16:09
 */
@Slf4j
@Order(0)
public class LocalPropertySource  implements PropertySource{
    
    private PropertyResourceBundle propertyResourceBundle;
    
    private final String path;
    
    public LocalPropertySource(String path) {
        this.path = path;
        try(InputStream inputStream = new FileInputStream(path)) {
            this.propertyResourceBundle = new PropertyResourceBundle(inputStream);
        } catch (IOException e) {
            log.warn("本地配置文件读取失败");
        }
    }
    
    @Override
    public String resolveProperty(String key){
        Object object = propertyResourceBundle.getObject(key);
        return String.valueOf(object);
    
    }

}
