package com.cdy.simplerpc.remoting;

import com.cdy.simplerpc.exception.RPCException;
import com.cdy.simplerpc.registry.IServiceRegistry;
import com.cdy.simplerpc.remoting.http.HttpServer;
import com.cdy.simplerpc.remoting.rpc.RPCServer;

/**
 * 服务创建工厂
 *
 * Created by 陈东一
 * 2019/5/25 0025 19:51
 */
public class ServerFactory {
    
    public static Server createServer(IServiceRegistry registry, String protocol, String port, String ip) {
        switch (protocol) {
            case "rpc":
                return new RPCServer(registry, new ServerMetaInfo("rpc" , port, ip));
            case "http":
                return new HttpServer(registry, new ServerMetaInfo("http" ,port, ip));
            default:
                throw new RPCException("没有合适的协议");
        }
    }
}
