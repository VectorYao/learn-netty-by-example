package com.yao.netty.MultiUserCommunicateDemo.Message;

import java.io.Serializable;

/**
 * 必须实现序列,serialVersionUID 一定要有
 */

public abstract class BaseMsg  implements Serializable {
    private static final long serialVersionUID = 1L;
    private MsgType type;

    public MsgType getType() {
        return type;
    }

    public void setType(MsgType type) {
        this.type = type;
    }
}
