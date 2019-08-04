package com.cdy.simplerpc.config;

import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.api.exception.NacosException;
import com.cdy.simplerpc.annotation.Order;
import com.cdy.simplerpc.util.StringUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;

/**
 * nacos远程配置
 *
 * Created by 陈东一
 * 2019/5/25 0025 16:09
 */
@Slf4j
@Order(100)
public class NacosPropertySource implements PropertySource {
    
    private final ConfigService configService;
    private final String serverAddr;
    private final String dataId;
    private final String group;
    
    private ConcurrentHashMap<String, String> map = new ConcurrentHashMap<>();
    
    public NacosPropertySource(String serverAddr, String dataId, String group) {
        this.serverAddr = serverAddr;
        this.dataId = dataId;
        this.group = group;
        Properties properties = new Properties();
        properties.put("serverAddr", serverAddr);
        try {
            this.configService = NacosFactory.createConfigService(properties);
            configService.addListener(dataId, group, new Listener() {
                @Override
                public void receiveConfigInfo(String configInfo) {
                    if (StringUtil.isBlank(configInfo)) {
                        return;
                    }
                    String[] split = configInfo.split("\n");
                    for (String property : split) {
                        String[] values = property.split(":");
                        map.put(values[0], values[1]);
                    }
                }
                @Override
                public Executor getExecutor() {
                    return null;
                }
            });
        } catch (NacosException e) {
            log.error("连接nacos配置中心失败");
            throw new RuntimeException(e);
        }
        
    }
    
    
    @Override
    public String resolveProperty(String key) {
        String object = map.get(key);
        return object;
        
    }

}
