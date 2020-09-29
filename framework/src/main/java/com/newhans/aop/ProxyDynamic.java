package com.newhans.aop;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

//AOP代理类
//同时也是代理的handle
public class ProxyDynamic implements InvocationHandler {
    //aspect:方面

    private Object aspect;
    private Method before;
    private Method after;
    private Object target;
    private String targetMethod;

    /**
     * @param aspect 织面对象，也就是before和after方法去调用者
     * @param before 前置方法
     * @param after 后置方法
     * @param target 被代理的对象（目标对象）
     * @param targetMethod 被代理的方法（目标方法）
     * @return 代理对象
     */
    public Object createProxy(Object aspect, Method before, Method after, Object target, String targetMethod){
        this.aspect = aspect;
        this.before = before;
        this.after = after;
        this.target = target;
        this.targetMethod = targetMethod;
        return Proxy.newProxyInstance(target.getClass().getClassLoader(), target.getClass().getInterfaces(), this);
        //this:handle
    }


    /**
     * 被代理对象（proxy）的每一个方法（method）都会进入此方法进行处理
     * @param proxy 被代理对象
     * @param method 被代理对象的某个方法
     * @param args 方法参数
     * @return 代理对象
     * @throws Throwable 不处理任何异常
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        //我们只需要对targetMethod方法进行处理，其他方法直接返回原来的调用
        if (!method.getName().equals(targetMethod)){
            return method.invoke(this.target, args);
        }

        /*
        invoke方法
        作用：调用包装在当前Method对象中的方法。
        原型：Object invoke（Object obj,Object...args）
        参数解释：obj：实例化后的对象
        args：用于方法调用的参数
        返回：根据obj和args调用的方法的返回值
        抛出错误：IllegalAccessException
        原因：Method对象强制Java语言执行控制 或 无权访问obj对象
        IllegalArgumentException
        原因：方法是实例化方法，而指定需要调用的对象并不是实例化后的类或接口
         */
        Object result;
        if (before != null){
            //before()方法简单起见，方法没有参数
            before.invoke(this.aspect);
        }

        result  = method.invoke(target);

        if (after != null){
            after.invoke(this.aspect);
        }
        return result;
    }
}
