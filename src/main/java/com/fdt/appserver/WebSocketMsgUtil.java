package com.fdt.appserver;

import com.fdt.appserver.actor.RequestType;
import com.fdt.appserver.actor.UpstreamAgentActor;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * @author guo_d
 * @date 2018/09/03
 */
public class WebSocketMsgUtil {
    private static Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

    public static <T> T gsonToClass(Class<T> c, String data) {
        return gson.fromJson(data, c);
    }

    public static <T> String classToGson(T t) {
        return gson.toJson(t);
    }

    public static UpstreamAgentActor.ClientRequest parseClientRequest(String connectionId, String gsonStr) {
        UpstreamAgentActor.ClientRequest request = WebSocketMsgUtil.gsonToClass(UpstreamAgentActor.ClientRequest.class, gsonStr);
        if (request == null || request.getRequest() == null) {
            return null;
        }
        request.setConnectionId(connectionId);
        RequestType type = RequestType.getRequestType(request.getRequest());
        if (type == null) {
            return null;
        }
        request.setType(type);
        request.setGson(gsonStr);
        return request;
    }
}
