package com.cdy.simplerpc.container;

import com.cdy.simplerpc.ClientBootStrap;
import com.cdy.simplerpc.config.SpringPropertySource;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class RPCReferencePostProcesser implements BeanPostProcessor, ApplicationContextAware {

    private final ClientBootStrap clientBootStrap;
    private ApplicationContext applicationContext;

    public RPCReferencePostProcesser() {
        this.clientBootStrap = ClientBootStrap
                .build(new SpringPropertySource(applicationContext.getEnvironment()))
                .start();
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        clientBootStrap.refer(bean);
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        clientBootStrap.inject(bean);
        return bean;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
