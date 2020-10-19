package com.newhans.web.handler;

import com.newhans.web.mvc.Controller;
import com.newhans.web.mvc.RequestMapping;
import com.newhans.web.mvc.RequestParam;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

//接下来我们添加一个管理器来管理这些handler
public class HandlerManager {
    //我们给这类添加一个静态属性，用来保存多个MappingHandler
    public static List<MappingHandler> mappingHandlerList = new ArrayList<>();
    //这个方法把Controller类挑选出来，然后把类里面的MappingHandler方法初始化成MappingHandler
    public static void resolveMappingHandler(List<Class<?>> classList){
        //首先，我们需要遍历这些类，把带有Controller注解的类挑选出来
        for (Class<?> cls : classList){
            //这个方法可以判断注解是否存在
            if (cls.isAnnotationPresent(Controller.class)){
                //如果Controller注解存在的话，我们就解析这个类
                parseHandlerFromController(cls);
            }
        }
    }

    private static void parseHandlerFromController(Class<?> cls){
        //我们首先通过反射获得到这个类的所有方法
        Method[] methods = cls.getDeclaredMethods();
        //我们遍历这些方法，找到被RequestMapping注解的方法
        for (Method method : methods){
            if (!method.isAnnotationPresent(RequestMapping.class)){
                continue;
            }
            //获取所有构成MappingHandler的属性
            //uri可以从注解的属性里获取到
            String uri = method.getDeclaredAnnotation(RequestMapping.class).value();
            List<String> paramNameList = new ArrayList<>();
            //我们可以遍历方法所有的参数，来依次判断，找到被@RequestParam注解的参数
            for (Parameter parameter : method.getParameters()){
                if (parameter.isAnnotationPresent(RequestParam.class)){
                    paramNameList.add(parameter.getDeclaredAnnotation(RequestParam.class).value());
                }
            }
            String[] params = paramNameList.toArray(new String[paramNameList.size()]);

            MappingHandler mappingHandler = new MappingHandler(uri, method, cls, params);
            //最后把构造好的MappingHandler放到handler管理器的静态属性里
            HandlerManager.mappingHandlerList.add(mappingHandler);
            //然后我们在dispatcherServlet里使用这些handler
        }
    }

}
