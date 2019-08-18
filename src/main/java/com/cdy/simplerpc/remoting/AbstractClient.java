package com.cdy.simplerpc.remoting;

import com.cdy.simplerpc.config.PropertySources;
import com.cdy.simplerpc.serialize.ISerialize;
import lombok.Getter;
import lombok.Setter;

/**
 * 服务端
 * Created by 陈东一
 * 2018/9/1 21:41
 */
public abstract class AbstractClient implements Client {
    
    protected final PropertySources propertySources;
    @Getter @Setter
    protected final ISerialize serialize;
    
    public AbstractClient(PropertySources propertySources) {
        this.propertySources = propertySources;
        this.serialize = null;
    }
    
    public AbstractClient(PropertySources propertySources, ISerialize serialize) {
        this.propertySources = propertySources;
        this.serialize = serialize;
    }
}
