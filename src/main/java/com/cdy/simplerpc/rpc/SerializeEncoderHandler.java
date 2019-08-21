package com.cdy.simplerpc.rpc;

import com.cdy.simplerpc.serialize.ISerialize;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;

/**
 * todo
 * Created by 陈东一
 * 2019/8/21 0021 0:38
 */
@Slf4j
public class SerializeEncoderHandler<T> extends MessageToByteEncoder<T> {
    
    
    private final ISerialize serialize;
    private final Class<T> clazz;
    
    public SerializeEncoderHandler(ISerialize serialize, Class<T> clazz) {
        this.serialize = serialize;
        this.clazz = clazz;
    }
    
    
    @Override
    protected void encode(ChannelHandlerContext ctx, T msg, ByteBuf out) throws Exception {
        log.info("序列化编码 {}", msg);
        // todo 大文件分段处理
        Object serialize = this.serialize.serialize(msg, clazz);
        out.writeBytes((byte[]) serialize);
        out.writeBytes(SerializeCoderFactory.buffer);
        ctx.flush();
    }
}
