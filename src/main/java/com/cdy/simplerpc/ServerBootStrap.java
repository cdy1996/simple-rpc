package com.cdy.simplerpc;

import com.cdy.simplerpc.annotation.RPCService;
import com.cdy.simplerpc.config.*;
import com.cdy.simplerpc.filter.Filter;
import com.cdy.simplerpc.registry.IServiceRegistry;
import com.cdy.simplerpc.registry.RegistryFactory;
import com.cdy.simplerpc.remoting.Server;
import com.cdy.simplerpc.remoting.ServerFactory;
import com.cdy.simplerpc.util.StringUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 服务端启动类
 * Created by 陈东一
 * 2019/1/22 0022 22:11
 */
@Slf4j
public class ServerBootStrap {
    
    private final PropertySources propertySources;
    private final BootstrapPropertySource bootstrapPropertySource;
    private final List<Filter> filters;
    private final Set<String> types;
    private final Map<String, Server> servers;
    private final List<Object> targets = new ArrayList<>();
    
    
    private ServerBootStrap(PropertySources propertySources) {
        this.propertySources = propertySources;
        this.bootstrapPropertySource = new BootstrapPropertySource();
        this.filters = new ArrayList<>();
        this.types = new HashSet<>();
        this.servers = new HashMap<>();
        propertySources.addPropertySources(this.bootstrapPropertySource);
    }
    
    public static ServerBootStrap build(String path) {
        PropertySources propertySources = new PropertySources();
        if (StringUtil.isNotBlank(path)) {
            propertySources.addPropertySources(new LocalPropertySource(path));
        }
        return new ServerBootStrap(propertySources);
    }
    
    public static ServerBootStrap build(PropertySource propertySource) {
        PropertySources propertySources = new PropertySources();
        propertySources.addPropertySources(propertySource);
        return new ServerBootStrap(propertySources);
    }
    
    //通用filter
    public ServerBootStrap filters(Filter... filters) {
        this.filters.addAll(Arrays.asList(filters));
        return this;
    }
    
    public ServerBootStrap target(Object... t) {
        this.targets.addAll(Arrays.asList(t));
        return this;
    }
    
    public ServerBootStrap port(String port) {
        bootstrapPropertySource.getMap().put("registry.port", port);
        return this;
    }
    
    public ServerBootStrap protocols(String protocols) {
//        bootstrapPropertySource.getMap().put("registry.protocols", protocols);
        types.add(protocols);
        return this;
    }
    
    public ServerBootStrap ip(String ip) {
        bootstrapPropertySource.getMap().put("registry.ip", ip);
        return this;
    }
    
    
    public ServerBootStrap registry(String type, String serverAddr, String s) {
        bootstrapPropertySource.getMap().put(type + ".serverAddr", serverAddr);
        if (type.startsWith("nacos")) {
            bootstrapPropertySource.getMap().put(type + ".namespaceId", s);
        } else if (type.startsWith("zookeeper")) {
            bootstrapPropertySource.getMap().put(type + ".zkRegistryPath", s);
        }
        return this;
    }
    
    public ServerBootStrap start() throws Exception {
        if (!types.isEmpty()) {
            bootstrapPropertySource.getMap().put("registry.protocols", String.join(",", types));
        }
        
        //扫描注解属性
        for (Object target : targets) {
            RPCService annotation = target.getClass().getAnnotation(RPCService.class);
            Map<String, String> config = RPCService.ServiceAnnotationInfo.getConfig(StringUtil.getServiceName(target.getClass()), annotation);
            propertySources.addPropertySources(new AnnotationPropertySource(config));
            
            // 多注册中心
            String types = propertySources.resolveProperty("registry.types");
            List<IServiceRegistry> serviceRegistryList = Arrays.stream(types.split(",")).map(type -> RegistryFactory.createRegistry(propertySources, type)).collect(Collectors.toList());
            
            
            //多协议
            if (annotation.protocols().length == 0) {
                String protocols = propertySources.resolveProperty("registry.protocols");
                for (String protocol : protocols.split(",")) {
                    openAndRegistry(serviceRegistryList, protocol, target);
                }
            } else {
                for (String protocol : annotation.protocols()) {
                    openAndRegistry(serviceRegistryList, protocol, target);
                }
            }
            
        }
        return this;
        
    }
    
    private void openAndRegistry(List<IServiceRegistry> serviceRegistryList, String protocol, Object target) throws Exception {
        Server server = ServerFactory.createServer(serviceRegistryList, propertySources, protocol);
        String serviceName = StringUtil.getServiceName(target.getClass());
        server.bind(serviceName, target, Collections.emptyList());
        server.openServer();
        server.register(serviceName);
    }
    
    public void closeAll() {
        servers.forEach((k, v) -> {
                    if (v != null) {
                        try {
                            v.close();
                        } catch (IOException e) {
                            log.error(e.getMessage(), e);
                        }
                        
                    }
                }
        );
    }
    
    
}
