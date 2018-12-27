package com.ireslab.sendx.electra;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource(value = "classpath:electra_config.properties")
@ConfigurationProperties
public class ElectraConfig {

	public String appBaseUrl;

	public String userProfileImageUrl;

	public String imageDirectoryRelativePath;
	
	public String truliooIdentityVerificationUsername;
	
	public String truliooIdentityVerificationPassword;
	
	public String truliooDocumentVerificationUsername;
	
	public String truliooDocumentVerificationPassword;
	
	public String truliooBusinessVerificationUsername;
	
	public String truliooBusinessVerificationPassword;
	//is_kyc_configure
	public Boolean isKycConfigure;
	
	public boolean isTestnetAccount;
	public String initialLumensLoadQuantity;
	public boolean isLoadInitialLumens;
	
	public String settlementReportModifiedBy;
	private String settlementNotificationTitle;
	private String settlementNotificationBody;
	private String settlementNotificationMessage;
	private String settlementNotificationCode;
	
	
	
	
	public String getSettlementNotificationTitle() {
		return settlementNotificationTitle;
	}

	public void setSettlementNotificationTitle(String settlementNotificationTitle) {
		this.settlementNotificationTitle = settlementNotificationTitle;
	}

	public String getSettlementNotificationBody() {
		return settlementNotificationBody;
	}

	public void setSettlementNotificationBody(String settlementNotificationBody) {
		this.settlementNotificationBody = settlementNotificationBody;
	}

	public String getSettlementNotificationMessage() {
		return settlementNotificationMessage;
	}

	public void setSettlementNotificationMessage(String settlementNotificationMessage) {
		this.settlementNotificationMessage = settlementNotificationMessage;
	}

	public String getSettlementNotificationCode() {
		return settlementNotificationCode;
	}

	public void setSettlementNotificationCode(String settlementNotificationCode) {
		this.settlementNotificationCode = settlementNotificationCode;
	}

	public String getSettlementReportModifiedBy() {
		return settlementReportModifiedBy;
	}

	public void setSettlementReportModifiedBy(String settlementReportModifiedBy) {
		this.settlementReportModifiedBy = settlementReportModifiedBy;
	}

	public boolean isLoadInitialLumens() {
		return isLoadInitialLumens;
	}

	public void setIsLoadInitialLumens(boolean isLoadInitialLumens) {
		this.isLoadInitialLumens = isLoadInitialLumens;
	}

	public String getInitialLumensLoadQuantity() {
		return initialLumensLoadQuantity;
	}

	public void setInitialLumensLoadQuantity(String initialLumensLoadQuantity) {
		this.initialLumensLoadQuantity = initialLumensLoadQuantity;
	}

	public boolean getIsTestnetAccount() {
		return isTestnetAccount;
	}

	public void setIsTestnetAccount(boolean isTestnetAccount) {
		this.isTestnetAccount = isTestnetAccount;
	}

	/**
	 * @return the appBaseUrl
	 */
	public String getAppBaseUrl() {
		return appBaseUrl;
	}

	/**
	 * @return the userProfileImageUrl
	 */
	public String getUserProfileImageUrl() {
		return userProfileImageUrl;
	}

	public String getImageDirectoryRelativePath() {
		return imageDirectoryRelativePath;
	}

	public void setImageDirectoryRelativePath(String imageDirectoryRelativePath) {
		this.imageDirectoryRelativePath = imageDirectoryRelativePath;
	}

	/**
	 * @param appBaseUrl
	 *            the appBaseUrl to set
	 */
	public void setAppBaseUrl(String appBaseUrl) {
		this.appBaseUrl = appBaseUrl;
	}

	/**
	 * @param userProfileImageUrl
	 *            the userProfileImageUrl to set
	 */
	public void setUserProfileImageUrl(String userProfileImageUrl) {
		this.userProfileImageUrl = userProfileImageUrl;
	}
	
	
	public String getTruliooIdentityVerificationUsername() {
		return truliooIdentityVerificationUsername;
	}

	public void setTruliooIdentityVerificationUsername(String truliooIdentityVerificationUsername) {
		this.truliooIdentityVerificationUsername = truliooIdentityVerificationUsername;
	}

	public String getTruliooIdentityVerificationPassword() {
		return truliooIdentityVerificationPassword;
	}

	public void setTruliooIdentityVerificationPassword(String truliooIdentityVerificationPassword) {
		this.truliooIdentityVerificationPassword = truliooIdentityVerificationPassword;
	}

	public String getTruliooDocumentVerificationUsername() {
		return truliooDocumentVerificationUsername;
	}

	public void setTruliooDocumentVerificationUsername(String truliooDocumentVerificationUsername) {
		this.truliooDocumentVerificationUsername = truliooDocumentVerificationUsername;
	}

	public String getTruliooDocumentVerificationPassword() {
		return truliooDocumentVerificationPassword;
	}

	public void setTruliooDocumentVerificationPassword(String truliooDocumentVerificationPassword) {
		this.truliooDocumentVerificationPassword = truliooDocumentVerificationPassword;
	}

	public String getTruliooBusinessVerificationUsername() {
		return truliooBusinessVerificationUsername;
	}

	public void setTruliooBusinessVerificationUsername(String truliooBusinessVerificationUsername) {
		this.truliooBusinessVerificationUsername = truliooBusinessVerificationUsername;
	}

	public String getTruliooBusinessVerificationPassword() {
		return truliooBusinessVerificationPassword;
	}

	public void setTruliooBusinessVerificationPassword(String truliooBusinessVerificationPassword) {
		this.truliooBusinessVerificationPassword = truliooBusinessVerificationPassword;
	}

	

	public Boolean getIsKycConfigure() {
		return isKycConfigure;
	}

	public void setIsKycConfigure(Boolean isKycConfigure) {
		this.isKycConfigure = isKycConfigure;
	}

	public static void main(String[] args) {
		String s1="+91111";
		System.out.println(s1.replace("+", ""));
	}

}
