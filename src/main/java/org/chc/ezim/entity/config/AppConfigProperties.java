package org.chc.ezim.entity.config;

import org.chc.ezim.utils.StringTools;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfigProperties {
    /**
     * websocket端口
     */
    @Value("${ws.port}")
    private Integer wsPort;

    /**
     * 项目文件目录
     */
    @Value("${project.folder}")
    private String projectFolder;

    /**
     * 管理员邮箱
     */
    @Value("${admin.emails}")
    private String adminEmails;

    public Integer getWsPort() {
        return wsPort;
    }

    public String getProjectFolder() {
        return projectFolder;
    }

    public String getAdminEmails() {
        return adminEmails;
    }
}
