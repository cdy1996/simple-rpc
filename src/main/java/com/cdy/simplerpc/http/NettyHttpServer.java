package com.cdy.simplerpc.http;

import com.cdy.simplerpc.rpc.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;
import lombok.extern.slf4j.Slf4j;

/**
 * 用netty实现http服务
 * <p>
 * Created by 陈东一
 * 2019/9/1 0001 12:49
 */
@Slf4j
public class NettyHttpServer<T, R> extends NettyServer<T,R>{
  
    @Override
    protected void pipeline(SocketChannel ch) {
        ch.pipeline().addLast(
                new HttpServerCodec())
                .addLast("httpResponse-encoder", new HttpResponseEncoder<>(serialize, RPCPackage.class))
                .addLast("encoder", new RPCPackageEncoder())
                .addLast("httpRequest-decoder", new HttpRequestDecoder())
                .addLast("serialize-decoder", new SerializeDecoderHandler<>(serialize, RPCPackage.class))
                .addLast("decoder", new RPCPackageServerDecoder())
                .addLast(getRPCServerHandler());
    }
  
}
