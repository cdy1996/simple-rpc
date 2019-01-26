package com.cdy.simplerpc.filter;

import com.cdy.simplerpc.proxy.Invocation;
import com.cdy.simplerpc.proxy.Invoker;
import com.cdy.simplerpc.proxy.LocalInvoker;
import com.cdy.simplerpc.proxy.RemoteInvoker;
import com.cdy.simplerpc.remoting.InvokerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * 客户端过滤器装饰器
 *
 * Created by 陈东一
 * 2019/1/23 0023 22:42
 */
public class FilterInvokerWrapper extends InvokerAdapter {
    
    private List<FilterAdapter> filters = new ArrayList<>();
    
    
    public FilterInvokerWrapper(Invoker invoker) {
        super(invoker);
        if (invoker.getClass().isAssignableFrom(LocalInvoker.class)) {
            filters.add(new ContextFilter(true));
        } else if (invoker.getClass().isAssignableFrom(RemoteInvoker.class)){
            filters.add(new ContextFilter(false));
    
        }
    }
    
    
    @Override
    public Object invoke(Invocation invocation) throws Exception {
        Filter last = invocation1 -> super.invoke(invocation);
        for (int i = filters.size() - 1; i >= 0; i--) {
            filters.get(i).setNext(last);
            last = filters.get(i);
        }
        return filters.get(0).doFilter(invocation);
    }
    
   
}
