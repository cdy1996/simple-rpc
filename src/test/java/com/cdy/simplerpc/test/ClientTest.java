package com.cdy.simplerpc.test;

import com.cdy.simplerpc.annotation.RPCReference;
import com.cdy.simplerpc.config.AnnotationPropertySource;
import com.cdy.simplerpc.config.LocalPropertySource;
import com.cdy.simplerpc.config.PropertySources;
import com.cdy.simplerpc.filter.FilterChain;
import com.cdy.simplerpc.proxy.Invoker;
import com.cdy.simplerpc.proxy.ProxyFactory;
import com.cdy.simplerpc.proxy.RemoteInvoker;
import com.cdy.simplerpc.registry.DiscoveryFactory;
import com.cdy.simplerpc.registry.IServiceDiscovery;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * 客户端测试
 * Created by 陈东一
 * 2019/1/22 0022 22:28
 */
public class ClientTest {
    
    @Test
    public void nacosTest() throws Exception {
        ClientTest3 test3 = new ClientTest3();
        
        PropertySources propertySources = new PropertySources();
        propertySources.addPropertySources(new LocalPropertySource("D:\\workspace\\ideaworkspace\\blog_project\\simple-rpc\\src\\main\\resources\\simlpe-rpc.properties"));
    
    
        String types = propertySources.resolveProperty("discovery.types");
    
        HashMap<String, IServiceDiscovery> serviceRegistryMap = new HashMap<>();
        for (String type : types.split(",")) {
            IServiceDiscovery discovery = DiscoveryFactory.createDiscovery(propertySources, type);
            serviceRegistryMap.put(type, discovery);
        }
        
        ClientTest2 clientTest2 = new ClientTest2();
        Field[] fields = clientTest2.getClass().getDeclaredFields();
        Map<Field, Object> hashedMap = new HashMap<>();
        // 收集所有的需要注入的服务客户端
        for (Field field : fields) {
            RPCReference annotation = field.getAnnotation(RPCReference.class);
            if (annotation == null) {
                continue;
            }
            Class<?> referenceClass = field.getType();
            Map<String, String> config = RPCReference.ReferenceAnnotationInfo.getConfig(referenceClass.getSimpleName(), annotation);
            //将注解信息添加到配置中,方便被覆盖
            AnnotationPropertySource annotationPropertySource = new AnnotationPropertySource(config);
            propertySources.addPropertySources(annotationPropertySource);
            hashedMap.put(field, clientTest2);
        }
        
        
        //所有客户端进行初始化并注入
        hashedMap.forEach((field, target)->{
            // 远程的接口
            Invoker invoker = new RemoteInvoker(propertySources, serviceRegistryMap);
            Object proxy = ProxyFactory.createProxy(new FilterChain(invoker), field.getType());
            try {
                field.setAccessible(true);
                field.set(target, proxy);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        });
    
        clientTest2.test2();
        System.in.read();
    }
//
//    @Test
//    public void mutiTest() throws Exception {
//        ClientTest2 test2 = new ClientTest2();
//        ClientTest3 test3 = new ClientTest3();
//
//        PropertySources propertySources = new PropertySources();
//        propertySources.addPropertySources(new LocalPropertySource("D:\\workspace\\ideaworkspace\\blog_project\\simple-rpc\\src\\main\\resources\\simlpe-rpc.properties"));
//
//
//        ClientBootStrap clientBootStrap = new ClientBootStrap();
//        IServiceDiscovery discovery = new SimpleDiscoveryImpl();
//        discovery.setBalance(new SimpleBalance());
//        clientBootStrap.setPropertySources(propertySources);
//
//        clientBootStrap.setServiceDiscovery(discovery);
//        ClientTest2 inject2 = clientBootStrap.inject(Collections.EMPTY_LIST, test2);
//        ClientTest3 inject3 = clientBootStrap.inject(Collections.EMPTY_LIST, test3);
//
//        inject3.test();
//        inject2.test2();
//        System.in.read();
//    }
//
//    @Test
//    public void nacosTest() throws Exception {
//        ClientTest3 test3 = new ClientTest3();
//
//        PropertySources propertySources = new PropertySources();
//        propertySources.addPropertySources(new LocalPropertySource("D:\\workspace\\ideaworkspace\\blog_project\\simple-rpc\\src\\main\\resources\\simlpe-rpc.properties"));
//
//
//        ClientBootStrap clientBootStrap = new ClientBootStrap();
//        IServiceDiscovery discovery = new NacosDiscovery(propertySources);
//        discovery.setBalance(new SimpleBalance());
//        clientBootStrap.setPropertySources(propertySources);
//
//        clientBootStrap.setServiceDiscovery(discovery);
//        ClientTest3 inject3 = clientBootStrap.inject(Collections.EMPTY_LIST, test3);
//        inject3.test();
//        System.in.read();
//    }


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
