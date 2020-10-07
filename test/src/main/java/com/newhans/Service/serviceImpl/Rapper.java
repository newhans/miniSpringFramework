package com.newhans.Service.serviceImpl;

import com.newhans.Service.Rap;
import com.newhans.beans.Component;

@Component
public class Rapper implements Rap {
    @Override
    public void rap(){
        System.out.println("I am rapping, please pick me up!");
    }
}
