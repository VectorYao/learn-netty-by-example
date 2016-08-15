package com.yao.netty.MultiUserCommunicateDemo.Client;

import com.yao.netty.MultiUserCommunicateDemo.Message.*;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;

import java.sql.Timestamp;

/**
 * 客户端的事件处理
 */
public class NettyClientHandler extends SimpleChannelInboundHandler<BaseMsg> {
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent e = (IdleStateEvent) evt;
            switch (e.state()) {
                case WRITER_IDLE://写超时，即一段时间内客户端没有数据发送。采用心跳机制让服务器唤醒客户端
                    PingMsg pingMsg=new PingMsg();
                    ctx.writeAndFlush(pingMsg);
                    System.out.println("send ping to server----------");
                    break;
                default:
                    break;
            }
        }
    }
    @Override
    protected void messageReceived(ChannelHandlerContext channelHandlerContext, BaseMsg baseMsg) throws Exception {
        MsgType msgType=baseMsg.getType();
        System.out.println("Receive Server Message Type is " + msgType.toString());
        switch (msgType){
            case LOGIN:{
                //用于超时或掉线时，服务器发起登录消息让客户端重连
                LoginMsg loginMsg=new LoginMsg();
                loginMsg.setClientId(baseMsg.getClientId());
                loginMsg.setUserName("yao");
                loginMsg.setPassword("123456");
                channelHandlerContext.writeAndFlush(loginMsg);
                break;
            }
            case PING:{
                System.out.println("receive ping from server----------");
            }break;
            case ASK:{
                AskMsg ask = (AskMsg)baseMsg;
                ReplyBody replyBody=new ReplyBody("Client: "+ask.getParams().getAuth()+"Reply Ask commond");
                ReplyMsg replyMsg=new ReplyMsg();
                replyMsg.setBody(replyBody);
                channelHandlerContext.writeAndFlush(replyMsg);
                break;
            }
            case REPLY:{
                ReplyMsg replyMsg=(ReplyMsg)baseMsg;
                ReplyBody replyServerBody=replyMsg.getBody();
                System.out.println("Client "+String.valueOf(baseMsg.getClientId())+new Timestamp(System.currentTimeMillis()).toString()+"receive Server msg: "+replyServerBody.getReplyInfo());
                break;
            }
        }
        ReferenceCountUtil.release(msgType);
    }
}
