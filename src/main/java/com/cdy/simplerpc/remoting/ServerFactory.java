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
 * <p>
 * Created by 陈东一
 * 2019/5/25 0025 19:51
 */
@Slf4j
public class ServerFactory {

    public static Server createServer(List<IServiceRegistry> registryList, PropertySources propertySources, String protocol, String key) {
        String ip = propertySources.resolveProperty(key + "." + ConfigConstants.ip);
        if (StringUtil.isBlank(ip)) {
            ip = propertySources.resolveProperty("registry.ip");
        }
        // rpc-10001
        String[] split = protocol.split("-");

        // todo spi
        if (protocol.startsWith("rpc")) {
            return new RPCServer(new ServerMetaInfo(split[0], split[1], ip, key), registryList, SerializeFactory.createSerialize(propertySources), propertySources);
        } else if (protocol.startsWith("http")) {
            return new HttpServer(new ServerMetaInfo(split[0], split[1], ip, key), registryList, SerializeFactory.createSerialize(propertySources), propertySources);
        }
        throw new RPCException("没有合适的协议");

    }
}
