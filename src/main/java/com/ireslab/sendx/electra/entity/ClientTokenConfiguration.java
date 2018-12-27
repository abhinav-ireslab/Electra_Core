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
 * The persistent class for the client_token_configuration database table.
 * 
 */
@Entity
@Table(name = "client_token_configuration")
@NamedQuery(name = "ClientTokenConfiguration.findAll", query = "SELECT c FROM ClientTokenConfiguration c")
public class ClientTokenConfiguration implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "client_token_config_id", unique = true, nullable = false)
	private Integer clientTokenConfigId;

	@Column(name = "client_id", nullable = false)
	private Integer clientId;

	@Column(name = "client_token_config_master_id", nullable = false)
	private Integer clientTokenConfigMasterId;

	@Column(name = "token_id", nullable = false)
	private Integer tokenId;

	@Column(length = 255)
	private String value;

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

	@Column(name = "is_enabled")
	private boolean isEnabled;

	public ClientTokenConfiguration() {
	}

	public Integer getClientTokenConfigId() {
		return this.clientTokenConfigId;
	}

	public void setClientTokenConfigId(Integer clientTokenConfigId) {
		this.clientTokenConfigId = clientTokenConfigId;
	}

	public Integer getClientId() {
		return this.clientId;
	}

	public void setClientId(Integer clientId) {
		this.clientId = clientId;
	}

	public Integer getClientTokenConfigMasterId() {
		return this.clientTokenConfigMasterId;
	}

	public void setClientTokenConfigMasterId(Integer clientTokenConfigMasterId) {
		this.clientTokenConfigMasterId = clientTokenConfigMasterId;
	}

	public Integer getCreatedBy() {
		return this.createdBy;
	}

	public void setCreatedBy(Integer createdBy) {
		this.createdBy = createdBy;
	}

	public Date getCreatedDate() {
		return this.createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Integer getModifiedBy() {
		return this.modifiedBy;
	}

	public void setModifiedBy(Integer modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	public Date getModifiedDate() {
		return this.modifiedDate;
	}

	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	public Integer getTokenId() {
		return this.tokenId;
	}

	public void setTokenId(Integer tokenId) {
		this.tokenId = tokenId;
	}

	public String getValue() {
		return this.value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}