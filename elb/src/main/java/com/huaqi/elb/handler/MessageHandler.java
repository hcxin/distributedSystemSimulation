package com.huaqi.elb.handler;

import com.huaqi.elb.service.IMessageService;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * 类的功能描述：
 *
 * @ClassName: MessageHandler
 * @Author haichen
 * @Date 2020-07-01 01:14:46
 */
@Slf4j
@Component
@Sharable
public class MessageHandler extends ChannelInboundHandlerAdapter {
    private AttributeKey<String> SERVER_ID = AttributeKey.valueOf("serverID");

    @Autowired
    private IMessageService messageService;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if (Objects.nonNull(ctx.channel()) && ctx.channel().isActive()) {
            messageService.handleMessage(ctx, msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        super.channelUnregistered(ctx);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);

    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        try {
            String serverID = ctx.channel().attr(SERVER_ID).get();
            log.info("MessageHandler: channelInactive: start logout: serverID is :[" + serverID + "]");
        } catch (Exception e) {
            e.printStackTrace();
        }
        messageService.logout(ctx);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        super.userEventTriggered(ctx, evt);
    }

    @Override
    public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
        super.channelWritabilityChanged(ctx);
    }
}
