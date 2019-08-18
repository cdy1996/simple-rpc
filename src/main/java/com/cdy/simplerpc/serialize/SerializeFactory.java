package com.cdy.simplerpc.serialize;

import com.cdy.simplerpc.config.PropertySources;
import lombok.extern.slf4j.Slf4j;

/**
 * 服务创建工厂
 * <p>
 * Created by 陈东一
 * 2019/5/25 0025 19:51
 */
@Slf4j
public class SerializeFactory {
    
    public static ISerialize createSerialize(PropertySources propertySources) {
        
        String type = propertySources.resolveProperty("serialize.type");
        log.info("使用{} 作为序列化", type);
        // todo spi
        switch (type){
            case "jdk":
                return new JdkSerialize();
            case "hession":
                return new HessionSerialize();
            case "json":
                return new JsonSerialize();
            case "pb":
                return new ProtobufSerialize();
            default:
                log.warn("不支持的序列化协议:"+type);
                return new JdkSerialize();
        }
        
    }
}
