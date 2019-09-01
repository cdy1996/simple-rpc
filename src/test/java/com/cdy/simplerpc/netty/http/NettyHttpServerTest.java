package com.cdy.simplerpc.netty.http;

import org.junit.Test;

public class NettyHttpServerTest {
    
    
    @Test
    public void openServer() throws Exception {
        NettyHttpServer<String, String> server = new NettyHttpServer<>();
        server.addProcessor((s, ctx) -> {
            System.out.println(s);
            return "11111";
        });
        
        server.openServer("127.0.0.1", 8080);
        
        System.in.read();
    }
}