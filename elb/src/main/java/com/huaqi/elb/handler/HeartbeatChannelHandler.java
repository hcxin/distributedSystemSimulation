package com.huaqi.elb.handler;

import com.huaqi.common.constant.CMDConst;
import com.huaqi.common.msg.Base;
import com.huaqi.common.util.MessageUtil;
import com.huaqi.elb.service.IMessageService;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Sharable
public class HeartbeatChannelHandler extends ChannelInboundHandlerAdapter {
    private AttributeKey<Integer> SERVER_ID = AttributeKey.valueOf("serverID");

    @Autowired
    private IMessageService messageService;

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        super.userEventTriggered(ctx, evt);
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state().equals(IdleState.READER_IDLE)) {

            } else if (event.state().equals(IdleState.WRITER_IDLE)) {

            } else if (event.state().equals(IdleState.ALL_IDLE)) {
                try {
                    Integer serverID = ctx.channel().attr(SERVER_ID).get();
                    log.info("HeartbeatChannelHandler: userEventTriggered: ALL_IDLE serverID is:[" + serverID + "]");
                } catch (Exception e) {
                    log.error("HeartbeatChannelHandler: userEventTriggered: ALL_IDLE hit exception" + e);
                }
                //未检测到心跳 踢下线
                messageService.logout(ctx);
                ChannelFuture future = ctx.writeAndFlush(MessageUtil.getMsg(new Base(CMDConst.CMD_CLOSE)));
                future.addListener((ChannelFutureListener) channelFuture -> ctx.channel().close());
            }
        }
    }

}