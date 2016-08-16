package com.yao.netty.MultiUserCommunicateDemo.Server;

import com.yao.netty.MultiUserCommunicateDemo.Message.*;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.SocketChannel;
import io.netty.util.ReferenceCountUtil;

import java.sql.Timestamp;

/**
 * 服务器端事件处理
 */
public class NettyServerHandler extends ChannelHandlerAdapter {
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        NettyChannelMap.remove((SocketChannel) ctx.channel());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg){
        BaseMsg baseMsg = (BaseMsg)msg;
        long playerId = 0;

        if(! MsgType.LOGIN.equals(baseMsg.getType())){
            playerId = ctx.channel().attr(CTXAttr.PLAYERID).get();
        }

        switch (baseMsg.getType()){
            case LOGIN:{
                LoginMsg loginMsg=(LoginMsg)baseMsg;
                if("yao".equals(loginMsg.getUserName())&&"123456".equals(loginMsg.getPassword())){
                    long id = UniqueID.genUniqueID();
                    ctx.channel().attr(CTXAttr.PLAYERID).set(id);
                    NettyChannelMap.add(id,(SocketChannel) ctx.channel());
                    //发送给客户端登录成功的消息，返回分配的唯一id
                    LoginSuccessMsg lsMsg = new LoginSuccessMsg();
                    lsMsg.setPlayerId(id);
                    NettyChannelMap.get(id).writeAndFlush(lsMsg);
                    System.out.println("client ["+id+"] 登录成功");
                }
                break;
            }
            case PING:{
                PingMsg replyPing=new PingMsg();
                NettyChannelMap.get(playerId).writeAndFlush(replyPing);
                break;
            }
            case ASK:{
                AskMsg askMsg=(AskMsg)baseMsg;
                if("authToken".equals(askMsg.getParams().getAuth())){
                    ReplyBody replyBody=new ReplyBody("服务器已成功接收到client "+playerId+"的ASK消息!!!");
                    ReplyMsg replyMsg=new ReplyMsg();
                    replyMsg.setBody(replyBody);
                    NettyChannelMap.get(playerId).writeAndFlush(replyMsg);
                }
                break;
            }
            case REPLY:{
                ReplyMsg replyMsg=(ReplyMsg)baseMsg;
                ReplyBody clientBody=replyMsg.getBody();
                System.out.println(new Timestamp(System.currentTimeMillis()).toString()+"："+clientBody.getReplyInfo());
                break;
            }
        }
        ReferenceCountUtil.release(baseMsg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception{
        System.out.println("Client ["+ctx.channel().attr(CTXAttr.PLAYERID).get()+"] 已关闭！");
        NettyChannelMap.remove((SocketChannel) ctx.channel());
        ctx.close();
    }
}
