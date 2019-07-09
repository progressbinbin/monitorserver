package com.msjwsms.netty.server;

import com.msjwsms.netty.utils.MarshallingCodeCFactory;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;


/**
 * Created by yuantousanfen on 2019/6/25.
 */
public class NettyServerStart {
    public static void main(String[] args) throws Exception {

       // System.out.println(System.getProperty("user.dir"));
/*        Class.getResourceAsStream(String path) ：

        path 不以'/'开头时默认是从此类所在的包下取资源，以'/'开头则是从ClassPath(Src根目录)根下获取。

        其只是通过path构造一个绝对路径，最终还是由ClassLoader获取资源。



        2. Class.getClassLoader.getResourceAsStream(String path) ：

        默认则是从ClassPath根下获取，path不能以'/'开头，最终是由ClassLoader获取资源。*/
      //InputStream stream= NettyServerStart.class.getResourceAsStream("/application.yml");
        //InputStream stream= NettyServerStart.class.getClassLoader().getResourceAsStream("application.yml");
       /* File file=new File(System.getProperty("user.dir")+File.separator+"application.yml");
        InputStream stream=new FileInputStream(file);
        Properties properties=new Properties();
        properties.load(stream);
        System.out.println(properties.get("test"));*/
        //1 第一个线程组 是用于接收Client端连接的
        EventLoopGroup bossGroup=new NioEventLoopGroup();
        //第二个线程组 用于实际的业务处理操作的
        EventLoopGroup workerGroup=new NioEventLoopGroup();
        //3 创建一个辅助类Bootstrap，就是对我们的Server进行一系列的配置
        ServerBootstrap serverBootstrap=new ServerBootstrap();
        //把俩个工作线程组加入进来
        serverBootstrap.group(bossGroup,workerGroup)
                //我要指定使用NioServerSocketChannel这种类型的通道
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG,1024)
                .handler(new LoggingHandler(LogLevel.INFO))
                //一定要使用 childHandler 去绑定具体的 事件处理器
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline().addLast(MarshallingCodeCFactory.buildMarshallingDecoder());
                        socketChannel.pipeline().addLast(MarshallingCodeCFactory.buildMarshallingEncoder());
                        socketChannel.pipeline().addLast(new ServerHandler());
                    }
                });
        /**服务器启动辅助类配置完成后，调用 bind 方法绑定监听端口，调用 sync 方法同步等待绑定操作完成*/
        ChannelFuture future=serverBootstrap.bind(8765).sync();

        System.out.println(Thread.currentThread().getName() + ",服务器开始监听端口，等待客户端连接.........");
        /**下面会进行阻塞，等待服务器连接关闭之后 main 方法退出，程序结束*/

        future.channel().closeFuture().sync();
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();

    }
}
