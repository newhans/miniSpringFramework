package com.newhans.aop;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface Pointcut {
    /**
     * 定义切点的名字，spring中这里还会用到execution()等表达式来解析
     * 简单起见，我们只定义一个字符串表示切点的位置
     * 如com.newhans.demo.rapper.rap
     */
    String value() default "";
}
