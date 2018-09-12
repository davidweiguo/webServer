package com.fdt.appserver.spring;

import akka.actor.Extension;
import akka.actor.Props;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * @author guo_d
 * @date 2018/09/03
 */
@Component("springExt")
public class SpringExt implements Extension, ApplicationContextAware {

    private ApplicationContext applicationContext;

    public Props props(String actorBeanName, Object... args) {
        return Props.create(SpringActorProducer.class, applicationContext, actorBeanName, args);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
