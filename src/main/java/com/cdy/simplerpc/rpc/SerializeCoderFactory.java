package com.cdy.simplerpc.rpc;

import com.cdy.simplerpc.remoting.RPCRequest;
import com.cdy.simplerpc.remoting.RPCResponse;
import com.cdy.simplerpc.rpc.SerializeDecoderHandler;
import com.cdy.simplerpc.rpc.SerializeEncoderHandler;
import com.cdy.simplerpc.serialize.ISerialize;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;

/**
 * 序列化工厂
 * Created by 陈东一
 * 2019/8/18 0018 16:41
 */
@Slf4j
public class SerializeCoderFactory {
    
    public static ByteBuf buffer = Unpooled.buffer();
    static {
        buffer.writeBytes("@@@".getBytes(StandardCharsets.UTF_8));
    }
    
    public static ByteToMessageDecoder getDecoder(ISerialize serialize, boolean server){
        if (server) {
            return new SerializeDecoderHandler(serialize, RPCRequest.class);
        } else {
            return new SerializeDecoderHandler(serialize, RPCResponse.class);
        }
    }
    public static MessageToByteEncoder getEncoder(ISerialize serialize, boolean server){
        if (server) {
            return new SerializeEncoderHandler(serialize, RPCResponse.class);
        } else {
            return new SerializeEncoderHandler(serialize, RPCRequest.class);
        }
    }
    
    
}
