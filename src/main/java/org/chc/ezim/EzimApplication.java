package org.chc.ezim;

import jakarta.annotation.Resource;
import org.chc.ezim.websocket.netty.NettyWebsocketStarter;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableAsync
@EnableScheduling
@MapperScan({"org.chc.ezim.mapper"})
@SpringBootApplication
public class EzimApplication {

    @Resource
    private NettyWebsocketStarter nettyWebsocketStarter;

    public static void main(String[] args) {
        SpringApplication.run(EzimApplication.class, args);
    }

    @Bean
    public ApplicationRunner runner() {
        return args -> {
            new Thread(nettyWebsocketStarter).start();
        };
    }
}
