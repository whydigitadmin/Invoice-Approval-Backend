//package com.invoice.approval.service;  // Or your preferred package
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import lombok.Value;
//
//@Component
//public class EmailTrackingTestScheduler {
//    
//    private static final Logger logger = LoggerFactory.getLogger(EmailTrackingTestScheduler.class);
//    
//    @Autowired
//    private EmailTrackingService emailTrackingService;
//    
//    
//    
////    @Scheduled(fixedDelay = 300000) // Every 5 minutes (300,000 milliseconds)
//    public void testTracking() {
//        logger.info("⏰ Scheduled tracking test running...");
//        
//        try {
//            // Test tracking
//            emailTrackingService.trackEmailReply(
//                "scheduled-test@" + System.currentTimeMillis() + ".com",
//                "Scheduled Test " + System.currentTimeMillis(),
//                "SCH-POL",
//                "SCH-POD",
//                "SEA",
//                "SCHEDULED_TEST",
//                null
//            );
//            
//            logger.info("✅ Scheduled tracking test completed");
//            
//        } catch (Exception e) {
//            logger.error("❌ Scheduled tracking test failed: {}", e.getMessage());
//        }
//    }
//}