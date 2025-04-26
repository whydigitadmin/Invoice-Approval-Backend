package com.invoice.approval.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.invoice.approval.dto.CreatedUpdatedDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "GST_TICKET")
@Data
@NoArgsConstructor
@AllArgsConstructor

public class TicketVO {

	@SuppressWarnings("unused")
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ticketgen")
	@SequenceGenerator(name = "ticketgen", sequenceName = "ticketseq", initialValue = 1000000001, allocationSize = 1)
	@Column(name = "GST_TICKETID")
	private Long id;

	@Column(name = "title", length = 50)
	private String title;

	@Column(name = "description")
	private String description;

	@Column(name = "status")
	private String status = "Pending";

	@Column(name = "assignto")
	private String assignTo;

	@Column(name = "solvedby")
	private String solvedBy;

	@Column(name = "solvedon")
	private String solvedOn;

	@JsonManagedReference
	@OneToMany(mappedBy = "ticketVO", cascade = CascadeType.ALL)
	private List<TicketAttachmentVO> ticketAttachmentVO;

	@Column(name = "createdby")
	private String createdBy;
	@Column(name = "modifiedby")
	private String updatedBy;

	@Embedded
	private CreatedUpdatedDate commonDate = new CreatedUpdatedDate();

}
