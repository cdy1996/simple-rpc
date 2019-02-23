package com.cdy.simplerpc.filter;

import com.cdy.simplerpc.annotation.Order;
import com.cdy.simplerpc.proxy.Invocation;
import com.cdy.simplerpc.remoting.RPCContext;

/**
 * 过滤器
 * Created by 陈东一
 * 2018/11/25 0025 14:50
 */
@Order(-1)
public class ContextFilter extends FilterAdapter {
    
    
    /**
     * 将rpccontext上下文附着在attach上传到服务端
     *
     * @param invocation
     * @throws Exception
     */
    @Override
    protected void beforeServerInvoke(Invocation invocation) {
        RPCContext rpcContext = RPCContext.newContext();
        rpcContext.setMap(invocation.getAttach());
        System.out.println("服务端过滤器将客户端传过来的隐式传参放在threadlocal中");
    }
    
    @Override
    protected void beforeClientInvoke(Invocation invocation) {
        RPCContext rpcContext = RPCContext.current();
        invocation.setAttach(rpcContext.getMap());
        System.out.println("客户端过滤器将threadlocal传递到服务端");
    }
}
