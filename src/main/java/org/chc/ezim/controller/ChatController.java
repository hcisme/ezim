package org.chc.ezim.controller;

import jakarta.annotation.Resource;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.chc.ezim.annotation.GlobalAccessInterceptor;
import org.chc.ezim.entity.config.AppConfigProperties;
import org.chc.ezim.entity.constants.Constants;
import org.chc.ezim.entity.dto.ChatSendMessageDto;
import org.chc.ezim.entity.dto.MessageSendDto;
import org.chc.ezim.entity.dto.TokenUserInfoDto;
import org.chc.ezim.entity.enums.ResponseCodeEnum;
import org.chc.ezim.entity.model.ChatMessage;
import org.chc.ezim.entity.vo.ResponseVO;
import org.chc.ezim.exception.BusinessException;
import org.chc.ezim.service.ChatMessageService;
import org.chc.ezim.service.ChatSessionUserService;
import org.chc.ezim.utils.CopyTools;
import org.chc.ezim.utils.StringTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;

/**
 * 发送消息 Controller
 */
@RestController
@RequestMapping("/chat")
public class ChatController extends ABaseController {
    public static final Logger logger = LoggerFactory.getLogger(ChatController.class);

    @Resource
    private ChatMessageService chatMessageService;

    @Resource
    private ChatSessionUserService chatSessionUserService;

    @Resource
    private AppConfigProperties appConfigProperties;

    @GlobalAccessInterceptor
    @PostMapping("/sendMessage")
    public ResponseVO sendMessage(@RequestHeader("token") String token, @RequestBody @Validated ChatSendMessageDto chatSendMessageDto) {
        TokenUserInfoDto userInfo = getTokenInfo(token);
        ChatMessage chatMessage = CopyTools.copy(chatSendMessageDto, ChatMessage.class);

        MessageSendDto messageSendDto = chatMessageService.saveMessage(chatMessage, userInfo);

        return getSuccessResponseVO(messageSendDto);
    }

    @GlobalAccessInterceptor
    @PostMapping("/uploadFile")
    public ResponseVO uploadFile(
            @RequestHeader("token") String token,
            @NotNull Long messageId,
            @RequestPart(value = "file") @NotNull MultipartFile file,
            @RequestPart(value = "cover") @NotNull MultipartFile cover
    ) {
        TokenUserInfoDto userInfo = getTokenInfo(token);

        chatMessageService.saveMessageFile(userInfo.getId(), messageId, file, cover);
        return getSuccessResponseVO(null);
    }

    @GlobalAccessInterceptor
    @PostMapping("/downloadFile")
    public void downloadFile(
            @RequestHeader("token") String token,
            HttpServletResponse response,
            @NotEmpty String fileId,
            @NotNull Boolean showCover
    ) {
        TokenUserInfoDto userInfo = getTokenInfo(token);
        OutputStream out = null;
        FileInputStream in = null;

        try {
            File file = null;
            if (!StringTools.isNumber(fileId)) {
                String avatarFolderName = Constants.FILE_FOLDER + Constants.FILE_FOLDER_AVATAR_NAME;
                String avatarPath = appConfigProperties.getProjectFolder() + avatarFolderName + fileId + Constants.IMAGE_SUFFIX;
                if (showCover) {
                    avatarPath = avatarPath + Constants.COVER_IMAGE_SUFFIX;
                }
                file = new File(avatarPath);
                if (!file.exists()) {
                    throw new BusinessException(ResponseCodeEnum.CODE_602);
                }
            } else {
                file = chatMessageService.downloadFile(userInfo, Long.valueOf(fileId), showCover);
            }

            response.setContentType("application/x-msdownload;charset=UTF-8");
            response.setHeader("Content-Disposition", "attachment;");
            response.setContentLengthLong(file.length());
            in = new FileInputStream(file);
            byte[] bytes = new byte[1024];
            out = response.getOutputStream();
            int len;
            while ((len = in.read(bytes)) != -1) {
                out.write(bytes, 0, len);
            }
            out.flush();
        } catch (Exception e) {
            logger.error("下载文件失败", e);
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (Exception e) {
                    logger.error("IO 异常", e);
                }
            }
            if (in != null) {
                try {
                    in.close();
                } catch (Exception e) {
                    logger.error("IO 异常", e);
                }
            }
        }
    }
}
