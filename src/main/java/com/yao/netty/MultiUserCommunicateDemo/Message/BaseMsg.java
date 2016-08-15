package com.yao.netty.MultiUserCommunicateDemo.Message;

import java.io.Serializable;

/**
 * 必须实现序列,serialVersionUID 一定要有
 */

public abstract class BaseMsg  implements Serializable {
    private static final long serialVersionUID = 1L;
    private MsgType type;
    //必须唯一，否者会出现channel调用混乱
    private long clientId;

    public BaseMsg() {
    }

    public long getClientId() {
        return clientId;
    }

    public void setClientId(long clientId) {
        this.clientId = clientId;
    }

    public MsgType getType() {
        return type;
    }

    public void setType(MsgType type) {
        this.type = type;
    }
}
