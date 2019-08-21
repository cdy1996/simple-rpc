package com.cdy.simplerpc.rpc;

import io.netty.channel.ChannelHandlerContext;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Data
public class RPCContext {

    //reference注解中的value,存放在context的key
    public static final String annotationKey = "annotationKey";
    //客户端访问服务端的地址, 存放在context的key
    public static final String address = "address";
    // 用于rpc 模式下
    private String requestId;
    //  用于rpc 模式下
    private ChannelHandlerContext ctx;
    private Object target;

    public RPCContext() {
    }

    public RPCContext(String requestId, ChannelHandlerContext ctx, Object target) {
        this.requestId = requestId;
        this.ctx = ctx;
        this.target = target;
    }

    @Getter @Setter
    private Map<String, Object> attach = new HashMap<>();

    private static ThreadLocal<RPCContext> local = new ThreadLocal<>();

    public static RPCContext current() {
        RPCContext rpcContext = local.get();
        if (rpcContext != null) {
            return rpcContext;
        }
        rpcContext = new RPCContext();
        local.set(rpcContext);
        return rpcContext;
    }
    public static void set(RPCContext context) {
        local.set(context);
    }
    public static void setOnServerReceiver(RPCContext context) {
        if (local.get() == null) {

        }
        local.set(context);
    }

    public static void cleanContext() {
        local.remove();
    }


}
