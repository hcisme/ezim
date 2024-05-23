package org.chc.ezim.websocket.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import jakarta.annotation.PreDestroy;
import jakarta.annotation.Resource;
import org.chc.ezim.entity.config.AppConfigProperties;
import org.chc.ezim.entity.constants.Constants;
import org.chc.ezim.utils.StringTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class NettyWebsocketStarter implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(NettyWebsocketStarter.class);

    private static final EventLoopGroup bossGroup = new NioEventLoopGroup(1);

    private static final EventLoopGroup workGroup = new NioEventLoopGroup();

    @Resource
    private HandlerHeartBeat handlerHeartBeat;

    @Resource
    private HandlerWebSocket handlerWebSocket;

    @Resource
    private AppConfigProperties appConfigProperties;

    @Value("${spring.data.redis.host}")
    private String redisHost;

    @Override
    public void run() {
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workGroup);
            serverBootstrap.channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.DEBUG)).childHandler(new ChannelInitializer<>() {
                        @Override
                        protected void initChannel(Channel channel) throws Exception {
                            ChannelPipeline pipeline = channel.pipeline();
                            // 对http协议的支持 使用 http 编码器 解码器
                            pipeline.addLast(new HttpServerCodec());
                            // 聚合解码 httpRequest/httpContent/lastHttpContent到fullHttpRequest
                            // 保证接收 http 请求的完整性
                            pipeline.addLast(new HttpObjectAggregator(64 * 1024));
                            // 心跳
                            // readerIdleTime 读超时时间 即测试端一定时间内未接收到被测试端消息
                            // writerIdleTime 写超时时间 即测试端一定时间内向被测试端发送消息
                            // allIdleTime 所有类型的超时时间
                            pipeline.addLast(new IdleStateHandler(Constants.REDIS_KEY_EXPIRES_HEART_BEAT, 0, 0, TimeUnit.SECONDS));
                            pipeline.addLast(handlerHeartBeat);
                            // 将 http 协议升级为 ws 协议 对websocket支持
                            pipeline.addLast(new WebSocketServerProtocolHandler(
                                    "/ws",
                                    null,
                                    true,
                                    64 * 1024,
                                    true,
                                    true,
                                    1000L)
                            );
                            pipeline.addLast(handlerWebSocket);
                        }
                    });

            Integer wsPort = appConfigProperties.getWsPort();
            String wsPortStr = System.getProperty("ws.port");
            if (!StringTools.isEmpty(wsPortStr)) {
                wsPort = Integer.parseInt(wsPortStr);
            }
            ChannelFuture channelFuture = serverBootstrap.bind(wsPort).sync();

            logger.info("启动 netty 成功, 服务运行在 {}:{}", redisHost, wsPort);

            channelFuture.channel().closeFuture().sync();
        } catch (Exception e) {
            logger.error("启动 netty 失败");
        } finally {
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    }

    @PreDestroy
    public void close() {
        bossGroup.shutdownGracefully();
        workGroup.shutdownGracefully();
    }
}
