package com.cdy.simplerpc.filter;

import com.cdy.simplerpc.annotation.Order;
import com.cdy.simplerpc.proxy.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * 客户端过滤器装饰器
 * <p>
 * Created by 陈东一
 * 2019/1/23 0023 22:42
 */
public class FilterChain extends InvokerAdapter {
    
    private List<Filter> filters = new ArrayList<>();
    
    
    public FilterChain(Invoker invoker, List<Filter> filterList) {
        super(invoker);
        filterList.stream().sorted(comparable).forEach(e -> {
            e.setServer(invoker.getClass().isAssignableFrom(LocalInvoker.class));
            filters.add(e);
        });
        //构建链
        Filter last = new DefaultFilter();
        for (int i = filters.size() - 1; i >= 0; i--) {
            filters.get(i).setNext(last);
            last = filters.get(i);
        }
    }
    
    
    @Override
    public Object invoke(Invocation invocation) throws Exception {
        return filters.get(0).doFilter(invocation);
    }
    
    class DefaultFilter implements Filter {
        @Override
        public Object doFilter(Invocation invocation) throws Exception {
            return getInvoker().invoke(invocation);
        }
        @Override
        public void setServer(Boolean server) {
        }
        @Override
        public void setNext(Filter last) {
        }
    }
    
    
    private Comparator<Filter> comparable = (a, b) -> {
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
