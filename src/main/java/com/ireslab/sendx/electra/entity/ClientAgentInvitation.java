package com.ireslab.sendx.electra.entity;

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
@Table(name = "client_agent_invitation")
@NamedQuery(name = "ClientAgentInvitation.findAll", query = "SELECT c FROM ClientAgentInvitation c")
public class ClientAgentInvitation {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "invitation_id", unique = true, nullable = false)
	private Integer invitationId;
	
	@Column(name = "client_id", nullable = false)
	private Integer clientId;
	
	@Column(name = "mobile_number", length = 50)
	private String mobileNumber;
	
	@Column(name = "email_address", length = 50)
	private String emailAddress;
	
	@Column(name = "is_register", nullable = false)
	private boolean isRegister;
	
	@Column(name = "invitation_date", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date invitationDate;
	
	@Column(name = "subscription_durations", length = 50)
	private String subscriptionDurations;

	@Column(name = "subscription_expire_on",nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date subscriptionExpireOn;
	
	
	
	
	public Date getSubscriptionExpireOn() {
		return subscriptionExpireOn;
	}

	public void setSubscriptionExpireOn(Date subscriptionExpireOn) {
		this.subscriptionExpireOn = subscriptionExpireOn;
	}

	public String getSubscriptionDurations() {
		return subscriptionDurations;
	}

	public void setSubscriptionDurations(String subscriptionDurations) {
		this.subscriptionDurations = subscriptionDurations;
	}

	public Integer getInvitationId() {
		return invitationId;
	}

	public void setInvitationId(Integer invitationId) {
		this.invitationId = invitationId;
	}

	public Integer getClientId() {
		return clientId;
	}

	public void setClientId(Integer clientId) {
		this.clientId = clientId;
	}

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public boolean isRegister() {
		return isRegister;
	}

	public void setRegister(boolean isRegister) {
		this.isRegister = isRegister;
	}

	public Date getInvitationDate() {
		return invitationDate;
	}

	public void setInvitationDate(Date invitationDate) {
		this.invitationDate = invitationDate;
	}
	
	

}
