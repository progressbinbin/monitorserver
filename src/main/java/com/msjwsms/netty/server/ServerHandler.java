package com.msjwsms.netty.server;

import com.msjwsms.netty.utils.RequestInfo;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Properties;


/**
 * Created by yuantousanfen on 2019/6/25.
 */
public class ServerHandler extends ChannelHandlerAdapter {

    private static final HashMap<String,String> AUTH_MAP=new HashMap<>();
    private static final String SUCCESS_KEY="auth_success_key";
    static {
       InputStream stream=null;
        try {
            //stream= NettyServerStart.class.getClassLoader().getResourceAsStream("application.yml");
            File file=new File(System.getProperty("user.dir")+ File.separator+"application.yml");
            stream=new FileInputStream(file);
            Properties properties = new Properties();
            properties.load(stream);
            System.out.println(properties.get("ips"));
            String[] ips=properties.get("ips").toString().split(",");
            for(String ip:ips){
                AUTH_MAP.put(ip,"666666");
            }
        }catch (IOException ex){
            ex.printStackTrace();
        }finally {
            try{
                if(stream!=null){
                    stream.close();
                }
            }catch (Exception e){
                e.printStackTrace();;
            }
        }
        //AUTH_MAP.put("192.168.0.158","123456");
    }
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        //do something msg
      /*  ByteBuf buf = (ByteBuf)msg;
        byte[] data = new byte[buf.readableBytes()];
        buf.readBytes(data);
        String request = new String(data, "utf-8");
        System.out.println("Server: " + request);
        //写给客户端
        String response = "我是反馈的信息";
        ctx.writeAndFlush(Unpooled.copiedBuffer("888".getBytes()));
        //.addListener(ChannelFutureListener.CLOSE);
*/
        /*String ret=(String)msg;
        System.out.println(ret);*/
        if (msg instanceof String) {
            String ret = (String) msg;
            System.out.println("客户端信息："+ret);
            auth(ctx, msg);
        } else if (msg instanceof RequestInfo) {
            RequestInfo requestInfo = (RequestInfo) msg;
            System.out.println("--------------------------------------");
            System.out.println("当前主机IP:" + requestInfo.getIp());
            System.out.println("--------------------------------------");
            System.out.println("当前主机CPU情况：");
            HashMap<String, Object> cpuPercMap = requestInfo.getCpuPercMap();
            System.out.println("总使用率：" + cpuPercMap.get("combined"));
            System.out.println("用户使用率：" + cpuPercMap.get("user"));
            System.out.println("系统使用率:" + cpuPercMap.get("sys"));
            System.out.println("当前等待率：" + cpuPercMap.get("wait"));
            System.out.println("当前空闲率:" + cpuPercMap.get("idle"));
            System.out.println("当前错误率：" + cpuPercMap.get("nice"));
            System.out.println("------------------------------------");
            System.out.println("当前主机内存情况：");
            HashMap<String, Object> mem = requestInfo.getMemoryMap();
            System.out.println("内存总量:" + mem.get("total"));
            System.out.println("当前内存使用量：" + mem.get("used"));
            System.out.println("当前内存剩余量：" + mem.get("free"));
            ctx.writeAndFlush("recevied");
        } else {
            final ChannelFuture future = ctx.writeAndFlush("connect failure!");
            future.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture channelFuture) throws Exception {
                    //assert future == channelFuture;
                    ctx.close();
                }
            });
        }
    }

    private boolean auth(ChannelHandlerContext ctx, Object msg){
        String ret[]=((String)msg).split(",");
        String auth=AUTH_MAP.get(ret[0]);
        if(auth!=null&&auth.equals(ret[1])){
            ctx.writeAndFlush(SUCCESS_KEY);
            return true;
        }else{
            ctx.writeAndFlush("auth failure!").addListener(ChannelFutureListener.CLOSE);
            return false;
        }
    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

}
