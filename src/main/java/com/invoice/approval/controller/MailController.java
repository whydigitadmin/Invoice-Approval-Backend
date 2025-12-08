package com.invoice.approval.controller;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.invoice.approval.common.CommonConstant;
import com.invoice.approval.dto.EmailRequestDTO;
import com.invoice.approval.dto.ResponseDTO;
import com.invoice.approval.service.EmailServiceAuto;
import com.invoice.approval.service.InvoiceApprovalService;

@RestController
@RequestMapping("/api/mail")
public class MailController extends BaseController{
	
	public static final Logger LOGGER = LoggerFactory.getLogger(InvoiceApprovalController.class);

    private final EmailServiceAuto emailServiceAuto;
    private final RestTemplate restTemplate;
    
    @Value("${whatsapp.service.url}")
    private String whatsappServiceUrl;
    
    @Autowired
	InvoiceApprovalService invoiceApprovalService;

    @Autowired
    public MailController(EmailServiceAuto emailServiceAuto, RestTemplate restTemplate) {
        this.emailServiceAuto = emailServiceAuto;
        this.restTemplate = restTemplate;
    }
    


    @GetMapping
    public ResponseEntity<?> getAvailableFiles() {
        try {
            System.out.println("Attempting to get available files...");
            System.out.println("Watch directory path: " + emailServiceAuto.getWatchDirectory());
            
            List<Map<String, String>> files = emailServiceAuto.getAvailableFiles();
            System.out.println("Found " + files.size() + " files");
            
            return ResponseEntity.ok(files);
        } catch (Exception e) {
            System.err.println("ERROR in getAvailableFiles:");
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, 
                "Failed to get files: " + e.getMessage(), e);
        }
    }

    @GetMapping("/content/{filename}")
    public ResponseEntity<String> getFileContent(@PathVariable String filename) {
        try {
            String content = emailServiceAuto.getFileContent(filename);
            return ResponseEntity.ok(content);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                "Error reading file: " + e.getMessage(), e);
        }
    }

//    @GetMapping("/download/{filename}")
//    public ResponseEntity<Resource> downloadFile(@PathVariable String filename) {
//        try {
//            Path file = Paths.get(emailServiceAuto.getWatchDirectory()).resolve(filename);
//            Resource resource = new UrlResource(file.toUri());
//            
//            if (resource.exists() && resource.isReadable()) {
//                return ResponseEntity.ok()
//                    .header("Content-Disposition", "attachment; filename=\"" + filename + "\"")
//                    .body(resource);
//            } else {
//                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "File not found");
//            }
//        } catch (Exception e) {
//            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, 
//                "Error downloading file: " + e.getMessage(), e);
//        }
//    }

    @GetMapping("/download/{filename}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String filename) {
        try {
            // Resolve file path
            Path file = Paths.get(emailServiceAuto.getWatchDirectory()).resolve(filename).normalize();

            // Create Resource object
            Resource resource = new UrlResource(file.toUri());

            if (resource.exists() && resource.isReadable()) {
                String contentType = Files.probeContentType(file);
                if (contentType == null) {
                    contentType = "application/octet-stream"; // fallback type
                }

                // Return the file with correct headers
                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                        .body(resource);
            } else {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "File not found or unreadable");
            }

        } catch (MalformedURLException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid file path", e);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error determining file type", e);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error downloading file", e);
        }
    }

    
//    @PostMapping("/send-emails")
//    public ResponseEntity<String> sendSelectedEmails(@RequestBody List<String> employeeCodes) {
//        try {
//            emailServiceAuto.sendSelectedEmails(employeeCodes);
//            return ResponseEntity.ok("Emails sent successfully");
//        } catch (Exception e) {
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
//                "Error sending emails: " + e.getMessage(), e);
//        }
//    }
    
    @PostMapping("/send-emails")
    public ResponseEntity<String> sendSelectedEmails(
        @RequestBody EmailRequestDTO emailRequest) {
        
        
        try {
            if (emailRequest.getEmployeeCodes() == null || emailRequest.getEmployeeCodes().isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                    "Employee codes list cannot be empty");
            }

            emailServiceAuto.sendSelectedEmails(
                emailRequest.getEmployeeCodes(), 
                emailRequest.getBccAddress()
            );
            return ResponseEntity.ok("Emails sent successfully");
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                "Error sending emails: " + e.getMessage(), e);
        }
    }
    
    
//    @PostMapping("/whatsapp/bulk")
//    public ResponseEntity<?> sendBulkWhatsAppMessages(
//        @RequestParam List<String> numbers,  // Accept list of numbers
//        @RequestParam(required = false) String name) {
//        
//        try {
//            // 1. Prepare the birthday message
//            String birthdayMessage = String.format(
//                "🎉 *Happy Birthday %s!* 🎉\n\n" +
//                "Wishing you a day filled with joy, laughter, and happiness!\n\n" +
//                "May this special day bring you endless smiles and may the year ahead be your best one yet!\n\n" +
//                "🎂 *Many happy returns of the day!* 🎂\n\n" +
//                "Best wishes,\n" +
//                "Your Company Team",
//                name != null ? name : ""
//            );
//
//            // 2. Prepare headers
//            HttpHeaders headers = new HttpHeaders();
//            headers.setContentType(MediaType.APPLICATION_JSON);
//            
//            // 3. Send to each number
//            List<Map<String, Object>> results = new ArrayList<>();
//            int successCount = 0;
//            
//            for (String number : numbers) {
//                try {
//                    Map<String, String> requestBody = new HashMap<>();
//                    requestBody.put("number", number);
//                    requestBody.put("message", birthdayMessage);
//                    
//                    ResponseEntity<String> response = restTemplate.postForEntity(
//                        whatsappServiceUrl,
//                        new HttpEntity<>(requestBody, headers),
//                        String.class
//                    );
//                    
//                    results.add(Map.of(
//                        "number", number,
//                        "status", "success",
//                        "response", response.getBody()
//                    ));
//                    successCount++;
//                    
//                } catch (Exception e) {
//                    results.add(Map.of(
//                        "number", number,
//                        "status", "failed",
//                        "error", e.getMessage()
//                    ));
//                }
//            }
//            
//            return ResponseEntity.ok(Map.of(
//                "totalNumbers", numbers.size(),
//                "successCount", successCount,
//                "failedCount", numbers.size() - successCount,
//                "results", results
//            ));
//            
//        } catch (ResourceAccessException e) {
//            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(Map.of(
//                "error", "WhatsApp service unreachable",
//                "solution", "1. Check if Node.js service is running\n2. Verify no firewall blocking port 3000"
//            ));
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
//                "error", "Unexpected error",
//                "details", e.getMessage()
//            ));
//        }
//    }
//        
//    @PostMapping("/whatsapp")
//    public ResponseEntity<?> testWhatsApp(
//        @RequestParam String number,
//        @RequestParam(required = false) String name) {  // Changed from 'message' to 'name'
//        
//    	try {
//    	    // 1. Prepare the birthday message
//    		 String birthdayMessage = String.format(
//    	                "🎉 *Happy Birthday %s!* 🎉\n\n" +
//    	                "Wishing you a day filled with joy, laughter, and happiness!\n\n" +
//    	                "May this special day bring you endless smiles and may the year ahead be your best one yet!\n\n" +
//    	                "🎂 *Many happy returns of the day!* 🎂\n\n" +
//    	                "Best wishes,\n" +
//    	                "Your Company Team",
//    	                name != null ? name : ""  // Use the provided name or empty string
//    	            );
//
//            // 2. Prepare request
//            HttpHeaders headers = new HttpHeaders();
//            headers.setContentType(MediaType.APPLICATION_JSON);
//            
//            Map<String, String> requestBody = new HashMap<>();
//            requestBody.put("number", number);
//            requestBody.put("message", birthdayMessage);  // Using the formatted message
//            
//            // 3. Make the request
//            ResponseEntity<String> response = restTemplate.postForEntity(
//                whatsappServiceUrl,
//                new HttpEntity<>(requestBody, headers),
//                String.class
//            );
//            
//            return ResponseEntity.ok(Map.of(
//                "status", "success",
//                "response", response.getBody()
//            ));
//            
//        } catch (ResourceAccessException e) {
//            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(Map.of(
//                "error", "WhatsApp service unreachable",
//                "solution", "1. Check if Node.js service is running\n2. Verify no firewall blocking port 3000"
//            ));
//        } catch (HttpClientErrorException e) {
//            return ResponseEntity.status(e.getStatusCode()).body(Map.of(
//                "error", "WhatsApp service error",
//                "details", e.getResponseBodyAsString()
//            ));
//        }
//    }
    
//    
//    @PostMapping("/whatsappfiles")
//    public ResponseEntity<?> sendWhatsAppMessage(
//        @RequestParam String number,
//        @RequestParam(required = false) String name,
//        @RequestParam(required = false) MultipartFile attachment) {
//        
//        try {
//            // 1. Prepare the birthday message
//            String birthdayMessage = String.format(
//                "🎉 *Happy Birthday %s!* 🎉\n\n" +
//                name != null ? name : ""
//            );
//
//            // 2. Prepare request
//            HttpHeaders headers = new HttpHeaders();
//            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
//            
//            // 3. Create multipart request
//            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
//            body.add("number", number);
//            body.add("message", birthdayMessage);
//            
//            if (attachment != null && !attachment.isEmpty()) {
//                body.add("attachment", new ByteArrayResource(attachment.getBytes()) {
//                    @Override
//                    public String getFilename() {
//                        return attachment.getOriginalFilename();
//                    }
//                });
//            }
//
//            // 4. Make the request
//            ResponseEntity<String> response = restTemplate.postForEntity(
//                whatsappServiceUrl,
//                new HttpEntity<>(body, headers),
//                String.class
//            );
//            
//            return ResponseEntity.ok(Map.of(
//                "status", "success",
//                "response", response.getBody()
//            ));
//            
//        } catch (ResourceAccessException e) {
//            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(Map.of(
//                "error", "WhatsApp service unreachable",
//                "solution", "1. Check if Node.js service is running\n2. Verify no firewall blocking port 3000"
//            ));
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
//                "error", "Failed to send WhatsApp message",
//                "details", e.getMessage()
//            ));
//        }
//    }
//    
    
    
//    public String formatWhatsAppMessage(List<Map<String, Object>> pendingApprovalDetails) {
//        StringBuilder whatsappMsg = new StringBuilder();
//        whatsappMsg.append("📋 *Pending Approval Report*\n\n");
//        
//        for (Map<String, Object> record : pendingApprovalDetails) {
//            whatsappMsg.append("🏢 *Party:* ").append(record.get("partyName")).append("\n");
//            whatsappMsg.append("📄 *Document ID:* ").append(record.get("docId")).append("\n");
//            whatsappMsg.append("📅 *Date:* ").append(record.get("docDate")).append("\n");
//            whatsappMsg.append("💵 *Amount:* ").append(record.get("totalInvAmtLc")).append("\n");
//            whatsappMsg.append("⏳ *Days Exceeded:* ").append(record.get("exceedDays")).append("\n");
//            whatsappMsg.append("🚩 *Remarks:* ").append(record.get("slabRemarks")).append("\n");
//            whatsappMsg.append("👤 *Salesperson:* ").append(record.get("salespersonName")).append("\n");
//            whatsappMsg.append("--------------------------------\n\n");
//        }
//        
//        whatsappMsg.append("Total Records: ").append(pendingApprovalDetails.size());
//        return whatsappMsg.toString();
//    }
//    
    
    
//    @GetMapping("/getPendingDetailsAndSend")
//    public ResponseEntity<ResponseDTO> getPendingDetailsAndSend(
//        @RequestParam String userType,
//        @RequestParam String userName,
//        @RequestParam String whatsappNumber) {
//        
//        String methodName = "getPendingDetailsAndSend()";
//        LOGGER.debug(CommonConstant.STARTING_METHOD, methodName);
//        
//        Map<String, Object> responseObjectsMap = new HashMap<>();
//        ResponseDTO responseDTO;
//        List<Map<String, Object>> pendingApprovalDetails = new ArrayList<>();
//        
//        try {
//            // 1. Get pending approval details
//            pendingApprovalDetails = invoiceApprovalService.getPendingApprovalReport(userType, userName);
//            
//            if (pendingApprovalDetails.isEmpty()) {
//                responseObjectsMap.put("message", "No pending approvals found");
//                responseDTO = createServiceResponse(responseObjectsMap);
//                return ResponseEntity.ok().body(responseDTO);
//            }
//            
//            // 2. Format WhatsApp message
//            String whatsappMessage = formatWhatsAppMessage(pendingApprovalDetails);
//            responseObjectsMap.put("whatsappMessage", whatsappMessage);
//            
//            // 3. Validate WhatsApp number
//            if (!whatsappNumber.matches("^\\+?[0-9]{10,15}$")) {
//                throw new IllegalArgumentException("Invalid WhatsApp number format");
//            }
//            
//            // 4. Send to WhatsApp
//            HttpHeaders headers = new HttpHeaders();
//            headers.setContentType(MediaType.APPLICATION_JSON);
//            
//            Map<String, String> requestBody = new HashMap<>();
//            requestBody.put("number", whatsappNumber);
//            requestBody.put("message", whatsappMessage);
//            
//            ResponseEntity<String> whatsappResponse = restTemplate.postForEntity(
//                whatsappServiceUrl,
//                new HttpEntity<>(requestBody, headers),
//                String.class
//            );
//            
//            // 5. Prepare response
//            responseObjectsMap.put("pendingApprovalDetails", pendingApprovalDetails);
//            responseObjectsMap.put("whatsappStatus", whatsappResponse.getStatusCode().toString());
//            responseObjectsMap.put("whatsappResponse", whatsappResponse.getBody());
//            
//            responseDTO = createServiceResponse(responseObjectsMap);
//            
//        } catch (Exception e) {
//            LOGGER.error("Error in {}: {}", methodName, e.getMessage());
//            responseDTO = createServiceResponseError(
//                responseObjectsMap, 
//                "Failed to process pending details and send WhatsApp",
//                e.getMessage()
//            );
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseDTO);
//        }
//        
//        LOGGER.debug(CommonConstant.ENDING_METHOD, methodName);
//        return ResponseEntity.ok().body(responseDTO);
//    }
 
    
}