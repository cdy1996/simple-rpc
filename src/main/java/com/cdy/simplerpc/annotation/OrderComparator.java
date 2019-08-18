package com.cdy.simplerpc.annotation;

import java.util.Comparator;

/**
 * order注解的比较器
 * Created by 陈东一
 * 2019/8/18 0018 13:29
 */
public class OrderComparator {
    
    public static Comparator<Object> orderComparator = (a, b) -> {
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
