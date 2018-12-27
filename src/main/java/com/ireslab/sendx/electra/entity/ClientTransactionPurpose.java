/**
 * 
 */
package com.ireslab.sendx.electra.entity;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 * @author ireslab
 *
 */
@Entity
@Table(name = "client_transaction_purpose")
@NamedQuery(name = "ClientTransactionPurpose.findAll", query = "SELECT t FROM ClientTransactionPurpose t")
public class ClientTransactionPurpose implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "client_purpose_id", unique = true, nullable = false)
	private Integer clientPurposeId;
	
	@Column(name = "client_id", nullable = false)
	private Integer clientId;
	
	@Column(name = "purpose_title", length = 50)
	private String purposeTitle;
	
	@Column(name = "created_date", nullable = false)
	private Date createdDate;
	
	@Column(name = "modified_date", nullable = false)
	private Timestamp modifiedDate;
	
	@Column(name = "audit_logon_id", nullable = true)
	private String auditLogonId;

	public Integer getClientPurposeId() {
		return clientPurposeId;
	}

	public void setClientPurposeId(Integer clientPurposeId) {
		this.clientPurposeId = clientPurposeId;
	}

	public Integer getClientId() {
		return clientId;
	}

	public void setClientId(Integer clientId) {
		this.clientId = clientId;
	}

	public String getPurposeTitle() {
		return purposeTitle;
	}

	public void setPurposeTitle(String purposeTitle) {
		this.purposeTitle = purposeTitle;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Timestamp getModifiedDate() {
		return modifiedDate;
	}

	public void setModifiedDate(Timestamp modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	public String getAuditLogonId() {
		return auditLogonId;
	}

	public void setAuditLogonId(String auditLogonId) {
		this.auditLogonId = auditLogonId;
	}

	

}
