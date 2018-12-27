package com.ireslab.sendx.electra.entity;

import java.io.Serializable;
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
@Table(name = "master_transaction_purpose")
@NamedQuery(name = "MasterTransactionPurpose.findAll", query = "SELECT m FROM MasterTransactionPurpose m")
public class MasterTransactionPurpose implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "master_purpose_id", unique = true, nullable = false)
	private Integer masterPurposeId;
	
	@Column(name = "purpose_title", length = 50)
	private String purposeTitle;
	
	@Column(name = "created_date", nullable = false)
	private Date createdDate;

	/**
	 * @return the masterPurposeId
	 */
	public Integer getMasterPurposeId() {
		return masterPurposeId;
	}

	/**
	 * @param masterPurposeId the masterPurposeId to set
	 */
	public void setMasterPurposeId(Integer masterPurposeId) {
		this.masterPurposeId = masterPurposeId;
	}

	/**
	 * @return the purposeTitle
	 */
	public String getPurposeTitle() {
		return purposeTitle;
	}

	/**
	 * @param purposeTitle the purposeTitle to set
	 */
	public void setPurposeTitle(String purposeTitle) {
		this.purposeTitle = purposeTitle;
	}

	/**
	 * @return the createdDate
	 */
	public Date getCreatedDate() {
		return createdDate;
	}

	/**
	 * @param createdDate the createdDate to set
	 */
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}
	
	
	
}
