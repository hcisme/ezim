package org.chc.ezim.websocket;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.GlobalEventExecutor;
import jakarta.annotation.Resource;
import org.chc.ezim.entity.dto.UserDto;
import org.chc.ezim.entity.enums.UserContactTypeEnum;
import org.chc.ezim.entity.model.User;
import org.chc.ezim.mapper.UserMapper;
import org.chc.ezim.redis.RedisComponent;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ChannelContextUtils {
    @Resource
    private RedisComponent redisComponent;

    @Resource
    private UserMapper<User, UserDto> userMapper;

    private static final ConcurrentHashMap<String, Channel> USER_CONTEXT_MAP = new ConcurrentHashMap<>();

    private static final ConcurrentHashMap<String, ChannelGroup> GROUP_CONTEXT_MAP = new ConcurrentHashMap<>();

    public void addContext(String userId, Channel channel) {
        String channelId = channel.id().asLongText();
        AttributeKey attributeKey = null;
        if (!AttributeKey.exists(channelId)) {
            attributeKey = AttributeKey.newInstance(channelId);
        } else {
            attributeKey = AttributeKey.valueOf(channelId);
        }
        channel.attr(attributeKey).set(userId);

        List<String> contactIdList = redisComponent.getUserContactIdList(userId);
        for (String contactId : contactIdList) {
            // 群组
            if (contactId.startsWith(UserContactTypeEnum.GROUP.getPrefix())) {
                add2Group(contactId, channel);
            }
        }

        USER_CONTEXT_MAP.put(userId, channel);
        // 连接后立即更新一下心跳
        redisComponent.saveUserHeartBeat(userId);

        // 更新用户最后连接时间
        User user = new User();
        user.setLastLoginTime(new Date());
        userMapper.updateById(user, userId);
    }

    private void add2Group(String groupId, Channel channel) {
        ChannelGroup channelGroup = GROUP_CONTEXT_MAP.get(groupId);
        if (channelGroup == null) {
            channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
            GROUP_CONTEXT_MAP.put(groupId, channelGroup);
        }
        if (channel == null) {
            return;
        }
        channelGroup.add(channel);
    }
}
