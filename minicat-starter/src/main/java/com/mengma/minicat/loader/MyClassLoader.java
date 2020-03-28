package com.mengma.minicat.loader;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author fgm
 * @description 自定义类加载器
 * @date 2020-03-24
 ***/
public class MyClassLoader extends URLClassLoader {

    protected final ClassLoader parent;

    protected final ClassLoader javaseClassLoader;


    /**
     * class资源加载记录
     */
    protected final Map<String, ResourceEntry> resourceEntries =new ConcurrentHashMap();

    //自定义加载类型
    private  final String CLASS_FILE_SUFFIX=".class";

    /**
     * minicat资源加载主目录
     */
    private String minicatBase;


    public MyClassLoader(String webAppPath,String webAppName) {
        super(new URL[0]);
        ClassLoader p = getParent();
        if (p == null) {
            p = getSystemClassLoader();
        }
        this.parent = p;

        ClassLoader j = String.class.getClassLoader();
        if (j == null) {
            j = getSystemClassLoader();
            while (j.getParent() != null) {
                j = j.getParent();
            }
        }
        this.javaseClassLoader = j;
        this.minicatBase=webAppPath+"/"+webAppName+"/";
    }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
       //检查本地是否加载
        Class<?>  clazz = findLoadedClass0(name);
        if (clazz != null) {
            System.out.println("已经加载过的class");
            return clazz;
        }
        //自定义加载器逻辑
       //1、从本地加载类缓存中获取
       clazz=findLoadedClass(name);
       if(null!=clazz){
           return clazz;
       }
      //2、从系统加载类缓存中获取
       clazz = findLoadedClass(name);
        if (clazz != null) {
            return clazz;
        }
      //3、优先从系统加载类中加载，防止重写javaSE的类
       try {
           clazz = javaseClassLoader.loadClass(name);
           if(clazz!=null){
               return clazz;
           }
       }catch (Exception ex){
           //do nothing
       }


       boolean delegateLoad = filter(name);
      //4、minicat组件类代理给父类加载
      if (delegateLoad) {
            try {
                clazz = Class.forName(name, false, parent);
                if (clazz != null) {
                    return clazz;
                }
            } catch (ClassNotFoundException e) {
                // Ignore
            }
        }
       //5、使用用自定义类加载器加载
        try {
            clazz = findClass(name);
            System.out.println("自定义类加载器加载");
            return clazz;
        } catch (ClassNotFoundException e) {
            // Ignore
            System.out.println("自定义类加载器加载异常");
        }

        //6、如果找不到，再次代理给父类加载
        if (!delegateLoad) {
            try {
                clazz = Class.forName(name, false, parent);
                if (clazz != null) {
                    return clazz;
                }
            } catch (ClassNotFoundException e) {
                // Ignore
            }
        }
        throw new ClassNotFoundException(name);

    }


    @Override
    public Class<?> findClass(String name) throws ClassNotFoundException{
        Class<?> clazz  = findClassInternal(name);
        if(null!=clazz){
            return clazz;
        }
        throw new ClassNotFoundException(name);
    }

    protected Class<?> findClassInternal(String name) {
        if (name == null) {
            return null;
        }
        String path = binaryNameToPath(name, true);

        ResourceEntry entry = resourceEntries.get(path);
        if(null!=entry){
            return entry.getLoadedClass();
        }
        entry = new ResourceEntry();
        entry.lastModified = System.currentTimeMillis();
        try {
            byte[]  classData = this.loadByteClass(name);
            Class<?> clazz= super.defineClass(name,classData,0,classData.length);
            entry.loadedClass = clazz;
            resourceEntries.put(path,entry);
            return clazz;
        } catch (Exception e) {
           e.printStackTrace();
        }
        return null;
    }



    private boolean filter(String className) {
        if (className == null){
            return false;
        }
        //公共资源交给父类加载
        if(className.startsWith("com.mengma.minicat")){
            return true;
        }
        return false;

    }

    /**
     * 查找已经加载的class
     * @param name
     * @return
     */
    protected Class<?> findLoadedClass0(String name) {
        String path = binaryNameToPath(name, true);
        ResourceEntry entry = resourceEntries.get(path);
        if (entry != null) {
            return entry.loadedClass;
        }
        return null;
    }

    /**
     * class资源地址转换
     * @param binaryName
     * @param withLeadingSlash
     * @return
     */
    private String binaryNameToPath(String binaryName, boolean withLeadingSlash) {
        // 1 for leading '/', 6 for ".class"
        StringBuilder path = new StringBuilder(7 + binaryName.length());
        if (withLeadingSlash) {
            path.append('/');
        }
        path.append(binaryName.replace('.', '/'));
        path.append(CLASS_FILE_SUFFIX);
        return path.toString();
    }


    private byte[] loadByteClass(String name) throws Exception{
        name = minicatBase+name.replaceAll("\\.","/")+CLASS_FILE_SUFFIX;
        File file=new File(name);
        // 这里要读入.class的字节，因此要使用字节流
        FileInputStream fis = new FileInputStream(file);
        FileChannel fc = fis.getChannel();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        WritableByteChannel wbc = Channels.newChannel(baos);
        ByteBuffer by = ByteBuffer.allocate(1024);
        while (true){
            int i = fc.read(by);
            if (i == 0 || i == -1)
                break;
            by.flip();
            wbc.write(by);
            by.clear();
        }
        fis.close();
        return baos.toByteArray();
    }



    private void closeResource(Closeable closeable) {
        try {
            if(null==closeable){
               return;
            }
            closeable.close();
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }


}
