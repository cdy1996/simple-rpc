package com.cdy.simplerpc.proxy;

import com.cdy.simplerpc.remoting.Client;
import com.cdy.simplerpc.remoting.RPCFuture;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 远程执行器
 *
 * Created by 陈东一
 * 2019/1/24 0024 23:27
 */
public class RemoteInvoker implements Invoker{
    
    public static ConcurrentHashMap<String, RPCFuture> responseFuture = new ConcurrentHashMap<>();
    
    private Client client;
    
    
    public RemoteInvoker(Client client) {
        this.client = client;
    }
    
    
    public Object invokeRemote(Invocation invocation) throws Exception {
        return client.invoke(invocation);
    }
    
    
    @Override
    public Object invoke(Invocation invocation) throws Exception {
        Object invoke = invokeRemote(invocation);
        if(invoke instanceof Exception){
            throw new RuntimeException(((Exception) invoke).getMessage());
        }
        return invoke;
    }
}
