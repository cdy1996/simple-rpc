package com.cdy.simplerpc.container;

import com.cdy.simplerpc.annotation.RPCReference;
import com.cdy.simplerpc.annotation.RPCService;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.context.annotation.ScannedGenericBeanDefinition;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.MultiValueMap;

import java.util.Map;
import java.util.Set;

public class RPCClassPathScanner  extends ClassPathBeanDefinitionScanner {
    public RPCClassPathScanner(BeanDefinitionRegistry registry) {
        super(registry);
    }

    public RPCClassPathScanner(BeanDefinitionRegistry registry, boolean useDefaultFilters) {
        super(registry, useDefaultFilters);
    }

    public RPCClassPathScanner(BeanDefinitionRegistry registry, boolean useDefaultFilters, Environment environment) {
        super(registry, useDefaultFilters, environment);
    }

    public RPCClassPathScanner(BeanDefinitionRegistry registry, boolean useDefaultFilters, Environment environment, ResourceLoader resourceLoader) {
        super(registry, useDefaultFilters, environment, resourceLoader);
    }

    @Override
    public Set<BeanDefinitionHolder> doScan(String... basePackages) {
        return super.doScan(basePackages);
    }


    @Override
    protected void registerBeanDefinition(BeanDefinitionHolder definitionHolder, BeanDefinitionRegistry registry) {
        processBeanDefinition(definitionHolder, registry);
        super.registerBeanDefinition(definitionHolder, registry);
    }

    private void processBeanDefinition(BeanDefinitionHolder definitionHolder, BeanDefinitionRegistry registry) {
        ScannedGenericBeanDefinition beanDefinition = (ScannedGenericBeanDefinition) definitionHolder.getBeanDefinition();

        Map<String, Object> serverBean = beanDefinition.getMetadata().getAnnotationAttributes(RPCService.class.getName());

        if (serverBean != null) {
            AbstractBeanDefinition beanDefinition1 = BeanDefinitionBuilder.genericBeanDefinition(RPCServerFactoryBean.class)
                    .addConstructorArgReference(definitionHolder.getBeanName())
                    .getBeanDefinition();

            registry.registerBeanDefinition("RPCServerFactoryBean$"+definitionHolder.getBeanName(), beanDefinition1);
        }

    }
}
