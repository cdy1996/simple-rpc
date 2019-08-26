package com.cdy.simplerpc.container;


import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Repeatable(RPCScans.class)
@Import({RPCScannerRegister.class})
public @interface RPCScan {

    String[] basePackages() default "";
}
