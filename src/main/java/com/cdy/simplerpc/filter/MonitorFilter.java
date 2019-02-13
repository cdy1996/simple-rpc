package com.cdy.simplerpc.filter;

import com.cdy.simplerpc.annotation.Order;
import com.cdy.simplerpc.monitor.MonitorEntity;
import com.cdy.simplerpc.monitor.MonitorSend;
import com.cdy.simplerpc.proxy.Invocation;

import java.util.Date;

/**
 * 用于记录服务调用信息
 * Created by 陈东一
 * 2019/2/8 0008 17:16
 */
@Order(-3)
public class MonitorFilter extends FilterAdapter {
    
    MonitorSend monitorSend;
    
    public MonitorFilter(MonitorSend monitorSend) {
        this.monitorSend = monitorSend;
    }
    
    @Override
    public void beforeServerInvoke(Invocation invocation) {
        // 记录来自客户端的调用的信息
        // 客户端的ip 调用的方法名称和参数
        MonitorEntity.start((String)invocation.getAttach().get("address"), new Date(), invocation.getMethodName(), invocation.getArgs());
    }
    
    @Override
    public void beforeClientInvoke(Invocation invocation) {
        // 记录调用服务端的调用信息
        // 记录服务端的地址 调用的方法名称和参数
        MonitorEntity.start((String)invocation.getAttach().get("address"), new Date(), invocation.getMethodName(), invocation.getArgs());
        
    }
    
    @Override
    public void afterServerInvoke(Invocation invocation, Object o) {
        MonitorEntity end = MonitorEntity.end(new Date(), o);
        monitorSend.send(end);
    }
    
    @Override
    public void afterClientInvoke(Invocation invocation, Object o) {
        MonitorEntity end = MonitorEntity.end(new Date(), o);
        monitorSend.send(end);
    }
}
