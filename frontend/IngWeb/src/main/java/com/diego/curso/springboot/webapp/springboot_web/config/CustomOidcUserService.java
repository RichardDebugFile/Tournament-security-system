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
        OidcUser oidcUser = super.loadUser(userRequest);

        // Obtener el proveedor (google, azure, etc.)
        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        logger.info("Loading OIDC user from provider: {}", registrationId);
        logger.info("User attributes: {}", oidcUser.getAttributes());
        logger.info("User claims: {}", oidcUser.getClaims());

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
        } else {
            // Por defecto, usar sub
            userNameAttributeName = "sub";
        }

        logger.info("Using username attribute: {} = {}", userNameAttributeName, attributes.get(userNameAttributeName));

        return new DefaultOidcUser(
            oidcUser.getAuthorities(),
            oidcUser.getIdToken(),
            oidcUser.getUserInfo(),
            userNameAttributeName
        );
    }
}
