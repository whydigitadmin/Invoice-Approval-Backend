//package com.invoice.approval.controller;
//
//import java.time.LocalDateTime;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import com.invoice.approval.service.EmailTrackingService;
//
//import lombok.experimental.var;
//
//@RestController
//@RequestMapping("/api/test")
//public class TestSchedulerController {
//    
//    @Autowired
//    private EmailTrackingService emailTrackingService;
//    
//    @GetMapping("/trigger-scheduler")
//    public String triggerSchedulerManually() {
//        try {
//            emailTrackingService.scheduledEmailProcessing();
//            return "✅ Scheduler triggered manually at " + LocalDateTime.now();
//        } catch (Exception e) {
//            return "❌ Error: " + e.getMessage();
//        }
//    }
//    
//    @GetMapping("/insert-test")
//    public String insertTestRecord() {
//        try {
//            var record = emailTrackingService.insertTestRecord();
//            return "✅ Test record inserted - ID: " + (record != null ? record.getId() : "null");
//        } catch (Exception e) {
//            return "❌ Error: " + e.getMessage();
//        }
//    }
//    
//    @GetMapping("/send-test-email")
//    public String sendTestEmail() {
//        try {
//            emailTrackingService.sendTestEmailNow();
//            return "✅ Test email sent! Check your inbox.";
//        } catch (Exception e) {
//            return "❌ Error: " + e.getMessage();
//        }
//    }
//    
//    @GetMapping("/stats")
//    public String getStats() {
//        try {
//            var stats = emailTrackingService.getEmailStats();
//            return "📊 Stats: " + stats.toString() + " | Time: " + LocalDateTime.now();
//        } catch (Exception e) {
//            return "❌ Error: " + e.getMessage();
//        }
//    }
//    
//    @GetMapping("/clear-tests")
//    public String clearTestRecords() {
//        try {
//            int count = emailTrackingService.clearTestRecords();
//            return "🧹 Cleared " + count + " test records";
//        } catch (Exception e) {
//            return "❌ Error: " + e.getMessage();
//        }
//    }
//}