package com.invoice.approval.entity;

import java.sql.Blob;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name="tcsirnres")
public class IRNQRVO {

	@Id
	private String documentNumber;
	
	@Lob
	@Column(columnDefinition = "CLOB")
	private String sqr;
	
	@Lob
	@Column(columnDefinition = "CLOB")
	private String sinv;

}
