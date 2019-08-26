package com.cdy.simplerpc.container;

import com.cdy.simplerpc.annotation.RPCService;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;

public class RPCScannerRegister implements ImportBeanDefinitionRegistrar, ResourceLoaderAware {

    private ResourceLoader resourceLoader;

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        AnnotationAttributes annotationAttributes = AnnotationAttributes.fromMap(importingClassMetadata.getAnnotationAttributes(RPCScan.class.getName()));
        RPCClassPathScanner scanner = new RPCClassPathScanner(registry/*, annotationAttributes.getString("defaultConfiguration")*/);

        if (resourceLoader != null) {
            scanner.setResourceLoader(resourceLoader);
        }

        scanner.addIncludeFilter(new AnnotationTypeFilter(RPCService.class));
        scanner.doScan(annotationAttributes.getStringArray("basePackages"));

    }
}
