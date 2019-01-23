package com.cdy.simplerpc.filter;

import com.cdy.simplerpc.proxy.Invocation;
import com.cdy.simplerpc.remoting.RPCContext;

import java.util.Map;

/**
 * 过滤器
 * Created by 陈东一
 * 2018/11/25 0025 14:50
 */
public class ClientContextFilter implements Filter {
    private Filter next;
    
    /**
     * 将rpccontext上下文附着在attach上传到服务端
     * @param invocation
     * @throws Exception
     */
    @Override
    public void doFilter(Invocation invocation) throws Exception {
        RPCContext rpcContext = RPCContext.local.get();
        Map<String, Object> map = rpcContext.getMap();
        invocation.setAttach(map);
        next.doFilter(invocation);
    }
    
    @Override
    public void setNext(Filter filter) {
        this.next = filter;
    }
    
    
}
