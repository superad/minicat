package com.mengma.minicat.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author fgm
 * @description  静态资源工具类
 * @date 2020-03-21
 ***/
public class StaticResourceUtil {

    private static final String RELATIVE_PATH="/source/webapps/";

    public static String getAbsolutePath(String url){
        String absolutePath = StaticResourceUtil.class.getResource("/").getPath();
        return absolutePath.replaceAll("\\\\","/")+url;
    }

    /**
     * 获取webapp存放的目录
     * @return
     */
    public static String getWebAppPath(){
        String absolutePath = StaticResourceUtil.class.getResource("/").getPath();
        absolutePath = absolutePath.replaceAll("\\\\","/");
        int position = absolutePath.indexOf("/minicat-starter/target/classes");
        String projectPath=absolutePath.substring(0,position);
        String webAppPath= projectPath+RELATIVE_PATH;
        return webAppPath;
    }



    public static void outputStaticResouce(InputStream inputStream, OutputStream outputStream) throws IOException {

        int count = 0;
        while(count == 0){
            count = inputStream.available();
        }
        int resourceSize = count;

        outputStream.write(HttpProtocolUtil.getHttpHeader200(resourceSize).getBytes());
        long written = 0;
        int batchSize = 1024;
        byte []bytes= new byte[batchSize];
        while(written<resourceSize){
            if(written+batchSize>resourceSize){
                batchSize = (int) (resourceSize - written);  // 剩余的文件内容长度
                bytes = new byte[batchSize];
            }
            inputStream.read(bytes);
            outputStream.write(bytes);
            outputStream.flush();
            written = written+batchSize;
        }

    }


}
