package com.cdy.simplerpc.proxy;

import com.cdy.simplerpc.cluster.ClusterClient;
import com.cdy.simplerpc.cluster.ClusterFactory;
import com.cdy.simplerpc.config.PropertySources;
import com.cdy.simplerpc.exception.RPCException;
import com.cdy.simplerpc.registry.IServiceDiscovery;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * 远程执行器
 *
 * Created by 陈东一
 * 2019/1/24 0024 23:27
 */
@Slf4j
public class RemoteInvoker implements Invoker{
    
    
    private final ClusterClient client;
    private final PropertySources propertySources;
    
    public RemoteInvoker(PropertySources propertySources, Map<String, IServiceDiscovery> servceDiscoveryMap,
                         String type) {
        this.propertySources = propertySources;
        this.client = ClusterFactory.createCluster(propertySources, servceDiscoveryMap, type);
        try {
            client.init(type, type);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RPCException(e);
        }
    }
    
    public Object invokeRemote(Invocation invocation) throws Exception {
        return client.invoke(invocation);
    }
    
    
    @Override
    public Object invoke(Invocation invocation) throws Exception {
        Object invoke = invokeRemote(invocation);
        if(invoke instanceof Exception){
            throw new RPCException(((Exception) invoke).getMessage());
        }
        return invoke;
    }
 
}
