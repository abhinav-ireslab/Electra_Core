package com.ireslab.sendx.electra.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties
@PropertySource(value = { "classpath:messages.properties" })
public class MessagesProperties {

	public String insufficientAccountBalance;
	public String transactionManagerTransferLumensTokenFail;
	public String tokenManagementUserNotFound;
	public String tokenManagementClientAssetToken;
	public String tokenManagementPaymentTransactionFailure;
	public String tokenManagementUserLock;
	public String stellarDestinationAccountDoesnotExists;
	public String stellarTrustlineCreationFailed;
	public String stellarAccountBalanceOperationFailed;
	public String stellarAccountCreationFailed;
	public String stellarAccountBalanceInsufficient;
	public String ekycComment;
	public String userClientCountryDialCodeNotFound;
	public String productListNotAvailable;//product_list_not_available
	public String addTransactionPurpose;//add_transaction_purpose
	public String updateTransactionPurpose;//update_transaction_purpose
	public String deleteTransactionPurpose;//delete_transaction_purpose
	

	public String addSubscriptionPlan;//add_subscription_plan
	public String updateSubscriptionPlan;//update_subscription_plan
	public String deleteSubscriptionPlan;//delete_subscription_plan
	public String loadTokenFailed;
	
	

	public String getUserClientCountryDialCodeNotFound() {
		return userClientCountryDialCodeNotFound;
	}

	public void setUserClientCountryDialCodeNotFound(String userClientCountryDialCodeNotFound) {
		this.userClientCountryDialCodeNotFound = userClientCountryDialCodeNotFound;
	}

	public String getEkycComment() {
		return ekycComment;
	}

	public void setEkycComment(String ekycComment) {
		this.ekycComment = ekycComment;
	}

	/**
	 * @return the insufficientAccountBalance
	 */
	public String getInsufficientAccountBalance() {
		return insufficientAccountBalance;
	}

	/**
	 * @param insufficientAccountBalance
	 *            the insufficientAccountBalance to set
	 */
	public void setInsufficientAccountBalance(String insufficientAccountBalance) {
		this.insufficientAccountBalance = insufficientAccountBalance;
	}

	/**
	 * @return the transactionManagerTransferLumensTokenFail
	 */
	public String getTransactionManagerTransferLumensTokenFail() {
		return transactionManagerTransferLumensTokenFail;
	}

	/**
	 * @param transactionManagerTransferLumensTokenFail
	 *            the transactionManagerTransferLumensTokenFail to set
	 */
	public void setTransactionManagerTransferLumensTokenFail(String transactionManagerTransferLumensTokenFail) {
		this.transactionManagerTransferLumensTokenFail = transactionManagerTransferLumensTokenFail;
	}

	/**
	 * @return the tokenManagementUserNotFound
	 */
	public String getTokenManagementUserNotFound() {
		return tokenManagementUserNotFound;
	}

	/**
	 * @param tokenManagementUserNotFound
	 *            the tokenManagementUserNotFound to set
	 */
	public void setTokenManagementUserNotFound(String tokenManagementUserNotFound) {
		this.tokenManagementUserNotFound = tokenManagementUserNotFound;
	}

	/**
	 * @return the tokenManagementClientAssetToken
	 */
	public String getTokenManagementClientAssetToken() {
		return tokenManagementClientAssetToken;
	}

	/**
	 * @param tokenManagementClientAssetToken
	 *            the tokenManagementClientAssetToken to set
	 */
	public void setTokenManagementClientAssetToken(String tokenManagementClientAssetToken) {
		this.tokenManagementClientAssetToken = tokenManagementClientAssetToken;
	}

	/**
	 * @return the tokenManagementPaymentTransactionFailure
	 */
	public String getTokenManagementPaymentTransactionFailure() {
		return tokenManagementPaymentTransactionFailure;
	}

	/**
	 * @param tokenManagementPaymentTransactionFailure
	 *            the tokenManagementPaymentTransactionFailure to set
	 */
	public void setTokenManagementPaymentTransactionFailure(String tokenManagementPaymentTransactionFailure) {
		this.tokenManagementPaymentTransactionFailure = tokenManagementPaymentTransactionFailure;
	}

	/**
	 * @return the tokenManagementUserLock
	 */
	public String getTokenManagementUserLock() {
		return tokenManagementUserLock;
	}

	/**
	 * @param tokenManagementUserLock
	 *            the tokenManagementUserLock to set
	 */
	public void setTokenManagementUserLock(String tokenManagementUserLock) {
		this.tokenManagementUserLock = tokenManagementUserLock;
	}

	/**
	 * @return the stellarDestinationAccountDoesnotExists
	 */
	public String getStellarDestinationAccountDoesnotExists() {
		return stellarDestinationAccountDoesnotExists;
	}

	/**
	 * @param stellarDestinationAccountDoesnotExists
	 *            the stellarDestinationAccountDoesnotExists to set
	 */
	public void setStellarDestinationAccountDoesnotExists(String stellarDestinationAccountDoesnotExists) {
		this.stellarDestinationAccountDoesnotExists = stellarDestinationAccountDoesnotExists;
	}

	/**
	 * @return the stellarTrustlineCreationFailed
	 */
	public String getStellarTrustlineCreationFailed() {
		return stellarTrustlineCreationFailed;
	}

	/**
	 * @param stellarTrustlineCreationFailed
	 *            the stellarTrustlineCreationFailed to set
	 */
	public void setStellarTrustlineCreationFailed(String stellarTrustlineCreationFailed) {
		this.stellarTrustlineCreationFailed = stellarTrustlineCreationFailed;
	}

	/**
	 * @return the stellarAccountBalanceOperationFailed
	 */
	public String getStellarAccountBalanceOperationFailed() {
		return stellarAccountBalanceOperationFailed;
	}

	/**
	 * @param stellarAccountBalanceOperationFailed
	 *            the stellarAccountBalanceOperationFailed to set
	 */
	public void setStellarAccountBalanceOperationFailed(String stellarAccountBalanceOperationFailed) {
		this.stellarAccountBalanceOperationFailed = stellarAccountBalanceOperationFailed;
	}

	/**
	 * @return the stellarAccountCreationFailed
	 */
	public String getStellarAccountCreationFailed() {
		return stellarAccountCreationFailed;
	}

	/**
	 * @param stellarAccountCreationFailed
	 *            the stellarAccountCreationFailed to set
	 */
	public void setStellarAccountCreationFailed(String stellarAccountCreationFailed) {
		this.stellarAccountCreationFailed = stellarAccountCreationFailed;
	}

	/**
	 * @return the stellarAccountBalanceInsufficient
	 */
	public String getStellarAccountBalanceInsufficient() {
		return stellarAccountBalanceInsufficient;
	}

	/**
	 * @param stellarAccountBalanceInsufficient
	 *            the stellarAccountBalanceInsufficient to set
	 */
	public void setStellarAccountBalanceInsufficient(String stellarAccountBalanceInsufficient) {
		this.stellarAccountBalanceInsufficient = stellarAccountBalanceInsufficient;
	}

	/**
	 * @return the productListNotAvailable
	 */
	public String getProductListNotAvailable() {
		return productListNotAvailable;
	}

	/**
	 * @param productListNotAvailable the productListNotAvailable to set
	 */
	public void setProductListNotAvailable(String productListNotAvailable) {
		this.productListNotAvailable = productListNotAvailable;
	}

	public String getAddTransactionPurpose() {
		return addTransactionPurpose;
	}

	public void setAddTransactionPurpose(String addTransactionPurpose) {
		this.addTransactionPurpose = addTransactionPurpose;
	}

	public String getUpdateTransactionPurpose() {
		return updateTransactionPurpose;
	}

	public void setUpdateTransactionPurpose(String updateTransactionPurpose) {
		this.updateTransactionPurpose = updateTransactionPurpose;
	}

	public String getDeleteTransactionPurpose() {
		return deleteTransactionPurpose;
	}

	public void setDeleteTransactionPurpose(String deleteTransactionPurpose) {
		this.deleteTransactionPurpose = deleteTransactionPurpose;
	}

	/**
	 * @return the addSubscriptionPlan
	 */
	public String getAddSubscriptionPlan() {
		return addSubscriptionPlan;
	}

	/**
	 * @param addSubscriptionPlan the addSubscriptionPlan to set
	 */
	public void setAddSubscriptionPlan(String addSubscriptionPlan) {
		this.addSubscriptionPlan = addSubscriptionPlan;
	}

	/**
	 * @return the updateSubscriptionPlan
	 */
	public String getUpdateSubscriptionPlan() {
		return updateSubscriptionPlan;
	}

	/**
	 * @param updateSubscriptionPlan the updateSubscriptionPlan to set
	 */
	public void setUpdateSubscriptionPlan(String updateSubscriptionPlan) {
		this.updateSubscriptionPlan = updateSubscriptionPlan;
	}

	/**
	 * @return the deleteSubscriptionPlan
	 */
	public String getDeleteSubscriptionPlan() {
		return deleteSubscriptionPlan;
	}

	/**
	 * @param deleteSubscriptionPlan the deleteSubscriptionPlan to set
	 */
	public void setDeleteSubscriptionPlan(String deleteSubscriptionPlan) {
		this.deleteSubscriptionPlan = deleteSubscriptionPlan;
	}
	

	public String getLoadTokenFailed() {
		return loadTokenFailed;
	}

	public void setLoadTokenFailed(String loadTokenFailed) {
		this.loadTokenFailed = loadTokenFailed;
	}
}
