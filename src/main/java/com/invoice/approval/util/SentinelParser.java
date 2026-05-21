package com.invoice.approval.util;

import org.springframework.stereotype.Component;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class SentinelParser {
    
    private static final Pattern SENTINEL_PATTERN = Pattern.compile("_([A-Z0-9]{6,20})_|SENTINEL([A-Z0-9]+)|_([A-Z0-9]+)\\.");
    
    public String extractSentinelFromFilename(String filename) {
        if (filename == null || filename.isEmpty()) {
            return "SENTINEL_" + System.currentTimeMillis();
        }
        
        Matcher matcher = SENTINEL_PATTERN.matcher(filename);
        if (matcher.find()) {
            for (int i = 1; i <= matcher.groupCount(); i++) {
                if (matcher.group(i) != null && !matcher.group(i).isEmpty()) {
                    return matcher.group(i);
                }
            }
        }
        
        return "SENTINEL_" + Math.abs(filename.hashCode());
    }
    
    public boolean hasValidSentinel(String filename) {
        String sentinel = extractSentinelFromFilename(filename);
        return sentinel != null && !sentinel.startsWith("NO_SENTINEL");
    }
}