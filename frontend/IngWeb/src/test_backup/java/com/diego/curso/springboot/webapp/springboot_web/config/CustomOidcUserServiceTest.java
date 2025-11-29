package com.diego.curso.springboot.webapp.springboot_web.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomOidcUserServiceTest {

    private CustomOidcUserService customOidcUserService;

    @Mock
    private OidcUserRequest userRequest;

    @Mock
    private ClientRegistration clientRegistration;

    @BeforeEach
    void setUp() {
        customOidcUserService = new CustomOidcUserService();
    }

    @Test
    void testLoadUser_Keycloak_WithPreferredUsername() {
        // Arrange
        String registrationId = "keycloak";
        String preferredUsername = "testuser";
        String email = "testuser@example.com";
        String sub = "123456789";

        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", sub);
        claims.put("preferred_username", preferredUsername);
        claims.put("email", email);
        claims.put("email_verified", true);

        OidcIdToken idToken = createOidcIdToken(claims);
        OidcUserInfo userInfo = new OidcUserInfo(claims);

        ClientRegistration registration = createClientRegistration(registrationId);
        OAuth2AccessToken accessToken = createAccessToken();

        OidcUserRequest request = new OidcUserRequest(registration, accessToken, idToken, userInfo);

        // Act
        OidcUser result = customOidcUserService.loadUser(request);

        // Assert
        assertNotNull(result);
        assertEquals(preferredUsername, result.getName());
        assertEquals(email, result.getEmail());
        assertEquals(sub, result.getSubject());
        assertTrue(result.getAttributes().containsKey("preferred_username"));
    }

    @Test
    void testLoadUser_Keycloak_WithoutPreferredUsername_FallbackToEmail() {
        // Arrange
        String registrationId = "keycloak";
        String email = "testuser@example.com";
        String sub = "123456789";

        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", sub);
        claims.put("email", email);
        claims.put("email_verified", true);

        OidcIdToken idToken = createOidcIdToken(claims);
        OidcUserInfo userInfo = new OidcUserInfo(claims);

        ClientRegistration registration = createClientRegistration(registrationId);
        OAuth2AccessToken accessToken = createAccessToken();

        OidcUserRequest request = new OidcUserRequest(registration, accessToken, idToken, userInfo);

        // Act
        OidcUser result = customOidcUserService.loadUser(request);

        // Assert
        assertNotNull(result);
        assertEquals(email, result.getName());
        assertEquals(email, result.getEmail());
    }

    @Test
    void testLoadUser_Keycloak_FallbackToSub() {
        // Arrange
        String registrationId = "keycloak";
        String sub = "123456789";

        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", sub);

        OidcIdToken idToken = createOidcIdToken(claims);
        OidcUserInfo userInfo = new OidcUserInfo(claims);

        ClientRegistration registration = createClientRegistration(registrationId);
        OAuth2AccessToken accessToken = createAccessToken();

        OidcUserRequest request = new OidcUserRequest(registration, accessToken, idToken, userInfo);

        // Act
        OidcUser result = customOidcUserService.loadUser(request);

        // Assert
        assertNotNull(result);
        assertEquals(sub, result.getName());
    }

    @Test
    void testLoadUser_Google_UsesEmail() {
        // Arrange
        String registrationId = "google";
        String email = "user@gmail.com";
        String sub = "987654321";

        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", sub);
        claims.put("email", email);
        claims.put("email_verified", true);

        OidcIdToken idToken = createOidcIdToken(claims);
        OidcUserInfo userInfo = new OidcUserInfo(claims);

        ClientRegistration registration = createClientRegistration(registrationId);
        OAuth2AccessToken accessToken = createAccessToken();

        OidcUserRequest request = new OidcUserRequest(registration, accessToken, idToken, userInfo);

        // Act
        OidcUser result = customOidcUserService.loadUser(request);

        // Assert
        assertNotNull(result);
        assertEquals(email, result.getName());
    }

    @Test
    void testLoadUser_Azure_UsesPreferredUsername() {
        // Arrange
        String registrationId = "azure";
        String preferredUsername = "user@company.com";
        String sub = "azure-sub-123";

        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", sub);
        claims.put("preferred_username", preferredUsername);

        OidcIdToken idToken = createOidcIdToken(claims);
        OidcUserInfo userInfo = new OidcUserInfo(claims);

        ClientRegistration registration = createClientRegistration(registrationId);
        OAuth2AccessToken accessToken = createAccessToken();

        OidcUserRequest request = new OidcUserRequest(registration, accessToken, idToken, userInfo);

        // Act
        OidcUser result = customOidcUserService.loadUser(request);

        // Assert
        assertNotNull(result);
        assertEquals(preferredUsername, result.getName());
    }

    // Helper methods

    private OidcIdToken createOidcIdToken(Map<String, Object> claims) {
        Instant issuedAt = Instant.now();
        Instant expiresAt = issuedAt.plusSeconds(3600);

        return new OidcIdToken(
                "token-value",
                issuedAt,
                expiresAt,
                claims
        );
    }

    private ClientRegistration createClientRegistration(String registrationId) {
        return ClientRegistration.withRegistrationId(registrationId)
                .clientId("test-client-id")
                .clientSecret("test-client-secret")
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .redirectUri("{baseUrl}/login/oauth2/code/" + registrationId)
                .scope("openid", "profile", "email")
                .authorizationUri("https://provider.com/oauth2/authorize")
                .tokenUri("https://provider.com/oauth2/token")
                .userInfoUri("https://provider.com/oauth2/userinfo")
                .jwkSetUri("https://provider.com/oauth2/jwks")
                .clientName(registrationId)
                .build();
    }

    private OAuth2AccessToken createAccessToken() {
        return new OAuth2AccessToken(
                OAuth2AccessToken.TokenType.BEARER,
                "access-token-value",
                Instant.now(),
                Instant.now().plusSeconds(3600)
        );
    }
}
