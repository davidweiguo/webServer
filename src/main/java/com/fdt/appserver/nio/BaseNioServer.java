package com.fdt.appserver.nio;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.string.StringDecoder;
import org.slf4j.Logger;

import javax.annotation.PreDestroy;
import java.util.concurrent.*;

/**
 * @author guo_d
 * @date 2018/09/03
 */
public abstract class BaseNioServer {

    private int port;

    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private ExecutorService exec;

    /**
     * 启动 NIO Server 程序，开始接收服务请求
     */
    public abstract void start();

    /**
     * 有子类实现，指定 Server 线程的名字
     *
     * @return 线程名字
     */
    public abstract String getServerName();

    protected void init(SimpleChannelInboundHandler<Object> handler, Logger log) {
        ThreadFactory factory = new ThreadFactoryBuilder().setNameFormat(getServerName()).build();
        exec = new ThreadPoolExecutor(1, 1, 0L,
                TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(1024), factory, new ThreadPoolExecutor.AbortPolicy());
        exec.execute(() -> {
            bossGroup = new NioEventLoopGroup();
            workerGroup = new NioEventLoopGroup();
            try {
                ServerBootstrap bs = new ServerBootstrap();
                bs.group(bossGroup, workerGroup)
                        .channel(NioServerSocketChannel.class)
                        .localAddress(port)
                        .childHandler(new ChannelInitializer<SocketChannel>() {
                            @Override
                            protected void initChannel(SocketChannel sc) {
                                ChannelPipeline cp = sc.pipeline();
                                cp.addLast(new HttpServerCodec());
                                cp.addLast(new HttpObjectAggregator(65536));
                                cp.addLast(new LineBasedFrameDecoder(1024));
                                cp.addLast(new StringDecoder());
                                cp.addLast(handler);
                                addPipelineLast(cp);
                            }
                        });
                Channel ch = bs.bind().sync().channel();
                ch.closeFuture().sync();
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            } finally {
                bossGroup.shutdownGracefully();
                workerGroup.shutdownGracefully();
            }
        });
    }

    /**
     * 根据需要增加配置项
     *
     * @param pipeline
     */
    protected abstract void addPipelineLast(ChannelPipeline pipeline);

    public void setPort(int port) {
        this.port = port;
    }

    @PreDestroy
    public void uninit() {
        if (bossGroup != null) {
            bossGroup.shutdownGracefully();
        }
        if (workerGroup != null) {
            workerGroup.shutdownGracefully();
        }
        if (exec != null) {
            exec.shutdown();
        }
    }
}
