package com.newhans.core;

import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ClassScanner {
    //工具类
    public static List<Class<?>> scanClasses(String packageName) throws IOException, ClassNotFoundException {
        //T:一类 ？:不知道多少不同的
        List<Class<?>> classList = new ArrayList<>();
        //包名转换为文件路径
        String path = packageName.replace(".","/");
        //获取默认的类加载器-->应该是Application Class Loader
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        //双亲委派机制:Application Class Loader -> Extension Class Loader -> Bootstrap Class Loader
        //调用类加载器的getResources方法，它的返回值是一个可遍历的URL资源
        Enumeration<URL> resources = classLoader.getResources(path);
        //接下来遍历这些资源
        while (resources.hasMoreElements()){
            URL resource = resources.nextElement();
            //资源的类型可以通过它的Protocol属性获得到
            //我们的项目，最终都要打到jar包来运行，所以我们处理资源类型为jar包的情况
            if (resource.getProtocol().contains("jar")){
                //jar包路径可以通过JarURLConnection来获得
                JarURLConnection jarURLConnection = (JarURLConnection) resource.openConnection();
                //从这个连接获取到jar包的路径名
                String jarFilePath = jarURLConnection.getJarFile().getName();
                //通过jar包的路径，获取到jar包下所有的类
                classList.addAll(getClassesFromJar(jarFilePath, path));
            }else{
                //TODO
                //我们只处理了资源类型为jar包的情况
            }
        }
        return classList;
    }

    private static List<Class<?>> getClassesFromJar(String jarFilePath, String path) throws IOException, ClassNotFoundException {
        //1.传入jar包的路径
        //2.一个jar包中有很多类文件，我们需要指定哪些类文件是我们需要的，可以通过类的相对路径来指定
        List<Class<?>> classes = new ArrayList<>();
        JarFile jarFile = new JarFile(jarFilePath);
        Enumeration<JarEntry> jarEntries = jarFile.entries();
        while (jarEntries.hasMoreElements()){
            JarEntry jarEntry = jarEntries.nextElement();
            String entryName = jarEntry.getName();
            //jar entry : eg. com/newhans/Test.class
            if (entryName.startsWith(path) && entryName.endsWith((".class"))){
                //以我们所需要的路径开头的文件
                String classFullName = entryName.replace("/", ".").substring(0, entryName.length() - 6);
                //反射：获得类对象
                //！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！
                //获取到类的全限定名之后，还需要通过类加载器，将他们加载到JVM里
                classes.add(Class.forName(classFullName));
                //forName：获取类类加载器，然后去加载这个类
            }
        }
        return classes;
    }
}
