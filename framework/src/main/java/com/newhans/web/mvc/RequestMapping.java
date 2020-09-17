package com.newhans.web.mvc;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
//目标是Controller里的方法
@Target(ElementType.METHOD)
public @interface RequestMapping {
    //用来保存需要映射的URL
    String value();
}
