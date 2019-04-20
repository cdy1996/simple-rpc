package com.cdy.simplerpc.remoting;

import com.cdy.simplerpc.ClientBootStrap;
import com.cdy.simplerpc.event.Publisher;
import com.cdy.simplerpc.registry.IServiceDiscovery;

/**
 * 服务端
 * Created by 陈东一
 * 2018/9/1 21:41
 */
public abstract class AbstractClient implements Client {
    private IServiceDiscovery serviceDiscovery;
    private ClientBootStrap clientBootStrap;
    private Publisher publisher;
    
    public void invokeBefore(){
    
    }
    public void invokeAfter(){
    
    }
    
    @Override
    public ClientBootStrap getClientBootStrap() {
        return clientBootStrap;
    }
    
    
    public void setClientBootStrap(ClientBootStrap clientBootStrap) {
        this.clientBootStrap = clientBootStrap;
    }
    
    public IServiceDiscovery getServiceDiscovery() {
        return serviceDiscovery;
    }
    
    @Override
    public void setServiceDiscovery(IServiceDiscovery serviceDiscovery) {
        this.serviceDiscovery = serviceDiscovery;
    }
}
