package com.cdy.simplerpc.annotation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Comparator;

/**
 * 接口的顺序
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Order {
    
    int value() default 1;
    
    
    Comparator<Object> orderComparator = (a, b) -> {
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
    };
}
