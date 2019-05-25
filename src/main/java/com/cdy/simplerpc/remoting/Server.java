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
     *
     * @param serviceName
     * @param services
     * @param filters
     * @param functions
     * @throws Exception
     */
    void bind(String serviceName, Object services, List<Filter> filters, Function<Invoker, Invoker>... functions) throws Exception;
    
    /**
     * 注册
     * @throws Exception
     * @param serviceName
     */
    void register(String serviceName) throws Exception;
    
    /**
     * 打开服务
     * @throws Exception
     */
    void openServer() throws Exception;
    
    /**
     * 设置注册器
     * @param registry
     */
    void setRegistry(IServiceRegistry registry);
    
 
}
