package com.huaqi.eas;

import com.huaqi.eas.client.ELBClient;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class EasApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(EasApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        ELBClient elbClient = new ELBClient();
        elbClient.start();
    }
}
