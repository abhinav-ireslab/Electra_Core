package com.ireslab.sendx.electra.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.ireslab.sendx.electra.utils.TokenActivity;
import com.ireslab.sendx.electra.utils.TokenOperation;

/**
 * The persistent class for the transaction_details database table.
 * 
 */
@Entity
@Table(name = "transaction_details")
@NamedQuery(name = "TransactionDetail.findAll", query = "SELECT t FROM TransactionDetail t")
public class TransactionDetail implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "transaction_detail_id")
	private Integer transactionDetailId;

	@Column(name = "sequence_no", nullable = true)
	private String transactionSequenceNo;

	@Column(name = "operation", nullable = true)
	@Enumerated(EnumType.STRING)
	private TokenOperation operation; // payment,change_trust,create account -stellar

	@Column(name = "type", nullable = true)
	@Enumerated(EnumType.STRING)
	private TokenActivity type; // LOAD TOKEN, ISSUE TOKEN, CREATE ACCOUNT

	@Column(name = "stellar_request", nullable = true)
	private String stellarRequest;

	@Column(name = "stellar_response", nullable = true)
	private String stellarResponse;

	@Column(name = "source_account_name", nullable = true)
	private String sourceAccountName;

	@Column(name = "is_source_account_user", nullable = true)
	private boolean isSourceAccountUser;

	@Column(name = "source_correlation_id", nullable = true)
	private String sourceCorrelationId; // (User correlation id, Client Correllation id)

	@Column(name = "destination_account_name", nullable = true)
	private String destinationAccountName;

	@Column(name = "is_destination_account_user", nullable = true)
	private boolean isDestinationAccountUser;

	@Column(name = "destination_correlation_id", nullable = true)
	private String destinationCorrelationId; // (User correlation id, Client Correllation id)

	@Column(name = "status", nullable = true)
	private short status; // 0-pending,1-success,2-failed

	@Column(name = "tnx_data", nullable = true)
	private String tnxData;

	@Column(name = "asset_code", nullable = true)
	private String assetCode;

	@Column(name = "tnx_hash", nullable = true)
	private String tnxHash;

	@Column(name = "transaction_xdr")
	private String transactionXdr;
	
	@Column(name = "transaction_purpose")
	private String transactionPurpose;

	@Column(name = "transaction_date")
	@Temporal(TemporalType.TIMESTAMP)
	private Date transactionDate;

	@Column(name = "modified_date")
	@Temporal(TemporalType.TIMESTAMP)
	private Date modifiedDate;
	
	@Column(name = "is_fee", nullable = true)
	private boolean isFee;
	
	@Column(name = "is_offline")
	private boolean isOffline;


	public TransactionDetail() {
	}

	public TransactionDetail(Integer transactionDetailId, String transactionSequenceNo, TokenOperation operation,
			TokenActivity type, String stellarRequest, String stellarResponse, String sourceAccountName,
			boolean isSourceAccountUser, String sourceCorrelationId, String destinationAccountName,
			boolean isDestinationAccountUser, String destinationCorrelationId, short status, String tnxData,
			String assetCode, String tnxHash, String transactionXdr, Date transactionDate, Date modifiedDate, String transactionPurpose) {
		super();
		this.transactionDetailId = transactionDetailId;
		this.operation = operation;
		this.type = type;
		this.stellarRequest = stellarRequest;
		this.stellarResponse = stellarResponse;
		this.sourceAccountName = sourceAccountName;
		this.isSourceAccountUser = isSourceAccountUser;
		this.sourceCorrelationId = sourceCorrelationId;
		this.destinationAccountName = destinationAccountName;
		this.isDestinationAccountUser = isDestinationAccountUser;
		this.destinationCorrelationId = destinationCorrelationId;
		this.status = status;
		this.tnxData = tnxData;
		this.assetCode = assetCode;
		this.tnxHash = tnxHash;
		this.transactionXdr = transactionXdr;
		this.transactionPurpose = transactionPurpose;
		this.transactionDate = transactionDate;
		this.modifiedDate = modifiedDate;
	}

	public Integer getTransactionDetailId() {
		return transactionDetailId;
	}

	public void setTransactionDetailId(Integer transactionDetailId) {
		this.transactionDetailId = transactionDetailId;
	}

	public String getTransactionSequenceNo() {
		return transactionSequenceNo;
	}

	public void setTransactionSequenceNo(String transactionSequenceNo) {
		this.transactionSequenceNo = transactionSequenceNo;
	}

	public TokenOperation getOperation() {
		return operation;
	}

	public void setOperation(TokenOperation operation) {
		this.operation = operation;
	}

	public TokenActivity getType() {
		return type;
	}

	public void setType(TokenActivity type) {
		this.type = type;
	}

	public String getStellarRequest() {
		return stellarRequest;
	}

	public void setStellarRequest(String stellarRequest) {
		this.stellarRequest = stellarRequest;
	}

	public String getStellarResponse() {
		return stellarResponse;
	}

	public void setStellarResponse(String stellarResponse) {
		this.stellarResponse = stellarResponse;
	}

	public boolean isSourceAccountUser() {
		return isSourceAccountUser;
	}

	public void setSourceAccountUser(boolean isSourceAccountUser) {
		this.isSourceAccountUser = isSourceAccountUser;
	}

	public String getSourceCorrelationId() {
		return sourceCorrelationId;
	}

	public void setSourceCorrelationId(String sourceCorrelationId) {
		this.sourceCorrelationId = sourceCorrelationId;
	}

	public String getDestinationAccountName() {
		return destinationAccountName;
	}

	public void setDestinationAccountName(String destinationAccountName) {
		this.destinationAccountName = destinationAccountName;
	}

	public boolean isDestinationAccountUser() {
		return isDestinationAccountUser;
	}

	public void setDestinationAccountUser(boolean isDestinationAccountUser) {
		this.isDestinationAccountUser = isDestinationAccountUser;
	}

	public String getDestinationCorrelationId() {
		return destinationCorrelationId;
	}

	public void setDestinationCorrelationId(String destinationCorrelationId) {
		this.destinationCorrelationId = destinationCorrelationId;
	}

	public short getStatus() {
		return status;
	}

	public void setStatus(short status) {
		this.status = status;
	}

	public String getTnxData() {
		return tnxData;
	}

	public void setTnxData(String tnxData) {
		this.tnxData = tnxData;
	}

	public String getAssetCode() {
		return assetCode;
	}

	public void setAssetCode(String assetCode) {
		this.assetCode = assetCode;
	}

	public String getTnxHash() {
		return tnxHash;
	}

	public void setTnxHash(String tnxHash) {
		this.tnxHash = tnxHash;
	}

	public String getTransactionXdr() {
		return transactionXdr;
	}

	public void setTransactionXdr(String transactionXdr) {
		this.transactionXdr = transactionXdr;
	}

	public Date getTransactionDate() {
		return transactionDate;
	}

	public void setTransactionDate(Date transactionDate) {
		this.transactionDate = transactionDate;
	}

	public Date getModifiedDate() {
		return modifiedDate;
	}

	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	public String getSourceAccountName() {
		return sourceAccountName;
	}

	public void setSourceAccountName(String sourceAccountName) {
		this.sourceAccountName = sourceAccountName;
	}

	/**
	 * @return the transactionPurpose
	 */
	public String getTransactionPurpose() {
		return transactionPurpose;
	}

	/**
	 * @param transactionPurpose the transactionPurpose to set
	 */
	public void setTransactionPurpose(String transactionPurpose) {
		this.transactionPurpose = transactionPurpose;
	}

	/**
	 * @return the isFee
	 */
	public boolean isFee() {
		return isFee;
	}

	/**
	 * @param isFee the isFee to set
	 */
	public void setFee(boolean isFee) {
		this.isFee = isFee;
	}

	

	public boolean getIsOffline() {
		return isOffline;
	}

	public void setIsOffline(boolean isOffline) {
		this.isOffline = isOffline;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "TransactionDetail [transactionDetailId=" + transactionDetailId + ", transactionSequenceNo="
				+ transactionSequenceNo + ", operation=" + operation + ", type=" + type + ", stellarRequest="
				+ stellarRequest + ", stellarResponse=" + stellarResponse + ", sourceAccountName=" + sourceAccountName
				+ ", isSourceAccountUser=" + isSourceAccountUser + ", sourceCorrelationId=" + sourceCorrelationId
				+ ", destinationAccountName=" + destinationAccountName + ", isDestinationAccountUser="
				+ isDestinationAccountUser + ", destinationCorrelationId=" + destinationCorrelationId + ", status="
				+ status + ", tnxData=" + tnxData + ", assetCode=" + assetCode + ", tnxHash=" + tnxHash
				+ ", transactionXdr=" + transactionXdr + ", transactionPurpose=" + transactionPurpose
				+ ", transactionDate=" + transactionDate + ", modifiedDate=" + modifiedDate + ", isFee=" + isFee + "]";
	}

	
	

	
}