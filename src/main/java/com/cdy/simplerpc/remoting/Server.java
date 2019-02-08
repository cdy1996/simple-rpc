package com.cdy.simplerpc.remoting;

import com.cdy.simplerpc.filter.Filter;
import com.cdy.simplerpc.proxy.Invoker;

import java.util.List;
import java.util.function.Function;

/**
 * 服务端接口
 * Created by 陈东一
 * 2018/11/25 0025 14:41
 */
public interface Server {
    
    void bind(Object services, List<Filter> filters, Function<Invoker, Invoker>... functions);
    
    void registerAndListen();
}
