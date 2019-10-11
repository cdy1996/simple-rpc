package com.cdy.simplerpc.cluster;

import com.cdy.simplerpc.config.ConfigConstants;
import com.cdy.simplerpc.config.PropertySources;
import com.cdy.simplerpc.exception.RPCException;
import com.cdy.simplerpc.netty.rpc.RPCContext;
import com.cdy.simplerpc.proxy.Invocation;
import com.cdy.simplerpc.registry.IServiceDiscovery;
import com.cdy.simplerpc.remoting.AbstractClient;
import com.cdy.simplerpc.remoting.Client;
import com.cdy.simplerpc.serialize.ISerialize;
import com.cdy.simplerpc.serialize.SerializeFactory;
import com.cdy.simplerpc.util.StringUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 集群的装饰层
 *
 * 一个服务的不同协议
 *
 * 失败切换 重试其他服务
 * 快速失败 只请求一次
 * 快速恢复 记录失败请求, 定时重发, 消息类的通知
 * 广播请求 想所有服务发出请求
 * <p>
 * Created by 陈东一
 * 2019/4/27 0027 16:16
 */
@Slf4j
public abstract class ClusterClient extends AbstractClient {

    private final Map<String, IServiceDiscovery> serviceDiscoveryMap;
    // key -> serviceName:protocol
    private final Map<String, Client> clientMap = new ConcurrentHashMap<>();

    public ClusterClient(PropertySources propertySources, Map<String, IServiceDiscovery> serviceDiscoveryMap) {
        super(propertySources);
        this.serviceDiscoveryMap = serviceDiscoveryMap;
    }
    
    public void init(String annotationKey, String serviceName) throws Exception {
        String directAddress = propertySources.resolveProperty(annotationKey + "." + ConfigConstants.url);
        if (StringUtil.isBlank(directAddress)) {
            String protocols = getProtocols(annotationKey);
            String discoveryType = getDiscoveryType(annotationKey);
            //多订阅时,只能选择一个注册中心
            List<String> list = serviceDiscoveryMap.get(discoveryType).listServer(serviceName, protocols.split(","));
            for (String address : list) {
                String[] split = address.split("-");
                String protocol = split[0];
                getClient(serviceName, protocol);
            }
        }
    }
    
    
    @Override
    public Object invoke(Invocation invocation) throws Exception {
        RPCContext context = RPCContext.current();
        Map<String, Object> map = context.getAttach();
        
        //服务发现
        String serviceName = invocation.getGeneric() ? invocation.getInterfaceClassName() : invocation.getInterfaceClass().getName();
        String directAddress = propertySources.resolveProperty(serviceName + "." + ConfigConstants.url);
        String address;
        if (StringUtil.isBlank(directAddress)) {
            String protocols = getProtocols(serviceName);
            String discoveryType = getDiscoveryType(serviceName);
            //多订阅时,只能选择一个注册中心
            IServiceDiscovery discovery = serviceDiscoveryMap.get(discoveryType);
            address = discovery.discovery(serviceName, protocols.split(","));
    
            if (StringUtil.isBlank(address)) {
                throw new RPCException("没有可以用的服务地址");
            }
            
           
            
            return doInvoke(protocols, serviceName, discovery, invocation);
        } else {
            //直连
            address = directAddress;
            String[] split = address.split("-");
            String protocol = split[0];
            Client client = getClient(serviceName, protocol);
            map.put("address", address.replace(protocol + "-", ""));
            return client.invoke(invocation);
        }
    }
    
    protected abstract Object doInvoke(String protocols, String serviceName, IServiceDiscovery discovery, Invocation invocation);
    
    private String getDiscoveryType(String annotationKey) {
        String discoveryType = propertySources.resolveProperty(annotationKey + ".discovery.type");
        if (StringUtil.isBlank(discoveryType)) {
            discoveryType = propertySources.resolveProperty("discovery.type");
        }
        return discoveryType;
    }
    
    private String getProtocols(String annotationKey) {
        String protocols = propertySources.resolveProperty(annotationKey + "." + ConfigConstants.protocols);
        if (StringUtil.isBlank(protocols)) { //注解没定义就取配置文件中的全局配置
            protocols = propertySources.resolveProperty("discovery." + ConfigConstants.protocols);
        }
        return protocols;
    }
    
    protected Client getClient(String serviceName, String protocol) throws Exception {
        Client client = clientMap.get(serviceName + ":" + protocol);
        if (client == null) {
            try {
                String replace = ClusterClient.class.getName().replace(ClusterClient.class.getSimpleName(), "");
                Class<?> clazz = Class.forName(replace + protocol.toLowerCase() + "." + protocol.toUpperCase() + "Client");
                client = (Client) clazz.getConstructor(PropertySources.class, ISerialize.class).newInstance(propertySources, SerializeFactory.createSerialize(propertySources));
            } catch (ClassNotFoundException e) {
                throw new RPCException("不支持的协议" + protocol);
            }
            clientMap.putIfAbsent(serviceName + ":" + protocol, client);
        }
        return client;
    }
    
    @Override
    public void close() {
        serviceDiscoveryMap.forEach((k, v) -> v.close());
        clientMap.forEach((k, v) -> v.close());
    }
}
