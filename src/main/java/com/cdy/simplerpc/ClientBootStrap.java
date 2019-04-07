package com.cdy.simplerpc;

import com.cdy.simplerpc.annotation.RPCReference;
import com.cdy.simplerpc.annotation.ReferenceMetaInfo;
import com.cdy.simplerpc.config.RemotingConfig;
import com.cdy.simplerpc.filter.Filter;
import com.cdy.simplerpc.filter.FilterChain;
import com.cdy.simplerpc.proxy.Invoker;
import com.cdy.simplerpc.proxy.ProxyFactory;
import com.cdy.simplerpc.proxy.RemoteInvoker;
import com.cdy.simplerpc.registry.IServiceDiscovery;
import com.cdy.simplerpc.registry.simple.SimpleDiscoveryImpl;
import com.cdy.simplerpc.remoting.Client;
import com.cdy.simplerpc.remoting.netty.RPCClient;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * 客户端启动类
 * <p>
 * Created by 陈东一
 * 2019/1/22 0022 22:11
 */
public class ClientBootStrap {
    
    private IServiceDiscovery discovery = new SimpleDiscoveryImpl();
    
    private Client client = new RPCClient();
    
    private ProxyFactory proxyFactory = new ProxyFactory();
    
    private Map<String, Invoker> invokerMap = new ConcurrentHashMap<>();
    
    private List<Filter> filters = new ArrayList<>();
    
    private RemotingConfig remotingConfig = new RemotingConfig();
    
    
    public void setDiscovery(IServiceDiscovery discovery) {
        this.discovery = discovery;
    }
    
    public void setClient(Client client) {
        client.setServiceDiscovery(this.discovery);
        this.client = client;
    }
    
    public ClientBootStrap filters(Filter... filters) {
        this.filters.addAll(Arrays.asList(filters));
        return this;
    }
    
    @SafeVarargs
    public final <T> T inject(T t, Function<Invoker, Invoker>... function) throws Exception {
        Field[] fields = t.getClass().getDeclaredFields();
        for (Field field : fields) {
            RPCReference annotation = field.getAnnotation(RPCReference.class);
            if (annotation != null) {
                Class<?> clazz = field.getType();
                synchronized (clazz.getName()) {
                    Invoker invoker = invokerMap.get(t.getClass().getName()+"#"+clazz.getName());
                    ReferenceMetaInfo data = new ReferenceMetaInfo(annotation);
                    field.setAccessible(true);
                    //相同的invoker但是可以配置不同的策略
                    if (invoker != null) {
                        invoker.addMetaInfo(t.getClass().getName() + "#" + clazz.getName(), data);
                        field.set(t, invoker);
                        continue;
                    }
                    invoker = new RemoteInvoker(client);
                    for (Function<Invoker, Invoker> invokerInvokerFunction : function) {
                        invoker = invokerInvokerFunction.apply(invoker);
                    }
                    invokerMap.put(clazz.getName(), invoker);
                    Object proxy = proxyFactory.createProxy(new FilterChain(invoker, filters), clazz);
                    field.set(t, proxy);
                }
            }
        }
        return t;
    }
    
    public ReferenceMetaInfo getReferenceMetaInfo(String serviceName) {
        return invokerMap.get(serviceName).getMetaInfo(serviceName);
    }
    
    public RemotingConfig getRemotingConfig() {
        return remotingConfig;
    }
    
    public void setRemotingConfig(RemotingConfig remotingConfig) {
        this.remotingConfig = remotingConfig;
    }
    
    
}
