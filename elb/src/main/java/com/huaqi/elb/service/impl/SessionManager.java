package com.huaqi.elb.service.impl;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.huaqi.common.constant.CMDConst;
import com.huaqi.common.msg.AppEngine;
import com.huaqi.common.msg.Base;
import com.huaqi.common.msg.EAS;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentMap;

/**
 * 类的功能描述：
 * 连接缓存
 *
 * @ClassName: SessionManager
 * @Author haichen
 * @Date 2020-07-01 07:00:35
 */
@Slf4j
@Service
public class SessionManager {
    private static Gson gson = new Gson();
    private ThreadLocal<Long> loginTime = new ThreadLocal<Long>();
    private ThreadLocal<Long> logoutTime = new ThreadLocal<Long>();

    private static final ConcurrentMap<Integer, EAS> sessionPool = Maps.newConcurrentMap();//key：serverID，value：EAS
    private AttributeKey<Integer> SERVER_ID = AttributeKey.valueOf("serverID");

    public void login(ChannelHandlerContext ctx, EAS eas) {
        Channel channel = ctx.channel();
        Integer serverID = eas.getServerID();
        loginTime.set(System.nanoTime());
        log.info("SessionManager:login: serverID is: " + serverID);
        SocketAddress socketAddress = channel.remoteAddress();
        if (Objects.nonNull(socketAddress) && socketAddress instanceof InetSocketAddress) {
            InetSocketAddress inetSocketAddress = (InetSocketAddress) socketAddress;
            if (Objects.nonNull(inetSocketAddress.getAddress())) {
                String hostName = inetSocketAddress.getAddress().getHostName();
                String ip = inetSocketAddress.getAddress().getHostAddress();
                int port = inetSocketAddress.getPort();
                log.info("SessionManager: login:  client info :  hostName: [" + hostName + "], ip: [" + ip + "], port: [" + port + "]");
            }
        }
        channel.attr(SERVER_ID).set(serverID);
        sessionPool.put(serverID, eas);

}

    public void logout(ChannelHandlerContext ctx) {
            try {
                logoutTime.set(System.nanoTime());
                try {
                    ChannelFuture future = ctx.writeAndFlush(new Base(CMDConst.CMD_CLOSE));
                    future.addListener((ChannelFutureListener) channelFuture -> ctx.channel().close());
                } catch (Exception e) {
                    log.error("SessionManager: logout future close: hit exception " + e);
                    e.printStackTrace();
                }

                Integer serverID = ctx.channel().attr(SERVER_ID).get();
                if (Objects.isNull(serverID)){
                    return;
                }
                log.info("SessionManager: start logout: serverID is :[" + serverID + "]");
                sessionPool.remove(serverID);
            } catch (Exception e) {
                log.error("SessionManager: logout: hit exception "+ e);
                e.printStackTrace();
            }
    }

    public ConcurrentMap<Integer, EAS> getSessionPool() {
        return sessionPool;
    }

    public List<AppEngine> getAllServerList() {
        List<AppEngine> list = new ArrayList<>();
        for (EAS eas :sessionPool.values()){
            list.addAll(eas.getAppEngineList());
        }
        return list;
    }
    /**
     * 获取serverID
     */
    public Integer getServerID(Channel channel) {
        Integer serverID = channel.attr(SERVER_ID).get();
        return serverID;
    }

    /**
     * 根据serverID获取Channel
     */
    public Channel getChannelByServerID(Integer serverID) {
        if (Objects.nonNull(sessionPool.get(serverID))) {
            return sessionPool.get(serverID).getChannel();
        } else {
            return null;
        }
    }

    public void updateInfo(ChannelHandlerContext ctx, EAS eas) {
        Channel channel = ctx.channel();
        Integer serverID = eas.getServerID();
        loginTime.set(System.nanoTime());
        log.info("SessionManager:updateInfo: serverID is: " + serverID);
        sessionPool.put(serverID, eas);

    }
}