package com.fdt.appserver.nio;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.*;

/**
 * @author guo_d
 * @date 2018/09/03
 */
@Component("connectionManager")
public class WebSocketConnectionManager {

    private static final Logger log = LoggerFactory.getLogger(WebSocketConnectionManager.class);

    private final int CONNECT_ALIVE_PERIOD_SECONDS = 60;

    /**
     * k = ConnectionId
     */
    private Map<String, Connection> activeConnectionMap = new ConcurrentHashMap<>();

    /**
     * k = UserId
     */
    private Map<String, List<Connection>> userConnectionMap = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        // 定时任务用于检查所有的 Connection 是否连接超时，如果超时，则进行删除操作
        ThreadFactory factory = new ThreadFactoryBuilder().setNameFormat("thread-connection-timer-check").build();
        ScheduledExecutorService exec = Executors.newScheduledThreadPool(1, factory);
        exec.scheduleAtFixedRate(factory.newThread(() -> {
            long currentTime = System.currentTimeMillis();
            for (Map.Entry<String, Connection> entry : activeConnectionMap.entrySet()) {
                if ((currentTime - entry.getValue().getLastUpdatedTime()) > CONNECT_ALIVE_PERIOD_SECONDS * 1000) {
                    removeConnection(entry.getKey());
                    log.warn("Remove Not Alive Connection, Id {}", entry.getKey());
                }
            }
        }), 0, CONNECT_ALIVE_PERIOD_SECONDS, TimeUnit.SECONDS);
    }

    public void addConnection(Channel channel) {
        Connection connection = new Connection(channel);
        activeConnectionMap.put(connection.getId(), connection);
        log.info("Add Connection, Id: {}", connection.getId());
    }

    public void removeConnection(Channel channel) {
        Connection connection = getConnection(channel);
        if (connection == null) {
            return;
        }
        removeConnection(connection.getId());
    }

    private void removeConnection(String connectionId) {
        Connection connection = getConnection(connectionId);
        activeConnectionMap.remove(connection.getId());
        if (connection.getUserId() == null || !userConnectionMap.containsKey(connection.getUserId())) {
            return;
        }
        userConnectionMap.get(connection.getUserId()).remove(connection);
    }

    public void assignUserToConnection(String userId, String connectionId) {
        if (!activeConnectionMap.containsKey(connectionId)) {
            return;
        }
        Connection connection = activeConnectionMap.get(connectionId);
        connection.setUserId(userId);
        userConnectionMap.computeIfAbsent(userId, k -> new ArrayList<>()).add(connection);
    }

    private Connection getConnection(Channel channel) {
        return getConnection(channel.id().asLongText());
    }

    private Connection getConnection(String connectionId) {
        return activeConnectionMap.get(connectionId);
    }

    public void sentData(String connectionId, String response) {
        Connection connection = this.getConnection(connectionId);
        if (connection != null) {
            connection.sendData(response);
        }
    }

    public void publishData(String userId, String response) {
        userConnectionMap.get(userId).forEach(connection -> connection.sendData(response));
    }

    public void refreshConnectionAliveTime(String connectionId, long time) {
        if (!activeConnectionMap.containsKey(connectionId)) {
            return;
        }
        activeConnectionMap.get(connectionId).setLastUpdatedTime(time);
    }
}
