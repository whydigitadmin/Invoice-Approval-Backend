package com.invoice.approval;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class InvoiceApprovalApplication {

	public static void main(String[] args) {
		SpringApplication.run(InvoiceApprovalApplication.class, args);
	}

}
