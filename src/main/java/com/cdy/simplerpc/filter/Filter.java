package com.cdy.simplerpc.filter;

import com.cdy.simplerpc.proxy.Invocation;

/**
 * 过滤器
 * Created by 陈东一
 * 2018/11/25 0025 14:50
 */
public interface Filter {
    
    /**
     * 过滤器调用
     * @param invocation
     * @return
     * @throws Exception
     */
    Object doFilter(Invocation invocation) throws Exception;
    
    /**
     * 设置是否为服务端使用过滤器
     * @param server
     */
    default void setServer(Boolean server){}
    
    /**
     * 设置下一个过滤器
     * @param last
     */
    default void setNext(Filter last){}
}
