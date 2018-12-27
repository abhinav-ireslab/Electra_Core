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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.ireslab.sendx.electra.utils.Status;

/**
 * The persistent class for the client_user_token database table.
 * 
 */
@Entity
@Table(name = "client_user_token")
@NamedQuery(name = "ClientUserToken.findAll", query = "SELECT c FROM ClientUserToken c")
public class ClientUserToken implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "client_user_token_id", unique = true, nullable = false)
	private Integer clientUserTokenId;

	@Column(name = "client_id", nullable = false)
	private Integer clientId;

	@Column(name = "is_trustline_created", nullable = false)
	private boolean isTrustlineCreated;

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

	@ManyToOne
	@JoinColumn(name = "user_id", referencedColumnName = "user_id")
	private ClientUser clientUser;

	@ManyToOne
	@JoinColumn(name = "token_id", referencedColumnName = "token_id")
	private ClientAssetToken clientAssetToken;


	@Column(name = "status")
	@Enumerated(EnumType.STRING)
	private Status status;

	
	public ClientUserToken() {
	}

	/**
	 * @return the clientUserTokenId
	 */
	public Integer getClientUserTokenId() {
		return clientUserTokenId;
	}

	/**
	 * @param clientUserTokenId
	 *            the clientUserTokenId to set
	 */
	public void setClientUserTokenId(Integer clientUserTokenId) {
		this.clientUserTokenId = clientUserTokenId;
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
	 * @return the isTrustlineCreated
	 */
	public boolean isTrustlineCreated() {
		return isTrustlineCreated;
	}

	/**
	 * @param isTrustlineCreated
	 *            the isTrustlineCreated to set
	 */
	public void setIsTrustlineCreated(boolean isTrustlineCreated) {
		this.isTrustlineCreated = isTrustlineCreated;
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
	 * @return the clientUser
	 */
	public ClientUser getClientUser() {
		return clientUser;
	}

	/**
	 * @param clientUser
	 *            the clientUser to set
	 */
	public void setClientUser(ClientUser clientUser) {
		this.clientUser = clientUser;
	}

	/**
	 * @return the clientAssetToken
	 */
	public ClientAssetToken getClientAssetToken() {
		return clientAssetToken;
	}

	/**
	 * @param clientAssetToken
	 *            the clientAssetToken to set
	 */
	public void setClientAssetToken(ClientAssetToken clientAssetToken) {
		this.clientAssetToken = clientAssetToken;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}


}