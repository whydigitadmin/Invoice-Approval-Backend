package com.invoice.approval.controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.mail.MessagingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.invoice.approval.dto.CurrentOutstandingDTO;
import com.invoice.approval.repo.CurrentOutstandingRepo;
import com.invoice.approval.service.CurrentOutstandingEmailService;
import com.invoice.approval.service.ExcelExportService; // Add this import

@RestController
@RequestMapping("/api/outstanding")
@CrossOrigin(origins = "*")
public class CurrentOutstandingController {

    private static final Logger LOGGER = LoggerFactory.getLogger(CurrentOutstandingController.class);

    @Autowired
    private CurrentOutstandingRepo currentOutstandingRepo;

    @Autowired
    private CurrentOutstandingEmailService outstandingEmailService;
    
    @Autowired
    private ExcelExportService excelExportService; // Add this autowired

    @GetMapping("/all")
    public ResponseEntity<?> getAllOutstanding() {
        try {
            List<CurrentOutstandingDTO> outstandingList = currentOutstandingRepo.getCurrentOutstandingDetails();
            return ResponseEntity.ok(outstandingList);
        } catch (Exception e) {
            LOGGER.error("Error fetching outstanding details: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch outstanding details: " + e.getMessage()));
        }
    }

    @GetMapping("/with-email")
    public ResponseEntity<?> getOutstandingWithEmail() {
        try {
            List<CurrentOutstandingDTO> outstandingList = currentOutstandingRepo.getCurrentOutstandingWithEmailDetails();
            return ResponseEntity.ok(outstandingList);
        } catch (Exception e) {
            LOGGER.error("Error fetching outstanding with email: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch outstanding with email: " + e.getMessage()));
        }
    }
    
    @GetMapping("/download-outstanding")
    public ResponseEntity<byte[]> downloadOutstandingExcel(
            @RequestParam String salespersonEmail,
            @RequestParam String employeeName) {
        
        try {
            List<CurrentOutstandingDTO> outstandingList = 
                currentOutstandingRepo.getOutstandingBySalesperson(salespersonEmail);
            
            if (outstandingList.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            byte[] excelBytes = excelExportService.generateOutstandingExcel(outstandingList, employeeName);
            
            String filename = "Pending_Invoices_" + employeeName.replace(" ", "_") + "_" 
                            + java.time.LocalDate.now() + ".xlsx";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
            headers.setContentDispositionFormData("attachment", filename);
            headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(excelBytes);
            
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    
    @PostMapping("/send-test")
    public ResponseEntity<?> sendTestEmail() {
        try {
            // Get data
            List<CurrentOutstandingDTO> data = currentOutstandingRepo.getCurrentOutstandingWithEmailDetails();
            
            if (data.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "No data found"));
            }
            
            // Use your email for testing
            String testEmail = "gjayabalan08@gmail.com"; // Your email
            
            // Filter data (take first 5 records if none match your email)
            List<CurrentOutstandingDTO> testData = data.stream()
                    .filter(d -> testEmail.equals(d.getSalespersonEmail()))
                    .collect(Collectors.toList());
            
            if (testData.isEmpty()) {
                testData = data.subList(0, Math.min(5, data.size()));
            }
            
            // Send email
            outstandingEmailService.sendOutstandingEmailToSalesperson(testEmail, testData, List.of());
            
            return ResponseEntity.ok(Map.of(
                "message", "Test email sent to " + testEmail,
                "recordsSent", testData.size()
            ));
        } catch (Exception e) {
            LOGGER.error("Error: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/salesperson/{email}")
    public ResponseEntity<?> getOutstandingBySalesperson(@PathVariable String email) {
        try {
            List<CurrentOutstandingDTO> outstandingList = currentOutstandingRepo.getOutstandingBySalesperson(email);
            return ResponseEntity.ok(outstandingList);
        } catch (Exception e) {
            LOGGER.error("Error fetching outstanding for salesperson {}: {}", email, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch outstanding for salesperson: " + e.getMessage()));
        }
    }

    @PostMapping("/send-all")
    public ResponseEntity<?> sendAllOutstandingEmails() {
        try {
            outstandingEmailService.processAndSendOutstandingEmails();
            return ResponseEntity.ok(Map.of(
                "message", "Outstanding emails processed successfully",
                "status", "success"
            ));
        } catch (Exception e) {
            LOGGER.error("Error sending outstanding emails: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                        "error", "Failed to send outstanding emails: " + e.getMessage(),
                        "status", "error"
                    ));
        }
    }

    @PostMapping("/send/{email}")
    public ResponseEntity<?> sendOutstandingEmailToSalesperson(@PathVariable String email) {
        try {
            outstandingEmailService.sendOutstandingForSalesperson(email);
            return ResponseEntity.ok(Map.of(
                "message", "Outstanding email sent successfully to: " + email,
                "status", "success"
            ));
        } catch (MessagingException e) {
            LOGGER.error("Error sending outstanding email to {}: {}", email, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                        "error", "Failed to send email: " + e.getMessage(),
                        "status", "error"
                    ));
        } catch (RuntimeException e) {
            LOGGER.error("Error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                        "error", e.getMessage(),
                        "status", "error"
                    ));
        }
    }

    @PostMapping("/test")
    public ResponseEntity<?> testOutstandingEmail() {
        try {
            // Create test data
            List<CurrentOutstandingDTO> testList = currentOutstandingRepo.getCurrentOutstandingWithEmailDetails();
            
            if (testList.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "No test data available"));
            }
            
            // Get first salesperson's email
            String firstEmail = testList.stream()
                    .map(CurrentOutstandingDTO::getSalespersonEmail)
                    .filter(e -> e != null && !e.isEmpty())
                    .findFirst()
                    .orElse("gjayabalan08@gmail.com");
            
            // Create a new variable for the target email
            String targetEmail;
            List<CurrentOutstandingDTO> filteredList;
            
            // Filter for that salesperson
            List<CurrentOutstandingDTO> salespersonList = testList.stream()
                    .filter(d -> firstEmail.equals(d.getSalespersonEmail()))
                    .collect(Collectors.toList());
            
            if (!salespersonList.isEmpty()) {
                filteredList = salespersonList;
                targetEmail = firstEmail;
            } else {
                // If no records for that email, take first 5 records and use default email
                filteredList = testList.subList(0, Math.min(5, testList.size()));
                targetEmail = "gjayabalan08@gmail.com";
            }
            
            List<String> testCC = List.of("gjayabalan08@gmail.com");
            
            outstandingEmailService.sendOutstandingEmailToSalesperson(targetEmail, filteredList, testCC);
            
            return ResponseEntity.ok(Map.of(
                "message", "Test email sent successfully to: " + targetEmail,
                "status", "success",
                "recordsSent", filteredList.size()
            ));
        } catch (Exception e) {
            LOGGER.error("Error sending test email: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                        "error", "Failed to send test email: " + e.getMessage(),
                        "status", "error"
                    ));
        }
    }
    
    // Alternative simpler test method
    @PostMapping("/test-simple")
    public ResponseEntity<?> testSimpleOutstandingEmail(@RequestParam(required = false) String email) {
        try {
            // Create test data
            List<CurrentOutstandingDTO> testList = currentOutstandingRepo.getCurrentOutstandingWithEmailDetails();
            
            if (testList.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "No test data available"));
            }
            
            // Use provided email or default
            String targetEmail = (email != null && !email.isEmpty()) ? email : "gjayabalan08@gmail.com";
            
            // Filter for that email or take first 5 if none
            List<CurrentOutstandingDTO> filteredList = testList.stream()
                    .filter(d -> targetEmail.equals(d.getSalespersonEmail()))
                    .collect(Collectors.toList());
            
            if (filteredList.isEmpty()) {
                filteredList = testList.subList(0, Math.min(5, testList.size()));
            }
            
            List<String> testCC = List.of("gjayabalan08@gmail.com");
            
            outstandingEmailService.sendOutstandingEmailToSalesperson(targetEmail, filteredList, testCC);
            
            return ResponseEntity.ok(Map.of(
                "message", "Test email sent successfully to: " + targetEmail,
                "status", "success",
                "recordsSent", filteredList.size()
            ));
        } catch (Exception e) {
            LOGGER.error("Error sending test email: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                        "error", "Failed to send test email: " + e.getMessage(),
                        "status", "error"
                    ));
        }
    }
}