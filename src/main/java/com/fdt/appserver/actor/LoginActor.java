package com.fdt.appserver.actor;

import akka.actor.AbstractActor;
import com.fdt.appserver.nio.WebSocketConnectionManager;
import com.fdt.appserver.service.AccountService;
import com.google.gson.annotations.Expose;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * @author guo_d
 * @date 2018/09/03
 */
@Component("login")
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class LoginActor extends AbstractActor {

    private static final Logger log = LoggerFactory.getLogger(LoginActor.class);

    @Autowired
    private ActorDispatcher actorDispatcher;

    @Autowired
    private AccountService accountService;

    @Autowired
    private WebSocketConnectionManager connectionManager;

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(LoginRequest.class, this::onLoginRequest)
                .match(LoginResponse.class, this::onLoginResponse)
                .build();
    }

    private void onLoginRequest(LoginRequest request) {
        accountService.login(request);
    }

    private void onLoginResponse(LoginResponse response) {
        log.info("Login Response, User: {}, Ok: {}", response.getUserId(), response.isOk());
        if (response.isOk()) {
            connectionManager.assignUserToConnection(response.getUserId(), response.getConnectionId());
        }
        UpstreamAgentActor.ClientResponse clientResponse = new UpstreamAgentActor.ClientResponse(
                ResponseType.REPLY, response, response.getConnectionId(), null);
        actorDispatcher.sendClientResponse(clientResponse);
    }

    public static class LoginRequest extends BaseData {
        @Expose
        private String token;

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }
    }

    public static class LoginResponse extends BaseResponse {

        @Expose
        private String pt = "login";

        private String userId;

        public LoginResponse(String connectionId, boolean ok, String msg) {
            super(connectionId, ok, msg);
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }
    }
}
