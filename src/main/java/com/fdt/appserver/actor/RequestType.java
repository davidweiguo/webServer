package com.fdt.appserver.actor;

/**
 * @author guo_d
 * @date 2018/09/03
 */
public enum RequestType {

    /**
     * 用于将接收前端发送来的请求映射到具体的业务处理对象（Actor）
     */
    PING("ping", ShakeHandActor.Ping.class),
    LOGIN("login", LoginActor.LoginRequest.class),
    ENTER_ORDER("/v1/order/new", EnterOrderActor.EnterOrderRequest.class);

    private String uri;

    private Class<? extends BaseData> requestClass;

    RequestType(String uri, Class<? extends BaseData> requestClass) {
        this.uri = uri;
        this.requestClass = requestClass;
    }

    public String getUri() {
        return uri;
    }

    public Class<? extends BaseData> getRequestClass() {
        return requestClass;
    }

    public static RequestType getRequestType(String uri) {
        for (RequestType e : RequestType.values()) {
            if (e.getUri().equals(uri)) {
                return e;
            }
        }
        return null;
    }
}
