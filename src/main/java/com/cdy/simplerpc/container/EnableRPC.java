package com.cdy.simplerpc.container;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@RPCScan
@Import({RPCReferencePostProcesser.class,SpringRPCServer.class})
public @interface EnableRPC {
}
