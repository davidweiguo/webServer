package com.fdt.appserver.spring;

import akka.actor.ActorSystem;
import com.typesafe.config.ConfigFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.io.File;

/**
 * @author guo_d
 * @date 2018/09/03
 */
@Configuration
@ComponentScan
public class AppConfiguration {
    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private SpringExt sprintExt;

    @Bean("actorSystem")
    public ActorSystem actorSystem() {
        ActorSystem actorSystem = ActorSystem.create("fdtapp", ConfigFactory.parseFile(new File("conf/akka.conf")));
        sprintExt.setApplicationContext(applicationContext);
        return actorSystem;
    }
}
