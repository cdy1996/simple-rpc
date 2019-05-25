package com.cdy.simplerpc.config;

import com.cdy.simplerpc.annotation.Order;
import com.cdy.simplerpc.util.StringUtil;

import java.util.Iterator;
import java.util.TreeSet;

/**
 * todo
 * Created by 陈东一
 * 2019/5/25 0025 16:09
 */
public class PropertySources implements PropertySource{
    
    private TreeSet<PropertySource> propertySources = new TreeSet<>((a, b) -> {
        Order orderA = a.getClass().getAnnotation(Order.class);
        Order orderB = b.getClass().getAnnotation(Order.class);
        if (orderA != null && orderB != null) {
            return orderA.value() - orderB.value();
        }
        if (orderA == null && orderB == null) {
            return 0;
        }
        if (orderA == null) {
            return 1 - orderB.value();
        } else {
            return orderA.value() - 1;
        }
    });
    
    
    public void addPropertySources(PropertySource propertySource){
        propertySources.add(propertySource);
    }
    

    @Override
    public String resolveProperty(String key){
    
        Iterator<PropertySource> iterator = propertySources.iterator();
        String result=null;
        while (iterator.hasNext()) {
            PropertySource next = iterator.next();
            String property = next.resolveProperty(key);
            if (!StringUtil.isBlank(property))
                result = property;
        }
        return result;
    }
    

}
