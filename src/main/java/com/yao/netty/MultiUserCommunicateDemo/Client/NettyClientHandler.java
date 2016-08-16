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
    protected void messageReceived(ChannelHandlerContext ctx, BaseMsg baseMsg) throws Exception {
        MsgType msgType=baseMsg.getType();
        switch (msgType){
            case LOGINSUCCESS:{
                LoginSuccessMsg lsMsg = (LoginSuccessMsg)baseMsg;
                ctx.channel().attr(CTXAttr.PLAYERID).set(lsMsg.getPlayerId());
                break;
            }
            case PING:{
                System.out.println("receive ping from server----------");
            }break;
            case ASK:{
                ReplyBody replyBody=new ReplyBody("Client ["+ctx.channel().attr(CTXAttr.PLAYERID).get()+"] 已成功接收到服务器端的ASK消息");
                ReplyMsg replyMsg=new ReplyMsg();
                replyMsg.setBody(replyBody);
                ctx.writeAndFlush(replyMsg);
                break;
            }
            case REPLY:{
                ReplyMsg replyMsg=(ReplyMsg)baseMsg;
                ReplyBody replyServerBody=replyMsg.getBody();
                System.out.println(new Timestamp(System.currentTimeMillis()).toString()+"："+replyServerBody.getReplyInfo());
                break;
            }
        }
        ReferenceCountUtil.release(msgType);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception{
        System.out.println("Server 已关闭！");
        ctx.close();
    }
}
