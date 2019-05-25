package com.cdy.simplerpc.remoting;

import com.cdy.simplerpc.config.PropertySources;

/**
 * 服务端
 * Created by 陈东一
 * 2018/9/1 21:41
 */
public abstract class AbstractClient implements Client {
    
    protected PropertySources propertySources;
    
    public AbstractClient(PropertySources propertySources) {
        this.propertySources = propertySources;
    }

}
