package com.newhans.aspect;

import com.newhans.aop.After;
import com.newhans.aop.Aspect;
import com.newhans.aop.Before;
import com.newhans.aop.Pointcut;
import com.newhans.beans.Component;

@Aspect
@Component
public class RapAspect {
    //定义切点，spring的实现中，此注解可以使用表达式execution（）通配符匹配切点
    //简单起见，我们实现明确到方法的切点
    @Pointcut("com.newhans.Service.serviceImpl.Rapper.rap()")
    public void rapPoint(){

    }

    @Before("rapPoint()")
    public void singAndDance(){
        System.out.println("before rap");
    }

    @After("rapPoint()")
    public void basketball(){
        System.out.println("after rap");
    }

}
