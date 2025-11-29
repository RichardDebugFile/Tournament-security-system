package com.diego.curso.springboot.webapp.springboot_web.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class CustomOidcUserService extends OidcUserService {

    private static final Logger logger = LoggerFactory.getLogger(CustomOidcUserService.class);

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        logger.info("========== CUSTOM OIDC USER SERVICE - START ==========");

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        logger.info("Provider (registrationId): {}", registrationId);
        logger.info("Client ID: {}", userRequest.getClientRegistration().getClientId());
        logger.info("Authorization Grant Type: {}", userRequest.getClientRegistration().getAuthorizationGrantType());

        OidcUser oidcUser;
        try {
            oidcUser = super.loadUser(userRequest);
            logger.info("OIDC User loaded successfully from parent class");
        } catch (Exception e) {
            logger.error("ERROR loading OIDC user from parent class", e);
            throw e;
        }

        logger.info("User attributes: {}", oidcUser.getAttributes());
        logger.info("User claims: {}", oidcUser.getClaims());
        logger.info("ID Token claims: {}", oidcUser.getIdToken().getClaims());

        // Determinar qué atributo usar como nombre principal
        String userNameAttributeName;
        Map<String, Object> attributes = new HashMap<>(oidcUser.getAttributes());

        if ("google".equals(registrationId)) {
            // Para Google, usar el email
            if (attributes.containsKey("email")) {
                userNameAttributeName = "email";
            } else {
                userNameAttributeName = "sub"; // fallback
            }
        } else if ("azure".equals(registrationId)) {
            // Para Azure AD, intentar diferentes atributos en orden de preferencia
            if (attributes.containsKey("preferred_username")) {
                userNameAttributeName = "preferred_username";
            } else if (attributes.containsKey("email")) {
                userNameAttributeName = "email";
            } else if (attributes.containsKey("unique_name")) {
                userNameAttributeName = "unique_name";
            } else {
                userNameAttributeName = "sub"; // fallback
            }
        } else if ("keycloak".equals(registrationId)) {
            // Para Keycloak, usar preferred_username como identificador principal
            if (attributes.containsKey("preferred_username")) {
                userNameAttributeName = "preferred_username";
            } else if (attributes.containsKey("email")) {
                userNameAttributeName = "email";
            } else {
                userNameAttributeName = "sub"; // fallback
            }

            logger.info("Keycloak user loaded - preferred_username: {}, email: {}, sub: {}",
                attributes.get("preferred_username"),
                attributes.get("email"),
                attributes.get("sub"));
        } else {
            // Por defecto, usar sub
            userNameAttributeName = "sub";
        }

        logger.info("Using username attribute: {} = {}", userNameAttributeName, attributes.get(userNameAttributeName));

        DefaultOidcUser customUser = new DefaultOidcUser(
            oidcUser.getAuthorities(),
            oidcUser.getIdToken(),
            oidcUser.getUserInfo(),
            userNameAttributeName
        );

        logger.info("Custom OIDC user created successfully");
        logger.info("Final username (getName()): {}", customUser.getName());
        logger.info("Final authorities: {}", customUser.getAuthorities());
        logger.info("========== CUSTOM OIDC USER SERVICE - END ==========");

        return customUser;
    }
}
