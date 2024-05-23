package org.chc.ezim.websocket;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.GlobalEventExecutor;
import jakarta.annotation.Resource;
import org.chc.ezim.entity.constants.Constants;
import org.chc.ezim.entity.dto.*;
import org.chc.ezim.entity.enums.SendMessageTypeEnum;
import org.chc.ezim.entity.enums.UserContactApplyStatusEnum;
import org.chc.ezim.entity.enums.UserContactTypeEnum;
import org.chc.ezim.entity.model.ChatMessage;
import org.chc.ezim.entity.model.ChatSessionUser;
import org.chc.ezim.entity.model.User;
import org.chc.ezim.entity.model.UserContactApply;
import org.chc.ezim.mapper.ChatMessageMapper;
import org.chc.ezim.mapper.ChatSessionUserMapper;
import org.chc.ezim.mapper.UserContactApplyMapper;
import org.chc.ezim.mapper.UserMapper;
import org.chc.ezim.redis.RedisComponent;
import org.chc.ezim.utils.JsonUtils;
import org.chc.ezim.utils.StringTools;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component
public class ChannelContextUtils {
    @Resource
    private RedisComponent redisComponent;

    @Resource
    private UserMapper<User, UserDto> userMapper;

    @Resource
    private UserContactApplyMapper<UserContactApply, UserContactApplyDto> userContactApplyMapper;

    @Resource
    private ChatMessageMapper<ChatMessage, ChatMessageDto> chatMessageMapper;

    @Resource
    private ChatSessionUserMapper<ChatSessionUser, ChatSessionUserDto> chatSessionUserMapper;

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
        for (String contactId : (contactIdList == null ? Collections.<String>emptyList() : contactIdList)) {
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

        // 给用户发消息
        User dbUser = userMapper.selectById(userId);
        Long dbLastOffTime = dbUser.getLastOffTime();
        Long lastOffTime = dbLastOffTime;
        if (dbLastOffTime != null && System.currentTimeMillis() - Constants.MillisSECONDS_3DAYS_ago > dbLastOffTime) {
            lastOffTime = Constants.MillisSECONDS_3DAYS_ago;
        }
        /**
         * 1. 查询用户所有的会话信息 保证换了设备会话同步
         */
        ChatSessionUserDto chatSessionUserDto = new ChatSessionUserDto();
        chatSessionUserDto.setUserId(userId);
        chatSessionUserDto.setOrderBy("last_receive_time desc");
        List<ChatSessionUser> chatSessionUserList = chatSessionUserMapper.selectList(chatSessionUserDto);
        WsInitDataDto wsInitDataDto = new WsInitDataDto();
        wsInitDataDto.setChatSessionList(chatSessionUserList);

        /**
         * 2. 查询聊天消息
         */
        List<String> groupIdList = contactIdList
                .stream()
                .filter(item -> item.startsWith(UserContactTypeEnum.GROUP.getPrefix()))
                .collect(Collectors.toList());
        groupIdList.add(userId);

        ChatMessageDto chatMessageDto = new ChatMessageDto();
        chatMessageDto.setContactIdList(groupIdList);
        chatMessageDto.setLastReceiveTime(lastOffTime);
        List<ChatMessage> chatMessageList = chatMessageMapper.selectList(chatMessageDto);
        wsInitDataDto.setChatMessageList(chatMessageList);

        /**
         * 3. 查询好友申请
         */
        UserContactApplyDto userContactApplyDto = new UserContactApplyDto();
        userContactApplyDto.setReceiveUserId(userId);
        userContactApplyDto.setStatus(UserContactApplyStatusEnum.INIT.getStatus());
        userContactApplyDto.setLastApplyTimeStamp(lastOffTime);
        Integer applyCount = userContactApplyMapper.selectCount(userContactApplyDto);
        wsInitDataDto.setApplyCount(applyCount);


        // 发送消息
        MessageSendDto<Object> messageSendDto = new MessageSendDto<>();
        messageSendDto.setMessageType(SendMessageTypeEnum.INIT.getType());
        messageSendDto.setContactId(userId);
        messageSendDto.setExtendData(wsInitDataDto);
        sendMessage(messageSendDto, userId);
    }

    public void sendMessage(MessageSendDto messageSendDto, String receiveUserId) {
        if (receiveUserId == null) {
            return;
        }
        Channel channel = USER_CONTEXT_MAP.get(receiveUserId);
        if (channel == null) {
            return;
        }

        // 给我发消息的人的id 昵称
        if (SendMessageTypeEnum.ADD_FRIEND_SELF.getType().equals(messageSendDto.getMessageType())) {
            User user = (User) messageSendDto.getExtendData();
            messageSendDto.setMessageType(SendMessageTypeEnum.ADD_FRIEND.getType());
            messageSendDto.setContactId(user.getId());
            messageSendDto.setContactName(user.getNickName());
            messageSendDto.setExtendData(null);
        } else {
            messageSendDto.setContactId(messageSendDto.getSendUserId());
            messageSendDto.setContactName(messageSendDto.getSendUserNickName());
        }
        channel.writeAndFlush(new TextWebSocketFrame(JsonUtils.convertObj2Json(messageSendDto)));
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

    public void addUser2Group(String userId, String groupId) {
        Channel channel = USER_CONTEXT_MAP.get(userId);
        add2Group(groupId, channel);
    }

    public void removeContext(Channel channel) {
        Attribute<String> attr = channel.attr(AttributeKey.valueOf(channel.id().asLongText()));
        String userId = attr.get();
        if (!StringTools.isEmpty(userId)) {
            USER_CONTEXT_MAP.remove(userId);
        }
        // 暂停程序 mysql redis链接已关闭 需要捕获异常
        try {
            redisComponent.removeUserHeartBeat(userId);
            // 更新用户最后离线时间
            User user = new User();
            user.setLastOffTime(System.currentTimeMillis());
            userMapper.updateById(user, userId);
        } catch (Exception e) {
            System.out.println("mysql 或 redis  连接已被关闭 " + e.getMessage());
        }
    }

    public void sendMessage(MessageSendDto messageSendDto) {
        UserContactTypeEnum contactTypeEnum = UserContactTypeEnum.getByPrefix(messageSendDto.getContactId());
        switch (contactTypeEnum) {
            case USER:
                send2User(messageSendDto);
                break;
            case GROUP:
                send2Group(messageSendDto);
                break;
        }
    }

    public void send2User(MessageSendDto messageSendDto) {
        String contactId = messageSendDto.getContactId();
        sendMessage(messageSendDto, contactId);
        // 强制下线
        if (SendMessageTypeEnum.FORCE_OFF_LINE.getType().equals(messageSendDto.getMessageType())) {
            // 关闭通道
            closeContext(contactId);
        }
    }

    public void closeContext(String userId) {
        if (StringTools.isEmpty(userId)) {
            return;
        }
        redisComponent.cleanTokenByUserId(userId);
        Channel userChannel = USER_CONTEXT_MAP.get(userId);
        if (userChannel == null) {
            return;
        }
        userChannel.close();
    }

    public void send2Group(MessageSendDto messageSendDto) {
        if (StringTools.isEmpty(messageSendDto.getContactId())) {
            return;
        }
        ChannelGroup channelGroup = GROUP_CONTEXT_MAP.get(messageSendDto.getContactId());
        if (channelGroup == null) {
            return;
        }
        channelGroup.writeAndFlush(new TextWebSocketFrame(JsonUtils.convertObj2Json(messageSendDto)));

        // 移出群聊
        SendMessageTypeEnum messageTypeEnum = SendMessageTypeEnum.getByType(messageSendDto.getMessageType());
        if (SendMessageTypeEnum.LEAVE_GROUP == messageTypeEnum || SendMessageTypeEnum.REMOVE_GROUP == messageTypeEnum) {
            String userId = (String) messageSendDto.getExtendData();
            redisComponent.removeUserContact(userId, messageSendDto.getContactId());
            Channel channel = USER_CONTEXT_MAP.get(userId);
            if (channel == null) {
                return;
            }
            channelGroup.remove(channel);
        }

        if (SendMessageTypeEnum.DISSOLUTION_GROUP == messageTypeEnum) {
            GROUP_CONTEXT_MAP.remove(messageSendDto.getContactId());
            channelGroup.close();
        }
    }
}
