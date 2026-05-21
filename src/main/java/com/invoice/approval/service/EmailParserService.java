package com.invoice.approval.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class EmailParserService {
    
    private static final Logger logger = LoggerFactory.getLogger(EmailParserService.class);
    
    /**
     * Parse email content to extract POL and POD
     */
    public QuoteRequest parseQuoteRequest(String emailContent) {
        QuoteRequest request = new QuoteRequest();
        
        try {
            // Pattern for "PoL – BLR to SIN" format
            Pattern pattern = Pattern.compile("(?i)pol[\\s\\-–]+([A-Z]{3})[\\s\\-–]+to[\\s\\-–]+([A-Z]{3})");
            Matcher matcher = pattern.matcher(emailContent);
            
            if (matcher.find()) {
                request.setPol(matcher.group(1).toUpperCase());
                request.setPod(matcher.group(2).toUpperCase());
            } else {
                // Alternative pattern for "BLR to SIN" format
                pattern = Pattern.compile("(?i)([A-Z]{3})[\\s\\-–]+to[\\s\\-–]+([A-Z]{3})");
                matcher = pattern.matcher(emailContent);
                
                if (matcher.find()) {
                    request.setPol(matcher.group(1).toUpperCase());
                    request.setPod(matcher.group(2).toUpperCase());
                }
            }
            
            // Determine mode (SEA, AIR, or BOTH)
            if (emailContent.toLowerCase().contains("sea") && emailContent.toLowerCase().contains("air")) {
                request.setMode("BOTH");
            } else if (emailContent.toLowerCase().contains("sea")) {
                request.setMode("SEA");
            } else if (emailContent.toLowerCase().contains("air")) {
                request.setMode("AIR");
            } else {
                request.setMode("BOTH"); // Default to both if not specified
            }
            
            logger.info("Parsed request: POL={}, POD={}, MODE={}", 
                request.getPol(), request.getPod(), request.getMode());
            
        } catch (Exception e) {
            logger.error("Error parsing email content", e);
        }
        
        return request;
    }
    
    /**
     * DTO for parsed quote request
     */
    public static class QuoteRequest {
        private String pol;
        private String pod;
        private String mode; // SEA, AIR, BOTH
        
        // Getters and Setters
        public String getPol() { return pol; }
        public void setPol(String pol) { this.pol = pol; }
        
        public String getPod() { return pod; }
        public void setPod(String pod) { this.pod = pod; }
        
        public String getMode() { return mode; }
        public void setMode(String mode) { this.mode = mode; }
        
        public boolean isValid() {
            return pol != null && !pol.isEmpty() && 
                   pod != null && !pod.isEmpty() &&
                   mode != null;
        }
        
        @Override
        public String toString() {
            return "QuoteRequest{POL='" + pol + "', POD='" + pod + "', MODE='" + mode + "'}";
        }
    }
}