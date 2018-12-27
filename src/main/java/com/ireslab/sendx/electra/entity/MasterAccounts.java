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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "master_accounts")
@NamedQuery(name = "MasterAccounts.findAll", query = "SELECT c FROM MasterAccounts c")
public class MasterAccounts implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "master_account_id", unique = true, nullable = false)
	private Integer masterAccountId;

	@Column(name = "is_testnet_account", nullable = false)
	private boolean isTestnetAccount;

	@Column(name = "issuing_account_public_key", length = 255)
	private String issuingAccountPublicKey;

	@Column(name = "issuing_account_secret_key", length = 255)
	private String issuingAccountSecretKey;

	@Column(name = "base_txn_account_public_key", length = 255)
	private String baseTxnAccountPublicKey;

	@Column(name = "base_txn_account_secret_key", length = 255)
	private String baseTxnAccountSecretKey;
	
	@Column(name = "created_by")
	private Integer createdBy;
	
	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	@Column(name = "created_date", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdDate;
	
	
	@Column(name = "account_name", length = 255)
	private String accountName;
	
	

	public Integer getMasterAccountId() {
		return masterAccountId;
	}

	public void setMasterAccountId(Integer masterAccountId) {
		this.masterAccountId = masterAccountId;
	}

	public boolean isTestnetAccount() {
		return isTestnetAccount;
	}

	public void setTestnetAccount(boolean isTestnetAccount) {
		this.isTestnetAccount = isTestnetAccount;
	}

	public String getIssuingAccountPublicKey() {
		return issuingAccountPublicKey;
	}

	public void setIssuingAccountPublicKey(String issuingAccountPublicKey) {
		this.issuingAccountPublicKey = issuingAccountPublicKey;
	}

	public String getIssuingAccountSecretKey() {
		return issuingAccountSecretKey;
	}

	public void setIssuingAccountSecretKey(String issuingAccountSecretKey) {
		this.issuingAccountSecretKey = issuingAccountSecretKey;
	}

	public String getBaseTxnAccountPublicKey() {
		return baseTxnAccountPublicKey;
	}

	public void setBaseTxnAccountPublicKey(String baseTxnAccountPublicKey) {
		this.baseTxnAccountPublicKey = baseTxnAccountPublicKey;
	}

	public String getBaseTxnAccountSecretKey() {
		return baseTxnAccountSecretKey;
	}

	public void setBaseTxnAccountSecretKey(String baseTxnAccountSecretKey) {
		this.baseTxnAccountSecretKey = baseTxnAccountSecretKey;
	}

	public Integer getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(Integer createdBy) {
		this.createdBy = createdBy;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}


}
