package com.diego.curso.springboot.webapp.springboot_web.config;

import com.diego.curso.springboot.webapp.springboot_web.services.OAuth2AuthenticationAuditService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class OAuth2AuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    private static final Logger logger = LoggerFactory.getLogger(OAuth2AuthenticationSuccessHandler.class);

    private final OAuth2AuthenticationAuditService auditService;

    @Autowired
    public OAuth2AuthenticationSuccessHandler(OAuth2AuthenticationAuditService auditService) {
        this.auditService = auditService;
        // Configurar URL de redirección por defecto
        setDefaultTargetUrl("/home");
        setAlwaysUseDefaultTargetUrl(true);
    }

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws ServletException, IOException {

        if (authentication instanceof OAuth2AuthenticationToken) {
            OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
            OAuth2User oauth2User = oauthToken.getPrincipal();
            String provider = oauthToken.getAuthorizedClientRegistrationId();

            logger.info("OAuth2 authentication successful - Provider: {}, User: {}",
                    provider, oauth2User.getName());

            // Registrar la autenticación exitosa de forma asíncrona
            try {
                auditService.logSuccessfulAuthentication(provider, oauth2User, request);
            } catch (Exception e) {
                logger.error("Error logging successful authentication (database may not be initialized)", e);
            }

            // Verificar actividad sospechosa
            String ipAddress = getClientIpAddress(request);
            try {
                if (auditService.hasSuspiciousActivity(ipAddress, 5, 15)) {
                    logger.warn("Suspicious authentication activity detected from IP: {}", ipAddress);
                    // Aquí podrías agregar lógica adicional como enviar alertas
                }
            } catch (Exception e) {
                logger.warn("Could not check for suspicious activity (database may not be initialized): {}", e.getMessage());
            }
        }

        // Continuar con el flujo normal de autenticación
        super.onAuthenticationSuccess(request, response, authentication);
    }

    private String getClientIpAddress(HttpServletRequest request) {
        String[] headerNames = {
                "X-Forwarded-For",
                "Proxy-Client-IP",
                "WL-Proxy-Client-IP",
                "HTTP_X_FORWARDED_FOR",
                "REMOTE_ADDR"
        };

        for (String header : headerNames) {
            String ip = request.getHeader(header);
            if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
                return ip.split(",")[0].trim();
            }
        }

        return request.getRemoteAddr();
    }
}
