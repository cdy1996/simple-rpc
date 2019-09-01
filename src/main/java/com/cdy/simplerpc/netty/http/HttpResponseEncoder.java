package com.cdy.simplerpc.netty.http;

import com.cdy.simplerpc.netty.rpc.RPCPackage;
import com.cdy.simplerpc.serialize.ISerialize;
import com.cdy.simplerpc.serialize.JsonSerialize;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.handler.codec.http.*;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * todo
 * Created by 陈东一
 * 2019/9/1 0001 14:35
 */
@Slf4j
public class HttpResponseEncoder<T> extends MessageToMessageEncoder<RPCPackage> {
    
    
    private final ISerialize serialize;
    private final Class<T> clazz;
    
    public HttpResponseEncoder(ISerialize serialize, Class<T> clazz) {
        this.serialize = serialize;
        this.clazz = clazz;
    }
    
    @Override
    protected void encode(ChannelHandlerContext ctx, RPCPackage msg, List<Object> out) throws Exception {
        log.info("编码response {}", msg);
        Object serialize = this.serialize.serialize((T) msg, clazz);
        
        DefaultFullHttpResponse httpResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
                HttpResponseStatus.OK,
                Unpooled.wrappedBuffer((byte[]) serialize));//建立httpResponse
        
        if (this.serialize instanceof JsonSerialize) {
            httpResponse.headers().set(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON);
        } else {
            httpResponse.headers().set(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_OCTET_STREAM);
        }
        httpResponse.headers().set(HttpHeaderNames.CONTENT_LENGTH, httpResponse.content().readableBytes());
        out.add(httpResponse);
    }
}
