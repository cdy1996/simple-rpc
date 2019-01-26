package com.cdy.simplerpc.filter;

import com.cdy.simplerpc.proxy.Invocation;

/**
 * 过滤器适配器
 * Created by 陈东一
 * 2018/11/25 0025 14:50
 */
public abstract class FilterAdapter implements Filter {
    
    private Filter next;
    
    public Filter getNext() {
        return next;
    }
    
    public void setNext(Filter next) {
        this.next = next;
    }
    
    @Override
    public Object doFilter(Invocation invocation) throws Exception {
        return next.doFilter(invocation);
    }
}
