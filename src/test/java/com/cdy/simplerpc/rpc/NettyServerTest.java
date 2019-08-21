package com.cdy.simplerpc.rpc;

import org.junit.Test;

import static org.junit.Assert.*;

public class NettyServerTest {

    @Test
    public void openServer() throws Exception {
        NettyServer<String, String> server = new NettyServer<>();
        server.openServer("127.0.0.1", 8080);

    }
}