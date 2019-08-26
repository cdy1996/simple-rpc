package com.cdy.simplerpc.container;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface RPCScans {
    RPCScan[] value();
}
