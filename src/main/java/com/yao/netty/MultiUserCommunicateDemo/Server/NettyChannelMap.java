package com.yao.netty.MultiUserCommunicateDemo.Server;

import io.netty.channel.socket.SocketChannel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 服务器端存储的<客户端id，客户端channel>映射管理
 */
public class NettyChannelMap {
    private  static Map<Long,SocketChannel> map=new ConcurrentHashMap<Long, SocketChannel>();
    public static void add(long clientId,SocketChannel socketChannel){
        map.put(clientId,socketChannel);
    }
    public static SocketChannel get(long clientId){
       return map.get(clientId);
    }
    public static void remove(SocketChannel socketChannel){
        for (Map.Entry entry:map.entrySet()){
            if (entry.getValue()==socketChannel){
                map.remove(entry.getKey());
            }
        }
    }
    public static List<Long> getAllKeys(){
        List<Long> list = new ArrayList<Long>();
        for (long key:map.keySet()){
            list.add(key);
        }
        return list;
    }

}
