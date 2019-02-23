package com.cdy.simplerpc.filter;

import com.cdy.simplerpc.proxy.Invocation;

/**
 * 过滤器适配器
 * Created by 陈东一
 * 2018/11/25 0025 14:50
 */
public abstract class FilterAdapter implements Filter {
    
    private Filter next;
    /**
     * 是否用于服务端的过滤器
     */
    private Boolean isServer;
 
    @Override
    public Object doFilter(Invocation invocation) throws Exception {
        if (isServer) {
            beforeServerInvoke(invocation);
        } else {
            beforeClientInvoke(invocation);
        }
        Object o = next.doFilter(invocation);
        if (isServer) {
            afterServerInvoke(invocation, o);
        } else {
            afterClientInvoke(invocation, o);
        }
        return o;
    }
    
    
    protected void beforeServerInvoke(Invocation invocation){}
    protected void afterServerInvoke(Invocation invocation, Object o){}
    
    protected void beforeClientInvoke(Invocation invocation){}
    protected void afterClientInvoke(Invocation invocation, Object o){}
    
    
    @Override
    public void setServer(Boolean server) {
        isServer = server;
    }
    
    @Override
    public void setNext(Filter next) {
        this.next = next;
    }
    
}
