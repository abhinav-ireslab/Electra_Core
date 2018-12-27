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

/**
 * The persistent class for the client_credentials database table.
 * 
 */
@Entity
@Table(name = "client_credentials")
@NamedQuery(name = "ClientCredential.findAll", query = "SELECT c FROM ClientCredential c")
public class ClientCredential implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "credential_id", unique = true, nullable = false)
	private Integer credentialId;

	@Column(name = "client_id", nullable = false)
	private Integer clientId;

	@Column(name = "access_token_validity", nullable = false)
	private Integer accessTokenValidity;

	@Column(name = "additional_information", length = 255)
	private String additionalInformation;

	@Column(nullable = false, length = 255)
	private String authorities;

	@Column(name = "client_api_key", nullable = false, length = 255)
	private String clientApiKey;

	@Column(name = "client_api_secret", nullable = false, length = 255)
	private String clientApiSecret;

	@Column(name = "created_by")
	private Integer createdBy;

	@Column(name = "created_date", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdDate;

	@Column(name = "grant_types", nullable = false, length = 255)
	private String grantTypes;

	@Column(name = "modified_by")
	private Integer modifiedBy;

	@Column(name = "modified_date", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date modifiedDate;

	@Column(name = "resource_ids", nullable = false, length = 255)
	private String resourceIds;

	@Column(nullable = false, length = 255)
	private String scopes;

	@Column(nullable = false, length = 1)
	private String status;

	public ClientCredential() {
	}

	/**
	 * @return the credentialId
	 */
	public Integer getCredentialId() {
		return credentialId;
	}

	/**
	 * @param credentialId
	 *            the credentialId to set
	 */
	public void setCredentialId(Integer credentialId) {
		this.credentialId = credentialId;
	}

	/**
	 * @return the clientId
	 */
	public Integer getClientId() {
		return clientId;
	}

	/**
	 * @param clientId
	 *            the clientId to set
	 */
	public void setClientId(Integer clientId) {
		this.clientId = clientId;
	}

	/**
	 * @return the accessTokenValidity
	 */
	public Integer getAccessTokenValidity() {
		return accessTokenValidity;
	}

	/**
	 * @param accessTokenValidity
	 *            the accessTokenValidity to set
	 */
	public void setAccessTokenValidity(Integer accessTokenValidity) {
		this.accessTokenValidity = accessTokenValidity;
	}

	/**
	 * @return the additionalInformation
	 */
	public String getAdditionalInformation() {
		return additionalInformation;
	}

	/**
	 * @param additionalInformation
	 *            the additionalInformation to set
	 */
	public void setAdditionalInformation(String additionalInformation) {
		this.additionalInformation = additionalInformation;
	}

	/**
	 * @return the authorities
	 */
	public String getAuthorities() {
		return authorities;
	}

	/**
	 * @param authorities
	 *            the authorities to set
	 */
	public void setAuthorities(String authorities) {
		this.authorities = authorities;
	}

	/**
	 * @return the clientApiKey
	 */
	public String getClientApiKey() {
		return clientApiKey;
	}

	/**
	 * @param clientApiKey
	 *            the clientApiKey to set
	 */
	public void setClientApiKey(String clientApiKey) {
		this.clientApiKey = clientApiKey;
	}

	/**
	 * @return the clientApiSecret
	 */
	public String getClientApiSecret() {
		return clientApiSecret;
	}

	/**
	 * @param clientApiSecret
	 *            the clientApiSecret to set
	 */
	public void setClientApiSecret(String clientApiSecret) {
		this.clientApiSecret = clientApiSecret;
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
	 * @return the grantTypes
	 */
	public String getGrantTypes() {
		return grantTypes;
	}

	/**
	 * @param grantTypes
	 *            the grantTypes to set
	 */
	public void setGrantTypes(String grantTypes) {
		this.grantTypes = grantTypes;
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
	 * @return the resourceIds
	 */
	public String getResourceIds() {
		return resourceIds;
	}

	/**
	 * @param resourceIds
	 *            the resourceIds to set
	 */
	public void setResourceIds(String resourceIds) {
		this.resourceIds = resourceIds;
	}

	/**
	 * @return the scopes
	 */
	public String getScopes() {
		return scopes;
	}

	/**
	 * @param scopes
	 *            the scopes to set
	 */
	public void setScopes(String scopes) {
		this.scopes = scopes;
	}

	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param status
	 *            the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}
}