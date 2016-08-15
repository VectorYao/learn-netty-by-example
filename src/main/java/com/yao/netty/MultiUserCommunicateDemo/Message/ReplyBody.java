package com.yao.netty.MultiUserCommunicateDemo.Message;

import java.io.Serializable;

/**
 * 消息回复实体
 */
public class ReplyBody implements Serializable {
    private static final long serialVersionUID = 1L;

    private String replyInfo;

    public ReplyBody(String info) {
        this.replyInfo = info;
    }

    public String getReplyInfo() {
        return replyInfo;
    }

    public void setReplyInfo(String replyInfo) {
        this.replyInfo = replyInfo;
    }

}
