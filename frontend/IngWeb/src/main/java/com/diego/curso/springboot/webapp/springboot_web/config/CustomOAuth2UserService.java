package com.diego.curso.springboot.webapp.springboot_web.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private static final Logger logger = LoggerFactory.getLogger(CustomOAuth2UserService.class);

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = super.loadUser(userRequest);

        // Obtener el proveedor (google, azure, etc.)
        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        logger.info("Loading user from provider: {}", registrationId);
        logger.info("User attributes: {}", oauth2User.getAttributes());

        // Determinar qué atributo usar como nombre principal
        String userNameAttributeName;
        Map<String, Object> attributes = new HashMap<>(oauth2User.getAttributes());

        if ("google".equals(registrationId)) {
            // Para Google, usar el email
            userNameAttributeName = "email";
        } else if ("azure".equals(registrationId)) {
            // Para Azure AD, intentar diferentes atributos en orden de preferencia
            if (attributes.containsKey("userPrincipalName")) {
                userNameAttributeName = "userPrincipalName";
            } else if (attributes.containsKey("mail")) {
                userNameAttributeName = "mail";
            } else if (attributes.containsKey("email")) {
                userNameAttributeName = "email";
            } else {
                // Si no hay ninguno, usar el atributo por defecto
                userNameAttributeName = userRequest.getClientRegistration()
                    .getProviderDetails()
                    .getUserInfoEndpoint()
                    .getUserNameAttributeName();
            }
        } else {
            // Por defecto, usar el atributo configurado
            userNameAttributeName = userRequest.getClientRegistration()
                .getProviderDetails()
                .getUserInfoEndpoint()
                .getUserNameAttributeName();
        }

        logger.info("Using username attribute: {} = {}", userNameAttributeName, attributes.get(userNameAttributeName));

        return new DefaultOAuth2User(
            oauth2User.getAuthorities(),
            attributes,
            userNameAttributeName
        );
    }
}
