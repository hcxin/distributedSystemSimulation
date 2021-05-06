package com.huaqi.elb.service.impl;

import com.huaqi.common.constant.CMDConst;
import com.huaqi.common.msg.Base;
import com.huaqi.common.msg.EAS;
import com.huaqi.common.util.MessageUtil;
import com.huaqi.elb.service.IMessageService;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Slf4j
@Service
public class MessageServiceImpl implements IMessageService {
    private AttributeKey<Integer> SERVER_ID = AttributeKey.valueOf("serverID");
    @Autowired
    private SessionManager sessionManager;
    @Override
    public void handleMessage(ChannelHandlerContext ctx, Object msgObj) {
        Base msg =  MessageUtil.decodeMsgBase(msgObj);

        //心跳
        if (msg.getCmd() == CMDConst.CMD_HEARTBEAT) {
            log.info("心跳.......");
            ctx.writeAndFlush(MessageUtil.getMsg(new Base(CMDConst.CMD_HEARTBEAT)));
            //连接成功
        } else if (msg.getCmd() == CMDConst.CMD_ACTIVE) {
            log.info("客户端连接成功...");
            EAS eas = MessageUtil.decodeMsgEAS(msgObj);
            log.info("初始信息..."+ MessageUtil.getGson().toJson(eas));
            sessionManager.login(ctx, eas);
            ctx.writeAndFlush(MessageUtil.getMsg(new Base(CMDConst.CMD_ACTIVE)));

        }else if (msg.getCmd() == CMDConst.CMD_INFO){
            EAS eas = MessageUtil.decodeMsgEAS(msgObj);
            sessionManager.updateInfo(ctx, eas);
            log.info("接收并更新EAS信息..."+ MessageUtil.getGson().toJson(eas));
            ctx.writeAndFlush(MessageUtil.getMsg(new Base(CMDConst.CMD_INFO)));
        } else if (msg.getCmd() == CMDConst.CMD_CLOSE){
            //TODO
            log.info("关闭...");
        }

    }

    @Override
    public void logout(ChannelHandlerContext ctx) {
        try {
            ChannelFuture future = ctx.writeAndFlush(MessageUtil.getMsg(new Base(CMDConst.CMD_CLOSE)));
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
        sessionManager.getSessionPool().remove(serverID);
    }
}
