package com.msjwsms.netty.client;

import com.msjwsms.netty.utils.MarshallingCodeCFactory;
import com.msjwsms.netty.utils.ReadFile;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


/**
 * Created by yuantousanfen on 2019/6/25.
 */
public class NettyClientStart {
    public static void main(String[] args) {
        EventLoopGroup workGroup = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(workGroup)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(MarshallingCodeCFactory.buildMarshallingDecoder());
                            socketChannel.pipeline().addLast(MarshallingCodeCFactory.buildMarshallingEncoder());
                            socketChannel.pipeline().addLast(new ClientHandler());
                        }
                    });

            String serverip = "127.0.0.1";
            String path = System.getProperty("user.dir") + File.separator + "application.yml";

            Properties properties = ReadFile.getProperties(path);
            System.out.println(properties.get("serverip"));
            serverip = properties.get("serverip").toString();

            ChannelFuture channelFuture = bootstrap.connect(serverip, 8765).sync();
            // channelFuture.channel().writeAndFlush(Unpooled.copiedBuffer("777".getBytes()));
            channelFuture.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            workGroup.shutdownGracefully();
        }
    }
}
