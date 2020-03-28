package com.mengma.minicat.servlet;

import com.mengma.minicat.request.Request;
import com.mengma.minicat.response.Response;

/**
 * @author fgm
 * @description  servlet接口
 * @date 2020-03-21
 ***/
public interface Servlet {

    void init() throws Exception;

    void destroy() throws Exception;


    void service(Request request, Response response) throws Exception;

}
