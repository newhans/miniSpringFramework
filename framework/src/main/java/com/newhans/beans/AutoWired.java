package com.newhans.beans;

import java.lang.annotation.*;


//这个注解，我们使用在bean的属性上，被他注解的属性，我们就要添加对应的依赖

@Documented
@Retention(RetentionPolicy.RUNTIME)
//target是类属性
@Target(ElementType.FIELD)
public @interface AutoWired {

}
