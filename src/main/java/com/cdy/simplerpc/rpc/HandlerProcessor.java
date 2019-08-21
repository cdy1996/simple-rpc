package com.cdy.simplerpc.rpc;

import io.netty.channel.ChannelHandlerContext;

import java.util.Objects;
import java.util.function.Function;

/**
 * todo
 * Created by 陈东一
 * 2019/8/20 0020 23:46
 */
public interface HandlerProcessor<T,R> {

    R process(T t, RPCContext ctx);


//
//    default <V> HandlerProcessor<T, V> andThen(HandlerProcessor<? super R, ? extends V> after) {
//        Objects.requireNonNull(after);
//        return (T t, ChannelHandlerContext ctx) -> after.process(process(t, ctx), ctx);
//    }

    default <V> HandlerProcessor<V, R> andThen(HandlerProcessor<? super V, ? extends T> before) {
        Objects.requireNonNull(before);
        return (V v, RPCContext ctx) -> process(before.process(v, ctx), ctx);
    }

}
