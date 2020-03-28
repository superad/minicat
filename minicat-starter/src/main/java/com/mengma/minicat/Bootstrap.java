package com.mengma.minicat;

import com.mengma.minicat.loader.MyClassLoader;
import com.mengma.minicat.mapper.MappingData;
import com.mengma.minicat.processor.RequestProcessor;
import com.mengma.minicat.servlet.HttpServlet;
import com.mengma.minicat.util.StaticResourceUtil;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * @author fgm
 * @description  启动类
 * @date 2020-03-21
 ***/
public class Bootstrap {

    /**
     * 上线文对应mapper
     *  key:webAppName
     *  value:mappingData
     */
    private static Map<String, MappingData> contextMappingData=new HashMap();

    /**
     * webapp的类加载器
     */
    private static Map<String,ClassLoader> webAppClassLoader=new HashMap();


    private int port=8080;

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public  void start() throws Exception{
        loadServlet();

        int corePoolSize=50;
        int maximumPoolSize=100;
        long keepAliveTime=600L;
        TimeUnit unit=TimeUnit.SECONDS;
        BlockingQueue<Runnable> workQueue=new ArrayBlockingQueue<Runnable>(1024);
        ThreadFactory threadFactory=Executors.defaultThreadFactory();
        ThreadPoolExecutor threadPoolExecutor=new ThreadPoolExecutor(corePoolSize,maximumPoolSize,keepAliveTime,unit,workQueue,threadFactory);
        ServerSocket serverSocket=new ServerSocket(this.port);
        System.out.println("=====>>>Minicat start on port：" + port);
        while (true){
            Socket socket = serverSocket.accept();
            RequestProcessor requestProcessor=new RequestProcessor(socket,contextMappingData,webAppClassLoader);
            threadPoolExecutor.execute(requestProcessor);
        }

    }



    /**
     * 加载自定义servlet
     */
    private void loadServlet() {

        //1、解析webapps 目录下的webapp
        String webAppPath= StaticResourceUtil.getWebAppPath();

        Map<String,InputStream> webAppMap=loadWebApps(webAppPath);
        if(null==webAppMap||webAppMap.isEmpty()){
            return;
        }

        //2、解析web.xml 映射对应url和servlet
        //3、servlet加载时，使用自定义的classLoader来加载
        for (Map.Entry<String, InputStream> webAppEntry : webAppMap.entrySet()) {
            String webAppName=webAppEntry.getKey();
            MappingData mappingData=new MappingData("localhost",webAppName);
            contextMappingData.put(webAppName,mappingData);
            System.out.println(webAppName+"应用加载中....");
            InputStream inputStream=webAppEntry.getValue();
            if(null==inputStream){
                continue;
            }
            SAXReader saxReader=new SAXReader();
            try {
                Document document = saxReader.read(inputStream);
                Element rootElement = document.getRootElement();

                //解析servletNameClass
                Map<String,String> servletNameClassMap=parseServletNameClass(rootElement);
                //解析servletNameUrl
                Map<String,String> servletUrlNameMap=parseUrlServletName(rootElement);

                MyClassLoader loader=new MyClassLoader(webAppPath,webAppName);
                webAppClassLoader.put(webAppName,loader);
                Map<String, HttpServlet> servletMap=new HashMap();
                for (Map.Entry<String, String> entry:servletUrlNameMap.entrySet()){
                    String url= entry.getKey();
                    String className=servletNameClassMap.get(entry.getValue());
                    if(null==className){
                        continue;
                    }
                    //使用自定义类加载器加载
                    HttpServlet servlet= (HttpServlet)loader.loadClass(className).newInstance();
                    servletMap.put(url,servlet);
                }
                mappingData.setServletMap(servletMap);


            } catch (DocumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }





    }

    /**
     * 加载所有webapp
     * @return
     * @throws FileNotFoundException
     */
    private Map<String, InputStream> loadWebApps(String webAppPath) {
        if(null==webAppPath||webAppPath.length()==0){
            System.out.println(webAppPath+"对应webapp不存在");
            return new HashMap();
        }
        Map<String, InputStream> webAppMap=new HashMap();
        File file=new File(webAppPath);
        if(!file.exists()){
            System.out.println(webAppPath+"对应webapp不存在");
            return new HashMap();
        }
        for(File item:file.listFiles()){
            String webXmlPath= file.getAbsolutePath()+"/"+item.getName()+"/web.xml";
            File webXmlFile=new File(webXmlPath);
            if(webXmlFile.exists()&&webXmlFile.isFile()){
                try {
                    InputStream inputStream = new FileInputStream(webXmlFile);
                    webAppMap.put(item.getName(),inputStream);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
        return webAppMap;
    }

    private Map<String, String> parseUrlServletName(Element rootElement) {
        //根据servletName 找到对应url-pattern
        Map<String,String> servletUrlNameMap=new HashMap<String, String>();
        List<Element> servletMappingNodes = rootElement.selectNodes("servlet-mapping");
        for(Element element:servletMappingNodes){
            Element servletNameElement=(Element)element.selectSingleNode("servlet-name");
            String servletName=servletNameElement.getStringValue();
            Element servletUrlElement=(Element)element.selectSingleNode("url-pattern");
            String servletUrl= servletUrlElement.getStringValue();
            servletUrlNameMap.put(servletUrl,servletName);
        }
        return servletUrlNameMap;
    }

    private Map<String, String> parseServletNameClass(Element rootElement) {
        Map<String,String> servletNameClassMap=new HashMap<String, String>();
        List<Element> servletNodes = rootElement.selectNodes("servlet");
        //解析servletNameClass
        for(Element element:servletNodes){
            Element servletNameElement = (Element)element.selectSingleNode("servlet-name");
            String servletName=servletNameElement.getStringValue();
            Element servletClassElement=(Element)element.selectSingleNode("servlet-class");
            String servletClass=servletClassElement.getStringValue();
            servletNameClassMap.put(servletName,servletClass);
        }
        return servletNameClassMap;
    }

    public static void main(String[] args) throws Exception {

        Bootstrap bootstrap=new Bootstrap();
        bootstrap.start();


    }

}
