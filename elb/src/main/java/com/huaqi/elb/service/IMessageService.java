package com.huaqi.elb.service;

import io.netty.channel.ChannelHandlerContext;

public interface IMessageService {
    void handleMessage(ChannelHandlerContext ctx, Object msg);
    void logout(ChannelHandlerContext ctx);
}
