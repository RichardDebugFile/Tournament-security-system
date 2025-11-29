package com.diego.curso.springboot.webapp.springboot_web.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Enumeration;

/**
 * Filtro de diagnóstico para depurar el flujo OAuth2
 * Este filtro registra información detallada sobre cada request
 */
@Component
public class OAuth2LoginDebugFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(OAuth2LoginDebugFilter.class);

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String requestURI = httpRequest.getRequestURI();

        // Solo loguear requests relacionados con OAuth2 y login
        if (requestURI.contains("/oauth2/") || requestURI.contains("/login")) {
            logger.info("========== OAUTH2 DEBUG FILTER ==========");
            logger.info("Request URI: {}", requestURI);
            logger.info("Request URL: {}", httpRequest.getRequestURL());
            logger.info("Method: {}", httpRequest.getMethod());
            logger.info("Query String: {}", httpRequest.getQueryString());
            logger.info("Session ID: {}", httpRequest.getSession(false) != null ? httpRequest.getSession().getId() : "NO SESSION");

            // Log all headers
            logger.info("Headers:");
            Enumeration<String> headerNames = httpRequest.getHeaderNames();
            while (headerNames.hasMoreElements()) {
                String headerName = headerNames.nextElement();
                logger.info("  {}: {}", headerName, httpRequest.getHeader(headerName));
            }

            // Log all parameters
            logger.info("Parameters:");
            httpRequest.getParameterMap().forEach((key, values) -> {
                logger.info("  {}: {}", key, String.join(", ", values));
            });

            // Log cookies
            if (httpRequest.getCookies() != null) {
                logger.info("Cookies:");
                for (var cookie : httpRequest.getCookies()) {
                    logger.info("  {}: {}", cookie.getName(), cookie.getValue());
                }
            }

            logger.info("==========================================");
        }

        chain.doFilter(request, response);

        // Log response status for OAuth2 requests
        if (requestURI.contains("/oauth2/") || requestURI.contains("/login")) {
            logger.info("Response Status: {}", httpResponse.getStatus());
        }
    }
}
