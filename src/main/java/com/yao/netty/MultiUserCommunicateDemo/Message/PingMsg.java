package com.yao.netty.MultiUserCommunicateDemo.Message;

/**
 * 心跳检测的消息类型
 */
public class PingMsg extends BaseMsg {
    public PingMsg() {
        setType(MsgType.PING);
    }
}
