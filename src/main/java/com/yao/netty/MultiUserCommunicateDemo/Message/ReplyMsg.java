package com.yao.netty.MultiUserCommunicateDemo.Message;

/**
 * 回复消息类
 */
public class ReplyMsg extends BaseMsg {
    public ReplyMsg() {
        setType(MsgType.REPLY);
    }
    private ReplyBody body;

    public ReplyBody getBody() {
        return body;
    }

    public void setBody(ReplyBody body) {
        this.body = body;
    }
}
