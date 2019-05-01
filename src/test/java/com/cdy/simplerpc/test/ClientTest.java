package com.cdy.simplerpc.test;

import com.cdy.simplerpc.ClientBootStrap;
import com.cdy.simplerpc.annotation.RPCReference;
import com.cdy.simplerpc.balance.SimpleBalance;
import com.cdy.simplerpc.registry.IServiceDiscovery;
import com.cdy.simplerpc.registry.nacos.NacosConfig;
import com.cdy.simplerpc.registry.nacos.NacosDiscovery;
import com.cdy.simplerpc.registry.simple.SimpleDiscoveryImpl;
import org.junit.Test;

import java.util.Collections;

/**
 * 客户端测试
 * Created by 陈东一
 * 2019/1/22 0022 22:28
 */
public class ClientTest {
    
    @Test
    public void mutiTest() throws Exception {
        ClientTest2 test2 = new ClientTest2();
        ClientTest3 test3 = new ClientTest3();
        
        ClientBootStrap clientBootStrap = new ClientBootStrap();
        IServiceDiscovery discovery = new SimpleDiscoveryImpl();
        discovery.setBalance(new SimpleBalance());
        
        
        clientBootStrap.setServiceDiscovery(discovery);
        ClientTest2 inject2 = clientBootStrap.inject(Collections.EMPTY_LIST, test2);
        ClientTest3 inject3 = clientBootStrap.inject(Collections.EMPTY_LIST, test3);
        
        inject3.test();
        inject2.test2();
        System.in.read();
    }
    
    @Test
    public void nacosTest() throws Exception {
        ClientTest3 test3 = new ClientTest3();
        
        ClientBootStrap clientBootStrap = new ClientBootStrap();
        IServiceDiscovery discovery = new NacosDiscovery(new NacosConfig());
        discovery.setBalance(new SimpleBalance());
        
        clientBootStrap.setServiceDiscovery(discovery);
        ClientTest3 inject3 = clientBootStrap.inject(Collections.EMPTY_LIST, test3);
        inject3.test();
        System.in.read();
    }
    
    
}

class ClientTest2 {
    @RPCReference
    private TestService testService;
    
    public void test2() {
        testService.test("12333");
    }
}

class ClientTest3 {
    @RPCReference
    private TestService testService;
    
    public void test() {
        testService.test("12333");
    }
}
