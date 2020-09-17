package com.newhans.beans;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
//注解在类上，所以target选type
@Target(ElementType.TYPE)

public @interface Bean {
}
