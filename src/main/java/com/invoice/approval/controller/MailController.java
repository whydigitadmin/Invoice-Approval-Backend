package com.invoice.approval.controller;


import com.invoice.approval.service.EmailService;

import lombok.Data;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/mail")
public class MailController {

    @Autowired
    private EmailService emailService;

    @PostMapping("/send")
    public ResponseEntity<String> sendMail(@RequestBody MailRequest mail) {
        emailService.sendMail(mail.getTo(), mail.getSubject(), mail.getBody());
        return ResponseEntity.ok("Mail queued");
    }
    
    @Data
    static class MailRequest {
    	private List<String> to;  
        private String subject;
        private String body;
    }
}
