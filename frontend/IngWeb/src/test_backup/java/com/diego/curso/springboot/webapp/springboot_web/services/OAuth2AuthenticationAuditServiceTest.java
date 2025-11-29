package com.diego.curso.springboot.webapp.springboot_web.services;

import com.diego.curso.springboot.webapp.springboot_web.models.OAuth2AuthenticationLog;
import com.diego.curso.springboot.webapp.springboot_web.repositories.OAuth2AuthenticationLogRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OAuth2AuthenticationAuditServiceTest {

    @Mock
    private OAuth2AuthenticationLogRepository logRepository;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpSession session;

    private OAuth2AuthenticationAuditService auditService;

    @BeforeEach
    void setUp() {
        auditService = new OAuth2AuthenticationAuditService(logRepository);
    }

    @Test
    void testLogSuccessfulAuthentication_Keycloak() {
        // Arrange
        String provider = "keycloak";
        String preferredUsername = "testuser";
        String email = "testuser@example.com";
        String ipAddress = "192.168.1.1";
        String userAgent = "Mozilla/5.0";
        String sessionId = "session-123";

        Map<String, Object> attributes = new HashMap<>();
        attributes.put("sub", "keycloak-sub-123");
        attributes.put("preferred_username", preferredUsername);
        attributes.put("email", email);
        attributes.put("email_verified", true);

        OAuth2User oauth2User = createOAuth2User(attributes, preferredUsername);

        when(request.getHeader("User-Agent")).thenReturn(userAgent);
        when(request.getRemoteAddr()).thenReturn(ipAddress);
        when(request.getSession(false)).thenReturn(session);
        when(session.getId()).thenReturn(sessionId);

        ArgumentCaptor<OAuth2AuthenticationLog> logCaptor = ArgumentCaptor.forClass(OAuth2AuthenticationLog.class);

        // Act
        auditService.logSuccessfulAuthentication(provider, oauth2User, request);

        // Wait a bit for async execution (in real tests, you'd use proper async testing)
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            // ignore
        }

        // Assert
        verify(logRepository, timeout(1000)).save(logCaptor.capture());
        OAuth2AuthenticationLog savedLog = logCaptor.getValue();

        assertEquals(provider, savedLog.getProvider());
        assertEquals(preferredUsername, savedLog.getUsername());
        assertEquals(email, savedLog.getUserEmail());
        assertTrue(savedLog.getSuccess());
        assertEquals(ipAddress, savedLog.getIpAddress());
        assertEquals(userAgent, savedLog.getUserAgent());
        assertEquals(sessionId, savedLog.getSessionId());
        assertNotNull(savedLog.getAttributes());
    }

    @Test
    void testLogSuccessfulAuthentication_Google() {
        // Arrange
        String provider = "google";
        String email = "user@gmail.com";
        String ipAddress = "10.0.0.1";

        Map<String, Object> attributes = new HashMap<>();
        attributes.put("sub", "google-sub-456");
        attributes.put("email", email);
        attributes.put("email_verified", true);
        attributes.put("name", "Test User");
        attributes.put("picture", "https://example.com/photo.jpg");

        OAuth2User oauth2User = createOAuth2User(attributes, email);

        when(request.getHeader("User-Agent")).thenReturn("Chrome");
        when(request.getRemoteAddr()).thenReturn(ipAddress);
        when(request.getSession(false)).thenReturn(null);

        ArgumentCaptor<OAuth2AuthenticationLog> logCaptor = ArgumentCaptor.forClass(OAuth2AuthenticationLog.class);

        // Act
        auditService.logSuccessfulAuthentication(provider, oauth2User, request);

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            // ignore
        }

        // Assert
        verify(logRepository, timeout(1000)).save(logCaptor.capture());
        OAuth2AuthenticationLog savedLog = logCaptor.getValue();

        assertEquals(provider, savedLog.getProvider());
        assertEquals(email, savedLog.getUsername());
        assertTrue(savedLog.getSuccess());
    }

    @Test
    void testLogFailedAuthentication() {
        // Arrange
        String provider = "keycloak";
        String errorMessage = "Invalid credentials";
        String ipAddress = "192.168.1.100";

        when(request.getRemoteAddr()).thenReturn(ipAddress);
        when(request.getSession(false)).thenReturn(null);

        ArgumentCaptor<OAuth2AuthenticationLog> logCaptor = ArgumentCaptor.forClass(OAuth2AuthenticationLog.class);

        // Act
        auditService.logFailedAuthentication(provider, errorMessage, request);

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            // ignore
        }

        // Assert
        verify(logRepository, timeout(1000)).save(logCaptor.capture());
        OAuth2AuthenticationLog savedLog = logCaptor.getValue();

        assertEquals(provider, savedLog.getProvider());
        assertEquals("FAILED_LOGIN", savedLog.getUsername());
        assertFalse(savedLog.getSuccess());
        assertEquals(errorMessage, savedLog.getErrorMessage());
        assertEquals(ipAddress, savedLog.getIpAddress());
    }

    @Test
    void testGetAuthenticationStatistics() {
        // Arrange
        List<Object[]> mockStats = Arrays.asList(
                new Object[]{"keycloak", true, 10L},
                new Object[]{"keycloak", false, 2L},
                new Object[]{"google", true, 15L},
                new Object[]{"google", false, 1L}
        );

        when(logRepository.getAuthenticationStatsByProvider()).thenReturn(mockStats);

        // Act
        Map<String, Map<String, Long>> stats = auditService.getAuthenticationStatistics();

        // Assert
        assertNotNull(stats);
        assertEquals(2, stats.size());

        assertTrue(stats.containsKey("keycloak"));
        assertEquals(10L, stats.get("keycloak").get("successful"));
        assertEquals(2L, stats.get("keycloak").get("failed"));

        assertTrue(stats.containsKey("google"));
        assertEquals(15L, stats.get("google").get("successful"));
        assertEquals(1L, stats.get("google").get("failed"));
    }

    @Test
    void testGetAuthenticationsByProviderLast24Hours() {
        // Arrange
        List<Object[]> mockCounts = Arrays.asList(
                new Object[]{"keycloak", 25L},
                new Object[]{"google", 30L}
        );

        when(logRepository.countAuthenticationsByProviderBetween(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(mockCounts);

        // Act
        Map<String, Long> counts = auditService.getAuthenticationsByProviderLast24Hours();

        // Assert
        assertNotNull(counts);
        assertEquals(2, counts.size());
        assertEquals(25L, counts.get("keycloak"));
        assertEquals(30L, counts.get("google"));
    }

    @Test
    void testGetUniqueUsersByProvider() {
        // Arrange
        List<Object[]> mockCounts = Arrays.asList(
                new Object[]{"keycloak", 8L},
                new Object[]{"google", 12L}
        );

        when(logRepository.countUniqueUsersByProvider()).thenReturn(mockCounts);

        // Act
        Map<String, Long> uniqueUsers = auditService.getUniqueUsersByProvider();

        // Assert
        assertNotNull(uniqueUsers);
        assertEquals(2, uniqueUsers.size());
        assertEquals(8L, uniqueUsers.get("keycloak"));
        assertEquals(12L, uniqueUsers.get("google"));
    }

    @Test
    void testHasSuspiciousActivity_True() {
        // Arrange
        String ipAddress = "192.168.1.100";
        int maxFailures = 5;
        int minutes = 15;

        List<OAuth2AuthenticationLog> failures = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            OAuth2AuthenticationLog log = new OAuth2AuthenticationLog();
            log.setIpAddress(ipAddress);
            log.setSuccess(false);
            failures.add(log);
        }

        when(logRepository.findFailedAttemptsByIpSince(eq(ipAddress), any(LocalDateTime.class)))
                .thenReturn(failures);

        // Act
        boolean result = auditService.hasSuspiciousActivity(ipAddress, maxFailures, minutes);

        // Assert
        assertTrue(result);
    }

    @Test
    void testHasSuspiciousActivity_False() {
        // Arrange
        String ipAddress = "192.168.1.100";
        int maxFailures = 5;
        int minutes = 15;

        List<OAuth2AuthenticationLog> failures = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            OAuth2AuthenticationLog log = new OAuth2AuthenticationLog();
            log.setIpAddress(ipAddress);
            log.setSuccess(false);
            failures.add(log);
        }

        when(logRepository.findFailedAttemptsByIpSince(eq(ipAddress), any(LocalDateTime.class)))
                .thenReturn(failures);

        // Act
        boolean result = auditService.hasSuspiciousActivity(ipAddress, maxFailures, minutes);

        // Assert
        assertFalse(result);
    }

    @Test
    void testGetRecentAuthentications() {
        // Arrange
        List<OAuth2AuthenticationLog> mockLogs = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            OAuth2AuthenticationLog log = new OAuth2AuthenticationLog();
            log.setProvider("keycloak");
            log.setUsername("user" + i);
            log.setSuccess(true);
            mockLogs.add(log);
        }

        when(logRepository.findTop10ByOrderByTimestampDesc()).thenReturn(mockLogs);

        // Act
        List<OAuth2AuthenticationLog> recentLogs = auditService.getRecentAuthentications();

        // Assert
        assertNotNull(recentLogs);
        assertEquals(5, recentLogs.size());
        verify(logRepository).findTop10ByOrderByTimestampDesc();
    }

    @Test
    void testCleanupOldLogs() {
        // Arrange
        int daysToKeep = 30;

        // Act
        auditService.cleanupOldLogs(daysToKeep);

        // Assert
        verify(logRepository).deleteByTimestampBefore(any(LocalDateTime.class));
    }

    @Test
    void testGetClientIpAddress_WithXForwardedFor() {
        // Arrange
        String realIp = "203.0.113.1";
        when(request.getHeader("X-Forwarded-For")).thenReturn(realIp + ", 10.0.0.1");
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");
        when(request.getSession(false)).thenReturn(null);

        OAuth2User oauth2User = createOAuth2User(Map.of("sub", "123", "email", "test@test.com"), "test@test.com");

        ArgumentCaptor<OAuth2AuthenticationLog> logCaptor = ArgumentCaptor.forClass(OAuth2AuthenticationLog.class);

        // Act
        auditService.logSuccessfulAuthentication("test", oauth2User, request);

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            // ignore
        }

        // Assert
        verify(logRepository, timeout(1000)).save(logCaptor.capture());
        OAuth2AuthenticationLog savedLog = logCaptor.getValue();

        assertEquals(realIp, savedLog.getIpAddress());
    }

    // Helper methods

    private OAuth2User createOAuth2User(Map<String, Object> attributes, String nameAttributeKey) {
        return new DefaultOAuth2User(
                Collections.emptyList(),
                attributes,
                nameAttributeKey
        );
    }

    private OidcUser createOidcUser(Map<String, Object> claims, String nameAttributeKey) {
        OidcIdToken idToken = new OidcIdToken(
                "token-value",
                Instant.now(),
                Instant.now().plusSeconds(3600),
                claims
        );

        OidcUserInfo userInfo = new OidcUserInfo(claims);

        return new DefaultOidcUser(
                Collections.emptyList(),
                idToken,
                userInfo,
                nameAttributeKey
        );
    }
}
