package com.cdy.simplerpc.test;

import com.cdy.simplerpc.ClientBootStrap;
import com.cdy.simplerpc.annotation.RPCReference;
import com.cdy.simplerpc.registry.simple.SimpleDiscoveryImpl;
import com.cdy.simplerpc.remoting.netty.RPCClient;

import java.util.Collections;

/**
 * 客户端测试
 * Created by 陈东一
 * 2019/1/22 0022 22:28
 */
public class ClientTest {
    
    @RPCReference
    private TestService testService;
    
    public void test() {
        testService.test("123");
    }
    
    public static void main(String[] args) throws Exception {
        ClientTest test = new ClientTest();
        ClientTest2 test2 = new ClientTest2();
        
        ClientBootStrap clientBootStrap = new ClientBootStrap();
//        ZKServiceDiscoveryImpl discovery = new ZKServiceDiscoveryImpl();
        SimpleDiscoveryImpl discovery = new SimpleDiscoveryImpl();
        RPCClient rpcClient = new RPCClient();
        rpcClient.setServiceDiscovery(discovery);
        rpcClient.setClientBootStrap(clientBootStrap);
        
        ClientTest inject = clientBootStrap.inject(rpcClient, Collections.EMPTY_LIST, test);
        ClientTest2 inject2 = clientBootStrap.inject(rpcClient, Collections.EMPTY_LIST, test2);
        inject.test();
        inject2.test2();
        System.in.read();
        
    }
    
}

class ClientTest2{
    @RPCReference
    private TestService testService;
    
    public void test2() {
        testService.test("12333");
    }
}
