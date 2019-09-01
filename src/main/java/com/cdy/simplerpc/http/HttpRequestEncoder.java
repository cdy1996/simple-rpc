package com.cdy.simplerpc.http;

import com.cdy.simplerpc.rpc.RPCPackage;
import com.cdy.simplerpc.serialize.ISerialize;
import com.cdy.simplerpc.serialize.JsonSerialize;
import com.cdy.simplerpc.util.StringUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.handler.codec.http.*;
import io.netty.util.Attribute;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.util.List;

import static com.cdy.simplerpc.rpc.NettyClient.ATTRIBUTE_KEY_ADDRESS;

/**
 * todo
 * Created by 陈东一
 * 2019/9/1 0001 14:35
 */
@Slf4j
public class HttpRequestEncoder<T> extends MessageToMessageEncoder<RPCPackage> {
    
    
    private final ISerialize serialize;
    private final Class<T> clazz;
    
    public HttpRequestEncoder(ISerialize serialize, Class<T> clazz) {
        this.serialize = serialize;
        this.clazz = clazz;
    }
    
    @Override
    protected void encode(ChannelHandlerContext ctx, RPCPackage msg, List<Object> out) throws Exception {
        log.info("编码request {}", msg);
        Attribute<String> attr = ctx.channel().attr(ATTRIBUTE_KEY_ADDRESS);
        String address = attr.get();
        Object serialize = this.serialize.serialize((T)msg, clazz);
        URI uri = new URI("http://"+address);
        DefaultFullHttpRequest httpRequest = new DefaultFullHttpRequest(
                HttpVersion.HTTP_1_1,
                HttpMethod.GET,
                uri.toASCIIString(),
                Unpooled.wrappedBuffer((byte[]) serialize));//生成一个默认的httpRequest。
    
        httpRequest.headers().set(HttpHeaderNames.HOST, StringUtil.getServer(address).getFirst());
        httpRequest.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
        if (this.serialize instanceof JsonSerialize){
            httpRequest.headers().set(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON);
        } else{
            httpRequest.headers().set(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_OCTET_STREAM);
        }
        httpRequest.headers().set(HttpHeaderNames.CONTENT_LENGTH, httpRequest.content().readableBytes());
        out.add(httpRequest);
    }
}
