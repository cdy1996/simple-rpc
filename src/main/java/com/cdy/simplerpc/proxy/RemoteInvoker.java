package com.cdy.simplerpc.proxy;

import com.cdy.simplerpc.ClientBootStrap;
import com.cdy.simplerpc.annotation.ReferenceMetaInfo;
import com.cdy.simplerpc.exception.RPCException;
import com.cdy.simplerpc.remoting.Client;
import com.cdy.simplerpc.remoting.ClusterClient;
import com.cdy.simplerpc.remoting.RPCFuture;

import java.util.concurrent.ConcurrentHashMap;

import static com.cdy.simplerpc.annotation.ReferenceMetaInfo.METAINFO_KEY;

/**
 * 远程执行器
 *
 * Created by 陈东一
 * 2019/1/24 0024 23:27
 */
public class RemoteInvoker implements Invoker{
    
    public static ConcurrentHashMap<String, RPCFuture> responseFuture = new ConcurrentHashMap<>();
    public static ConcurrentHashMap<String, ReferenceMetaInfo> metaInfoMap = new ConcurrentHashMap<>();
    
    private Client client;
    private ClientBootStrap clientBootStrap;
    
    public RemoteInvoker(ClientBootStrap clientBootStrap) {
        this.clientBootStrap = clientBootStrap;
        this.client = new ClusterClient(clientBootStrap.getServiceDiscovery());
    }
    
    public Object invokeRemote(Invocation invocation) throws Exception {
        return client.invoke(invocation);
    }
    
    
    @Override
    public Object invoke(Invocation invocation) throws Exception {
        ReferenceMetaInfo referenceMetaInfo = clientBootStrap.getReferenceMetaInfo((String) invocation.getAttach().get(METAINFO_KEY));
        invocation.getAttach().put(METAINFO_KEY, referenceMetaInfo);
        
        Object invoke = invokeRemote(invocation);
        if(invoke instanceof Exception){
            throw new RPCException(((Exception) invoke).getMessage());
        }
        return invoke;
    }
    
    @Override
    public void addMetaInfo(String s, ReferenceMetaInfo data) {
        metaInfoMap.putIfAbsent(s, data);
    }
    
    @Override
    public ReferenceMetaInfo getMetaInfo(String s) {
        return metaInfoMap.get(s);
    }
}
