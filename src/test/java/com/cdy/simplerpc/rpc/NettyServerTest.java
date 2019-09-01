package com.cdy.simplerpc.rpc;

import org.junit.Test;

public class NettyServerTest {

    @Test
    public void openServer() throws Exception {
        NettyServer<String, String> server = new NettyServer<>();
        server.addProcessor((s, ctx) -> {
            System.out.println(s);
            return "11111";
        });
        
        server.openServer("127.0.0.1", 8080);

        System.in.read();
    }
}