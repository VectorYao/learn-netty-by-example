package com.yao.netty.MultiUserCommunicateDemo.Server;

import com.yao.netty.MultiUserCommunicateDemo.Message.CTXAttr;
import io.netty.channel.socket.SocketChannel;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 服务器端存储的<客户端id，客户端channel>映射管理
 */
public class NettyChannelMap {
    private  static Map<Long,SocketChannel> map=new ConcurrentHashMap<Long, SocketChannel>();

    public static void add(long clientId, SocketChannel channel){
        map.put(clientId,channel);
    }

    public static SocketChannel get(long clientId){
       return map.get(clientId);
    }

    public static void remove(SocketChannel channel){
        map.remove(channel.attr(CTXAttr.PLAYERID).get());
    }

    public static Set<Long> getAllKeys(){
        return map.keySet();
    }

    public static void clear(){
        map.clear();
    }

}
