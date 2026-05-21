package com.invoice.approval;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableAspectJAutoProxy(proxyTargetClass = true)
@EnableScheduling
public class InvoiceApprovalApplication {

	public static void main(String[] args) {
		SpringApplication.run(InvoiceApprovalApplication.class, args);
	}
	
	  @Bean
	    public RestTemplate restTemplate() {
	        return new RestTemplate();
	    }

}
