package com.fdt.appserver.actor;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 所有点对点相应的类都需要继承自该类
 *
 * @author guo_d
 * @date 2018/9/4
 */
public abstract class BaseResponse extends BaseData {

    @SerializedName("ok")
    @Expose
    private boolean ok;

    @Expose
    @SerializedName("msg")
    private String msg;

    public BaseResponse(String connectionId, boolean ok, String msg) {
        setConnectionId(connectionId);
        this.ok = ok;
        this.msg = msg;
    }

    public boolean isOk() {
        return ok;
    }

    public String getMsg() {
        return msg;
    }
}
