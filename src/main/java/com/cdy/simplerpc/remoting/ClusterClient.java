package com.cdy.simplerpc.remoting;

import com.cdy.simplerpc.config.ConfigConstants;
import com.cdy.simplerpc.config.PropertySources;
import com.cdy.simplerpc.exception.RPCException;
import com.cdy.simplerpc.proxy.Invocation;
import com.cdy.simplerpc.registry.IServiceDiscovery;
import com.cdy.simplerpc.util.StringUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 集群的装饰层
 * <p>
 * Created by 陈东一
 * 2019/4/27 0027 16:16
 */
@Slf4j
public class ClusterClient extends AbstractClient {
    
    private final Map<String, IServiceDiscovery> servceDiscoveryMap;
    private final Map<String, Client> clientMap = new ConcurrentHashMap<>();
    
    public ClusterClient(PropertySources propertySources, Map<String, IServiceDiscovery> servceDiscoveryMap) {
        super(propertySources);
        this.servceDiscoveryMap = servceDiscoveryMap;
    }
    
    @Override
    public Object invoke(Invocation invocation) throws Exception {
        RPCContext rpcContext1 = RPCContext.current();
        Map<String, Object> rpcContext1Map = rpcContext1.getMap();
        
        // 对应注解的类名作为key,可以从属性集中获取
        String annotationKey = (String) rpcContext1Map.get(RPCContext.annotationKey);
        String directAddress = propertySources.resolveProperty(annotationKey + "." + ConfigConstants.url);
        
        //服务发现
        String serviceName = invocation.getInterfaceClass().getName();
        String address = null;
        while (StringUtil.isBlank(address)) {
            if (StringUtil.isBlank(directAddress)) {
                String protocols = propertySources.resolveProperty(annotationKey + "." + ConfigConstants.protocols);
                //多订阅时,只能选择一个注册中心
                if (StringUtil.isBlank(annotationKey)) {
                    address = servceDiscoveryMap.get(propertySources.resolveProperty(annotationKey + ".discovery.type")).discovery(serviceName, protocols.split(","));
                } else {
                    address = servceDiscoveryMap.get(propertySources.resolveProperty("discovery.type")).discovery(serviceName, protocols.split(","));
                }
            } else {
                //直连
                address = directAddress;
            }
            log.warn("没有可以用的服务地址");
        }
        
        String[] split = address.split("-");
        String protocol = split[0];
        Client client = clientMap.get(serviceName + protocol);
        if (client == null) {
            try {
                String replace = ClusterClient.class.getName().replace(ClusterClient.class.getSimpleName(), "");
                Class<?> clazz = Class.forName(replace + protocol.toLowerCase() + "." + protocol.toUpperCase() + "Client");
                client = (Client) clazz.getConstructor().newInstance(propertySources);
            } catch (ClassNotFoundException e) {
                throw new RPCException("不支持的协议" + protocol);
            }
            clientMap.putIfAbsent(serviceName + protocol, client);
        }
        
        
        rpcContext1Map.put("address", address.replace(protocol + "-", ""));
        return client.invoke(invocation);
    }
    
    @Override
    public void close() {
    
    }
}
