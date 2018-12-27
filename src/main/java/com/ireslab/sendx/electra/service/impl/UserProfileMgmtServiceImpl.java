package com.ireslab.sendx.electra.service.impl;

import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.ireslab.sendx.electra.ElectraConfig;
import com.ireslab.sendx.electra.dto.Communication;
import com.ireslab.sendx.electra.dto.DataFields;
import com.ireslab.sendx.electra.dto.Document;
import com.ireslab.sendx.electra.dto.DriverLicence;
import com.ireslab.sendx.electra.dto.Location;
import com.ireslab.sendx.electra.dto.LocationAdditionalFields;
import com.ireslab.sendx.electra.dto.NationalId;
import com.ireslab.sendx.electra.dto.Passport;
import com.ireslab.sendx.electra.dto.PersonInfo;
import com.ireslab.sendx.electra.dto.StellarAccountDetailsDto;
import com.ireslab.sendx.electra.dto.StellarTransactionConfigDto;
import com.ireslab.sendx.electra.dto.StellarTransactionConfigDto.SenderAccount;
import com.ireslab.sendx.electra.dto.TransactionDetailsDto;
import com.ireslab.sendx.electra.dto.VerifyRequest;
import com.ireslab.sendx.electra.dto.VerifyResponse;
import com.ireslab.sendx.electra.entity.Client;
import com.ireslab.sendx.electra.entity.ClientAgentInvitation;
import com.ireslab.sendx.electra.entity.ClientUser;
import com.ireslab.sendx.electra.entity.ClientUserAgent;
import com.ireslab.sendx.electra.entity.Country;
import com.ireslab.sendx.electra.entity.Exchange;
import com.ireslab.sendx.electra.entity.MasterAccounts;
import com.ireslab.sendx.electra.entity.TransactionDetail;
import com.ireslab.sendx.electra.exceptions.ApiException;
import com.ireslab.sendx.electra.model.AgentResponse;
import com.ireslab.sendx.electra.model.AssetDetails;
import com.ireslab.sendx.electra.model.Error;
import com.ireslab.sendx.electra.model.SendxElectraRequest;
import com.ireslab.sendx.electra.model.SendxElectraResponse;
import com.ireslab.sendx.electra.model.UserAgentRegistrationRequest;
import com.ireslab.sendx.electra.model.UserAgentRegistrationResponse;
import com.ireslab.sendx.electra.model.UserAgentRequest;
import com.ireslab.sendx.electra.model.UserAgentResponse;
import com.ireslab.sendx.electra.model.UserProfile;
import com.ireslab.sendx.electra.model.UserProfileResponse;
import com.ireslab.sendx.electra.model.UserRegistrationRequest;
import com.ireslab.sendx.electra.model.UserRegistrationResponse;
import com.ireslab.sendx.electra.repository.ClientAgentInvitationRepository;
import com.ireslab.sendx.electra.repository.ClientRepository;
import com.ireslab.sendx.electra.repository.ClientUserRepository;
import com.ireslab.sendx.electra.repository.CountryRepository;
import com.ireslab.sendx.electra.repository.ExchangeRepository;
import com.ireslab.sendx.electra.repository.MasterAccountRepository;
import com.ireslab.sendx.electra.repository.TransactionDetailRepository;
import com.ireslab.sendx.electra.repository.UserAgentRepository;
import com.ireslab.sendx.electra.service.CommonService;
import com.ireslab.sendx.electra.service.ProfileImageService;
import com.ireslab.sendx.electra.service.UserProfileMgmtApiService;
import com.ireslab.sendx.electra.stellar.StellarTransactionManager;
import com.ireslab.sendx.electra.utils.CommonUtils;
import com.ireslab.sendx.electra.utils.ResponseCode;
import com.ireslab.sendx.electra.utils.Status;
import com.ireslab.sendx.electra.utils.StellarConstants;

/**
 * @author iRESlab
 *
 */
@Service
public class UserProfileMgmtServiceImpl implements UserProfileMgmtApiService {

	private static final Logger LOG = LoggerFactory.getLogger(UserProfileMgmtServiceImpl.class);
	@Autowired
	private ExchangeRepository exchangeRepository;
	@Autowired
	private CommonService commonService;

	@Autowired
	private ProfileImageService profileImageService;

	@Autowired
	private ClientUserRepository clientUserRepo;

	@Autowired
	private ClientRepository clientRepository;

	@Autowired
	private UserAgentRepository userAgentRepo;

	@Autowired
	private StellarTransactionManager stellarTransactionManager;

	@Autowired
	private TransactionDetailRepository transactionDetailRepository;

	@Autowired
	private CountryRepository countryRepository;

	@Autowired
	private MasterAccountRepository masterAccountRepository;

	

	@Autowired
	private ElectraConfig electraConfig;

	@Autowired
	private ClientAgentInvitationRepository clientAgentInvitationRepository;

	
	/* (non-Javadoc)
	 * @see com.ireslab.sendx.electra.service.UserProfileMgmtApiService#registerUserAccounts(com.ireslab.sendx.electra.model.UserRegistrationRequest)
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public UserRegistrationResponse registerUserAccounts(UserRegistrationRequest userRegistrationReq) {

		UserRegistrationResponse userRegistrationResponse = null;
		Date currentDate = new Date();

		List<UserProfile> userProfileList = userRegistrationReq.getUsers();
		List<Error> apiErrors = new ArrayList<>();

		// Check if the request is invalid - userProfileList is empty
		if (userProfileList == null || (userProfileList.isEmpty())) {
			throw new ApiException(HttpStatus.BAD_REQUEST, ResponseCode.FAIL.getCode(), apiErrors);
		}

		Client clientDetails = commonService.getClientDetailsByCorrelationId(userRegistrationReq.getClientId());
		LOG.info("Client Correlation Id : " + userRegistrationReq.getClientId());

		StellarTransactionConfigDto stellarTxnConfigDto = new StellarTransactionConfigDto();
		stellarTxnConfigDto.setIsTestNetwork(clientDetails.isTestnetAccount());

		
		MasterAccounts masterAccounts = ((List<MasterAccounts>) masterAccountRepository.findAll()).get(0);
		if (!clientDetails.isTestnetAccount()) {
			String initialLumensLoadQuantity = electraConfig.initialLumensLoadQuantity;
			LOG.debug("Initial Lumens for user registrations" + initialLumensLoadQuantity);
			// stellarTxnConfigDto.setIsLoadInitialAsset(true);
			stellarTxnConfigDto.setIsLoadInitialLumens(electraConfig.isLoadInitialLumens);
			stellarTxnConfigDto.setIsNativeAssetOperation(true);
			stellarTxnConfigDto.setInitialLumensLoadQuantity(initialLumensLoadQuantity);
			stellarTxnConfigDto.setNoOfTokens(initialLumensLoadQuantity);
			
			stellarTxnConfigDto
					.setSenderAccount(new SenderAccount().setPublicKey(masterAccounts.getIssuingAccountPublicKey())
							.setSecretSeed(masterAccounts.getIssuingAccountSecretKey()));

			
			stellarTxnConfigDto.setIssuingAccount(new StellarTransactionConfigDto.IssuingAccount()
					.setSecretSeed(masterAccounts.getIssuingAccountSecretKey())
					.setPublicKey(masterAccounts.getIssuingAccountPublicKey()));
			
		}

		// Iterating over the list
		userProfileList.forEach((userProfile) -> {

			try {

				/********************************
				 * ACCOUNT CREATION
				 ********************************/

				// Creating Stellar Account for user (plus Load initial asset)
				StellarAccountDetailsDto stellarAccountDetails = stellarTransactionManager
						.createAccount(stellarTxnConfigDto);
				
				LOG.info("Stellar account created for user : \n Full Name - " + userProfile.getFirstName() + " "
						+ userProfile.getLastName() + "\n User Correlation Id - " + userProfile.getUserCorrelationId());

				LOG.info("\n Public key ----> " +stellarAccountDetails.getPublicKey()+ "\n Secret Key ----> "+stellarAccountDetails.getSecretKey()+" \n Full Name - " + userProfile.getFirstName() );
				
				/*********************************
				 * TRUSTLINE CREATION
				 *********************************/

				/*******************************
				 * creating trust line with local currency (by country iso4217_currency_name)
				 *****************************/
				Country country = countryRepository.findCountryByCountryDialCode(userProfile.getCountryDialCode());

				

				// Logic implemented to support local currency transaction.
				if (country != null) {
					stellarTxnConfigDto.setAssetCode(country.getIso4217CurrencyAlphabeticCode());
					stellarTxnConfigDto.setAssetLimit(String.valueOf(StellarConstants.ASSET_TRUSTLINE_LIMIT));
				}
				// Master Account configuration
				List<MasterAccounts> masterAccountList = (List<MasterAccounts>) masterAccountRepository.findAll();
				MasterAccounts masterAccount = null;

				if (masterAccountList.size() > 0 && masterAccountList.get(0) != null) {
					masterAccount = masterAccountList.get(0);
				}

				// Removing client issuing account and setting MasterAccount issuing account

				
				stellarTxnConfigDto.setIssuingAccount(new StellarTransactionConfigDto.IssuingAccount()
						.setSecretSeed(masterAccount.getIssuingAccountSecretKey())
						.setPublicKey(masterAccount.getIssuingAccountPublicKey()));

				stellarTxnConfigDto.setReceiverAccount(new StellarTransactionConfigDto.ReceiverAccount()
						.setPublicKey(stellarAccountDetails.getPublicKey())
						.setSecretSeed(stellarAccountDetails.getSecretKey()));

				if (country != null) {
					LOG.debug("Creating trustline for user for holding "+country.getIso4217CurrencyAlphabeticCode()+" tokens");
					stellarTransactionManager.createTrustLine(stellarTxnConfigDto);
					LOG.info("Stellar trustline created");
				}

				ClientUser clientUser = clientUserRepo.findByUserCorrelationId(userProfile.getUserCorrelationId());

				if (clientUser == null) {
					clientUser = new ClientUser();
				} else if (clientUser != null && clientUser.getStatus().equals(Status.TERMINATED)) {
					LOG.debug("Updating existing account with new signup details having userId - "
							+ clientUser.getUserId());
				}

				clientUser.setAccountPublicKey(stellarAccountDetails.getPublicKey());
				clientUser.setAccountSecretKey(stellarAccountDetails.getSecretKey());
				clientUser.setFirstName(userProfile.getFirstName());
				clientUser.setLastName(userProfile.getLastName());
				clientUser.setClient(clientDetails);
				clientUser.setUserCorrelationId(userProfile.getUserCorrelationId());
				clientUser.setCreatedDate(currentDate);
				clientUser.setModifiedDate(currentDate);
				clientUser.setStatus(Status.ACTIVE);
				clientUser.setEkycEkybApproved(false);
				clientUser.setMobileNumber(userProfile.getMobileNumber());
				clientUser.setEmailAddress(userProfile.getEmailAddress());
				clientUser.setUniqueCode(userProfile.getUniqueCode());
				
				clientUser.setCountryDialCode(userProfile.getCountryDialCode());

				clientUser.setBusinessId(userProfile.getBusinessId());
				clientUser.setDob(userProfile.getDob());
				clientUser.setGender(userProfile.getGender());
				clientUser.setPostalCode(userProfile.getPostalCode());
				clientUser.setScanDocumentId(userProfile.getScanDocumentId());
				clientUser.setScanDocumentType(userProfile.getScanDocumentType());
				clientUser.setResidentialAddress(userProfile.getResidentialAddress());
				clientUser.setBusinessLatitude(userProfile.getBusinessLat());
				clientUser.setBusinessLongitude(userProfile.getBusinessLong());

				if (userProfile.getProfileImageValue() != null) {

					String profileImageUrl = profileImageService.saveImage("profile", userProfile.getMobileNumber(),
							userProfile.getProfileImageValue());
					clientUser.setProfileImageUrl(profileImageUrl);
				}

				if (userProfile.getScanDocumentFrontPage() != null) {

					String frontPartUrl = profileImageService.saveImage("scanDocumentFrontPart",
							userProfile.getMobileNumber(), userProfile.getScanDocumentFrontPage());
					clientUser.setScanDocumentFrontPart(frontPartUrl);
				}

				if (userProfile.getScanDocumentBackPage() != null) {

					String backPartUrl = profileImageService.saveImage("scanDocumentBackPart",
							userProfile.getMobileNumber(), userProfile.getScanDocumentBackPage());
					clientUser.setScanDocumentBackPart(backPartUrl);
				}

				if (userProfile.getIdproofImage() != null) {

					String idImageUrl = profileImageService.saveImage("idProof", userProfile.getMobileNumber(),
							userProfile.getIdproofImage());
					clientUser.setIdProofImage(idImageUrl);
				}

				if (electraConfig.getIsKycConfigure()) {
					if (country != null && country.getCountryCode() != null) {

						VerifyRequest verifyRequest = getTruliooVerifyRequest(userProfile, country);

						VerifyResponse verifyResponse = null;
						VerifyResponse verifyIdentityResponse = null;

						try {
							verifyResponse = commonService.truliooEkycEkyb(verifyRequest,
									electraConfig.truliooDocumentVerificationUsername,
									electraConfig.truliooDocumentVerificationPassword);
						} catch (Exception e) {

							e.printStackTrace();
						}

						try {

							List<String> consentForDataSource = new ArrayList<>();
							consentForDataSource.add("Credit Agency");

							verifyRequest.setConsentForDataSources(consentForDataSource);
							verifyIdentityResponse = commonService.truliooEkycEkyb(verifyRequest,
									electraConfig.truliooIdentityVerificationUsername,
									electraConfig.truliooIdentityVerificationPassword);
						} catch (Exception e) {

							e.printStackTrace();
						}
						if (verifyResponse != null) {
							clientUser.setKycTransactionId(verifyResponse.getTransactionID());
							clientUser.setKycTransactionRecordId(verifyResponse.getRecord().getTransactionRecordID());
							clientUser.setKycUploadedDate(verifyResponse.getUploadedDt());
						}
						if (verifyIdentityResponse != null) {
							clientUser.setIdentityTransactionId(verifyIdentityResponse.getTransactionID());
							clientUser.setIdentityTransactionRecordId(
									verifyIdentityResponse.getRecord().getTransactionRecordID());
							clientUser.setKycUploadedDate(verifyIdentityResponse.getUploadedDt());
						}

						if (verifyIdentityResponse != null && verifyResponse != null) {

							if ((!verifyResponse.getRecord().getRecordStatus().equalsIgnoreCase("nomatch")
									&& verifyResponse.getErrors().size() <= 0)
									&& (!verifyIdentityResponse.getRecord().getRecordStatus()
											.equalsIgnoreCase("nomatch")
											&& verifyIdentityResponse.getErrors().size() <= 0)) {
								clientUser.setEkycEkybApproved(true);
							} else {
								clientUser.setEkycEkybApproved(false);
							}
						}
					}
				}

				
				ClientAgentInvitation clientAgentInvitationByEmail = clientAgentInvitationRepository
						.findByEmailAddressAndClientId(userProfile.getEmailAddress(), clientDetails.getClientId());
				ClientAgentInvitation clientAgentInvitationByMobile = clientAgentInvitationRepository
						.findByMobileNumberAndClientId(userProfile.getMobileNumber(), clientDetails.getClientId());

				if (clientAgentInvitationByEmail != null) {

					clientUser.setSubscriptionDurations(clientAgentInvitationByEmail.getSubscriptionDurations());
					clientUser.setSubscriptionExpireOn(CommonUtils.getSubscriptionDurations(
							clientAgentInvitationByEmail.getSubscriptionDurations(),
							clientAgentInvitationByEmail.getInvitationDate()));
					
				}

				if (clientAgentInvitationByMobile != null) {
					clientUser.setSubscriptionDurations(clientAgentInvitationByMobile.getSubscriptionDurations());
					clientUser.setSubscriptionExpireOn(CommonUtils.getSubscriptionDurations(
							clientAgentInvitationByMobile.getSubscriptionDurations(),
							clientAgentInvitationByMobile.getInvitationDate()));
					
				}

				clientUser = clientUserRepo.save(clientUser);
				
				userProfile.setRegistered(true);

				
				userProfile.setEkycEkybApproved(clientUser.isEkycEkybApproved());

				// Getting updated account balance from stellar
				stellarTxnConfigDto.setReceiverAccount(new StellarTransactionConfigDto.ReceiverAccount()
						.setPublicKey(stellarAccountDetails.getPublicKey()));

				List<AssetDetails> accountBalances = stellarTransactionManager.getAccountBalance(stellarTxnConfigDto);

				for (AssetDetails accountBalance : accountBalances) {
					userProfile.getAssetDetails().add(accountBalance);
				}

			} catch (ApiException e) {

				LOG.error("Exception occurred while account creation - " + ExceptionUtils.getStackTrace(e));
				userProfile.setRegistered(false);
				apiErrors.addAll(e.getErrors());

			} catch (Exception exp) {

				LOG.error("Exception occurred while account creation - " + ExceptionUtils.getStackTrace(exp));
				userProfile.setRegistered(false);
			}
		});

		userRegistrationResponse = new UserRegistrationResponse();
		userRegistrationResponse.setCode(ResponseCode.Success.getCode());
		userRegistrationResponse.setStatus(HttpStatus.OK.value());
		userRegistrationResponse.setUsers(userProfileList);
		userRegistrationResponse.setErrors(apiErrors);
		return userRegistrationResponse;
	}

	
	/* (non-Javadoc)
	 * @see com.ireslab.sendx.electra.service.UserProfileMgmtApiService#getUserProfile(java.lang.String, java.lang.String)
	 */
	@Override
	public UserProfileResponse getUserProfile(String clientCorrelationId, String userCorrelationId) {

		UserProfileResponse userProfileResponse = null;

		
       LOG.info("clientCorrelationId : "+clientCorrelationId+"\n userCorrelationId :" +userCorrelationId);
		ClientUser clientUser = clientUserRepo.findByUserCorrelationId(userCorrelationId);
		// Checking if user not exists
		if (clientUser == null) {
			throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, ResponseCode.FAIL.getCode(), null);
		}
		

		boolean isExpired = false;
		Date subscriptionExpireOn = clientUser.getSubscriptionExpireOn();

		if (subscriptionExpireOn != null) {

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Date currentDate = null;
			Date expiryDate = null;
			try {
				currentDate = sdf.parse(sdf.format(new Date()));
				expiryDate = sdf.parse(sdf.format(subscriptionExpireOn));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (currentDate.compareTo(expiryDate) > 0) {
				isExpired = true;
			}

			if (isExpired) {

				clientUser.setStatus(Status.SUSPENDED);
				clientUserRepo.save(clientUser);
			}
		}

		Client client = clientRepository.findByClientId(clientUser.getClient().getClientId());
		clientCorrelationId = client.getClientCorrelationId();

		if (!clientUser.getStatus().equals(Status.ACTIVE)) {

			userProfileResponse = new UserProfileResponse();
			userProfileResponse.setStatus(HttpStatus.OK.value());
			userProfileResponse.setCode(ResponseCode.Success.getCode());
			userProfileResponse.setUserProfile(new UserProfile().setStatus(clientUser.getStatus()));
			return userProfileResponse;
		}

		StellarTransactionConfigDto stellarConfig = new StellarTransactionConfigDto();
		stellarConfig.setIsTestNetwork(
				commonService.getClientDetailsByCorrelationId(clientCorrelationId).isTestnetAccount());
		stellarConfig.setReceiverAccount(
				new StellarTransactionConfigDto.ReceiverAccount().setPublicKey(clientUser.getAccountPublicKey()));

		// Getting consolidated account balance from stellar
		List<AssetDetails> accountBalances = stellarTransactionManager.getAccountBalance(stellarConfig);

		UserProfile userProfile = new UserProfile();
		userProfile.setRegistered(true);
		userProfile.setStatus(Status.ACTIVE);
		userProfile.setAssetDetails(accountBalances);
		userProfile.setEkycEkybApproved(clientUser.isEkycEkybApproved());
		userProfile.setSubscriptionExpireOn(clientUser.getSubscriptionExpireOn());
		Country country = countryRepository.findCountryByCountryDialCode(clientUser.getCountryDialCode());
		userProfile.setCountryDialCode(country.getCountryDialCode());
		userProfile.setCurrencySymbol(country.getCurrencySymbol());
		userProfile.setIso4217CurrencyAlphabeticCode(country.getIso4217CurrencyAlphabeticCode());

		userProfileResponse = new UserProfileResponse();
		userProfileResponse.setStatus(HttpStatus.OK.value());
		userProfileResponse.setCode(ResponseCode.Success.getCode());
		userProfileResponse.setUserProfile(userProfile);

		return userProfileResponse;
	}

	/* (non-Javadoc)
	 * @see com.ireslab.sendx.electra.service.UserProfileMgmtApiService#findAllUsersByClientId(java.lang.String)
	 */
	@Override
	public UserRegistrationResponse findAllUsersByClientId(String clientCorrelationId) {
		UserRegistrationResponse clientUsersInfoResponse = null;
		List<UserProfile> userProfilesList = new ArrayList<UserProfile>();

		
		Client client = clientRepository.findByClientCorrelationId(clientCorrelationId);

		clientUserRepo.findByClient(client).forEach((clientUser) -> {
			UserProfile profile = new UserProfile();
			profile.setAssetDetails(getUserProfile(clientCorrelationId, clientUser.getUserCorrelationId())
					.getUserProfile().getAssetDetails());
			profile.setUserCorrelationId(clientUser.getUserCorrelationId());
			profile.setFirstName(clientUser.getFirstName());
			profile.setLastName(clientUser.getLastName());
			profile.setStatus(clientUser.getStatus());
			
			profile.setCreatedDate(CommonUtils.formatDate(clientUser.getCreatedDate(), "yyyy-MM-dd"));
			profile.setAccountPublicKey(clientUser.getAccountPublicKey());
			userProfilesList.add(profile);
		});

		clientUsersInfoResponse = new UserRegistrationResponse();
		clientUsersInfoResponse.setUsers(userProfilesList);
		return clientUsersInfoResponse;
	}

	/* (non-Javadoc)
	 * @see com.ireslab.sendx.electra.service.UserProfileMgmtApiService#findAllAgentsByClientId(java.lang.String)
	 */
	@Override
	public UserAgentRegistrationResponse findAllAgentsByClientId(String userCorrelationId) {
		UserAgentRegistrationResponse userAgentRegResponse = null;
		List<AgentResponse> agentList = new ArrayList<AgentResponse>();

		LOG.info("\n User Correlation Id - " + userCorrelationId);

		ClientUser clientUser = clientUserRepo.findByUserCorrelationId(userCorrelationId);

		LOG.debug("\n Client User Data - " + clientUser.toString());

		userAgentRepo.findByClientIdAndStatusNot(clientUser.getUserId()).forEach((clientUserAgent) -> {
			AgentResponse agentResponse = new AgentResponse();
			agentResponse.setAgentAddress(clientUserAgent.getAgentAddress());
			agentResponse.setBusinessId(clientUserAgent.getBusinessId());
			agentResponse.setBusinessLatitude(clientUserAgent.getBusinessLatitude());
			agentResponse.setBusinessLongitude(clientUserAgent.getBusinessLongitude());
			agentResponse.setBusinessLatitude(clientUserAgent.getBusinessLatitude());
			agentResponse.setCountryCode(clientUserAgent.getCountryCode());
			agentResponse.setCryptoCurrencies(clientUserAgent.getCryptoCurrencies());
			agentResponse.setFiatCurrencies(clientUserAgent.getFiatCurrencies());
			
			agentResponse.setMobileNumber(clientUserAgent.getMobileNumber());

			agentList.add(agentResponse);
		});

		userAgentRegResponse = new UserAgentRegistrationResponse();
		userAgentRegResponse.setAgents(agentList);
		return userAgentRegResponse;
	}

	/* (non-Javadoc)
	 * @see com.ireslab.sendx.electra.service.UserProfileMgmtApiService#updateUserAccounts(com.ireslab.sendx.electra.model.UserRegistrationRequest, java.lang.String)
	 */
	@Override
	public UserRegistrationResponse updateUserAccounts(UserRegistrationRequest userUpdationReq,
			String userCorrelationId) {

		UserRegistrationResponse userRegistrationResponse = null;
		List<UserProfile> userProfileList = userUpdationReq.getUsers();

		// Check if the request is invalid - userProfileList is empty
		if (userProfileList == null || (userProfileList.isEmpty())) {
			throw new ApiException(HttpStatus.BAD_REQUEST, ResponseCode.FAIL.getCode(), null);
		}

		List<Error> apiErrors = new ArrayList<>();

		// Iterating over the list
		userProfileList.forEach((userProfile) -> {

			try {
				
				ClientUser clientUserData = clientUserRepo.findByUserCorrelationId(userCorrelationId);

				LOG.info("clientUserData : " + clientUserData);
				LOG.info("\n User Correlation Id - " + userCorrelationId);

				Date currentDate = new Date();

				
				if (userProfile.getFirstName() != null) {

					clientUserData.setFirstName(userProfile.getFirstName());
				}

				if (userProfile.getLastName() != null) {
					clientUserData.setLastName(userProfile.getLastName());
				}
				if (userProfile.getStatus() != null) {
					clientUserData.setStatus(userProfile.getStatus());
					

				}

				clientUserData.setModifiedDate(currentDate);
				clientUserRepo.save(clientUserData);

				ClientUserAgent clientUserAgent = userAgentRepo.findByClientId(clientUserData.getUserId());
				// Insert entry into ClientUserAgent table

				if (clientUserAgent != null) {
					clientUserAgent.setStatus(clientUserData.getStatus().name());
					clientUserAgent.setCreatedDate(currentDate);
					userAgentRepo.save(clientUserAgent);
				}

				userProfile.setRegistered(true);

			} catch (ApiException e) {
				userProfile.setRegistered(false);
				apiErrors.addAll(e.getErrors());
			} catch (Exception exp) {
				userProfile.setRegistered(false);
			}
		});

		userRegistrationResponse = new UserRegistrationResponse();
		userRegistrationResponse.setCode(ResponseCode.Success.getCode());
		userRegistrationResponse.setStatus(HttpStatus.OK.value());
		userRegistrationResponse.setUsers(userProfileList);
		userRegistrationResponse.setErrors(apiErrors);
		return userRegistrationResponse;
	}

	/* (non-Javadoc)
	 * @see com.ireslab.sendx.electra.service.UserProfileMgmtApiService#updateUserAgent(com.ireslab.sendx.electra.model.UserAgentRegistrationRequest)
	 */
	@Override
	public UserAgentResponse updateUserAgent(UserAgentRegistrationRequest agentUpdationReq) {

		UserAgentResponse userAgentRegResponse = null;
		List<UserAgentRequest> agentList = agentUpdationReq.getAgents();

		// Check if the request is invalid - userProfileList is empty
		if (agentList == null || (agentList.isEmpty())) {
			throw new ApiException(HttpStatus.BAD_REQUEST, ResponseCode.FAIL.getCode(), null);
		}

		List<Error> apiErrors = new ArrayList<>();

		// Iterating over the list
		agentList.forEach((userAgentRequest) -> {

			try {
				ClientUser clientUser = commonService.getClientUserByCorrelationId(userAgentRequest.getCorrelationId());
				ClientUserAgent clientUserAgent = userAgentRepo.findByClientId(clientUser.getUserId());

				LOG.info("\n User Correlation Id - " + userAgentRequest.getCorrelationId());

				// Insert entry into ClientUserAgent table
				clientUserAgent.setStatus(userAgentRequest.getStatus());
				clientUserAgent.setCreatedDate(new Date());
				userAgentRepo.save(clientUserAgent);

			} catch (ApiException e) {
				LOG.error(ExceptionUtils.getStackTrace(e));
				apiErrors.addAll(e.getErrors());
			}
		});

		userAgentRegResponse = new UserAgentResponse();
		userAgentRegResponse.setCode(ResponseCode.Success.getCode());
		userAgentRegResponse.setStatus(HttpStatus.OK.value());
		userAgentRegResponse.setErrors(apiErrors);
		return userAgentRegResponse;
	}

	/* (non-Javadoc)
	 * @see com.ireslab.sendx.electra.service.UserProfileMgmtApiService#registerUserAgentAccounts(com.ireslab.sendx.electra.model.UserAgentRegistrationRequest, javax.servlet.http.HttpServletRequest)
	 */
	@Override
	public UserAgentRegistrationResponse registerUserAgentAccounts(
			UserAgentRegistrationRequest agentRegistrationRequest, HttpServletRequest request) {

		UserAgentRegistrationResponse userAgentRegistrationResponse = null;
		List<UserAgentRequest> userAgentRequestList = agentRegistrationRequest.getAgents();

		List<Error> apiErrors = new ArrayList<>();

		// Check if the request is invalid - userProfileList is empty
		if (userAgentRequestList == null || (userAgentRequestList.isEmpty())) {
			throw new ApiException(HttpStatus.BAD_REQUEST, ResponseCode.FAIL.getCode(), apiErrors);
		}

		// Iterating over the list
		userAgentRequestList.forEach((userAgentRequest) -> {

			try {
				ClientUserAgent clientUserAgent = null;
				Date currentDate = new Date();

				LOG.debug("Checking if request for Agent onboarding is already registered");
				ClientUser clientUser = commonService.getClientUserByCorrelationId(userAgentRequest.getCorrelationId());

				// Getting ClientUserAgent based on agent mobile number
				clientUserAgent = userAgentRepo
						.findBymobileNumber(BigInteger.valueOf(userAgentRequest.getAgentMobNo()));

				if (clientUserAgent != null) {
					LOG.info("Agent already exists with status - " + clientUserAgent.getStatus()
							+ " | Updating Onboarding data");

					if (clientUserAgent.getStatus().equalsIgnoreCase(Status.TERMINATED.name())) {
						LOG.info("Terminated agent | registering again");
					}

					clientUserAgent.setClientId(clientUser.getUserId());
					clientUserAgent.setAgentAddress(userAgentRequest.getBusinessAdd());
					clientUserAgent.setBusinessId(userAgentRequest.getBusinessId());
					clientUserAgent.setBusinessLatitude(userAgentRequest.getBusinessLat());
					clientUserAgent.setBusinessLongitude(userAgentRequest.getBusinessLong());
					clientUserAgent.setCountryCode(userAgentRequest.getCountryDialCode());
					clientUserAgent.setCryptoCurrencies(userAgentRequest.getCryptoCurrency());
					clientUserAgent.setFiatCurrencies(userAgentRequest.getFiatCurrency());
					clientUserAgent.setMobileNumber(BigInteger.valueOf(userAgentRequest.getAgentMobNo()));
					clientUserAgent.setCreatedDate(currentDate);

					clientUserAgent.setEkyc("PENDING");
					clientUserAgent.setStatus(Status.ACTIVE + "");
					
					userAgentRepo.save(clientUserAgent);

				} else {

					LOG.info("New Agent onboarding request received");

					
					clientUserAgent = new ClientUserAgent();

					clientUserAgent.setClientId(clientUser.getUserId());
					clientUserAgent.setAgentAddress(userAgentRequest.getBusinessAdd());
					clientUserAgent.setBusinessId(userAgentRequest.getBusinessId());
					clientUserAgent.setBusinessLatitude(userAgentRequest.getBusinessLat());
					clientUserAgent.setBusinessLongitude(userAgentRequest.getBusinessLong());
					clientUserAgent.setCountryCode(userAgentRequest.getCountryDialCode());
					clientUserAgent.setCryptoCurrencies(userAgentRequest.getCryptoCurrency());
					clientUserAgent.setFiatCurrencies(userAgentRequest.getFiatCurrency());
					clientUserAgent.setMobileNumber(BigInteger.valueOf(userAgentRequest.getAgentMobNo()));
					clientUserAgent.setCreatedDate(currentDate);
					
					clientUserAgent.setEkyc("PENDING");
					clientUserAgent.setStatus(Status.ACTIVE + "");

					clientUserAgent = userAgentRepo.save(clientUserAgent);
				}

				LOG.info("Agent onboarding data successfully saved in database");

			} catch (ApiException e) {

				LOG.error("Error occurred while registering agent - " + ExceptionUtils.getStackTrace(e));
				apiErrors.addAll(e.getErrors());
				throw new ApiException(HttpStatus.BAD_REQUEST, ResponseCode.FAIL.getCode(), apiErrors);
			}
		});

		userAgentRegistrationResponse = new UserAgentRegistrationResponse();
		userAgentRegistrationResponse.setCode(ResponseCode.Success.getCode());
		userAgentRegistrationResponse.setStatus(HttpStatus.OK.value());
		return userAgentRegistrationResponse;
	}

	/* (non-Javadoc)
	 * @see com.ireslab.sendx.electra.service.UserProfileMgmtApiService#userClientEntry(com.ireslab.sendx.electra.model.UserRegistrationRequest, javax.servlet.http.HttpServletRequest)
	 */
	@Override
	public UserRegistrationResponse userClientEntry(UserRegistrationRequest userRegistrationRequest,
			HttpServletRequest request) {

		
		LOG.info("\n User registering request - " + userRegistrationRequest.toString());
		UserRegistrationResponse userRegistrationResponse = null;
		List<UserProfile> userProfileList = userRegistrationRequest.getUsers();
		List<Error> apiErrors = new ArrayList<>();

		userProfileList.forEach((userProfile) -> {

			try {
				LOG.info("\n Updated Company Code - " + userProfile.getCompanyCode());

				Client client = clientRepository.findByCompanyCode(userProfile.getCompanyCode());

				LOG.info("\n Updated Client Correlation Id - " + client.getClientCorrelationId());
				UserRegistrationResponse userResponse = registerUserAccounts(
						(UserRegistrationRequest) userRegistrationRequest.setClientId(client.getClientCorrelationId()));
				if (userResponse == null) {
					throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, ResponseCode.FAIL.getCode(), apiErrors);
				}

			} catch (ApiException e) {

				apiErrors.addAll(e.getErrors());
			}
		});

		userRegistrationResponse = new UserRegistrationResponse();
		userRegistrationResponse.setCode(ResponseCode.Success.getCode());
		userRegistrationResponse.setStatus(HttpStatus.OK.value());
		userRegistrationResponse.setUsers(userProfileList);
		userRegistrationResponse.setErrors(apiErrors);

		return userRegistrationResponse;
	}

	/* (non-Javadoc)
	 * @see com.ireslab.sendx.electra.service.UserProfileMgmtApiService#getAgent(com.ireslab.sendx.electra.model.UserAgentRegistrationRequest)
	 */
	@Override
	public UserAgentResponse getAgent(UserAgentRegistrationRequest agentRequest) {
		final UserAgentResponse userAgentResponse = new UserAgentResponse();

		List<UserAgentRequest> userAgentRequestList = agentRequest.getAgents();
		List<Error> apiErrors = new ArrayList<>();

		userAgentRequestList.forEach((userAgentRequest) -> {

			try {
				ClientUserAgent clientUserAgent = userAgentRepo
						.findBymobileNumber(BigInteger.valueOf(userAgentRequest.getAgentMobNo()));

				if (clientUserAgent != null) {

					LOG.debug("Client user agent data - " + clientUserAgent.toString());

					AgentResponse agentResponse = new AgentResponse();

					if (clientUserAgent.getStatus().equals("ACTIVE")) {

						agentResponse.setAgentAddress(clientUserAgent.getAgentAddress());
						agentResponse.setEkyc(clientUserAgent.getEkyc());
						agentResponse.setBusinessId(clientUserAgent.getBusinessId());
						agentResponse.setBusinessLatitude(clientUserAgent.getBusinessLatitude());
						agentResponse.setBusinessLongitude(clientUserAgent.getBusinessLongitude());
						agentResponse.setCryptoCurrencies(clientUserAgent.getCryptoCurrencies());
						agentResponse.setFiatCurrencies(clientUserAgent.getFiatCurrencies());
						
						agentResponse.setMobileNumber(clientUserAgent.getMobileNumber());
						
					}
					userAgentResponse.setAgentResponse(agentResponse);
					userAgentResponse.setCode(ResponseCode.Success.getCode());
					userAgentResponse.setStatus(HttpStatus.OK.value());
				}

			} catch (ApiException e) {

				LOG.error("Error occurred while retrieving user agent data - " + ExceptionUtils.getStackTrace(e));
				apiErrors.addAll(e.getErrors());
				throw new ApiException(HttpStatus.BAD_REQUEST, apiErrors);
			}
		});

		userAgentResponse.setStatus(HttpStatus.OK.value());
		userAgentResponse.setCode(ResponseCode.Success.getCode());

		return userAgentResponse;
	}

	/* (non-Javadoc)
	 * @see com.ireslab.sendx.electra.service.UserProfileMgmtApiService#getAllUserTransactionalDetails(com.ireslab.sendx.electra.model.SendxElectraRequest)
	 */
	@Override
	public SendxElectraResponse getAllUserTransactionalDetails(SendxElectraRequest sendxElectraRequest) {

		SendxElectraResponse sendxElectraResponse = new SendxElectraResponse();

		List<TransactionDetailsDto> transactionDetailsDtos = sendxElectraResponse.getTransactionDetailsDtos();
		Country country;
		Client client;
		ClientUser clientUser = clientUserRepo.findByUserCorrelationId(sendxElectraRequest.getUserCorrelationId());
		if (clientUser == null) {
			client = clientRepository.findByClientCorrelationId(sendxElectraRequest.getUserCorrelationId());
			country = countryRepository.findCountryByCountryDialCode(client.getCountryDialCode());
		} else {
			country = countryRepository.findCountryByCountryDialCode(clientUser.getCountryDialCode());
		}

		
		
		List<TransactionDetail> transactionDetailList = null;
		
		if(sendxElectraRequest.getOfflineLedger() && !sendxElectraRequest.getAllLedger()) {
			transactionDetailList=  transactionDetailRepository
				.findBySourceCorrelationIdOrDestinationCorrelationIdAndIsOffline(sendxElectraRequest.getUserCorrelationId(),sendxElectraRequest.getUserCorrelationId(),sendxElectraRequest.getOfflineLedger());
		 }else if(!sendxElectraRequest.getOfflineLedger() && !sendxElectraRequest.getAllLedger()) {
			 transactionDetailList= transactionDetailRepository
				.findBySourceCorrelationIdOrDestinationCorrelationIdAndIsOffline(sendxElectraRequest.getUserCorrelationId(),sendxElectraRequest.getUserCorrelationId(),sendxElectraRequest.getOfflineLedger());
		 }
		 else {
			 transactionDetailList=transactionDetailRepository.findBySourceCorrelationIdOrDestinationCorrelationId(sendxElectraRequest.getUserCorrelationId(),
						sendxElectraRequest.getUserCorrelationId());
		 }
		
		
		for (TransactionDetail transactionDetail : transactionDetailList) {
			
			

			TransactionDetailsDto transactionDetailsDto = new TransactionDetailsDto();

			
			transactionDetailsDto.setRecieverFirstName(transactionDetail.getDestinationAccountName());
			transactionDetailsDto.setSenderFirstName(transactionDetail.getSourceAccountName());
			
			transactionDetailsDto
					.setTransactionDate(CommonUtils.transactionDate(transactionDetail.getTransactionDate()));
			

			transactionDetailsDto.setTransactionTime(transactionDetail.getTransactionDate().toString());
			transactionDetailsDto.setTransactionStatus(transactionDetail.getStatus());
			
			transactionDetailsDto.setTxnDate(CommonUtils.formatDateWithTimezoneForDevice(
					transactionDetail.getTransactionDate(), country.getCountryTimeZone()));
			
			transactionDetailsDto.setOffline(transactionDetail.getIsOffline());

			

			if (transactionDetail.getSourceCorrelationId().equals(sendxElectraRequest.getUserCorrelationId())) {
				transactionDetailsDto.setSendingTransaction(true);
				
				
				Country receiverCountry=null;
				Client receiverClient=null;
				ClientUser receiverClientUser = clientUserRepo.findByUserCorrelationId(transactionDetail.getDestinationCorrelationId());
				if (receiverClientUser == null) {
					receiverClient = clientRepository.findByClientCorrelationId(transactionDetail.getDestinationCorrelationId());
					if(receiverClient != null) {
						receiverCountry = countryRepository.findCountryByCountryDialCode(receiverClient.getCountryDialCode());
					}
					
				} else {
					receiverCountry = countryRepository.findCountryByCountryDialCode(receiverClientUser.getCountryDialCode());
				}
				if(receiverCountry != null) {
					transactionDetailsDto.setNoOfTokens(tokenConversion(transactionDetail.getAssetCode(), receiverCountry.getIso4217CurrencyAlphabeticCode(), transactionDetail.getTnxData()));
				}else {
					transactionDetailsDto.setNoOfTokens(transactionDetail.getTnxData());
				}
				
				
				
				
				transactionDetailsDto.setCorrelationId(transactionDetail.getDestinationCorrelationId());
			} else {
				transactionDetailsDto.setNoOfTokens(transactionDetail.getTnxData());
				transactionDetailsDto.setCorrelationId(transactionDetail.getSourceCorrelationId());
			}
			transactionDetailsDtos.add(transactionDetailsDto);

		}
		

		return sendxElectraResponse;
	}

	/**
	 * use to trulioo verification.
	 * 
	 * @param userProfile
	 * @param country
	 * @return
	 */
	public VerifyRequest getTruliooVerifyRequest(UserProfile userProfile, Country country) {

		DataFields dataFields = new DataFields();

		Date date = null;
		try {
			date = new SimpleDateFormat("dd/MM/yyyy").parse(userProfile.getDob());
		} catch (ParseException e) {
			
			e.printStackTrace();
		}
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH) + 1;
		int day = cal.get(Calendar.DAY_OF_MONTH);

		

		PersonInfo personInfo = new PersonInfo();
		personInfo.setFirstGivenName(userProfile.getFirstName());
		personInfo.setFirstSurName(userProfile.getLastName());
		personInfo.setGender(String.valueOf(userProfile.getGender().charAt(0)));
		personInfo.setDayOfBirth(day);
		personInfo.setMonthOfBirth(month);
		personInfo.setYearOfBirth(year);
		dataFields.setPersonInfo(personInfo);

		Communication communication = new Communication();
		communication.setMobileNumber(String.valueOf(userProfile.getMobileNumber()));
		communication.setEmailAddress(userProfile.getEmailAddress());
		dataFields.setCommunication(communication);

		Location location = new Location();
		location.setCounty(country.getCountryName());
		// location.setPostalCode(userProfile.getPostalCode());

		LocationAdditionalFields locationAdditionField = new LocationAdditionalFields();
		locationAdditionField.setAddress1(userProfile.getResidentialAddress());
		location.setAdditionalFields(locationAdditionField);
		dataFields.setLocation(location);

		List<NationalId> nationIdList = new ArrayList<NationalId>();

		Passport passport = new Passport();

		if (userProfile.getScanDocumentType().equalsIgnoreCase("Passport")) {
			passport.setNumber(userProfile.getScanDocumentId());
			passport.setMrz1("00000");
			passport.setMrz2("00000");
			dataFields.setPassport(passport);

			Document document = new Document();
			document.setDocumentFrontImage(userProfile.getScanDocumentFrontPage());
			document.setDocumentBackImage(userProfile.getScanDocumentBackPage());
			// document.setDocumentType(userProfile.getScanDocumentType());
			document.setDocumentType("Passport");
			dataFields.setDocument(document);
		} else if (userProfile.getScanDocumentType().equalsIgnoreCase("NRIC/ ID")) {
			NationalId nationalIds = new NationalId();
			nationalIds.setNumber(userProfile.getScanDocumentId());
			
			nationalIds.setType(country.getNationalId());
			nationIdList.add(nationalIds);
			dataFields.setNationalIds(nationIdList);

			Document document = new Document();
			document.setDocumentFrontImage(userProfile.getScanDocumentFrontPage());
			document.setDocumentBackImage(userProfile.getScanDocumentBackPage());
			document.setDocumentType("IdentityCard");
			dataFields.setDocument(document);
		} else {
			DriverLicence driverLicence = new DriverLicence();
			driverLicence.setNumber(userProfile.getScanDocumentId());
			dataFields.setDriverLicence(driverLicence);

			Document document = new Document();
			document.setDocumentFrontImage(userProfile.getScanDocumentFrontPage());
			document.setDocumentBackImage(userProfile.getScanDocumentBackPage());
			document.setDocumentType(userProfile.getScanDocumentType());
			dataFields.setDocument(document);
		}

		VerifyRequest verifyRequest = new VerifyRequest();
		verifyRequest.setCountryCode(country.getCountryCode());
		verifyRequest.setDataFields(dataFields);
		verifyRequest.setDemo(false);
		verifyRequest.setAcceptTruliooTermsAndConditions(true);

		
		verifyRequest.setConfigurationName("Identity Verification");

		return verifyRequest;

	}
	
	 /**
	  * use to exchange tokens.
	  * 
	 * @param to
	 * @param from
	 * @param token
	 * @return
	 */
	public  String tokenConversion(String to, String from, String token) {
	    	
	    	double ammoutToBeTransfered=0.0;
	    	
	    	Exchange exchange = exchangeRepository.findByExchangeTokenAndNativeCurrency(from,to);
	    	
	    	ammoutToBeTransfered =Double.parseDouble(token)*Double.parseDouble(exchange.getExchangeRate());
	    	
	    	return ""+ammoutToBeTransfered;
	    	
	    }

}