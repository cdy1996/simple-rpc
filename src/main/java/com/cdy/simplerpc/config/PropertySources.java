package com.cdy.simplerpc.config;

import com.cdy.simplerpc.annotation.Order;
import com.cdy.simplerpc.util.StringUtil;

import java.util.Iterator;
import java.util.TreeSet;

import static com.cdy.simplerpc.annotation.OrderComparator.orderComparator;

/**
 * 配置聚合,用于配置覆盖
 *
 *   外部化配置顺序, 下面的会覆盖上面的
 *   -> 文件配置
 *   -> bootstrap配置
 *   -> 注解配置
 *   -> 系统配置
 *   -> 环境配置
 *   -> 远程配置
 *
 * Created by 陈东一
 * 2019/5/25 0025 16:09
 */
public class PropertySources implements PropertySource {
 
    //todo 可能有线程安全问题
    private final TreeSet<PropertySource> propertySources = new TreeSet<>(orderComparator);
    
    
    public void addPropertySources(PropertySource propertySource) {
        if (propertySource instanceof AnnotationPropertySource){
            AnnotationPropertySource p = (AnnotationPropertySource) propertySource;
            for (PropertySource next : propertySources) {
                if (next instanceof AnnotationPropertySource) {
                    AnnotationPropertySource n = (AnnotationPropertySource) next;
                    n.addProperty(p.getMap());
                    return;
                }
            }
        }
        propertySources.add(propertySource);
    }
    
    public PropertySource getProperetySource(Integer order) {
        for (PropertySource next : propertySources) {
            Order o;
            if ((o = next.getClass().getAnnotation(Order.class)) != null
                    && o.value() == order) {
                return next;
            }
        }
        return null;
    }
    
    @Override
    public String resolveProperty(String key) {
        
        Iterator<PropertySource> iterator = propertySources.iterator();
        String result = null;
        while (iterator.hasNext()) {
            PropertySource next = iterator.next();
            String property = next.resolveProperty(key);
            if (!StringUtil.isBlank(property))
                result = property;
        }
        return result;
    }
    
    
}
