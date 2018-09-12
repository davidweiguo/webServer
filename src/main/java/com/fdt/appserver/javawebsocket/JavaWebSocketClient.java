package com.fdt.appserver.javawebsocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;

@ClientEndpoint
public class JavaWebSocketClient {

    private static final Logger log = LoggerFactory.getLogger(JavaWebSocketClient.class);

    private static final String URI_STR = "ws://localhost:8080/websocket/davidguo";

    private Session session;

    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        log.info("Client connection open");
    }

    @OnMessage
    public void onMessage(String message) {
        log.info("Client received message: {}", message);
        try {
            session.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @OnClose
    public void onClose() {
        log.info("Client connection closed");
    }

    private void start() {
        WebSocketContainer container = null;
        try {
            container = ContainerProvider.getWebSocketContainer();
            session = container.connectToServer(JavaWebSocketClient.class, URI.create(URI_STR));
        } catch (DeploymentException | IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage() {
        try {
            session.getBasicRemote().sendText("Hello World");
            Thread.sleep(1000);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        JavaWebSocketClient client = new JavaWebSocketClient();
        client.start();
        client.sendMessage();
    }
}
