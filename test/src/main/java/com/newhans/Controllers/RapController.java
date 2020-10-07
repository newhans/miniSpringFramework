package com.newhans.Controllers;

import com.newhans.Service.Rap;
import com.newhans.beans.AutoWired;
import com.newhans.web.mvc.Controller;
import com.newhans.web.mvc.RequestMapping;

@Controller
public class RapController {
    @AutoWired
    private Rap rapper;

    @RequestMapping("/rap")
    public String rap(){
        rapper.rap();
        return "rapper...,view console";
    }
}
