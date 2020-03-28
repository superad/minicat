package com.mengma.minicat.request;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author fgm
 * @description   请求实体
 * @date 2020-03-21
 ***/
public class Request {

    private String method;// 请求方式，比如GET/POST
    private String url;// 例如 /,/index.html
    private String context;
    private InputStream inputStream;// 输入流，其他属性从输入流中解析出来

    public Request(InputStream inputStream) throws IOException {
       this.inputStream = inputStream;

       int count = 0;
       while(count == 0){
          count= inputStream.available();
       }
       byte[] bytes=new byte[count];
       inputStream.read(bytes);
       String requestStr=new String(bytes);
       String firstLine =  requestStr.split("\n")[0];// GET / HTTP/1.1
       String []strings = firstLine.split(" ");
       this.method=strings[0];
       String fullPath=strings[1];
       if(fullPath!=null&&fullPath.length()>0){
          this.context=fullPath.split("/")[1];
          this.url=fullPath.substring(this.context.length()+1);
       }else{
           this.context="";
           this.url="/";
       }
       System.out.println("========>>>method:"+method);
       System.out.println("========>>>context:"+context);
       System.out.println("========>>>url:"+url);

    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }
}
