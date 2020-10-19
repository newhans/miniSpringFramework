package com.newhans;

import com.newhans.starter.MiniApplication;

public class Application {
    public static void main(String[] args) {
        System.out.println("hello");
        //调用的是starter里的MiniApplication
        //参数是这个类，args也是这个类的参数
        MiniApplication.run(Application.class, args);
    }
}
