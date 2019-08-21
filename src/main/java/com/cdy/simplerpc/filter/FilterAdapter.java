package com.cdy.simplerpc.filter;

import com.cdy.simplerpc.proxy.Invocation;
import com.cdy.simplerpc.proxy.Invoker;
import com.cdy.simplerpc.proxy.LocalInvoker;
import lombok.Setter;

import java.util.concurrent.CompletableFuture;

/**
 * 过滤器适配器
 * Created by 陈东一
 * 2018/11/25 0025 14:50
 */
public abstract class FilterAdapter implements Filter {
    
    /**
     * 下一层过滤器
     */
    @Setter
    private Filter next;
    
    /**
     * 正真执行
     */
    @Setter
    private Invoker invoker;
 
    @Override
    public Object doFilter(Invocation invocation) throws Exception {
        boolean isServer = invoker.getClass().isAssignableFrom(LocalInvoker.class);
        if (isServer) {
            beforeServerInvoke(invocation);
        } else {
            beforeClientInvoke(invocation);
        }
        Object o = next.doFilter(invocation);
        if (o instanceof CompletableFuture){
            CompletableFuture<Object> future = (CompletableFuture<Object>) o;
            future.whenComplete((result, exception)->{
                if (isServer) {
                    afterServerInvoke(invocation, result==null?exception:result);
                } else {
                    afterClientInvoke(invocation, result==null?exception:result);
                }
            });
        } else {

            if (isServer) {
                afterServerInvoke(invocation, o);
            } else {
                afterClientInvoke(invocation, o);
            }
        }
        return o;
    }
    
    
    protected void beforeServerInvoke(Invocation invocation){}
    protected void afterServerInvoke(Invocation invocation, Object o){}
    
    protected void beforeClientInvoke(Invocation invocation){}
    protected void afterClientInvoke(Invocation invocation, Object o){}
    
    
}
