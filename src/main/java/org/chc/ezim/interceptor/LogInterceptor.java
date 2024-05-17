package org.chc.ezim.interceptor;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;

@Component
public class LogInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull Object handler
    ) throws IOException, ServletException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentTime = sdf.format(new Date());
        StringBuilder params = new StringBuilder();

        String contentType = request.getContentType();
        if (contentType != null) {
            if (contentType.contains("application/x-www-form-urlencoded")) {
                handleUrlencodedRequest(request, params);
            } else if (contentType.contains("multipart/form-data")) {
                handleFormDataRequest(request, params);
            }

            System.out.println(
                    currentTime + " " + request.getMethod() + " " + request.getRequestURI() +
                            "\n 参数信息: " + params
            );
        } else {
            System.out.println(currentTime + " " + request.getMethod() + " " + request.getRequestURI());
        }

        return true;
    }

    private void handleUrlencodedRequest(HttpServletRequest request, StringBuilder params) {
        Enumeration<String> paramNames = request.getParameterNames();
        while (paramNames.hasMoreElements()) {
            String key = paramNames.nextElement();
            String value = request.getParameter(key);
            params.append(key).append(": ").append(value).append(", ");
        }
    }

    private void handleFormDataRequest(HttpServletRequest request, StringBuilder params) throws IOException, ServletException {
        Collection<Part> parts = request.getParts();
        for (Part part : parts) {
            String name = part.getName();
            String value = request.getParameter(name);
            params.append(name).append(": ").append(value).append(", ");
        }
    }
}
