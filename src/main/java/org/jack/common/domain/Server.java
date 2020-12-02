package org.jack.common.domain;

import java.util.Properties;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

public class Server {
    /**
     * 加密方式-SSL加密
     */
    public static final String ENCRYPTION_TYPE_SSL="SSL";
    
    /**
     * 加密方式-TLS加密
     */
    public static final String ENCRYPTION_TYPE_TLS="TLS";
    /**
     * 主机
     */
    private String host;
    /**
     * 端口
     */
    private Integer port;
    /**
     * 加密方式
     * @return
     */
    private String encryptionType;
    /**
     * 授权信息
     */
    private Authorization authorization;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getEncryptionType() {
        return encryptionType;
    }

    public void setEncryptionType(String encryptionType) {
        this.encryptionType = encryptionType;
    }

    public Authorization getAuthorization() {
        return authorization;
    }

    public void setAuthorization(Authorization authorization) {
        this.authorization = authorization;
    }
    public JavaMailSender useJavaMailSender() {
        JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
        javaMailSender.setHost(host);
        if (port != null && port > 0) {
            javaMailSender.setPort(port);
        }
        if(authorization!=null){
            javaMailSender.setUsername(authorization.username);
            javaMailSender.setPassword(authorization.password);
        }
        Properties javaMailProperties = new Properties();
        javaMailSender.setDefaultEncoding("UTF-8");
        javaMailSender.setProtocol("smtp");
        if (authorization!=null) {
            javaMailProperties.put("mail.smtp.auth", "true");
        }
        if (ENCRYPTION_TYPE_TLS.equals(encryptionType)) {
            javaMailProperties.put("mail.smtp.starttls.enable", "true");
        } else if (ENCRYPTION_TYPE_SSL.equals(encryptionType)) {
            javaMailProperties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            if (port != null && port > 0) {
                javaMailProperties.put("mail.smtp.socketFactory.port", port);
            }
        }
        javaMailSender.setJavaMailProperties(javaMailProperties);
        return javaMailSender;
    }
    public static class Authorization{
        /**
         * 用户名
         */
        private String username;
        /**
         * 密码
         */
        private String password;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
}
