package com.cdy.simplerpc.filter;

import com.cdy.simplerpc.container.Order;
import com.cdy.simplerpc.proxy.Invocation;
import com.cdy.simplerpc.proxy.Invoker;
import com.cdy.simplerpc.proxy.LocalInvoker;
import com.cdy.simplerpc.proxy.RemoteInvoker;
import com.cdy.simplerpc.proxy.InvokerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * 客户端过滤器装饰器
 *
 * Created by 陈东一
 * 2019/1/23 0023 22:42
 */
public class FilterInvokerWrapper extends InvokerAdapter {
    
    private List<Filter> filters = new ArrayList<>();
    
    
    public FilterInvokerWrapper(Invoker invoker, List<Filter> filterList) {
        super(invoker);
        filterList.stream().sorted((a,b)->{
            Order orderA = a.getClass().getAnnotation(Order.class);
            Order orderB = b.getClass().getAnnotation(Order.class);
            if (orderA != null && orderB != null) {
                return orderA.value()-orderB.value();
            }
            if (orderA == null && orderB == null) {
                return 0;
            }
            if(orderA==null){
                return 1-orderB.value();
            } else {
                return orderA.value()-1;
            }
            
        }).forEach(e->{
            if (invoker.getClass().isAssignableFrom(LocalInvoker.class)) {
                e.setServer(true);
                filters.add(e);
            } else if (invoker.getClass().isAssignableFrom(RemoteInvoker.class)){
                e.setServer(false);
                filters.add(e);
            }
        });
        
        
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
