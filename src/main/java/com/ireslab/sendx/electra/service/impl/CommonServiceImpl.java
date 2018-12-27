package com.ireslab.sendx.electra.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.apache.commons.codec.binary.Base64;
import org.json.JSONException;
import org.json.JSONObject;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.gson.Gson;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.ireslab.sendx.electra.ElectraConfig;
import com.ireslab.sendx.electra.dto.CashOutDto;
import com.ireslab.sendx.electra.dto.ClientTokenConfigurationDto;
import com.ireslab.sendx.electra.dto.ExchangeDto;
import com.ireslab.sendx.electra.dto.MerchantDto;
import com.ireslab.sendx.electra.dto.NotificationDto;
import com.ireslab.sendx.electra.dto.ProductDto;
import com.ireslab.sendx.electra.dto.ProductGroupDto;
import com.ireslab.sendx.electra.dto.PurchaserDto;
import com.ireslab.sendx.electra.dto.StellarTransactionConfigDto;
import com.ireslab.sendx.electra.dto.StellarTransactionConfigDto.IssuingAccount;
import com.ireslab.sendx.electra.dto.StellarTransactionConfigDto.ReceiverAccount;
import com.ireslab.sendx.electra.dto.StellarTransactionConfigDto.SenderAccount;
import com.ireslab.sendx.electra.dto.TransactionDto;
import com.ireslab.sendx.electra.dto.VerifyRequest;
import com.ireslab.sendx.electra.dto.VerifyResponse;
import com.ireslab.sendx.electra.entity.BankDetails;
import com.ireslab.sendx.electra.entity.CashOut;
import com.ireslab.sendx.electra.entity.Client;
import com.ireslab.sendx.electra.entity.ClientAgentInvitation;
import com.ireslab.sendx.electra.entity.ClientConfiguration;
import com.ireslab.sendx.electra.entity.ClientTokenConfiguration;
import com.ireslab.sendx.electra.entity.ClientUser;
import com.ireslab.sendx.electra.entity.Country;
import com.ireslab.sendx.electra.entity.Exchange;
import com.ireslab.sendx.electra.entity.MasterAccounts;
import com.ireslab.sendx.electra.entity.Notification;
import com.ireslab.sendx.electra.entity.Product;
import com.ireslab.sendx.electra.entity.ProductGroup;
import com.ireslab.sendx.electra.entity.ProductPurchaseHistory;
import com.ireslab.sendx.electra.entity.TransactionDetail;
import com.ireslab.sendx.electra.exceptions.ApiException;
import com.ireslab.sendx.electra.model.AccountDetailsResponse;
import com.ireslab.sendx.electra.model.BankDetailDto;
import com.ireslab.sendx.electra.model.BankDetailResponse;
import com.ireslab.sendx.electra.model.BankDetailsRequest;
import com.ireslab.sendx.electra.model.ClentAgentInvitationRequest;
import com.ireslab.sendx.electra.model.ClentAgentInvitationResponse;
import com.ireslab.sendx.electra.model.ClientInfoRequest;
import com.ireslab.sendx.electra.model.ClientInfoResponse;
import com.ireslab.sendx.electra.model.CountryDetails;
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
import com.ireslab.sendx.electra.model.TokenLifecycleManagementRequest;
import com.ireslab.sendx.electra.model.TokenLifecycleManagementResponse;
import com.ireslab.sendx.electra.model.TokenTransferRequest;
import com.ireslab.sendx.electra.model.UserProfile;
import com.ireslab.sendx.electra.model.UserProfileResponse;
import com.ireslab.sendx.electra.notification.AndroidPushNotificationRequest;
import com.ireslab.sendx.electra.notification.AndroidPushNotificationsService;
import com.ireslab.sendx.electra.properties.MessagesProperties;
import com.ireslab.sendx.electra.repository.BankDetailsRepository;
import com.ireslab.sendx.electra.repository.CashOutRepository;
import com.ireslab.sendx.electra.repository.ClientAgentInvitationRepository;
import com.ireslab.sendx.electra.repository.ClientRepository;
import com.ireslab.sendx.electra.repository.ClientTokenConfigurationRepository;
import com.ireslab.sendx.electra.repository.ClientUserRepository;
import com.ireslab.sendx.electra.repository.CountryRepository;
import com.ireslab.sendx.electra.repository.ExchangeRepository;
import com.ireslab.sendx.electra.repository.MasterAccountRepository;
import com.ireslab.sendx.electra.repository.NotificationRepository;
import com.ireslab.sendx.electra.repository.PaymentTermRepository;
import com.ireslab.sendx.electra.repository.ProductGroupRepository;
import com.ireslab.sendx.electra.repository.ProductPurchasedRepository;
import com.ireslab.sendx.electra.repository.ProductRepository;
import com.ireslab.sendx.electra.repository.TransactionDetailRepository;
import com.ireslab.sendx.electra.service.ClientAgentInvitationService;
import com.ireslab.sendx.electra.service.ClientProfileMgmtApiService;
import com.ireslab.sendx.electra.service.CommonService;
import com.ireslab.sendx.electra.service.UserProfileMgmtApiService;
import com.ireslab.sendx.electra.utils.AppConstants;
import com.ireslab.sendx.electra.utils.ApplicationStatusCodes;
import com.ireslab.sendx.electra.utils.ClientConfigurations;
import com.ireslab.sendx.electra.utils.CommonUtils;
import com.ireslab.sendx.electra.utils.PhoneNumberValidationUtils;
import com.ireslab.sendx.electra.utils.SMSService;
import com.ireslab.sendx.electra.utils.Status;
import com.ireslab.sendx.electra.utils.TokenActivity;
import com.ireslab.sendx.electra.utils.TokenOperation;

/**
 * @author iRESlab
 *
 */
@Service
public class CommonServiceImpl implements CommonService {

	private static final Logger LOG = LoggerFactory.getLogger(CommonServiceImpl.class);

	private static Map<String, Client> clientCorrelationDetailsMap = new HashMap<>();
	private static Map<String, List<ClientTokenConfigurationDto>> clientTokenConfigDetailsMap = new HashMap<>();

	private static Map<String, ClientUser> clientUserCorrelationDetailsMap = new HashMap<>();

	@Autowired
	private ModelMapper modelMapper;
	
	@Autowired
	private ClientRepository clientRepository;
	
	@Autowired
	private ClientUserRepository clientUserRepository;

	@Autowired
	private CountryRepository countryRepo;

	@Autowired
	private ProductRepository productRepo;

	@Autowired
	private ProductPurchasedRepository purchasedRepo;

	@Autowired
	private ClientTokenConfigurationRepository clientTokenConfigRepo;

	@Autowired
	private ModelMapper mapper;

	@Autowired
	private ClientUserRepository clientUserRepo;

	@Autowired
	private TransactionDetailRepository txnDetailRepo;

	@Autowired
	private ProductGroupRepository productGroupRepository;

	@Autowired
	private ClientAgentInvitationRepository clientAgentInvitationRepo;

	@Autowired
	private ClientAgentInvitationService clientAgentInvitationService;

	@Autowired
	private SMSService smsService;

	@Autowired
	private CommonService commonService;
	
	@Autowired
	private PaymentTermRepository paymentRepository;

	@Autowired
	private MasterAccountRepository masterAccountRepository;

	@Autowired
	private ObjectWriter objectWriter;
	
	@Autowired
	private ExchangeRepository exchangeRepository;

	@Autowired
	private TokenManagementServiceImpl tokenManagementServiceImpl;

	@Autowired
	private ElectraConfig electraConfig;

	@Autowired
	private BankDetailsRepository bankDetailsRepo;

	@Autowired
	private MessagesProperties messagesProperties;

	@Autowired
	Gson gson;
	
	@Autowired
	private UserProfileMgmtApiService userProfileMgmtService;
	
	@Autowired
	private ClientProfileMgmtApiService clientProfileMgmtService;
	
	@Autowired
	private NotificationRepository notificationRepo;
	
	@Autowired
	private CashOutRepository cashOutRepository;
	
	@Autowired
	private AndroidPushNotificationsService pushNotificationService;

	/**
	 * @param clientCorrelationId
	 * @return
	 */
	@Override
	public Client getClientDetailsByCorrelationId(String clientCorrelationId) {

		Client client = clientCorrelationDetailsMap.get(clientCorrelationId);

		if (client == null) {
			LOG.info("Preparing map for Client Correlation ID and Client details . . . ");

			clientRepository.findAll().forEach((clientFromDb) -> {
				clientCorrelationDetailsMap.put(clientFromDb.getClientCorrelationId(), clientFromDb);
			});

			client = clientCorrelationDetailsMap.get(clientCorrelationId);
		}

		return client;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ireslab.sendx.electra.service.CommonService#
	 * getClientConfigurationByClientIdAndConfigCode(java.lang.String,
	 * com.ireslab.sendx.electra.utils.ClientConfigurations)
	 */
	@Override
	@Transactional
	public ClientConfiguration getClientConfigurationByClientIdAndConfigCode(String clientCorrelationId,
			ClientConfigurations configCode) {
		Client client = getClientDetailsByCorrelationId(clientCorrelationId);
		List<ClientConfiguration> s = client.getClientConfigurations();
		return s.stream().filter(
				o -> o.getClientConfigurationMaster().getConfigCode().compareToIgnoreCase(configCode.name()) == 0)
				.collect(singletonCollector());

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ireslab.sendx.electra.service.CommonService#
	 * getClientTokenConfigurationByClientIdAndTokenIdAndIsEnabled(java.lang.
	 * Integer, java.lang.Integer, boolean)
	 */
	public ClientTokenConfigurationDto getClientTokenConfigurationByClientIdAndTokenIdAndIsEnabled(Integer clientId,
			Integer tokenId, boolean isLoadInitialAsset) {
		String key = clientId + "_" + tokenId;
		List<ClientTokenConfigurationDto> clientTokenConfigurationDtos = clientTokenConfigDetailsMap.get(key);
		ClientTokenConfigurationDto config = null;
		if (clientTokenConfigurationDtos == null) {
			List<ClientTokenConfiguration> clientTokenConfigurations = clientTokenConfigRepo
					.findByClientIdAndTokenId(clientId, tokenId);

			java.lang.reflect.Type clientConfigurationMasterDtoTargetListType = new TypeToken<List<ClientTokenConfigurationDto>>() {
			}.getType();

			clientTokenConfigurationDtos = mapper.map(clientTokenConfigurations,
					clientConfigurationMasterDtoTargetListType);
			clientTokenConfigDetailsMap.put(key, clientTokenConfigurationDtos);

		}

		config = clientTokenConfigurationDtos.stream()
				.filter(listObject -> (listObject.isEnabled() == isLoadInitialAsset)).collect(singletonCollector());

		return config;
	}

	/* (non-Javadoc)
	 * @see com.ireslab.sendx.electra.service.CommonService#getClientUserByCorrelationId(java.lang.String)
	 */
	@Override
	public ClientUser getClientUserByCorrelationId(String userCorrelationId) {

		LOG.info("Preparing map for Client User Correlation ID  . . . ");

		ClientUser clientUser = clientUserCorrelationDetailsMap.get(userCorrelationId);
		if (clientUser == null) {

			clientUserRepo.findAll().forEach((clientUserFromDb) -> {
				clientUserCorrelationDetailsMap.put(clientUserFromDb.getUserCorrelationId(), clientUserFromDb);
			});

			clientUser = clientUserCorrelationDetailsMap.get(userCorrelationId);

		}

		return clientUser;
	}

	/* (non-Javadoc)
	 * @see com.ireslab.sendx.electra.service.CommonService#updateClientUser(com.ireslab.sendx.electra.entity.ClientUser)
	 */
	public void updateClientUser(ClientUser clientUser) {

		clientUserRepo.save(clientUser);
		clientUserCorrelationDetailsMap.put(clientUser.getUserCorrelationId(), clientUser);

	}

	/**
	 * @return
	 */
	private static <T> Collector<T, ?, T> singletonCollector() {
		return Collectors.collectingAndThen(Collectors.toList(), list -> {
			if (list.size() != 1) {
				// throw new IllegalStateException();
				return null;
			}
			return list.get(0);
		});
	}

	/* (non-Javadoc)
	 * @see com.ireslab.sendx.electra.service.CommonService#getAccountDetails(java.lang.String)
	 */
	@Override
	public AccountDetailsResponse getAccountDetails(String accountPublicKey) {
		AccountDetailsResponse accountDetailsResponse = null;
		

		// Search Account is of client.

		Client clientByIssuingAccountPublicKey = clientRepository.findByIssuingAccountPublicKey(accountPublicKey);
		Client clientByBaseTxnAccountPublicKey = clientRepository.findByBaseTxnAccountPublicKey(accountPublicKey);
		ClientUser clientUser = clientUserRepo.findByAccountPublicKey(accountPublicKey);
		if (clientByIssuingAccountPublicKey != null) {
			accountDetailsResponse = new AccountDetailsResponse();
			accountDetailsResponse.setClientName(clientByIssuingAccountPublicKey.getClientName() + " - Issuing");
		} else if (clientByBaseTxnAccountPublicKey != null) {
			accountDetailsResponse = new AccountDetailsResponse();
			accountDetailsResponse.setClientName(clientByBaseTxnAccountPublicKey.getClientName() + " - Base");
		} else if (clientUser != null) {
			accountDetailsResponse = new AccountDetailsResponse();
			accountDetailsResponse.setFirstName(clientUser.getFirstName());
			accountDetailsResponse.setLastName(clientUser.getLastName());
		}

		return accountDetailsResponse;
	}

	/* (non-Javadoc)
	 * @see com.ireslab.sendx.electra.service.CommonService#saveTransactionDetails(com.ireslab.sendx.electra.utils.TokenOperation, com.ireslab.sendx.electra.utils.TokenActivity, java.lang.String, java.lang.String, java.lang.String, boolean, java.lang.String, java.lang.String, boolean, java.lang.String, short, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.util.Date, java.util.Date, java.lang.String, boolean, boolean)
	 */
	@Override
	public TransactionDetail saveTransactionDetails(TokenOperation operation, TokenActivity type, String stellarRequest,
			String stellarResponse, String sourceAccountName, boolean isSourceAccountUser, String sourceCorrelationId,
			String destinationAccountName, boolean isDestinationAccountUser, String destinationCorrelationId,
			short status, String tnxData, String assetCode, String tnxHash, String transactionXdr, Date transactionDate,
			Date modifiedDate, String transactionPurpose, boolean fee, boolean offline) {

		TransactionDetail transactionDetail = new TransactionDetail();
		//TimeZone.setDefault(TimeZone.getTimeZone("UTC"));

		transactionDetail.setOperation(operation);
		transactionDetail.setType(type);
		transactionDetail.setStellarRequest(stellarRequest);
		transactionDetail.setStellarResponse(stellarResponse);
		transactionDetail.setSourceAccountName(sourceAccountName);
		transactionDetail.setSourceAccountUser(isSourceAccountUser);// boolean
		transactionDetail.setSourceCorrelationId(sourceCorrelationId);
		transactionDetail.setDestinationAccountName(destinationAccountName);
		transactionDetail.setDestinationAccountUser(isDestinationAccountUser);
		transactionDetail.setDestinationCorrelationId(destinationCorrelationId);
		transactionDetail.setStatus(status);
		transactionDetail.setTnxData(tnxData);
		transactionDetail.setAssetCode(assetCode);
		transactionDetail.setTnxHash(tnxHash);
		transactionDetail.setTransactionXdr(transactionXdr);
		transactionDetail.setTransactionPurpose(transactionPurpose);
		transactionDetail.setTransactionDate(new Date());
		transactionDetail.setModifiedDate(new Date());
		transactionDetail.setFee(fee);
		transactionDetail.setIsOffline(offline);

		transactionDetail = txnDetailRepo.save(transactionDetail);
		LOG.info("transaction detail data saved.");

		String txnSequenceNumber = org.apache.commons.lang3.StringUtils
				.leftPad(String.valueOf(transactionDetail.getTransactionDetailId()), 6, "0");
		txnSequenceNumber = "STX-" + txnSequenceNumber;
		transactionDetail.setTransactionSequenceNo(txnSequenceNumber);
		transactionDetail = txnDetailRepo.save(transactionDetail);
		return transactionDetail;
	}

	/* (non-Javadoc)
	 * @see com.ireslab.sendx.electra.service.CommonService#transferToClientAccount(com.ireslab.sendx.electra.model.TokenTransferRequest)
	 */
	@Override
	public void transferToClientAccount(TokenTransferRequest tokenTransferRequest) {

		try {


			Client client = null;
			
			// clientRepository.findByClientCorrelationId(tokenTransferRequest.getReceiverCorrelationId());
			if (tokenTransferRequest.getClientId() != null) {
				client = clientRepository.findByClientCorrelationId(tokenTransferRequest.getClientId());
			} else {
				client = clientRepository.findByClientCorrelationId(tokenTransferRequest.getReceiverCorrelationId());
			}

			
			LOG.debug("Sender CorrelationId : " + tokenTransferRequest.getSenderCorrelationId()
					+ " Receiver correletionId :" + tokenTransferRequest.getClientId());
			

			

			// stellarConfig.setAssetCode(clientUser.get);

			TokenLifecycleManagementRequest tokenManagementRequest = new TokenLifecycleManagementRequest();
			tokenManagementRequest.setUserCorrelationId(tokenTransferRequest.getSenderCorrelationId());
			tokenManagementRequest.setBeneficiaryCorrelationId(tokenTransferRequest.getReceiverCorrelationId());
			tokenManagementRequest.setNoOfTokens(tokenTransferRequest.getNoOfToken());
			tokenManagementRequest.setTransactionPurpose(tokenTransferRequest.getTransactionPurpose());
			tokenManagementRequest.setIsFee(true);
			LOG.debug("Inside try  - NO of token :- " + tokenManagementRequest.getNoOfTokens()
					+ "\n sender correlation Id :- " + tokenManagementRequest.getUserCorrelationId()
					+ "\n Receiver Correlation id :- " + tokenManagementRequest.getBeneficiaryCorrelationId());
			tokenManagementServiceImpl.transferTokens(tokenManagementRequest);

			
		} catch (Exception e) {
			TokenLifecycleManagementRequest tokenManagementRequest = new TokenLifecycleManagementRequest();
			tokenManagementRequest.setUserCorrelationId(tokenTransferRequest.getSenderCorrelationId());
			tokenManagementRequest.setBeneficiaryCorrelationId(tokenTransferRequest.getReceiverCorrelationId());
			tokenManagementRequest.setNoOfTokens(tokenTransferRequest.getNoOfToken());
			tokenManagementRequest.setTransactionPurpose(tokenTransferRequest.getTransactionPurpose());
			tokenManagementRequest.setIsFee(true);
			LOG.debug("Inside catch  - NO of token :- " + tokenManagementRequest.getNoOfTokens()
					+ "\n sender correlation Id :- " + tokenManagementRequest.getUserCorrelationId()
					+ "\n Receiver Correlation id :- " + tokenManagementRequest.getBeneficiaryCorrelationId());
			tokenManagementServiceImpl.transferTokens(tokenManagementRequest);
			e.printStackTrace();
		}

	}

	/* (non-Javadoc)
	 * @see com.ireslab.sendx.electra.service.CommonService#transferFeeToMasterAccount(com.ireslab.sendx.electra.model.TokenTransferRequest)
	 */
	@Override
	public void transferFeeToMasterAccount(TokenTransferRequest tokenTransferRequest) {
		TokenLifecycleManagementRequest tokenManagementRequest = new TokenLifecycleManagementRequest();
		tokenManagementRequest.setUserCorrelationId(tokenTransferRequest.getSenderCorrelationId());
		tokenManagementRequest.setBeneficiaryCorrelationId(tokenTransferRequest.getReceiverCorrelationId());
		tokenManagementRequest.setNoOfTokens(tokenTransferRequest.getNoOfToken());
		tokenManagementRequest.setTransactionPurpose(tokenTransferRequest.getTransactionPurpose());
		tokenManagementRequest.setIsFee(true);
		LOG.debug("Inside transferFeeToMasterAccount  - NO of token :- " + tokenManagementRequest.getNoOfTokens()
				+ "\n sender correlation Id :- " + tokenManagementRequest.getUserCorrelationId()
				+ "\n Receiver Correlation id :- " + tokenManagementRequest.getBeneficiaryCorrelationId());
		tokenManagementServiceImpl.transferFeeToMasterAccount(tokenManagementRequest);

	}

	/* (non-Javadoc)
	 * @see com.ireslab.sendx.electra.service.CommonService#getCountryList(java.lang.String)
	 */
	@Override
	public CountryListResponse getCountryList(String clientCorrelationId) {

		CountryListResponse countryListResponse = new CountryListResponse();
		List<CountryDetails> countryDetailList = new ArrayList<>();
		LOG.info("Finding subscription plan avilabe details and county list for clientID :" + clientCorrelationId);
		Client client = clientRepository.findByClientCorrelationId(clientCorrelationId);
		List<Country> countryList = (List<Country>) countryRepo.findAll();

		for (Country country : countryList) {
			CountryDetails countryDetails = new CountryDetails();
			countryDetails.setCountryDialCode(country.getCountryDialCode());
			countryDetails.setCountryId(country.getCountryId());
			countryDetails.setCountryName(country.getCountryName());
			countryDetails.setIso4217CurrencyAlphabeticCode(country.getIso4217CurrencyAlphabeticCode());
			countryDetails.setIso4217CurrencyName(country.getIso4217CurrencyName());
			countryDetailList.add(countryDetails);

		}
		Integer remainingInvitation = 0;
		Integer subscriptionPlan = 0;
		Integer subscribedUser = 0;
		if (client.getClientSubscriptionId() != null) {
			remainingInvitation = client.getClientSubscriptionId().getAvailableUsers();
			subscriptionPlan = client.getClientSubscriptionId().getSubscriptionPlanId().getSupportedUsers();
			// Math.abs(-1) will convert the negative number 1 to positive 1
			subscribedUser = subscriptionPlan - remainingInvitation;
		}

		countryListResponse.setCountryDetails(countryDetailList);
		countryListResponse.setSubscribedUser(Math.abs(subscribedUser));
		countryListResponse.setRemainingInvitation(remainingInvitation);
		countryListResponse.setSubscriptionPlan(subscriptionPlan);
		countryListResponse.setStatus(HttpStatus.OK.value());
		countryListResponse.setMessage("Success");
		countryListResponse.setCode(100);

		return countryListResponse;
	}

	/* (non-Javadoc)
	 * @see com.ireslab.sendx.electra.service.CommonService#saveClientInvitationDetails(com.ireslab.sendx.electra.model.ClentAgentInvitationRequest)
	 */
	@Override
	public ClentAgentInvitationResponse saveClientInvitationDetails(
			ClentAgentInvitationRequest clentAgentInvitationRequest) {
		ClentAgentInvitationResponse clentAgentInvitationResponse = new ClentAgentInvitationResponse();
		if (clentAgentInvitationRequest.getSend().equals("MAIL")) {
			clentAgentInvitationRequest.getClientAgentInvitationList().forEach((clientAgentInvitationDto) -> {

				ClientAgentInvitation clientagentInvitation = clientAgentInvitationRepo
						.findByEmailAddress(clientAgentInvitationDto.getEmailAddress());
				if (clientagentInvitation != null) {
					
					Client client = clientRepository.findByClientCorrelationId(clentAgentInvitationRequest.getClientId());

					clientagentInvitation.setClientId(client.getClientId());
					clientagentInvitation.setRegister(false);
					clientagentInvitation.setInvitationDate(new Date());
					clientagentInvitation.setSubscriptionDurations(clentAgentInvitationRequest.getSubscriptionDurations());
					Date subscriptionDurations = CommonUtils.getSubscriptionDurations(clentAgentInvitationRequest.getSubscriptionDurations(),new Date());
					clientagentInvitation.setSubscriptionExpireOn(subscriptionDurations);
					clientAgentInvitationService.saveInvitationDetail(clientagentInvitation);

				} else {

					Client client = clientRepository.findByClientCorrelationId(clentAgentInvitationRequest.getClientId());
					ClientAgentInvitation agentInvitation = new ClientAgentInvitation();
					agentInvitation.setClientId(client.getClientId());
					if (clientAgentInvitationDto.getEmailAddress() != null) {
						agentInvitation.setEmailAddress(clientAgentInvitationDto.getEmailAddress());
					}
					if (clientAgentInvitationDto.getMobileNumber() != null) {
						agentInvitation.setMobileNumber(clientAgentInvitationDto.getMobileNumber());
					}
					agentInvitation.setInvitationDate(new Date());
					agentInvitation.setSubscriptionDurations(clentAgentInvitationRequest.getSubscriptionDurations());
					Date subscriptionDurations = CommonUtils.getSubscriptionDurations(clentAgentInvitationRequest.getSubscriptionDurations(),new Date());
					agentInvitation.setSubscriptionExpireOn(subscriptionDurations);
					clientAgentInvitationService.saveInvitationDetail(agentInvitation);
				}

			});

		}

		if (clentAgentInvitationRequest.getSend().equals("SMS")) {

			clentAgentInvitationRequest.getClientAgentInvitationList().forEach((clientAgentInvitationDto) -> {
				Client client = clientRepository.findByClientCorrelationId(clentAgentInvitationRequest.getClientId());
				
				String mobileString = clientAgentInvitationDto.getMobileNumber();
				// Change to save mobile no to table.
				String[] split = mobileString.split("-");
				String saveMobileNo = split[1];
				String sendSmsMobileNo = split[0] + split[1];

				String region = split[0].replace("+", "");

				
				if (PhoneNumberValidationUtils.validateMobNoWithCountryAbbv(saveMobileNo,
						PhoneNumberUtil.getInstance().getRegionCodeForCountryCode(Integer.parseInt(region)))) {
					String message = String.format(smsService.getAgentInvitationAppMessage(),
							smsService.getElectraRemittanceAppUrl(), client.getCompanyCode());
					if (smsService.sendInvitationMessage(sendSmsMobileNo, message)) {

						ClientAgentInvitation clientagentInvitation = clientAgentInvitationRepo
								.findByMobileNumber(saveMobileNo);
						if (clientagentInvitation != null) {


							clientagentInvitation.setClientId(client.getClientId());
							clientagentInvitation.setRegister(false);
							
							clientagentInvitation.setInvitationDate(new Date());
							clientagentInvitation.setSubscriptionDurations(clentAgentInvitationRequest.getSubscriptionDurations());
							Date subscriptionDurations = CommonUtils.getSubscriptionDurations(clentAgentInvitationRequest.getSubscriptionDurations(),new Date());
							clientagentInvitation.setSubscriptionExpireOn(subscriptionDurations);
							clientAgentInvitationService.saveInvitationDetail(clientagentInvitation);

						} else {

							ClientAgentInvitation agentInvitation = new ClientAgentInvitation();
							agentInvitation.setClientId(client.getClientId());
							if (clientAgentInvitationDto.getEmailAddress() != null) {
								agentInvitation.setEmailAddress(clientAgentInvitationDto.getEmailAddress());
							}
							if (clientAgentInvitationDto.getMobileNumber() != null) {
								
								agentInvitation.setMobileNumber(saveMobileNo);
							}
							agentInvitation.setInvitationDate(new Date());
							agentInvitation.setSubscriptionDurations(clentAgentInvitationRequest.getSubscriptionDurations());
							Date subscriptionDurations = CommonUtils.getSubscriptionDurations(clentAgentInvitationRequest.getSubscriptionDurations(),new Date());
							agentInvitation.setSubscriptionExpireOn(subscriptionDurations);
							clientAgentInvitationService.saveInvitationDetail(agentInvitation);
						}

					}
				}

			});
		}

		clentAgentInvitationResponse
				.setClientAgentInvitationList(clentAgentInvitationRequest.getClientAgentInvitationList());
		clentAgentInvitationResponse.setCode(200);
		return clentAgentInvitationResponse;
	}

	/* (non-Javadoc)
	 * @see com.ireslab.sendx.electra.service.CommonService#getClientDetailsByCorrelationId(com.ireslab.sendx.electra.model.ClientInfoRequest)
	 */
	@Override
	public ClientInfoResponse getClientDetailsByCorrelationId(ClientInfoRequest clientInfoRequest) {
		ClientInfoResponse clientInfoResponse = new ClientInfoResponse();

		Client findBycompanyCode = clientRepository.findBycompanyCode(clientInfoRequest.getCompanyCode());
		clientInfoResponse.setClientCorrelationId(findBycompanyCode.getClientCorrelationId());

		return clientInfoResponse;
	}

	/* (non-Javadoc)
	 * @see com.ireslab.sendx.electra.service.CommonService#getProductList(com.ireslab.sendx.electra.model.ProductRequest)
	 */
	@Override
	public ProductResponse getProductList(ProductRequest productRequest) {
		
		ProductResponse productResponse = new ProductResponse();

		MerchantDto merchantDto = new MerchantDto();
		List<ProductDto> productlist = new ArrayList<>();
		
		
		Client client = null;

		client = clientRepository.findByContactNumber1(productRequest.getMobileNumber());
		if(client == null) {
			client = clientRepository.findByClientCorrelationId(productRequest.getClientCorrelationId());
		}
		
		

		if (client != null) {
			List<Product> productList = productRepo.findByClientIdCust(client.getClientId());

			for (Product product : productList) {
				ProductDto productDto = new ProductDto();
								
				
				//----------------- Product list changed after product configuraton ---------------
				//------------ Product Configuration ---
				productDto.setItemCode(product.getItemCode());
				productDto.setInvoiceType(product.getInvoiceType());	
				productDto.setGstInclusive(product.isGstInclusive());	
				productDto.setItemNameOrDesc(product.getItemNameOrDesc());	
				productDto.setItemTypeOrChapter(product.getItemTypeOrChapter());
				productDto.setInvoiceType(product.getInvoiceType());	
				productDto.setDiscountPercentage(product.getDiscountPercentage());
				productDto.setItemPrice(product.getItemPrice());
				productDto.setAvailableQuantity(product.getAvailableQuantity());
				productDto.setSubTotal(product.getSubTotal());
				productDto.setDiscount(product.getDiscount());
				productDto.setTotalTaxInclusive(product.getTotalTaxInclusive());
				productDto.setTotal(product.getTotal());
				productDto.setCgst(product.getCgst());
				productDto.setSgstUtgst(product.getSgst_utgst());
				productDto.setIgst(product.getIgst());
				productDto.setCustomerNotes(product.getCustomerNotes());
				productDto.setTermsAndConditions(product.getTermsAndConditions());
				productDto.setInterState(product.isInterState());
				//----------------------------------------
				productDto.setProductCode(product.getProductCode());
				productDto.setProductId(product.getProductId());
				
				productDto.setPaymentTerms(paymentRepository.findPaymentTermByPaymentTermId(Integer.parseInt(product.getPaymentTerms())).getTermValue()+"");
				
				
				
				productlist.add(productDto);
			}

			// merchantDto.setCountryDialCode("+91");
			merchantDto.setMobileNumber(client.getContactNumber1());
			merchantDto.setEmailAddress(client.getEmailAddress());
			merchantDto.setFirstName(client.getClientName());
			merchantDto.setLastName(client.getClientName());
			merchantDto.setCompanyCode(client.getCompanyCode());
			merchantDto.setGstn(client.getGstNumber());
			
			
		}

		productResponse.setProductDetails(productlist);
		productResponse.setMerchantDetails(merchantDto);
		if (productlist.size() > 0) {
			productResponse.setCode(100);
			productResponse.setStatus(HttpStatus.OK.value());
			productResponse.setMessage("Success");
		} else {
			productResponse.setCode(101);
			productResponse.setStatus(HttpStatus.OK.value());
			
			productResponse.setMessage(messagesProperties.productListNotAvailable);
		}
		return productResponse;
	}
	
	/* (non-Javadoc)
	 * @see com.ireslab.sendx.electra.service.CommonService#getProductListForConsole(com.ireslab.sendx.electra.model.ProductRequest)
	 */
	@Override
	public ProductResponse getProductListForConsole(ProductRequest productRequest) {
		
		ProductResponse productResponse = new ProductResponse();

		MerchantDto merchantDto = new MerchantDto();
		List<ProductDto> productlist = new ArrayList<>();
		
		
		Client client = null;

		
			client = clientRepository.findByClientCorrelationId(productRequest.getClientCorrelationId());
		

		if (client != null) {
			List<Product> productList = productRepo.findByClientId(client.getClientId());

			for (Product product : productList) {
				ProductDto productDto = new ProductDto();
				productDto.setProductCode(product.getProductCode());
				productDto.setProductName(product.getProductName());
				productDto.setProductCost(String.format("%.2f", product.getRate()));
				productDto.setAvailableItem(String.format("%.2f", product.getQty()));
				productDto.setProductUnit(product.getUnit());
				productDto.setProductUnitRange(String.format("%.2f", new Double(product.getUnitRange())));
				productDto.setProductDescription(product.getProductDescription());
				productDto.setProductGroup(product.getProductGroup());
				productDto.setProductId(product.getProductId());
				productDto.setTax(product.getTax());
				productlist.add(productDto);
			}

			// merchantDto.setCountryDialCode("+91");
			merchantDto.setMobileNumber(client.getContactNumber1());
			merchantDto.setEmailAddress(client.getEmailAddress());
			merchantDto.setFirstName(client.getClientName());
			merchantDto.setLastName(client.getClientName());
			merchantDto.setCompanyCode(client.getCompanyCode());

		}

		productResponse.setProductDetails(productlist);
		productResponse.setMerchantDetails(merchantDto);
		if (productlist.size() > 0) {
			productResponse.setCode(100);
			productResponse.setStatus(HttpStatus.OK.value());
			productResponse.setMessage("Success");
		} else {
			productResponse.setCode(101);
			productResponse.setStatus(HttpStatus.OK.value());
			
			productResponse.setMessage(messagesProperties.productListNotAvailable);
		}
		return productResponse;
	}

	/* (non-Javadoc)
	 * @see com.ireslab.sendx.electra.service.CommonService#makePayment(com.ireslab.sendx.electra.model.PaymentRequest)
	 */
	@Override
	public PaymentResponse makePayment(PaymentRequest paymentRequest) {
		PaymentResponse transferPaymentToClientAccountResponse = null;
		if (paymentRequest.getTokenTransferRequest() != null) {
			transferPaymentToClientAccountResponse = commonService
					.transferPaymentToClientAccount(paymentRequest.getTokenTransferRequest());
		}

		if (transferPaymentToClientAccountResponse.getErrors().size() > 0) {
			transferPaymentToClientAccountResponse.setCode(101);
			transferPaymentToClientAccountResponse
					.setMessage(transferPaymentToClientAccountResponse.getErrors().get(0).getMessage());
		}
		return transferPaymentToClientAccountResponse;
	}

	/* (non-Javadoc)
	 * @see com.ireslab.sendx.electra.service.CommonService#getclientInformation(com.ireslab.sendx.electra.model.ClientInfoRequest)
	 */
	@Override
	public ClientInfoResponse getclientInformation(ClientInfoRequest clientInfoRequest) {
		ClientInfoResponse clientInfoResponse = new ClientInfoResponse();
		
		Client client = clientRepository.findByEmail(clientInfoRequest.getEmail());

		if (client != null) {

			clientInfoResponse.setClientCorrelationId(client.getClientCorrelationId());
			clientInfoResponse.setCompanyCode(client.getCompanyCode());
			clientInfoResponse.setEmail(client.getEmailAddress());
			clientInfoResponse.setClientName(client.getClientName());
			clientInfoResponse.setCode(100);
			return clientInfoResponse;
		}
		clientInfoResponse.setCode(101);
		clientInfoResponse.setMessage("Client not found with given information");
		return clientInfoResponse;
	}

	/* (non-Javadoc)
	 * @see com.ireslab.sendx.electra.service.CommonService#savePurchasedProduct(com.ireslab.sendx.electra.model.PaymentRequest)
	 */
	@Override
	public PaymentResponse savePurchasedProduct(PaymentRequest paymentRequest) {
		PaymentResponse paymentResponse = new PaymentResponse();

		List<ProductDto> purchaseProductList = paymentRequest.getProductList();
		PurchaserDto purchaserDto = paymentRequest.getPurchaserDetails();

		Integer purchaserId = 0;
		
		boolean isClient = false;
		ClientUser clientUser = clientUserRepo.findByUserCorrelationId(purchaserDto.getCorrelationId());
		if (clientUser == null) {
			Client client = clientRepository.findByClientCorrelationId(purchaserDto.getCorrelationId());
			purchaserId = client.getClientId();
			isClient = true;
		} else {
			purchaserId = clientUser.getUserId();
		}

		for (ProductDto productDto : purchaseProductList) {
			ProductPurchaseHistory purchaseHistory = new ProductPurchaseHistory();
			Product product = productRepo.findByProductCode(productDto.getProductCode());

			purchaseHistory.setProductId(product.getProductId());
			purchaseHistory.setQty(new Double(productDto.getPurchasedQty()));
			purchaseHistory.setTotalPrice(new Double(productDto.getTotalItemPrice()));
			
			purchaseHistory.setClient(isClient);
			purchaseHistory.setPurchaseDate(new Date());
			purchaseHistory.setPurchaserId(purchaserId);
			purchaseHistory.setInvoiceNumber(paymentRequest.getInvoiceNumber());
			purchasedRepo.save(purchaseHistory);

			

		}

		paymentResponse.setCode(100);
		paymentResponse.setMessage("Success");
		paymentResponse.setStatus(HttpStatus.OK.value());

		return paymentResponse;
	}

	/* (non-Javadoc)
	 * @see com.ireslab.sendx.electra.service.CommonService#transferPaymentToClientAccount(com.ireslab.sendx.electra.model.TokenTransferRequest)
	 */
	@Override
	public PaymentResponse transferPaymentToClientAccount(TokenTransferRequest tokenTransferRequest) {
		PaymentResponse paymentResponse = new PaymentResponse();
		LOG.info("Transfering to client account.");
		try {
			LOG.info("tokenTransferRequest JSON :" + objectWriter.writeValueAsString(tokenTransferRequest));
		} catch (JsonProcessingException e1) {
			
			e1.printStackTrace();
		}
		StellarTransactionConfigDto stellarConfig = new StellarTransactionConfigDto();
		
		Client client = clientRepository.findByClientCorrelationId(tokenTransferRequest.getReceiverCorrelationId());
		if(client == null) {
			ClientUser userInvoice = clientUserRepo.findByUserCorrelationId(tokenTransferRequest.getReceiverCorrelationId());
			client = new Client();
			client.setBaseTxnAccountPublicKey(userInvoice.getAccountPublicKey());
			client.setBaseTxnAccountSecretKey(userInvoice.getAccountSecretKey());
			client.setCountryDialCode(userInvoice.getCountryDialCode());
		}
		

		ClientUser clientUser = clientUserRepo.findByUserCorrelationId(tokenTransferRequest.getSenderCorrelationId());

		if (clientUser == null) {
			Client clientInvoice = clientRepository.findByClientCorrelationId(tokenTransferRequest.getSenderCorrelationId());
			clientUser = new ClientUser();
			clientUser.setAccountPublicKey(clientInvoice.getBaseTxnAccountPublicKey());
			clientUser.setAccountSecretKey(clientInvoice.getBaseTxnAccountSecretKey());
			paymentResponse.setClient(true);

		}

		// Sender Account Details
		SenderAccount senderAccount = new SenderAccount();
		senderAccount.setPublicKey(clientUser.getAccountPublicKey());
		senderAccount.setSecretSeed(clientUser.getAccountSecretKey());

		// Receiver Account Details
		ReceiverAccount receiverAccount = new ReceiverAccount();
		receiverAccount.setPublicKey(client.getBaseTxnAccountPublicKey());
		receiverAccount.setSecretSeed(client.getBaseTxnAccountSecretKey());

		// Issuing Account Details
		List<MasterAccounts> masterAccountList = (List<MasterAccounts>) masterAccountRepository.findAll();
		MasterAccounts masterAccount = masterAccountList.get(0);
		IssuingAccount issuingAccount = new IssuingAccount();
		issuingAccount.setPublicKey(masterAccount.getIssuingAccountPublicKey());
		issuingAccount.setSecretSeed(masterAccount.getIssuingAccountSecretKey());

		stellarConfig.setSenderAccount(senderAccount);
		stellarConfig.setReceiverAccount(receiverAccount);
		stellarConfig.setNoOfTokens(tokenTransferRequest.getNoOfToken());
		Country country = countryRepo.findCountryByCountryDialCode(client.getCountryDialCode());
		// Token to be send.
		stellarConfig.setAssetCode(country.getIso4217CurrencyAlphabeticCode());
		stellarConfig.setIssuingAccount(issuingAccount);
		stellarConfig.setIsTestNetwork(electraConfig.isTestnetAccount);
		stellarConfig.setAssetLimit("50000");

		TokenLifecycleManagementRequest tokenManagementRequest = new TokenLifecycleManagementRequest();
		tokenManagementRequest.setUserCorrelationId(tokenTransferRequest.getSenderCorrelationId());
		tokenManagementRequest.setBeneficiaryCorrelationId(tokenTransferRequest.getReceiverCorrelationId());
		tokenManagementRequest.setNoOfTokens(tokenTransferRequest.getNoOfToken());
		tokenManagementRequest.setIsFee(true);
		LOG.debug("transfer payment To client account  - NO of token :- " + tokenManagementRequest.getNoOfTokens()
				+ "\n sender correlation Id :- " + tokenManagementRequest.getUserCorrelationId()
				+ "\n Receiver Correlation id :- " + tokenManagementRequest.getBeneficiaryCorrelationId());
		TokenLifecycleManagementResponse tokenLifecycleManagementResponse = tokenManagementServiceImpl
				.transferTokens(tokenManagementRequest);
		paymentResponse.setTransactionDto(tokenLifecycleManagementResponse.getTransactionDto());
		paymentResponse.setCode(tokenLifecycleManagementResponse.getCode());
		paymentResponse.setMessage(tokenLifecycleManagementResponse.getMessage());
		paymentResponse.setStatus(tokenLifecycleManagementResponse.getStatus());

		if (tokenLifecycleManagementResponse.getCode() == 200) {
			paymentResponse.setCode(100);
			paymentResponse.setMessage("Payment has been completed successfully.");
		} else {
			paymentResponse.setCode(101);
			paymentResponse.setMessage("Insufficient account balance");
		}
		
		return paymentResponse;
	}

	/* (non-Javadoc)
	 * @see com.ireslab.sendx.electra.service.CommonService#checkProductAvailability(com.ireslab.sendx.electra.model.ProductAvailabilityRequest)
	 */
	@Override
	public ProductAvailabilityResponse checkProductAvailability(ProductAvailabilityRequest productAvailabilityRequest) {
		ProductAvailabilityResponse productAvailabilityResponse = new ProductAvailabilityResponse();
		Product findByProductCode = productRepo.findByProductCode(productAvailabilityRequest.getProductCode());
		if (Double.parseDouble(productAvailabilityRequest.getOrderQuantity()) <= findByProductCode.getQty()) {
			productAvailabilityResponse.setProductAvailabilityStatus(true);
			return productAvailabilityResponse;
		}
		productAvailabilityResponse.setProductAvailabilityStatus(false);
		return productAvailabilityResponse;
	}

	

	/**
	 * @param username
	 * @param password
	 * @return
	 */
	private HttpHeaders createHeaders(String username, String password) {
		return new HttpHeaders() {

			{
				String auth = username + ":" + password;
				byte[] encodedAuth = Base64.encodeBase64(auth.getBytes());
				String authHeader = "Basic " + new String(encodedAuth);
				set("Authorization", authHeader);
				LOG.debug("Header for trulioo : " + authHeader);
			}
		};
	}

	/* (non-Javadoc)
	 * @see com.ireslab.sendx.electra.service.CommonService#truliooEkycEkyb(com.ireslab.sendx.electra.dto.VerifyRequest, java.lang.String, java.lang.String)
	 */
	@Override
	public VerifyResponse truliooEkycEkyb(VerifyRequest verifyRequest,String username, String password) {
		VerifyResponse verifyResponse = new VerifyResponse();

		String verifyRequestJson = new Gson().toJson(verifyRequest);

		

		String url = "https://api.globaldatacompany.com/verifications/v1/verify/";
		
		HttpHeaders headers = createHeaders(username, password);
		headers.setContentType(MediaType.APPLICATION_JSON);
		LOG.debug("Header :----" + headers);
		HttpEntity<String> request = new HttpEntity<String>(verifyRequestJson, headers);
		
		ResponseEntity<String> response = new RestTemplate().exchange(url, HttpMethod.POST, request, String.class);

		verifyResponse = new Gson().fromJson(response.getBody(), VerifyResponse.class);
		LOG.debug("Verify Response : " + response.getBody());
		LOG.debug("Response : Status code value -" + response.getStatusCodeValue() + "\n Status Code - "
				+ response.getStatusCode());

		return verifyResponse;
	}

	/* (non-Javadoc)
	 * @see com.ireslab.sendx.electra.service.CommonService#saveBankDetails(com.ireslab.sendx.electra.model.BankDetailsRequest)
	 */
	@Override
	public BankDetailResponse saveBankDetails(BankDetailsRequest bankDetailsRequest) {
		
		int clientId;
		boolean isclient = true;
		Client client = clientRepository.findByClientCorrelationId(bankDetailsRequest.getCorrelationId());
		ClientUser clientUser = clientUserRepo.findByUserCorrelationId(bankDetailsRequest.getCorrelationId());
		if(client == null) {
			clientUser = clientUserRepo.findByUserCorrelationId(bankDetailsRequest.getCorrelationId());
			clientId = clientUser.getUserId();
			isclient = false;
		}
		else {
			clientId = client.getClientId();
		}

		BankDetailResponse bankDetailResponse = new BankDetailResponse();
		
		BankDetails bankDetails = bankDetailsRepo.findByClientIdCust(clientId,isclient);
		if(bankDetails == null) {
			bankDetails = new BankDetails();
		}
		
			
		//BankDetails bankDetails = new BankDetails();
		bankDetails.setAccountNumber(bankDetailsRequest.getAccountNumber());
		bankDetails.setBankName(bankDetailsRequest.getBankName());
		bankDetails.setIfscCode(bankDetailsRequest.getIfscCode());
		bankDetails.setSwiftCode(bankDetailsRequest.getSwiftCode());
		bankDetails.setClientId(clientId);
		bankDetails.setClient(isclient);
		bankDetails.setAccountType(bankDetailsRequest.getAccountType());
		bankDetailsRepo.save(bankDetails);
		
		bankDetailResponse.setCode(100);
		bankDetailResponse.setStatus(HttpStatus.OK.value());
		bankDetailResponse.setMessage("Success");

		return bankDetailResponse;
	}
	
	/* (non-Javadoc)
	 * @see com.ireslab.sendx.electra.service.CommonService#getBankDetailsByClientEmail(com.ireslab.sendx.electra.model.BankDetailsRequest)
	 */
	@Override
	public BankDetailResponse getBankDetailsByClientEmail(BankDetailsRequest bankDetailsRequest) {
		
		BankDetailResponse bankDetailResponse = new BankDetailResponse();
		Client client = clientRepository.findByClientCorrelationId(bankDetailsRequest.getCorrelationId());
		int clientId;
		boolean isclient = true;
		ClientUser clientUser = clientUserRepo.findByUserCorrelationId(bankDetailsRequest.getCorrelationId());
		if(client == null) {
			clientUser = clientUserRepo.findByUserCorrelationId(bankDetailsRequest.getCorrelationId());
			clientId = clientUser.getUserId();
			isclient = false;
		}
		else {
			clientId = client.getClientId();
		}
		
		BankDetails bankDetails = bankDetailsRepo.findByClientIdCust(clientId,isclient);
			if(bankDetails != null) {
				
			
			
				BankDetailDto bankDetailDto = new BankDetailDto();
				bankDetailDto.setAccountNumber(bankDetails.getAccountNumber());
				bankDetailDto.setBankName(bankDetails.getBankName());
				bankDetailDto.setIfscCode(bankDetails.getIfscCode());
				bankDetailDto.setSwiftCode(bankDetails.getSwiftCode());
				bankDetailDto.setAccountType(bankDetails.getAccountType());
				
				
				bankDetailResponse.setBankDetailDto(bankDetailDto);
				bankDetailResponse.setCode(100);
				bankDetailResponse.setStatus(HttpStatus.OK.value());
				bankDetailResponse.setMessage("Success");
				return bankDetailResponse;
			}
			bankDetailResponse.setCode(101);
			bankDetailResponse.setStatus(HttpStatus.OK.value());
			bankDetailResponse.setMessage("Bank Details not found");
		
		
		
		return bankDetailResponse;
	}

	/* (non-Javadoc)
	 * @see com.ireslab.sendx.electra.service.CommonService#getProductGroupList()
	 */
	@Override
	public ProductGroupResponse getProductGroupList() {
		ProductGroupResponse productGroupResponse = new ProductGroupResponse();
		List<ProductGroup> productGroups = null;
		try {
			productGroups = (List<ProductGroup>) productGroupRepository.findAll();
		} catch (ApiException e) {
			e.printStackTrace();
		}

		if (productGroups == null) {
			productGroupResponse.setCode(ApplicationStatusCodes.INVALID_REQUEST);
			productGroupResponse.setMessage("FAILURE");
		} else {
			java.lang.reflect.Type productGroupList = new TypeToken<List<ProductGroupDto>>() {
			}.getType();
			List<ProductGroupDto> groupListDto = mapper.map(productGroups, productGroupList);

			productGroupResponse.setCode(ApplicationStatusCodes.SUCCESS);
			productGroupResponse.setMessage("SUCCESS");
			productGroupResponse.setProductGroups(groupListDto);
		}
		return productGroupResponse;
	}

	/* (non-Javadoc)
	 * @see com.ireslab.sendx.electra.service.CommonService#saveProduct(com.ireslab.sendx.electra.model.SaveProductRequest)
	 */
	@Override
	public SaveProductResponse saveProduct(SaveProductRequest productRequest) {
		SaveProductResponse productResponse = new SaveProductResponse();

		// Check Product Code
		String productCode = productRequest.getProductCode();
		Product checkProductCode = productRepo.findByProductCode(productCode);
		String response = null;
		if (checkProductCode != null) {
			response = checkProductCode.getProductCode();
		}

		if (response != null && response.length() > 0) {
			productResponse.setMessage("Product code already exists. Please enter a unique product code.");
			productResponse.setSuccess("failure");
			productResponse.setCode(AppConstants.RESPONSE_FAILUR_CODE);
		} else {
			Product product = new Product();
			Client client = getClientDetailsByCorrelationId(productRequest.getClientCorrelationId());
			product.setClientId(client.getClientId());
			product.setProductCode(productCode);
			product.setProductName(productRequest.getProductName());
			product.setProductDescription(productRequest.getProductDescription());
			product.setRate(Double.valueOf(productRequest.getProductCost()));
			product.setQty(Double.valueOf(productRequest.getAvailableItem()));
			product.setUnit(productRequest.getProductUnit());
			product.setUnitRange(productRequest.getProductRange());
			product.setCreatedDate(new Date());
			
			product.setTax(productRequest.getTax());
			product.setManufacturingDate(new Date());
			product.setExpiryDate(new Date());

			java.util.Date utilDate = new java.util.Date();
			java.sql.Timestamp timestamp = new java.sql.Timestamp(utilDate.getTime());
			product.setModifiedDate(timestamp);

			product.setProductGroup(productRequest.getProductGroup());

			try {
				productRepo.save(product);
			} catch (Exception e) {
				e.printStackTrace();
				productResponse.setMessage("Product not saved.");
				productResponse.setSuccess("failure");
				productResponse.setCode(AppConstants.RESPONSE_FAILUR_CODE);
			}

			productResponse.setMessage("Product saved sucessfully.");
			productResponse.setSuccess("success");
			productResponse.setCode(AppConstants.RESPONSE_SUCCESS_CODE);
		}

		return productResponse;
	}

	/* (non-Javadoc)
	 * @see com.ireslab.sendx.electra.service.CommonService#editProduct(com.ireslab.sendx.electra.model.SaveProductRequest)
	 */
	@Override
	public SaveProductResponse editProduct(SaveProductRequest productRequest) {
		SaveProductResponse productResponse = new SaveProductResponse();
		
		int productId = productRequest.getProductId();
		Product product = productRepo.findByProductId(productId);
		Client client = getClientDetailsByCorrelationId(productRequest.getClientCorrelationId());
		product.setClientId(client.getClientId());
		product.setProductCode(productRequest.getProductCode());
		product.setProductName(productRequest.getProductName());
		product.setProductDescription(productRequest.getProductDescription());
		product.setRate(Double.valueOf(productRequest.getProductCost()));
		product.setQty(Double.valueOf(productRequest.getAvailableItem()));
		product.setProductGroup(productRequest.getProductGroup());
		product.setUnit(productRequest.getProductUnit());
		product.setUnitRange(productRequest.getProductRange());
		product.setTax(Double.valueOf(productRequest.getTax()));
		product.setManufacturingDate(new Date());
		product.setExpiryDate(new Date());

		java.util.Date utilDate = new java.util.Date();
		java.sql.Timestamp timestamp = new java.sql.Timestamp(utilDate.getTime());
		product.setModifiedDate(timestamp);

		product.setProductGroup(productRequest.getProductGroup());

		try {
			productRepo.save(product);
		} catch (ApiException e) {
			e.printStackTrace();
			productResponse.setMessage("Product not edited.");
			productResponse.setSuccess("failure");
			productResponse.setCode(AppConstants.RESPONSE_FAILUR_CODE);
		}

		productResponse.setMessage("Product updated sucessfully.");
		productResponse.setSuccess("success");
		productResponse.setCode(AppConstants.RESPONSE_SUCCESS_CODE);
		

		return productResponse;
	}

	/* (non-Javadoc)
	 * @see com.ireslab.sendx.electra.service.CommonService#deleteProduct(java.lang.String)
	 */
	@Override
	public SaveProductResponse deleteProduct(String productCode) {
		SaveProductResponse productResponse = new SaveProductResponse();
		Product product = productRepo.findByProductCode(productCode);
		try {
			productRepo.delete(product);
			productResponse.setMessage("Product deleted sucessfully.");
			productResponse.setSuccess("success");
			productResponse.setCode(AppConstants.RESPONSE_SUCCESS_CODE);
		} catch (ApiException e) {
			e.printStackTrace();
			productResponse.setMessage("Product not deleted sucessfully.");
			productResponse.setSuccess("success");
			productResponse.setCode(AppConstants.RESPONSE_SUCCESS_CODE);
		}

		return productResponse;
	}

	/* (non-Javadoc)
	 * @see com.ireslab.sendx.electra.service.CommonService#getClientOrUserDetailsByUniqueCode(java.lang.String)
	 */
	@Override
	public UserProfileResponse getClientOrUserDetailsByUniqueCode(String uniqueCode) {
		UserProfileResponse userProfileResponse =null;
		ClientUser clientUser = clientUserRepo.findByUniqueCode(uniqueCode);
		Client client = clientRepository.findByuniqueCode(uniqueCode);
		UserProfile userProfile =null;
		if(clientUser!=null) {
			userProfileResponse = userProfileMgmtService.getUserProfile(clientUser.getClient().getClientCorrelationId(), clientUser.getUserCorrelationId());
			Country country = countryRepo.findCountryByCountryDialCode(clientUser.getCountryDialCode());
			userProfile =userProfileResponse.getUserProfile();
			userProfile.setMobileNumber(CommonUtils.splitDialCodeAndMobileNumber(clientUser.getMobileNumber(), clientUser.getCountryDialCode()));
			
			
			userProfile.setEmailAddress(clientUser.getEmailAddress());
			userProfile.setCountryDialCode(clientUser.getCountryDialCode());
			userProfile.setUserCorrelationId(clientUser.getUserCorrelationId());
			
			userProfile.setCountryName(country.getCountryName());
			userProfile.setClientCorrelationId(clientUser.getClient().getClientCorrelationId());
			userProfile.setUniqueCode(clientUser.getUniqueCode());
			userProfile.setIsClient(false);
			userProfile.setFirstName(clientUser.getFirstName());
			userProfile.setLastName(clientUser.getLastName());
			userProfile.setStatus(clientUser.getStatus());
			userProfile.setGcmRegisterKey(clientUser.getGcmRegistaryKey());
			userProfile.setFirebaseServiceKey(clientUser.getFirebaseServiceKey());
			userProfile.setResidentialAddress(clientUser.getResidentialAddress());
			userProfile.setIso4217CurrencyAlphabeticCode(country.getIso4217CurrencyAlphabeticCode());
			
			
			LOG.info("searching user uniqueCode :"+uniqueCode+" json : "+userProfile);
		}else if(client!=null) {
			
			userProfileResponse = clientProfileMgmtService.getClientProfile(client.getClientCorrelationId());
			userProfile =userProfileResponse.getUserProfile();
			userProfile.setMobileNumber(client.getContactNumber1());
			userProfile.setEmailAddress(client.getEmailAddress());
			userProfile.setCountryDialCode(client.getCountryDialCode());
			userProfile.setUserCorrelationId(client.getClientCorrelationId());
			Country country = countryRepo.findCountryByCountryDialCode(client.getCountryDialCode());
			userProfile.setCountryName(country.getCountryName());
			userProfile.setUniqueCode(client.getUniqueCode());
			userProfile.setIsClient(true);
			userProfile.setFirstName(client.getFirstName());
			userProfile.setLastName(client.getLastName());
			userProfile.setStatus(Enum.valueOf(Status.class, client.getStatus()));
			userProfile.setGcmRegisterKey(client.getGcmRegistaryKey());
			userProfile.setFirebaseServiceKey(client.getFirebaseServiceKey());
			userProfile.setResidentialAddress(client.getResidentialAddress());
			userProfile.setIso4217CurrencyAlphabeticCode(country.getIso4217CurrencyAlphabeticCode());
			LOG.info("searching client uniqueCode :"+uniqueCode+" json : "+userProfile+" isClient :"+userProfile.getIsClient());
			
		}
		
		
		
		return userProfileResponse;
	}

	/* (non-Javadoc)
	 * @see com.ireslab.sendx.electra.service.CommonService#saveNotification(com.ireslab.sendx.electra.model.NotificationRequest)
	 */
	@Override
	public NotificationResponse saveNotification(NotificationRequest notificationRequest) {
		NotificationResponse notificationResponse = new NotificationResponse();
		Notification notification = new Notification();
		NotificationDto notificationDto = notificationRequest.getNotificationDto();
		notification.setCorrelationId(notificationDto.getCorrelationId());
		notification.setEmailAddress(notificationDto.getEmailAddress());
		notification.setMobileNumber(notificationDto.getMobileNumber());
		notification.setNotificationData(notificationDto.getNotificationData());
		notification.setStatus(notificationDto.getStatus());
		notification.setCreatedDate(new Date());
		notification.setInvoice(notificationDto.getInvoice());
		notification.setIsPaymentConfirm(notificationDto.getIsPaymentConfirm());
		notification.setModifiedDate(new Date());
		notification.setIsOffline(notificationDto.getIsOffline());
		
		notificationRepo.save(notification);
		
		notificationResponse.setCode(100);
		notificationResponse.setMessage("Notification saved successfully.");
		
		return notificationResponse;
	}
	
	/* (non-Javadoc)
	 * @see com.ireslab.sendx.electra.service.CommonService#deleteNotification(com.ireslab.sendx.electra.model.NotificationRequest)
	 */
	@Override
	public NotificationResponse deleteNotification(NotificationRequest notificationRequest) {
		NotificationResponse notificationResponse = new NotificationResponse();

		notificationRepo.delete(notificationRequest.getNotificationDto().getNotificationId());
		notificationResponse.setCode(100);
		notificationResponse.setMessage("Notification deleted successfully.");
		return notificationResponse;
	}

	
	/* (non-Javadoc)
	 * @see com.ireslab.sendx.electra.service.CommonService#updateNotification(com.ireslab.sendx.electra.model.SendxElectraRequest)
	 */
	@Override
	public SendxElectraResponse updateNotification(SendxElectraRequest sendxElectraRequest) {
			
		SendxElectraResponse sendxElectraResponse  =new SendxElectraResponse();
		Notification notification = notificationRepo.findByNotificationId(sendxElectraRequest.getNotificationId());
			if(notification!=null) {
				notification.setStatus(sendxElectraRequest.isStatus());
				notification.setModifiedDate(new Date());
				notificationRepo.save(notification);
				sendxElectraResponse.setCode(100);
				sendxElectraResponse.setMessage("Deleted successfully.!");
				return sendxElectraResponse;
			}
			sendxElectraResponse.setCode(101);
			sendxElectraResponse.setMessage("notification not deleted successfully.!");
			
			return sendxElectraResponse;
	}

	/* (non-Javadoc)
	 * @see com.ireslab.sendx.electra.service.CommonService#getAllNotification(java.lang.String)
	 */
	@Override
	public SendxElectraResponse getAllNotification(String correlationId) {
		List<Notification> notificationList = notificationRepo.findAllByCorrelationId(false, correlationId);
		SendxElectraResponse sendxElectraResponse =new SendxElectraResponse();
		
		if(notificationList == null) {
			notificationList = new ArrayList<>();
		}
		java.lang.reflect.Type targetListType = new TypeToken<List<com.ireslab.sendx.electra.dto.Notification>>() {
		}.getType();

        List<com.ireslab.sendx.electra.dto.Notification> notifications= modelMapper.map(notificationList, targetListType);			

		if(notificationList.size()>0 && !notificationList.isEmpty()) {
			
			sendxElectraResponse.setNotificationList(notifications);
			sendxElectraResponse.setCode(100);
			sendxElectraResponse.setMessage("SUCCESS");
			sendxElectraResponse.setStatus(200);
			return sendxElectraResponse;
		}
		sendxElectraResponse.setNotificationList(notifications);
		sendxElectraResponse.setCode(101);
		sendxElectraResponse.setMessage("Notification List is Empty");
		sendxElectraResponse.setStatus(200);
		return sendxElectraResponse;
	}

	/* (non-Javadoc)
	 * @see com.ireslab.sendx.electra.service.CommonService#updateDeviceSpecificParameter(com.ireslab.sendx.electra.model.SendxElectraRequest)
	 */
	@Override
	public SendxElectraResponse updateDeviceSpecificParameter(SendxElectraRequest sendxElectraRequest) {
		
		SendxElectraResponse sendxElectraResponse = new SendxElectraResponse();
		
		Client client = null;
		ClientUser clientUser = null;
		
		clientUser = clientUserRepo.findByUserCorrelationId(sendxElectraRequest.getUserCorrelationId());
		if(clientUser == null) {
			client = clientRepository.findByClientCorrelationId(sendxElectraRequest.getUserCorrelationId());
		}
		
		if(client != null) {
			client.setGcmRegistaryKey(sendxElectraRequest.getGcmRegisterKey());	
			client.setFirebaseServiceKey(sendxElectraRequest.getFirebaseServiceKey());
			clientRepository.save(client);
			sendxElectraResponse.setCode(100);
			sendxElectraResponse.setMessage("gcm key and service key updated successfully in client");
			return sendxElectraResponse;
			
		}
		if(clientUser != null) {
			clientUser.setGcmRegistaryKey(sendxElectraRequest.getGcmRegisterKey());	
			clientUser.setFirebaseServiceKey(sendxElectraRequest.getFirebaseServiceKey());
			clientUserRepo.save(clientUser);
			sendxElectraResponse.setCode(100);
			sendxElectraResponse.setMessage("gcm key and service key updated successfully in client");
			return sendxElectraResponse;
		}
		sendxElectraResponse.setCode(100);
		sendxElectraResponse.setMessage("Record not found for update");
		return sendxElectraResponse;
	}

	/* (non-Javadoc)
	 * @see com.ireslab.sendx.electra.service.CommonService#makeOfflinePayment(com.ireslab.sendx.electra.model.TokenTransferRequest)
	 */
	@Override
	public PaymentResponse makeOfflinePayment(TokenTransferRequest tokenTransferRequest) {
		PaymentResponse paymentResponse = new PaymentResponse();
		
		String senderName;
		String senderCorrelationId;
		String receiverName;
		String receiverCorrelationId;
		String countryDialCode;
		boolean senderIsClient = false;
		boolean receiverIsClient = false;
		
		ClientUser clientUserSender = clientUserRepo.findByUserCorrelationId(tokenTransferRequest.getSenderCorrelationId());
		if(clientUserSender == null) {
			Client clientSender = clientRepository.findByClientCorrelationId(tokenTransferRequest.getSenderCorrelationId());
			senderName = clientSender.getClientName();
			senderCorrelationId = clientSender.getClientCorrelationId();
			senderIsClient = true;
			countryDialCode = clientSender.getCountryDialCode();
		}else {
			senderName = clientUserSender.getFirstName()+" "+clientUserSender.getLastName();
			senderCorrelationId = clientUserSender.getUserCorrelationId();
			countryDialCode = clientUserSender.getCountryDialCode();
		}
		
		Client clientReceiver = clientRepository.findByClientCorrelationId(tokenTransferRequest.getReceiverCorrelationId());
		if(clientReceiver == null) {
			ClientUser clientUserReceiver = clientUserRepo.findByUserCorrelationId(tokenTransferRequest.getReceiverCorrelationId());
			receiverName = clientUserReceiver.getFirstName()+" "+clientUserReceiver.getLastName();
			receiverCorrelationId = clientUserReceiver.getUserCorrelationId();;
		}else {
			receiverName = clientReceiver.getClientName();
			receiverCorrelationId = clientReceiver.getClientCorrelationId();
			receiverIsClient = true;
		}
		
		Country country = countryRepo.findCountryByCountryDialCode(countryDialCode);
		
		
		TransactionDetail transactionDetail = commonService.saveTransactionDetails(TokenOperation.Payment, TokenActivity.TRANSFER_TOKENS,
				"stellarRequest", "stellarResponse",
				senderName, senderIsClient,
				senderCorrelationId,
				receiverName, receiverIsClient,
				receiverCorrelationId, (short) 1, tokenTransferRequest.getNoOfToken(),
				country.getIso4217CurrencyAlphabeticCode(), null, null, new Date(), new Date(), null, true, true);
		
		
		TransactionDto transactionDto = new TransactionDto();
		transactionDto.setAssetCode(transactionDetail.getAssetCode());
		transactionDto.setDestinationAccountName(transactionDetail.getDestinationAccountName());
		transactionDto.setDestinationCorrelationId(transactionDetail.getDestinationCorrelationId());
		transactionDto.setOperation(transactionDetail.getOperation());
		transactionDto.setSourceAccountName(transactionDetail.getSourceAccountName());
		transactionDto.setSourceCorrelationId(transactionDetail.getSourceCorrelationId());
		transactionDto.setStatus(transactionDetail.getStatus());
		transactionDto.setTnxData(transactionDetail.getTnxData());
		transactionDto.setTnxHash(transactionDetail.getTnxHash());
		transactionDto.setTransactionSequenceNo(transactionDetail.getTransactionSequenceNo());
		
		paymentResponse.setTransactionDto(transactionDto);
		
		paymentResponse.setCode(100);
		paymentResponse.setMessage("Ledger update for offline transaction.");
		
		
		return paymentResponse;
	}

	/* (non-Javadoc)
	 * @see com.ireslab.sendx.electra.service.CommonService#updateInvoicedProduct(com.ireslab.sendx.electra.model.PaymentRequest)
	 */
	@Override
	public PaymentResponse updateInvoicedProduct(PaymentRequest paymentRequest) {
		PaymentResponse paymentResponse = new PaymentResponse();

		List<ProductDto> purchaseProductList = paymentRequest.getProductList();

		for (ProductDto productDto : purchaseProductList) {
			
			Product product = productRepo.findByProductCode(productDto.getProductCode());

			product.setAvailableQuantity(new Double(product.getAvailableQuantity()) - new Double(productDto.getPurchasedQty())+"");
			productRepo.save(product);

		}

		paymentResponse.setCode(100);
		paymentResponse.setMessage("Success");
		paymentResponse.setStatus(HttpStatus.OK.value());

		return paymentResponse;
	}
	
	/* (non-Javadoc)
	 * @see com.ireslab.sendx.electra.service.CommonService#getExchangeRate(com.ireslab.sendx.electra.model.ExchangeRequest)
	 */
	@Override
	public ExchangeResponse getExchangeRate(ExchangeRequest exchangeRequest) {
		Exchange exchange = exchangeRepository.findByExchangeTokenAndNativeCurrency(exchangeRequest.getExchangeToken(),exchangeRequest.getNativeCurrency());
		ExchangeDto exchangeDto = new ExchangeDto();
		exchangeDto.setExchangeRate(exchange.getExchangeRate());
		exchangeDto.setTransferFee(exchange.getTransferFee());
		List<ExchangeDto> exchangeDtoList = new ArrayList<>();
		exchangeDtoList.add(exchangeDto);
		ExchangeResponse exchangeResponse = new ExchangeResponse();
		exchangeResponse.setExchangeList(exchangeDtoList);
		exchangeResponse.setCode(100);
		exchangeResponse.setMessage("Success");
		
		return exchangeResponse;
	}

	/* (non-Javadoc)
	 * @see com.ireslab.sendx.electra.service.CommonService#getClientOrUserDetailsByMobileNumber(java.lang.String, java.lang.String)
	 */
	@Override
	public UserProfileResponse getClientOrUserDetailsByMobileNumber(String countryDailCode ,String mobileNumber) {
		UserProfileResponse userProfileResponse =null;
		
		String userMobileNo =countryDailCode.trim()+mobileNumber.trim();
		  ClientUser clientUser = clientUserRepo.findByMobileNumber(userMobileNo.trim());
		  
		  Client client = clientRepository.findBycontactNumber1(mobileNumber);
		
		UserProfile userProfile =null;
		if(clientUser!=null) {
			userProfileResponse = userProfileMgmtService.getUserProfile(clientUser.getClient().getClientCorrelationId(), clientUser.getUserCorrelationId());
			userProfile =userProfileResponse.getUserProfile();
			userProfile.setMobileNumber(clientUser.getMobileNumber());
			userProfile.setEmailAddress(clientUser.getEmailAddress());
			userProfile.setCountryDialCode(clientUser.getCountryDialCode());
			userProfile.setUserCorrelationId(clientUser.getUserCorrelationId());
			Country country = countryRepo.findCountryByCountryDialCode(clientUser.getCountryDialCode());
			userProfile.setCountryName(country.getCountryName());
			userProfile.setClientCorrelationId(clientUser.getClient().getClientCorrelationId());
			userProfile.setUniqueCode(clientUser.getUniqueCode());
			userProfile.setIsClient(false);
			userProfile.setFirstName(clientUser.getFirstName());
			userProfile.setLastName(clientUser.getLastName());
			userProfile.setStatus(clientUser.getStatus());
			userProfile.setGcmRegisterKey(clientUser.getGcmRegistaryKey());
			userProfile.setFirebaseServiceKey(clientUser.getFirebaseServiceKey());
			userProfile.setResidentialAddress(clientUser.getResidentialAddress());
			userProfile.setIso4217CurrencyAlphabeticCode(country.getIso4217CurrencyAlphabeticCode());
			userProfile.setClientCorrelationId(clientUser.getClient().getClientCorrelationId());
			userProfile.setIsClient(false);
			
			
			LOG.info("searching user countryDailCode and contact :"+countryDailCode+mobileNumber+" json : "+userProfile);
		}else if(client!=null) {
			
			userProfileResponse = clientProfileMgmtService.getClientProfile(client.getClientCorrelationId());
			userProfile =userProfileResponse.getUserProfile();
			userProfile.setMobileNumber(client.getContactNumber1());
			userProfile.setEmailAddress(client.getEmailAddress());
			userProfile.setCountryDialCode(client.getCountryDialCode());
			userProfile.setUserCorrelationId(client.getClientCorrelationId());
			Country country = countryRepo.findCountryByCountryDialCode(client.getCountryDialCode());
			userProfile.setCountryName(country.getCountryName());
			userProfile.setUniqueCode(client.getUniqueCode());
			userProfile.setIsClient(true);
			userProfile.setFirstName(client.getFirstName());
			userProfile.setLastName(client.getLastName());
			userProfile.setStatus(Enum.valueOf(Status.class, client.getStatus()));
			userProfile.setGcmRegisterKey(client.getGcmRegistaryKey());
			userProfile.setFirebaseServiceKey(client.getFirebaseServiceKey());
			userProfile.setResidentialAddress(client.getResidentialAddress());
			userProfile.setIso4217CurrencyAlphabeticCode(country.getIso4217CurrencyAlphabeticCode());
			userProfile.setIsClient(true);
			LOG.info("searching client countryDailCode and contact :"+countryDailCode+mobileNumber+" json : "+userProfile);
			
		}
		
		
		
		return userProfileResponse;

	}

	/* (non-Javadoc)
	 * @see com.ireslab.sendx.electra.service.CommonService#getAllSettlementReports()
	 */
	@Override
	public SendxElectraResponse getAllSettlementReports() {
		
				
		List<CashOut> settlementList = (List<CashOut>) cashOutRepository.findAll();
		SendxElectraResponse sendxElectraResponse =new SendxElectraResponse();
		
		if(settlementList == null) {
			settlementList = new ArrayList<>();
		}
		
		
		 List<com.ireslab.sendx.electra.dto.CashOutDto> settlement =new ArrayList<>();
		for (CashOut cashOut : settlementList) {
			CashOutDto cashOutDto = new CashOutDto();
			cashOutDto.setSettlementId(cashOut.getSettlementId());
			cashOutDto.setUserCorrelationId(cashOut.getUserCorrelationId());
			cashOutDto.setRequesterName(cashOut.getRequesterName());
			Country country =countryRepo.findCountryByCountryDialCode(cashOut.getCountryDialCode());
			cashOutDto.setNoOfTokens(country.getCurrencySymbol()+" "+cashOut.getNoOfTokens());
			cashOutDto.setInstitutionName(cashOut.getInstitutionName());
			cashOutDto.setInstitutionAccountNumber(cashOut.getInstitutionAccountNumber());
			cashOutDto.setBeneficiaryMobileNumber(cashOut.getBeneficiaryMobileNumber());
			cashOutDto.setStatus(cashOut.getStatus());
			cashOutDto.setModifiedDate(cashOut.getModifiedBy());
			cashOutDto.setModifiedBy(cashOut.getModifiedBy());
			cashOutDto.setCreatedDate(""+cashOut.getCreatedDate());
			cashOutDto.setFee(cashOut.getFee());
			settlement.add(cashOutDto);
		}
		

		if(settlementList.size()>0 && !settlementList.isEmpty()) {
			
			sendxElectraResponse.setSettlementReportList(settlement);
			sendxElectraResponse.setCode(100);
			sendxElectraResponse.setMessage("SUCCESS");
			sendxElectraResponse.setStatus(200);
			return sendxElectraResponse;
		}
		sendxElectraResponse.setSettlementReportList(settlement);
		sendxElectraResponse.setCode(101);
		sendxElectraResponse.setMessage("Settlement List is Empty");
		sendxElectraResponse.setStatus(200);
		return sendxElectraResponse;
	}

	/* (non-Javadoc)
	 * @see com.ireslab.sendx.electra.service.CommonService#updateSettlementReport(com.ireslab.sendx.electra.model.SendxElectraRequest)
	 */
	@Override
	public SendxElectraResponse updateSettlementReport(SendxElectraRequest sendxElectraRequest) {
		SendxElectraResponse sendxElectraResponse  =new SendxElectraResponse();
		ClientUser userDetails = null;
		
		
		
		Client client = clientRepository.findByClientCorrelationId(sendxElectraRequest.getUserCorrelationId());

		if (client != null) {

			userDetails = new ClientUser();
			userDetails.setAccountPublicKey(client.getBaseTxnAccountPublicKey());
			userDetails.setAccountSecretKey(client.getBaseTxnAccountSecretKey());
			userDetails.setUserCorrelationId(client.getClientCorrelationId());
			userDetails.setCountryDialCode(client.getCountryDialCode());
			userDetails.setStatus(Enum.valueOf(Status.class, client.getStatus()));
			userDetails.setClient(client);
			userDetails.setFirstName(client.getFirstName());
			userDetails.setLastName(client.getLastName());
			userDetails.setCountryDialCode(client.getCountryDialCode());
			userDetails.setMobileNumber(client.getContactNumber1());
			userDetails.setFirebaseServiceKey(client.getFirebaseServiceKey());
			userDetails.setGcmRegistaryKey(client.getGcmRegistaryKey());

		} else {
			userDetails = clientUserRepository.findByUserCorrelationId(sendxElectraRequest.getUserCorrelationId());
		}
		
		
		CashOut cashOut = cashOutRepository.findBySettlementId(Integer.parseInt(sendxElectraRequest.getSettlementId()));
		
			if(cashOut!=null) {
				cashOut.setStatus(sendxElectraRequest.getSettlementStatus().toUpperCase());
				cashOut.setModifiedDate(new Date());
				cashOut.setModifiedBy(electraConfig.getSettlementReportModifiedBy());
				cashOutRepository.save(cashOut);
				
				//---------- Send push notification to user about this settlement ------
				
				AndroidPushNotificationRequest androidPushNotificationRequestForUser = new AndroidPushNotificationRequest();
				JSONObject bodyForUser = new JSONObject();
				JSONObject notificationForUser = new JSONObject();
				JSONObject dataForUser = new JSONObject();
				LOG.info(" User GCM Key : "+ userDetails.getGcmRegistaryKey());
				try {
					
					bodyForUser.put("to", "/topics/" + userDetails.getGcmRegistaryKey());
					bodyForUser.put("priority", "high");
					notificationForUser.put("title", electraConfig.getSettlementNotificationTitle());
					notificationForUser.put("body", electraConfig.getSettlementNotificationBody());
					dataForUser.put("message", electraConfig.getSettlementNotificationMessage());
					dataForUser.put("code", electraConfig.getSettlementNotificationCode());
					bodyForUser.put("notification", notificationForUser);
					bodyForUser.put("data", dataForUser);
					
					androidPushNotificationRequestForUser.setFirebaseServiceKey(userDetails.getFirebaseServiceKey());
					androidPushNotificationRequestForUser.setBody(bodyForUser);

					// --send push notification
					pushNotification(androidPushNotificationRequestForUser);
				
				
				
				
				sendxElectraResponse.setCode(100);
				sendxElectraResponse.setMessage("settlement report updated  successfully.!");
			
			}catch (JSONException e) {
				sendxElectraResponse.setCode(101);
				sendxElectraResponse.setMessage("settlement report not updated  successfully.!");
				e.printStackTrace();
			}
			
				
			}
			return sendxElectraResponse;
	}

	/**
	 * @param androidPushNotificationRequest
	 * @return
	 */
	private String pushNotification(AndroidPushNotificationRequest androidPushNotificationRequest) {
		return pushNotificationService.sendPushNotification(androidPushNotificationRequest);
	}

	/* (non-Javadoc)
	 * @see com.ireslab.sendx.electra.service.CommonService#getAllSettlementReports(java.lang.String)
	 */
	@Override
	public SendxElectraResponse getAllSettlementReports(String correlationId) {
		List<CashOut> settlementList = (List<CashOut>) cashOutRepository.findAllByUserCorrelationId(correlationId);
		SendxElectraResponse sendxElectraResponse =new SendxElectraResponse();
		
		if(settlementList == null) {
			settlementList = new ArrayList<>();
		}
		 List<com.ireslab.sendx.electra.dto.CashOutDto> settlement =new ArrayList<>();	
		 for (CashOut cashOut : settlementList) {
				CashOutDto cashOutDto = new CashOutDto();
				cashOutDto.setSettlementId(cashOut.getSettlementId());
				cashOutDto.setUserCorrelationId(cashOut.getUserCorrelationId());
				cashOutDto.setRequesterName(cashOut.getRequesterName());
				Country country =countryRepo.findCountryByCountryDialCode(cashOut.getCountryDialCode());
				cashOutDto.setNoOfTokens(country.getCurrencySymbol()+" "+cashOut.getNoOfTokens());
				cashOutDto.setInstitutionName(cashOut.getInstitutionName());
				cashOutDto.setInstitutionAccountNumber(cashOut.getInstitutionAccountNumber());
				cashOutDto.setBeneficiaryMobileNumber(cashOut.getBeneficiaryMobileNumber());
				cashOutDto.setStatus(cashOut.getStatus());
				cashOutDto.setModifiedDate(cashOut.getModifiedBy());
				cashOutDto.setModifiedBy(cashOut.getModifiedBy());
				cashOutDto.setCreatedDate(""+cashOut.getCreatedDate());
				settlement.add(cashOutDto);
			}

		 if(settlementList.size()>0 && !settlementList.isEmpty()) {
				
				sendxElectraResponse.setSettlementReportList(settlement);
				sendxElectraResponse.setCode(100);
				sendxElectraResponse.setMessage("SUCCESS");
				sendxElectraResponse.setStatus(200);
				return sendxElectraResponse;
			}
			sendxElectraResponse.setSettlementReportList(settlement);
			sendxElectraResponse.setCode(101);
			sendxElectraResponse.setMessage("Settlement List is Empty");
			sendxElectraResponse.setStatus(200);
			return sendxElectraResponse;
	}
	

}
