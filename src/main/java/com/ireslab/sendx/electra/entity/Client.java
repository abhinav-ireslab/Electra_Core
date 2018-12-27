package com.ireslab.sendx.electra.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
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

/**
 * The persistent class for the client database table.
 * 
 */
@Entity
@Table(name = "client")
@NamedQuery(name = "Client.findAll", query = "SELECT c FROM Client c")
public class Client implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "client_id", unique = true, nullable = false)
	private Integer clientId;

	@Column(name = "client_correlation_id", nullable = false, length = 50)
	private String clientCorrelationId;

	@Column(name = "client_name", nullable = false, length = 50)
	private String clientName;

	@Column(name = "contact_number1", length = 50)
	private String contactNumber1;

	@Column(name = "contact_number2", length = 50)
	private String contactNumber2;

	@Column(name = "contact_number3", length = 50)
	private String contactNumber3;

	@Column(name = "description", length = 255)
	private String description;

	@Column(name = "email_address", length = 50)
	private String emailAddress;

	@Column(name = "is_testnet_account", nullable = false)
	private boolean isTestnetAccount;

	@Column(name = "issuing_account_public_key", length = 255)
	private String issuingAccountPublicKey;

	@Column(name = "issuing_account_secret_key", length = 255)
	private String issuingAccountSecretKey;

	@Column(name = "base_txn_account_public_key", length = 255)
	private String baseTxnAccountPublicKey;

	@Column(name = "base_txn_account_secret_key", length = 255)
	private String baseTxnAccountSecretKey;

	@Column(nullable = false, length = 255)
	private String status;

	@Column(name = "created_by")
	private Integer createdBy;

	@Column(name = "username", nullable = false, length = 50)
	private String username;

	@Column(name = "reset_token", nullable = true, length = 150)
	private String resetToken;

	@Column(name = "password", nullable = true, length = 200)
	private String password;

	@Column(name = "is_2fa_enabled")
	private boolean is2faEnabled;

	@Column(name = "security_code")
	private String securityCode;

	@Column(name = "company_address", nullable = true, length = 150)
	private String companyAddress;

	@Column(name = "company_country", nullable = true, length = 150)
	private String companyCountry;

	@Column(name = "company_state", nullable = true, length = 150)
	private String companyState;

	@Column(name = "company_city", nullable = true, length = 150)
	private String companyCity;

	@Column(name = "company_pin_code", nullable = true, length = 150)
	private String companyPinCode;

	@Column(name = "company_contact", nullable = true, length = 150)
	private String companyContact;

	@Column(name = "company_fax", nullable = true, length = 150)
	private String companyFax;

	@Column(name = "company_code", nullable = true, length = 150)
	private String companyCode;

	@Column(name = "account_type", nullable = true, length = 150)
	private String accountType;

	@Column(name = "created_date", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdDate;

	@Column(name = "modified_by")
	private Integer modifiedBy;
	
	@Column(name = "verification_status", nullable = false)
	private boolean isEkycEkybApproved;
	
	@Column(name = "country_dial_code", nullable = false)
	private String countryDialCode;
	
	@Column(name = "business_id", nullable = true)
	private String businessId;
	
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
	
	@Column(name = "first_name")
	private String firstName;
	
	@Column(name = "last_name")
	private String lastName;
	
	@Column(name = "kyc_transaction_id")
	private String kycTransactionId;
	
	@Column(name = "kyc_transaction_record_id")
	private String kycTransactionRecordId;
	
	@Column(name = "identity_transaction_id")
	private String identityTransactionId;
	
	@Column(name = "identity_transaction_record_id")
	private String identityTransactionRecordId;
	
	@Column(name = "kyc_uploaded_date")
	private String kycUploadedDate;
	
	@Column(name = "gcm_registary_key", nullable = true)
	private String gcmRegistaryKey;
	
	@Column(name = "firebase_service_key", nullable = true)
	private String firebaseServiceKey;

	@Column(name = "modified_date", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date modifiedDate;
	
	@Column(name = "password_reset", nullable = false)
	private boolean isPasswordReset;

	@ManyToOne
	@JoinColumn(name = "client_subs_id", referencedColumnName = "client_subs_id")
	private ClientSubscription clientSubscriptionId;
	
	
	@Column(name = "unique_code", nullable = false)
	private String uniqueCode;

	
	@Column(name = "gst_number", nullable = true, length = 255)
	private String gstNumber;
	
	/*
	 * @OneToMany(mappedBy = "client") private List<ClientConfiguration>
	 * clientConfigurations;
	 */

	@OneToMany(mappedBy = "client", cascade = CascadeType.ALL)
	private List<ClientConfiguration> clientConfigurations;

	
	//cascade = CascadeType.ALL,
	
	@OneToMany(mappedBy = "client", fetch=FetchType.EAGER)
	//@OneToMany(mappedBy = "client")
	private List<ClientAssetToken> clientAssetTokens;

	public Client() {
	}

	
	
	public String getCountryDialCode() {
		return countryDialCode;
	}



	public String getUniqueCode() {
		return uniqueCode;
	}



	public void setUniqueCode(String uniqueCode) {
		this.uniqueCode = uniqueCode;
	}



	public void setCountryDialCode(String countryDialCode) {
		this.countryDialCode = countryDialCode;
	}



	public Integer getClientId() {
		return clientId;
	}

	public void setClientId(Integer clientId) {
		this.clientId = clientId;
	}

	public String getClientCorrelationId() {
		return clientCorrelationId;
	}

	public void setClientCorrelationId(String clientCorrelationId) {
		this.clientCorrelationId = clientCorrelationId;
	}

	public String getClientName() {
		return clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}

	public String getContactNumber1() {
		return contactNumber1;
	}

	public void setContactNumber1(String contactNumber1) {
		this.contactNumber1 = contactNumber1;
	}

	public String getContactNumber2() {
		return contactNumber2;
	}

	public void setContactNumber2(String contactNumber2) {
		this.contactNumber2 = contactNumber2;
	}

	public String getContactNumber3() {
		return contactNumber3;
	}

	public void setContactNumber3(String contactNumber3) {
		this.contactNumber3 = contactNumber3;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public boolean isTestnetAccount() {
		return isTestnetAccount;
	}

	public void setTestnetAccount(boolean isTestnetAccount) {
		this.isTestnetAccount = isTestnetAccount;
	}

	public String getIssuingAccountPublicKey() {
		return issuingAccountPublicKey;
	}

	public void setIssuingAccountPublicKey(String issuingAccountPublicKey) {
		this.issuingAccountPublicKey = issuingAccountPublicKey;
	}

	public String getIssuingAccountSecretKey() {
		return issuingAccountSecretKey;
	}

	public void setIssuingAccountSecretKey(String issuingAccountSecretKey) {
		this.issuingAccountSecretKey = issuingAccountSecretKey;
	}

	public String getBaseTxnAccountPublicKey() {
		return baseTxnAccountPublicKey;
	}

	public void setBaseTxnAccountPublicKey(String baseTxnAccountPublicKey) {
		this.baseTxnAccountPublicKey = baseTxnAccountPublicKey;
	}

	public String getBaseTxnAccountSecretKey() {
		return baseTxnAccountSecretKey;
	}

	public void setBaseTxnAccountSecretKey(String baseTxnAccountSecretKey) {
		this.baseTxnAccountSecretKey = baseTxnAccountSecretKey;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Integer getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(Integer createdBy) {
		this.createdBy = createdBy;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getResetToken() {
		return resetToken;
	}

	public void setResetToken(String resetToken) {
		this.resetToken = resetToken;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isIs2faEnabled() {
		return is2faEnabled;
	}

	public void setIs2faEnabled(boolean is2faEnabled) {
		this.is2faEnabled = is2faEnabled;
	}

	public String getSecurityCode() {
		return securityCode;
	}

	public void setSecurityCode(String securityCode) {
		this.securityCode = securityCode;
	}

	public String getCompanyAddress() {
		return companyAddress;
	}

	public void setCompanyAddress(String companyAddress) {
		this.companyAddress = companyAddress;
	}

	public String getCompanyCountry() {
		return companyCountry;
	}

	public void setCompanyCountry(String companyCountry) {
		this.companyCountry = companyCountry;
	}

	public String getCompanyState() {
		return companyState;
	}

	public void setCompanyState(String companyState) {
		this.companyState = companyState;
	}

	public String getCompanyCity() {
		return companyCity;
	}

	public void setCompanyCity(String companyCity) {
		this.companyCity = companyCity;
	}

	public String getCompanyPinCode() {
		return companyPinCode;
	}

	public void setCompanyPinCode(String companyPinCode) {
		this.companyPinCode = companyPinCode;
	}

	public String getCompanyContact() {
		return companyContact;
	}

	public void setCompanyContact(String companyContact) {
		this.companyContact = companyContact;
	}

	public String getCompanyFax() {
		return companyFax;
	}

	public void setCompanyFax(String companyFax) {
		this.companyFax = companyFax;
	}

	public String getCompanyCode() {
		return companyCode;
	}

	public void setCompanyCode(String companyCode) {
		this.companyCode = companyCode;
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

	public Date getModifiedDate() {
		return modifiedDate;
	}

	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	public List<ClientConfiguration> getClientConfigurations() {
		return clientConfigurations;
	}

	public void setClientConfigurations(List<ClientConfiguration> clientConfigurations) {
		this.clientConfigurations = clientConfigurations;
	}

	public List<ClientAssetToken> getClientAssetTokens() {
		return clientAssetTokens;
	}

	public void setClientAssetTokens(List<ClientAssetToken> clientAssetTokens) {
		this.clientAssetTokens = clientAssetTokens;
	}

	public String getAccountType() {
		return accountType;
	}

	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}

	/**
	 * @return the clientSubscriptionId
	 */
	public ClientSubscription getClientSubscriptionId() {
		return clientSubscriptionId;
	}

	/**
	 * @param clientSubscriptionId
	 *            the clientSubscriptionId to set
	 */
	public void setClientSubscriptionId(ClientSubscription clientSubscriptionId) {
		this.clientSubscriptionId = clientSubscriptionId;
	}

	public boolean isEkycEkybApproved() {
		return isEkycEkybApproved;
	}

	public void setEkycEkybApproved(boolean isEkycEkybApproved) {
		this.isEkycEkybApproved = isEkycEkybApproved;
	}



	public String getBusinessId() {
		return businessId;
	}



	public void setBusinessId(String businessId) {
		this.businessId = businessId;
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



	public boolean isPasswordReset() {
		return isPasswordReset;
	}



	public void setPasswordReset(boolean isPasswordReset) {
		this.isPasswordReset = isPasswordReset;
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



	public String getGstNumber() {
		return gstNumber;
	}



	public void setGstNumber(String gstNumber) {
		this.gstNumber = gstNumber;
	}


}