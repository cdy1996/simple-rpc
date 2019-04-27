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
    
    String value() default "default";
    // version
    boolean async() default false;
    // timeout
    long timeout() default 5000L;
    
    String[] protocols() default {"rpc","http"};
}
