package com.ireslab.sendx.electra.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * The persistent class for the client_configuration database table.
 * 
 */
@Entity
@Table(name = "client_configuration")
@NamedQuery(name = "ClientConfiguration.findAll", query = "SELECT c FROM ClientConfiguration c")
public class ClientConfiguration implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "client_config_id", unique = true, nullable = false)
	private Integer clientConfigId;

	@Column(length = 50)
	private String value;

	@Column(name = "enablement_date", nullable = false)
	private Date enablementDate;

	@Column(name = "expiry_date", nullable = false)
	private Date expiryDate;

	@Column(name = "is_enabled", nullable = false)
	private boolean isEnabled;

	@Column(name = "created_by")
	private Integer createdBy;

	@Column(name = "created_date")
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdDate;

	@Column(name = "modified_by")
	private Integer modifiedBy;

	@Column(name = "modified_date")
	@Temporal(TemporalType.TIMESTAMP)
	private Date modifiedDate;

	@ManyToOne
	@JoinColumn(name = "client_id", referencedColumnName = "client_id")
	private Client client;

	@ManyToOne
	@JoinColumn(name = "master_client_config_id", referencedColumnName = "master_client_config_id")
	private ClientConfigurationMaster clientConfigurationMaster;

	public ClientConfiguration() {
	}

	/**
	 * @return the clientConfigId
	 */
	public Integer getClientConfigId() {
		return clientConfigId;
	}

	/**
	 * @param clientConfigId
	 *            the clientConfigId to set
	 */
	public void setClientConfigId(Integer clientConfigId) {
		this.clientConfigId = clientConfigId;
	}

	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @param value
	 *            the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * @return the enablementDate
	 */
	public Date getEnablementDate() {
		return enablementDate;
	}

	/**
	 * @param enablementDate
	 *            the enablementDate to set
	 */
	public void setEnablementDate(Date enablementDate) {
		this.enablementDate = enablementDate;
	}

	/**
	 * @return the expiryDate
	 */
	public Date getExpiryDate() {
		return expiryDate;
	}

	/**
	 * @param expiryDate
	 *            the expiryDate to set
	 */
	public void setExpiryDate(Date expiryDate) {
		this.expiryDate = expiryDate;
	}

	/**
	 * @return the isEnabled
	 */
	public boolean isEnabled() {
		return isEnabled;
	}

	/**
	 * @param isEnabled
	 *            the isEnabled to set
	 */
	public void setEnabled(boolean isEnabled) {
		this.isEnabled = isEnabled;
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

	/**
	 * @return the clientConfigurationMaster
	 */
	public ClientConfigurationMaster getClientConfigurationMaster() {
		return clientConfigurationMaster;
	}

	/**
	 * @param clientConfigurationMaster
	 *            the clientConfigurationMaster to set
	 */
	public void setClientConfigurationMaster(ClientConfigurationMaster clientConfigurationMaster) {
		this.clientConfigurationMaster = clientConfigurationMaster;
	}

}