package com.cdy.simplerpc.http;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpRequest;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * todo
 * Created by 陈东一
 * 2019/9/1 0001 14:35
 */
@Slf4j
@ChannelHandler.Sharable
public class HttpRequestDecoder extends MessageToMessageDecoder<Object> {
    
    
    @Override
    protected void decode(ChannelHandlerContext ctx, Object msg, List<Object> out) throws Exception {
        log.info("解码request {}", msg);
        if (msg instanceof HttpRequest) {
        }
        if (msg instanceof HttpContent){
            HttpContent httpContent = (HttpContent) msg;
            httpContent.content().retain();
            out.add(httpContent.content());
        }
    }
}
