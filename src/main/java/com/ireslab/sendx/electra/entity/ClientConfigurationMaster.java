package com.ireslab.sendx.electra.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * The persistent class for the client_configuration_master database table.
 * 
 */
@Entity
@Table(name = "client_configuration_master")
@NamedQuery(name = "ClientConfigurationMaster.findAll", query = "SELECT c FROM ClientConfigurationMaster c")
public class ClientConfigurationMaster implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "master_client_config_id", unique = true, nullable = false)
	private Integer masterClientConfigId;

	@Column(name = "config_code", nullable = false, length = 255)
	private String configCode;

	@Column(name = "config_description", length = 255)
	private String configDescription;

	@Column(name = "config_title", nullable = false, length = 255)
	private String configTitle;

	@Column(name = "created_by")
	private Integer createdBy;

	@Column(name = "created_date", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdDate;

	@Column(name = "default_value", length = 255)
	private String defaultValue;

	@Column(name = "modified_by")
	private Integer modifiedBy;

	@Column(name = "modified_date", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date modifiedDate;

	@OneToMany(mappedBy = "clientConfigurationMaster")
	private List<ClientConfiguration> clientConfigurations;

	public ClientConfigurationMaster() {
	}

	/**
	 * @return the masterClientConfigId
	 */
	public Integer getMasterClientConfigId() {
		return masterClientConfigId;
	}

	/**
	 * @param masterClientConfigId
	 *            the masterClientConfigId to set
	 */
	public void setMasterClientConfigId(Integer masterClientConfigId) {
		this.masterClientConfigId = masterClientConfigId;
	}

	/**
	 * @return the configCode
	 */
	public String getConfigCode() {
		return configCode;
	}

	/**
	 * @param configCode
	 *            the configCode to set
	 */
	public void setConfigCode(String configCode) {
		this.configCode = configCode;
	}

	/**
	 * @return the configDescription
	 */
	public String getConfigDescription() {
		return configDescription;
	}

	/**
	 * @param configDescription
	 *            the configDescription to set
	 */
	public void setConfigDescription(String configDescription) {
		this.configDescription = configDescription;
	}

	/**
	 * @return the configTitle
	 */
	public String getConfigTitle() {
		return configTitle;
	}

	/**
	 * @param configTitle
	 *            the configTitle to set
	 */
	public void setConfigTitle(String configTitle) {
		this.configTitle = configTitle;
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
	 * @return the defaultValue
	 */
	public String getDefaultValue() {
		return defaultValue;
	}

	/**
	 * @param defaultValue
	 *            the defaultValue to set
	 */
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
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
	 * @return the clientConfigurations
	 */
	public List<ClientConfiguration> getClientConfigurations() {
		return clientConfigurations;
	}

	/**
	 * @param clientConfigurations
	 *            the clientConfigurations to set
	 */
	public void setClientConfigurations(List<ClientConfiguration> clientConfigurations) {
		this.clientConfigurations = clientConfigurations;
	}
}