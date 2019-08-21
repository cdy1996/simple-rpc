package com.cdy.simplerpc.filter;

import com.cdy.simplerpc.annotation.Order;
import com.cdy.simplerpc.monitor.MonitorEntity;
import com.cdy.simplerpc.monitor.MonitorSend;
import com.cdy.simplerpc.proxy.Invocation;
import com.cdy.simplerpc.rpc.RPCContext;

import java.util.Date;
import java.util.Map;

/**
 * 用于记录服务调用信息
 * Created by 陈东一
 * 2019/2/8 0008 17:16
 */
@Order(-3)
public class MonitorFilter extends FilterAdapter {
    
    private final MonitorSend monitorSend;
    
    public MonitorFilter(MonitorSend monitorSend) {
        this.monitorSend = monitorSend;
    }
    
    @Override
    protected void beforeServerInvoke(Invocation invocation) {
        // 记录来自客户端的调用的信息
        // 客户端的ip 调用的方法名称和参数
        Map<String, Object> map = RPCContext.current().getAttach();
        map.get("traceId");//todo
        map.get("spanId");
        
        MonitorEntity.start(new Date(), invocation.getMethodName(), invocation.getArgs());
    }
    
    @Override
    protected void beforeClientInvoke(Invocation invocation) {
        // 记录调用服务端的调用信息
        // 记录服务端的地址 调用的方法名称和参数
        MonitorEntity.start(new Date(), invocation.getMethodName(), invocation.getArgs());
        
    }
    
    @Override
    protected void afterServerInvoke(Invocation invocation, Object o) {
        Map<String, Object> rpcContext1Map = RPCContext.current().getAttach();
        MonitorEntity end = MonitorEntity.end((String) rpcContext1Map.get(com.cdy.simplerpc.rpc.RPCContext.address),new Date(), o);
        monitorSend.send(end);
    }
    
    @Override
    protected void afterClientInvoke(Invocation invocation, Object o) {
        Map<String, Object> rpcContext1Map = RPCContext.current().getAttach();
        MonitorEntity end = MonitorEntity.end((String) rpcContext1Map.get(com.cdy.simplerpc.rpc.RPCContext.address), new Date(), o);
        monitorSend.send(end);
    }
}
