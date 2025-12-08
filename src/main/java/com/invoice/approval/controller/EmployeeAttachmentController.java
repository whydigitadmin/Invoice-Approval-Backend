package com.invoice.approval.controller;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JsonParseException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.invoice.approval.dto.EmployeeAttachmentDTO;
import com.invoice.approval.dto.EmployeeBatchRequestDTO;
import com.invoice.approval.entity.EmployeeAttachmentVO;
import com.invoice.approval.repo.EmployeeAttachmentRepo;
import com.invoice.approval.service.EmailService;

import antlr.StringUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

@Tag(name = "Employee Attachment", description = "Employee Attachment Management APIs")
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/employeeattachment")
public class EmployeeAttachmentController extends BaseController {

    @Autowired
    private EmployeeAttachmentRepo empattachRepo;
    
    @Autowired
    private EmailService emailService;
    
    @Operation(
        summary = "Upload employee attachments", 
        description = "Upload text and PDF files for an employee",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(
                mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                schema = @Schema(implementation = EmployeeAttachmentDTO.class),
                examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                    value = "{\"employeeEmail\":\"user@example.com\", \"emailSubject\":\"Test Subject\", \"scheduledTime\":\"2025-08-01T17:35:00.000Z\"}"
                )
            )
        ),
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Files uploaded successfully",
                content = @Content(schema = @Schema(implementation = String.class))
            ),
            @ApiResponse(
                responseCode = "400",
                description = "Invalid input",
                content = @Content(schema = @Schema(implementation = String.class))
            ),
            @ApiResponse(
                responseCode = "500",
                description = "Internal server error",
                content = @Content(schema = @Schema(implementation = String.class))
            )
        }
    )
    
    @PostMapping(value = "/bulk-upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> bulkUploadAttachments(
            @RequestPart("employeeData") String employeeDataJson,
            @RequestPart("files") List<MultipartFile> files) {

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        try {
            // Parse JSON data
            List<EmployeeBatchRequestDTO> employees = objectMapper.readValue(
                employeeDataJson,
                new TypeReference<List<EmployeeBatchRequestDTO>>() {}
            );

            // Validate input
            if (employees.size() * 2 != files.size()) {
                return ResponseEntity.badRequest()
                    .body("File count mismatch. Expected " + (employees.size() * 2) + " files");
            }

            List<EmployeeAttachmentVO> savedAttachments = new ArrayList<>();
            DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;

            for (int i = 0; i < employees.size(); i++) {
            	EmployeeBatchRequestDTO request = employees.get(i);
                MultipartFile textFile = files.get(i * 2);
                MultipartFile pdfFile = files.get(i * 2 + 1);

                // Process each employee
                EmployeeAttachmentVO attachment = new EmployeeAttachmentVO();
//                attachment.setEmployeeEmail(request.getEmployeeEmail());
//                attachment.setEmailSubject(request.getEmailSubject());
//                attachment.setScheduledTime(LocalDateTime.parse(request.getScheduledTime(), formatter));
                attachment.setTextFileName(textFile.getOriginalFilename());
                attachment.setTextFileData(textFile.getBytes());
                attachment.setPdfFileName(pdfFile.getOriginalFilename());
                attachment.setPdfFileData(pdfFile.getBytes());
                attachment.setActive("F");
                attachment.setScheduled(false);

                savedAttachments.add(empattachRepo.save(attachment));
            }

            return ResponseEntity.ok()
                .body(Map.of(
                    "message", "Successfully processed " + savedAttachments.size() + " employees",
                    "processedCount", savedAttachments.size()
                ));

        } catch (JsonProcessingException e) {
            return ResponseEntity.badRequest()
                .body("Invalid JSON format: " + e.getMessage());
        } catch (IOException e) {
            return ResponseEntity.internalServerError()
                .body("File processing error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body("Bulk upload failed: " + e.getMessage());
        }
    }
    
//    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    public ResponseEntity<?> uploadAttachment(
//        @RequestPart("employees[0][employeeEmail]") String employeeEmail,
//        @RequestPart("employees[0][emailSubject]") String emailSubject,
//        @RequestPart("employees[0][scheduledTime]") String scheduledTime,
//        @RequestPart("employees[0][textFile]") MultipartFile textFile,
//        @RequestPart("employees[0][pdfFile]") MultipartFile pdfFile) {
//
//        try {
//            // Validate using the parameters directly
//            if (employeeEmail == null || employeeEmail.isEmpty()) {
//                return ResponseEntity.badRequest().body("Employee email is required");
//            }
//
//            DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME; // For "2025-08-01T17:35:00.000Z"
//            LocalDateTime scheduledDateTime = LocalDateTime.parse(scheduledTime, formatter);
//            
//            EmployeeAttachmentVO attachment = new EmployeeAttachmentVO();
//            attachment.setEmployeeEmail(employeeEmail);
//            attachment.setTextFileName(textFile.getOriginalFilename());
//            attachment.setTextFileData(textFile.getBytes());
//            attachment.setPdfFileName(pdfFile.getOriginalFilename());
//            attachment.setPdfFileData(pdfFile.getBytes());
//            attachment.setActive("F"); // Default value
//            attachment.setScheduledTime(scheduledDateTime); 
//            attachment.setScheduled(false); // Default value
//            attachment.setEmailSubject(emailSubject);
//
//            EmployeeAttachmentVO savedAttachment = empattachRepo.save(attachment);
//            
//            return ResponseEntity.ok()
//                .body("Attachment uploaded successfully with ID: " + savedAttachment.getId());
//        } catch (Exception e) {
//            return ResponseEntity.internalServerError()
//                .body("Error uploading attachment: " + e.getMessage());
//        }
//    }


    
//    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    public ResponseEntity<?> uploadAttachments(
//            @RequestPart("employees") EmployeeBatchRequestDTO employeeBatch,
//            @RequestPart("files") List<MultipartFile> files) {
//
//        List<EmployeeAttachmentVO> savedAttachments = new ArrayList<>();
//        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
//
//        try {
//            // Validate we have matching files and employees
//            if (files.size() != employeeBatch.getEmployees().size() * 2) {
//                return ResponseEntity.badRequest()
//                    .body("Number of files must be exactly 2 per employee");
//            }
//
//            for (int i = 0; i < employeeBatch.getEmployees().size(); i++) {
//                EmployeeAttachmentDTO dto = employeeBatch.getEmployees().get(i);
//                MultipartFile textFile = files.get(i * 2);
//                MultipartFile pdfFile = files.get(i * 2 + 1);
//
//                // Validate input
//                if (dto.getEmployeeEmail() == null || dto.getEmployeeEmail().isEmpty()) {
//                    return ResponseEntity.badRequest()
//                        .body("Employee email is required for all records");
//                }
//
//                EmployeeAttachmentVO attachment = new EmployeeAttachmentVO();
//                attachment.setEmployeeEmail(dto.getEmployeeEmail());
//                attachment.setTextFileName(textFile.getOriginalFilename());
//                attachment.setTextFileData(textFile.getBytes());
//                attachment.setPdfFileName(pdfFile.getOriginalFilename());
//                attachment.setPdfFileData(pdfFile.getBytes());
//                attachment.setActive("F");
//                
//                // Parse the date
//                try {
////                    attachment.setScheduledTime(LocalDateTime.parse(dto.getScheduledTime(), formatter));
//                } catch (DateTimeParseException e) {
//                    return ResponseEntity.badRequest()
//                        .body("Invalid date format for employee " + dto.getEmployeeEmail());
//                }
//                
//                attachment.setScheduled(false);
//                attachment.setEmailSubject(dto.getEmailSubject());
//
//                savedAttachments.add(empattachRepo.save(attachment));
//            }
//
//            return ResponseEntity.ok()
//                .body(savedAttachments.size() + " attachments uploaded successfully");
//        } catch (IOException e) {
//            return ResponseEntity.internalServerError()
//                .body("Error processing files: " + e.getMessage());
//        } catch (Exception e) {
//            return ResponseEntity.internalServerError()
//                .body("Error uploading attachments: " + e.getMessage());
//        }
//    }
//    
    
        
    
//    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    public ResponseEntity<?> uploadAttachments(HttpServletRequest request) {
//        try {
//            MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
//            
//            // Find all employee indices
//            Set<Integer> indices = new HashSet<>();
//            for (String name : multipartRequest.getParameterMap().keySet()) {
//                Matcher m = Pattern.compile("employees\\[(\\d+)\\]").matcher(name);
//                if (m.find()) indices.add(Integer.parseInt(m.group(1)));
//            }
//
//            List<EmployeeAttachmentVO> saved = new ArrayList<>();
//            DateTimeFormatter fmt = DateTimeFormatter.ISO_DATE_TIME;
//
//            for (int i : indices) {
//                // Get fields
//                String email = multipartRequest.getParameter("employees["+i+"][employeeEmail]");
//                String subject = multipartRequest.getParameter("employees["+i+"][emailSubject]");
//                String time = multipartRequest.getParameter("employees["+i+"][scheduledTime]");
//                
//                // Get files
//                MultipartFile text = multipartRequest.getFile("employees["+i+"][textFile]");
//                MultipartFile pdf = multipartRequest.getFile("employees["+i+"][pdfFile]");
//
//                // Validate
//                if (email == null || email.isEmpty()) {
//                    return ResponseEntity.badRequest().body("Email required for employee " + i);
//                }
//                if (text == null || pdf == null) {
//                    return ResponseEntity.badRequest().body("Files required for employee " + i);
//                }
//
//                // Create entity
//                EmployeeAttachmentVO att = new EmployeeAttachmentVO();
//                att.setEmployeeEmail(email);
//                att.setEmailSubject(subject);
//                if (time != null) att.setScheduledTime(LocalDateTime.parse(time, fmt));
//                att.setTextFileData(text.getBytes());
//                att.setPdfFileData(pdf.getBytes());
//                // Set other fields...
//
//                saved.add(empattachRepo.save(att));
//            }
//
//            return ResponseEntity.ok("Processed " + saved.size() + " employees");
//        } catch (Exception e) {
//            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
//        }
//    }
 
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadAttachments(HttpServletRequest request) {
        try {
            MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
            
            // Find all employee indices
            Set<Integer> employeeIndices = new HashSet<>();
            for (String paramName : multipartRequest.getParameterMap().keySet()) {
                Matcher matcher = Pattern.compile("employees\\[(\\d+)\\]").matcher(paramName);
                if (matcher.find()) {
                    employeeIndices.add(Integer.parseInt(matcher.group(1)));
                }
            }

            List<EmployeeAttachmentVO> savedAttachments = new ArrayList<>();
            DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;

            for (int index : employeeIndices) {
                // Get employee data
                String email = multipartRequest.getParameter("employees[" + index + "][employeeEmail]");
                String subject = multipartRequest.getParameter("employees[" + index + "][emailSubject]");
                String scheduledTime = multipartRequest.getParameter("employees[" + index + "][scheduledTime]");

                // Get files with their original filenames
                MultipartFile textFile = multipartRequest.getFile("employees[" + index + "][textFile]");
                MultipartFile pdfFile = multipartRequest.getFile("employees[" + index + "][pdfFile]");

                // Validate
                if (email == null || email.trim().isEmpty()) {
                    return ResponseEntity.badRequest().body("Email required for employee at index " + index);
                }

                // Create and save attachment
                EmployeeAttachmentVO attachment = new EmployeeAttachmentVO();
                attachment.setEmployeeEmail(email);
                attachment.setEmailSubject(subject);
                
                if (scheduledTime != null) {
                    attachment.setScheduledTime(LocalDateTime.parse(scheduledTime, formatter));
                }
                
                // Set filenames from the MultipartFile objects
     
                    attachment.setTextFileName(textFile.getOriginalFilename());
                    attachment.setTextFileData(textFile.getBytes());
     
                
     
                    attachment.setPdfFileName(pdfFile.getOriginalFilename());
                    attachment.setPdfFileData(pdfFile.getBytes());
     
                
                savedAttachments.add(empattachRepo.save(attachment));
            }

            return ResponseEntity.ok()
                .body(Map.of(
                    "message", "Successfully processed " + savedAttachments.size() + " employees",
                    "savedCount", savedAttachments.size()
                ));

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body("Upload failed: " + e.getMessage());
        }
    }
       
    private List<EmployeeAttachmentDTO> parseEmployeeData(Map<String, String> allParams) {
        List<EmployeeAttachmentDTO> employees = new ArrayList<>();
        
        // Group parameters by employee index
        Map<Integer, EmployeeAttachmentDTO> employeeMap = new HashMap<>();
        
        for (Map.Entry<String, String> entry : allParams.entrySet()) {
            if (entry.getKey().startsWith("employees[")) {
                // Extract index and field name
                Matcher matcher = Pattern.compile("employees\\[(\\d+)\\]\\[(\\w+)\\]").matcher(entry.getKey());
                if (matcher.find()) {
                    int index = Integer.parseInt(matcher.group(1));
                    String field = matcher.group(2);
                    
                    EmployeeAttachmentDTO dto = employeeMap.computeIfAbsent(index, k -> new EmployeeAttachmentDTO());
                    
                    switch (field) {
                        case "employeeEmail":
                            dto.setEmployeeEmail(entry.getValue());
                            break;
                        case "emailSubject":
                            dto.setEmailSubject(entry.getValue());
                            break;
                        case "scheduledTime":
//                            dto.setScheduledTime(entry.getValue());
                            break;
                    }
                }
            }
        }
        
        employees.addAll(employeeMap.values());
        return employees;
    }
    
    
    @Operation(
        summary = "Schedule email with attachments",
        description = "Schedule an email to be sent with previously uploaded attachments",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Email scheduled successfully",
                content = @Content(schema = @Schema(implementation = String.class))
            ),
            @ApiResponse(
                responseCode = "400",
                description = "No attachments found for employee",
                content = @Content(schema = @Schema(implementation = String.class))
            ),
            @ApiResponse(
                responseCode = "500",
                description = "Internal server error",
                content = @Content(schema = @Schema(implementation = String.class))
            )
        }
    )
    @PostMapping("/schedule")
    public ResponseEntity<?> scheduleEmail(@RequestBody EmployeeAttachmentDTO request) {
        try {
            Optional<EmployeeAttachmentVO> attachmentOpt = empattachRepo.findByEmployeeEmail(request.getEmployeeEmail());
            
            if (attachmentOpt.isEmpty()) {
                return ResponseEntity.badRequest()
                    .body("No attachments found for employee: " + request.getEmployeeEmail());
            }
            
            EmployeeAttachmentVO attachment = attachmentOpt.get();
            attachment.setScheduledTime(request.getScheduledTime());
            attachment.setScheduled(true);
            attachment.setEmailSubject(request.getEmailSubject());
            
            empattachRepo.save(attachment);
            
            // Optionally trigger email service here if needed
            // emailService.scheduleEmail(attachment);
            
            return ResponseEntity.ok()
                .body("Email scheduled successfully for " + request.getScheduledTime());
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body("Error scheduling email: " + e.getMessage());
        }
    }
}
