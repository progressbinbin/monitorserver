package com.msjwsms.netty.client;

import com.msjwsms.netty.utils.RequestInfo;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import org.hyperic.sigar.CpuPerc;
import org.hyperic.sigar.Mem;
import org.hyperic.sigar.Sigar;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Created by yuantousanfen on 2019/6/25.
 */
public class ClientHandler extends ChannelHandlerAdapter {
    private InetAddress address;
    private ScheduledExecutorService scheduledExecutorService= Executors.newScheduledThreadPool(1);
    private ScheduledFuture<?> heartBeat;
    private static final String SUCCESS_KEY="auth_success_key";
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        //super.channelActive(ctx);
        address=InetAddress.getLocalHost();
        String ip=address.getHostAddress();
        System.out.println("ip地址:"+ip);
        String key="666666";
        //证书
        String auth_key=ip+","+key;
        ctx.writeAndFlush(auth_key);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            //do something msg
            /*ByteBuf buf = (ByteBuf)msg;
            byte[] data = new byte[buf.readableBytes()];
            buf.readBytes(data);
            String request = new String(data, "utf-8");
            System.out.println("Client: " + request);*/
            if(msg instanceof String) {
                String ret = (String) msg;
                if (SUCCESS_KEY.equals(ret)) {
                    //握手成功，自动发送心跳包
                    this.heartBeat = this.scheduledExecutorService.scheduleWithFixedDelay(new HeartBeatTask(ctx), 0, 10, TimeUnit.SECONDS);
                }
                System.out.println(msg);
            }

        } finally {
            ReferenceCountUtil.release(msg);
        }

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
    private class HeartBeatTask implements Runnable{

        private final ChannelHandlerContext ctx;
        public HeartBeatTask(ChannelHandlerContext ctx){
            this.ctx=ctx;
        }
        @Override
        public void run() {
            try{
                RequestInfo requestInfo=new RequestInfo();
                requestInfo.setIp(address.getHostAddress());
                Sigar sigar=new Sigar();
                CpuPerc cpuPerc=sigar.getCpuPerc();
                HashMap<String,Object> cpuPercMap=new HashMap<>();
                cpuPercMap.put("combined",Math.round(cpuPerc.getCombined()*100)+"%");//cpu总使用率
                cpuPercMap.put("user",Math.round(cpuPerc.getUser()*100)+"%");//cpu用户使用率
                cpuPercMap.put("sys",Math.round(cpuPerc.getSys()*100)+"%");//cpu系统使用率
                cpuPercMap.put("idle",Math.round(cpuPerc.getIdle()*100)+"%");//cpu当前空闲率
                cpuPercMap.put("wait",Math.round(cpuPerc.getWait()*100)+"%");//cpu当前等待率
                cpuPercMap.put("nice",Math.round(cpuPerc.getNice()*100)+"%");//cpu当前错误率
                requestInfo.setCpuPercMap(cpuPercMap);

                Mem mem=sigar.getMem();
                HashMap<String,Object> memMap=new HashMap<>();
                memMap.put("total",mem.getTotal()/1024/1024+"M");//内存总量
                memMap.put("used",mem.getUsed()/1024/1024+"M");//当前内存使用量
                memMap.put("free",mem.getFree()/1024/1024+"M");//当前内存剩余量

                requestInfo.setCpuPercMap(cpuPercMap);
                requestInfo.setMemoryMap(memMap);
                ctx.writeAndFlush(requestInfo);

            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
