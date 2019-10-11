package com.cdy.simplerpc;

import com.cdy.simplerpc.annotation.RPCReference;
import com.cdy.simplerpc.config.*;
import com.cdy.simplerpc.filter.Filter;
import com.cdy.simplerpc.filter.FilterChain;
import com.cdy.simplerpc.proxy.*;
import com.cdy.simplerpc.registry.DiscoveryFactory;
import com.cdy.simplerpc.registry.IServiceDiscovery;
import com.cdy.simplerpc.util.StringUtil;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.*;

/**
 * 客户端启动类
 * 单例使用, 给都可实例注入
 * <p>
 * Created by 陈东一
 * 2019/1/22 0022 22:11
 */
@Slf4j
public class ClientBootStrap {
    
    private final List<Filter> filters;
    private final PropertySources propertySources;
    private final BootstrapPropertySource bootstrapPropertySource;
    private final Map<String, IServiceDiscovery> serviceDiscoveryMap;
    private final Set<String> types;
    private final Set<String> discoveryTypes;
    private final List<RemoteInvoker> remoteInvokers;

    public ClientBootStrap(PropertySources propertySources) {
        this.filters = new ArrayList<>();
        this.remoteInvokers = new ArrayList<>();
        this.propertySources = propertySources;
        this.bootstrapPropertySource = new BootstrapPropertySource();
        this.serviceDiscoveryMap = new HashMap<>();
        this.types = new HashSet<>();
        this.discoveryTypes = new HashSet<>();

        bootstrapPropertySource.getMap().put("discovery.lazy", "false");
        bootstrapPropertySource.getMap().put("balance", "simple");
        bootstrapPropertySource.getMap().put("discovery.custer", "failfast");
        this.propertySources.addPropertySources(this.bootstrapPropertySource);
    }
    
    
    public ClientBootStrap filters(Filter... filters) {
        this.filters.addAll(Arrays.asList(filters));
        return this;
    }

    public static ClientBootStrap build() {
        PropertySources propertySources = new PropertySources();
        return new ClientBootStrap(propertySources);
    }
    public static ClientBootStrap build(String path) {
        PropertySources propertySources = new PropertySources();
        if (StringUtil.isNotBlank(path)) {
            propertySources.addPropertySources(new LocalPropertySource(path));
        }
        return new ClientBootStrap(propertySources);
    }
    
    public static ClientBootStrap build(PropertySource propertySource) {
        PropertySources propertySources = new PropertySources();
        propertySources.addPropertySources(propertySource);
        return new ClientBootStrap(propertySources);
    }
    
    public ClientBootStrap discovery(String type, String serverAddr, String s) {
        discoveryTypes.add(type);
        if (type.startsWith("nacos")) {
            bootstrapPropertySource.getMap().put(type + ".serverAddr", serverAddr);
            bootstrapPropertySource.getMap().put(type + ".namespaceId", s);
        } else if (type.startsWith("zookeeper")) {
            bootstrapPropertySource.getMap().put(type + ".serverAddr", serverAddr);
            bootstrapPropertySource.getMap().put(type + ".zkRegistryPath", s);
        }
        return this;
    }

    /**
     * 运行时选择的注册中心
     *
     * @param type
     * @return
     */
    public ClientBootStrap type(String type) {
        bootstrapPropertySource.getMap().put("discovery.type", type);
        return this;
    }

    public ClientBootStrap protocols(String protocols) {
        types.add(protocols);
        return this;
    }
    
    public ClientBootStrap start(){
        if (!types.isEmpty()) {
            bootstrapPropertySource.getMap().put("discovery.protocols", String.join(",", types));
        }
        if (!discoveryTypes.isEmpty()) {
            bootstrapPropertySource.getMap().put("discovery.types", String.join(",", discoveryTypes));
        }
        
        
        String types = propertySources.resolveProperty("discovery.types");
        for (String type : types.split(",")) {
            IServiceDiscovery discovery = DiscoveryFactory.createDiscovery(propertySources, type);
            serviceDiscoveryMap.put(type, discovery);
        }


        Runtime.getRuntime().addShutdownHook(new Thread(this::close));
        return this;
    }

    public ClientBootStrap refer(Object o) {
        String defaultType = propertySources.resolveProperty("discovery.type");

        Field[] fields = o.getClass().getDeclaredFields();
        for (Field field : fields) {
            RPCReference annotation = field.getAnnotation(RPCReference.class);
            if (annotation == null) {
                continue;
            }
            Class<?> referenceClass = field.getType();
            String serviceName = StringUtil.getServiceName(referenceClass);
            Map<String, String> config = RPCReference.ReferenceAnnotationInfo.getConfig(serviceName, annotation);

            String type = config.get(serviceName + "." + ConfigConstants.type);
            type = StringUtil.isBlank(type) ? defaultType : type;

            if (serviceDiscoveryMap.get(type) ==null) {
                serviceDiscoveryMap.put(type, DiscoveryFactory.createDiscovery(propertySources, type));
            }
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
            RemoteInvoker invoker = new RemoteInvoker(propertySources, serviceDiscoveryMap, StringUtil.getServiceName(field.getType()));
            remoteInvokers.add(invoker);
            Object proxy = ProxyFactory.createProxy(new FilterChain(invoker), field.getType());
            try {
                field.setAccessible(true);
                field.set(o, proxy);
            } catch (IllegalAccessException e) {
                log.error(e.getMessage(), e);
            }
        }
        
       return this;
    }

    public GenericService generic(String className){
        Invoker invoker = new RemoteInvoker(propertySources, serviceDiscoveryMap, null);
        GenericService genericService = ProxyFactory.createProxy(GenericService.class,
                new GenericInvocationHandler(new FilterChain(invoker), className));
       return genericService;

    }

    public void close() {
        remoteInvokers.forEach(RemoteInvoker::close);
    }


}
