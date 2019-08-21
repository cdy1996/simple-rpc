package com.cdy.simplerpc.remoting;

import com.cdy.simplerpc.config.ConfigConstants;
import com.cdy.simplerpc.config.PropertySources;
import com.cdy.simplerpc.exception.RPCException;
import com.cdy.simplerpc.registry.IServiceRegistry;
import com.cdy.simplerpc.remoting.http.HttpServer;
import com.cdy.simplerpc.remoting.rpc.RPCServer;
import com.cdy.simplerpc.serialize.SerializeFactory;
import com.cdy.simplerpc.util.StringUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * 服务创建工厂
 *
 * Created by 陈东一
 * 2019/5/25 0025 19:51
 */
@Slf4j
public class ServerFactory {
    
    public static Server createServer(List<IServiceRegistry> registryList, PropertySources propertySources, String protocol) {
        return  createServer(registryList, propertySources,protocol, null);
    }
    
    public static Server createServer(List<IServiceRegistry> registryList, PropertySources propertySources, String protocol, String key) {
        String port,ip;
        if (StringUtil.isBlank(key)) {
            port = propertySources.resolveProperty("registry.port");
            ip = propertySources.resolveProperty("registry.ip");
        } else {
            port = propertySources.resolveProperty(key + "." + ConfigConstants.port);
            ip = propertySources.resolveProperty(key + "." + ConfigConstants.ip);
        }
        log.info("创建服务处理 ip{} port{}", ip, port);
    
        // todo spi
        if ("rpc".equalsIgnoreCase(protocol)) {
            return new RPCServer(new ServerMetaInfo(protocol, port, ip), registryList,  SerializeFactory.createSerialize(propertySources), propertySources);
        } else if ("http".equalsIgnoreCase(protocol)) {
            return new HttpServer(new ServerMetaInfo(protocol, port, ip), registryList, SerializeFactory.createSerialize(propertySources), propertySources);
        }
        throw new RPCException("没有合适的协议");
       
    }
}
