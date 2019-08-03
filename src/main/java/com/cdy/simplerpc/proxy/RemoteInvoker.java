package com.cdy.simplerpc.proxy;

import com.cdy.simplerpc.config.PropertySources;
import com.cdy.simplerpc.exception.RPCException;
import com.cdy.simplerpc.registry.IServiceDiscovery;
import com.cdy.simplerpc.remoting.Client;
import com.cdy.simplerpc.remoting.ClusterClient;

/**
 * 远程执行器
 *
 * Created by 陈东一
 * 2019/1/24 0024 23:27
 */
public class RemoteInvoker implements Invoker{
    
    
    private final Client client;
    private final PropertySources propertySources;
    
    public RemoteInvoker(PropertySources propertySources, IServiceDiscovery serviceDiscovery) {
        this.propertySources = propertySources;
        this.client = new ClusterClient(serviceDiscovery, propertySources);
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
