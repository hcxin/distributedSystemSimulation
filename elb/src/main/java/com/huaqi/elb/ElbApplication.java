package com.huaqi.elb;

import com.huaqi.elb.server.ELBServer;
import io.netty.channel.ChannelFuture;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.net.InetSocketAddress;

@SpringBootApplication
//ELB代理服务地址：http://localhost:8181/customer
//直接访问App Engine地址： http://localhost:8083/customer
public class ElbApplication implements CommandLineRunner {
    @Value("${netty.port}")
    private int port;

    @Value("${netty.url}")
    private String url;


    @Autowired
    private ELBServer elbServer;

    //mode =1 Random rule， mode =2 RoundRobin rule.
    private static Integer mode = 1;

    public static void main(String[] args) {
        SpringApplication.run(ElbApplication.class, args);
    }

    @Override
    public void run(String... strings) {
        InetSocketAddress address = new InetSocketAddress(url, port);
        ChannelFuture future = null;
        try {
            future = elbServer.run(address, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Runtime.getRuntime().addShutdownHook(new Thread(() -> elbServer.destroy()));
        future.channel().closeFuture().syncUninterruptibly();
    }

    public static Integer getMode() {
        return mode;
    }
}
