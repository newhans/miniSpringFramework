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
        //框架的入口类：传参为应用的入口类，args[]是应用入口类启动时的参数数组
        System.out.println("hello mini-spring");
        TomcatServer tomcatServer = new TomcatServer(args);
        try {
            tomcatServer.startServer();
            List<Class<?>> classList = null;
            try {
                classList = ClassScanner.scanClasses(cls.getPackage().getName());
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
            HandlerManager.resolveMappingHandler(classList);
            classList.forEach(it -> System.out.println(it.getName()));
        } catch (LifecycleException e) {
            e.printStackTrace();
        }
    }
}
