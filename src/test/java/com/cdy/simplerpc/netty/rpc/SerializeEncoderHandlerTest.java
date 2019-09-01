package com.cdy.simplerpc.netty.rpc;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.Unpooled;
import org.junit.Test;

import java.nio.charset.StandardCharsets;

public class SerializeEncoderHandlerTest {
    
    @Test
    public void encode() {
        
        ByteBuf buffer = Unpooled.buffer();
        buffer.writeBytes("@@@".getBytes(StandardCharsets.UTF_8));
        ByteBuf byteBuf = ByteBufAllocator.DEFAULT.buffer(16, 16);
        byteBuf.writeBytes(buffer);
        System.out.println(byteBuf.readCharSequence(3,StandardCharsets.UTF_8));
//        buffer.resetReaderIndex();
        ByteBuf slice = buffer.slice();
        slice.resetReaderIndex();
        ByteBuf byteBuf1 = ByteBufAllocator.DEFAULT.buffer(16, 16);
        byteBuf1.writeBytes(slice);
        System.out.println(byteBuf1.readCharSequence(3,StandardCharsets.UTF_8));
    
    }
}