package com.cdy.simplerpc.filter;

import com.cdy.simplerpc.proxy.Invocation;
import com.cdy.simplerpc.proxy.Invoker;

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
     * 设置是正真执行器
     * @param invoker
     */
    default void setInvoker(Invoker invoker){}
    
    /**
     * 设置下一个过滤器
     * @param last
     */
    default void setNext(Filter last){}
}
