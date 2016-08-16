package com.yao.netty.MultiUserCommunicateDemo.Message;

import io.netty.util.AttributeKey;

/**
 * @author Yao
 * @create 2016/8/16
 */
public class CTXAttr {
    public final static AttributeKey<Long> PLAYERID = AttributeKey.valueOf("playerId");
}
