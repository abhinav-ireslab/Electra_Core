package com.ireslab.sendx.electra.entity;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.ireslab.sendx.electra.utils.Status;

/**
 * The persistent class for the client_asset_tokens database table.
 * 
 */
@Entity
@Table(name = "client_asset_tokens")
@NamedQuery(name = "ClientAssetToken.findAll", query = "SELECT c FROM ClientAssetToken c")
public class ClientAssetToken implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "token_id", unique = true, nullable = false)
	private Integer tokenId;

	@Column(name = "issuing_account_public_key", length = 255)
	private String issuingAccountPublicKey;

	@Column(name = "issuing_account_secret_key", length = 255)
	private String issuingAccountSecretKey;

	@Column(name = "base_txn_account_public_key", length = 255)
	private String baseTxnAccountPublicKey;

	@Column(name = "base_txn_account_secret_key", length = 255)
	private String baseTxnAccountSecretKey;

	@Column(name = "batch_quantity", nullable = false)
	private BigInteger batchQuantity;

	@Column(name = "provisioned_quantity")
	private BigInteger provisionedQuantity;

	@Column(name="status")
	@Enumerated(EnumType.STRING)
	private Status status;

	@Column(name = "token_code", nullable = false, length = 20)
	private String tokenCode;

	@Column(name = "token_correlation_id", nullable = false, length = 50)
	private String tokenCorrelationId;

	@Column(name = "token_description", length = 255)
	private String tokenDescription;

	@Column(name = "token_name", nullable = false, length = 50)
	private String tokenName;

	@Column(name = "created_by")
	private Integer createdBy;

	@Column(name = "created_date", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdDate;

	@Column(name = "modified_by")
	private Integer modifiedBy;

	@Column(name = "modified_date", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date modifiedDate;

	@OneToMany(mappedBy = "clientAssetToken")
	private List<ClientUserToken> clientUserTokens;
	

	@OneToMany()
	private List<ClientTokenConfiguration> clientTokenConfiguration;
	
	

	@ManyToOne
	@JoinColumn(name = "client_id", referencedColumnName = "client_id")
	private Client client;

	public ClientAssetToken() {
	}

	/**
	 * @return the tokenId
	 */
	public Integer getTokenId() {
		return tokenId;
	}

	/**
	 * @param tokenId
	 *            the tokenId to set
	 */
	public void setTokenId(Integer tokenId) {
		this.tokenId = tokenId;
	}

	/**
	 * @return the issuingAccountPublicKey
	 */
	public String getIssuingAccountPublicKey() {
		return issuingAccountPublicKey;
	}

	/**
	 * @param issuingAccountPublicKey
	 *            the issuingAccountPublicKey to set
	 */
	public void setIssuingAccountPublicKey(String issuingAccountPublicKey) {
		this.issuingAccountPublicKey = issuingAccountPublicKey;
	}

	/**
	 * @return the issuingAccountSecretKey
	 */
	public String getIssuingAccountSecretKey() {
		return issuingAccountSecretKey;
	}

	/**
	 * @param issuingAccountSecretKey
	 *            the issuingAccountSecretKey to set
	 */
	public void setIssuingAccountSecretKey(String issuingAccountSecretKey) {
		this.issuingAccountSecretKey = issuingAccountSecretKey;
	}

	/**
	 * @return the baseTxnAccountPublicKey
	 */
	public String getBaseTxnAccountPublicKey() {
		return baseTxnAccountPublicKey;
	}

	/**
	 * @param baseTxnAccountPublicKey
	 *            the baseTxnAccountPublicKey to set
	 */
	public void setBaseTxnAccountPublicKey(String baseTxnAccountPublicKey) {
		this.baseTxnAccountPublicKey = baseTxnAccountPublicKey;
	}

	/**
	 * @return the baseTxnAccountSecretKey
	 */
	public String getBaseTxnAccountSecretKey() {
		return baseTxnAccountSecretKey;
	}

	/**
	 * @param baseTxnAccountSecretKey
	 *            the baseTxnAccountSecretKey to set
	 */
	public void setBaseTxnAccountSecretKey(String baseTxnAccountSecretKey) {
		this.baseTxnAccountSecretKey = baseTxnAccountSecretKey;
	}

	/**
	 * @return the batchQuantity
	 */
	public BigInteger getBatchQuantity() {
		return batchQuantity;
	}

	/**
	 * @param batchQuantity
	 *            the batchQuantity to set
	 */
	public void setBatchQuantity(BigInteger batchQuantity) {
		this.batchQuantity = batchQuantity;
	}

	/**
	 * @return the provisionedQuantity
	 */
	public BigInteger getProvisionedQuantity() {
		return provisionedQuantity;
	}

	/**
	 * @param provisionedQuantity
	 *            the provisionedQuantity to set
	 */
	public void setProvisionedQuantity(BigInteger provisionedQuantity) {
		this.provisionedQuantity = provisionedQuantity;
	}

	/**
	 * @return the status
	 */
	public Status getStatus() {
		return status;
	}

	/**
	 * @param status
	 *            the status to set
	 */
	public void setStatus(Status status) {
		this.status = status;
	}

	/**
	 * @return the tokenCode
	 */
	public String getTokenCode() {
		return tokenCode;
	}

	/**
	 * @param tokenCode
	 *            the tokenCode to set
	 */
	public void setTokenCode(String tokenCode) {
		this.tokenCode = tokenCode;
	}

	/**
	 * @return the tokenCorrelationId
	 */
	public String getTokenCorrelationId() {
		return tokenCorrelationId;
	}

	/**
	 * @param tokenCorrelationId
	 *            the tokenCorrelationId to set
	 */
	public void setTokenCorrelationId(String tokenCorrelationId) {
		this.tokenCorrelationId = tokenCorrelationId;
	}

	/**
	 * @return the tokenDescription
	 */
	public String getTokenDescription() {
		return tokenDescription;
	}

	/**
	 * @param tokenDescription
	 *            the tokenDescription to set
	 */
	public void setTokenDescription(String tokenDescription) {
		this.tokenDescription = tokenDescription;
	}

	/**
	 * @return the tokenName
	 */
	public String getTokenName() {
		return tokenName;
	}

	/**
	 * @param tokenName
	 *            the tokenName to set
	 */
	public void setTokenName(String tokenName) {
		this.tokenName = tokenName;
	}

	/**
	 * @return the createdBy
	 */
	public Integer getCreatedBy() {
		return createdBy;
	}

	/**
	 * @param createdBy
	 *            the createdBy to set
	 */
	public void setCreatedBy(Integer createdBy) {
		this.createdBy = createdBy;
	}

	/**
	 * @return the createdDate
	 */
	public Date getCreatedDate() {
		return createdDate;
	}

	/**
	 * @param createdDate
	 *            the createdDate to set
	 */
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	/**
	 * @return the modifiedBy
	 */
	public Integer getModifiedBy() {
		return modifiedBy;
	}

	/**
	 * @param modifiedBy
	 *            the modifiedBy to set
	 */
	public void setModifiedBy(Integer modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	/**
	 * @return the modifiedDate
	 */
	public Date getModifiedDate() {
		return modifiedDate;
	}

	/**
	 * @param modifiedDate
	 *            the modifiedDate to set
	 */
	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	/**
	 * @return the clientUserTokens
	 */
	public List<ClientUserToken> getClientUserTokens() {
		return clientUserTokens;
	}

	/**
	 * @param clientUserTokens
	 *            the clientUserTokens to set
	 */
	public void setClientUserTokens(List<ClientUserToken> clientUserTokens) {
		this.clientUserTokens = clientUserTokens;
	}

	/**
	 * @return the client
	 */
	public Client getClient() {
		return client;
	}

	/**
	 * @param client
	 *            the client to set
	 */
	public void setClient(Client client) {
		this.client = client;
	}

	public List<ClientTokenConfiguration> getClientTokenConfiguration() {
		return clientTokenConfiguration;
	}

	public void setClientTokenConfiguration(List<ClientTokenConfiguration> clientTokenConfiguration) {
		this.clientTokenConfiguration = clientTokenConfiguration;
	}

	
	
	
}