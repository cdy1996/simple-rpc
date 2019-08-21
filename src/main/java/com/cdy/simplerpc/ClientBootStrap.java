package com.cdy.simplerpc;

import com.cdy.simplerpc.annotation.RPCReference;
import com.cdy.simplerpc.config.AnnotationPropertySource;
import com.cdy.simplerpc.config.BootstrapPropertySource;
import com.cdy.simplerpc.config.LocalPropertySource;
import com.cdy.simplerpc.config.PropertySources;
import com.cdy.simplerpc.filter.Filter;
import com.cdy.simplerpc.filter.FilterChain;
import com.cdy.simplerpc.proxy.Invoker;
import com.cdy.simplerpc.proxy.ProxyFactory;
import com.cdy.simplerpc.proxy.RemoteInvoker;
import com.cdy.simplerpc.registry.DiscoveryFactory;
import com.cdy.simplerpc.registry.IServiceDiscovery;
import com.cdy.simplerpc.util.StringUtil;

import java.lang.reflect.Field;
import java.util.*;

/**
 * 客户端启动类
 * 单例使用, 给都可实例注入
 * <p>
 * Created by 陈东一
 * 2019/1/22 0022 22:11
 */
public class ClientBootStrap {
    
    private final List<Filter> filters;
    private final PropertySources propertySources;
    private final BootstrapPropertySource bootstrapPropertySource;
    private final Map<String, IServiceDiscovery> serviceRegistryMap;
    private final Set<String> types;
    
    public ClientBootStrap(PropertySources propertySources) {
        this.filters = new ArrayList<>();
        this.propertySources = propertySources;
        this.bootstrapPropertySource = new BootstrapPropertySource();
        this.serviceRegistryMap = new HashMap<>();
        this.types = new HashSet<>();
        this.propertySources.addPropertySources(this.bootstrapPropertySource);
    }
    
    
    public ClientBootStrap filters(Filter... filters) {
        this.filters.addAll(Arrays.asList(filters));
        return this;
    }
    
    
    public static ClientBootStrap build(String path) {
        PropertySources propertySources = new PropertySources();
        if (StringUtil.isNotBlank(path)) {
            propertySources.addPropertySources(new LocalPropertySource(path));
        }
        return new ClientBootStrap(propertySources);
    }
    
    public static ClientBootStrap build(PropertySources propertySources) {
        return new ClientBootStrap(propertySources);
    }
    
    public ClientBootStrap discovery(String type, String serverAddr, String s) {
        bootstrapPropertySource.getMap().put(type + ".serverAddr", serverAddr);
        if (type.startsWith("nacos")) {
            bootstrapPropertySource.getMap().put(type + ".namespaceId", s);
        } else if (type.startsWith("zookeeper")) {
            bootstrapPropertySource.getMap().put(type + ".zkRegistryPath", s);
        }
        return this;
    }
    
    public ClientBootStrap type(String type) {
        bootstrapPropertySource.getMap().put("discovery.type", type);
        return this;
    }

    
    public ClientBootStrap start(){
        if (!types.isEmpty()) {
            bootstrapPropertySource.getMap().put("discovery.protocols", String.join(",", types));
        }
        
        
        String types = propertySources.resolveProperty("discovery.types");
        for (String type : types.split(",")) {
            IServiceDiscovery discovery = DiscoveryFactory.createDiscovery(propertySources, type);
            serviceRegistryMap.put(type, discovery);
        }
        return this;
    }
    
    public ClientBootStrap refer(Object o) {
        Field[] fields = o.getClass().getDeclaredFields();
        for (Field field : fields) {
            RPCReference annotation = field.getAnnotation(RPCReference.class);
            if (annotation == null) {
                continue;
            }
            Class<?> referenceClass = field.getType();
            Map<String, String> config = RPCReference.ReferenceAnnotationInfo.getConfig(referenceClass.getName(), annotation);
            //将注解信息添加到配置中,方便被覆盖
            AnnotationPropertySource annotationPropertySource = new AnnotationPropertySource(config);
            propertySources.addPropertySources(annotationPropertySource);
        }
        return this;
    }
    
    public ClientBootStrap inject(Object o){
        Field[] fields = o.getClass().getDeclaredFields();
        for (Field field : fields) {
            RPCReference annotation = field.getAnnotation(RPCReference.class);
            if (annotation == null) {
                continue;
            }
            Invoker invoker = new RemoteInvoker(propertySources, serviceRegistryMap);
            Object proxy = ProxyFactory.createProxy(new FilterChain(invoker), field.getType());
            try {
                field.setAccessible(true);
                field.set(o, proxy);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        
       return this;
    }

    
    public ClientBootStrap protocols(String protocols) {
        types.add(protocols);
        return this;
    }

}
