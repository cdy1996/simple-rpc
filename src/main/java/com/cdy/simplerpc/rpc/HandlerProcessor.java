package com.cdy.simplerpc.rpc;

import io.netty.channel.ChannelHandlerContext;

/**
 * todo
 * Created by 陈东一
 * 2019/8/20 0020 23:46
 */
public interface HandlerProcessor<T> {
    
    void process(T t, ChannelHandlerContext ctx);
    
    
    default HandlerProcessor<T> then(HandlerProcessor<T> handlerProcessor){
    
        return (T t, ChannelHandlerContext ctx) -> { process(t, ctx); handlerProcessor.process(t, ctx); };
    }
}
