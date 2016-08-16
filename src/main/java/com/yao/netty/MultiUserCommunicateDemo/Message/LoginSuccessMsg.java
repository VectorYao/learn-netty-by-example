package com.yao.netty.MultiUserCommunicateDemo.Message;

/**
 * @author Yao
 * @create 2016/8/16
 */
public class LoginSuccessMsg extends BaseMsg {
    private long playerId;

    public LoginSuccessMsg() {
        setType(MsgType.LOGINSUCCESS);
    }

    public long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(long playerId) {
        this.playerId = playerId;
    }
}
