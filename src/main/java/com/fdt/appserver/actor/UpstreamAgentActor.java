package com.fdt.appserver.actor;

import akka.actor.AbstractActor;
import com.fdt.appserver.WebSocketMsgUtil;
import com.fdt.appserver.nio.WebSocketConnectionManager;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
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
@Component("upstreamAgent")
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class UpstreamAgentActor extends AbstractActor {

    private static final Logger log = LoggerFactory.getLogger(UpstreamAgentActor.class);

    @Autowired
    private ActorDispatcher actorDispatcher;

    @Autowired
    private WebSocketConnectionManager connectionManager;

    public UpstreamAgentActor() {
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(ClientRequest.class, this::onClientRequest)
                .match(ClientResponse.class, this::onClientResponse)
                .build();
    }

    private void onClientRequest(ClientRequest request) {
        switch (request.getType()) {
            case PING:
                ShakeHandActor.Ping ping = new ShakeHandActor.Ping();
                ping.setConnectionId(request.getConnectionId());
                actorDispatcher.sendPing(ping);
                break;
            case LOGIN:
                LoginActor.LoginRequest login = (LoginActor.LoginRequest) WebSocketMsgUtil
                        .gsonToClass(request.getType().getRequestClass(), request.getGson());
                login.setConnectionId(request.getConnectionId());
                actorDispatcher.sendLoginRequest(login);
                break;
            case ENTER_ORDER:
                EnterOrderActor.EnterOrderRequest enterOrder = (EnterOrderActor.EnterOrderRequest) WebSocketMsgUtil
                        .gsonToClass(request.getType().getRequestClass(), request.getGson());
                enterOrder.setConnectionId(request.getConnectionId());
                actorDispatcher.sendEnterOrderRequest(enterOrder);
                break;
            default:
                break;
        }
    }

    private void onClientResponse(ClientResponse response) {
        String gsonStr = WebSocketMsgUtil.classToGson(response.responseObject);
        log.info("Response, ConnectionId: {}, Gson: {}", response.getConnectionId(), gsonStr);
        switch (response.type) {
            case REPLY:
                connectionManager.sentData(response.getConnectionId(), gsonStr);
                break;
            case PUBLISH:
                connectionManager.publishData(response.getUser(), gsonStr);
                break;
            default:
                break;
        }
    }

    public static class ClientRequest extends BaseData {
        
        @SerializedName("request")
        @Expose
        private String request;

        private RequestType type;

        private String gson;

        public String getRequest() {
            return request;
        }

        public void setRequest(String request) {
            this.request = request;
        }

        public RequestType getType() {
            return type;
        }

        public void setType(RequestType type) {
            this.type = type;
        }

        public String getGson() {
            return gson;
        }

        public void setGson(String gson) {
            this.gson = gson;
        }
    }

    /**
     * 当 ResponseType 为 REPLY 时，表示对请求的相应，需要提供 ConnectionId；
     * 而当 ResponseType 为 PUBLISH 时，需要提供 User，从而获取所有连接进行信息发布
     */
    public static class ClientResponse extends BaseData {
        private String user;

        private ResponseType type;

        private BaseData responseObject;

        public ClientResponse(ResponseType type, BaseData responseObject, String connectionId, String user) {
            this.type = type;
            this.responseObject = responseObject;
            setConnectionId(connectionId);
            setUser(user);
        }

        public String getUser() {
            return user;
        }

        public void setUser(String user) {
            this.user = user;
        }

        public ResponseType getType() {
            return type;
        }

        public void setType(ResponseType type) {
            this.type = type;
        }

        public BaseData getResponseObject() {
            return responseObject;
        }

        public void setResponseObject(BaseData responseObject) {
            this.responseObject = responseObject;
        }
    }
}