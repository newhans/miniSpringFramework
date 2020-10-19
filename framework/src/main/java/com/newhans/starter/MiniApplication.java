package com.newhans.starter;

import com.newhans.beans.BeanFactory;
import com.newhans.core.ClassScanner;
import com.newhans.web.handler.HandlerManager;
import com.newhans.web.server.TomcatServer;
import org.apache.catalina.LifecycleException;

import java.io.IOException;
import java.util.List;

public class MiniApplication {
    public static void run(Class<?> cls, String[] args)  {
        //框架的入口类：传参为应用的入口类，通过入口类，我们就可以定位到项目的根目录，也就能获得到应用入口类的信息
        //args[]是应用入口类启动时的参数数组
        System.out.println("hello mini-spring");
        TomcatServer tomcatServer = new TomcatServer(args);
        try {
            tomcatServer.startServer();
            List<Class<?>> classList = null;
            try {
                //这个包名，就使用项目入口类的包名
                classList = ClassScanner.scanClasses(cls.getPackage().getName());
                //System.out.println(cls.getPackage().getName());
                //cls.getPackage().getName() ----> com.newhans
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

            try {
                BeanFactory.initBean(classList);
            } catch (Exception e) {
                e.printStackTrace();
            }
            //在框架入口类，带哦用HandlerManager初始化所有的MappingHandler
            HandlerManager.resolveMappingHandler(classList);
            //打印classList中的类
            classList.forEach(it -> System.out.println(it.getName()));
        } catch (LifecycleException e) {
            e.printStackTrace();
        }
    }
}
