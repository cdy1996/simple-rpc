package com.cdy.simplerpc.filter;

/**
 * 过滤器
 * Created by 陈东一
 * 2018/11/25 0025 14:50
 */
public interface Filter {
    
    
    boolean pre();
    
    Object invoke();
    
    void after();
}
