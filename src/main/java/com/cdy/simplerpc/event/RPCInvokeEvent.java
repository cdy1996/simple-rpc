package com.cdy.simplerpc.event;

import com.cdy.simplerpc.proxy.Invocation;

/**
 * 事件接口
 * Created by 陈东一
 * 2019/4/14 0014 12:11
 */
public class RPCInvokeEvent implements RPCEvent{

    private Invocation invocation;
    
    public RPCInvokeEvent(Invocation invocation) {
        this.invocation = invocation;
    }
    
    
    public static class ClientBeforeInvokeEvent extends RPCInvokeEvent{
    
        public ClientBeforeInvokeEvent(Invocation invocation) {
            super(invocation);
        }
    }
    
    public static class ClientAfterInvokeEvent extends RPCInvokeEvent{
    
        public ClientAfterInvokeEvent(Invocation invocation) {
            super(invocation);
        }
    }
    
    
    public static class ServerBeforeInvokeEvent extends RPCInvokeEvent{
    
        public ServerBeforeInvokeEvent(Invocation invocation) {
            super(invocation);
        }
    }
    
    public static class ServerAfterInvokeEvent extends RPCInvokeEvent{
    
        public ServerAfterInvokeEvent(Invocation invocation) {
            super(invocation);
        }
    }

}
