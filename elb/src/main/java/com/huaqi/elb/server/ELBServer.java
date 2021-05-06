package com.huaqi.elb.server;

import com.huaqi.elb.handler.HeartbeatChannelHandler;
import com.huaqi.elb.handler.MessageHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.json.JsonObjectDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.util.Objects;
import java.util.concurrent.TimeUnit;


/**
 * 类的功能描述：
 * netty服务端
 * @ClassName: ELBServer
 * @Author haichen
 * @Date 2020-07-01 01:14:46
 */
@Slf4j
@Component
public final class ELBServer {

    @Autowired
    private MessageHandler messageHandler;

    @Autowired
    private HeartbeatChannelHandler heartbeatChannelHandler;

    private final EventLoopGroup bossGroup = new NioEventLoopGroup(2);
    private final EventLoopGroup workerGroup = new NioEventLoopGroup(4);
    private final EventLoopGroup businessGroup = new NioEventLoopGroup();
    private Channel channel;

    public ChannelFuture run(InetSocketAddress address, boolean ssl) throws Exception {
        // Configure SSL.
        ChannelFuture future = null;
        final SslContext sslCtx;
        if (ssl) {
            SelfSignedCertificate ssc = new SelfSignedCertificate();
            sslCtx = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey()).build();
        } else {
            sslCtx = null;
        }

        try {
            StringEncoder stringEncoder = new StringEncoder();
            StringDecoder stringDecoder = new StringDecoder();
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class) .option(ChannelOption.SO_BACKLOG, 1024).option(ChannelOption.TCP_NODELAY, true).childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline p = ch.pipeline();
                            if (Objects.nonNull(sslCtx)) {
                                p.addLast(sslCtx.newHandler(ch.alloc()));
                            }
                            p.addLast(new JsonObjectDecoder());
                            p.addLast(stringDecoder);
                            p.addLast(stringEncoder);
                            p.addLast(new IdleStateHandler(15, 15, 30, TimeUnit.SECONDS));
                            p.addLast(heartbeatChannelHandler);
                            p.addLast(businessGroup, messageHandler);
                        }
                    });

            // Start the server.
            future = b.bind(address.getPort()).sync();
            channel = future.channel();
        } finally {
            if (future != null && future.isSuccess()) {
                log.info("netty server listening " + address.getHostName() + " on port " + address.getPort() + " and ready for connections...");
            } else {
                log.error("netty server start up Error!");
            }
        }

        return future;
    }

    public void destroy() {
        log.info("Shutdown Netty Server...");
        if (channel != null) {
            channel.close();
        }
        workerGroup.shutdownGracefully();
        bossGroup.shutdownGracefully();
        businessGroup.shutdownGracefully();
        log.info("Shutdown Netty Server Success!");
    }
}
