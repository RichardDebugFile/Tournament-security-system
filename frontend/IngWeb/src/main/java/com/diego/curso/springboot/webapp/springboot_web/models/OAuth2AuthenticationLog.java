package com.diego.curso.springboot.webapp.springboot_web.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "oauth2_authentication_logs")
public class OAuth2AuthenticationLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "provider", nullable = false, length = 50)
    private String provider; // google, keycloak, azure, etc.

    @Column(name = "username", nullable = false, length = 255)
    private String username; // email, preferred_username, etc.

    @Column(name = "user_email", length = 255)
    private String userEmail;

    @Column(name = "success", nullable = false)
    private Boolean success;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "user_agent", columnDefinition = "TEXT")
    private String userAgent;

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;

    @Column(name = "session_id", length = 255)
    private String sessionId;

    @Column(name = "attributes", columnDefinition = "TEXT")
    private String attributes; // JSON string with additional attributes

    // Constructors

    public OAuth2AuthenticationLog() {
        this.timestamp = LocalDateTime.now();
    }

    public OAuth2AuthenticationLog(String provider, String username, Boolean success) {
        this();
        this.provider = provider;
        this.username = username;
        this.success = success;
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getAttributes() {
        return attributes;
    }

    public void setAttributes(String attributes) {
        this.attributes = attributes;
    }

    @Override
    public String toString() {
        return "OAuth2AuthenticationLog{" +
                "id=" + id +
                ", provider='" + provider + '\'' +
                ", username='" + username + '\'' +
                ", userEmail='" + userEmail + '\'' +
                ", success=" + success +
                ", timestamp=" + timestamp +
                '}';
    }
}
