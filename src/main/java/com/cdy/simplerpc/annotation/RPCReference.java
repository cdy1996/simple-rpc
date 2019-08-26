package com.cdy.simplerpc.annotation;

import com.cdy.simplerpc.config.ConfigConstants;
import com.cdy.simplerpc.util.StringUtil;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.HashMap;
import java.util.Map;

/**
 * 远程服务
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RPCReference {
    
//    /**
//     * 引用的别名,用于区分不同的引用.类似spring的beanId
//     * @return
//     */
//    String value();
    
    /**
     * 运行中的注册中心
     * @return
     */
    String type() default "";
    
    /**
     * 是否开启异步
     * @return
     */
    String async() default "false";
    
    /**
     * 服务调用的超时时间
     * @return
     */
    String timeout() default "5000";
    
    /**
     * 直连的url地址
     * @return
     */
    String url() default "";
    
    String[] protocols() default {};
    
    
    class ReferenceAnnotationInfo {

        public static Map<String,String> getConfig(String key, RPCReference annotation) {
            Map<String, String> map = new HashMap<>();
            if (StringUtil.isNotBlank(annotation.async())) {
                map.put(key+"."+ConfigConstants.async, annotation.async()+"");
            }
            if (StringUtil.isNotBlank(annotation.timeout())) {
                map.put(key + "." + ConfigConstants.timeout, annotation.timeout() + "");
            }
            if (StringUtil.isNotBlank(annotation.type())) {
                map.put(key + "." + ConfigConstants.type, annotation.type() + "");
            }
            if (!StringUtil.isBlank(annotation.url())) {
                map.put(key + "." + ConfigConstants.url, annotation.url() + "");
            }
            if (annotation.protocols().length !=0) {
                map.put(key+"."+ConfigConstants.protocols, String.join(",",annotation.protocols()));
            }
            return map;
        }
        
    }
    
}
