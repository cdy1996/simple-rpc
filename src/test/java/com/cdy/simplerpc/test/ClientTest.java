package com.cdy.simplerpc.test;

import com.cdy.simplerpc.ClientBootStrap;
import com.cdy.simplerpc.annotation.RPCReference;
import com.cdy.simplerpc.balance.SimpleBalance;
import com.cdy.simplerpc.registry.simple.SimpleDiscoveryImpl;

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
        discovery.setBalance(new SimpleBalance());
        
    
        clientBootStrap.setServiceDiscovery(discovery);
        ClientTest inject1 = clientBootStrap.inject(Collections.EMPTY_LIST, test);
        ClientTest2 inject2 = clientBootStrap.inject(Collections.EMPTY_LIST, test2);
        inject1.test();
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
