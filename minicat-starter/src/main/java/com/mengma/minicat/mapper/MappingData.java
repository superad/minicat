package com.mengma.minicat.mapper;

import com.mengma.minicat.servlet.HttpServlet;

import java.util.HashMap;
import java.util.Map;

/**
 * @author fgm
 * @description  上下文映射器
 * @date 2020-03-28
 ***/
public class MappingData {

    private String host;

    private String context;

    private Map<String, HttpServlet> servletMap;

    public MappingData(String host, String context) {
        this.host = host;
        this.context = context;
        this.servletMap=new HashMap();
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public Map<String, HttpServlet> getServletMap() {
        return servletMap;
    }

    public void setServletMap(Map<String, HttpServlet> servletMap) {
        this.servletMap = servletMap;
    }
}
