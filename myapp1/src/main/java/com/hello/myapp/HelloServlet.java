package com.hello.myapp;

import com.mengma.minicat.request.Request;
import com.mengma.minicat.response.Response;
import com.mengma.minicat.servlet.HttpServlet;
import com.mengma.minicat.util.HttpProtocolUtil;

import java.io.IOException;

/**
 * @author fgm
 * @description  自定义servlet
 * @date 2020-03-21
 ***/
public class HelloServlet extends HttpServlet {

    protected void doPost(Request request, Response response) {
        String content = "<h1>HelloServlet post in myapp1</h1>";
        try {
            response.output(HttpProtocolUtil.getHttpHeader200(content.getBytes().length)+content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void doGet(Request request, Response response) {
        String content = "<h1>HelloServlet get in myapp1</h1>";
        try {
            response.output(HttpProtocolUtil.getHttpHeader200(content.getBytes().length)+content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void init() throws Exception {

    }


    public void destroy() throws Exception {

    }


}
