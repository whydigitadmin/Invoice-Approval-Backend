package com.invoice.approval.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_action")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserActionVO {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private long actionId;
	private String userName;
	private long userId;
	private String actionType;
	private Date actionDate;
	private String loginIp;
	
	@PrePersist
	public void onSave() {
		Date currentDate = new Date();
		this.actionDate = currentDate;
	}
	
}
