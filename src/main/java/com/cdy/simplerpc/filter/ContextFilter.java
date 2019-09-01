package com.cdy.simplerpc.filter;

import com.cdy.simplerpc.annotation.Order;
import com.cdy.simplerpc.proxy.Invocation;
import com.cdy.simplerpc.netty.rpc.RPCContext;
import lombok.extern.slf4j.Slf4j;

/**
 * 过滤器
 * Created by 陈东一
 * 2018/11/25 0025 14:50
 */
@Order(-1)
@Slf4j
public class ContextFilter extends FilterAdapter {
    
    
    @Override
    protected void afterServerInvoke(Invocation invocation, Object o) {
        //服务端返回后清理上下文
        RPCContext.cleanContext();
    }
    
    @Override
    protected void afterClientInvoke(Invocation invocation, Object o) {
//        RPCContext.cleanContext();
    }
}
