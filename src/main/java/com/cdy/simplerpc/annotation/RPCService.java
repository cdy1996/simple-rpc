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
 * 暴露服务
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RPCService {

//    /**
//     * 注册类别
//     * nacos / zookeeper
//     * @return
//     */
//    String type() default "";
//    /**
//     * 注册地址
//     * 127.0.0.1:2181;127.0.0.2:2181
//     * @return
//     */
//    String address() default "";
    
    
    /**
     * 服务要注册地址
     * todo 之后改成自动获取网卡的数据
     * @return
     */
    String ip() default "";
    
    String[] protocols() default {};
    
    String port() default "";
    
    class ServiceAnnotationInfo {
    
        /**
         * 获取注解中的配置信息
         * @param key  一般是类名 simpleName
         * @param annotation  注解
         * @return
         */
        public static Map<String,String> getConfig(String key, RPCService annotation) {
            Map<String, String> map = new HashMap<>();
//            if (!StringUtil.isBlank(annotation.address())) {
//                map.put(key+"."+ ConfigConstants.address, annotation.address()+"");
//            }
            if (!StringUtil.isBlank(annotation.port())) {
                map.put(key+"."+ConfigConstants.port, annotation.port()+"");
            }
            if (!StringUtil.isBlank(annotation.ip())) {
                map.put(key+"."+ConfigConstants.ip, annotation.ip()+"");
            }
            if (annotation.protocols().length != 0) {
                map.put(key+"."+ConfigConstants.protocols, String.join(",",annotation.protocols()));
            }
            return map;
        }
        
    }
    

}
