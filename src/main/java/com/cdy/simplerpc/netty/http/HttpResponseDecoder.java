package com.cdy.simplerpc.netty.http;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * todo
 * Created by 陈东一
 * 2019/9/1 0001 14:35
 */
@Slf4j
@ChannelHandler.Sharable
public class HttpResponseDecoder extends MessageToMessageDecoder<Object> {
    
    @Override
    protected void decode(ChannelHandlerContext ctx, Object response, List<Object> out) throws Exception {
        log.info("解码response {}", response);
    
        if(response instanceof HttpResponse){
        }
        if(response instanceof HttpContent) {
            HttpContent content = (HttpContent) response;
            content.content().retain();
            out.add(content.content());
        }
    }
}
