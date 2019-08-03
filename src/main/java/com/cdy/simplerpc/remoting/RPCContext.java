package com.cdy.simplerpc.remoting;

import java.util.HashMap;
import java.util.Map;

/**
 * 上下文
 * Created by 陈东一
 * 2019/1/23 0023 22:16
 */
public class RPCContext {
    //reference注解中的value,存放在context的key
    public static final String annotationKey = "annotationKey";
    //客户端访问服务端的地址, 存放在context的key
    public static final String address = "address";
    
    private static ThreadLocal<RPCContext> local = new ThreadLocal<>();
    
    public static RPCContext current(){
        RPCContext rpcContext = local.get();
        if (rpcContext != null) {
            return rpcContext;
        }
    
        rpcContext = new RPCContext();
        local.set(rpcContext);
        return rpcContext;
    }
    
    public static void cleanContext(){
        local.remove();
    }
    
    /**
     * 存储上下文map
     */
    private Map<String, Object> map = new HashMap<>();
    
    public Map<String, Object> getMap() {
        return map;
    }
    
    public void setMap(Map<String, Object> map) {
        this.map = map;
    }
}
