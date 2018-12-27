package com.ireslab.sendx.electra.entity;

import com.ireslab.sendx.electra.utils.Status;

import java.io.Serializable;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * The persistent class for the client_users database table.
 *
 */
@Entity
@Table(name = "client_users")
@NamedQuery(name = "ClientUser.findAll", query = "SELECT c FROM ClientUser c")
public class ClientUser implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "user_id", unique = true, nullable = false)
	private Integer userId;

	@Column(name = "first_name", length = 50)
	private String firstName;

	@Column(name = "last_name", length = 50)
	private String lastName;

	@Column(name = "user_correlation_id", nullable = false, length = 50)
	private String userCorrelationId;

	@Column(name = "account_public_key", nullable = false, length = 255)
	private String accountPublicKey;

	@Column(name = "account_secret_key", nullable = false, length = 255)
	private String accountSecretKey;

	@Column(name = "status")
	@Enumerated(EnumType.STRING)
	private Status status;

	@Column(name = "created_by")
	private Integer createdBy;

	@Column(name = "created_date", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdDate;

	@Column(name = "modified_by")
	private Integer modifiedBy;
	
	@Column(name = "verification_status", nullable = false)
	private boolean isEkycEkybApproved;
	
	
	@Column(name = "mobile_number", nullable = true)
	private String mobileNumber;
	
	@Column(name = "email_address", nullable = true)
	private String emailAddress;
	
	@Column(name = "unique_code", nullable = true)
	private String uniqueCode;
	
	@Column(name = "gcm_registary_key", nullable = true)
	private String gcmRegistaryKey;
	
	@Column(name = "firebase_service_key", nullable = true)
	private String firebaseServiceKey;
	
	@Column(name = "country_dial_code", nullable = true)
	private String countryDialCode;
	
	@Column(name = "business_id", nullable = true)
	private String businessId;
	
	@Column(name = "postal_code", nullable = true)
	private String postalCode;
	
	@Column(name = "dob", nullable = true)
	private String dob;
	
	@Column(name = "gender", nullable = true)
	private String gender;
	
	@Column(name = "scan_document_type", nullable = true)
	private String scanDocumentType;
	
	@Column(name = "scan_document_id", nullable = true)
	private String scanDocumentId;
	
	@Column(name = "scan_document_front_part", nullable = true)
	private String scanDocumentFrontPart;
	
	@Column(name = "scan_document_back_part", nullable = true)
	private String scanDocumentBackPart;
	
	@Column(name = "id_proof_image", nullable = true)
	private String idProofImage;
	
	@Column(name = "profile_image_url")
	private String profileImageUrl;

	@Column(name = "residential_address")
	private String residentialAddress;
	
	@Column(name = "business_longitude")
	private String businessLongitude;

	@Column(name = "business_latitude")
	private String businessLatitude;
	
	@Column(name = "kyc_transaction_id")
	private String kycTransactionId;
	
	@Column(name = "kyc_transaction_record_id")
	private String kycTransactionRecordId;
	
	@Column(name = "identity_transaction_id")
	private String identityTransactionId;
	
	@Column(name = "identity_transaction_record_id")
	private String identityTransactionRecordId;
	
	@Column(name = "subscription_durations")
	private String subscriptionDurations;
	
	@Column(name = "subscription_expire_on",nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date subscriptionExpireOn;
	
	
	
	
	public String getSubscriptionDurations() {
		return subscriptionDurations;
	}

	public void setSubscriptionDurations(String subscriptionDurations) {
		this.subscriptionDurations = subscriptionDurations;
	}

	public Date getSubscriptionExpireOn() {
		return subscriptionExpireOn;
	}

	public void setSubscriptionExpireOn(Date subscriptionExpireOn) {
		this.subscriptionExpireOn = subscriptionExpireOn;
	}

	public String getIdentityTransactionId() {
		return identityTransactionId;
	}

	public void setIdentityTransactionId(String identityTransactionId) {
		this.identityTransactionId = identityTransactionId;
	}

	public String getIdentityTransactionRecordId() {
		return identityTransactionRecordId;
	}

	public void setIdentityTransactionRecordId(String identityTransactionRecordId) {
		this.identityTransactionRecordId = identityTransactionRecordId;
	}

	@Column(name = "kyc_uploaded_date")
	private String kycUploadedDate;

	public String getGcmRegistaryKey() {
		return gcmRegistaryKey;
	}

	public void setGcmRegistaryKey(String gcmRegistaryKey) {
		this.gcmRegistaryKey = gcmRegistaryKey;
	}

	public String getFirebaseServiceKey() {
		return firebaseServiceKey;
	}

	public void setFirebaseServiceKey(String firebaseServiceKey) {
		this.firebaseServiceKey = firebaseServiceKey;
	}

	@Column(name = "modified_date", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date modifiedDate;
	
	@ManyToOne
	@JoinColumn(name = "client_id", referencedColumnName = "client_id")
	private Client client;

	@OneToMany(mappedBy = "clientUser", fetch = FetchType.LAZY)
	private List<ClientUserToken> clientUserTokens;

	public ClientUser() {
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getUserCorrelationId() {
		return userCorrelationId;
	}

	public void setUserCorrelationId(String userCorrelationId) {
		this.userCorrelationId = userCorrelationId;
	}

	public String getAccountPublicKey() {
		return accountPublicKey;
	}

	public void setAccountPublicKey(String accountPublicKey) {
		this.accountPublicKey = accountPublicKey;
	}

	public String getAccountSecretKey() {
		return accountSecretKey;
	}

	public void setAccountSecretKey(String accountSecretKey) {
		this.accountSecretKey = accountSecretKey;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public Integer getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(Integer createdBy) {
		this.createdBy = createdBy;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Integer getModifiedBy() {
		return modifiedBy;
	}

	public void setModifiedBy(Integer modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	public boolean isEkycEkybApproved() {
		return isEkycEkybApproved;
	}

	public void setEkycEkybApproved(boolean isEkycEkybApproved) {
		this.isEkycEkybApproved = isEkycEkybApproved;
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

	public String getUniqueCode() {
		return uniqueCode;
	}

	public void setUniqueCode(String uniqueCode) {
		this.uniqueCode = uniqueCode;
	}

	public String getCountryDialCode() {
		return countryDialCode;
	}

	public void setCountryDialCode(String countryDialCode) {
		this.countryDialCode = countryDialCode;
	}

	public String getBusinessId() {
		return businessId;
	}

	public void setBusinessId(String businessId) {
		this.businessId = businessId;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	public String getDob() {
		return dob;
	}

	public void setDob(String dob) {
		this.dob = dob;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getScanDocumentType() {
		return scanDocumentType;
	}

	public void setScanDocumentType(String scanDocumentType) {
		this.scanDocumentType = scanDocumentType;
	}

	public String getScanDocumentId() {
		return scanDocumentId;
	}

	public void setScanDocumentId(String scanDocumentId) {
		this.scanDocumentId = scanDocumentId;
	}

	public String getScanDocumentFrontPart() {
		return scanDocumentFrontPart;
	}

	public void setScanDocumentFrontPart(String scanDocumentFrontPart) {
		this.scanDocumentFrontPart = scanDocumentFrontPart;
	}

	public String getScanDocumentBackPart() {
		return scanDocumentBackPart;
	}

	public void setScanDocumentBackPart(String scanDocumentBackPart) {
		this.scanDocumentBackPart = scanDocumentBackPart;
	}

	public String getProfileImageUrl() {
		return profileImageUrl;
	}

	public void setProfileImageUrl(String profileImageUrl) {
		this.profileImageUrl = profileImageUrl;
	}

	public String getResidentialAddress() {
		return residentialAddress;
	}

	public void setResidentialAddress(String residentialAddress) {
		this.residentialAddress = residentialAddress;
	}

	public Date getModifiedDate() {
		return modifiedDate;
	}

	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	public Client getClient() {
		return client;
	}

	public void setClient(Client client) {
		this.client = client;
	}

	public List<ClientUserToken> getClientUserTokens() {
		return clientUserTokens;
	}

	public void setClientUserTokens(List<ClientUserToken> clientUserTokens) {
		this.clientUserTokens = clientUserTokens;
	}
	
	

	public String getBusinessLongitude() {
		return businessLongitude;
	}

	public void setBusinessLongitude(String businessLongitude) {
		this.businessLongitude = businessLongitude;
	}

	public String getBusinessLatitude() {
		return businessLatitude;
	}

	public void setBusinessLatitude(String businessLatitude) {
		this.businessLatitude = businessLatitude;
	}

	public String getKycTransactionId() {
		return kycTransactionId;
	}

	public void setKycTransactionId(String kycTransactionId) {
		this.kycTransactionId = kycTransactionId;
	}

	

	public String getKycTransactionRecordId() {
		return kycTransactionRecordId;
	}

	public void setKycTransactionRecordId(String kycTransactionRecordId) {
		this.kycTransactionRecordId = kycTransactionRecordId;
	}

	public String getKycUploadedDate() {
		return kycUploadedDate;
	}

	public void setKycUploadedDate(String kycUploadedDate) {
		this.kycUploadedDate = kycUploadedDate;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	/**
	 * @return the idProofImage
	 */
	public String getIdProofImage() {
		return idProofImage;
	}

	/**
	 * @param idProofImage the idProofImage to set
	 */
	public void setIdProofImage(String idProofImage) {
		this.idProofImage = idProofImage;
	}
	
	
}