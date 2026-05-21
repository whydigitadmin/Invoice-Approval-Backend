package com.invoice.approval.service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.security.Key;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;

@Service
public class SentinelJwtService {
    
    private static final Logger log = LoggerFactory.getLogger(SentinelJwtService.class);
    
    @Value("${sentinel.jwt.secret:}")
    private String jwtSecret;
    
    @Value("${sentinel.jwt.public-key:}")
    private String publicKeyBase64;
    
    private Key signingKey;
    private PublicKey publicKey;
    
    @PostConstruct
    public void init() {
        if (publicKeyBase64 != null && !publicKeyBase64.isEmpty()) {
            this.publicKey = loadPublicKey(publicKeyBase64);
            log.info("Sentinel JWT public key loaded successfully");
        } else if (jwtSecret != null && !jwtSecret.isEmpty()) {
            if (jwtSecret.length() < 32) {
                log.warn("JWT secret key is too short. Recommended at least 32 characters.");
            }
            this.signingKey = Keys.hmacShaKeyFor(jwtSecret.getBytes());
            log.info("Sentinel JWT secret key loaded successfully");
        } else {
            log.warn("No JWT secret or public key configured - TOKEN VALIDATION WILL FAIL");
        }
    }
    
    private PublicKey loadPublicKey(String base64PublicKey) {
        try {
            byte[] keyBytes = Base64.getDecoder().decode(base64PublicKey);
            X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePublic(spec);
        } catch (Exception e) {
            log.error("Failed to load public key: {}", e.getMessage());
            throw new RuntimeException("Failed to load public key", e);
        }
    }
    
    public boolean validateToken(String token) {
        if (token == null || token.trim().isEmpty()) {
            log.warn("Token is null or empty");
            return false;
        }
        
        try {
            JwtParserBuilder parserBuilder;
            
            if (publicKey != null) {
                parserBuilder = Jwts.parserBuilder().setSigningKey(publicKey);
            } else if (signingKey != null) {
                parserBuilder = Jwts.parserBuilder().setSigningKey(signingKey);
            } else {
                log.error("No signing key configured");
                return false;
            }
            
            // Parse the token
            Jws<Claims> jws = parserBuilder.build().parseClaimsJws(token);
            Claims claims = jws.getBody();
            
            // Get expiration date
            Date expiration = claims.getExpiration();
            Date now = new Date();
            
            log.debug("Token expiration: {}", expiration);
            log.debug("Current time: {}", now);
            
            // Check if token has expiry
            if (expiration != null) {
                if (expiration.before(now)) {
                    log.warn("Token expired at: {}", expiration);
                    return false;
                }
                log.info("Token is valid. Expires at: {}", expiration);
            } else {
                log.info("Token has no expiry - accepting as permanent token");
            }
            
            return true;
            
        } catch (ExpiredJwtException e) {
            log.error("Token expired: {}", e.getMessage());
            return false;
        } catch (MalformedJwtException e) {
            log.error("Malformed token: {}", e.getMessage());
            return false;
        } catch (SignatureException e) {
            log.error("Invalid token signature: {}", e.getMessage());
            return false;
        } catch (UnsupportedJwtException e) {
            log.error("Unsupported token: {}", e.getMessage());
            return false;
        } catch (IllegalArgumentException e) {
            log.error("Empty token: {}", e.getMessage());
            return false;
        } catch (Exception e) {
            log.error("Token validation failed: {}", e.getMessage());
            return false;
        }
    }
    
    public Claims getClaims(String token) {
        try {
            if (publicKey != null) {
                return Jwts.parserBuilder().setSigningKey(publicKey).build().parseClaimsJws(token).getBody();
            } else if (signingKey != null) {
                return Jwts.parserBuilder().setSigningKey(signingKey).build().parseClaimsJws(token).getBody();
            }
        } catch (Exception e) {
            log.error("Failed to parse claims: {}", e.getMessage());
        }
        return null;
    }
}