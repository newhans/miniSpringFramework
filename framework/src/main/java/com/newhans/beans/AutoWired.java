package com.newhans.beans;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
//target是类属性
@Target(ElementType.FIELD)
public @interface AutoWired {

}
