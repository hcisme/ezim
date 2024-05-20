package org.chc.ezim.websocket.netty;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import jakarta.annotation.Resource;
import org.chc.ezim.entity.dto.TokenUserInfoDto;
import org.chc.ezim.redis.RedisComponent;
import org.chc.ezim.websocket.ChannelContextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@ChannelHandler.Sharable
@Component
public class HandlerWebSocket extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    private static final Logger logger = LoggerFactory.getLogger(HandlerWebSocket.class);

    @Resource
    private RedisComponent redisComponent;

    @Resource
    private ChannelContextUtils channelContextUtils;

    /**
     * 通道就绪后 调用
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.info("有新连接加入");
    }
    /**
     * 断掉连接 调用
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        Attribute<String> attr = channel.attr(AttributeKey.valueOf(channel.id().asLongText()));
        String userId = attr.get();

        logger.info("{} 连接断开", userId);
    }

    /**
     * 读取消息
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        Channel channel = ctx.channel();
        Attribute<String> attr = channel.attr(AttributeKey.valueOf(channel.id().asLongText()));
        String userId = attr.get();

        logger.info("收到 {} 消息: {}", userId, msg.text());

        redisComponent.saveUserHeartBeat(userId);

//        channelContextUtils.send2Group(msg.text());
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        // 握手完成
        if (evt instanceof WebSocketServerProtocolHandler.HandshakeComplete complete) {
            HttpHeaders headers = complete.requestHeaders(); // 获取请求头
            String token = headers.get("token");
            if (token == null) {
                ctx.channel().close();
                return;
            }

            TokenUserInfoDto userInfo = redisComponent.getUserInfo(token);
            if (userInfo == null) {
                ctx.channel().close();
                return;
            }

            channelContextUtils.addContext(userInfo.getId(), ctx.channel());
        }
    }
}
