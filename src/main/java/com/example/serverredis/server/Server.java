package com.example.serverredis.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;


import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
public class Server implements CommandLineRunner {

    @Value("${Netty.server.inetHost:127.0.0.1}")
    private String inetHost;

    @Value("${Netty.server.inetPort:6666}")
    private Integer inetPort;

    @Value("${Timer.initialDelay:0}")
    private Integer initialDelay;
    @Value("${Timer.period:30}")
    private Integer period;
    @Value("${Timer.unit:MINUTES}")
    private TimeUnit timeUnit;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private static final CommandServer commandServer = CommandServer.getInstance();

    @Override
    public void run(String... args) throws Exception {
        commandServer.init();
        scheduler.scheduleAtFixedRate(commandServer::clean,initialDelay,period, timeUnit);
        connection();
    }

    public void connection() throws Exception{
        //创建两个线程组 boosGroup、workerGroup
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            //创建服务端的启动对象，设置参数
            ServerBootstrap bootstrap = new ServerBootstrap();
            //设置两个线程组boosGroup和workerGroup
            bootstrap.group(bossGroup, workerGroup)
                    //设置服务端通道实现类型
                    .channel(NioServerSocketChannel.class)
                    //设置线程队列得到连接个数
                    .option(ChannelOption.SO_BACKLOG, 128)
                    //设置保持活动连接状态
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    //使用匿名内部类的形式初始化通道对象
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            //给pipeline管道设置处理器
                            socketChannel.pipeline().addLast(new ServerHandler());
                        }
                    });//给workerGroup的EventLoop对应的管道设置处理器
            //绑定端口号，启动服务端
            ChannelFuture channelFuture = bootstrap.bind(inetHost,inetPort).sync();
            //对关闭通道进行监听
            channelFuture.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
