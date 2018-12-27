package com.ireslab.sendx.electra.service;

import java.util.Date;

import com.ireslab.sendx.electra.dto.ClientTokenConfigurationDto;
import com.ireslab.sendx.electra.dto.VerifyRequest;
import com.ireslab.sendx.electra.dto.VerifyResponse;
import com.ireslab.sendx.electra.entity.Client;
import com.ireslab.sendx.electra.entity.ClientConfiguration;
import com.ireslab.sendx.electra.entity.ClientUser;
import com.ireslab.sendx.electra.entity.TransactionDetail;
import com.ireslab.sendx.electra.model.AccountDetailsResponse;
import com.ireslab.sendx.electra.model.BankDetailResponse;
import com.ireslab.sendx.electra.model.BankDetailsRequest;
import com.ireslab.sendx.electra.model.ClentAgentInvitationRequest;
import com.ireslab.sendx.electra.model.ClentAgentInvitationResponse;
import com.ireslab.sendx.electra.model.ClientInfoRequest;
import com.ireslab.sendx.electra.model.ClientInfoResponse;
import com.ireslab.sendx.electra.model.CountryListResponse;
import com.ireslab.sendx.electra.model.ExchangeRequest;
import com.ireslab.sendx.electra.model.ExchangeResponse;
import com.ireslab.sendx.electra.model.NotificationRequest;
import com.ireslab.sendx.electra.model.NotificationResponse;
import com.ireslab.sendx.electra.model.PaymentRequest;
import com.ireslab.sendx.electra.model.PaymentResponse;
import com.ireslab.sendx.electra.model.ProductAvailabilityRequest;
import com.ireslab.sendx.electra.model.ProductAvailabilityResponse;
import com.ireslab.sendx.electra.model.ProductGroupResponse;
import com.ireslab.sendx.electra.model.ProductRequest;
import com.ireslab.sendx.electra.model.ProductResponse;
import com.ireslab.sendx.electra.model.SaveProductRequest;
import com.ireslab.sendx.electra.model.SaveProductResponse;
import com.ireslab.sendx.electra.model.SendxElectraRequest;
import com.ireslab.sendx.electra.model.SendxElectraResponse;
import com.ireslab.sendx.electra.model.TokenTransferRequest;
import com.ireslab.sendx.electra.model.UserProfileResponse;
import com.ireslab.sendx.electra.utils.ClientConfigurations;
import com.ireslab.sendx.electra.utils.TokenActivity;
import com.ireslab.sendx.electra.utils.TokenOperation;

/**
 * @author iRESlab
 *
 */
public interface CommonService {

	/**
	 * @param clientCorrelationId
	 * @return
	 */
	public Client getClientDetailsByCorrelationId(String clientCorrelationId);

	/**
	 * @param clientCorrelationId
	 * @param configCode
	 * @return
	 */
	public ClientConfiguration getClientConfigurationByClientIdAndConfigCode(String clientCorrelationId,
			ClientConfigurations configCode);

	/**
	 * @param clientId
	 * @param tokenId
	 * @param isLoadInitialAsset
	 * @return
	 */
	public ClientTokenConfigurationDto getClientTokenConfigurationByClientIdAndTokenIdAndIsEnabled(Integer clientId,
			Integer tokenId, boolean isLoadInitialAsset);

	/**
	 * @param clientCorrelationId
	 * @return
	 */
	public ClientUser getClientUserByCorrelationId(String clientCorrelationId);

	/**
	 * @param clientUser
	 */
	public void updateClientUser(ClientUser clientUser);

	// This method is to get account details.
	public AccountDetailsResponse getAccountDetails(String account);

	public TransactionDetail saveTransactionDetails(TokenOperation operation, TokenActivity type, String stellarRequest,
			String stellarResponse, String sourceAccountName, boolean isSourceAccountUser, String sourceCorrelationId,
			String destinationAccountName, boolean isDestinationAccountUser, String destinationCorrelationId,
			short status, String tnxData, String assetCode, String tnxHash, String transactionXdr, Date transactionDate,
			Date modifiedDate , String transactionPurpose, boolean isFee, boolean offline);

	public void transferToClientAccount(TokenTransferRequest tokenTransferRequest);
	
	public ClentAgentInvitationResponse saveClientInvitationDetails(ClentAgentInvitationRequest clentAgentInvitationRequest);

	public CountryListResponse getCountryList(String clientCorrelationId);
	
	public ClientInfoResponse getClientDetailsByCorrelationId(ClientInfoRequest clientInfoRequest);

	public ProductResponse getProductList(ProductRequest productRequest);
	
	public ProductGroupResponse getProductGroupList();
	
	public SaveProductResponse saveProduct(SaveProductRequest productRequest);
	
	public SaveProductResponse editProduct(SaveProductRequest productRequest);
	
	public SaveProductResponse deleteProduct(String productCode);

	public PaymentResponse makePayment(PaymentRequest paymentRequest);

	public ClientInfoResponse getclientInformation(ClientInfoRequest clientInfoRequest);

	public PaymentResponse savePurchasedProduct(PaymentRequest paymentRequest);

	PaymentResponse transferPaymentToClientAccount(TokenTransferRequest tokenTransferRequest);

	public ProductAvailabilityResponse checkProductAvailability(ProductAvailabilityRequest productAvailabilityRequest);
	
	//private VerifyResponse truliooEkycEkyb(VerifyRequest verifyRequest);
	
	public VerifyResponse truliooEkycEkyb(VerifyRequest verifyrequest, String username, String password);

	public BankDetailResponse saveBankDetails(BankDetailsRequest bankDetailsRequest);

	public void transferFeeToMasterAccount(TokenTransferRequest tokenTransferRequest);

	public BankDetailResponse getBankDetailsByClientEmail(BankDetailsRequest bankDetailsRequest);

	public UserProfileResponse getClientOrUserDetailsByUniqueCode(String uniqueCode);

	public NotificationResponse saveNotification(NotificationRequest notificationRequest);

	public SendxElectraResponse updateNotification(SendxElectraRequest sendxElectraRequest);

	public SendxElectraResponse getAllNotification(String mobileNumber);

	public SendxElectraResponse updateDeviceSpecificParameter(SendxElectraRequest sendxElectraRequest);

	public PaymentResponse makeOfflinePayment(TokenTransferRequest tokenTransferRequest);

	public ProductResponse getProductListForConsole(ProductRequest productRequest);

	public PaymentResponse updateInvoicedProduct(PaymentRequest paymentRequest);

	public NotificationResponse deleteNotification(NotificationRequest notificationRequest);

	public ExchangeResponse getExchangeRate(ExchangeRequest exchangeRequest);

	public UserProfileResponse getClientOrUserDetailsByMobileNumber(String countryDailCode, String mobileNumber);

	public SendxElectraResponse getAllSettlementReports();

	public SendxElectraResponse updateSettlementReport(SendxElectraRequest sendxElectraRequest);

	public SendxElectraResponse getAllSettlementReports(String correlationId);
	

}
