package com.cdy.simplerpc.rpc;

import com.cdy.simplerpc.serialize.ISerialize;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * todo
 * Created by 陈东一
 * 2019/8/21 0021 0:39
 */
@Slf4j
public class SerializeDecoderHandler<T> extends ByteToMessageDecoder {
    
    private final ISerialize serialize;
    private final Class<T> clazz;
    
    public SerializeDecoderHandler(ISerialize serialize, Class<T> clazz) {
        this.serialize = serialize;
        this.clazz = clazz;
    }
    
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        if (in.readableBytes() <=0){
            return;
        }
        
        byte[] bytes = new byte[in.readableBytes()];
        in.readBytes(bytes);
        T deserialize = serialize.deserialize(bytes, clazz);
        log.info("序列化解码 {}", deserialize);
        out.add(deserialize);
    }
}
