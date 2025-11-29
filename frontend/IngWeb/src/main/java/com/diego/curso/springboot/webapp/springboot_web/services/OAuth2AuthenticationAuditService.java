package com.diego.curso.springboot.webapp.springboot_web.services;

import com.diego.curso.springboot.webapp.springboot_web.models.OAuth2AuthenticationLog;
import com.diego.curso.springboot.webapp.springboot_web.repositories.OAuth2AuthenticationLogRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class OAuth2AuthenticationAuditService {

    private static final Logger logger = LoggerFactory.getLogger(OAuth2AuthenticationAuditService.class);

    private final OAuth2AuthenticationLogRepository logRepository;
    private final ObjectMapper objectMapper;

    @Autowired
    public OAuth2AuthenticationAuditService(OAuth2AuthenticationLogRepository logRepository) {
        this.logRepository = logRepository;
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Registra una autenticación exitosa de OAuth2
     */
    @Async
    @Transactional
    public void logSuccessfulAuthentication(
            String provider,
            OAuth2User oauth2User,
            HttpServletRequest request
    ) {
        try {
            OAuth2AuthenticationLog log = new OAuth2AuthenticationLog();
            log.setProvider(provider);
            log.setSuccess(true);

            // Extraer información del usuario según el tipo
            String username = oauth2User.getName();
            String email = extractEmail(oauth2User);

            log.setUsername(username);
            log.setUserEmail(email);

            // Información de la request
            log.setIpAddress(getClientIpAddress(request));
            log.setUserAgent(request.getHeader("User-Agent"));
            log.setSessionId(request.getSession(false) != null ? request.getSession().getId() : null);

            // Guardar atributos importantes como JSON
            Map<String, Object> relevantAttributes = extractRelevantAttributes(oauth2User, provider);
            log.setAttributes(convertToJson(relevantAttributes));

            logRepository.save(log);

            logger.info("Successful OAuth2 authentication logged - Provider: {}, Username: {}, Email: {}, IP: {}",
                    provider, username, email, log.getIpAddress());

        } catch (Exception e) {
            logger.error("Error logging successful authentication", e);
        }
    }

    /**
     * Registra un fallo de autenticación de OAuth2
     */
    @Async
    @Transactional
    public void logFailedAuthentication(
            String provider,
            String errorMessage,
            HttpServletRequest request
    ) {
        try {
            OAuth2AuthenticationLog log = new OAuth2AuthenticationLog();
            log.setProvider(provider != null ? provider : "UNKNOWN");
            log.setSuccess(false);
            log.setUsername("FAILED_LOGIN");
            log.setErrorMessage(errorMessage);

            // Información de la request
            log.setIpAddress(getClientIpAddress(request));
            log.setUserAgent(request.getHeader("User-Agent"));
            log.setSessionId(request.getSession(false) != null ? request.getSession().getId() : null);

            logRepository.save(log);

            logger.warn("Failed OAuth2 authentication logged - Provider: {}, Error: {}, IP: {}",
                    provider, errorMessage, log.getIpAddress());

        } catch (Exception e) {
            logger.error("Error logging failed authentication", e);
        }
    }

    /**
     * Obtiene estadísticas de autenticaciones por proveedor
     */
    public Map<String, Map<String, Long>> getAuthenticationStatistics() {
        List<Object[]> stats = logRepository.getAuthenticationStatsByProvider();

        Map<String, Map<String, Long>> result = new HashMap<>();

        for (Object[] row : stats) {
            String provider = (String) row[0];
            Boolean success = (Boolean) row[1];
            Long count = (Long) row[2];

            result.putIfAbsent(provider, new HashMap<>());
            result.get(provider).put(success ? "successful" : "failed", count);
        }

        return result;
    }

    /**
     * Obtiene el conteo de autenticaciones por proveedor en las últimas 24 horas
     */
    public Map<String, Long> getAuthenticationsByProviderLast24Hours() {
        LocalDateTime since = LocalDateTime.now().minusHours(24);
        List<Object[]> counts = logRepository.countAuthenticationsByProviderBetween(since, LocalDateTime.now());

        return counts.stream()
                .collect(Collectors.toMap(
                        row -> (String) row[0],
                        row -> (Long) row[1]
                ));
    }

    /**
     * Obtiene usuarios únicos por proveedor
     */
    public Map<String, Long> getUniqueUsersByProvider() {
        List<Object[]> counts = logRepository.countUniqueUsersByProvider();

        return counts.stream()
                .collect(Collectors.toMap(
                        row -> (String) row[0],
                        row -> (Long) row[1]
                ));
    }

    /**
     * Obtiene las últimas autenticaciones
     */
    public List<OAuth2AuthenticationLog> getRecentAuthentications() {
        return logRepository.findTop10ByOrderByTimestampDesc();
    }

    /**
     * Verifica si hay intentos de autenticación sospechosos desde una IP
     */
    public boolean hasSuspiciousActivity(String ipAddress, int maxFailures, int minutes) {
        LocalDateTime since = LocalDateTime.now().minusMinutes(minutes);
        List<OAuth2AuthenticationLog> failures = logRepository.findFailedAttemptsByIpSince(ipAddress, since);

        return failures.size() >= maxFailures;
    }

    // Métodos auxiliares privados

    private String extractEmail(OAuth2User oauth2User) {
        // Intentar obtener el email de diferentes atributos
        Map<String, Object> attributes = oauth2User.getAttributes();

        if (attributes.containsKey("email")) {
            return (String) attributes.get("email");
        }

        if (oauth2User instanceof OidcUser) {
            OidcUser oidcUser = (OidcUser) oauth2User;
            if (oidcUser.getEmail() != null) {
                return oidcUser.getEmail();
            }
        }

        return null;
    }

    private Map<String, Object> extractRelevantAttributes(OAuth2User oauth2User, String provider) {
        Map<String, Object> relevant = new HashMap<>();
        Map<String, Object> allAttributes = oauth2User.getAttributes();

        // Atributos comunes
        relevant.put("name", oauth2User.getName());

        if (oauth2User instanceof OidcUser) {
            OidcUser oidcUser = (OidcUser) oauth2User;
            relevant.put("sub", oidcUser.getSubject());
            relevant.put("email_verified", oidcUser.getEmailVerified());
        }

        // Atributos específicos según el proveedor
        switch (provider.toLowerCase()) {
            case "google":
                addIfPresent(relevant, allAttributes, "picture", "locale", "given_name", "family_name");
                break;
            case "keycloak":
                addIfPresent(relevant, allAttributes, "preferred_username", "email_verified", "groups", "realm_access");
                break;
            case "azure":
                addIfPresent(relevant, allAttributes, "preferred_username", "unique_name", "upn", "tid");
                break;
        }

        return relevant;
    }

    private void addIfPresent(Map<String, Object> target, Map<String, Object> source, String... keys) {
        for (String key : keys) {
            if (source.containsKey(key)) {
                target.put(key, source.get(key));
            }
        }
    }

    private String getClientIpAddress(HttpServletRequest request) {
        String[] headerNames = {
                "X-Forwarded-For",
                "Proxy-Client-IP",
                "WL-Proxy-Client-IP",
                "HTTP_X_FORWARDED_FOR",
                "HTTP_X_FORWARDED",
                "HTTP_X_CLUSTER_CLIENT_IP",
                "HTTP_CLIENT_IP",
                "HTTP_FORWARDED_FOR",
                "HTTP_FORWARDED",
                "HTTP_VIA",
                "REMOTE_ADDR"
        };

        for (String header : headerNames) {
            String ip = request.getHeader(header);
            if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
                // Si hay múltiples IPs, tomar la primera
                return ip.split(",")[0].trim();
            }
        }

        return request.getRemoteAddr();
    }

    private String convertToJson(Map<String, Object> data) {
        try {
            return objectMapper.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            logger.warn("Error converting attributes to JSON", e);
            return "{}";
        }
    }

    /**
     * Limpia logs antiguos (para ejecutar periódicamente)
     */
    @Transactional
    public void cleanupOldLogs(int daysToKeep) {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(daysToKeep);
        logRepository.deleteByTimestampBefore(cutoffDate);
        logger.info("Cleaned up authentication logs older than {} days", daysToKeep);
    }
}
