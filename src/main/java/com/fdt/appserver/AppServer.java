package com.fdt.appserver;

import com.fdt.appserver.actor.ActorDispatcher;
import com.fdt.appserver.bean.User;
import com.fdt.appserver.nio.BaseNioServer;
import com.fdt.appserver.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * @author guo_d
 * @date 2018/09/03
 */
@Configuration
@SpringBootApplication
public class AppServer {

    private static final Logger log = LoggerFactory.getLogger(AppServer.class);

    @Autowired
    private ActorDispatcher actorDispatcher;

    @Autowired
    private BaseNioServer webSocketServer;

    @Autowired
    private UserRepository userRepository;

    @Value("${websocket.port}")
    private int websocketPort;

    @PostConstruct
    public void init() {
        log.info("AppServer init...");
        // 1. Start WebSocketServer
        webSocketServer.setPort(websocketPort);
        webSocketServer.start();
        // 2. DB Jpa test
        List<User> users = userRepository.findAll();
        for (User user : users) {
            System.out.println(user.getId());
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(AppServer.class, args);
    }
}
