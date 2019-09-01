package com.cdy.simplerpc.remoting;

import com.cdy.simplerpc.config.ConfigConstants;
import com.cdy.simplerpc.config.PropertySources;
import com.cdy.simplerpc.exception.RPCException;
import com.cdy.simplerpc.proxy.Invocation;
import com.cdy.simplerpc.registry.IServiceDiscovery;
import com.cdy.simplerpc.netty.rpc.RPCContext;
import com.cdy.simplerpc.serialize.ISerialize;
import com.cdy.simplerpc.serialize.SerializeFactory;
import com.cdy.simplerpc.util.StringUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 集群的装饰层
 * todo
 * 失败切换 重试其他服务
 * 快速失败 只请求一次
 * 快速恢复 记录失败请求, 定时重发, 消息类的通知
 * 广播请求 想所有服务发出请求
 * <p>
 * Created by 陈东一
 * 2019/4/27 0027 16:16
 */
@Slf4j
public class ClusterClient extends AbstractClient {
    
    private final Map<String, IServiceDiscovery> servceDiscoveryMap;
    private final Map<String, Client> clientMap = new ConcurrentHashMap<>();
    
    public ClusterClient(PropertySources propertySources, Map<String, IServiceDiscovery> servceDiscoveryMap) {
        super(propertySources);
        this.servceDiscoveryMap = servceDiscoveryMap;
    }
    
    @Override
    public Object invoke(Invocation invocation) throws Exception {
        RPCContext context = RPCContext.current();
        Map<String, Object> map = context.getAttach();
        
        // 对应注解的类名作为key,可以从属性集中获取
        String annotationKey = (String) map.get(RPCContext.annotationKey);
        String directAddress = propertySources.resolveProperty(annotationKey + "." + ConfigConstants.url);
        
        //服务发现
        String serviceName = invocation.getGeneric()?invocation.getInterfaceClassName():invocation.getInterfaceClass().getName();
        String address = null;
        while (StringUtil.isBlank(address)) {
            if (StringUtil.isBlank(directAddress)) {
                String protocols = propertySources.resolveProperty(annotationKey + "." + ConfigConstants.protocols);
                if (StringUtil.isBlank(protocols)) { //注解没定义就取配置文件中的全局配置
                    protocols = propertySources.resolveProperty("discovery."+ConfigConstants.protocols);
                }
                String discoveryType = propertySources.resolveProperty(annotationKey + ".discovery.type");
                if (StringUtil.isBlank(discoveryType)) {
                    discoveryType = propertySources.resolveProperty("discovery.type");
                }
                //多订阅时,只能选择一个注册中心
                address = servceDiscoveryMap.get(discoveryType).discovery(serviceName, protocols.split(","));
            } else {
                //直连
                address = directAddress;
            }
            if (StringUtil.isBlank(address)) {
                log.warn("没有可以用的服务地址");
            }
        }
        
        String[] split = address.split("-");
        String protocol = split[0];
        Client client = clientMap.get(serviceName +":"+ protocol);
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
        
        
        map.put("address", address.replace(protocol + "-", ""));
        return client.invoke(invocation);
    }
    
    @Override
    public void close() {
    
    }
}
