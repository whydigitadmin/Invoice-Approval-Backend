//package com.invoice.approval.controller;
//
//import java.time.LocalDateTime;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.mail.SimpleMailMessage;
//import org.springframework.mail.javamail.JavaMailSender;
//import org.springframework.web.bind.annotation.CrossOrigin;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//import com.invoice.approval.entity.QuoteEmailTracking;
//import com.invoice.approval.entity.QuoteRateVO;
//import com.invoice.approval.repo.QuoteEmailTrackingRepo;
//import com.invoice.approval.repo.QuoteRateRepo;
//import com.invoice.approval.service.EmailTrackingService;
//
//@RestController
//@RequestMapping("/api/quote-auto-reply")
//@CrossOrigin(origins = "*")
//public class QuoteAutoReplyController {
//    
//    private static final Logger logger = LoggerFactory.getLogger(QuoteAutoReplyController.class);
//    
//    @Autowired
//    private EmailTrackingService emailTrackingService;
//    
//    @Autowired
//    private QuoteEmailTrackingRepo trackingRepo;
//    
//    @Autowired
//    private JavaMailSender mailSender;
//    
//    @Autowired
//    private QuoteRateRepo quoteRateRepo;
//    
//    // ========== AUTO-RESPONDER ENDPOINTS ==========
//    
//    /**
//     * MAIN AUTO-RESPONDER: Process incoming quote request email
//     * This is what you need - it parses email, fetches rates, and sends auto-reply
//     */
//    @PostMapping("/process-incoming-email")
//    public ResponseEntity<?> processIncomingEmail(@RequestBody IncomingEmailRequest request) {
//        logger.info("📧 Processing incoming email from: {}", request.getFromEmail());
//        
//        try {
//            // 1. Parse email to extract POL, POD, Mode
//            EmailDetails details = parseEmailContent(request.getSubject(), request.getBody());
//            
//            if (details.getPol() == null || details.getPod() == null) {
//                Map<String, Object> errorResponse = new HashMap<>();
//                errorResponse.put("status", "error");
//                errorResponse.put("message", "Could not extract POL and POD from email");
//                errorResponse.put("parsedDetails", details.toString());
//                errorResponse.put("suggestedFormat", "Use format: 'POL - BLR to SIN' or 'from BLR to SIN'");
//                return ResponseEntity.badRequest().body(errorResponse);
//            }
//            
//            logger.info("📝 Parsed details: POL={}, POD={}, Mode={}", 
//                       details.getPol(), details.getPod(), details.getMode());
//            
//            // 2. Check if already replied
//            boolean alreadyReplied = emailTrackingService.isAlreadyReplied(
//                request.getFromEmail(), request.getSubject(), 
//                details.getPol(), details.getPod(), details.getMode());
//            
//            if (alreadyReplied) {
//                Map<String, Object> response = new HashMap<>();
//                response.put("status", "skipped");
//                response.put("message", "Already replied to this quote request within 24 hours");
//                response.put("fromEmail", request.getFromEmail());
//                response.put("pol", details.getPol());
//                response.put("pod", details.getPod());
//                response.put("mode", details.getMode());
//                response.put("timestamp", LocalDateTime.now());
//                return ResponseEntity.ok(response);
//            }
//            
//            // 3. Fetch rates from GST_QUOTERATE table
//            List<QuoteRateVO> rates = quoteRateRepo.findByPolAndPod(details.getPol(), details.getPod());
//            
//            logger.info("📊 Found {} rates for {} to {}", rates.size(), details.getPol(), details.getPod());
//            
//            if (rates.isEmpty()) {
//                logger.warn("📭 No rates found in GST_QUOTERATE for {} to {}", 
//                           details.getPol(), details.getPod());
//                
//                // Send "no rates" email
//                sendNoRatesEmail(request.getFromEmail(), details);
//                
//                // Track as no rates
//                emailTrackingService.trackEmailReply(
//                    request.getFromEmail(), request.getSubject(),
//                    details.getPol(), details.getPod(), details.getMode(),
//                    "NO_RATES_FOUND", null
//                );
//                
//                Map<String, Object> response = new HashMap<>();
//                response.put("status", "no_rates");
//                response.put("message", "No rates found for this route in database");
//                response.put("pol", details.getPol());
//                response.put("pod", details.getPod());
//                response.put("mode", details.getMode());
//                response.put("timestamp", LocalDateTime.now());
//                return ResponseEntity.ok(response);
//            }
//            
//            // 4. Send auto-reply with rates
//            sendAutoReplyWithRates(request.getFromEmail(), details, rates);
//            
//            // 5. Track successful auto-reply
//            emailTrackingService.trackEmailReply(
//                request.getFromEmail(), request.getSubject(),
//                details.getPol(), details.getPod(), details.getMode(),
//                "AUTO_REPLIED", null
//            );
//            
//            Map<String, Object> response = new HashMap<>();
//            response.put("status", "success");
//            response.put("message", "Auto-reply sent with rates");
//            response.put("fromEmail", request.getFromEmail());
//            response.put("pol", details.getPol());
//            response.put("pod", details.getPod());
//            response.put("mode", details.getMode());
//            response.put("ratesCount", rates.size());
//            response.put("timestamp", LocalDateTime.now());
//            
//            logger.info("✅ Auto-reply sent to {} for {} to {} ({})", 
//                       request.getFromEmail(), details.getPol(), details.getPod(), details.getMode());
//            
//            return ResponseEntity.ok(response);
//            
//        } catch (Exception e) {
//            logger.error("❌ Failed to process incoming email: {}", e.getMessage(), e);
//            
//            Map<String, String> error = new HashMap<>();
//            error.put("error", "Failed to process email: " + e.getMessage());
//            error.put("details", e.getCause() != null ? e.getCause().getMessage() : "No additional details");
//            return ResponseEntity.status(500).body(error);
//        }
//    }
//    
//    /**
//     * Parse email content to extract POL, POD, Mode
//     */
//    private EmailDetails parseEmailContent(String subject, String body) {
//        EmailDetails details = new EmailDetails();
//        
//        // Combine subject and body for parsing
//        String combinedText = (subject + " " + body).toUpperCase();
//        
//        logger.debug("📄 Parsing text: {}", combinedText);
//        
//        // Pattern 1: "POL – BLR to SIN" (with en dash or hyphen)
//        Pattern pattern1 = Pattern.compile("POL\\s*[–\\-]\\s*([A-Z]{2,4})\\s+TO\\s+([A-Z]{2,4})", Pattern.CASE_INSENSITIVE);
//        
//        // Pattern 2: "from BLR to SIN"
//        Pattern pattern2 = Pattern.compile("FROM\\s+([A-Z]{2,4})\\s+TO\\s+([A-Z]{2,4})", Pattern.CASE_INSENSITIVE);
//        
//        // Pattern 3: "BLR to SIN" (direct)
//        Pattern pattern3 = Pattern.compile("([A-Z]{2,4})\\s+TO\\s+([A-Z]{2,4})", Pattern.CASE_INSENSITIVE);
//        
//        Matcher matcher = pattern1.matcher(combinedText);
//        if (matcher.find()) {
//            details.setPol(matcher.group(1).trim());
//            details.setPod(matcher.group(2).trim());
//            logger.debug("✅ Found with pattern1: POL={}, POD={}", details.getPol(), details.getPod());
//        } else {
//            matcher = pattern2.matcher(combinedText);
//            if (matcher.find()) {
//                details.setPol(matcher.group(1).trim());
//                details.setPod(matcher.group(2).trim());
//                logger.debug("✅ Found with pattern2: POL={}, POD={}", details.getPol(), details.getPod());
//            } else {
//                matcher = pattern3.matcher(combinedText);
//                if (matcher.find()) {
//                    details.setPol(matcher.group(1).trim());
//                    details.setPod(matcher.group(2).trim());
//                    logger.debug("✅ Found with pattern3: POL={}, POD={}", details.getPol(), details.getPod());
//                }
//            }
//        }
//        
//        // Detect mode (Sea/Air)
//        if (combinedText.contains("SEA")) {
//            details.setMode("SEA");
//        } else if (combinedText.contains("AIR")) {
//            details.setMode("AIR");
//        } else if (combinedText.contains("ROAD")) {
//            details.setMode("ROAD");
//        } else {
//            details.setMode("UNKNOWN");
//        }
//        
//        return details;
//    }
//    
//    /**
//     * Send auto-reply with rates from database
//     */
//    private void sendAutoReplyWithRates(String toEmail, EmailDetails details, List<QuoteRateVO> rates) {
//        try {
//            // Build email subject
//            String subject = String.format("Quote Rates: %s to %s (%s)", 
//                                          details.getPol(), details.getPod(), details.getMode());
//            
//            // Build email body
//            StringBuilder body = new StringBuilder();
//            body.append("Dear Customer,\n\n");
//            body.append("Thank you for your quote request.\n\n");
//            body.append(String.format("Route: %s to %s\n", details.getPol(), details.getPod()));
//            body.append(String.format("Mode: %s\n\n", details.getMode()));
//            body.append("Available Rates:\n\n");
//            
//            boolean hasRates = false;
//            for (QuoteRateVO rate : rates) {
//                if ("SEA".equalsIgnoreCase(details.getMode()) && rate.getSeaRate() != null) {
//                    body.append(String.format("• Sea Rate: $%.2f\n", rate.getSeaRate()));
//                    hasRates = true;
//                } else if ("AIR".equalsIgnoreCase(details.getMode()) && rate.getAirRate() != null) {
//                    body.append(String.format("• Air Rate: $%.2f\n", rate.getAirRate()));
//                    hasRates = true;
//                } else if ("UNKNOWN".equalsIgnoreCase(details.getMode())) {
//                    // If mode unknown, show both if available
//                    if (rate.getSeaRate() != null) {
//                        body.append(String.format("• Sea Rate: $%.2f\n", rate.getSeaRate()));
//                        hasRates = true;
//                    }
//                    if (rate.getAirRate() != null) {
//                        body.append(String.format("• Air Rate: $%.2f\n", rate.getAirRate()));
//                        hasRates = true;
//                    }
//                }
//            }
//            
//            if (!hasRates) {
//                body.append("• No specific rates available for this mode.\n");
//                body.append("• Please contact us for custom quotation.\n");
//            }
//            
//            body.append("\n\nFor detailed quotation, please contact our sales team.\n\n");
//            body.append("Best regards,\n");
//            body.append("Invoice Approval Team\n");
//            body.append("Email: abc@gmail.com\n");
//            body.append(String.format("Time: %s\n", LocalDateTime.now()));
//            
//            // Send email
//            SimpleMailMessage message = new SimpleMailMessage();
//            message.setTo(toEmail);
//            message.setSubject(subject);
//            message.setText(body.toString());
//            
//            mailSender.send(message);
//            
//            logger.info("📤 Auto-reply sent to {} with rates", toEmail);
//            
//        } catch (Exception e) {
//            logger.error("❌ Failed to send auto-reply: {}", e.getMessage());
//            throw new RuntimeException("Failed to send auto-reply", e);
//        }
//    }
//    
//    /**
//     * Send email when no rates found
//     */
//    private void sendNoRatesEmail(String toEmail, EmailDetails details) {
//        try {
//            String subject = "Re: Your Quote Request";
//            String body = String.format(
//                "Dear Customer,\n\n" +
//                "Thank you for your quote request for %s to %s via %s.\n\n" +
//                "Currently, we don't have standard rates for this route in our system.\n" +
//                "Our sales team will contact you shortly with a custom quote.\n\n" +
//                "Best regards,\n" +
//                "Invoice Approval Team\n" +
//                "Email: abc@gmail.com\n\n" +
//                "Time: %s",
//                details.getPol(), details.getPod(), details.getMode(), LocalDateTime.now()
//            );
//            
//            SimpleMailMessage message = new SimpleMailMessage();
//            message.setTo(toEmail);
//            message.setSubject(subject);
//            message.setText(body);
//            
//            mailSender.send(message);
//            
//            logger.info("📤 No-rates email sent to: {}", toEmail);
//            
//        } catch (Exception e) {
//            logger.error("❌ Failed to send no-rates email: {}", e.getMessage());
//        }
//    }
//    
//    /**
//     * Test endpoint for your specific example
//     */
//    @PostMapping("/test-auto-responder")
//    public ResponseEntity<?> testAutoResponder() {
//        // Simulate your example email
//        IncomingEmailRequest testRequest = new IncomingEmailRequest();
//        testRequest.setFromEmail("test@gmail.com");
//        testRequest.setSubject("Quote Request");
//        testRequest.setBody("Dear Team, I want quote rate for Sea POL – BLR to SIN.");
//        
//        logger.info("🧪 Testing auto-responder with example email");
//        return processIncomingEmail(testRequest);
//    }
//    
//    /**
//     * Quick test parsing
//     */
//    @GetMapping("/test-parse")
//    public ResponseEntity<?> testParse(@RequestParam String text) {
//        EmailDetails details = parseEmailContent("Test", text);
//        return ResponseEntity.ok(details);
//    }
//    
//    /**
//     * Check if rates exist in database
//     */
//    @GetMapping("/check-rates")
//    public ResponseEntity<?> checkRates(@RequestParam String pol, @RequestParam String pod) {
//        try {
//            List<QuoteRateVO> rates = quoteRateRepo.findByPolAndPod(pol, pod);
//            Map<String, Object> response = new HashMap<>();
//            response.put("pol", pol);
//            response.put("pod", pod);
//            response.put("ratesFound", rates.size());
//            response.put("rates", rates);
//            return ResponseEntity.ok(response);
//        } catch (Exception e) {
//            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
//        }
//    }
//    
//    // ========== EXISTING ENDPOINTS (keep these) ==========
//    
//    @GetMapping("/ping")
//    public ResponseEntity<?> ping() {
//        Map<String, String> response = new HashMap<>();
//        response.put("status", "OK");
//        response.put("message", "Email tracking service is running");
//        response.put("timestamp", LocalDateTime.now().toString());
//        logger.info("✅ Ping endpoint called");
//        return ResponseEntity.ok(response);
//    }
//    
//    @GetMapping("/stats")
//    public ResponseEntity<?> getEmailStats() {
//        logger.info("📊 Getting email stats");
//        try {
//            EmailTrackingService.EmailStats stats = emailTrackingService.getEmailStats();
//            return ResponseEntity.ok(stats);
//        } catch (Exception e) {
//            logger.error("❌ Failed to get stats", e);
//            Map<String, String> error = new HashMap<>();
//            error.put("error", "Failed to get stats: " + e.getMessage());
//            return ResponseEntity.status(500).body(error);
//        }
//    }
//    
//    @GetMapping("/all-records")
//    public ResponseEntity<?> getAllRecords() {
//        logger.info("📋 Getting all records");
//        try {
//            List<QuoteEmailTracking> records = emailTrackingService.getAllTrackingRecords();
//            Map<String, Object> response = new HashMap<>();
//            response.put("count", records.size());
//            response.put("records", records);
//            logger.info("✅ Found {} records", records.size());
//            return ResponseEntity.ok(response);
//        } catch (Exception e) {
//            logger.error("❌ Failed to get records", e);
//            Map<String, String> error = new HashMap<>();
//            error.put("error", "Failed to get records: " + e.getMessage());
//            return ResponseEntity.status(500).body(error);
//        }
//    }
//    
//    @GetMapping("/check-replied")
//    public ResponseEntity<?> checkIfAlreadyReplied(
//            @RequestParam String fromEmail,
//            @RequestParam String subject,
//            @RequestParam String pol,
//            @RequestParam String pod,
//            @RequestParam String mode) {
//        
//        logger.info("🔍 Checking if already replied: {} - {} to {}", fromEmail, pol, pod);
//        try {
//            boolean alreadyReplied = emailTrackingService.isAlreadyReplied(fromEmail, subject, pol, pod, mode);
//            
//            Map<String, Object> response = new HashMap<>();
//            response.put("fromEmail", fromEmail);
//            response.put("subject", subject);
//            response.put("pol", pol);
//            response.put("pod", pod);
//            response.put("mode", mode);
//            response.put("alreadyReplied", alreadyReplied);
//            response.put("timestamp", LocalDateTime.now());
//            response.put("cooldownHours", 24);
//            
//            logger.info("✅ Check result: {}", alreadyReplied ? "Already replied" : "Can reply");
//            return ResponseEntity.ok(response);
//        } catch (Exception e) {
//            logger.error("❌ Failed to check", e);
//            Map<String, String> error = new HashMap<>();
//            error.put("error", "Failed to check: " + e.getMessage());
//            return ResponseEntity.status(500).body(error);
//        }
//    }
//    
//    @GetMapping("/test-insert")
//    public ResponseEntity<?> testInsert() {
//        logger.info("🧪 Testing insert via GET");
//        try {
//            QuoteEmailTracking testRecord = emailTrackingService.insertTestRecord();
//            
//            Map<String, Object> response = new HashMap<>();
//            response.put("success", testRecord != null);
//            response.put("record", testRecord);
//            response.put("message", testRecord != null ? 
//                "Test record inserted successfully" : "Failed to insert test record");
//            
//            logger.info("✅ Test insert result: {}", testRecord != null ? "Success" : "Failed");
//            return ResponseEntity.ok(response);
//        } catch (Exception e) {
//            logger.error("❌ Test insert failed", e);
//            Map<String, String> error = new HashMap<>();
//            error.put("error", "Test insert failed: " + e.getMessage());
//            error.put("exception", e.getClass().getName());
//            return ResponseEntity.status(500).body(error);
//        }
//    }
//    
//    @GetMapping("/quick-test")
//    public ResponseEntity<?> quickTest() {
//        logger.info("⚡ Quick test endpoint called");
//        try {
//            QuoteEmailTracking test = new QuoteEmailTracking();
//            test.setFromEmail("quick_test_" + System.currentTimeMillis() + "@test.com");
//            test.setSubject("Quick Test " + System.currentTimeMillis());
//            test.setPol("QT");
//            test.setPod("QT");
//            test.setMode("TEST");
//            test.setEmailHash("quick_" + System.currentTimeMillis());
//            test.setIsReplied("T");
//            test.setReplyCount(1);
//            test.setReplyStatus("QUICK_TEST");
//            
//            QuoteEmailTracking saved = trackingRepo.save(test);
//            
//            Map<String, Object> response = new HashMap<>();
//            response.put("success", true);
//            response.put("id", saved.getId());
//            response.put("message", "Quick test insert successful");
//            response.put("email", saved.getFromEmail());
//            
//            logger.info("✅ Quick test successful, ID: {}", saved.getId());
//            return ResponseEntity.ok(response);
//            
//        } catch (Exception e) {
//            logger.error("❌ Quick test failed", e);
//            Map<String, String> error = new HashMap<>();
//            error.put("error", "Quick test failed: " + e.getMessage());
//            return ResponseEntity.status(500).body(error);
//        }
//    }
//    
//    // ========== EXISTING POST ENDPOINTS ==========
//    
//    @PostMapping("/send-real-email")
//    public ResponseEntity<?> sendRealEmail(@RequestBody SendEmailRequest request) {
//        logger.info("📧 SENDING REAL EMAIL to: {} - Subject: {}", 
//                   request.getToEmail(), request.getSubject());
//        try {
//            if (request.getToEmail() == null || request.getToEmail().trim().isEmpty()) {
//                Map<String, String> error = new HashMap<>();
//                error.put("error", "Recipient email is required");
//                return ResponseEntity.badRequest().body(error);
//            }
//            
//            boolean alreadyReplied = emailTrackingService.isAlreadyReplied(
//                request.getToEmail(), request.getSubject(), 
//                request.getPol(), request.getPod(), request.getMode());
//            
//            if (alreadyReplied) {
//                Map<String, Object> response = new HashMap<>();
//                response.put("status", "skipped");
//                response.put("message", "Email already replied within cooldown period (24 hours)");
//                response.put("toEmail", request.getToEmail());
//                response.put("timestamp", LocalDateTime.now());
//                return ResponseEntity.ok(response);
//            }
//            
//            SimpleMailMessage message = new SimpleMailMessage();
//            message.setTo(request.getToEmail());
//            message.setSubject(request.getSubject());
//            message.setText(request.getBody());
//            
//            mailSender.send(message);
//            
//            logger.info("✅ Real email sent successfully to: {}", request.getToEmail());
//            
//            emailTrackingService.trackEmailReply(
//                request.getToEmail(),
//                request.getSubject(),
//                request.getPol(),
//                request.getPod(),
//                request.getMode(),
//                "REAL_SENT",
//                null
//            );
//            
//            Map<String, Object> response = new HashMap<>();
//            response.put("status", "success");
//            response.put("message", "Real email sent successfully");
//            response.put("toEmail", request.getToEmail());
//            response.put("subject", request.getSubject());
//            response.put("timestamp", LocalDateTime.now());
//            
//            return ResponseEntity.ok(response);
//        } catch (Exception e) {
//            logger.error("❌ Failed to send real email: {}", e.getMessage());
//            
//            emailTrackingService.trackEmailReply(
//                request.getToEmail(),
//                request.getSubject(),
//                request.getPol(),
//                request.getPod(),
//                request.getMode(),
//                "FAILED",
//                e.getMessage()
//            );
//            
//            Map<String, String> error = new HashMap<>();
//            error.put("error", "Failed to send email: " + e.getMessage());
//            return ResponseEntity.status(500).body(error);
//        }
//    }
//    
//    @PostMapping("/test-email-sending")
//    public ResponseEntity<?> testEmailSending(@RequestParam String testEmail) {
//        logger.info("🧪 Testing email sending to: {}", testEmail);
//        try {
//            if (!testEmail.contains("@") || !testEmail.contains(".")) {
//                Map<String, String> error = new HashMap<>();
//                error.put("error", "Invalid email format");
//                return ResponseEntity.badRequest().body(error);
//            }
//            
//            SimpleMailMessage message = new SimpleMailMessage();
//            message.setTo(testEmail);
//            message.setSubject("Test Email from Invoice Approval System");
//            
//            String body = String.format(
//                "This is a test email sent from the Invoice Approval System.\n\n" +
//                "Timestamp: %s\n" +
//                "If you received this email, the email system is working correctly.\n\n" +
//                "Thank you.",
//                LocalDateTime.now()
//            );
//            message.setText(body);
//            
//            mailSender.send(message);
//            
//            emailTrackingService.trackEmailReply(
//                testEmail,
//                "Test Email from Invoice Approval System",
//                "TEST",
//                "TEST",
//                "TEST",
//                "TEST_SENT",
//                null
//            );
//            
//            Map<String, Object> response = new HashMap<>();
//            response.put("success", true);
//            response.put("message", "Test email sent successfully");
//            response.put("testEmail", testEmail);
//            response.put("timestamp", LocalDateTime.now());
//            
//            logger.info("✅ Test email sent to: {}", testEmail);
//            return ResponseEntity.ok(response);
//        } catch (Exception e) {
//            logger.error("❌ Test email failed: {}", e.getMessage());
//            
//            Map<String, String> error = new HashMap<>();
//            error.put("error", "Test email failed: " + e.getMessage());
//            error.put("details", "Check email configuration in application.properties");
//            return ResponseEntity.status(500).body(error);
//        }
//    }
//    
//    @PostMapping("/simulate-email")
//    public ResponseEntity<?> simulateEmail(@RequestBody SimulateEmailRequest request) {
//        logger.info("📧 Simulating email: {} - {} to {}", 
//                   request.getFromEmail(), request.getPol(), request.getPod());
//        try {
//            boolean alreadyReplied = emailTrackingService.isAlreadyReplied(
//                request.getFromEmail(), request.getSubject(), 
//                request.getPol(), request.getPod(), request.getMode());
//            
//            if (alreadyReplied) {
//                Map<String, Object> response = new HashMap<>();
//                response.put("status", "skipped");
//                response.put("message", "Email already replied within cooldown period (24 hours)");
//                response.put("fromEmail", request.getFromEmail());
//                response.put("pol", request.getPol());
//                response.put("pod", request.getPod());
//                return ResponseEntity.ok(response);
//            }
//            
//            emailTrackingService.trackEmailReply(
//                request.getFromEmail(),
//                request.getSubject(),
//                request.getPol(),
//                request.getPod(),
//                request.getMode(),
//                "SIMULATED_SENT",
//                null
//            );
//            
//            Map<String, Object> response = new HashMap<>();
//            response.put("status", "success");
//            response.put("message", "Email simulated and tracked successfully");
//            response.put("fromEmail", request.getFromEmail());
//            response.put("pol", request.getPol());
//            response.put("pod", request.getPod());
//            response.put("mode", request.getMode());
//            
//            return ResponseEntity.ok(response);
//        } catch (Exception e) {
//            logger.error("❌ Failed to simulate email", e);
//            Map<String, String> error = new HashMap<>();
//            error.put("error", "Failed to simulate email: " + e.getMessage());
//            return ResponseEntity.status(500).body(error);
//        }
//    }
//    
//    @PostMapping("/manual-track")
//    public ResponseEntity<?> manualTrackEmail(@RequestBody TrackRequest request) {
//        logger.info("📝 Manual tracking: {} - {} to {}", 
//                   request.getFromEmail(), request.getPol(), request.getPod());
//        try {
//            emailTrackingService.trackEmailReply(
//                request.getFromEmail(),
//                request.getSubject(),
//                request.getPol(),
//                request.getPod(),
//                request.getMode(),
//                request.getStatus() != null ? request.getStatus() : "MANUAL",
//                request.getErrorMessage()
//            );
//            
//            Map<String, Object> response = new HashMap<>();
//            response.put("success", true);
//            response.put("message", "Email tracking recorded successfully");
//            response.put("fromEmail", request.getFromEmail());
//            response.put("pol", request.getPol());
//            response.put("pod", request.getPod());
//            response.put("mode", request.getMode());
//            
//            return ResponseEntity.ok(response);
//        } catch (Exception e) {
//            logger.error("❌ Manual track failed", e);
//            Map<String, String> error = new HashMap<>();
//            error.put("error", "Manual track failed: " + e.getMessage());
//            return ResponseEntity.status(500).body(error);
//        }
//    }
//    
//    // ========== DTOs ==========
//    
//    /**
//     * DTO for incoming email request (for auto-responder)
//     */
//    public static class IncomingEmailRequest {
//        private String fromEmail;
//        private String subject;
//        private String body;
//        
//        public String getFromEmail() { return fromEmail; }
//        public void setFromEmail(String fromEmail) { this.fromEmail = fromEmail; }
//        
//        public String getSubject() { return subject; }
//        public void setSubject(String subject) { this.subject = subject; }
//        
//        public String getBody() { return body; }
//        public void setBody(String body) { this.body = body; }
//    }
//    
//    /**
//     * Internal class for parsed email details
//     */
//    public static class EmailDetails {
//        private String pol;
//        private String pod;
//        private String mode;
//        
//        public String getPol() { return pol; }
//        public void setPol(String pol) { this.pol = pol; }
//        
//        public String getPod() { return pod; }
//        public void setPod(String pod) { this.pod = pod; }
//        
//        public String getMode() { return mode; }
//        public void setMode(String mode) { this.mode = mode; }
//        
//        @Override
//        public String toString() {
//            return String.format("POL: %s, POD: %s, Mode: %s", pol, pod, mode);
//        }
//    }
//    
//    /**
//     * DTO for sending real email
//     */
//    public static class SendEmailRequest {
//        private String toEmail;
//        private String subject;
//        private String body;
//        private String pol;
//        private String pod;
//        private String mode;
//        
//        public String getToEmail() { return toEmail; }
//        public void setToEmail(String toEmail) { this.toEmail = toEmail; }
//        
//        public String getSubject() { return subject; }
//        public void setSubject(String subject) { this.subject = subject; }
//        
//        public String getBody() { return body; }
//        public void setBody(String body) { this.body = body; }
//        
//        public String getPol() { return pol; }
//        public void setPol(String pol) { this.pol = pol; }
//        
//        public String getPod() { return pod; }
//        public void setPod(String pod) { this.pod = pod; }
//        
//        public String getMode() { return mode; }
//        public void setMode(String mode) { this.mode = mode; }
//    }
//    
//    /**
//     * DTO for quote reply
//     */
//    public static class QuoteReplyRequest {
//        private String customerEmail;
//        private String customerName;
//        private String quoteNumber;
//        private String pol;
//        private String pod;
//        private String mode;
//        
//        public String getCustomerEmail() { return customerEmail; }
//        public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }
//        
//        public String getCustomerName() { return customerName; }
//        public void setCustomerName(String customerName) { this.customerName = customerName; }
//        
//        public String getQuoteNumber() { return quoteNumber; }
//        public void setQuoteNumber(String quoteNumber) { this.quoteNumber = quoteNumber; }
//        
//        public String getPol() { return pol; }
//        public void setPol(String pol) { this.pol = pol; }
//        
//        public String getPod() { return pod; }
//        public void setPod(String pod) { this.pod = pod; }
//        
//        public String getMode() { return mode; }
//        public void setMode(String mode) { this.mode = mode; }
//    }
//    
//    /**
//     * DTO for simulate email request
//     */
//    public static class SimulateEmailRequest {
//        private String fromEmail;
//        private String subject;
//        private String pol;
//        private String pod;
//        private String mode;
//        private String content;
//        
//        public String getFromEmail() { return fromEmail; }
//        public void setFromEmail(String fromEmail) { this.fromEmail = fromEmail; }
//        
//        public String getSubject() { return subject; }
//        public void setSubject(String subject) { this.subject = subject; }
//        
//        public String getPol() { return pol; }
//        public void setPol(String pol) { this.pol = pol; }
//        
//        public String getPod() { return pod; }
//        public void setPod(String pod) { this.pod = pod; }
//        
//        public String getMode() { return mode; }
//        public void setMode(String mode) { this.mode = mode; }
//        
//        public String getContent() { return content; }
//        public void setContent(String content) { this.content = content; }
//    }
//    
//    /**
//     * DTO for track request
//     */
//    public static class TrackRequest {
//        private String fromEmail;
//        private String subject;
//        private String pol;
//        private String pod;
//        private String mode;
//        private String status;
//        private String errorMessage;
//        
//        public String getFromEmail() { return fromEmail; }
//        public void setFromEmail(String fromEmail) { this.fromEmail = fromEmail; }
//        
//        public String getSubject() { return subject; }
//        public void setSubject(String subject) { this.subject = subject; }
//        
//        public String getPol() { return pol; }
//        public void setPol(String pol) { this.pol = pol; }
//        
//        public String getPod() { return pod; }
//        public void setPod(String pod) { this.pod = pod; }
//        
//        public String getMode() { return mode; }
//        public void setMode(String mode) { this.mode = mode; }
//        
//        public String getStatus() { return status; }
//        public void setStatus(String status) { this.status = status; }
//        
//        public String getErrorMessage() { return errorMessage; }
//        public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
//    }
//}