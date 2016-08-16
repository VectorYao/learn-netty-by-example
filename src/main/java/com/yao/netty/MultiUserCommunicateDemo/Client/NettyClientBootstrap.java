package com.yao.netty.MultiUserCommunicateDemo.Client;

import com.yao.netty.MultiUserCommunicateDemo.Message.AskMsg;
import com.yao.netty.MultiUserCommunicateDemo.Message.AskParams;
import com.yao.netty.MultiUserCommunicateDemo.Message.LoginMsg;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

/**
 * 客户端启动代码
 */
public class NettyClientBootstrap {
    private int port;
    private String host;
    private SocketChannel socketChannel;
    public NettyClientBootstrap(int port, String host) throws InterruptedException {
        this.port = port;
        this.host = host;
        start();
    }
    private void start() throws InterruptedException {
        EventLoopGroup group = new NioEventLoopGroup();
//        try{
            Bootstrap bootstrap=new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.SO_KEEPALIVE,true)
                    .remoteAddress(host,port)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            //设定读空闲时间为20s、写空闲时间为10s。触发事件类型见Class IdleState。
                            socketChannel.pipeline().addLast(new IdleStateHandler(20,10,0));
                            socketChannel.pipeline().addLast(new ObjectEncoder());
                            socketChannel.pipeline().addLast(new ObjectDecoder(ClassResolvers.cacheDisabled(null)));
                            socketChannel.pipeline().addLast(new NettyClientHandler());
                        }
                    });

            ChannelFuture future =bootstrap.connect(host,port).sync();
            if (future.isSuccess()) {
                socketChannel = (SocketChannel)future.channel();
                System.out.println("connect server  成功---------");
            }
//            //阻塞直到关闭连接
//            future.channel().closeFuture().sync();
//        }finally {
//            //优雅关闭，释放所有线程池资源
//            group.shutdownGracefully();
//        }

    }
    public static void main(String[]args) throws InterruptedException {
        NettyClientBootstrap bootstrap=new NettyClientBootstrap(9999,"localhost");

        LoginMsg loginMsg=new LoginMsg();
        loginMsg.setUserName("yao");
        loginMsg.setPassword("123456");
        bootstrap.socketChannel.writeAndFlush(loginMsg);
        while (true){
            TimeUnit.SECONDS.sleep(3);
            AskMsg askMsg=new AskMsg();
            AskParams askParams=new AskParams();
            askParams.setAuth("authToken");
            askMsg.setParams(askParams);
            bootstrap.socketChannel.writeAndFlush(askMsg);
        }
    }
}
