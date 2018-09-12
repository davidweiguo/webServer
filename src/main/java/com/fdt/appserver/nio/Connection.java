package com.fdt.appserver.nio;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;

import java.net.InetSocketAddress;

/**
 * @author guo_d
 * @date 2018/09/03
 */
class Connection {

    private Channel channel;

    private String id;

    private String remoteIp;

    private int remotePort;

    private String userId;

    private long lastUpdatedTime;

    public Connection(Channel channel) {
        this.channel = channel;
        this.id = this.channel.id().asLongText();
        InetSocketAddress address = (InetSocketAddress) this.channel.remoteAddress();
        this.remoteIp = address.getAddress().getHostAddress();
        this.remotePort = address.getPort();
        this.lastUpdatedTime = System.currentTimeMillis();
    }

    public void sendData(String gsonStr) {
        ByteBuf byteBuf = ByteBufAllocator.DEFAULT.buffer();
        byteBuf.writeBytes(gsonStr.getBytes());
        channel.writeAndFlush(new BinaryWebSocketFrame(byteBuf));
    }

    public String getId() {
        return this.id;
    }

    public String getRemoteIp() {
        return remoteIp;
    }

    public int getRemotePort() {
        return remotePort;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public long getLastUpdatedTime() {
        return lastUpdatedTime;
    }

    public void setLastUpdatedTime(long lastUpdatedTime) {
        this.lastUpdatedTime = lastUpdatedTime;
    }
}
