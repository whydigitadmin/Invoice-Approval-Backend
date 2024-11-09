package com.invoice.approval.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "token")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TokenVO {

	@Id
	private String id;
	private Date createdDate;
	private Date expDate;
	private long userId;
	@Transient
	private String token;
}
