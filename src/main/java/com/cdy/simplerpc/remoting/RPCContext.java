package com.cdy.simplerpc.remoting;

import java.util.HashMap;
import java.util.Map;

/**
 * 上下文
 * Created by 陈东一
 * 2019/1/23 0023 22:16
 */
public class RPCContext {
    
    public static ThreadLocal<RPCContext> local = new ThreadLocal<>();
    
    private Map<String, Object> map = new HashMap<>();
    
    public Map<String, Object> getMap() {
        return map;
    }
    
    public void setMap(Map<String, Object> map) {
        this.map = map;
    }
}
