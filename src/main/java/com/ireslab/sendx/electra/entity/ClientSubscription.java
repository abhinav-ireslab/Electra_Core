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
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "client_subscription")
@NamedQuery(name = "ClientSubscription.findAll", query = "SELECT cs FROM ClientSubscription cs")
public class ClientSubscription implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "client_subs_id", unique = true, nullable = false)
	private Integer clientSubscriptionId;
	
	@ManyToOne
	@JoinColumn(name = "subscription_id", referencedColumnName = "subscription_id")
	private SubscriptionPlans subscriptionPlanId;
	
	@Column(name = "client_id", nullable = false, length = 50)
	private Integer clientId;
	
	@Column(name = "available_users", nullable = false, length = 50)
	private Integer availableUsers;
	
	@Column(name = "subscription_date", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date subscriptionDate;
	
	@Column(name = "modified_date", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date modifiedDate;
	
	@Column(name = "expiry_date", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date expiryDate;

	/**
	 * @return the clientSubscriptionId
	 */
	public Integer getClientSubscriptionId() {
		return clientSubscriptionId;
	}

	/**
	 * @param clientSubscriptionId the clientSubscriptionId to set
	 */
	public void setClientSubscriptionId(Integer clientSubscriptionId) {
		this.clientSubscriptionId = clientSubscriptionId;
	}

	/**
	 * @return the subscriptionPlanId
	 */
	public SubscriptionPlans getSubscriptionPlanId() {
		return subscriptionPlanId;
	}

	/**
	 * @param subscriptionPlanId the subscriptionPlanId to set
	 */
	public void setSubscriptionPlanId(SubscriptionPlans subscriptionPlanId) {
		this.subscriptionPlanId = subscriptionPlanId;
	}

	/**
	 * @return the clientId
	 */
	public Integer getClientId() {
		return clientId;
	}

	/**
	 * @param clientId the clientId to set
	 */
	public void setClientId(Integer clientId) {
		this.clientId = clientId;
	}

	/**
	 * @return the availableUsers
	 */
	public Integer getAvailableUsers() {
		return availableUsers;
	}

	/**
	 * @param availableUsers the availableUsers to set
	 */
	public void setAvailableUsers(Integer availableUsers) {
		this.availableUsers = availableUsers;
	}

	/**
	 * @return the subscriptionDate
	 */
	public Date getSubscriptionDate() {
		return subscriptionDate;
	}

	/**
	 * @param subscriptionDate the subscriptionDate to set
	 */
	public void setSubscriptionDate(Date subscriptionDate) {
		this.subscriptionDate = subscriptionDate;
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

	/**
	 * @return the expiryDate
	 */
	public Date getExpiryDate() {
		return expiryDate;
	}

	/**
	 * @param expiryDate the expiryDate to set
	 */
	public void setExpiryDate(Date expiryDate) {
		this.expiryDate = expiryDate;
	}

	

}
