package com.newhans.web.servlet;

import com.newhans.web.handler.HandlerManager;
import com.newhans.web.handler.MappingHandler;

import javax.servlet.*;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

//要实现servlet接口，并实现接口方法
public class DispatcherServlet implements Servlet {

    @Override
    public void init(ServletConfig config) throws ServletException {

    }

    @Override
    public ServletConfig getServletConfig() {
        return null;
    }

    @Override
    public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
        //我们从ServletRequest内读取数据，写入到ServletResponse内
        //我们在DispatcherServlet里的service里使用这些MappingHandler
        //当一个请求过来时，我们依次判断这些handler能不能处理这个请求
        //如果能处理的话，就响应结果
        for (MappingHandler mappingHandler : HandlerManager.mappingHandlerList){
            try {
                //handle方法放回boolean类型
                if (mappingHandler.handle(req, res)){
                    return;
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public String getServletInfo() {
        return null;
    }

    @Override
    public void destroy() {

    }
}
