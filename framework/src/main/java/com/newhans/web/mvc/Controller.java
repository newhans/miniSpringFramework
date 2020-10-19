package com.newhans.web.mvc;

import java.lang.annotation.*;

//元注解
@Documented
//需要保留到运行期
//retention这个单词的意思就是”保留“
@Retention(RetentionPolicy.RUNTIME)
//作用目标:类
@Target(ElementType.TYPE)
public @interface Controller {
}
