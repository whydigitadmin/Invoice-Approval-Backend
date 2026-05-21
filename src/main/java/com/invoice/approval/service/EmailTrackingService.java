//package com.invoice.approval.service;
//
//import java.time.LocalDateTime;
//import java.util.Base64;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.Properties;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//
//import javax.annotation.PostConstruct;
//import javax.mail.BodyPart;
//import javax.mail.Flags;
//import javax.mail.Folder;
//import javax.mail.Message;
//import javax.mail.Multipart;
//import javax.mail.Session;
//import javax.mail.Store;
//import javax.mail.internet.InternetAddress;
//import javax.mail.internet.MimeMessage;
//import javax.mail.search.AndTerm;
//import javax.mail.search.ComparisonTerm;
//import javax.mail.search.ReceivedDateTerm;
//import javax.mail.search.SearchTerm;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.mail.javamail.JavaMailSender;
//import org.springframework.mail.javamail.MimeMessageHelper;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import com.invoice.approval.entity.QuoteEmailTracking;
//import com.invoice.approval.entity.QuoteRateVO;
//import com.invoice.approval.repo.QuoteEmailTrackingRepo;
//import com.invoice.approval.repo.QuoteRateRepo;
//
//import lombok.extern.slf4j.Slf4j;
//
//@Service
//@Slf4j
//public class EmailTrackingService {
//    
//    @Autowired
//    private QuoteEmailTrackingRepo emailTrackingRepo;
//    
//    @Autowired
//    private QuoteRateRepo quoteRateRepo;
//    
//    @Autowired
//    private JavaMailSender mailSender;
//    
//    @Value("${email.tracking.enabled:true}")
//    private boolean trackingEnabled;
//    
//    @Value("${email.tracking.test-mode:false}")
//    private boolean testMode;
//    
//    @Value("${email.processing.enabled:true}")
//    private boolean emailProcessingEnabled;
//    
//    @Value("${email.inbox.address:gjayabalan08@gmail.com}")
//    private String inboxEmail;
//    
//    @Value("${spring.mail.password:}")
//    private String emailPassword;
//    
//    @Value("${spring.mail.username:gjayabalan08@gmail.com}")
//    private String senderEmail;
//    
//    @PostConstruct
//    public void init() {
//        log.info("🚀 EmailTrackingService initialized at {}", LocalDateTime.now());
//        log.info("🚀 Tracking enabled: {}", trackingEnabled);
//        log.info("🚀 Test mode: {}", testMode);
//        log.info("🚀 Email processing enabled: {}", emailProcessingEnabled);
//        log.info("🚀 Inbox email: {}", inboxEmail);
//        
//        if (emailProcessingEnabled) {
//            log.info("🚀 Email auto-processing scheduler will run every minute");
//        }
//    }
//    
////    @Scheduled(fixedDelay = 300000) // 5 minutes for statistics
//    public void runScheduledTask() {
//        if (!trackingEnabled) {
//            log.info("Tracking is disabled. Skipping scheduled task.");
//            return;
//        }
//        
//        log.info("⏰ ==================== EMAIL PROCESSING SCHEDULER ====================");
//        log.info("⏰ Started at {}", LocalDateTime.now());
//        
//        if (testMode) {
//            log.info("🧪 TEST MODE: Creating test record...");
//            createTestRecord();
//        }
//        
//        // Process pending quote emails
//        if (emailProcessingEnabled) {
//            processPendingQuoteEmails();
//        }
//        
//        // Calculate and log statistics
//        calculateStatistics();
//        
//        log.info("✅ ==================== SCHEDULER COMPLETED ====================");
//    }
//    
////    @Scheduled(fixedDelay = 60000) // 1 minute for email checking
//    public void checkAndProcessEmails() {
//        if (!emailProcessingEnabled) {
//            return;
//        }
//        
//        try {
//            log.info("📧 Checking for new quote emails from last 24 hours...");
//            fetchAndProcessNewEmails();
//        } catch (Exception e) {
//            log.error("❌ Error checking emails: {}", e.getMessage());
//        }
//    }
//    
//    private void fetchAndProcessNewEmails() {
//        Properties props = new Properties();
//        props.put("mail.store.protocol", "imaps");
//        props.put("mail.imaps.host", "imap.gmail.com");
//        props.put("mail.imaps.port", "993");
//        props.put("mail.imaps.ssl.enable", "true");
//        props.put("mail.imaps.auth", "true");
//        props.put("mail.imaps.starttls.enable", "true");
//        props.put("mail.imaps.ssl.trust", "*");
//        props.put("mail.debug", "false");
//        
//        Store store = null;
//        Folder inbox = null;
//        
//        try {
//            log.info("🔐 Connecting to Gmail IMAP with username: {}", inboxEmail);
//            
//            Session session = Session.getInstance(props);
//            store = session.getStore("imaps");
//            store.connect("imap.gmail.com", inboxEmail, emailPassword);
//            
//            log.info("✅ Successfully connected to Gmail");
//            
//            inbox = store.getFolder("INBOX");
//            inbox.open(Folder.READ_WRITE);
//            
//            // Only get emails from last 24 hours to avoid processing too many
//            long oneDayAgo = System.currentTimeMillis() - (24 * 60 * 60 * 1000);
//            java.util.Date oneDayAgoDate = new java.util.Date(oneDayAgo);
//            SearchTerm dateTerm = new ReceivedDateTerm(ComparisonTerm.GT, oneDayAgoDate);
//            
//            // Search for ALL emails from last 24 hours (both read and unread)
//            Message[] messages = inbox.search(dateTerm);
//            
//            log.info("📧 Found {} emails from last 24 hours", messages.length);
//            
//            // Limit to only process recent 50 emails max
//            int maxEmailsToProcess = Math.min(50, messages.length);
//            log.info("🔄 Will process {} most recent emails", maxEmailsToProcess);
//            
//            // Process from newest to oldest
//            for (int i = messages.length - 1; i >= Math.max(0, messages.length - maxEmailsToProcess); i--) {
//                Message message = messages[i];
//                try {
//                    processEmailMessage(message);
//                    // Don't mark as read automatically
//                } catch (Exception e) {
//                    log.error("❌ Error processing email: {}", e.getMessage());
//                }
//            }
//            
//        } catch (javax.mail.AuthenticationFailedException e) {
//            log.error("❌ Gmail Authentication Failed!");
//            log.error("Error: {}", e.getMessage());
//        } catch (Exception e) {
//            log.error("❌ Error connecting to email: {}", e.getMessage());
//        } finally {
//            try {
//                if (inbox != null && inbox.isOpen()) {
//                    inbox.close(false);
//                }
//                if (store != null) {
//                    store.close();
//                }
//            } catch (Exception e) {
//                log.error("Error closing email connection: {}", e.getMessage());
//            }
//        }
//    }
//    
//    private void processEmailMessage(Message message) throws Exception {
//        String from = InternetAddress.toString(message.getFrom());
//        String subject = message.getSubject();
//        String content = getTextFromMessage(message);
//        
//        log.info("📧 Processing email from: {}, Subject: {}", from, subject);
//        
//        // Check if it's a quote request
//        if (isQuoteRequest(subject, content)) {
//            log.info("✅ Found quote request in email from: {}", from);
//            
//            // Parse quote details
//            Map<String, String> quoteDetails = parseQuoteDetails(subject + " " + content);
//            
//            if (!quoteDetails.isEmpty() && isValidQuoteDetails(quoteDetails)) {
//                log.info("✅ Valid quote details found: {}", quoteDetails);
//                
//                // Save to tracking table
//                saveQuoteRequest(from, subject, quoteDetails);
//                
//                // Fetch rates and send reply
//                sendQuoteReply(from, subject, quoteDetails);
//                
//                // Mark as read after successful processing
//                message.setFlag(Flags.Flag.SEEN, true);
//            } else {
//                log.warn("⚠️ Not a valid quote request, skipping");
//            }
//        } else {
//            log.info("⏭️ Not a quote request, skipping");
//        }
//    }
//    
//    private boolean isQuoteRequest(String subject, String content) {
//        if (subject == null) subject = "";
//        if (content == null) content = "";
//        
//        String text = (subject + " " + content).toLowerCase();
//        
//        // Look for specific quote patterns
//        if (text.contains("quote") && text.contains("sea") && text.contains("pol")) {
//            return true;
//        }
//        
//        if (text.contains("freight quote") || text.contains("shipping quote") || text.contains("logistics quote")) {
//            return true;
//        }
//        
//        // Look for port codes pattern (BLR to SIN)
//        Pattern portPattern = Pattern.compile("\\b([A-Z]{3})\\s*(?:to|and|for)\\s*([A-Z]{3})\\b", Pattern.CASE_INSENSITIVE);
//        if (portPattern.matcher(text).find()) {
//            return true;
//        }
//        
//        return false;
//    }
//    
//    private Map<String, String> parseQuoteDetails(String text) {
//        Map<String, String> details = new HashMap<>();
//        
//        if (text == null) return details;
//        
//        // Pattern 1: "Sea PoL – BLR to SIN" (your exact format)
//        Pattern pattern = Pattern.compile(
//            "(?i)(sea|air|ocean)\\s*(?:pol|port\\s*of\\s*loading)?\\s*[\\-–]\\s*([A-Z]{2,4})\\s*(?:to|and|for)\\s*([A-Z]{2,4})"
//        );
//        
//        Matcher matcher = pattern.matcher(text);
//        if (matcher.find()) {
//            details.put("mode", matcher.group(1).toUpperCase());
//            details.put("pol", matcher.group(2).toUpperCase());
//            details.put("pod", matcher.group(3).toUpperCase());
//            return details;
//        }
//        
//        // Pattern 2: "BLR to SIN Sea Freight"
//        pattern = Pattern.compile(
//            "(?i)([A-Z]{2,4})\\s*(?:to|and|for)\\s*([A-Z]{2,4})\\s*(sea|air|ocean|freight)"
//        );
//        
//        matcher = pattern.matcher(text);
//        if (matcher.find()) {
//            details.put("pol", matcher.group(1).toUpperCase());
//            details.put("pod", matcher.group(2).toUpperCase());
//            details.put("mode", matcher.group(3).toUpperCase());
//            return details;
//        }
//        
//        // Pattern 3: Just "BLR to SIN" with context
//        pattern = Pattern.compile("(?i)\\b([A-Z]{2,4})\\s*(?:to|and|for)\\s*([A-Z]{2,4})\\b");
//        matcher = pattern.matcher(text);
//        if (matcher.find() && (text.toLowerCase().contains("quote") || text.toLowerCase().contains("freight"))) {
//            details.put("pol", matcher.group(1).toUpperCase());
//            details.put("pod", matcher.group(2).toUpperCase());
//            details.put("mode", "SEA"); // Default mode
//            return details;
//        }
//        
//        return details;
//    }
//    
//    private boolean isValidQuoteDetails(Map<String, String> details) {
//        String pol = details.get("pol");
//        String pod = details.get("pod");
//        
//        if (pol == null || pod == null) {
//            return false;
//        }
//        
//        // Valid port codes are usually 3 letters (BLR, SIN, HKG, etc.)
//        // Reject common English words
//        String[] invalidPorts = {"CUS", "MER", "YOU", "FOR", "THE", "AND", "ARE", "HIS", "HER", "OUR"};
//        
//        for (String invalid : invalidPorts) {
//            if (invalid.equalsIgnoreCase(pol) || invalid.equalsIgnoreCase(pod)) {
//                return false;
//            }
//        }
//        
//        // Accept if they're 2-4 letter codes that look like port codes
//        return pol.length() >= 2 && pol.length() <= 4 && pod.length() >= 2 && pod.length() <= 4;
//    }
//    
//    private String getTextFromMessage(Message message) throws Exception {
//        if (message.isMimeType("text/plain")) {
//            return message.getContent().toString();
//        } else if (message.isMimeType("multipart/*")) {
//            Multipart multipart = (Multipart) message.getContent();
//            return getTextFromMimeMultipart(multipart);
//        }
//        return "";
//    }
//    
//    private String getTextFromMimeMultipart(Multipart multipart) throws Exception {
//        StringBuilder result = new StringBuilder();
//        for (int i = 0; i < multipart.getCount(); i++) {
//            BodyPart bodyPart = multipart.getBodyPart(i);
//            if (bodyPart.isMimeType("text/plain")) {
//                result.append(bodyPart.getContent().toString());
//                break;
//            } else if (bodyPart.isMimeType("text/html")) {
//                String html = bodyPart.getContent().toString();
//                result.append(html.replaceAll("<[^>]*>", " "));
//            } else if (bodyPart.getContent() instanceof Multipart) {
//                result.append(getTextFromMimeMultipart((Multipart) bodyPart.getContent()));
//            }
//        }
//        return result.toString();
//    }
//    
//    @Transactional
//    private void saveQuoteRequest(String from, String subject, Map<String, String> details) {
//        try {
//            // Check if we already processed this email
//            List<QuoteEmailTracking> existing = emailTrackingRepo.findByFromEmail(from);
//            for (QuoteEmailTracking existingRecord : existing) {
//                if (existingRecord.getSubject() != null && 
//                    existingRecord.getSubject().equals(subject) && 
//                    existingRecord.getFirstReceivedDate() != null &&
//                    existingRecord.getFirstReceivedDate().isAfter(LocalDateTime.now().minusHours(24))) {
//                    log.info("⏭️ Already processed this email within 24 hours, skipping");
//                    return;
//                }
//            }
//            
//            QuoteEmailTracking tracking = new QuoteEmailTracking();
//            
//            String hashData = from + subject + LocalDateTime.now();
//            String emailHash = Base64.getEncoder().encodeToString(hashData.getBytes());
//            
//            tracking.setEmailHash(emailHash);
//            tracking.setFromEmail(from);
//            tracking.setSubject(subject);
//            tracking.setFirstReceivedDate(LocalDateTime.now());
//            tracking.setIsReplied("F");
//            tracking.setReplyStatus("PENDING_QUOTE");
//            tracking.setReplyCount(0);
//            
//            if (details.containsKey("pol")) {
//                tracking.setPol(details.get("pol"));
//            }
//            if (details.containsKey("pod")) {
//                tracking.setPod(details.get("pod"));
//            }
//            
//            emailTrackingRepo.save(tracking);
//            log.info("✅ Saved quote request from: {}", from);
//            
//        } catch (Exception e) {
//            log.error("❌ Error saving quote request: {}", e.getMessage());
//        }
//    }
//    
//    private void sendQuoteReply(String toEmail, String originalSubject, Map<String, String> details) {
//        try {
//            String mode = details.get("mode");
//            String pol = details.get("pol");
//            String pod = details.get("pod");
//            
//            log.info("🔍 Searching rates for: Mode={}, POL={}, POD={}", mode, pol, pod);
//            
//            List<QuoteRateVO> rates = quoteRateRepo.findRatesForQuote(mode, pol, pod);
//            log.info("📊 Found {} rates for the query", rates.size());
//            
//            MimeMessage message = mailSender.createMimeMessage();
//            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
//            
//            helper.setTo(toEmail);
//            helper.setFrom(senderEmail);
//            helper.setSubject("RE: " + originalSubject);
//            
//            String emailContent = buildQuoteReplyEmail(mode, pol, pod, rates);
//            helper.setText(emailContent, true);
//            
//            mailSender.send(message);
//            
//            updateTrackingAfterReply(toEmail, originalSubject, rates.size());
//            log.info("✅ Sent quote reply to: {}", toEmail);
//            
//        } catch (Exception e) {
//            log.error("❌ Error sending quote reply: {}", e.getMessage());
//        }
//    }
//    
//    private String buildQuoteReplyEmail(String mode, String pol, String pod, List<QuoteRateVO> rates) {
//        StringBuilder html = new StringBuilder();
//        html.append("<html><head><style>");
//        html.append("body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; margin: 20px; }");
//        html.append("h3 { color: #2c3e50; }");
//        html.append("table { border-collapse: collapse; width: 100%; margin: 20px 0; }");
//        html.append("th { background-color: #3498db; color: white; padding: 10px; text-align: left; }");
//        html.append("td { padding: 10px; border: 1px solid #ddd; }");
//        html.append("tr:nth-child(even) { background-color: #f9f9f9; }");
//        html.append(".no-rates { color: #e74c3c; padding: 20px; background-color: #fdf2f2; border-radius: 5px; }");
//        html.append("</style></head><body>");
//        
//        html.append("<h3>Thank you for your quote request!</h3>");
//        html.append("<p>We have received your inquiry for ").append(mode).append(" freight rates.</p>");
//        html.append("<p><strong>Route:</strong> ").append(pol).append(" to ").append(pod).append("</p>");
//        html.append("<p><strong>Mode:</strong> ").append(mode).append("</p>");
//        
//        if (rates == null || rates.isEmpty()) {
//            html.append("<div class='no-rates'>");
//            html.append("<h4>⚠️ No Rates Available in System</h4>");
//            html.append("<p>We currently don't have rates available for the requested route in our system.</p>");
//            html.append("<p>Our sales team will contact you shortly with a custom quotation.</p>");
//            html.append("</div>");
//        } else {
//            html.append("<h4>Available Rates:</h4>");
//            html.append("<table>");
//            html.append("<tr><th>Rate Type</th><th>Rate (USD)</th></tr>");
//            
//            for (QuoteRateVO rate : rates) {
//                html.append("<tr><td>");
//                if ("SEA".equalsIgnoreCase(mode)) {
//                    html.append("Sea Freight");
//                } else {
//                    html.append("Air Freight");
//                }
//                html.append("</td><td>");
//                if ("SEA".equalsIgnoreCase(mode) && rate.getSeaRate() != null) {
//                    html.append("$").append(rate.getSeaRate());
//                } else if ("AIR".equalsIgnoreCase(mode) && rate.getAirRate() != null) {
//                    html.append("$").append(rate.getAirRate());
//                } else {
//                    html.append("Contact for rates");
//                }
//                html.append("</td></tr>");
//            }
//            
//            html.append("</table>");
//            html.append("<p><em>Note: Rates are subject to change and availability. Contact us for exact pricing.</em></p>");
//        }
//        
//        html.append("<br/><p>For more information or to confirm booking, please contact our sales team.</p>");
//        html.append("<p>Best regards,<br/><strong>Quotes Team</strong><br/>");
//        html.append(senderEmail).append("</p></body></html>");
//        
//        return html.toString();
//    }
//    
//    @Transactional
//    private void updateTrackingAfterReply(String fromEmail, String subject, int ratesFound) {
//        try {
//            List<QuoteEmailTracking> trackings = emailTrackingRepo.findByFromEmail(fromEmail);
//            
//            for (QuoteEmailTracking tracking : trackings) {
//                if (tracking.getSubject() != null && tracking.getSubject().equals(subject) && "F".equals(tracking.getIsReplied())) {
//                    tracking.setIsReplied("T");
//                    tracking.setLastRepliedDate(LocalDateTime.now());
//                    tracking.setReplyCount(tracking.getReplyCount() + 1);
//                    tracking.setReplyStatus("AUTO_REPLIED_" + (ratesFound > 0 ? "RATES_FOUND" : "NO_RATES"));
//                    
//                    emailTrackingRepo.save(tracking);
//                    log.info("✅ Updated tracking record for: {}", fromEmail);
//                    break;
//                }
//            }
//        } catch (Exception e) {
//            log.error("❌ Error updating tracking: {}", e.getMessage());
//        }
//    }
//    
//    private void processPendingQuoteEmails() {
//        try {
//            List<QuoteEmailTracking> pendingQuotes = emailTrackingRepo.findPendingQuotes();
//            log.info("📊 Found {} pending quote emails to process", pendingQuotes.size());
//            
//            for (QuoteEmailTracking quote : pendingQuotes) {
//                try {
//                    Map<String, String> details = new HashMap<>();
//                    if (quote.getPol() != null) details.put("pol", quote.getPol());
//                    if (quote.getPod() != null) details.put("pod", quote.getPod());
//                    
//                    if (!details.isEmpty()) {
//                        details.put("mode", "SEA"); // Default mode
//                        sendQuoteReply(quote.getFromEmail(), quote.getSubject(), details);
//                    }
//                } catch (Exception e) {
//                    log.error("❌ Error processing pending quote: {}", e.getMessage());
//                }
//            }
//        } catch (Exception e) {
//            log.error("❌ Error processing pending quotes: {}", e.getMessage());
//        }
//    }
//    
//    private void createTestRecord() {
//        try {
//            QuoteEmailTracking testRecord = new QuoteEmailTracking();
//            String timestamp = String.valueOf(System.currentTimeMillis());
//            String testEmail = "test-quote-" + timestamp + "@test.com";
//            
//            testRecord.setEmailHash("test-" + timestamp);
//            testRecord.setFromEmail(testEmail);
//            testRecord.setSubject("Test Quote Request " + timestamp);
//            testRecord.setFirstReceivedDate(LocalDateTime.now());
//            testRecord.setIsReplied("T");
//            testRecord.setLastRepliedDate(LocalDateTime.now());
//            testRecord.setPol("BLR");
//            testRecord.setPod("SIN");
//            testRecord.setReplyCount(1);
//            testRecord.setReplyStatus("SCHEDULER_TEST");
//            
//            emailTrackingRepo.save(testRecord);
//            log.info("✅ Created test record");
//        } catch (Exception e) {
//            log.error("❌ Error creating test record: {}", e.getMessage());
//        }
//    }
//    
//    private void calculateStatistics() {
//        try {
//            long total = emailTrackingRepo.count();
//            long replied = emailTrackingRepo.findByIsReplied("T").size();
//            long pending = emailTrackingRepo.findByIsReplied("F").size();
//            long test = emailTrackingRepo.findByReplyStatusContaining("TEST").size();
//            long today = emailTrackingRepo.countRepliedToday();
//            
//            log.info("📊 Stats calculated - Total: {}, Replied: {}, Pending: {}, Today: {}, Test: {}", 
//                    total, replied, pending, today, test);
//        } catch (Exception e) {
//            log.error("❌ Error calculating statistics: {}", e.getMessage());
//        }
//    }
//}