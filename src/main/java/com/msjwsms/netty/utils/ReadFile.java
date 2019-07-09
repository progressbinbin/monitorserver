package com.msjwsms.netty.utils;

import com.msjwsms.netty.server.NettyServerStart;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by yuantousanfen on 2019/7/9.
 */
public class ReadFile {

    public static Properties getProperties(String path){
       // InputStream stream= NettyServerStart.class.getResourceAsStream("/application.yml");
        //InputStream stream= NettyServerStart.class.getClassLoader().getResourceAsStream("application.yml");
        File file=null;
        InputStream stream=null;
        Properties properties=null;
        try{
            file=new File(path);
            stream=new FileInputStream(file);
            properties=new Properties();
            properties.load(stream);
            return properties;
        }catch (Exception ex){
            ex.printStackTrace();

        }finally {
            try {
                if (stream != null) {
                    stream.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
                ;
            }
        }
        return null;
    }
}
