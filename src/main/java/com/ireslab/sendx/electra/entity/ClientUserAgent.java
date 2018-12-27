/**
 * 
 */
package com.ireslab.sendx.electra.entity;

import java.io.Serializable;
import java.math.BigInteger;
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
@Table(name = "client_users_agent")
@NamedQuery(name = "ClientUserAgent.findAll", query = "SELECT c FROM ClientUserAgent c")
public class ClientUserAgent implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "client_agent_id", unique = true, nullable = false)
	private Integer clientAgentId;

	@Column(name = "client_id")
	private Integer clientId;

	@Column(name = "mobile_number")
	private BigInteger mobileNumber;

	@Column(name = "agent_address")
	private String agentAddress;

	@Column(name = "business_id")
	private String businessId;

	@Column(name = "fiat_currencies")
	private String fiatCurrencies;

	@Column(name = "crypto_currencies")
	private String cryptoCurrencies;

	@Column(name = "business_longitude")
	private String businessLongitude;

	@Column(name = "business_latitude")
	private String businessLatitude;

	@Column(name = "ekyc")
	private String ekyc;

	@Column(name = "status")
	private String status;

	@Column(name = "country_code")
	private String countryCode;

	// @Column(name = "id_card_image_url")
	// private String idCardImage;

	@Column(name = "created_date", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdDate;

	/**
	 * @return the clientAgentId
	 */
	public Integer getClientAgentId() {
		return clientAgentId;
	}

	/**
	 * @param clientAgentId
	 *            the clientAgentId to set
	 */
	public void setClientAgentId(Integer clientAgentId) {
		this.clientAgentId = clientAgentId;
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
	 * @return the mobileNumber
	 */
	public BigInteger getMobileNumber() {
		return mobileNumber;
	}

	/**
	 * @param mobileNumber
	 *            the mobileNumber to set
	 */
	public void setMobileNumber(BigInteger mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	/**
	 * @return the agentAddress
	 */
	public String getAgentAddress() {
		return agentAddress;
	}

	/**
	 * @param agentAddress
	 *            the agentAddress to set
	 */
	public void setAgentAddress(String agentAddress) {
		this.agentAddress = agentAddress;
	}

	/**
	 * @return the businessId
	 */
	public String getBusinessId() {
		return businessId;
	}

	/**
	 * @param businessId
	 *            the businessId to set
	 */
	public void setBusinessId(String businessId) {
		this.businessId = businessId;
	}

	/**
	 * @return the fiatCurrencies
	 */
	public String getFiatCurrencies() {
		return fiatCurrencies;
	}

	/**
	 * @param fiatCurrencies
	 *            the fiatCurrencies to set
	 */
	public void setFiatCurrencies(String fiatCurrencies) {
		this.fiatCurrencies = fiatCurrencies;
	}

	/**
	 * @return the cryptoCurrencies
	 */
	public String getCryptoCurrencies() {
		return cryptoCurrencies;
	}

	/**
	 * @param cryptoCurrencies
	 *            the cryptoCurrencies to set
	 */
	public void setCryptoCurrencies(String cryptoCurrencies) {
		this.cryptoCurrencies = cryptoCurrencies;
	}

	/**
	 * @return the businessLongitude
	 */
	public String getBusinessLongitude() {
		return businessLongitude;
	}

	/**
	 * @param businessLongitude
	 *            the businessLongitude to set
	 */
	public void setBusinessLongitude(String businessLongitude) {
		this.businessLongitude = businessLongitude;
	}

	/**
	 * @return the businessLatitude
	 */
	public String getBusinessLatitude() {
		return businessLatitude;
	}

	/**
	 * @param businessLatitude
	 *            the businessLatitude to set
	 */
	public void setBusinessLatitude(String businessLatitude) {
		this.businessLatitude = businessLatitude;
	}

	/**
	 * @return the ekyc
	 */
	public String getEkyc() {
		return ekyc;
	}

	/**
	 * @param ekyc
	 *            the ekyc to set
	 */
	public void setEkyc(String ekyc) {
		this.ekyc = ekyc;
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

	/**
	 * @return the countryCode
	 */
	public String getCountryCode() {
		return countryCode;
	}

	/**
	 * @param countryCode
	 *            the countryCode to set
	 */
	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	// /**
	// * @return the idCardImage
	// */
	// public String getIdCardImage() {
	// return idCardImage;
	// }
	//
	// /**
	// * @param idCardImage
	// * the idCardImage to set
	// */
	// public void setIdCardImage(String idCardImage) {
	// this.idCardImage = idCardImage;
	// }

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
}
