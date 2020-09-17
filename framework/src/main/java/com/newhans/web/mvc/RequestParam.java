package com.newhans.web.mvc;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
//用在Controller方法的参数上
@Target(ElementType.PARAMETER)
public @interface RequestParam {
    //表示query的key
    String value();
}
