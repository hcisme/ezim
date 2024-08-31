package org.chc.ezim.service.impl;

import jakarta.annotation.Resource;
import org.apache.commons.lang3.ArrayUtils;
import org.chc.ezim.entity.config.AppConfigProperties;
import org.chc.ezim.entity.constants.Constants;
import org.chc.ezim.entity.dto.*;
import org.chc.ezim.entity.enums.*;
import org.chc.ezim.entity.model.ChatMessage;
import org.chc.ezim.entity.model.ChatSession;
import org.chc.ezim.entity.model.UserContact;
import org.chc.ezim.entity.vo.PaginationResultVO;
import org.chc.ezim.exception.BusinessException;
import org.chc.ezim.mapper.ChatMessageMapper;
import org.chc.ezim.mapper.ChatSessionMapper;
import org.chc.ezim.mapper.UserContactMapper;
import org.chc.ezim.redis.RedisComponent;
import org.chc.ezim.service.ChatMessageService;
import org.chc.ezim.utils.CopyTools;
import org.chc.ezim.utils.DateUtil;
import org.chc.ezim.utils.StringTools;
import org.chc.ezim.websocket.MessageHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Date;
import java.util.List;


/**
 * 聊天消息表 业务接口实现
 */
@Service("chatMessageService")
public class ChatMessageServiceImpl implements ChatMessageService {
    private static final Logger logger = LoggerFactory.getLogger(ChatMessageServiceImpl.class);

    @Resource
    private ChatMessageMapper<ChatMessage, ChatMessageDto> chatMessageMapper;

    @Resource
    private RedisComponent redisComponent;

    @Resource
    private UserContactMapper<UserContact, UserContactDto> userContactMapper;

    @Resource
    private MessageHandler messageHandler;

    @Resource
    private AppConfigProperties appConfigProperties;

    @Resource
    private ChatSessionMapper<ChatSession, ChatSessionDto> chatSessionMapper;

    /**
     * 根据条件查询列表
     */
    @Override
    public List<ChatMessage> findListByParam(ChatMessageDto param) {
        return this.chatMessageMapper.selectList(param);
    }

    /**
     * 根据条件查询列表
     */
    @Override
    public Integer findCountByParam(ChatMessageDto param) {
        return this.chatMessageMapper.selectCount(param);
    }

    /**
     * 分页查询方法
     */
    @Override
    public PaginationResultVO<ChatMessage> findListByPage(ChatMessageDto param) {
        int count = this.findCountByParam(param);
        int pageSize = param.getPageSize() == null ? PageSize.SIZE15.getSize() : param.getPageSize();

        SimplePage page = new SimplePage(param.getPage(), count, pageSize);
        param.setSimplePage(page);
        List<ChatMessage> list = this.findListByParam(param);
        PaginationResultVO<ChatMessage> result = new PaginationResultVO(count, page.getPageSize(), page.getPageNo(), page.getPageTotal(), list);
        return result;
    }

    /**
     * 新增
     */
    @Override
    public Integer add(ChatMessage bean) {
        return this.chatMessageMapper.insert(bean);
    }

    /**
     * 批量新增
     */
    @Override
    public Integer addBatch(List<ChatMessage> listBean) {
        if (listBean == null || listBean.isEmpty()) {
            return 0;
        }
        return this.chatMessageMapper.insertBatch(listBean);
    }

    /**
     * 批量新增或者修改
     */
    @Override
    public Integer addOrUpdateBatch(List<ChatMessage> listBean) {
        if (listBean == null || listBean.isEmpty()) {
            return 0;
        }
        return this.chatMessageMapper.insertOrUpdateBatch(listBean);
    }

    /**
     * 多条件更新
     */
    @Override
    public Integer updateByParam(ChatMessage bean, ChatMessageDto param) {
        StringTools.checkParam(param);
        return this.chatMessageMapper.updateByParam(bean, param);
    }

    /**
     * 多条件删除
     */
    @Override
    public Integer deleteByParam(ChatMessageDto param) {
        StringTools.checkParam(param);
        return this.chatMessageMapper.deleteByParam(param);
    }

    /**
     * 根据Id获取对象
     */
    @Override
    public ChatMessage getChatMessageById(Long id) {
        return this.chatMessageMapper.selectById(id);
    }

    /**
     * 根据Id修改
     */
    @Override
    public Integer updateChatMessageById(ChatMessage bean, Long id) {
        return this.chatMessageMapper.updateById(bean, id);
    }

    /**
     * 根据Id删除
     */
    @Override
    public Integer deleteChatMessageById(Long id) {
        return this.chatMessageMapper.deleteById(id);
    }

    @Override
    public MessageSendDto saveMessage(ChatMessage chatMessage, TokenUserInfoDto tokenUserInfoDto) {
        // 不是机器人回复 判断好友状态
        if (!Constants.ROBOT_UID.equals(tokenUserInfoDto.getId())) {
            List<String> userContactIdList = redisComponent.getUserContactIdList(tokenUserInfoDto.getId());
            if (!userContactIdList.contains(chatMessage.getContactId())) {
                UserContactTypeEnum userContactTypeEnum = UserContactTypeEnum.getByPrefix(chatMessage.getContactId());
                if (UserContactTypeEnum.USER == userContactTypeEnum) {
                    throw new BusinessException(ResponseCodeEnum.CODE_902);
                } else {
                    throw new BusinessException(ResponseCodeEnum.CODE_903);
                }
            }
        }

        String sessionId = null;
        String sendUserId = tokenUserInfoDto.getId();
        String contactId = chatMessage.getContactId();
        Long date = System.currentTimeMillis();
        UserContactTypeEnum contactTypeEnum = UserContactTypeEnum.getByPrefix(contactId);
        if (UserContactTypeEnum.USER == contactTypeEnum) {
            sessionId = StringTools.generatorChatSessionId4User(new String[]{sendUserId, contactId});
        } else {
            sessionId = StringTools.generatorChatSessionId4Group(contactId);
        }
        chatMessage.setSessionId(sessionId);
        chatMessage.setSendTime(date);

        SendMessageTypeEnum messageTypeEnum = SendMessageTypeEnum.getByType(chatMessage.getMessageType());
        if (messageTypeEnum == null ||
                !ArrayUtils.contains(
                        new Integer[]{SendMessageTypeEnum.CHAT.getType(), SendMessageTypeEnum.MEDIA_CHAT.getType()},
                        chatMessage.getMessageType()
                )
        ) {
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }
        Integer status = SendMessageTypeEnum.MEDIA_CHAT == messageTypeEnum ? MessageStatusEnum.SENDING.getStatus() : MessageStatusEnum.SENDED.getStatus();
        chatMessage.setStatus(status);

        String messageContent = StringTools.cleanHtmlTag(chatMessage.getMessageContent());
        chatMessage.setMessageContent(messageContent);

        // 更新会话
        ChatSession chatSession = new ChatSession();
        chatSession.setLastMessage(messageContent);
        if (UserContactTypeEnum.GROUP == contactTypeEnum) {
            chatSession.setLastMessage(tokenUserInfoDto.getNickName() + "：" + messageContent);
        }
        chatSession.setLastReceiveTime(date);
        chatSessionMapper.updateById(chatSession, sessionId);

        // 记录消息表
        chatMessage.setSendUserId(sendUserId);
        chatMessage.setSendUserNickName(tokenUserInfoDto.getNickName());
        chatMessage.setContactType(contactTypeEnum.getType());
        chatMessageMapper.insert(chatMessage);

        MessageSendDto messageSendDto = CopyTools.copy(chatMessage, MessageSendDto.class);
        messageSendDto.setMessageId(chatMessage.getId());
        if (Constants.ROBOT_UID.equals(contactId)) {
            SettingDto setting = redisComponent.getSetting();
            TokenUserInfoDto robot = new TokenUserInfoDto();
            robot.setId(setting.getRobotUid());
            robot.setNickName(setting.getRobotNickName());
            ChatMessage robotChatMessage = new ChatMessage();
            robotChatMessage.setContactId(sendUserId);
            robotChatMessage.setMessageContent("我只是一个机器人，无法识别你的消息。");
            robotChatMessage.setMessageType(SendMessageTypeEnum.CHAT.getType());
            saveMessage(robotChatMessage, robot);
        } else {
            messageHandler.sendMessage(messageSendDto);
        }
        return messageSendDto;
    }

    @Override
    public void saveMessageFile(String userId, Long messageId, MultipartFile file, MultipartFile cover) {
        ChatMessage chatMessage = chatMessageMapper.selectById(messageId);
        if (chatMessage == null || !chatMessage.getSendUserId().equals(userId)) {
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }
        SettingDto setting = redisComponent.getSetting();
        String fileSuffix = StringTools.getFileSuffix(file.getOriginalFilename());
        if (fileSuffix != null
                && ArrayUtils.contains(Constants.IMAGE_SUFFIX_LIST, fileSuffix.toLowerCase())
                && file.getSize() > setting.getMaxImageSize() * Constants.FILE_SIZE_MB
        ) {
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        } else if (fileSuffix != null
                && ArrayUtils.contains(Constants.VIDEO_SUFFIX_LIST, fileSuffix.toLowerCase())
                && file.getSize() > setting.getMaxVideoSize() * Constants.FILE_SIZE_MB
        ) {
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        } else {
            if (file.getSize() > setting.getMaxFileSize() * Constants.FILE_SIZE_MB) {
                throw new BusinessException(ResponseCodeEnum.CODE_600);
            }
        }

        String fileName = file.getOriginalFilename();
        String fileExtName = StringTools.getFileSuffix(fileName);
        String fileRealName = messageId + fileExtName;
        String month = DateUtil.format(new Date(chatMessage.getSendTime()), DateTimePatternEnum.YYYYMM.getPattern());
        File folder = new File(appConfigProperties.getProjectFolder() + Constants.FILE_FOLDER + Constants.FILE_FOLDER_UPLOAD_FILE_NAME + month);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        File uploadFile = new File(folder.getPath() + "/" + fileRealName);
        try {
            file.transferTo(uploadFile);
            cover.transferTo(new File(uploadFile.getPath() + Constants.COVER_IMAGE_SUFFIX));
        } catch (Exception e) {
            logger.error("上传文件失败", e);
        }

        ChatMessage uploadMessage = new ChatMessage();
        uploadMessage.setStatus(MessageStatusEnum.SENDED.getStatus());
        ChatMessageDto chatMessageDto = new ChatMessageDto();
        chatMessageDto.setId(messageId);
        chatMessageDto.setStatus(MessageStatusEnum.SENDING.getStatus());
        chatMessageMapper.updateByParam(uploadMessage, chatMessageDto);

        MessageSendDto<Object> messageSendDto = new MessageSendDto<>();
        messageSendDto.setStatus(MessageStatusEnum.SENDING.getStatus());
        messageSendDto.setMessageId(messageId);
        messageSendDto.setMessageType(SendMessageTypeEnum.FILE_UPLOAD.getType());
        messageSendDto.setContactId(chatMessage.getContactId());
        messageHandler.sendMessage(messageSendDto);
    }

    @Override
    public File downloadFile(TokenUserInfoDto tokenUserInfoDto, Long messageId, Boolean showCover) {
        ChatMessage message = chatMessageMapper.selectById(messageId);
        String contactId = message.getContactId();
        UserContactTypeEnum contactTypeEnum = UserContactTypeEnum.getByPrefix(contactId);
        if (UserContactTypeEnum.USER == contactTypeEnum && tokenUserInfoDto.getId().equals(message.getContactId())) {
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }
        if (UserContactTypeEnum.GROUP == contactTypeEnum) {
            UserContactDto userContactDto = new UserContactDto();
            userContactDto.setUserId(tokenUserInfoDto.getId());
            userContactDto.setContactType(contactTypeEnum.getType());
            userContactDto.setContactId(contactId);
            userContactDto.setStatus(UserContactStatusEnum.FRIEND.getStatus());
            Integer i = userContactMapper.selectCount(userContactDto);
            if (i == 0) {
                throw new BusinessException(ResponseCodeEnum.CODE_600);
            }
        }

        String month = DateUtil.format(new Date(message.getSendTime()), DateTimePatternEnum.YYYYMM.getPattern());
        File folder = new File(appConfigProperties.getProjectFolder() + Constants.FILE_FOLDER + Constants.FILE_FOLDER_UPLOAD_FILE_NAME + month);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        String fileName = message.getFileName();
        String fileExtName = StringTools.getFileSuffix(fileName);
        String fileRealName = messageId + fileExtName;
        if (showCover != null && showCover) {
            fileRealName = fileRealName + Constants.COVER_IMAGE_SUFFIX;
        }

        File file = new File(folder.getPath() + File.separator + fileRealName);
        if (!file.exists()) {
            logger.info("文件不存在 {}", messageId);
            throw new BusinessException(ResponseCodeEnum.CODE_602);
        }
        return file;
    }
}