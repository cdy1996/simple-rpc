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
    // key -> protocol-port
    private final Map<String, Server> servers;
    private final List<Object> targets = new ArrayList<>();
    private final List<String> registryType = new ArrayList<>();


    private ServerBootStrap(PropertySources propertySources) {
        this.propertySources = propertySources;
        this.bootstrapPropertySource = new BootstrapPropertySource();
        this.filters = new ArrayList<>();
        this.types = new HashSet<>();
        this.servers = new HashMap<>();

        bootstrapPropertySource.getMap().put("serialize.type", "hession");

        propertySources.addPropertySources(this.bootstrapPropertySource);
    }

    public static ServerBootStrap build() {
        PropertySources propertySources = new PropertySources();
        return new ServerBootStrap(propertySources);
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

    public ServerBootStrap protocol(String protocol) {
        bootstrapPropertySource.getMap().put("registry.protocol", protocol);
        return this;
    }

    public ServerBootStrap protocols(String protocol, String port) {
//        bootstrapPropertySource.getMap().put("registry.protocols", protocols);
        types.add(protocol + "-" + port);
        return this;
    }

    public ServerBootStrap ip(String ip) {
        bootstrapPropertySource.getMap().put("registry.ip", ip);
        return this;
    }


    /**
     * simple, null, null
     * nacos-1, 127.0.0.1:8848 , namespaceId
     * nacos-2, 127.0.0.1:8848 , namespaceId
     * zookeeper-1, 127.0.0.1:3306, /s
     */
    public ServerBootStrap registry(String type, String serverAddr, String info) {
        registryType.add(type);
        if (type.startsWith("nacos")) {
            bootstrapPropertySource.getMap().put(type + ".serverAddr", serverAddr);
            bootstrapPropertySource.getMap().put(type + ".namespaceId", info);
        } else if (type.startsWith("zookeeper")) {
            bootstrapPropertySource.getMap().put(type + ".serverAddr", serverAddr);
            bootstrapPropertySource.getMap().put(type + ".zkRegistryPath", info);
        }
        return this;
    }


    public ServerBootStrap start() throws Exception {
        if (!types.isEmpty()) {
            bootstrapPropertySource.getMap().put("registry.protocols", String.join(",", types));
        }
        if (!registryType.isEmpty()) {
            bootstrapPropertySource.getMap().put("registry.types", String.join(",", registryType));
        }

        // 多注册中心
        String types = propertySources.resolveProperty("registry.types");
        List<IServiceRegistry> serviceRegistryList = Arrays.stream(types.split(",")).map(type -> RegistryFactory.createRegistry(propertySources, type)).collect(Collectors.toList());


        String defaultProtocol = propertySources.resolveProperty("registry.protocol");
        String defaultPort = propertySources.resolveProperty("registry.port");
        String protocols = propertySources.resolveProperty("registry.protocols");

        //扫描注解属性
        for (Object target : targets) {
            RPCService annotation = target.getClass().getAnnotation(RPCService.class);
            Map<String, String> config = RPCService.ServiceAnnotationInfo.getConfig(StringUtil.getServiceName(target.getClass()), annotation);
            propertySources.addPropertySources(new AnnotationPropertySource(config));


            List<String> registry = new ArrayList<>();

            if (annotation.protocols().length > 0) {
                registry.addAll(Arrays.asList(annotation.protocols()));
            } else if (!StringUtil.isBlank(protocols)) {
                registry.addAll(Arrays.asList(protocols.split(",")));

            } else {
                //没有默认的多协议 注解上也没有多协议
                String protocol = annotation.protocol();
                String port = annotation.port();
                registry.add((StringUtil.isBlank(protocol) ? defaultProtocol : protocol) + "-"
                        + (StringUtil.isBlank(port) ? defaultPort : port));
            }

            for (String protocolAndPort : registry) {
                Server server = servers.get(protocolAndPort);
                String serviceName = StringUtil.getServiceName(target.getClass());
                if (server == null) {
                    servers.put(protocolAndPort, getServer(serviceRegistryList, protocolAndPort, serviceName));
                }
                server = servers.get(protocolAndPort);
                server.bind(serviceName, target, Collections.emptyList());
//                server.openServer();
            }


        }
        registryAll();
        return this;
    }


    private Server getServer(List<IServiceRegistry> serviceRegistryList, String protocol, String serviceName) {
        return ServerFactory.createServer(serviceRegistryList, propertySources, protocol, serviceName);
    }

    public void registryAll() {
        servers.forEach((k, v) -> {
                    if (v != null) {
                        try {
                            v.register(v.getServerMetaInfo().getServiceName());
                        } catch (Exception e) {
                            log.error(e.getMessage(), e);
                        }

                    }
                }
        );
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
