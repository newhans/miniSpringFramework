package com.newhans.aop;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

//单个字符串的时候可以用也可以不用花括号，但是多个字符串的时候需要用花括号包起来表示
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface After {
    //需要织入的切点，由@Pointcut定义
    String value() default "";
}
