package com.cdy.simplerpc.filter;

import com.cdy.simplerpc.annotation.Order;
import com.cdy.simplerpc.event.Publisher;
import com.cdy.simplerpc.event.RPCInvokeEvent;
import com.cdy.simplerpc.proxy.Invocation;
import lombok.extern.slf4j.Slf4j;

/**
 * 事件触发
 * Created by 陈东一
 * 2018/11/25 0025 14:50
 */
@Order(-4)
@Slf4j
public class EventFilter extends FilterAdapter {
    
    private final Publisher publisher;
    
    public EventFilter(Publisher publisher) {
        this.publisher = publisher;
    }
    
    @Override
    protected void beforeClientInvoke(Invocation invocation) {
        publisher.publish(new RPCInvokeEvent.ClientBeforeInvokeEvent(invocation));
    }
    
    @Override
    protected void beforeServerInvoke(Invocation invocation) {
        publisher.publish(new RPCInvokeEvent.ServerBeforeInvokeEvent(invocation));
    }
    
    @Override
    protected void afterServerInvoke(Invocation invocation, Object o) {
        publisher.publish(new RPCInvokeEvent.ServerAfterInvokeEvent(invocation));
    }
    
    @Override
    protected void afterClientInvoke(Invocation invocation, Object o) {
        publisher.publish(new RPCInvokeEvent.ClientAfterInvokeEvent(invocation));
    }
}
