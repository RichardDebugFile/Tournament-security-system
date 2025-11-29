package com.diego.curso.springboot.webapp.springboot_web.config;

import com.diego.curso.springboot.webapp.springboot_web.services.OAuth2AuthenticationAuditService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class OAuth2AuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    private static final Logger logger = LoggerFactory.getLogger(OAuth2AuthenticationFailureHandler.class);

    private final OAuth2AuthenticationAuditService auditService;

    @Autowired
    public OAuth2AuthenticationFailureHandler(OAuth2AuthenticationAuditService auditService) {
        this.auditService = auditService;
        // Configurar URL de redirección en caso de fallo
        setDefaultFailureUrl("/login?error=true");
    }

    @Override
    public void onAuthenticationFailure(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException exception
    ) throws IOException, ServletException {

        String errorMessage = exception.getMessage();
        String provider = extractProvider(request);

        logger.error("========== OAUTH2 AUTHENTICATION FAILURE ==========");
        logger.error("Provider: {}", provider);
        logger.error("Request URI: {}", request.getRequestURI());
        logger.error("Request URL: {}", request.getRequestURL());
        logger.error("Query String: {}", request.getQueryString());
        logger.error("Exception Type: {}", exception.getClass().getName());
        logger.error("Error Message: {}", errorMessage);
        logger.error("IP Address: {}", getClientIpAddress(request));
        logger.error("Session ID: {}", request.getSession(false) != null ? request.getSession().getId() : "NO SESSION");
        logger.error("Full Stack Trace:", exception);

        // Si es una excepción OAuth2, extraer más detalles
        if (exception instanceof OAuth2AuthenticationException) {
            OAuth2AuthenticationException oauth2Exception = (OAuth2AuthenticationException) exception;
            errorMessage = oauth2Exception.getError().getDescription() != null
                    ? oauth2Exception.getError().getDescription()
                    : oauth2Exception.getError().getErrorCode();

            logger.error("OAuth2 error code: {}", oauth2Exception.getError().getErrorCode());
        }

        // Registrar el fallo de autenticación de forma asíncrona
        try {
            auditService.logFailedAuthentication(provider, errorMessage, request);
        } catch (Exception e) {
            logger.error("Error logging failed authentication (database may not be initialized)", e);
        }

        // IMPORTANTE: Invalidar la sesión para permitir reintentos
        // Sin esto, la sesión queda en estado corrupto y bloquea futuros intentos
        if (request.getSession(false) != null) {
            logger.debug("Invalidating session after OAuth2 authentication failure");
            request.getSession().invalidate();
        }

        // Verificar si hay demasiados intentos fallidos desde esta IP
        String ipAddress = getClientIpAddress(request);
        try {
            if (auditService.hasSuspiciousActivity(ipAddress, 5, 15)) {
                logger.warn("SECURITY ALERT: Multiple failed login attempts from IP: {}", ipAddress);
                // Aquí podrías implementar un bloqueo temporal o CAPTCHA
                setDefaultFailureUrl("/login?error=true&blocked=true");
            }
        } catch (Exception e) {
            logger.warn("Could not check for suspicious activity (database may not be initialized): {}", e.getMessage());
        }

        // Continuar con el flujo normal de manejo de fallos
        super.onAuthenticationFailure(request, response, exception);
    }

    /**
     * Intenta extraer el proveedor OAuth2 de la URL de la request
     */
    private String extractProvider(HttpServletRequest request) {
        String uri = request.getRequestURI();

        // El patrón típico es /login/oauth2/code/{provider}
        if (uri.contains("/oauth2/")) {
            String[] parts = uri.split("/");
            for (int i = 0; i < parts.length - 1; i++) {
                if ("code".equals(parts[i]) && i + 1 < parts.length) {
                    return parts[i + 1];
                }
            }
        }

        // Intentar desde el parámetro de query
        String registrationId = request.getParameter("registration_id");
        if (registrationId != null) {
            return registrationId;
        }

        // Intentar desde el referer
        String referer = request.getHeader("Referer");
        if (referer != null && referer.contains("oauth2")) {
            if (referer.contains("google")) return "google";
            if (referer.contains("keycloak")) return "keycloak";
            if (referer.contains("azure")) return "azure";
        }

        return "UNKNOWN";
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
