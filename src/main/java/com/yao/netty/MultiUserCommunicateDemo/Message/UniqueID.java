package com.yao.netty.MultiUserCommunicateDemo.Message;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Yao
 * @create 2016/8/15
 */
public class UniqueID {
    private static AtomicLong addId = new AtomicLong(0);
    private static long factor = 0L;
    private static boolean inited = false;

    public static void initFactor() {
        long serverId = 1<<54;
        long time = System.currentTimeMillis();
        time <<= 24;
        time &= 0x003FFFFFFFFFFFFFL;
        factor = serverId + time;
        inited = true;
    }

    //10 bit serverid,30 bit time, 24 bit addid
    public static long genUniqueID() {
        if (!inited) {
            initFactor();
        }

        long addId = UniqueID.addId.incrementAndGet();
        addId &= 0x0000000000FFFFFFL;

        return factor + addId;
    }
}
