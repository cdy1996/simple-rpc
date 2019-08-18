package com.cdy.simplerpc.filter;

import com.cdy.simplerpc.proxy.Invocation;
import com.cdy.simplerpc.proxy.Invoker;
import com.cdy.simplerpc.proxy.InvokerAdapter;

import java.util.ArrayList;
import java.util.List;

import static com.cdy.simplerpc.annotation.OrderComparator.orderComparator;

/**
 * 客户端过滤器装饰器
 * <p>
 * Created by 陈东一
 * 2019/1/23 0023 22:42
 */
public class FilterChain extends InvokerAdapter {
    
    private final List<Filter> filters = new ArrayList<>();
    
    public FilterChain(Invoker invoker) {
        super(invoker);
    }
    
    public FilterChain(Invoker invoker, List<Filter> filterList) {
        super(invoker);
        filterList.stream().sorted(orderComparator).forEach(e -> {
            e.setInvoker(invoker);
            filters.add(e);
        });
        Filter last = i->getInvoker().invoke(i);
        //构建链
        for (int i = filters.size() - 1; i >= 0; i--) {
            filters.get(i).setNext(last);
            last = filters.get(i);
        }
    }
    
    
    @Override
    public Object invoke(Invocation invocation) throws Exception {
        Filter defaultFilter = i->getInvoker().invoke(i);
        return filters.isEmpty()?defaultFilter.doFilter(invocation):filters.get(0).doFilter(invocation);
    }
    
  
}
