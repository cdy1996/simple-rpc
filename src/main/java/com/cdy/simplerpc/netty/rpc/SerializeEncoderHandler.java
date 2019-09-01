package com.cdy.simplerpc.netty.rpc;

import com.cdy.simplerpc.serialize.ISerialize;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;

/**
 * todo
 * Created by 陈东一
 * 2019/8/21 0021 0:38
 */
@Slf4j
public class SerializeEncoderHandler<T> extends MessageToByteEncoder<Object> {
    
    
    private final ISerialize serialize;
    private final Class<T> clazz;
    
    public SerializeEncoderHandler(ISerialize serialize, Class<T> clazz) {
        this.serialize = serialize;
        this.clazz = clazz;
    }
    
    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        ByteBuf delimiter = Unpooled.copiedBuffer("@@@".getBytes());
        log.info("序列化编码 {}", msg);
        // todo 大文件分段处理
        Object serialize = this.serialize.serialize((T)msg, clazz);
        out.writeBytes((byte[]) serialize);
        out.writeBytes(delimiter);
        ctx.flush();
    }
}
