package com.mengma.minicat.response;

import com.mengma.minicat.util.HttpProtocolUtil;
import com.mengma.minicat.util.StaticResourceUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author fgm
 * @description   返回实体
 * @date 2020-03-21
 ***/
public class Response {

    private OutputStream outputStream;

    public Response(OutputStream outputStream) {
        this.outputStream = outputStream;
    }


    public void output(String content) throws IOException {
        outputStream.write(content.getBytes());
    }

    public void outputHtml(String context,String path) throws IOException {
        String webAppPath=StaticResourceUtil.getWebAppPath();
        String resourcePath=webAppPath+"/"+context+"/"+path;

        File file = new File(resourcePath);
        if(file.exists()&&file.isFile()){
            StaticResourceUtil.outputStaticResouce(new FileInputStream(file),outputStream);
        }else{
            output(HttpProtocolUtil.getHttpHeader404());
        }

    }


    public OutputStream getOutputStream() {
        return outputStream;
    }

    public void setOutputStream(OutputStream outputStream) {
        this.outputStream = outputStream;
    }
}
