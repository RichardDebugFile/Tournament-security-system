package com.diego.curso.springboot.webapp.springboot_web;

import com.diego.curso.springboot.webapp.springboot_web.models.OAuth2AuthenticationLog;
import com.diego.curso.springboot.webapp.springboot_web.repositories.OAuth2AuthenticationLogRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oauth2Login;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests de integración para verificar la configuración de OAuth2 con Keycloak y Google
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class OAuth2IntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OAuth2AuthenticationLogRepository authLogRepository;

    @Test
    void contextLoads() {
        // Verificar que el contexto de Spring se carga correctamente
        assertNotNull(mockMvc);
        assertNotNull(authLogRepository);
    }

    @Test
    void testLoginPage_IsAccessible() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk());
    }

    @Test
    void testHomeRequiresAuthentication() throws Exception {
        mockMvc.perform(get("/home"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void testHomeAccessibleWithAuthentication() throws Exception {
        mockMvc.perform(get("/home"))
                .andExpect(status().isOk());
    }

    @Test
    void testOAuth2Login_WithKeycloakMock() throws Exception {
        mockMvc.perform(get("/home")
                .with(oauth2Login()
                        .attributes(attrs -> {
                            attrs.put("sub", "keycloak-user-123");
                            attrs.put("preferred_username", "keycloak_user");
                            attrs.put("email", "user@keycloak.com");
                            attrs.put("email_verified", true);
                        })
                        .clientRegistration(clientRegistration -> {
                            clientRegistration.registrationId("keycloak");
                        })
                ))
                .andExpect(status().isOk());
    }

    @Test
    void testOAuth2Login_WithGoogleMock() throws Exception {
        mockMvc.perform(get("/home")
                .with(oauth2Login()
                        .attributes(attrs -> {
                            attrs.put("sub", "google-user-456");
                            attrs.put("email", "user@gmail.com");
                            attrs.put("email_verified", true);
                            attrs.put("name", "Test User");
                        })
                        .clientRegistration(clientRegistration -> {
                            clientRegistration.registrationId("google");
                        })
                ))
                .andExpect(status().isOk());
    }

    @Test
    void testAuthenticationLogRepository_SaveAndRetrieve() {
        // Arrange
        OAuth2AuthenticationLog log = new OAuth2AuthenticationLog();
        log.setProvider("keycloak");
        log.setUsername("test_user");
        log.setUserEmail("test@example.com");
        log.setSuccess(true);
        log.setIpAddress("127.0.0.1");
        log.setTimestamp(LocalDateTime.now());

        // Act
        OAuth2AuthenticationLog saved = authLogRepository.save(log);

        // Assert
        assertNotNull(saved.getId());
        assertEquals("keycloak", saved.getProvider());
        assertEquals("test_user", saved.getUsername());

        // Cleanup
        authLogRepository.delete(saved);
    }

    @Test
    void testAuthenticationLogRepository_FindByProvider() {
        // Arrange
        OAuth2AuthenticationLog log1 = createLog("keycloak", "user1", true);
        OAuth2AuthenticationLog log2 = createLog("keycloak", "user2", true);
        OAuth2AuthenticationLog log3 = createLog("google", "user3", true);

        authLogRepository.save(log1);
        authLogRepository.save(log2);
        authLogRepository.save(log3);

        // Act
        List<OAuth2AuthenticationLog> keycloakLogs = authLogRepository.findByProvider("keycloak");
        List<OAuth2AuthenticationLog> googleLogs = authLogRepository.findByProvider("google");

        // Assert
        assertTrue(keycloakLogs.size() >= 2);
        assertTrue(googleLogs.size() >= 1);

        // Cleanup
        authLogRepository.deleteAll(List.of(log1, log2, log3));
    }

    @Test
    void testAuthenticationLogRepository_FindSuccessful() {
        // Arrange
        OAuth2AuthenticationLog successLog = createLog("keycloak", "user1", true);
        OAuth2AuthenticationLog failLog = createLog("keycloak", "user2", false);

        authLogRepository.save(successLog);
        authLogRepository.save(failLog);

        // Act
        List<OAuth2AuthenticationLog> successfulLogs = authLogRepository.findBySuccess(true);
        List<OAuth2AuthenticationLog> failedLogs = authLogRepository.findBySuccess(false);

        // Assert
        assertTrue(successfulLogs.size() >= 1);
        assertTrue(failedLogs.size() >= 1);

        // Cleanup
        authLogRepository.deleteAll(List.of(successLog, failLog));
    }

    @Test
    void testAuthenticationLogRepository_CountByProvider() {
        // Arrange
        OAuth2AuthenticationLog log1 = createLog("keycloak", "user1", true);
        OAuth2AuthenticationLog log2 = createLog("google", "user2", true);

        authLogRepository.save(log1);
        authLogRepository.save(log2);

        // Act
        List<Object[]> stats = authLogRepository.getAuthenticationStatsByProvider();

        // Assert
        assertNotNull(stats);
        assertFalse(stats.isEmpty());

        // Cleanup
        authLogRepository.deleteAll(List.of(log1, log2));
    }

    @Test
    void testAuthenticationLogRepository_FindFailedAttemptsByIp() {
        // Arrange
        String testIp = "192.168.1.100";
        OAuth2AuthenticationLog log1 = createLog("keycloak", "user1", false);
        log1.setIpAddress(testIp);
        log1.setTimestamp(LocalDateTime.now().minusMinutes(5));

        OAuth2AuthenticationLog log2 = createLog("keycloak", "user2", false);
        log2.setIpAddress(testIp);
        log2.setTimestamp(LocalDateTime.now().minusMinutes(2));

        authLogRepository.save(log1);
        authLogRepository.save(log2);

        // Act
        LocalDateTime since = LocalDateTime.now().minusMinutes(10);
        List<OAuth2AuthenticationLog> failures = authLogRepository.findFailedAttemptsByIpSince(testIp, since);

        // Assert
        assertTrue(failures.size() >= 2);
        assertEquals(testIp, failures.get(0).getIpAddress());

        // Cleanup
        authLogRepository.deleteAll(List.of(log1, log2));
    }

    // Helper methods

    private OAuth2AuthenticationLog createLog(String provider, String username, boolean success) {
        OAuth2AuthenticationLog log = new OAuth2AuthenticationLog();
        log.setProvider(provider);
        log.setUsername(username);
        log.setUserEmail(username + "@example.com");
        log.setSuccess(success);
        log.setIpAddress("127.0.0.1");
        log.setTimestamp(LocalDateTime.now());
        if (!success) {
            log.setErrorMessage("Authentication failed");
        }
        return log;
    }
}
