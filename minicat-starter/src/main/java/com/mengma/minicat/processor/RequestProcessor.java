package com.mengma.minicat.processor;

import com.mengma.minicat.mapper.MappingData;
import com.mengma.minicat.request.Request;
import com.mengma.minicat.response.Response;
import com.mengma.minicat.servlet.HttpServlet;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

/**
 * @author fgm
 * @description
 * @date 2020-03-21
 ***/
public class RequestProcessor implements Runnable{


    private Socket socket;

    private Map<String, MappingData> contextMappingData;

    private Map<String,ClassLoader> webAppClassLoader;

    public RequestProcessor(Socket socket, Map<String, MappingData> contextMappingData,Map<String,ClassLoader> webAppClassLoader) {
        this.socket = socket;
        this.contextMappingData = contextMappingData;
        this.webAppClassLoader = webAppClassLoader;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public Map<String, MappingData> getContextMappingData() {
        return contextMappingData;
    }

    public void setContextMappingData(Map<String, MappingData> contextMappingData) {
        this.contextMappingData = contextMappingData;
    }

    public Map<String, ClassLoader> getWebAppClassLoader() {
        return webAppClassLoader;
    }

    public void setWebAppClassLoader(Map<String, ClassLoader> webAppClassLoader) {
        this.webAppClassLoader = webAppClassLoader;
    }

    public void run() {
        try {

            InputStream inputStream=socket.getInputStream();
            Request request=new Request(inputStream);
            //设置线程类加载器
            ClassLoader classLoader= webAppClassLoader.get(request.getContext());
            Thread.currentThread().setContextClassLoader(classLoader);

            Response response=new Response(socket.getOutputStream());
            String url=request.getUrl();
            String context=request.getContext();
            //解析host、context、servlet

            MappingData mappingData = contextMappingData.get(context);
            Map<String, HttpServlet> httpServletMap = mappingData.getServletMap();
            //静态资源处理
            if(null==httpServletMap.get(url)){
                response.outputHtml(request.getContext(),request.getUrl());
            }else{
                //动态资源处理
                HttpServlet httpServlet=httpServletMap.get(url);
                httpServlet.service(request,response);
            }
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
