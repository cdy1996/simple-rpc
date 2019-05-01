package com.cdy.simplerpc;

import com.cdy.simplerpc.annotation.RPCReference;
import com.cdy.simplerpc.annotation.ReferenceMetaInfo;
import com.cdy.simplerpc.config.RemotingConfig;
import com.cdy.simplerpc.filter.Filter;
import com.cdy.simplerpc.filter.FilterChain;
import com.cdy.simplerpc.proxy.Invoker;
import com.cdy.simplerpc.proxy.ProxyFactory;
import com.cdy.simplerpc.proxy.RemoteInvoker;
import com.cdy.simplerpc.remoting.Client;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * 客户端启动类
 * 单例使用, 给都可实例注入
 * <p>
 * Created by 陈东一
 * 2019/1/22 0022 22:11
 */
public class ClientBootStrap {
    
    
    private ProxyFactory proxyFactory = new ProxyFactory();
    
    private Map<String, Invoker> invokerMap = new ConcurrentHashMap<>();
    
    private List<Filter> filters = new ArrayList<>();
    
    private RemotingConfig remotingConfig = new RemotingConfig();
    
    private Set<Client> clients = new HashSet<>();
    
    public ClientBootStrap filters(Filter... filters) {
        this.filters.addAll(Arrays.asList(filters));
        return this;
    }
    
    @SafeVarargs
    public final <T> T inject(Client client, List<Filter> filters, T target, Function<Invoker, Invoker>... function) throws Exception {
        clients.add(client);
        Field[] fields = target.getClass().getDeclaredFields();
        for (Field field : fields) {
            RPCReference annotation = field.getAnnotation(RPCReference.class);
            if (annotation != null) {
                Class<?> fieldType = field.getType();
                String fieldName = field.getName();
                synchronized (fieldType.getName()) {
                    String key = target.getClass().getName() + "#" + fieldName;
                    Invoker invoker = invokerMap.get(key);
                    ReferenceMetaInfo data = ReferenceMetaInfo.generateMetaInfo(annotation);
                    field.setAccessible(true);
                    //相同的invoker但是可以配置不同的策略
                    if (invoker != null) {
                        invoker.addMetaInfo(key, data);
                        Object proxy = proxyFactory.createProxy(new FilterChain(invoker, filters), fieldType, key);
                        field.set(target, proxy);
                        continue;
                    }
                    invoker = new RemoteInvoker(client);
                    invoker.addMetaInfo(key, data);
                    for (Function<Invoker, Invoker> invokerInvokerFunction : function) {
                        invoker = invokerInvokerFunction.apply(invoker);
                    }
                    invokerMap.put(key, invoker);
                    filters.addAll(this.filters);
                    Object proxy = proxyFactory.createProxy(new FilterChain(invoker, filters), fieldType, key);
                    field.set(target, proxy);
                }
            }
        }
        return target;
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
