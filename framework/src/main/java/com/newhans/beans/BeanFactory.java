package com.newhans.beans;

import com.newhans.aop.*;
import com.newhans.web.mvc.Controller;
import org.omg.CORBA.ObjectHelper;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

//Bean工厂，用来初始化和保存Bean
public class BeanFactory {
    //用来表示bean类型到bean实例的映射，我们可以通过bean的类型，从这个映射里取到对应的bean
    //这个映射在后续处理时可能是并发处理，我们使用concurrentHashMap
    private static Map<Class<?>, Object> beans = new ConcurrentHashMap<>();

    /**
     *带有@Autowired注解修饰的属性的类
     */
    //这里@Autowired是基于注解的依赖注入
    /**
     * 而依赖注入一共有四种实现方式
     * 1.构造方法注入
     * 2.setter方法注入
     * 3.接口注入
     * 4.注解注入 ---> @Autowired + @Component
      */
    private static Set<Class<?>> beansHasAutoWiredField = Collections.synchronizedSet(new HashSet<>());

    //用来从映射里获取bean
    public static Object getBean(Class<?> cls){
        return beans.get(cls);
    }

    /**
     * 根据类列表classList来查找所有需要初始化的类放入Component工厂
     * 并且处理类中所有带@Autowired注解的字段的依赖问题
     * @param classList
     * @throws Exception
     */
    //传入的是之前扫描到的类定义
    public static void initBean(List<Class<?>> classList) throws Exception {
        /*因为类定义后续处理类中@RequestMapping注解生成处理器时还要使用
        因此这里要创建新容器，不能修改原引用
         */
        List<Class<?>> classesToCreate = new ArrayList<>(classList);
        //被@aspect注解的切面类
        List<Class<?>> aspectClasses = new ArrayList<>();

        for (Class<?> aClass : classesToCreate){
            if (aClass.isAnnotationPresent(Aspect.class)){
                aspectClasses.add(aClass);
            }else{
                createBean(aClass);
            }
        }

        //使用动态代理来处理AOP
        resolveAOP(aspectClasses);

        //todo
        //有的类中某个属性已经通过@Autowired注入了旧的被代理的对象，重新创建他们
        for (Class<?> aClass : beansHasAutoWiredField){
            createBean(aClass);
        }
    }

    private static void createBean(Class<?> aClass) throws IllegalAccessException, InstantiationException {
        //只处理带@Component注解或者@Controller注解的类
        //Controller也是一种特殊的bean
        if (!aClass.isAnnotationPresent(Component.class) && !aClass.isAnnotationPresent(Controller.class)){
            return;
        }
        //初始化对象
        /**
         * ！！！！！！！！！在这里初始化bean
         */
        Object bean = aClass.newInstance();
        //遍历类中所有定义的字段，如果字段带有@Autowired注解，则需要注入对应依赖
        for (Field field : bean.getClass().getDeclaredFields()){
            //如果这个属性被@Autowired注解到，就表示它需要依赖注入来解决这个依赖
            if (!field.isAnnotationPresent(AutoWired.class)){
                continue;
            }
            //将需要注入其他Bean的类保存起来，因为要等到AOP代理类生成之后，需要更新他们
            BeanFactory.beansHasAutoWiredField.add(aClass);
            //我们先获得这个属性的类型，再通过类型区bean工厂获取bean
            Class<?> fieldType = field.getType();
            field.setAccessible(true);
            /*
             *只能对可以访问的字段使用get和set方法（不能访问的比如：private字段），所以get和set（这里的get和set不是getter和setter，
             * 而是java.lang.reflect.Field下的get和set方法）。java安全机制允许查看一个对象有哪些字段，但除非拥有访问权限，否则
             * 不允许读写那些字段的值
             * 反射机制的默认行为受限于java的访问控制。不过，可以调用Field,Method和Constructor对象的setAccessible方法
             * 覆盖java的访问控制，这个方法是AccessibleObject类的一个方法，它是Field,Method,Constructor类的公共超类
             */
            //field.setAccessible(true);
            if (fieldType.isInterface()){
                //如果依赖的类型是接口，则查询其实现类
                //class1.isAssignableFrom(class2) = true 代表class2是class1类型，可分配class2对象给class1
                for (Class<?> key : BeanFactory.beans.keySet()){
                    if (fieldType.isAssignableFrom(key)){
                        fieldType = key;
                        break;
                    }
                }
            }
            //可以直接用字段的set方法设值注入了
            field.set(bean, BeanFactory.getBean(fieldType));
        }
        //todo
        /**
         * ！！！这里可能Autowired注入失败，例如存在循环依赖，或者bean工厂中根本不存在，目前暂时先不处理
         */
        //字段处理完后，就可以把bean放入bean工厂了
        //这里可能Autowired注入失败，例如存在循环依赖，或者bean工厂中根本不存在，目前暂时先不处理
        beans.put(aClass, bean);
        //然后回MappingHandler，让MappingHandler可以使用bean
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
            //所有注册到容器的业务对象，在spring中称为bean （业务需求<--->系统需求）
            for (Method m : bean.getClass().getDeclaredMethods()){
                if (m.isAnnotationPresent(Pointcut.class)){
                    //eg. @Pointcut("com.newhans.Service.serviceImpl.Rapper.rap()")
                    String pointcut = m.getAnnotation(Pointcut.class).value();
                    String classStr = pointcut.substring(0, pointcut.lastIndexOf("."));
                    //loadClass将类加载
                    target = Thread.currentThread().getContextClassLoader().loadClass(classStr).newInstance();
                    method = pointcut.substring(pointcut.lastIndexOf(".") + 1);
                    pointcutName = m.getName();
                }
            }

            for (Method m : bean.getClass().getDeclaredMethods()){
                //eg.  @Before("rapPoint()")
                if (m.isAnnotationPresent(Before.class)){
                    String value = m.getAnnotation(Before.class).value();
                    value = value.substring(0, value.indexOf("("));
                    if (value.equals(pointcutName)){
                        before = m;
                    }
                }else if (m.isAnnotationPresent(After.class)){
                    String value = m.getAnnotation(After.class).value();
                    value = value.substring(0, value.indexOf("("));
                    if (value.equals((pointcutName))){
                        after = m;
                    }
                }
            }

            //获取代理对象并更新bean工厂
            Object proxy = new ProxyDynamic().createProxy(bean, before, after, target, method.substring(0, method.indexOf("(")));

            BeanFactory.beans.put(target.getClass(), proxy);
        }
    }
}

/*
    双亲委派模型的实现
    protected synchronized Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException{
        Class c = findLoadedClass(name);
        if (c == null){
            try{
                if (parent != null){
                    c = parent.loadClass(name, false);
                }else{
                    c = findBootstrapClassOrNull(name);
                }
            }catch (ClassNotFoundException e){

            }
            if (c == null){
                //在父类加载器无法加载的时候，再调用本身的findClass方法来进行类加载
                c = findClass(name);
            }
            if (resolve){
                resolveClass(c);
            }
        }
        return c;
    }
 */
