package com.cdy.simplerpc.remoting.rpc;

import com.cdy.simplerpc.remoting.RPCRequest;
import com.cdy.simplerpc.remoting.RPCResponse;
import com.cdy.simplerpc.serialize.ISerialize;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;

import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * 序列化工厂
 * Created by 陈东一
 * 2019/8/18 0018 16:41
 */
@Slf4j
public class SerializeCoderFactory {
    
    public static ByteBuf buffer = Unpooled.buffer();
    static {
        try {
            buffer.writeBytes("@@@".getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            log.error(e.getMessage(), e);
        }
    }
    
    public static ByteToMessageDecoder getDecoder(ISerialize serialize, boolean server){
        if (server) {
            return new ServerSerializeDecoderHandler(serialize);
        } else {
            return new ClientSerializeDecoderHandler(serialize);
        }
    }
    public static MessageToByteEncoder getEncoder(ISerialize serialize, boolean server){
        if (server) {
            return new ServerSerializeEncoderHandler(serialize);
        } else {
            return new ClientSerializeEncoderHandler(serialize);
        }
    }
    
    
    @Slf4j
    static class ClientSerializeDecoderHandler extends ByteToMessageDecoder {
        
        private final ISerialize serialize;
        
        public ClientSerializeDecoderHandler(ISerialize serialize) {
            this.serialize = serialize;
        }
        
        
        @Override
        protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
            byte[] bytes = new byte[in.readableBytes()];
            in.readBytes(bytes);
            RPCResponse deserialize = serialize.deserialize(bytes, RPCResponse.class);
            log.info("序列化解码 {}", deserialize);
            out.add(deserialize);
        }
    }
    
    @Slf4j
    static class ClientSerializeEncoderHandler extends MessageToByteEncoder<RPCRequest> {
        
        
        private final ISerialize serialize;
        
        public ClientSerializeEncoderHandler(ISerialize serialize) {
            this.serialize = serialize;
        }
        
        
        
        @Override
        protected void encode(ChannelHandlerContext ctx, RPCRequest msg, ByteBuf out) throws Exception {
            log.info("序列化编码 {}", msg);
            // todo 大文件分段处理
            Object serialize = this.serialize.serialize(msg, RPCRequest.class);
            out.writeBytes((byte[]) serialize);
            out.writeBytes(buffer);
            ctx.flush();
        }
    }
    
    
    @Slf4j
    static class ServerSerializeDecoderHandler extends ByteToMessageDecoder {
        
        private final ISerialize serialize;
        
        public ServerSerializeDecoderHandler(ISerialize serialize) {
            this.serialize = serialize;
        }
        
        @Override
        protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
            byte[] bytes = new byte[in.readableBytes()];
            in.readBytes(bytes);
            RPCRequest deserialize = serialize.deserialize(bytes, RPCRequest.class);
            log.info("序列化解码 {}", deserialize);
            out.add(deserialize);
        }
    }
    
    
    @Slf4j
    static class ServerSerializeEncoderHandler extends MessageToByteEncoder<RPCResponse> {
        
        
        private final ISerialize serialize;
        
        public ServerSerializeEncoderHandler(ISerialize serialize) {
            this.serialize = serialize;
        }
        
        
        @Override
        protected void encode(ChannelHandlerContext ctx, RPCResponse msg, ByteBuf out) throws Exception {
            log.info("序列化编码 {}", msg);
            // todo 大文件分段处理
            Object serialize = this.serialize.serialize(msg, RPCResponse.class);
            out.writeBytes((byte[]) serialize);
            out.writeBytes(buffer);
            ctx.flush();
        }
    }
    
    
}
