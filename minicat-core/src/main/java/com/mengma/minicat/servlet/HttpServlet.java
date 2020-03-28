package com.mengma.minicat.servlet;

import com.mengma.minicat.request.Request;
import com.mengma.minicat.response.Response;

/**
 * @author fgm
 * @description  httpServlet
 * @date 2020-03-21
 ***/
public abstract class HttpServlet implements Servlet {


    public void service(Request request, Response response) throws Exception {

        if("GET".equalsIgnoreCase(request.getMethod())){
            doGet(request,response);
        }else {
            doPost(request,response);
        }
    }

    protected abstract void doPost(Request request, Response response);

    protected abstract void doGet(Request request, Response response);





}
