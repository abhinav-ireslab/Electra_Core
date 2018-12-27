package com.ireslab.sendx.electra.entity;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.ireslab.sendx.electra.utils.Status;

@Entity
@Table(name = "settlement")
@NamedQuery(name = "CashOut.findAll", query = "SELECT s FROM CashOut s")
public class CashOut implements Serializable {

	private static final long serialVersionUID = 1L;
	
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "settlement_id")
	private Integer settlementId;

	@Column(name = "requester_name")
	private String requesterName;
	
	@Column(name = "country_dial_code", nullable = true)
	private String countryDialCode;
	
	@Column(name = "beneficiary_mobile_number")
	private String beneficiaryMobileNumber;

	@Column(name = "no_of_tokens")
	private String noOfTokens;

	@Column(name = "is_cash_out")
	private boolean isCashOut;

	@Column(name = "institution_name")
	private String institutionName;

	@Column(name = "institution_account_number")
	private String institutionAccountNumber;

	@Column(name = "additional_institution_info")
	private String additionalInstitutionInfo;

	@Column(name = "created_date")
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdDate;

	@Column(name = "modified_date")
	@Temporal(TemporalType.TIMESTAMP)
	private Date modifiedDate;
	
	@Column(name = "modified_by")
	private String modifiedBy;
	
	
	@Column(name = "correlation_id")
	private String userCorrelationId;
	
	@Column(name = "fee")
	private String fee;
	
	@Column(name = "status")
	private String status;
	
	
	

	public String getModifiedBy() {
		return modifiedBy;
	}

	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	public String getRequesterName() {
		return requesterName;
	}

	public void setRequesterName(String requesterName) {
		this.requesterName = requesterName;
	}

	public String getCountryDialCode() {
		return countryDialCode;
	}

	public void setCountryDialCode(String countryDialCode) {
		this.countryDialCode = countryDialCode;
	}

	public String getUserCorrelationId() {
		return userCorrelationId;
	}

	public void setUserCorrelationId(String userCorrelationId) {
		this.userCorrelationId = userCorrelationId;
	}

	public String getFee() {
		return fee;
	}

	public void setFee(String fee) {
		this.fee = fee;
	}

	public Integer getSettlementId() {
		return settlementId;
	}

	public void setSettlementId(Integer settlementId) {
		this.settlementId = settlementId;
	}

	

	public String getNoOfTokens() {
		return noOfTokens;
	}

	public void setNoOfTokens(String noOfTokens) {
		this.noOfTokens = noOfTokens;
	}

	public boolean isCashOut() {
		return isCashOut;
	}

	public void setCashOut(boolean isCashOut) {
		this.isCashOut = isCashOut;
	}

	public String getInstitutionName() {
		return institutionName;
	}

	public void setInstitutionName(String institutionName) {
		this.institutionName = institutionName;
	}

	public String getInstitutionAccountNumber() {
		return institutionAccountNumber;
	}

	public void setInstitutionAccountNumber(String institutionAccountNumber) {
		this.institutionAccountNumber = institutionAccountNumber;
	}

	public String getAdditionalInstitutionInfo() {
		return additionalInstitutionInfo;
	}

	public void setAdditionalInstitutionInfo(String additionalInstitutionInfo) {
		this.additionalInstitutionInfo = additionalInstitutionInfo;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Date getModifiedDate() {
		return modifiedDate;
	}

	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	

	public String getBeneficiaryMobileNumber() {
		return beneficiaryMobileNumber;
	}

	public void setBeneficiaryMobileNumber(String beneficiaryMobileNumber) {
		this.beneficiaryMobileNumber = beneficiaryMobileNumber;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	

	
	
}
