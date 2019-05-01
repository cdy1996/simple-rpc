package com.cdy.simplerpc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 远程服务
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RPCReference {
    
    /**
     * 是否开启异步
     * @return
     */
    boolean async() default false;
    
    /**
     * 服务调用的超时时间
     * @return
     */
    long timeout() default 5000L;
    
    /**
     * 直连的url地址
     * @return
     */
    String url() default "";
}
