package com.huaqi.eas.client;

import com.google.gson.Gson;
import com.huaqi.common.constant.CMDConst;
import com.huaqi.common.msg.AppEngine;
import com.huaqi.common.msg.Base;
import com.huaqi.common.msg.EAS;
import com.huaqi.common.util.MessageUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;


/**
 *  类的功能描述：
 *
 * @ClassName:  ELBClientHandler
 * @Author  haichen
 * @Date 2020-07-01 14:30:02
 */
@Slf4j
public class ELBClientHandler extends ChannelInboundHandlerAdapter {
    private Channel channel;
    private static Gson gson = new Gson();
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        this.channel = ctx.channel();
        EAS easMsg = getEASInfo();
        easMsg.setCmd(CMDConst.CMD_ACTIVE);
        ctx.writeAndFlush(gson.toJson(easMsg));

        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Base baseMsg = new Base(CMDConst.CMD_HEARTBEAT);
                channel.pipeline().writeAndFlush(gson.toJson(baseMsg));
            }

        }).start();

        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(30000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                EAS easInfoMsg = getEASInfo();
                log.info("sending... EAS info: "+ gson.toJson(easInfoMsg));
                channel.pipeline().writeAndFlush(gson.toJson(easInfoMsg));
            }

        }).start();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msgObj) {
        Base msg =  MessageUtil.decodeMsgBase(msgObj);

        //心跳
        if (msg.getCmd() == CMDConst.CMD_HEARTBEAT) {
            log.info("心跳.......");
            //连接成功
        } else if (msg.getCmd() == CMDConst.CMD_ACTIVE) {
            log.info("连接成功.......");
        }else if (msg.getCmd() == CMDConst.CMD_INFO){
            log.info("状态更新成功...");

        } else if (msg.getCmd() == CMDConst.CMD_CLOSE){
            log.info("关闭...");
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {

    }

    private EAS getEASInfo(){
        EAS easMsg = new EAS(CMDConst.CMD_INFO);
        easMsg.setServerID(111);
        fillEASInfo(easMsg);
        AppEngine engine1 = new AppEngine();
        engine1.setInstanceID("server1");
        engine1.setIp("127.0.0.1");
        engine1.setServer("EAS");
        engine1.setPort("8083");

        AppEngine engine2 = new AppEngine();
        engine2.setInstanceID("server2");
        engine2.setIp("127.0.0.1");
        engine2.setServer("EAS");
        engine2.setPort("8083");

        AppEngine engine3 = new AppEngine();
        engine3.setInstanceID("server3");
        engine3.setIp("127.0.0.1");
        engine3.setServer("EAS");
        engine3.setPort("8083");

        AppEngine engine4 = new AppEngine();
        engine4.setInstanceID("server4");
        engine4.setIp("127.0.0.1");
        engine4.setServer("EAS");
        engine4.setPort("8083");

        AppEngine engine5 = new AppEngine();
        engine5.setInstanceID("server5");
        engine5.setIp("127.0.0.1");
        engine5.setServer("EAS");
        engine5.setPort("8083");
        easMsg.setAppEngineList(Arrays.asList(engine1,engine2, engine3, engine4, engine5));
        return easMsg;
    }

    private void fillEASInfo(EAS eas){
            eas.setAvailableMemory(0.6f);
            eas.setAvailableCPU(21);
            eas.setHealthyScore(7);
    }
}
