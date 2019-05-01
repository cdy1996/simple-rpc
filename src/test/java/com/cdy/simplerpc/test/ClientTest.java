package com.cdy.simplerpc.test;

import com.cdy.simplerpc.ClientBootStrap;
import com.cdy.simplerpc.annotation.RPCReference;
import com.cdy.simplerpc.balance.SimpleBalance;
import com.cdy.simplerpc.registry.IServiceDiscovery;
import com.cdy.simplerpc.registry.nacos.NacosConfig;
import com.cdy.simplerpc.registry.nacos.NacosDiscovery;
import com.cdy.simplerpc.registry.simple.SimpleDiscoveryImpl;
import com.cdy.simplerpc.remoting.jetty.httpClient.HttpClient;
import com.cdy.simplerpc.remoting.netty.RPCClient;
import org.junit.Test;

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
    
    
    @Test
    public void mutiTest() throws Exception {
        ClientTest test = new ClientTest();
        ClientTest2 test2 = new ClientTest2();
        
        ClientBootStrap clientBootStrap = new ClientBootStrap();
        SimpleDiscoveryImpl discovery = new SimpleDiscoveryImpl();
        discovery.setBalance(new SimpleBalance());
        
        RPCClient rpcClient = new RPCClient();
        HttpClient httpClient = new HttpClient();
        rpcClient.setServiceDiscovery(discovery);
        httpClient.setServiceDiscovery(discovery);
        rpcClient.setClientBootStrap(clientBootStrap);
        httpClient.setClientBootStrap(clientBootStrap);
        
        ClientTest inject1 = clientBootStrap.inject(httpClient, Collections.EMPTY_LIST, test);
        ClientTest2 inject2 = clientBootStrap.inject(rpcClient, Collections.EMPTY_LIST, test2);
        inject1.test();
        inject2.test2();
        System.in.read();
    }
    
    @Test
    public void nacosTest() throws Exception {
        ClientTest2 test2 = new ClientTest2();
        
        ClientBootStrap clientBootStrap = new ClientBootStrap();
        IServiceDiscovery discovery = new NacosDiscovery(new NacosConfig());
        discovery.setBalance(new SimpleBalance());
        
        RPCClient rpcClient = new RPCClient();
        rpcClient.setServiceDiscovery(discovery);
        rpcClient.setClientBootStrap(clientBootStrap);
        ClientTest2 inject2 = clientBootStrap.inject(rpcClient, Collections.EMPTY_LIST, test2);
        inject2.test2();
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
