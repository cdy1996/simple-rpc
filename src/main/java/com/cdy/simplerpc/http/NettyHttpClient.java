package com.cdy.simplerpc.http;

import com.cdy.simplerpc.rpc.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpClientCodec;
import lombok.extern.slf4j.Slf4j;

/**
 * 用netty实现http服务
 * <p>
 * Created by 陈东一
 * 2019/9/1 0001 12:49
 */
@Slf4j
public class NettyHttpClient<T, R> extends NettyClient<T,R>{
    
    private NettyHttpClient() {
       super();
    }
    
    public static <T, R> NettyHttpClient<T, R> connect(String address) throws Exception {
        NettyHttpClient<T, R> nettyClient = new NettyHttpClient<>();
        nettyClient.connect(nettyClient, address);
        return nettyClient;
    }
    
    @Override
    protected void pipeline(SocketChannel ch) {
        ch.pipeline()
                .addLast(new HttpClientCodec())
                .addLast("httpRequest-encoder", new HttpRequestEncoder<>(serialize, RPCPackage.class))
                .addLast("encoder", new RPCPackageEncoder())
                .addLast("httpResponse-decoder", new HttpResponseDecoder())
                .addLast("serialize-decoder", new SerializeDecoderHandler<>(serialize, RPCPackage.class))
                .addLast("decoder", new RPCPackageClientDecoder())
                .addLast(getRPCClientHandler());
    }
}
    
