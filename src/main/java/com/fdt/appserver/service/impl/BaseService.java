package com.fdt.appserver.service.impl;

import com.fdt.appserver.actor.ActorDispatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;

/**
 * @author guo_d
 * @date 2018/09/04
 */
public class BaseService {

    @Autowired
    protected ActorDispatcher actorDispatcher;
}
