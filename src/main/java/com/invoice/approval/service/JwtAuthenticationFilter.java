package com.invoice.approval.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    
    @Autowired
    private SentinelJwtService jwtService;
    
    @Value("${security.excluded-paths:/actuator/health}")
    private String[] excludedPaths;
    
    private Set<String> excludedPathSet;
    
    @javax.annotation.PostConstruct
    public void init() {
        excludedPathSet = new HashSet<>(Arrays.asList(excludedPaths));
        log.info("JWT Authentication Filter initialized. Excluded paths: {}", excludedPathSet);
    }
    
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                   HttpServletResponse response,
                                   FilterChain filterChain)
            throws ServletException, IOException {
        
        String path = request.getRequestURI();
        String method = request.getMethod();
        
        // GET requests and excluded paths skip authentication
        if ("GET".equalsIgnoreCase(method) || shouldSkipAuthentication(path)) {
            log.debug("Skipping authentication for: {} {}", method, path);
            filterChain.doFilter(request, response);
            return;
        }
        
        // POST, PUT, DELETE require authentication
        String authHeader = request.getHeader("Authorization");
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("Missing or invalid Authorization header for: {} {}", method, path);
            sendUnauthorizedResponse(response, "Missing or invalid Authorization header. Expected format: Bearer <token>");
            return;
        }
        
        String token = authHeader.substring(7);
        
        if (!jwtService.validateToken(token)) {
            log.warn("Invalid JWT token for request: {} {}", method, path);
            sendUnauthorizedResponse(response, "Invalid or expired JWT token");
            return;
        }
        
        log.info("Valid JWT token received for: {} {}", method, path);
        filterChain.doFilter(request, response);
    }
    
    private boolean shouldSkipAuthentication(String path) {
        if (excludedPathSet == null) return false;
        return excludedPathSet.stream().anyMatch(path::startsWith);
    }
    
    private void sendUnauthorizedResponse(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json");
        response.getWriter().write(String.format(
            "{\"timestamp\":\"%s\",\"status\":401,\"error\":\"Unauthorized\",\"message\":\"%s\",\"path\":\"%s\"}",
            java.time.Instant.now(), message, ""
        ));
    }
}