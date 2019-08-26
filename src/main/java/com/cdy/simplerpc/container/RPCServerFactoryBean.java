package com.cdy.simplerpc.container;

import lombok.Getter;

public class RPCServerFactoryBean {

    @Getter
    private final Object target;

    public RPCServerFactoryBean(Object target) {
        this.target = target;
    }
}
