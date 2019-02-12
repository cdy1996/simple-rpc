package com.cdy.simplerpc.remoting;

import com.cdy.simplerpc.filter.Filter;
import com.cdy.simplerpc.proxy.Invoker;
import com.cdy.simplerpc.registry.IServiceRegistry;

import java.util.List;
import java.util.function.Function;

/**
 * 服务端接口
 * Created by 陈东一
 * 2018/11/25 0025 14:41
 */
public interface Server {
    
    /**
     * 绑定
     * @param services
     * @param filters
     * @param functions
     * @throws Exception
     */
    void bind(Object services, List<Filter> filters, Function<Invoker, Invoker>... functions) throws Exception;
    
    /**
     * 注册和监听
     * @throws Exception
     */
    void registerAndListen() throws Exception;
    
    /**
     * 设置注册器
     * @param registry
     */
    void setRegistry(IServiceRegistry registry);
}
