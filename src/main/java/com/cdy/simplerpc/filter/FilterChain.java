package com.cdy.simplerpc.filter;

import com.cdy.simplerpc.proxy.Invocation;
import com.cdy.simplerpc.remoting.Client;

import java.util.ArrayList;
import java.util.List;

/**
 * todo
 * Created by 陈东一
 * 2019/1/23 0023 22:42
 */
public class FilterChain implements Client{
    
    List<Filter> filters = new ArrayList<>();
    
    Client client;
    
    public FilterChain() {
        filters.add(new ClientContextFilter());
    }
    
    
    @Override
    public <T> Object invoke(Invocation invocation) {
        Filter last = new Filter() {
            @Override
            public void doFilter(Invocation invocation) throws Exception {
            
            }
    
            @Override
            public void setNext(Filter filter) {
        
            }
        };
        
        for (int i = filters.size() - 1; i >= 0; i--) {
            filters.get(i).setNext(last);
            last = filters.get(i);
        }
        
        return client.invoke(invocation);
    }
}
