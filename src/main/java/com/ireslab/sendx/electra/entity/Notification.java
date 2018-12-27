package com.ireslab.sendx.electra.entity;

import java.math.BigInteger;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@NamedQuery(name = "Notification.findAll", query = "SELECT a FROM Notification a")
public class Notification {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "notification_id")
	private Integer notificationId;
	
	@Column(name="correlation_id")
	private String correlationId;
	
	@Column(name = "notification_type")
	private String notificationType;
	
	@Column(name = "notification_data")
	private String notificationData;
	
	@Column(name = "created_date")
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdDate;

	@Column(name = "modified_date")
	@Temporal(TemporalType.TIMESTAMP)
	private Date modifiedDate;
	
	@Column(name="status")
	private boolean status;
	
	@Column(name="invoice")
	private boolean invoice;
	
	@Column(name = "mobile_number")
	private String mobileNumber;
	
	@Column(name = "email_address")
	private String emailAddress;
	
	@Column(name="is_offline")
	private Boolean isOffline;
	
	@Column(name="is_payment_confirm")
	private Boolean isPaymentConfirm;
	
	

	public Integer getNotificationId() {
		return notificationId;
	}

	public void setNotificationId(Integer notificationId) {
		this.notificationId = notificationId;
	}

	public String getCorrelationId() {
		return correlationId;
	}

	public void setCorrelationId(String correlationId) {
		this.correlationId = correlationId;
	}

	public String getNotificationType() {
		return notificationType;
	}

	public void setNotificationType(String notificationType) {
		this.notificationType = notificationType;
	}

	public String getNotificationData() {
		return notificationData;
	}

	public void setNotificationData(String notificationData) {
		this.notificationData = notificationData;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Date getModifiedDate() {
		return modifiedDate;
	}

	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	public boolean isInvoice() {
		return invoice;
	}

	public void setInvoice(boolean invoice) {
		this.invoice = invoice;
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

	public Boolean getIsOffline() {
		return isOffline;
	}

	public void setIsOffline(Boolean isOffline) {
		this.isOffline = isOffline;
	}

	public Boolean getIsPaymentConfirm() {
		return isPaymentConfirm;
	}

	public void setIsPaymentConfirm(Boolean isPaymentConfirm) {
		this.isPaymentConfirm = isPaymentConfirm;
	}

	@Override
	public String toString() {
		return "Notification [notificationId=" + notificationId + ", notificationType=" + notificationType
				+ ", notificationData=" + notificationData + ", createdDate=" + createdDate + ", modifiedDate="
				+ modifiedDate + ", status=" + status + ", mobileNumber=" + mobileNumber + ", emailAddress="
				+ emailAddress + "]";
	}
	
	
}
