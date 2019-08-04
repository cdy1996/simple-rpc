package com.cdy.simplerpc.remoting;

import com.cdy.simplerpc.filter.Filter;

import java.io.Closeable;
import java.util.List;

/**
 * 服务端接口
 * Created by 陈东一
 * 2018/11/25 0025 14:41
 */
public interface Server extends Closeable {
    
    /**
     * 绑定
     *
     * @param serviceName
     * @param services
     * @param filters
     * @throws Exception
     */
    void bind(String serviceName, Object services, List<Filter> filters) throws Exception;
    
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
    
 
}
