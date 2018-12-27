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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * The persistent class for the country database table.
 * 
 */
@Entity
@NamedQuery(name = "Country.findAll", query = "SELECT c FROM Country c")
public class Country implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "country_id")
	private Integer countryId;

	@Column(name = "country_dial_code")
	private String countryDialCode;

	@Column(name = "country_name")
	private String countryName;

	@Column(name = "iso4217_currency_name")
	private String iso4217CurrencyName;

	@Column(name = "iso4217_currency_alphabetic_code")
	private String iso4217CurrencyAlphabeticCode;
	
	@Column(name = "country_code")
	private String countryCode;
	
	@Column(name = "national_id")
	private String nationalId;

	@Column(name = "country_timezone")
	private String countryTimeZone;
	
	@Column(name = "created_date")
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdDate;

	@Column(name = "modified_date")
	@Temporal(TemporalType.TIMESTAMP)
	private Date modifiedDate;
	
	@Column(name = "currency_symbol")
	private String currencySymbol;
	
	
	

	public String getCurrencySymbol() {
		return currencySymbol;
	}

	public void setCurrencySymbol(String currencySymbol) {
		this.currencySymbol = currencySymbol;
	}

	public Country() {
	}

	/**
	 * @return the countryId
	 */
	public Integer getCountryId() {
		return countryId;
	}

	/**
	 * @param countryId the countryId to set
	 */
	public void setCountryId(Integer countryId) {
		this.countryId = countryId;
	}

	/**
	 * @return the countryDialCode
	 */
	public String getCountryDialCode() {
		return countryDialCode;
	}

	/**
	 * @param countryDialCode the countryDialCode to set
	 */
	public void setCountryDialCode(String countryDialCode) {
		this.countryDialCode = countryDialCode;
	}

	/**
	 * @return the countryName
	 */
	public String getCountryName() {
		return countryName;
	}

	/**
	 * @param countryName the countryName to set
	 */
	public void setCountryName(String countryName) {
		this.countryName = countryName;
	}

	/**
	 * @return the iso4217CurrencyName
	 */
	public String getIso4217CurrencyName() {
		return iso4217CurrencyName;
	}

	/**
	 * @param iso4217CurrencyName the iso4217CurrencyName to set
	 */
	public void setIso4217CurrencyName(String iso4217CurrencyName) {
		this.iso4217CurrencyName = iso4217CurrencyName;
	}

	/**
	 * @return the iso4217CurrencyAlphabeticCode
	 */
	public String getIso4217CurrencyAlphabeticCode() {
		return iso4217CurrencyAlphabeticCode;
	}

	/**
	 * @param iso4217CurrencyAlphabeticCode the iso4217CurrencyAlphabeticCode to set
	 */
	public void setIso4217CurrencyAlphabeticCode(String iso4217CurrencyAlphabeticCode) {
		this.iso4217CurrencyAlphabeticCode = iso4217CurrencyAlphabeticCode;
	}

	/**
	 * @return the createdDate
	 */
	public Date getCreatedDate() {
		return createdDate;
	}

	/**
	 * @param createdDate the createdDate to set
	 */
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	/**
	 * @return the modifiedDate
	 */
	public Date getModifiedDate() {
		return modifiedDate;
	}

	/**
	 * @param modifiedDate the modifiedDate to set
	 */
	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}
	
	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public String getNationalId() {
		return nationalId;
	}

	public void setNationalId(String nationalId) {
		this.nationalId = nationalId;
	}

	public String getCountryTimeZone() {
		return countryTimeZone;
	}

	public void setCountryTimeZone(String countryTimeZone) {
		this.countryTimeZone = countryTimeZone;
	}

	@Override
	public String toString() {
		return "Country [countryId=" + countryId + ", countryDialCode=" + countryDialCode + ", countryName="
				+ countryName + ", iso4217CurrencyName=" + iso4217CurrencyName + ", iso4217CurrencyAlphabeticCode="
				+ iso4217CurrencyAlphabeticCode + ", countryCode=" + countryCode + ", nationalId=" + nationalId
				+ ", createdDate=" + createdDate + ", modifiedDate=" + modifiedDate + "]";
	}


	
	
	
}