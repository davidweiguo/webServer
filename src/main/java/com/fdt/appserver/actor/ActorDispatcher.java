package com.fdt.appserver.actor;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import com.fdt.appserver.spring.SpringExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author guo_d
 * @date 2018/09/03
 */
@Component("actorDispatcher")
public class ActorDispatcher {

    @Autowired
    private SpringExt springExt;

    @Autowired
    private ActorSystem actorSystem;

    public void sendPing(ShakeHandActor.Ping ping) {
        sendMsg("shakeHand", ping);
    }

    public void sendClientRequest(UpstreamAgentActor.ClientRequest request) {
        sendMsg("upstreamAgent", request);
    }

    public void sendClientResponse(UpstreamAgentActor.ClientResponse response) {
        sendMsg("upstreamAgent", response);
    }

    public void sendLoginRequest(LoginActor.LoginRequest request) {
        sendMsg("login", request);
    }

    public void sendLoginResponse(LoginActor.LoginResponse response) {
        sendMsg("login", response);
    }

    public void sendEnterOrderRequest(EnterOrderActor.EnterOrderRequest request) {
        sendMsg("enterOrder", request);
    }

    private void sendMsg(String actorName, Object msg) {
        ActorRef ref = actorSystem.actorOf(springExt.props(actorName));
        ref.tell(msg, ActorRef.noSender());
    }
}
