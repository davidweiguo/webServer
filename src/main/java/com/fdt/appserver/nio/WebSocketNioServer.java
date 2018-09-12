package com.fdt.appserver.nio;

import com.fdt.appserver.WebSocketMsgUtil;
import com.fdt.appserver.actor.ActorDispatcher;
import com.fdt.appserver.actor.UpstreamAgentActor;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author guo_d
 * @date 2018/09/03
 */
@Component("webSocketServer")
public class WebSocketNioServer extends BaseNioServer {
    private static final Logger log = LoggerFactory.getLogger(WebSocketNioServer.class);

    private final String REQUEST_PROTOCAL = "websocket";
    private final String REQUEST_PROTOCAL_TYPE = "Upgrade";
    private final int HTTP_GOOD_STATUS = 200;

    @Autowired
    private ActorDispatcher actorDispatcher;

    @Autowired
    private WebSocketConnectionManager connectionManager;

    private WebSocketServerHandshaker handshaker;

    @Override
    public void start() {
        super.init(new WebSocketServiceHandler(), log);
    }

    @Override
    protected void addPipelineLast(ChannelPipeline pipeline) {
        pipeline.addLast("http-chunked", new ChunkedWriteHandler());
    }

    @ChannelHandler.Sharable
    private class WebSocketServiceHandler extends SimpleChannelInboundHandler<Object> {

        @Override
        public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
            connectionManager.addConnection(ctx.channel());
        }

        @Override
        public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
            connectionManager.removeConnection(ctx.channel());
            ctx.channel().close();
        }

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, Object msg) {
            // HTTP Request
            if (msg instanceof FullHttpRequest) {
                handleHttpRequest(ctx, (FullHttpRequest) msg);
            }
            // WebSocket Request
            else if (msg instanceof WebSocketFrame) {
                handleWebSocketFrame(ctx, (WebSocketFrame) msg);
            }
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
            log.info(cause.getMessage());
            connectionManager.removeConnection(ctx.channel());
            ctx.channel().close();
        }
    }

    private void handleHttpRequest(ChannelHandlerContext ctx, FullHttpRequest req) {
        // Failed to decode
        if (req.decoderResult().isFailure() || !REQUEST_PROTOCAL.equalsIgnoreCase(req.headers().get(REQUEST_PROTOCAL_TYPE))) {
            sendHttpResponse(new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST));
            log.warn("Failed to decode or Not WebSocket connection!");
            return;
        }

        // Handshake
        WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(
                "ws://" + req.headers().get("Host") + req.uri(), null, false);
        handshaker = wsFactory.newHandshaker(req);
        if (handshaker == null) {
            WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
        } else {
            handshaker.handshake(ctx.channel(), req);
        }
    }

    private void sendHttpResponse(FullHttpResponse res) {
        if (res.status().code() != HTTP_GOOD_STATUS) {
            ByteBuf buf = Unpooled.copiedBuffer(res.status().toString(), CharsetUtil.UTF_8);
            res.content().writeBytes(buf);
            buf.release();
        }
    }

    private void handleWebSocketFrame(ChannelHandlerContext ctx, WebSocketFrame frame) {
        // Close connection
        if (frame instanceof CloseWebSocketFrame) {
            handshaker.close(ctx.channel(), ((CloseWebSocketFrame) frame).retain());
            log.info("Closing WebSocket Connection");
            connectionManager.removeConnection(ctx.channel());
            ctx.channel().close();
            return;
        }
        // Ping
        if (frame instanceof PingWebSocketFrame) {
            ctx.channel().write(new PongWebSocketFrame(frame.content().retain()));
            return;
        }
        // Only support Text message
        if (!(frame instanceof TextWebSocketFrame)) {
            throw new UnsupportedOperationException(String.format("%s frame types not supported", frame.getClass().getName()));
        }
        // Accept Text message
        String request = ((TextWebSocketFrame) frame).text();
        if (log.isDebugEnabled()) {
            log.debug("Channel {} received {}", ctx.channel().id(), request);
        }

        // 将所有的请求转换为 ClientRequest，并发送给 UpstreamAgentActor 统一调度
        UpstreamAgentActor.ClientRequest requestObj = WebSocketMsgUtil.parseClientRequest(ctx.channel().id().asLongText(), request);
        if (requestObj != null) {
            actorDispatcher.sendClientRequest(requestObj);
        } else {
            log.warn("Invalid request from client: {}", request);
        }
    }

    @Override
    public String getServerName() {
        return "thread-netty-server-websocket";
    }
}
