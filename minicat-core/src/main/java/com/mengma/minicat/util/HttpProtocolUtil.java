package com.mengma.minicat.util;

/**
 * @author fgm
 * @description  http协议工具类
 * @date 2020-03-21
 ***/
public class HttpProtocolUtil {
    public static String getHttpHeader200(int contentLength) {
        return "HTTP/1.1 200 OK \n" +
            "Content-Type: text/html \n" +
            "Content-Length: " + contentLength + " \n" +
            "\r\n";
    }


    public static String getHttpHeader404() {
        String str404 = "<h1>404 not found</h1>";
        return "HTTP/1.1 404 NOT Found \n" +
            "Content-Type: text/html \n" +
            "Content-Length: " + str404.getBytes().length + " \n" +
            "\r\n" + str404;
    }

}
