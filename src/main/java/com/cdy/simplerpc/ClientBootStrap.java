package com.cdy.simplerpc;

import com.cdy.simplerpc.balance.IBalance;
import com.cdy.simplerpc.config.RemotingConfig;
import com.cdy.simplerpc.annotation.RPCReference;
import com.cdy.simplerpc.filter.Filter;
import com.cdy.simplerpc.filter.FilterInvokerWrapper;
import com.cdy.simplerpc.proxy.Invoker;
import com.cdy.simplerpc.proxy.ProxyFactory;
import com.cdy.simplerpc.proxy.RemoteInvoker;
import com.cdy.simplerpc.registry.IServiceDiscovery;
import com.cdy.simplerpc.registry.simple.SimpleDiscoveryImpl;
import com.cdy.simplerpc.remoting.Client;
import com.cdy.simplerpc.remoting.netty.RPCClient;

import java.lang.reflect.Field;
import java.util.ArrayList;
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
    
    private IBalance iBalance;
    
    
    public static ClientBootStrap build() {
        return new ClientBootStrap();
    }
    
    public ClientBootStrap balance(IBalance iBalance){
        this.iBalance = iBalance;
        return this;
    }
    
    public ClientBootStrap discovery(IServiceDiscovery discovery) {
        discovery.setBalance(iBalance);
        this.discovery = discovery;
        return this;
    }
    
    public ClientBootStrap client(Client client) {
        client.setServiceDiscovery(discovery);
        this.client = client;
        return this;
    }
    
    public ClientBootStrap filters(Filter... filters) {
        for (Filter filter : filters) {
            this.filters.add(filter);
        }
        return this;
    }
    
    public Object inject(Object t, Function<Invoker, Invoker>... function) throws Exception {
        Field[] fields = t.getClass().getDeclaredFields();
        for (Field field : fields) {
            RPCReference annotation = field.getAnnotation(RPCReference.class);
            if (annotation != null) {
                Class<?> clazz = field.getType();
                synchronized (clazz.getName()) {
                    Invoker invoker = invokerMap.get(clazz.getName());
                    field.setAccessible(true);
                    if (invoker != null) {
                        field.set(t, invoker);
                        continue;
                    }
                    invoker = new RemoteInvoker(client);
                    for (Function<Invoker, Invoker> invokerInvokerFunction : function) {
                        invoker = invokerInvokerFunction.apply(invoker);
                    }
                    invokerMap.put(clazz.getName(), invoker);
                    Object proxy = proxyFactory.createProxy(new FilterInvokerWrapper(invoker, filters), clazz);
                    field.set(t, proxy);
                }
            }
        }
        return t;
    }
    
    
    public RemotingConfig getRemotingConfig() {
        return remotingConfig;
    }
    
    public void setRemotingConfig(RemotingConfig remotingConfig) {
        this.remotingConfig = remotingConfig;
    }
    
    
}
