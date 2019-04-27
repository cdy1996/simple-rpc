package com.cdy.simplerpc.remoting;

import com.cdy.simplerpc.annotation.ReferenceMetaInfo;
import com.cdy.simplerpc.exception.RPCException;
import com.cdy.simplerpc.proxy.Invocation;
import com.cdy.simplerpc.registry.IServiceDiscovery;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 集群的装饰层
 *
 * Created by 陈东一
 * 2019/4/27 0027 16:16
 */
public class ClusterClient extends AbstractClient {
    
    private IServiceDiscovery servceDiscovery;
    private static Map<String, Client> clientMap = new ConcurrentHashMap<>();
    
    public ClusterClient(IServiceDiscovery servceDiscovery) {
        this.servceDiscovery = servceDiscovery;
    }
    
    @Override
    public Object invoke(Invocation invocation) throws Exception {
        ReferenceMetaInfo referenceMetaInfo = (ReferenceMetaInfo) invocation.getAttach().get("metaInfoKey");
        
        //服务发现
        String serviceName = invocation.getInterfaceClass().getName();
        String address = servceDiscovery.discovery(serviceName, referenceMetaInfo.getProtocols());
        String[] split = address.split("-");
        String protocol = split[0];
        Client client = clientMap.get(serviceName + protocol);
        if (client == null) {
            try {
                Class<?> clazz = Class.forName("com.cdy.simplerpc.remoting." + protocol.toLowerCase() + "." + protocol.toUpperCase() + "Client");
                client = (Client) clazz.getConstructor().newInstance();
            } catch (ClassNotFoundException e) {
                throw new RPCException("不支持的协议");
            }
            clientMap.putIfAbsent(serviceName + protocol, client);
        }
        invocation.setAddress(address.replace(protocol + "-", ""));
        return client.invoke(invocation);
    }
    
    @Override
    public void close() {
    
    }
}
