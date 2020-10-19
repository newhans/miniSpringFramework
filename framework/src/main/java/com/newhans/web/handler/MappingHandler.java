package com.newhans.web.handler;

import com.newhans.beans.BeanFactory;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

//每一个MappingHandler都是一个请求映射器
public class MappingHandler {
    ////我们在他的属性里保存一下他要处理的uri
    private String uri;
    //保存它对应的Controller方法
    //Method类是反射包里的类，要想调用这个Method我们还要知道对应的类 ---
    private Method method;
    //-->就是Controller类
    private Class<?> controller;
    //最后我们还要保存上，调用方法所用的参数
    private String[] args;

    //构造方法
    MappingHandler(String uri, Method method, Class<?> cls, String[] args){
        this.uri = uri;
        this.method = method;
        this.controller = cls;
        this.args = args;
    }
    //接下来我们添加一个管理器来管理这些handler

    public boolean handle(ServletRequest req, ServletResponse res) throws IllegalAccessException, InstantiationException, InvocationTargetException, IOException {
        //把ServletRequest转换为HttpServletRequest
        String requestUri = ((HttpServletRequest) req).getRequestURI();
        if (!uri.equals(requestUri)){
            return false;
        }
        //如果uir相等，我们就要调用Handler里的Method方法，首先我们准备下参数
        Object[] parameters = new Object[args.length];
        for (int i = 0; i < args.length; i++){
            //我们通过参数名，依次从ServletRequest里获得这些参数
            parameters[i] = req.getParameter(args[i]);
        }
        //实例化这个Controller
        Object ctl = BeanFactory.getBean(controller);
        //用Object存储结果
        Object response = method.invoke(ctl, parameters);
        //我们把方法返回的结果，放到ServletResponse里面去
        res.getWriter().println(response.toString());
        //处理成功后我们返回true
        return true;
    }

}
