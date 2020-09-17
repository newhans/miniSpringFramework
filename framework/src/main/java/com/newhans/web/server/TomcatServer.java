package com.newhans.web.server;

import com.newhans.web.servlet.DispatcherServlet;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.startup.Tomcat;

public class TomcatServer {
    private Tomcat tomcat;
    private String[] args;

    //构造方法
    public TomcatServer(String[] args){
        this.args = args;
    }

    //启动Tomcat的主方法
    public void startServer() throws LifecycleException {
        tomcat = new Tomcat();
        tomcat.setPort(7777);
        tomcat.start();

        /**
         * Tomcat容器
         * 1.Engine：最顶级的容器，tomcat的总控中心
         * 2.Host：对应一个虚拟主机，用于管理主机的信息以及子容器
         * 3.Context:是最接近servlet的容器，可以设置资源属性和管理组件
         * 4.Wrapper：是对一个servlet的封装，负责servlet的加载，初始化，执行和销毁
         */

        Context context = new StandardContext();
        context.setPath("");
        context.addLifecycleListener(new Tomcat.FixContextListener());

        DispatcherServlet servlet = new DispatcherServlet();
        Tomcat.addServlet(context, "dispatcherServlet", servlet).setAsyncSupported(true);
        //添加servlet到URL的映射，访问这个URL时，Tomcat会调用这个servlet

        //根URL
        context.addServletMappingDecoded("/", "dispatcherServlet");
        //context容器需要依附在一个Host容器内，我们把它注册到一个默认的Host容器
        tomcat.getHost().addChild(context);

        //用非守护线程保持tomcat的存活
        Thread awaitThread = new Thread("tomcat_await_thread"){
          @Override
          public void run(){
            TomcatServer.this.tomcat.getServer().await();
          }
        };
        //把这个线程设置为非守护线程
        awaitThread.setDaemon(false);
        awaitThread.start();
    }
}
