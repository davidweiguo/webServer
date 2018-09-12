package com.fdt.appserver.actor;

import akka.actor.AbstractActor;
import com.fdt.appserver.nio.WebSocketConnectionManager;
import com.google.gson.annotations.Expose;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * @author guo_d
 * @date 2018/09/03
 */
@Component("shakeHand")
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ShakeHandActor extends AbstractActor {

    private static final Logger log = LoggerFactory.getLogger(ShakeHandActor.class);

    @Autowired
    private ActorDispatcher actorDispatcher;

    @Autowired
    private WebSocketConnectionManager connectionManager;

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(Ping.class, this::receivePing)
                .build();
    }

    private void receivePing(Ping ping) {
        log.debug("Received Ping, ConnectionId: {}, Time: {}", ping.getConnectionId(), ping.getNow());
        connectionManager.refreshConnectionAliveTime(ping.getConnectionId(), ping.getNow().getTime());
        UpstreamAgentActor.ClientResponse response = new UpstreamAgentActor.ClientResponse(
                ResponseType.REPLY, new Pong(), ping.getConnectionId(), null);
        actorDispatcher.sendClientResponse(response);
    }

    public static class Ping extends BaseData {
        private Date now;

        public Ping() {
            this.now = new Date();
        }

        public Date getNow() {
            return now;
        }
    }

    public static class Pong extends BaseData {

        @Expose
        private String pt = "pong";

        private String time;

        public Pong() {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");
            LocalDateTime localDateTime = LocalDateTime.now();
            time = localDateTime.format(formatter);
        }

        public String getTime() {
            return time;
        }
    }
}
