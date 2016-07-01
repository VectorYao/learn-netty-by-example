package com.yao.netty.EchoHTTPDemo;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.util.CharsetUtil;
import io.netty.util.ResourceLeakDetector;

import java.net.InetSocketAddress;

import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * @author Yao
 * @create 2016/7/1
 */
public class EchoHTTPDemo {

    public ChannelFuture server(EventLoopGroup workerGroup) {
        ServerBootstrap b = new ServerBootstrap();
        b.group(workerGroup).channel(NioServerSocketChannel.class)
                //Setting InetSocketAddress to port 0 will assign one at random
                .localAddress(new InetSocketAddress(0))
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        //HttpServerCodec is a helper ChildHandler that encompasses
                        //both HTTP request decoding and HTTP response encoding
                        ch.pipeline().addLast(new HttpServerCodec());
                        //HttpObjectAggregator helps collect chunked HttpRequest pieces into
                        //a single FullHttpRequest. If you don't make use of streaming, this is
                        //much simpler to work with.
                        ch.pipeline().addLast(new HttpObjectAggregator(1048576));
                        //Finally add your FullHttpRequest handler. Real examples might replace this
                        //with a request router
                        ch.pipeline().addLast(new SimpleChannelInboundHandler<FullHttpRequest>() {
                            @Override
                            public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
                                ctx.flush();
                                //The close is important here in an HTTP request as it sets the Content-Length of a
                                //response body back to the client.
                                ctx.close();
                            }
                            @Override
                            protected void messageReceived(ChannelHandlerContext ctx, FullHttpRequest msg) throws Exception {
                                DefaultFullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK, msg.content().copy());
                                ctx.write(response);
                            }
                        });
                    }
                });

        // Start the server & bind to a random port.
        return b.bind();
    }

    public Bootstrap client() {
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        Bootstrap b = new Bootstrap();
        b.group(workerGroup).channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        //HttpClient codec is a helper ChildHandler that encompasses
                        //both HTTP response decoding and HTTP request encoding
                        ch.pipeline().addLast(new HttpClientCodec());
                        //HttpObjectAggregator helps collect chunked HttpRequest pieces into
                        //a single FullHttpRequest. If you don't make use of streaming, this is
                        //much simpler to work with.
                        ch.pipeline().addLast(new HttpObjectAggregator(1048576));
                        ch.pipeline().addLast(new SimpleChannelInboundHandler<FullHttpResponse>() {
                            @Override
                            protected void messageReceived(ChannelHandlerContext ctx, FullHttpResponse msg) throws Exception {
                                final String echo = msg.content().toString(CharsetUtil.UTF_8);
                                System.out.println("Response: {"+echo+"}");
                            }
                        });
                    }
                });
        return b;
    }

    public static void main(String[] args) throws Exception {
        //I find while learning Netty to keep resource leak detecting at Paranoid,
        //however *DO NOT* ship with this level.
        ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.PARANOID);
        EventLoopGroup serverWorkgroup = new NioEventLoopGroup();
        EventLoopGroup clientWorkgroup = new NioEventLoopGroup();
        EchoHTTPDemo app = new EchoHTTPDemo();
        try {
            Channel serverChannel = app.server(serverWorkgroup).sync().channel();
            int PORT = ((InetSocketAddress) serverChannel.localAddress()).getPort();
            System.err.println("Open your web browser and navigate to " +
                    "://127.0.0.1:" + PORT + '/');
            System.err.println("Echo back any TEXT with POST HTTP requests");

            Bootstrap clientBootstrap = app.client();
            final ByteBuf content = Unpooled.copiedBuffer("Hello World! EchoHTTP.", CharsetUtil.UTF_8);
            clientBootstrap
                    .connect(serverChannel.localAddress())
                    .addListener(new ChannelFutureListener() {
                        public void operationComplete(ChannelFuture future) throws Exception {
                            // Prepare the HTTP request.
                            HttpRequest request = new DefaultFullHttpRequest(
                                    HTTP_1_1, HttpMethod.POST, "/", content);
                            // If we don't set a content length from the client, HTTP RFC
                            // dictates that the body must be be empty then and Netty won't read it.
                            request.headers().set("Content-Length", content.readableBytes());
                            future.channel().writeAndFlush(request);
                        }
                    });

            serverChannel.closeFuture().sync();
        }finally {
            //Gracefully shutdown both event loop groups
            serverWorkgroup.shutdownGracefully();
            clientWorkgroup.shutdownGracefully();
        }
    }


}
