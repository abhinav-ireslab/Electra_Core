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
 * The persistent class for the client_token_configuration_master database
 * table.
 * 
 */
@Entity
@Table(name = "client_token_configuration_master")
@NamedQuery(name = "ClientTokenConfigurationMaster.findAll", query = "SELECT c FROM ClientTokenConfigurationMaster c")
public class ClientTokenConfigurationMaster implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "client_token_config_master_id", unique = true, nullable = false)
	private Integer clientTokenConfigMasterId;

	@Column(name = "token_config_code", nullable = false, length = 255)
	private String tokenConfigCode;

	@Column(name = "token_config_title", nullable = false, length = 255)
	private String tokenConfigTitle;

	@Column(name = "token_config_description", length = 255)
	private String tokenConfigDescription;

	@Column(name = "default_value", length = 255)
	private String defaultValue;

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

	public ClientTokenConfigurationMaster() {
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

	public String getDefaultValue() {
		return this.defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
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

	public String getTokenConfigCode() {
		return this.tokenConfigCode;
	}

	public void setTokenConfigCode(String tokenConfigCode) {
		this.tokenConfigCode = tokenConfigCode;
	}

	public String getTokenConfigDescription() {
		return this.tokenConfigDescription;
	}

	public void setTokenConfigDescription(String tokenConfigDescription) {
		this.tokenConfigDescription = tokenConfigDescription;
	}

	public String getTokenConfigTitle() {
		return this.tokenConfigTitle;
	}

	public void setTokenConfigTitle(String tokenConfigTitle) {
		this.tokenConfigTitle = tokenConfigTitle;
	}

}