package com.cdy.simplerpc;

import com.cdy.simplerpc.annotation.Order;
import com.cdy.simplerpc.annotation.RPCReference;
import com.cdy.simplerpc.config.AnnotationPropertySource;
import com.cdy.simplerpc.config.PropertySources;
import com.cdy.simplerpc.filter.Filter;
import com.cdy.simplerpc.filter.FilterChain;
import com.cdy.simplerpc.proxy.Invoker;
import com.cdy.simplerpc.proxy.ProxyFactory;
import com.cdy.simplerpc.proxy.RemoteInvoker;
import com.cdy.simplerpc.registry.IServiceDiscovery;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * 客户端启动类
 * 单例使用, 给都可实例注入
 * <p>
 * Created by 陈东一
 * 2019/1/22 0022 22:11
 */
public class ClientBootStrap {
    
    
    private ProxyFactory proxyFactory = new ProxyFactory();
    
    private Map<String, Invoker> invokerMap = new ConcurrentHashMap<>();
    
    private List<Filter> filters = new ArrayList<>();
    
    private IServiceDiscovery serviceDiscovery;
    
    private PropertySources propertySources;
    
    public ClientBootStrap filters(Filter... filters) {
        this.filters.addAll(Arrays.asList(filters));
        return this;
    }
    
    public void setServiceDiscovery(IServiceDiscovery serviceDiscovery) {
        this.serviceDiscovery = serviceDiscovery;
    }
    
    public void setPropertySources(PropertySources propertySources) {
        this.propertySources = propertySources;
    }
    
    
    @SafeVarargs
    public final <T> T inject(List<Filter> filters, T target, Function<Invoker, Invoker>... function) throws Exception {
        Field[] fields = target.getClass().getDeclaredFields();
        for (Field field : fields) {
            RPCReference annotation = field.getAnnotation(RPCReference.class);
            if (annotation == null) {
                continue;
            }
            
            Order order = AnnotationPropertySource.class.getAnnotation(Order.class);
            AnnotationPropertySource annotationPropertySource = (AnnotationPropertySource) propertySources.getProperetySource(order.value());
            
            String key = annotation.value();
            
            Class<?> referenceClass = field.getType();
            Invoker invoker = invokerMap.get(referenceClass.getName());
            
            Map<String, String> config = RPCReference.ReferenceAnnotationInfo.getConfig(key, annotation);
            //将注解信息添加到配置中,方便被覆盖
            annotationPropertySource.addProperty(config);
            field.setAccessible(true);
            
            //已经存在相同类的代理,直接使用即可
            if (invoker != null) {
                //创建代理对象
                Object proxy = proxyFactory.createProxy(new FilterChain(invoker, filters), referenceClass, key);
                field.set(target, proxy);
                continue;
            }
            
            invoker = new RemoteInvoker(propertySources, serviceDiscovery);
            for (Function<Invoker, Invoker> invokerInvokerFunction : function) {
                invoker = invokerInvokerFunction.apply(invoker);
            }
            filters.addAll(this.filters);
            Object proxy = proxyFactory.createProxy(new FilterChain(invoker, filters), referenceClass, key);
            field.set(target, proxy);
            invokerMap.put(referenceClass.getName(), invoker);
        }
        return target;
    }
    
    
}
