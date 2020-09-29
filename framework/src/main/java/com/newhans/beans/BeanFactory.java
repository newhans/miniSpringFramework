package com.newhans.beans;

import com.newhans.aop.Pointcut;
import com.newhans.web.mvc.Controller;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BeanFactory {
    private static Map<Class<?>, Object> classToBean = new ConcurrentHashMap<>();

    public static Object getBean(Class<?> cls){
        return classToBean.get(cls);
    }

    public static void initBean(List<Class<?>> classList) throws Exception {
        List<Class<?>> toCreate = new ArrayList<>(classList);
        while (toCreate.size() != 0){
            int remainSize = toCreate.size();
            for (int i = 0; i < toCreate.size(); i++){
                if (finishCreate(toCreate.get(i))){
                    toCreate.remove(i);
                }
            }
            if (toCreate.size() == remainSize){
                throw new Exception("cycle dependency");
            }
        }
    }

    private static boolean finishCreate(Class<?> cls) throws IllegalAccessException, InstantiationException {
        //只处理带@Bean注解或者@Controller注解的类
        if (!cls.isAnnotationPresent(Bean.class) && !cls.isAnnotationPresent(Controller.class)){
            return true;
        }

        Object bean = cls.newInstance();
        //遍历所有字段，如果又AutoWired注解，则要依赖注入
        for (Field field : cls.getDeclaredFields()){
            if (field.isAnnotationPresent(AutoWired.class)){
                //classToBean这个concurrentHashMap里key是cls,value是bean
                Class<?> fieldType = field.getType();
                Object reliantBean = BeanFactory.getBean(fieldType);
                if (reliantBean == null){
                    return false;
                }
                /*
                 *只能对可以访问的字段使用get和set方法（不能访问的比如：private字段），所以get和set（这里的get和set不是getter和setter，
                 * 而是java.lang.reflect.Field下的get和set方法）。java安全机制允许查看一个对象有哪些字段，但除非拥有访问权限，否则
                 * 不允许读写那些字段的值
                 * 反射机制的默认行为受限于java的访问控制。不过，可以调用Field,Method和Constructor对象的setAccessible方法
                 * 覆盖java的访问控制，这个方法是AccessibleObject类的一个方法，它是Field,Method,Constructor类的公共超类
                 */
                field.setAccessible(true);
                field.set(bean, reliantBean);
            }
        }
        classToBean.put(cls, bean);
        return true;
    }

    /**
     * 对于所有被@Aspect注解修饰的类
     * 遍历它们定义的方法，处理@Pointcut、@Before、@After注解
     */
    private static void resolveAOP(List<Class<?>> aspectClasses) throws IllegalAccessException, InstantiationException, ClassNotFoundException {
        if (aspectClasses.size() == 0) return;

        for (Class<?> aClass : aspectClasses){
            Method before = null;
            Method after = null;
            Object target = null;

            String method = null;
            String pointcutName = null;

            //初始化对象，为了简单起见，假设每一个代理类，最多只有一个切点，一个前置以及一个后置处理器
            //所以我们也必须先处理pointcut，再解析before和after方法
            Object bean = aClass.newInstance();
            for (Method m : bean.getClass().getDeclaredMethods()){
                if (m.isAnnotationPresent(Pointcut.class)){
                    //com.newhans.demo.rapper.rap()
                    String pointcut = m.getAnnotation(Pointcut.class).value();
                    String classStr = pointcut.substring(0, pointcut.lastIndexOf("."));
                    target = Thread.currentThread().getContextClassLoader().loadClass(classStr).newInstance();
                    method = pointcut.substring(pointcut.lastIndexOf(".") + 1);
                    pointcutName = m.getName();
                }
            }

            for (Method m : bean.getClass().getDeclaredMethods()){

            }

        }
    }
}
