package com.ireslab.sendx.electra.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@Table(name = "subscription_plan")
@NamedQuery(name = "SubscriptionPlans.findAll", query = "SELECT sp FROM SubscriptionPlans sp")
public class SubscriptionPlans implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "subscription_id", unique = true, nullable = false)
	private Integer subscriptionId;

	@Column(name = "plan_title", nullable = false, length = 50)
	private String planTitle;
	
	@Column(name = "token", nullable = false, length = 50)
	private Double token;
	
	@Column(name = "validity", nullable = false, length = 50)
	private String validity;
	
	@Column(name = "supported_users", nullable = false, length = 50)
	private Integer supportedUsers;
	
	@ManyToOne
	@JoinColumn(name = "country_id", referencedColumnName = "country_id")
	private Country countryId;
	
	@Column(name = "is_deleted", nullable = false, length = 50)
	private boolean isDeleted;

	/**
	 * @return the subscriptionId
	 */
	public Integer getSubscriptionId() {
		return subscriptionId;
	}

	/**
	 * @param subscriptionId the subscriptionId to set
	 */
	public void setSubscriptionId(Integer subscriptionId) {
		this.subscriptionId = subscriptionId;
	}

	/**
	 * @return the planTitle
	 */
	public String getPlanTitle() {
		return planTitle;
	}

	/**
	 * @return the countryId
	 */
	public Country getCountryId() {
		return countryId;
	}

	/**
	 * @param countryId the countryId to set
	 */
	public void setCountryId(Country countryId) {
		this.countryId = countryId;
	}

	

	/**
	 * @return the isDeleted
	 */
	public boolean isDeleted() {
		return isDeleted;
	}

	/**
	 * @param isDeleted the isDeleted to set
	 */
	public void setDeleted(boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

	/**
	 * @param planTitle the planTitle to set
	 */
	public void setPlanTitle(String planTitle) {
		this.planTitle = planTitle;
	}

	

	/**
	 * @return the token
	 */
	public Double getToken() {
		return token;
	}

	/**
	 * @param token the token to set
	 */
	public void setToken(Double token) {
		this.token = token;
	}

	/**
	 * @return the validity
	 */
	public String getValidity() {
		return validity;
	}

	/**
	 * @param validity the validity to set
	 */
	public void setValidity(String validity) {
		this.validity = validity;
	}

	/**
	 * @return the supportedUsers
	 */
	public Integer getSupportedUsers() {
		return supportedUsers;
	}

	/**
	 * @param supportedUsers the supportedUsers to set
	 */
	public void setSupportedUsers(Integer supportedUsers) {
		this.supportedUsers = supportedUsers;
	}
	
	

	

}
