package com.invoice.approval.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.invoice.approval.dto.SubledgerAlertDTO;
import com.invoice.approval.repo.SubledgerRepo;

//Spring Boot Controller
@RestController
@RequestMapping("/api/subledgers")
@CrossOrigin(origins = "*")
public class SubledgerController extends BaseController{

 @Autowired
 private SubledgerRepo subledgerRepo;

 // Get all high percentage subledgers
 @GetMapping("/all-high-percentage")
 public ResponseEntity<List<SubledgerAlertDTO>> getAllHighPercentageSubledgers() {
     try {
         List<SubledgerAlertDTO> alerts = subledgerRepo.getSubledgersWithHighPercentage();
         return ResponseEntity.ok(alerts);
     } catch (Exception e) {
         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
     }
 }

 // Get subledgers filtered by logged-in employee's email
 @GetMapping("/alerts")
 public ResponseEntity<List<SubledgerAlertDTO>> getAlertsForEmployee(
         @RequestParam String email) {
     try {
         List<SubledgerAlertDTO> allAlerts = subledgerRepo.getSubledgersWithHighPercentage();
         
         // Filter alerts for the specific employee
         List<SubledgerAlertDTO> filteredAlerts = allAlerts.stream()
                 .filter(alert -> {
                     String alertEmail = alert.getMailid() != null ? 
                         alert.getMailid().toLowerCase() : "";
                     String paramEmail = email.toLowerCase();
                     
                     // Check if the employee's email matches the alert email or ccmail
                     return alertEmail.contains(paramEmail) || 
                            (alert.getCcmail() != null && 
                             alert.getCcmail().toLowerCase().contains(paramEmail));
                 })
                 .collect(Collectors.toList());
         
         return ResponseEntity.ok(filteredAlerts);
     } catch (Exception e) {
         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
     }
 }
}
