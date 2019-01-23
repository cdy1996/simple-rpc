package com.cdy.simplerpc.filter;

import com.cdy.simplerpc.proxy.Invocation;

/**
 * 过滤器
 * Created by 陈东一
 * 2018/11/25 0025 14:50
 */
public interface Filter {
    
    void doFilter(Invocation invocation) throws Exception;
    
    void setNext(Filter filter);
}
