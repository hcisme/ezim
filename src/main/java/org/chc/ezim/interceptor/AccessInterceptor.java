package org.chc.ezim.interceptor;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.chc.ezim.annotation.GlobalAccessInterceptor;
import org.chc.ezim.entity.constants.Constants;
import org.chc.ezim.entity.dto.TokenUserInfoDto;
import org.chc.ezim.entity.enums.ResponseCodeEnum;
import org.chc.ezim.exception.BusinessException;
import org.chc.ezim.redis.RedisUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.lang.reflect.Method;

@Component
public class AccessInterceptor implements HandlerInterceptor {
    @Resource
    private RedisUtils redisUtils;

    private static final Logger logger = LoggerFactory.getLogger(AccessInterceptor.class);

    @Override
    public boolean preHandle(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull Object handler
    ) {
        try {
            if (handler instanceof HandlerMethod handlerMethod) {
                Method method = handlerMethod.getMethod();

                // 检查方法上是否有GlobalInterceptor注解
                if (method.isAnnotationPresent(GlobalAccessInterceptor.class)) {
                    var globalAccessInterceptor = method.getAnnotation(GlobalAccessInterceptor.class);
                    if (globalAccessInterceptor.checkLoginAccess() || globalAccessInterceptor.checkAdminAccess()) {
                        return checkAccess(globalAccessInterceptor.checkAdminAccess());
                    }
                }
                return true;
            }
            return true;
        } catch (BusinessException e) {
            logger.error("全局拦截异常", e);
            throw e;
        } catch (Throwable e) {
            logger.error("全局拦截异常", e);
            throw new BusinessException(ResponseCodeEnum.CODE_500);
        }
    }

    private boolean checkAccess(boolean requireAdminAccess) {
        var attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            String token = attributes.getRequest().getHeader("token");
            if (token == null) {
                throw new BusinessException(ResponseCodeEnum.CODE_401);
            }
            TokenUserInfoDto tokenUserInfoDto =
                    (TokenUserInfoDto) redisUtils.getValue(Constants.REDIS_KEY_WS_TOKEN + token);

            if (tokenUserInfoDto == null) {
                throw new BusinessException(ResponseCodeEnum.CODE_401);
            }

            if (requireAdminAccess && !tokenUserInfoDto.getAdmin()) {
                throw new BusinessException(ResponseCodeEnum.CODE_403);
            }

            return true;
        }

        throw new BusinessException(ResponseCodeEnum.CODE_500);
    }
}
