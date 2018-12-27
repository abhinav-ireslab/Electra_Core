/**
 * 
 */
package com.ireslab.sendx.electra.entity;

import java.io.Serializable;

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
@Table(name = "bank_details")
@NamedQuery(name = "BankDetails.findAll", query = "SELECT b FROM BankDetails b")
public class BankDetails implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "bank_detail_id", unique = true, nullable = false)
	private Integer bankDetailId;
	
	@Column(name = "client_id", nullable = false)
	private Integer clientId;
	
	@Column(name = "bank_name", nullable = false)
	private String bankName;
	
	@Column(name = "account_number", nullable = false)
	private String accountNumber;
	
	@Column(name = "ifsc_code", nullable = true)
	private String ifscCode;
	
	@Column(name = "swift_code", nullable = true)
	private String swiftCode;
	
	@Column(name = "account_type", nullable = true)
	private String accountType;
	
	@Column(name = "is_client")
	private boolean isClient;

	/**
	 * @return the bankDetailId
	 */
	public Integer getBankDetailId() {
		return bankDetailId;
	}

	/**
	 * @param bankDetailId the bankDetailId to set
	 */
	public void setBankDetailId(Integer bankDetailId) {
		this.bankDetailId = bankDetailId;
	}

	/**
	 * @return the clientId
	 */
	public Integer getClientId() {
		return clientId;
	}

	/**
	 * @param clientId the clientId to set
	 */
	public void setClientId(Integer clientId) {
		this.clientId = clientId;
	}

	/**
	 * @return the bankName
	 */
	public String getBankName() {
		return bankName;
	}

	/**
	 * @param bankName the bankName to set
	 */
	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	/**
	 * @return the accountNumber
	 */
	public String getAccountNumber() {
		return accountNumber;
	}

	/**
	 * @param accountNumber the accountNumber to set
	 */
	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	/**
	 * @return the ifscCode
	 */
	public String getIfscCode() {
		return ifscCode;
	}

	/**
	 * @param ifscCode the ifscCode to set
	 */
	public void setIfscCode(String ifscCode) {
		this.ifscCode = ifscCode;
	}

	/**
	 * @return the swiftCode
	 */
	public String getSwiftCode() {
		return swiftCode;
	}

	/**
	 * @param swiftCode the swiftCode to set
	 */
	public void setSwiftCode(String swiftCode) {
		this.swiftCode = swiftCode;
	}

	public String getAccountType() {
		return accountType;
	}

	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}

	public boolean isClient() {
		return isClient;
	}

	public void setClient(boolean isClient) {
		this.isClient = isClient;
	}

	
	
	

}
