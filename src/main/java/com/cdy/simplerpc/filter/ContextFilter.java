package com.cdy.simplerpc.filter;

import com.cdy.simplerpc.proxy.Invocation;
import com.cdy.simplerpc.remoting.RPCContext;

/**
 * 过滤器
 * Created by 陈东一
 * 2018/11/25 0025 14:50
 */
public class ContextFilter extends FilterAdapter {
    private Filter next;
    
    private Boolean isServer;
    
    public ContextFilter(Boolean isServer) {
        this.isServer = isServer;
    }
    
    /**
     * 将rpccontext上下文附着在attach上传到服务端
     * @param invocation
     * @throws Exception
     */
    @Override
    public Object doFilter(Invocation invocation) throws Exception {
        if(isServer) {
            RPCContext.local.set(new RPCContext());
            RPCContext.local.get().setMap(invocation.getAttach());
            System.out.println("服务端过滤器将客户端传过来的隐式传参放在threadlocal中");
        } else {
            RPCContext rpcContext = RPCContext.local.get();
            if (rpcContext != null) {
                invocation.setAttach(RPCContext.local.get().getMap());
            }
            System.out.println("客户端过滤器将threadlocal传递到服务端");
        }
        return next.doFilter(invocation);
    }
    
    @Override
    public void setNext(Filter filter) {
        this.next = filter;
    }
    
    
}
