package com.invoice.approval.entity;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="EMPLOYEEATTACHMENT") // Oracle typically uses uppercase
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeAttachmentVO {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "employeeattachmentgen")
    @SequenceGenerator(name = "employeeattachmentgen", sequenceName = "EMPLOYEEATTACHMENTSEQ", // Uppercase for Oracle
                      initialValue = 1000000001, allocationSize = 1)
    @Column(name = "EMPLOYEEATTACHMENTID")
    private Long id;
    
    @Column(name = "EMPLOYEEEMAIL")
    private String employeeEmail;
    
    @Column(name = "TEXTFILENAME")
    private String textFileName;
    
    @Lob
    @Column(name = "TEXTFILEDATA")
    private byte[] textFileData;
    
    @Column(name = "PDFFILENAME")
    private String pdfFileName;
    
    @Lob
    @Column(name = "PDFFILEDATA")
    private byte[] pdfFileData;
    
    @Column(nullable = false, columnDefinition = "VARCHAR2(1) DEFAULT 'F'") // Oracle uses VARCHAR2
    private String active = "F";
    
    @Column(name = "SCHEDULEDTIME")
    private LocalDateTime scheduledTime;
    
    @Column(name = "IS_SCHEDULED", columnDefinition = "NUMBER(1) DEFAULT 0") // Oracle doesn't have boolean
    private boolean isScheduled = false;
    
    @Column(name = "IS_SENT", columnDefinition = "NUMBER(1) DEFAULT 0") // 1=true, 0=false
    private boolean isSent = false;
    
    @Column(name = "EMAIL_SUBJECT")
    private String emailSubject;
}