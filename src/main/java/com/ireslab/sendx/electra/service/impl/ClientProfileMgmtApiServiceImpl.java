package com.ireslab.sendx.electra.service.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

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
import com.ireslab.sendx.electra.dto.TransactionDto;
import com.ireslab.sendx.electra.dto.VerifyRequest;
import com.ireslab.sendx.electra.dto.VerifyResponse;
import com.ireslab.sendx.electra.entity.Client;
import com.ireslab.sendx.electra.entity.ClientAgentInvitation;
import com.ireslab.sendx.electra.entity.ClientAssetToken;
import com.ireslab.sendx.electra.entity.ClientCredential;
import com.ireslab.sendx.electra.entity.ClientSubscription;
import com.ireslab.sendx.electra.entity.ClientTransactionPurpose;
import com.ireslab.sendx.electra.entity.ClientUser;
import com.ireslab.sendx.electra.entity.Country;
import com.ireslab.sendx.electra.entity.Exchange;
import com.ireslab.sendx.electra.entity.MasterAccounts;
import com.ireslab.sendx.electra.entity.MasterTransactionPurpose;
import com.ireslab.sendx.electra.entity.SubscriptionPlans;
import com.ireslab.sendx.electra.entity.TransactionDetail;
import com.ireslab.sendx.electra.exceptions.ApiException;
import com.ireslab.sendx.electra.model.AssetDetails;
import com.ireslab.sendx.electra.model.ClientAssetMgmtRequest;
import com.ireslab.sendx.electra.model.ClientAssetTokenRequest;
import com.ireslab.sendx.electra.model.ClientAssetTokenResponse;
import com.ireslab.sendx.electra.model.ClientCredentials;
import com.ireslab.sendx.electra.model.ClientDataDto;
import com.ireslab.sendx.electra.model.ClientPageRequest;
import com.ireslab.sendx.electra.model.ClientPageResponse;
import com.ireslab.sendx.electra.model.ClientProfile;
import com.ireslab.sendx.electra.model.ClientProfileResponse;
import com.ireslab.sendx.electra.model.ClientRegistrationRequest;
import com.ireslab.sendx.electra.model.ClientRegistrationResponse;
import com.ireslab.sendx.electra.model.ClientSubscriptionDto;
import com.ireslab.sendx.electra.model.ClientSubscriptionRequest;
import com.ireslab.sendx.electra.model.ClientSubscriptionResponse;
import com.ireslab.sendx.electra.model.ClientSubscriptionUpdateRequest;
import com.ireslab.sendx.electra.model.ClientSubscriptionUpdateResponse;
import com.ireslab.sendx.electra.model.CompanyCodeResponse;
import com.ireslab.sendx.electra.model.Error;
import com.ireslab.sendx.electra.model.FilterLedgerRequest;
import com.ireslab.sendx.electra.model.FilterLedgerResponse;
import com.ireslab.sendx.electra.model.SubscriptionPlanDto;
import com.ireslab.sendx.electra.model.SubscriptionPlanResponse;
import com.ireslab.sendx.electra.model.UserProfile;
import com.ireslab.sendx.electra.model.UserProfileResponse;
import com.ireslab.sendx.electra.repository.ClientAgentInvitationRepository;
import com.ireslab.sendx.electra.repository.ClientAssetTokenRepository;
import com.ireslab.sendx.electra.repository.ClientCredentialRepository;
import com.ireslab.sendx.electra.repository.ClientRepository;
import com.ireslab.sendx.electra.repository.ClientSubscriptionRepository;
import com.ireslab.sendx.electra.repository.ClientUserRepository;
import com.ireslab.sendx.electra.repository.CountryRepository;
import com.ireslab.sendx.electra.repository.ExchangeRepository;
import com.ireslab.sendx.electra.repository.MasterAccountRepository;
import com.ireslab.sendx.electra.repository.MasterPurposeRepository;
import com.ireslab.sendx.electra.repository.SubscriptionPlanRepository;
import com.ireslab.sendx.electra.repository.TransactionDetailRepository;
import com.ireslab.sendx.electra.repository.TransactionPurposeRepository;
import com.ireslab.sendx.electra.service.ClientAssetMgmtService;
import com.ireslab.sendx.electra.service.ClientProfileMgmtApiService;
import com.ireslab.sendx.electra.service.CommonService;
import com.ireslab.sendx.electra.service.ProfileImageService;
import com.ireslab.sendx.electra.stellar.StellarTransactionManager;
import com.ireslab.sendx.electra.utils.CommonUtils;
import com.ireslab.sendx.electra.utils.ResponseCode;
import com.ireslab.sendx.electra.utils.Status;
import com.ireslab.sendx.electra.utils.StellarConstants;
import com.ireslab.sendx.electra.utils.TokenActivity;
import com.ireslab.sendx.electra.utils.TokenOperation;

/**
 * @author iRESlab
 *
 */
@Service
public class ClientProfileMgmtApiServiceImpl implements ClientProfileMgmtApiService {
	private static Logger LOG = LoggerFactory.getLogger(ClientProfileMgmtApiServiceImpl.class);

	@Autowired
	private StellarTransactionManager stellarTransactionManager;

	@Autowired
	private CommonService commonService;
	
	@Autowired
	private ExchangeRepository exchangeRepository;

	@Autowired
	private ProfileImageService profileImageService;

	@Autowired
	private ClientRepository clientRepository;

	@Autowired
	private ClientSubscriptionRepository clientSubscriptionRepository;

	@Autowired
	private SubscriptionPlanRepository subsPlanRepository;

	@Autowired
	private ClientCredentialRepository clientCredentialRepository;

	@Autowired
	private ClientAssetTokenRepository clientAssetTokenRepository;

	@Autowired
	private ModelMapper modelMapper;

	@Autowired
	private ClientAssetMgmtService clientAssetMgmtService;

	@Autowired
	private TransactionDetailRepository txnDetailRepo;

	@Autowired
	private ClientUserRepository clientUserRepo;

	@Autowired
	private ClientAgentInvitationRepository invitationRepository;

	@Autowired
	private CountryRepository countryRepository;

	@Autowired
	private ElectraConfig electraConfig;

	@Autowired
	private TransactionPurposeRepository txnPurposeRepo;

	@Autowired
	private MasterPurposeRepository masterPurposeRepo;

	@Autowired
	private MasterAccountRepository masterAccountRepo;
	
	

	/* (non-Javadoc)
	 * @see com.ireslab.sendx.electra.service.ClientProfileMgmtApiService#registerUserAccounts(com.ireslab.sendx.electra.model.ClientRegistrationRequest)
	 */
	@Override
	public ClientRegistrationResponse registerUserAccounts(ClientRegistrationRequest clientRegistrationReq) {

		ClientRegistrationResponse clientRegistrationResponse = null;

		List<ClientProfile> clientProfileList = clientRegistrationReq.getClientProfile();
		List<Error> apiErrors = new ArrayList<>();

		// Check if the request is invalid - userProfileList is empty
		if (clientProfileList == null || (clientProfileList.isEmpty())) {

			throw new ApiException(HttpStatus.BAD_REQUEST, ResponseCode.FAIL.getCode(), apiErrors);
		}
		LOG.debug("electra config properties is kyc configure : " + electraConfig.getIsKycConfigure());
		
		clientProfileList.forEach((clientProfile) -> {

			try {

				StellarTransactionConfigDto stellarTxnConfigDto = new StellarTransactionConfigDto();
				
				stellarTxnConfigDto.setIsTestNetwork(electraConfig.isTestnetAccount);

				// setting Master Account Details if isTestnetAccount =false
				MasterAccounts masterAccounts = ((List<MasterAccounts>) masterAccountRepo.findAll()).get(0);
				if (!electraConfig.isTestnetAccount) {

					stellarTxnConfigDto.setSenderAccount(
							new SenderAccount().setPublicKey(masterAccounts.getIssuingAccountPublicKey())
									.setSecretSeed(masterAccounts.getIssuingAccountSecretKey()));
					
					stellarTxnConfigDto.setInitialLumensLoadQuantity(electraConfig.initialLumensLoadQuantity);
					stellarTxnConfigDto.setIsLoadInitialLumens(electraConfig.isLoadInitialLumens);
					stellarTxnConfigDto.setIsNativeAssetOperation(true);
					stellarTxnConfigDto.setAssetCode("XLM");
					stellarTxnConfigDto.setNoOfTokens(electraConfig.initialLumensLoadQuantity);
				}

				// Creating Stellar Account for user (plus Load initial asset)
				StellarAccountDetailsDto stellarBaseAccountDetails = stellarTransactionManager
						.createAccount(stellarTxnConfigDto);
				
				
				// LOG.debug(stellarBaseAccountDetails.toString());

				Client clientdata = clientRepository.findByClientCorrelationId(clientProfile.getClientCorrelationId());
				if (stellarBaseAccountDetails == null) {
					LOG.info("Stellar Account Creation Errror occured..!!");
					apiErrors.add(new Error(8002, "client", "Stellar Account Creation Errror occured"));
					throw new ApiException(HttpStatus.BAD_REQUEST, ResponseCode.FAIL.getCode(), apiErrors);

				}

				else if (clientdata != null || stellarBaseAccountDetails == null) {
					LOG.info("Duplicate ClientCorrelationId found while creating client..!!");
					apiErrors.add(new Error(8001, "client", "dublicate client"));
					throw new ApiException(HttpStatus.BAD_REQUEST, ResponseCode.FAIL.getCode(), apiErrors);

				}

				else {
					 
					 
					 
					 
					 
					// Master Account configuration
						List<MasterAccounts> masterAccountList = (List<MasterAccounts>) masterAccountRepo.findAll();
						MasterAccounts masterAccount = null;

						if (masterAccountList.size() > 0 && masterAccountList.get(0) != null) {
							masterAccount = masterAccountList.get(0);
						}

						
						stellarTxnConfigDto.setIssuingAccount(new StellarTransactionConfigDto.IssuingAccount()
								.setSecretSeed(masterAccount.getIssuingAccountSecretKey())
								.setPublicKey(masterAccount.getIssuingAccountPublicKey()));

						stellarTxnConfigDto.setReceiverAccount(new StellarTransactionConfigDto.ReceiverAccount()
								.setPublicKey(stellarBaseAccountDetails.getPublicKey())
								.setSecretSeed(stellarBaseAccountDetails.getSecretKey()));
					 
					 

					LOG.debug(stellarBaseAccountDetails.toString());

					Country country = countryRepository
							.findCountryByCountryDialCode(clientProfile.getCountryDialCode());
					
					//stellarTxnConfigDto.setAssetCode(country.getIso4217CurrencyAlphabeticCode());
					
					if (country != null) {
						stellarTxnConfigDto.setAssetCode(country.getIso4217CurrencyAlphabeticCode());
						stellarTxnConfigDto.setAssetLimit(String.valueOf(StellarConstants.ASSET_TRUSTLINE_LIMIT));
					}	
					
					if (country != null) {
						LOG.debug("Creating trustline for user for holding "+country.getIso4217CurrencyAlphabeticCode()+" tokens");
						stellarTransactionManager.createTrustLine(stellarTxnConfigDto);
						LOG.info("Stellar trustline created");
					}
					

					// Insert entry into Client table
					Client client = new Client();
					client.setUniqueCode(clientProfile.getUniqueCode());
					client.setClientName(clientProfile.getClientName());
					client.setClientCorrelationId(clientProfile.getClientCorrelationId());
					client.setDescription(clientProfile.getDescription());
					
					client.setStatus(clientProfile.getClientStatus());
					
					client.setBaseTxnAccountPublicKey(stellarBaseAccountDetails.getPublicKey());
					client.setBaseTxnAccountSecretKey(stellarBaseAccountDetails.getSecretKey());
					client.setIssuingAccountPublicKey(stellarBaseAccountDetails.getPublicKey());
					client.setIssuingAccountSecretKey(stellarBaseAccountDetails.getSecretKey());

					client.setUsername(clientProfile.getUserName());
					client.setPassword(clientProfile.getPassword());
					client.setEmailAddress(clientProfile.getEmailAddress());
					client.setContactNumber1(clientProfile.getContactNumber1());
					client.setAccountType(clientProfile.getAccount_type());
					client.setCountryDialCode(clientProfile.getCountryDialCode());

					
					if (clientProfile.getCompanyCode() != null) {
						client.setCompanyCode(clientProfile.getCompanyCode());
					}

					client.setBusinessId(clientProfile.getBusinessId());
					client.setFirstName(clientProfile.getFirstName());
					client.setLastName(clientProfile.getLastName());
					client.setBusinessLatitude(clientProfile.getBusinessLat());
					client.setBusinessLongitude(clientProfile.getBusinessLong());
					client.setCompanyPinCode(clientProfile.getCompanyPinCode());
					client.setDob(clientProfile.getDob());
					client.setGender(clientProfile.getGender());
					client.setScanDocumentType(clientProfile.getScanDocumentType());
					client.setScanDocumentId(clientProfile.getScanDocumentId());

					client.setResidentialAddress(clientProfile.getResidentialAddress());

					if (clientProfile.getProfileImageValue() != null) {

						String profileImageUrl = profileImageService.saveImage("profile",
								clientProfile.getClientCorrelationId(), clientProfile.getProfileImageValue());
						client.setProfileImageUrl(profileImageUrl);
					}

					if (clientProfile.getScanDocumentFrontPage() != null) {

						String frontPartUrl = profileImageService.saveImage("scanDocumentFrontPart",
								clientProfile.getClientCorrelationId(), clientProfile.getScanDocumentFrontPage());
						client.setScanDocumentFrontPart(frontPartUrl);
					}

					if (clientProfile.getScanDocumentBackPage() != null) {

						String backPartUrl = profileImageService.saveImage("scanDocumentBackPart",
								clientProfile.getClientCorrelationId(), clientProfile.getScanDocumentBackPage());
						client.setScanDocumentBackPart(backPartUrl);
					}
					if (clientProfile.getIdProofImageValue() != null) {

						String idproofImageUrl = profileImageService.saveImage("idProof",
								clientProfile.getClientCorrelationId(), clientProfile.getIdProofImageValue());
						client.setIdProofImage(idproofImageUrl);
					}

					if (electraConfig.getIsKycConfigure()) {
						if (country != null && country.getCountryCode() != null) {

							VerifyRequest verifyRequest = getTruliooVerifyRequest(clientProfile, country);
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
								client.setKycTransactionId(verifyResponse.getTransactionID());
								client.setKycTransactionRecordId(verifyResponse.getRecord().getTransactionRecordID());
								client.setKycUploadedDate(verifyResponse.getUploadedDt());
							}
							if (verifyIdentityResponse != null) {
								client.setIdentityTransactionId(verifyIdentityResponse.getTransactionID());
								client.setIdentityTransactionRecordId(
										verifyIdentityResponse.getRecord().getTransactionRecordID());
								client.setKycUploadedDate(verifyIdentityResponse.getUploadedDt());
							}

							if (verifyResponse != null && verifyIdentityResponse != null) {

								if ((!verifyResponse.getRecord().getRecordStatus().equalsIgnoreCase("nomatch")
										&& verifyResponse.getErrors().size() <= 0)
										&& (!verifyIdentityResponse.getRecord().getRecordStatus()
												.equalsIgnoreCase("nomatch")
												&& verifyIdentityResponse.getErrors().size() <= 0)) {
									client.setEkycEkybApproved(true);
								} else {
									client.setEkycEkybApproved(false);
								}
							}

						}
					}

					
					client.setTestnetAccount(electraConfig.isTestnetAccount);

					clientRepository.save(client);
					clientProfile.setRegistered(true);
					clientProfile.setEkycEkybApproved(client.isEkycEkybApproved());

					
					Client clientData = clientRepository
							.findByClientCorrelationId(clientProfile.getClientCorrelationId());

					List<MasterTransactionPurpose> masterPurposeList = (List<MasterTransactionPurpose>) masterPurposeRepo
							.findAll();
					for (MasterTransactionPurpose masterPurpose : masterPurposeList) {
						ClientTransactionPurpose clientpurpose = new ClientTransactionPurpose();

						clientpurpose.setPurposeTitle(masterPurpose.getPurposeTitle());
						clientpurpose.setClientId(clientData.getClientId());
						clientpurpose.setCreatedDate(new Date());
						
						txnPurposeRepo.save(clientpurpose);

					}

					ClientCredentials clientCredentials = new ClientCredentials();

					clientCredentials.setClientId(clientData.getClientId());

					String dummyApiKey = client.getClientName().trim();
					clientCredentials.setClientApiKey(dummyApiKey);

					
					clientCredentials.setClientApiSecret(generateClientApiSecret(12));

					// clientCredentials.setClientApiSecret(clientData.getClientName());
					clientCredentials.setStatus(Status.BLOCKED.toString());
					clientCredentials.setResourceIds("sendxRestAPI");
					clientCredentials.setScopes("trust, read, write");
					clientCredentials.setGrantTypes("client_credentials");
					clientCredentials.setAuthorities("USER");
					clientCredentials.setAccessTokenValidity(30000);
					clientCredentials.setAdditionalInformation("additionalInformation");
					clientCredentials.setCreatedBy(1);
					clientCredentials.setCreatedDate(new Date());
					createClientCredentials(clientCredentials);
				}
			} catch (ApiException e) {

				clientProfile.setRegistered(false);
				apiErrors.addAll(e.getErrors());
			} catch (Exception exp) {
				clientProfile.setRegistered(false);
			}
		});

		clientRegistrationResponse = new ClientRegistrationResponse();
		clientRegistrationResponse.setCode(ResponseCode.Success.getCode());
		clientRegistrationResponse.setStatus(HttpStatus.OK.value());
		clientRegistrationResponse.setClients(clientProfileList);
		clientRegistrationResponse.setErrors(apiErrors);
		return clientRegistrationResponse;
	}

	/* (non-Javadoc)
	 * @see com.ireslab.sendx.electra.service.ClientProfileMgmtApiService#updateUserAccounts(com.ireslab.sendx.electra.model.ClientRegistrationRequest)
	 */
	@Override
	public ClientRegistrationResponse updateUserAccounts(ClientRegistrationRequest clientUpdationRequest) {
		
		ClientRegistrationResponse clientRegistrationResponse = null;

		List<ClientProfile> clientProfileList = clientUpdationRequest.getClientProfile();

		
		// Check if the request is invalid - userProfileList is empty
		if (clientProfileList == null || (clientProfileList.isEmpty())) {
			throw new ApiException(HttpStatus.BAD_REQUEST, ResponseCode.FAIL.getCode(), null);
		}

		Client clientDetails = commonService.getClientDetailsByCorrelationId(clientUpdationRequest.getClientId());

		List<Error> apiErrors = new ArrayList<>();

		// Iterating over the list
		clientProfileList.forEach((clientProfile) -> {

			try {

				// Insert entry into Client table

				clientDetails.setClientName(clientProfile.getClientName());
				clientDetails.setClientCorrelationId(clientProfile.getClientCorrelationId());
				clientDetails.setDescription(clientProfile.getDescription());
				clientDetails.setStatus(clientProfile.getClientStatus());
				clientDetails.setBaseTxnAccountPublicKey(clientDetails.getBaseTxnAccountPublicKey());
				clientDetails.setBaseTxnAccountSecretKey(clientDetails.getBaseTxnAccountSecretKey());
				clientDetails.setIssuingAccountPublicKey(clientDetails.getIssuingAccountPublicKey());
				clientDetails.setIssuingAccountSecretKey(clientDetails.getIssuingAccountSecretKey());

				clientRepository.save(clientDetails);
				clientProfile.setUpdated(true);
			} catch (ApiException e) {
				clientProfile.setUpdated(false);
				apiErrors.addAll(e.getErrors());
			} catch (Exception exp) {
				clientProfile.setUpdated(false);
			}
		});

		clientRegistrationResponse = new ClientRegistrationResponse();
		clientRegistrationResponse.setCode(ResponseCode.Success.getCode());
		clientRegistrationResponse.setStatus(HttpStatus.OK.value());
		clientRegistrationResponse.setClients(clientProfileList);
		clientRegistrationResponse.setErrors(apiErrors);
		return clientRegistrationResponse;

	}

	/* (non-Javadoc)
	 * @see com.ireslab.sendx.electra.service.ClientProfileMgmtApiService#updateClientStatus(com.ireslab.sendx.electra.model.ClientRegistrationRequest)
	 */
	@Override
	public ClientRegistrationResponse updateClientStatus(ClientRegistrationRequest clientUpdationRequest) {
		
		ClientRegistrationResponse clientRegistrationResponse = null;

		List<ClientProfile> clientProfileList = clientUpdationRequest.getClientProfile();

		// Check if the request is invalid - userProfileList is empty
		if (clientProfileList == null || (clientProfileList.isEmpty())) {
			throw new ApiException(HttpStatus.BAD_REQUEST, ResponseCode.FAIL.getCode(), null);
		}

		LOG.info("client update status in service :: " + clientUpdationRequest);

		Client clientDetails = commonService.getClientDetailsByCorrelationId(clientUpdationRequest.getClientId());

		List<Error> apiErrors = new ArrayList<>();

		// Iterating over the list
		clientProfileList.forEach((clientProfile) -> {

			try {
				// Insert entry into Client table
				clientDetails.setStatus(clientProfile.getClientStatus());
				
				clientRepository.save(clientDetails);
				
				if(clientProfile.getClientStatus().equalsIgnoreCase("TERMINATED") || clientProfile.getClientStatus().equalsIgnoreCase("SUSPENDED")) {
					
					List<ClientUser> userList = clientUserRepo.findByClient(clientDetails);
					for(ClientUser clientUser : userList) {
						clientUser.setStatus(Status.valueOf(clientProfile.getClientStatus()));
						clientUserRepo.save(clientUser);
					}
					
				}
				

			} catch (ApiException e) {
				LOG.error(ExceptionUtils.getStackTrace(e));
				apiErrors.addAll(e.getErrors());
			}
		});

		clientRegistrationResponse = new ClientRegistrationResponse();
		clientRegistrationResponse.setCode(ResponseCode.Success.getCode());
		clientRegistrationResponse.setStatus(HttpStatus.OK.value());
		clientRegistrationResponse.setClients(clientProfileList);
		clientRegistrationResponse.setErrors(apiErrors);
		return clientRegistrationResponse;

	}

	/* (non-Javadoc)
	 * @see com.ireslab.sendx.electra.service.ClientProfileMgmtApiService#updateClient(com.ireslab.sendx.electra.model.ClientRegistrationRequest)
	 */
	@Override
	public ClientRegistrationResponse updateClient(ClientRegistrationRequest clientUpdationRequest) {
		
		ClientRegistrationResponse clientRegistrationResponse = null;

		List<ClientProfile> clientProfileList = clientUpdationRequest.getClientProfile();

		// Check if the request is invalid - userProfileList is empty
		if (clientProfileList == null || (clientProfileList.isEmpty())) {
			throw new ApiException(HttpStatus.BAD_REQUEST, ResponseCode.FAIL.getCode(), null);
		}

		LOG.info("client update status in service  ");

		

		Client clientDetails = clientRepository.findByClientCorrelationId(clientUpdationRequest.getClientId());

		List<Error> apiErrors = new ArrayList<>();

		// Iterating over the list
		clientProfileList.forEach((clientProfile) -> {

			try {
				// Insert entry into Client table

				clientDetails.setCompanyAddress(clientProfile.getCompanyAddress());
				clientDetails.setCompanyCity(clientProfile.getCompanyCity());
				clientDetails.setCompanyContact(clientProfile.getCompanyContact());
				clientDetails.setCompanyCountry(clientProfile.getCompanyCountry());
				clientDetails.setCompanyFax(clientProfile.getCompanyFax());
				clientDetails.setCompanyPinCode(clientProfile.getCompanyPinCode());
				clientDetails.setCompanyState(clientProfile.getCompanyState());

				clientDetails.setPassword(clientProfile.getPassword());
				clientDetails.setIs2faEnabled(clientProfile.getIs2faEnabled());
				clientDetails.setDescription(clientProfile.getDescription());
				clientDetails.setPasswordReset(clientProfile.isPasswordReset());

				clientDetails.setResetToken(clientProfile.getResetToken());

				if (clientProfile.getSecurityCode() != null) {
					clientDetails.setSecurityCode(clientProfile.getSecurityCode());
				}
				
				if(clientProfile.getGstNumber()!=null) {
					clientDetails.setGstNumber(clientProfile.getGstNumber());
				}

				clientRepository.save(clientDetails);

			} catch (ApiException e) {
				LOG.error(ExceptionUtils.getStackTrace(e));
				apiErrors.addAll(e.getErrors());
			}
		});

		clientRegistrationResponse = new ClientRegistrationResponse();
		clientRegistrationResponse.setCode(ResponseCode.Success.getCode());
		clientRegistrationResponse.setStatus(HttpStatus.OK.value());
		clientRegistrationResponse.setClients(clientProfileList);
		clientRegistrationResponse.setErrors(apiErrors);
		return clientRegistrationResponse;

	}

	/* (non-Javadoc)
	 * @see com.ireslab.sendx.electra.service.ClientProfileMgmtApiService#activateclientCredentials(com.ireslab.sendx.electra.model.ClientRegistrationRequest)
	 */
	@Override
	public ClientRegistrationResponse activateclientCredentials(ClientRegistrationRequest clientUpdationRequest) {

		ClientRegistrationResponse clientRegistrationResponse = null;

		List<ClientProfile> clientProfileList = clientUpdationRequest.getClientProfile();

		
		// Check if the request is invalid - userProfileList is empty
		if (clientProfileList == null || (clientProfileList.isEmpty())) {
			throw new ApiException(HttpStatus.BAD_REQUEST, ResponseCode.FAIL.getCode(), null);
		}

		LOG.debug("clientUpdationRequest.getClientId() : " + clientUpdationRequest.getClientId());
		Client clientDetails = commonService.getClientDetailsByCorrelationId(clientUpdationRequest.getClientId());

		LOG.debug("clientDetails.getClientId() :" + clientDetails.getClientId());

		ClientCredential clientCredential = clientCredentialRepository.findByclientId(clientDetails.getClientId());

		List<Error> apiErrors = new ArrayList<>();
		clientRegistrationResponse = new ClientRegistrationResponse();

		LOG.debug("clientCredential.getStatus() : " + clientCredential.getStatus());

		if ("BLOCKED".equals(clientCredential.getStatus())) {

			String apiSecret = clientCredential.getClientApiSecret();

			// Encoding client apiSecret on request
			BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
			clientCredential.setClientApiSecret(bCryptPasswordEncoder.encode(apiSecret));

			// ACTIVATING client security configuration
			clientCredential.setStatus(Status.ACTIVE.toString());

			ClientCredential credential = clientCredentialRepository.save(clientCredential);

			ClientProfile clientProfile = clientProfileList.get(0);
			if ("ACTIVE".equals(credential.getStatus())) {

				clientProfile.setClientCredentialActivated(true);
			} else {
				clientProfile.setClientCredentialActivated(false);
			}
			// clientProfileList.add(clientProfile);
			clientRegistrationResponse.setCode(ResponseCode.CLIENT_CREDENTIAL_ACTIVATION_SUCCESS.getCode()); // 1401
			clientRegistrationResponse.setMessage("client credential activation success");
			clientRegistrationResponse.setStatus(HttpStatus.OK.value());
			clientRegistrationResponse.setClients(clientProfileList);
			clientRegistrationResponse.setErrors(apiErrors);
		} else {

			clientRegistrationResponse.setCode(ResponseCode.CLIENT_CREDENTIAL_ACTIVATION_FAIL.getCode()); // 1402
			clientRegistrationResponse.setMessage("client credential activation fail");
			clientRegistrationResponse.setStatus(HttpStatus.OK.value());
			clientRegistrationResponse.setClients(clientProfileList);
			clientRegistrationResponse.setErrors(apiErrors);

		}

		return clientRegistrationResponse;

	}

	/* (non-Javadoc)
	 * @see com.ireslab.sendx.electra.service.ClientProfileMgmtApiService#createClientCredentials(com.ireslab.sendx.electra.model.ClientCredentials)
	 */
	@Override
	public void createClientCredentials(ClientCredentials clientCredentials) {
		ClientCredential clientCredential = modelMapper.map(clientCredentials, ClientCredential.class);
		clientCredentialRepository.save(clientCredential);

	}

	/* (non-Javadoc)
	 * @see com.ireslab.sendx.electra.service.ClientProfileMgmtApiService#getClientCredentials(java.lang.String)
	 */
	@Override
	public ClientProfileResponse getClientCredentials(String clientCorrelationId) {

		ClientProfileResponse clientProfileResponse = new ClientProfileResponse();

		List<Error> apiErrors = new ArrayList<>();

		Client client = clientRepository.findByClientCorrelationId(clientCorrelationId);
		LOG.debug("clientCorrelationId :" + clientCorrelationId);

		if (client == null) {

			throw new ApiException(HttpStatus.BAD_REQUEST, ResponseCode.FAIL.getCode(), apiErrors);
		} else {

			ClientCredential clientCredential = clientCredentialRepository.findByclientId(client.getClientId());

			clientProfileResponse.setClientApiKey(clientCredential.getClientApiKey());
			clientProfileResponse.setClientApiSecret(clientCredential.getClientApiSecret());
		}

		return clientProfileResponse;
	}

	/* (non-Javadoc)
	 * @see com.ireslab.sendx.electra.service.ClientProfileMgmtApiService#clientAssetTokenConfiguration(com.ireslab.sendx.electra.model.ClientAssetTokenRequest)
	 */
	@Override
	public ClientAssetTokenResponse clientAssetTokenConfiguration(ClientAssetTokenRequest clientAssetTokenRequest) {
		// TODO Client Asset Token configuration code
		ClientAssetTokenResponse clientAssetTokenResponse = new ClientAssetTokenResponse();

		modelMapper = new ModelMapper();

		ClientAssetToken clientAssetToken = modelMapper.map(clientAssetTokenRequest, ClientAssetToken.class);
		
		Client client = clientRepository.findByClientCorrelationId(clientAssetTokenRequest.getClientCorrelationId());
		ClientAssetToken clientAssetTokenData = clientAssetTokenRepository.findByClientAndTokenCode(client,
				clientAssetTokenRequest.getTokenCode());

		if (clientAssetTokenData == null) {

			LOG.info("configuring new assetToken with tokenCode :: " + clientAssetTokenRequest.getTokenCode());

			clientAssetToken.setTokenCorrelationId(UUID.randomUUID().toString());
			clientAssetToken.setClient(
					clientRepository.findByClientCorrelationId(clientAssetTokenRequest.getClientCorrelationId()));

			clientAssetTokenRepository.save(clientAssetToken);
			
			
			
			com.ireslab.sendx.electra.dto.ClientAssetToken clientAssetTokenToSave = modelMapper.map(clientAssetToken, com.ireslab.sendx.electra.dto.ClientAssetToken.class);
			
			

			ClientAssetMgmtRequest clientAssetMgmtRequest = new ClientAssetMgmtRequest();
			
			clientAssetMgmtRequest.setClientAssetToken(clientAssetTokenToSave);
			
			clientAssetMgmtRequest.setClientCorrelationId(clientAssetTokenRequest.getClientCorrelationId());
			clientAssetMgmtRequest.setNoOfTokens(clientAssetTokenRequest.getBatchQuantity().toString());
			clientAssetMgmtService.createAccountOnStellar(clientAssetMgmtRequest);

			clientAssetTokenResponse.setCode(7001); // Token Created successfully
			clientAssetTokenResponse.setMessage("Token Created successfully");

		} else {

			LOG.info("Duplicate tokenCode : " + clientAssetTokenRequest.getTokenCode());

			clientAssetTokenResponse.setCode(7002); // Dublicate token
			clientAssetTokenResponse.setMessage("Duplicate Token");
		}

		return clientAssetTokenResponse;
	}

	/* (non-Javadoc)
	 * @see com.ireslab.sendx.electra.service.ClientProfileMgmtApiService#getClientDetail(java.lang.String)
	 */
	@Override
	public ClientProfileResponse getClientDetail(String clientCorrelationId) {
		Client client = clientRepository.findByClientCorrelationId(clientCorrelationId);
		ClientProfileResponse clientProfileResponse = new ClientProfileResponse();
		if (client != null) {

			clientProfileResponse.setIssuingAccountPublicKey(client.getIssuingAccountPublicKey());
			clientProfileResponse.setBaseTxnAccountPublicKey(client.getBaseTxnAccountPublicKey());
		} else {

			clientProfileResponse.setCode(7005);// client not found
			clientProfileResponse.setMessage("client not found");
		}

		return clientProfileResponse;
	}

	/* (non-Javadoc)
	 * @see com.ireslab.sendx.electra.service.ClientProfileMgmtApiService#getClientByCorrelationId(java.lang.String)
	 */
	@Override
	public ClientProfile getClientByCorrelationId(String clientCorrelationId) {
		Client client = clientRepository.findByClientCorrelationId(clientCorrelationId);
		ClientProfile clientProfile = new ClientProfile();
		if(client == null) {
			clientProfile.setCode(1101);
			return clientProfile;
		}
		ClientCredential clientCredential = clientCredentialRepository.findByclientId(client.getClientId());
		if (client != null) {
			clientProfile.setClientCorrelationId(client.getClientCorrelationId());
			clientProfile.setClientName(client.getClientName());
			clientProfile.setContactNumber1("("+client.getCountryDialCode()+")-" + CommonUtils.formatPhoneNumber(client.getContactNumber1()));
			clientProfile.setEmailAddress(client.getEmailAddress());
			clientProfile.setPassword(client.getPassword());
			clientProfile.setUserName(client.getUsername());
			clientProfile.setClientStatus(client.getStatus());
			clientProfile.setAccount_type(client.getAccountType());
			clientProfile.setCompany_name(client.getClientName());
			clientProfile.setCompanyAddress(client.getCompanyAddress());
			clientProfile.setCompanyCountry(client.getCompanyCountry());
			clientProfile.setCompanyContact(client.getCompanyContact());
			clientProfile.setCompanyFax(client.getCompanyFax());
			clientProfile.setCompanyCity("");
			clientProfile.setCompanyPinCode("");
			clientProfile.setCompanyState(client.getCompanyState());
			clientProfile.setDescription(client.getDescription());
			clientProfile.setIs2faEnabled(client.isIs2faEnabled());
			clientProfile.setSecurityCode(client.getSecurityCode());
			clientProfile.setResetToken(client.getResetToken());
			clientProfile.setCompanyCode(client.getCompanyCode());
			clientProfile.setPasswordReset(client.isPasswordReset());
			clientProfile.setCountryDialCode(client.getCountryDialCode());
			clientProfile.setGstNumber(client.getGstNumber());
			
			Country country = countryRepository.findCountryByCountryDialCode(client.getCountryDialCode());
			clientProfile.setCurrencySymbol(country.getCurrencySymbol());
			if (clientCredential != null) {
				if ("ACTIVE".equals(clientCredential.getStatus())) {

					clientProfile.setClientCredentialActivated(true);
				} else {
					clientProfile.setClientCredentialActivated(false);
				}
			}

			if (client.getClientSubscriptionId() != null) {
				clientProfile.setAvailableUsers(client.getClientSubscriptionId().getAvailableUsers());
			}

		}

		return clientProfile;
	}

	/* (non-Javadoc)
	 * @see com.ireslab.sendx.electra.service.ClientProfileMgmtApiService#getClientByUserName(java.lang.String)
	 */
	@Override
	public ClientProfile getClientByUserName(String userName) {

		LOG.info("Requested User Name - " + userName);
		Client client = clientRepository.findByUsername(userName);
		ClientProfile clientProfile = new ClientProfile();
		if(client == null) {
			
			clientProfile.setUserName(null);
			
			return clientProfile;
		}
		else {
		ClientCredential clientCredential = clientCredentialRepository.findByclientId(client.getClientId());

		if (client != null) {
			LOG.info("client data fetched by username");
			clientProfile.setClientCorrelationId(client.getClientCorrelationId());
			clientProfile.setClientName(client.getClientName());
			clientProfile.setContactNumber1(client.getContactNumber1());
			clientProfile.setEmailAddress(client.getEmailAddress());
			clientProfile.setPassword(client.getPassword());
			clientProfile.setUserName(client.getUsername());
			clientProfile.setClientStatus(client.getStatus());
			clientProfile.setAccount_type(client.getAccountType());
			clientProfile.setCompany_name(client.getClientName());
			clientProfile.setCompanyAddress(client.getCompanyAddress());
			clientProfile.setCompanyCountry(client.getCompanyCountry());
			clientProfile.setCompanyContact(client.getCompanyContact());
			clientProfile.setCompanyFax(client.getCompanyFax());
			clientProfile.setCompanyCity("");
			clientProfile.setCompanyPinCode("");
			clientProfile.setCompanyState(client.getCompanyState());
			clientProfile.setDescription(client.getDescription());
			clientProfile.setIs2faEnabled(client.isIs2faEnabled());
			clientProfile.setSecurityCode(client.getSecurityCode());
			clientProfile.setPasswordReset(client.isPasswordReset());
			clientProfile.setCountryDialCode(client.getCountryDialCode());
			if (clientCredential != null) {
				if ("ACTIVE".equals(clientCredential.getStatus())) {

					clientProfile.setClientCredentialActivated(true);
				} else {
					clientProfile.setClientCredentialActivated(false);
				}
			}
			
			if(client.getClientSubscriptionId() != null) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				Date currentDate = null;
				Date expiryDate = null;
				try {
					currentDate = sdf.parse(sdf.format(new Date()));
					expiryDate = sdf.parse(sdf.format(client.getClientSubscriptionId().getExpiryDate()));
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				if (currentDate.compareTo(expiryDate) > 0) {
					clientProfile.setIsSubscriptionExpired(true);
				}
			}

		}

		return clientProfile;
		}
	}

	/* (non-Javadoc)
	 * @see com.ireslab.sendx.electra.service.ClientProfileMgmtApiService#getClientByEmailId(java.lang.String)
	 */
	@Override
	public ClientProfile getClientByEmailId(String emailId) {

		LOG.info("Requested Mail ID - " + emailId);
		Client client = clientRepository.findByEmail(emailId);
		ClientProfile clientProfile = new ClientProfile();

		if (client != null) {

			LOG.info("client data fetched by email ID");
			clientProfile.setClientCorrelationId(client.getClientCorrelationId());
			clientProfile.setClientName(client.getClientName());
			clientProfile.setContactNumber1(client.getContactNumber1());
			clientProfile.setEmailAddress(client.getEmailAddress());
			clientProfile.setPassword(client.getPassword());
			clientProfile.setUserName(client.getUsername());
			clientProfile.setClientStatus(client.getStatus());
			clientProfile.setAccount_type(client.getAccountType());
			clientProfile.setCompany_name(client.getClientName());
			clientProfile.setCompanyAddress(client.getCompanyAddress());
			clientProfile.setCompanyCountry(client.getCompanyCountry());
			clientProfile.setCompanyContact(client.getCompanyContact());
			clientProfile.setCompanyFax(client.getCompanyFax());
			clientProfile.setCompanyCity("");
			clientProfile.setCompanyPinCode("");
			clientProfile.setCompanyState(client.getCompanyState());
			clientProfile.setDescription(client.getDescription());
			clientProfile.setIs2faEnabled(client.isIs2faEnabled());
			clientProfile.setSecurityCode(client.getSecurityCode());
			clientProfile.setPasswordReset(client.isPasswordReset());
			clientProfile.setCountryDialCode(client.getCountryDialCode());
			if(client.getClientSubscriptionId()!=null) {
				//clientProfile.setSubscriptionStatus(true);
				
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				Date currentDate = null;
				Date expiryDate = null;
				try {
					currentDate = sdf.parse(sdf.format(new Date()));
					expiryDate = sdf.parse(sdf.format(client.getClientSubscriptionId().getExpiryDate()));
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				if (currentDate.compareTo(expiryDate) < 0) {
					clientProfile.setSubscriptionStatus(true);
				}
				
			}else {
				
				clientProfile.setSubscriptionStatus(false);
			}
		}

		return clientProfile;
	}

	/* (non-Javadoc)
	 * @see com.ireslab.sendx.electra.service.ClientProfileMgmtApiService#getClientByResetToken(java.lang.String)
	 */
	@Override
	public ClientProfile getClientByResetToken(String token) {
		Client client = clientRepository.findUserByResetToken(token);
		ClientProfile clientProfile = new ClientProfile();

		if (client != null) {
			clientProfile.setClientCorrelationId(client.getClientCorrelationId());
			clientProfile.setClientName(client.getClientName());
			clientProfile.setContactNumber1(client.getContactNumber1());
			clientProfile.setEmailAddress(client.getEmailAddress());
			clientProfile.setPassword(client.getPassword());
			clientProfile.setUserName(client.getUsername());
			clientProfile.setClientStatus(client.getStatus());
			clientProfile.setAccount_type(client.getAccountType());
			clientProfile.setCompany_name(client.getClientName());
			clientProfile.setCompanyAddress(client.getCompanyAddress());
			clientProfile.setCompanyCountry(client.getCompanyCountry());
			clientProfile.setCompanyContact(client.getCompanyContact());
			clientProfile.setCompanyFax(client.getCompanyFax());
			clientProfile.setCompanyCity("");
			clientProfile.setCompanyPinCode("");
			clientProfile.setCompanyState(client.getCompanyState());
			clientProfile.setDescription(client.getDescription());
			clientProfile.setIs2faEnabled(client.isIs2faEnabled());
			clientProfile.setSecurityCode(client.getSecurityCode());
			clientProfile.setPasswordReset(client.isPasswordReset());
			clientProfile.setCountryDialCode(client.getCountryDialCode());
		}

		return clientProfile;
	}

	/* (non-Javadoc)
	 * @see com.ireslab.sendx.electra.service.ClientProfileMgmtApiService#getAllClient()
	 */
	@Override
	public ClientRegistrationResponse getAllClient() {
		List<Client> clientList = new ArrayList<>();
		clientRepository.findAll().forEach(clientList::add);

		java.lang.reflect.Type targetListType = new TypeToken<List<ClientProfile>>() {
		}.getType();

		List<ClientProfile> profileList = modelMapper.map(clientList, targetListType);

		ClientRegistrationResponse clientRegistrationResponse = new ClientRegistrationResponse();
		clientRegistrationResponse.setClients(profileList);

		return clientRegistrationResponse;
	}

	/* (non-Javadoc)
	 * @see com.ireslab.sendx.electra.service.ClientProfileMgmtApiService#getClientByCompanyCode(java.lang.String)
	 */
	@Override
	public ClientRegistrationResponse getClientByCompanyCode(String companyCode) {
		ClientRegistrationResponse clientRegistrationResponse = new ClientRegistrationResponse();

		Client client = clientRepository.findByCompanyCode(companyCode);
		if (client != null) {
			/*List<Client> clientList = new ArrayList<>();
			clientList.add(client);
			java.lang.reflect.Type targetListType = new TypeToken<List<ClientProfile>>() {
			}.getType();*/
			
			List<ClientProfile> profileList = new ArrayList<>();
			ClientProfile clientProfile = new ClientProfile();
			clientProfile.setClientCorrelationId(client.getClientCorrelationId());
			clientProfile.setClientName(client.getClientName());
			clientProfile.setContactNumber1("("+client.getCountryDialCode()+")-" + CommonUtils.formatPhoneNumber(client.getContactNumber1()));
			clientProfile.setEmailAddress(client.getEmailAddress());
			clientProfile.setPassword(client.getPassword());
			clientProfile.setUserName(client.getUsername());
			clientProfile.setClientStatus(client.getStatus());
			clientProfile.setAccount_type(client.getAccountType());
			clientProfile.setCompany_name(client.getClientName());
			clientProfile.setCompanyAddress(client.getCompanyAddress());
			clientProfile.setCompanyCountry(client.getCompanyCountry());
			clientProfile.setCompanyContact(client.getCompanyContact());
			clientProfile.setCompanyFax(client.getCompanyFax());
			clientProfile.setCompanyCity(client.getCompanyCity());
			clientProfile.setCompanyPinCode(client.getCompanyPinCode());
			clientProfile.setCompanyState(client.getCompanyState());
			clientProfile.setDescription(client.getDescription());
			clientProfile.setIs2faEnabled(client.isIs2faEnabled());
			clientProfile.setSecurityCode(client.getSecurityCode());
			clientProfile.setResetToken(client.getResetToken());
			clientProfile.setCompanyCode(client.getCompanyCode());
			clientProfile.setPasswordReset(client.isPasswordReset());
			clientProfile.setCountryDialCode(client.getCountryDialCode());
			clientProfile.setGstNumber(client.getGstNumber());

			profileList.add(clientProfile);
			clientRegistrationResponse.setClients(profileList);
		}

		return clientRegistrationResponse;
	}

	/* (non-Javadoc)
	 * @see com.ireslab.sendx.electra.service.ClientProfileMgmtApiService#getAllClientCustom(com.ireslab.sendx.electra.model.ClientPageRequest)
	 */
	@Override
	public ClientPageResponse getAllClientCustom(ClientPageRequest clientPageRequest) {

		
		Pageable pageable = createPageRequest(clientPageRequest);
		Page<Client> pageList = clientRepository.findAllCustom(pageable);

		
		List<Client> content = pageList.getContent();
		
		List<ClientDataDto> list = new ArrayList<>();
		
		for (Client client : content) {
			
			
			ClientDataDto c = new ClientDataDto();
			c.setCompany_name(client.getClientName());
			c.setEmail(client.getEmailAddress());
			
			c.setCompanyContact("("+client.getCountryDialCode()+")-" + CommonUtils.formatPhoneNumber(client.getContactNumber1()));
			
			c.setCreatedDate(CommonUtils.formatDate(client.getCreatedDate(), "yyyy-MM-dd"));

			c.setStatus(client.getStatus());
			c.setAccountId(client.getClientId());
			c.setClientCorrelationId(client.getClientCorrelationId());
			c.setUsername(client.getUsername());
			c.setAccount_type(client.getAccountType());
			c.setDescription(client.getDescription());

			list.add(c);
		}

		ClientPageResponse clientPageResponse = new ClientPageResponse();
		clientPageResponse.setPageList(list);
		clientPageResponse.setTotalElements(String.valueOf(pageList.getTotalElements()));
		clientPageResponse.setTotalPages(String.valueOf(pageList.getTotalPages()));
		clientPageResponse.setNumberOfElements(String.valueOf(pageList.getNumberOfElements()));
		clientPageResponse.setSize(String.valueOf(pageList.getSize()));

		return clientPageResponse;
	}

	/**
	 * @param clientPageRequest
	 * @return
	 */
	private Pageable createPageRequest(ClientPageRequest clientPageRequest) {
		return new PageRequest(clientPageRequest.getPageNumber(), clientPageRequest.getPageSize());
	}

	/* (non-Javadoc)
	 * @see com.ireslab.sendx.electra.service.ClientProfileMgmtApiService#getAllClientAssetToken(java.lang.String)
	 */
	@Override
	@Transactional
	public ClientAssetTokenResponse getAllClientAssetToken(String clientCorrelationId) {
		
		ClientAssetTokenResponse clientAssetTokenResponse = new ClientAssetTokenResponse();
		List<Error> apiErrors = new ArrayList<>();

		if (clientCorrelationId == null) {

			
			apiErrors.add(new Error(9001, "tokenDetailError", "clientCorrelationId not found.",
					"clientCorrelationId not found in All Client Asset Token request"));
			throw new ApiException(HttpStatus.BAD_REQUEST, ResponseCode.FAIL.getCode(), apiErrors);
		}

		Client client = clientRepository.findByClientCorrelationId(clientCorrelationId);

		if (client != null) {
			List<ClientAssetToken> clientAssetTokenList = client.getClientAssetTokens();

			java.lang.reflect.Type targetListType = new TypeToken<List<ClientAssetTokenRequest>>() {
			}.getType();
			List<ClientAssetTokenRequest> list = modelMapper.map(clientAssetTokenList, targetListType);
			clientAssetTokenResponse.setAssetTokenRequestsList(list);
		}

		return clientAssetTokenResponse;
	}

	/* (non-Javadoc)
	 * @see com.ireslab.sendx.electra.service.ClientProfileMgmtApiService#getAllFilterLedgerInfo(com.ireslab.sendx.electra.model.FilterLedgerRequest, org.springframework.data.domain.Pageable, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	public FilterLedgerResponse getAllFilterLedgerInfo(FilterLedgerRequest filterLedgerRequest, Pageable pageable,
			HttpServletResponse res) {

		

		FilterLedgerResponse filterLedgerResponse = new FilterLedgerResponse();
		if (filterLedgerRequest.getFilterBy() != null && filterLedgerRequest.getFilterBy().equals("date")) {
			LOG.info("SERCHING BY DATE....!!");

			String fromDate = filterLedgerRequest.getFromDate() + " 00:00:00";
			String toDate = filterLedgerRequest.getToDate() + " 23:59:59";
			

			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

			Date fromDateObj = null;
			Date toDateObj = null;
			try {
				fromDateObj = simpleDateFormat.parse(fromDate);
				toDateObj = simpleDateFormat.parse(toDate);
			} catch (ParseException e) {

				e.printStackTrace();
			}

			
			

			LOG.info("DATE ... FROM :" + fromDateObj + " TO :" + toDateObj);

			Client client = clientRepository.findByClientCorrelationId(filterLedgerRequest.getClientCorrelationId());

			Country country = countryRepository.findCountryByCountryDialCode(client.getCountryDialCode());

			List<ClientUser> findByClient = clientUserRepo.findByClient(client);
			

			List<String> listString = new ArrayList<>();

			for (ClientUser clientUser : findByClient) {
				listString.add(clientUser.getUserCorrelationId());
			}
			listString.add(filterLedgerRequest.getClientCorrelationId());

			Page<TransactionDetail> findBySourceCorrelationIdOrDestinationCorrelationId = txnDetailRepo
					.findByTransactionDateCustom(fromDateObj, toDateObj, listString, pageable);

			
			List<TransactionDetail> content = findBySourceCorrelationIdOrDestinationCorrelationId.getContent();

			List<TransactionDto> transactionDtoList = getTransactionDtoList(content, country, filterLedgerRequest);

			
			filterLedgerResponse.setTransactionList(transactionDtoList);
			filterLedgerResponse.setNumberOfElements(
					String.valueOf(findBySourceCorrelationIdOrDestinationCorrelationId.getNumberOfElements()));
			filterLedgerResponse
					.setTotalPages(String.valueOf(findBySourceCorrelationIdOrDestinationCorrelationId.getTotalPages()));
			filterLedgerResponse.setTotalElements(
					String.valueOf(findBySourceCorrelationIdOrDestinationCorrelationId.getTotalElements()));

		} else if (filterLedgerRequest.getFilterBy() != null && filterLedgerRequest.getFilterBy().equals("name")) {
			LOG.info("SERCHING BY NAME....!!");

			Client client = clientRepository.findByClientCorrelationId(filterLedgerRequest.getClientCorrelationId());

			Country country = countryRepository.findCountryByCountryDialCode(client.getCountryDialCode());

			List<ClientUser> findByClient = clientUserRepo.findByClient(client);
			System.out.println(findByClient.size());

			List<String> listString = new ArrayList<>();

			for (ClientUser clientUser : findByClient) {
				listString.add(clientUser.getUserCorrelationId());
			}
			listString.add(filterLedgerRequest.getClientCorrelationId());

			
			Page<TransactionDetail> findBySourceCorrelationIdOrDestinationCorrelationId = txnDetailRepo
					.findBySourceAccountNameContainingOrDestinationAccountNameContainingCust(
							filterLedgerRequest.getFilterData(), filterLedgerRequest.getFilterData(), listString,
							pageable);
			List<TransactionDetail> content = findBySourceCorrelationIdOrDestinationCorrelationId.getContent();

			List<TransactionDto> transactionDtoList = getTransactionDtoList(content, country,filterLedgerRequest);

			filterLedgerResponse.setTransactionList(transactionDtoList);
			filterLedgerResponse.setNumberOfElements(
					String.valueOf(findBySourceCorrelationIdOrDestinationCorrelationId.getNumberOfElements()));
			filterLedgerResponse
					.setTotalPages(String.valueOf(findBySourceCorrelationIdOrDestinationCorrelationId.getTotalPages()));
			filterLedgerResponse.setTotalElements(
					String.valueOf(findBySourceCorrelationIdOrDestinationCorrelationId.getTotalElements()));

		} else if (filterLedgerRequest.getFilterBy() != null && filterLedgerRequest.getFilterBy().equals("both")) {
			LOG.info("SEARCHING BY NAME AND DATE...!!");

			String fromDate = filterLedgerRequest.getFromDate() + " 00:00:00";
			String toDate = filterLedgerRequest.getToDate() + " 23:59:59";

			

			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

			Date fromDateObj = null;
			Date toDateObj = null;
			String filterData = filterLedgerRequest.getFilterData();
			try {
				fromDateObj = simpleDateFormat.parse(fromDate);
				toDateObj = simpleDateFormat.parse(toDate);
			} catch (ParseException e) {

				e.printStackTrace();
			}

			

			Client client = clientRepository.findByClientCorrelationId(filterLedgerRequest.getClientCorrelationId());

			Country country = countryRepository.findCountryByCountryDialCode(client.getCountryDialCode());

			List<ClientUser> findByClient = clientUserRepo.findByClient(client);
			

			List<String> listString = new ArrayList<>();

			for (ClientUser clientUser : findByClient) {
				listString.add(clientUser.getUserCorrelationId());
			}
			listString.add(filterLedgerRequest.getClientCorrelationId());

			Page<TransactionDetail> findBySourceCorrelationIdOrDestinationCorrelationId = txnDetailRepo
					.findByTransactionDateAndNameCustom(fromDateObj, toDateObj, filterData, filterData, listString,
							pageable);
			List<TransactionDetail> content = findBySourceCorrelationIdOrDestinationCorrelationId.getContent();

			List<TransactionDto> transactionDtoList = getTransactionDtoList(content, country,filterLedgerRequest);

			
			filterLedgerResponse.setTransactionList(transactionDtoList);
			filterLedgerResponse.setNumberOfElements(
					String.valueOf(findBySourceCorrelationIdOrDestinationCorrelationId.getNumberOfElements()));
			filterLedgerResponse
					.setTotalPages(String.valueOf(findBySourceCorrelationIdOrDestinationCorrelationId.getTotalPages()));
			filterLedgerResponse.setTotalElements(
					String.valueOf(findBySourceCorrelationIdOrDestinationCorrelationId.getTotalElements()));

		}

		else {

			Client client = clientRepository.findByClientCorrelationId(filterLedgerRequest.getClientCorrelationId());

			// countryRepository
			Country country = countryRepository.findCountryByCountryDialCode(client.getCountryDialCode());

			List<ClientUser> findByClient = clientUserRepo.findByClient(client);
			System.out.println(findByClient.size());

			List<String> listString = new ArrayList<>();

			for (ClientUser clientUser : findByClient) {
				listString.add(clientUser.getUserCorrelationId());
			}
			listString.add(filterLedgerRequest.getClientCorrelationId());

			

			Page<TransactionDetail> findBySourceCorrelationIdOrDestinationCorrelationId = txnDetailRepo
					.customMethod(listString, pageable);

			List<TransactionDetail> content = findBySourceCorrelationIdOrDestinationCorrelationId.getContent();

			List<TransactionDto> transactionDtoList = getTransactionDtoList(content, country,filterLedgerRequest);

			
			filterLedgerResponse.setTransactionList(transactionDtoList);
			filterLedgerResponse.setNumberOfElements(
					String.valueOf(findBySourceCorrelationIdOrDestinationCorrelationId.getNumberOfElements()));
			filterLedgerResponse
					.setTotalPages(String.valueOf(findBySourceCorrelationIdOrDestinationCorrelationId.getTotalPages()));
			filterLedgerResponse.setTotalElements(
					String.valueOf(findBySourceCorrelationIdOrDestinationCorrelationId.getTotalElements()));

			
		}

		
		return filterLedgerResponse;
	}
	
	
	/* (non-Javadoc)
	 * @see com.ireslab.sendx.electra.service.ClientProfileMgmtApiService#getOnlineOfflineLedger(com.ireslab.sendx.electra.model.FilterLedgerRequest, org.springframework.data.domain.Pageable, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	public FilterLedgerResponse getOnlineOfflineLedger(FilterLedgerRequest filterLedgerRequest, Pageable pageable,
			HttpServletResponse response) {

		FilterLedgerResponse filterLedgerResponse = new FilterLedgerResponse();
		if (filterLedgerRequest.getFilterBy() != null && filterLedgerRequest.getFilterBy().equals("date")) {
			LOG.info("SERCHING BY DATE....!!");

			String fromDate = filterLedgerRequest.getFromDate() + " 00:00:00";
			String toDate = filterLedgerRequest.getToDate() + " 23:59:59";

			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

			Date fromDateObj = null;
			Date toDateObj = null;
			try {
				fromDateObj = simpleDateFormat.parse(fromDate);
				toDateObj = simpleDateFormat.parse(toDate);
			} catch (ParseException e) {

				e.printStackTrace();
			}

			
			LOG.info("Search by - DATE ... FROM :" + fromDateObj + " TO :" + toDateObj);

			Client client = clientRepository.findByClientCorrelationId(filterLedgerRequest.getClientCorrelationId());

			Country country = countryRepository.findCountryByCountryDialCode(client.getCountryDialCode());

			List<ClientUser> findByClient = clientUserRepo.findByClient(client);
			LOG.debug("User list find by client : "+findByClient.size());

			List<String> listString = new ArrayList<>();

			for (ClientUser clientUser : findByClient) {
				listString.add(clientUser.getUserCorrelationId());
			}
			listString.add(filterLedgerRequest.getClientCorrelationId());

			Page<TransactionDetail> findBySourceCorrelationIdOrDestinationCorrelationId = txnDetailRepo
					.findByDateOnlineOfflineCustom(fromDateObj, toDateObj, listString, filterLedgerRequest.getOffline(), pageable);

			List<TransactionDetail> content = findBySourceCorrelationIdOrDestinationCorrelationId.getContent();

			List<TransactionDto> transactionDtoList = getTransactionDtoList(content, country,filterLedgerRequest);

			filterLedgerResponse.setTransactionList(transactionDtoList);
			filterLedgerResponse.setNumberOfElements(
					String.valueOf(findBySourceCorrelationIdOrDestinationCorrelationId.getNumberOfElements()));
			filterLedgerResponse
					.setTotalPages(String.valueOf(findBySourceCorrelationIdOrDestinationCorrelationId.getTotalPages()));
			filterLedgerResponse.setTotalElements(
					String.valueOf(findBySourceCorrelationIdOrDestinationCorrelationId.getTotalElements()));

		} else if (filterLedgerRequest.getFilterBy() != null && filterLedgerRequest.getFilterBy().equals("name")) {
			LOG.info("SERCHING BY NAME....!!");

			Client client = clientRepository.findByClientCorrelationId(filterLedgerRequest.getClientCorrelationId());

			Country country = countryRepository.findCountryByCountryDialCode(client.getCountryDialCode());

			List<ClientUser> findByClient = clientUserRepo.findByClient(client);
			LOG.debug("User list find by client : "+findByClient.size());

			List<String> listString = new ArrayList<>();

			for (ClientUser clientUser : findByClient) {
				listString.add(clientUser.getUserCorrelationId());
			}
			listString.add(filterLedgerRequest.getClientCorrelationId());

			Page<TransactionDetail> findBySourceCorrelationIdOrDestinationCorrelationId = txnDetailRepo
					.findByNameOnlineOfflineCust(
							filterLedgerRequest.getFilterData(), filterLedgerRequest.getFilterData(), listString,
							filterLedgerRequest.getOffline(),pageable);
			List<TransactionDetail> content = findBySourceCorrelationIdOrDestinationCorrelationId.getContent();

			List<TransactionDto> transactionDtoList = getTransactionDtoList(content, country,filterLedgerRequest);

			filterLedgerResponse.setTransactionList(transactionDtoList);
			filterLedgerResponse.setNumberOfElements(
					String.valueOf(findBySourceCorrelationIdOrDestinationCorrelationId.getNumberOfElements()));
			filterLedgerResponse
					.setTotalPages(String.valueOf(findBySourceCorrelationIdOrDestinationCorrelationId.getTotalPages()));
			filterLedgerResponse.setTotalElements(
					String.valueOf(findBySourceCorrelationIdOrDestinationCorrelationId.getTotalElements()));

		} else if (filterLedgerRequest.getFilterBy() != null && filterLedgerRequest.getFilterBy().equals("both")) {
			LOG.info("SEARCHING BY NAME AND DATE...!!");

			String fromDate = filterLedgerRequest.getFromDate() + " 00:00:00";
			String toDate = filterLedgerRequest.getToDate() + " 23:59:59";

			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

			Date fromDateObj = null;
			Date toDateObj = null;
			String filterData = filterLedgerRequest.getFilterData();
			try {
				fromDateObj = simpleDateFormat.parse(fromDate);
				toDateObj = simpleDateFormat.parse(toDate);
			} catch (ParseException e) {

				e.printStackTrace();
			}


			Client client = clientRepository.findByClientCorrelationId(filterLedgerRequest.getClientCorrelationId());

			Country country = countryRepository.findCountryByCountryDialCode(client.getCountryDialCode());

			List<ClientUser> findByClient = clientUserRepo.findByClient(client);
			LOG.debug("User list find by client : "+findByClient.size());
			List<String> listString = new ArrayList<>();

			for (ClientUser clientUser : findByClient) {
				listString.add(clientUser.getUserCorrelationId());
			}
			listString.add(filterLedgerRequest.getClientCorrelationId());

			Page<TransactionDetail> findBySourceCorrelationIdOrDestinationCorrelationId = txnDetailRepo
					.findByDateAndNameOnlineOfflineCustom(fromDateObj, toDateObj, filterData, filterData, listString,
							filterLedgerRequest.getOffline(),pageable);
			List<TransactionDetail> content = findBySourceCorrelationIdOrDestinationCorrelationId.getContent();

			List<TransactionDto> transactionDtoList = getTransactionDtoList(content, country,filterLedgerRequest);

			filterLedgerResponse.setTransactionList(transactionDtoList);
			filterLedgerResponse.setNumberOfElements(
					String.valueOf(findBySourceCorrelationIdOrDestinationCorrelationId.getNumberOfElements()));
			filterLedgerResponse
					.setTotalPages(String.valueOf(findBySourceCorrelationIdOrDestinationCorrelationId.getTotalPages()));
			filterLedgerResponse.setTotalElements(
					String.valueOf(findBySourceCorrelationIdOrDestinationCorrelationId.getTotalElements()));

		}

		else {

			Client client = clientRepository.findByClientCorrelationId(filterLedgerRequest.getClientCorrelationId());

			Country country = countryRepository.findCountryByCountryDialCode(client.getCountryDialCode());

			List<ClientUser> findByClient = clientUserRepo.findByClient(client);
			LOG.debug("User list find by client for all data : "+findByClient.size());

			List<String> listString = new ArrayList<>();

			for (ClientUser clientUser : findByClient) {
				listString.add(clientUser.getUserCorrelationId());
			}
			listString.add(filterLedgerRequest.getClientCorrelationId());


			Page<TransactionDetail> findBySourceCorrelationIdOrDestinationCorrelationId = txnDetailRepo
					.customOnlineOfflineMethod(listString,filterLedgerRequest.getOffline(), pageable);

			List<TransactionDetail> content = findBySourceCorrelationIdOrDestinationCorrelationId.getContent();

			List<TransactionDto> transactionDtoList = getTransactionDtoList(content, country,filterLedgerRequest);

			filterLedgerResponse.setTransactionList(transactionDtoList);
			filterLedgerResponse.setNumberOfElements(
					String.valueOf(findBySourceCorrelationIdOrDestinationCorrelationId.getNumberOfElements()));
			filterLedgerResponse
					.setTotalPages(String.valueOf(findBySourceCorrelationIdOrDestinationCorrelationId.getTotalPages()));
			filterLedgerResponse.setTotalElements(
					String.valueOf(findBySourceCorrelationIdOrDestinationCorrelationId.getTotalElements()));

			
		}
        
		return filterLedgerResponse;
	}
	

	/* (non-Javadoc)
	 * @see com.ireslab.sendx.electra.service.ClientProfileMgmtApiService#generateCompanyCode()
	 */
	@Override
	public CompanyCodeResponse generateCompanyCode() {
		

		String STRINGCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
		StringBuilder salt = new StringBuilder();
		Random rnd = new Random();
		while (salt.length() < 5) { // length of the random string.
			int index = (int) (rnd.nextFloat() * STRINGCHARS.length());
			salt.append(STRINGCHARS.charAt(index));
		}
		String companyCode = salt.toString();

		CompanyCodeResponse companyCodeResponse = new CompanyCodeResponse();
		companyCodeResponse.setCompanyCode(companyCode);
		companyCodeResponse.setCode(100);
		companyCodeResponse.setStatus(HttpStatus.OK.value());
		companyCodeResponse.setMessage("SUCCESS");
		return companyCodeResponse;
	}

	/**
	 * @param length
	 * @return
	 */
	public String generateClientApiSecret(Integer length) {
		

		String STRINGCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
		StringBuilder salt = new StringBuilder();
		Random rnd = new Random();
		while (salt.length() < length) { // length of the random string.
			int index = (int) (rnd.nextFloat() * STRINGCHARS.length());
			salt.append(STRINGCHARS.charAt(index));
		}
		String companyCode = salt.toString();

		return companyCode;
	}

	

	/**
	 * use to generate transaction detail list.
	 * 
	 * @param transactionDetails
	 * @param country
	 * @param filterLedgerRequest
	 * @return
	 */
	private List<TransactionDto> getTransactionDtoList(List<TransactionDetail> transactionDetails,
			Country country, FilterLedgerRequest filterLedgerRequest) {

		List<TransactionDto> transactionDtos = new ArrayList<>();

		for (TransactionDetail transactionDetailsObj : transactionDetails) {
			TransactionDto transactionDto = new TransactionDto();
			transactionDto.setType(transactionDetailsObj.getType());
			transactionDto.setOperation(transactionDetailsObj.getOperation());
			
			transactionDto.setTransactionDate(
					CommonUtils.formatDateWithTimezone(transactionDetailsObj.getTransactionDate(), country.getCountryTimeZone()));
			transactionDto.setTransactionSequenceNo(transactionDetailsObj.getTransactionSequenceNo());
			transactionDto.setSourceAccountName(transactionDetailsObj.getSourceAccountName());
			transactionDto.setDestinationAccountName(transactionDetailsObj.getDestinationAccountName());
			if(transactionDetailsObj.getSourceCorrelationId().equals(filterLedgerRequest.getClientCorrelationId()) && !transactionDetailsObj.getDestinationCorrelationId().equalsIgnoreCase("Master Account") && !transactionDetailsObj.getDestinationCorrelationId().equalsIgnoreCase("Y8jAm7LzPH")) {
				Country receiverCountry=null;
				Client receiverClient=null;
				ClientUser receiverClientUser = clientUserRepo.findByUserCorrelationId(transactionDetailsObj.getDestinationCorrelationId());
				if (receiverClientUser == null) {
					//receiverClient = clientRepository.findByClientCorrelationId(transactionDetailsObj.getDestinationCorrelationId());
				receiverClient = clientRepository.findByClientCorrelationIdCust(transactionDetailsObj.getDestinationCorrelationId());
					if(receiverClient != null) {
						receiverCountry = countryRepository.findCountryByCountryDialCode(receiverClient.getCountryDialCode());
					}
					
				} else {
					receiverCountry = countryRepository.findCountryByCountryDialCode(receiverClientUser.getCountryDialCode());
				}
				transactionDto.setTnxData(tokenConversion(transactionDetailsObj.getAssetCode().trim(),receiverCountry.getIso4217CurrencyAlphabeticCode().trim(), transactionDetailsObj.getTnxData()));
			}else {
				transactionDto.setTnxData(transactionDetailsObj.getTnxData());
			}
			
			transactionDto.setTnxHash(transactionDetailsObj.getTnxHash());
			transactionDto.setCurrencySymbol(country.getCurrencySymbol());
			transactionDtos.add(transactionDto);
		}

		return transactionDtos;
	}

	/* (non-Javadoc)
	 * @see com.ireslab.sendx.electra.service.ClientProfileMgmtApiService#saveClientSubscriptionPlan(com.ireslab.sendx.electra.model.ClientSubscriptionRequest)
	 */
	@Override
	public ClientSubscriptionResponse saveClientSubscriptionPlan(ClientSubscriptionRequest clientSubscriptionRequest) {
		ClientSubscriptionResponse clientSubscriptionResponse = new ClientSubscriptionResponse();

		ClientSubscriptionDto clientSubscriptionDto = clientSubscriptionRequest.getClientSubscriptionDto();

		Client clientDetails = clientRepository.findByEmail(clientSubscriptionDto.getEmail());

		if (clientDetails == null) {
			LOG.info("Client not found with associated mailId");
			throw new ApiException(HttpStatus.BAD_REQUEST, ResponseCode.FAIL.getCode(), null);
		}

		SubscriptionPlans subscriptionPlans = subsPlanRepository
				.findSubscriptionPlansBySubscriptionId(clientSubscriptionDto.getSubscriptionPlanId());

		ClientSubscription clientSubscription = null;
		Integer availableUser = 0;

		if (clientDetails.getClientSubscriptionId() != null) {
			clientSubscription = clientDetails.getClientSubscriptionId();
			availableUser = subscriptionPlans.getSupportedUsers() + clientSubscription.getAvailableUsers();
		} else {
			clientSubscription = new ClientSubscription();
			availableUser = subscriptionPlans.getSupportedUsers();
		}
	
		String assetToken = "";
		Country country = countryRepository.findCountryByCountryDialCode(clientDetails.getCountryDialCode());
        if(country != null) {
        	assetToken = country.getIso4217CurrencyAlphabeticCode();
        }
		
		Date currentDate = new Date();

		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_MONTH, -1);
		
		calendar.add(Calendar.MONTH, Integer.parseInt(subscriptionPlans.getValidity()));
		Date expiryDate = calendar.getTime();

		clientSubscription.setAvailableUsers(availableUser);
		clientSubscription.setSubscriptionDate(currentDate);
		clientSubscription.setModifiedDate(currentDate);
		clientSubscription.setSubscriptionPlanId(subscriptionPlans);
		clientSubscription.setClientId(clientDetails.getClientId());
		clientSubscription.setExpiryDate(expiryDate);

		
		commonService.saveTransactionDetails(TokenOperation.Payment, TokenActivity.TRANSFER_TOKENS, "stellarRequest",
				"stellarResponse", clientDetails.getClientName(), false, clientDetails.getClientCorrelationId(),
				"Master Account", false, "Master Account", (short) 1, subscriptionPlans.getToken().toString(), assetToken, "", null,
				new Date(), new Date(), "", true, false);

		clientSubscriptionRepository.save(clientSubscription);

		if (clientDetails.getClientSubscriptionId() == null) {
			clientDetails.setPassword(clientSubscriptionDto.getPassword());
			String vToken = UUID.randomUUID().toString();
			clientDetails.setResetToken(vToken);
			clientDetails.setPasswordReset(true);
		}

		clientDetails.setClientSubscriptionId(clientSubscription);
		
		clientDetails.setUsername(clientDetails.getEmailAddress());
		clientRepository.save(clientDetails);

		clientSubscriptionDto.setExpiryDate(expiryDate);
		clientSubscriptionDto.setPlanTitle(subscriptionPlans.getPlanTitle());
		clientSubscriptionDto.setPassword("");

		clientSubscriptionResponse.setClientSubscriptionDto(clientSubscriptionDto);
		clientSubscriptionResponse.setCode(ResponseCode.Success.getCode());
		clientSubscriptionResponse.setStatus(HttpStatus.OK.value());
		clientSubscriptionResponse.setMessage("You have successfully subscribed your plan");

		return clientSubscriptionResponse;
	}

	/* (non-Javadoc)
	 * @see com.ireslab.sendx.electra.service.ClientProfileMgmtApiService#getClientSubscriptionPlan(com.ireslab.sendx.electra.model.ClientSubscriptionRequest)
	 */
	@Override
	public ClientSubscriptionResponse getClientSubscriptionPlan(ClientSubscriptionRequest clientSubscriptionRequest) {
		ClientSubscriptionResponse clientSubscriptionResponse = new ClientSubscriptionResponse();

		ClientSubscriptionDto clientSubscriptionDto = clientSubscriptionRequest.getClientSubscriptionDto();

		Client clientDetails = clientRepository.findByEmail(clientSubscriptionDto.getEmail());

		if (clientDetails == null) {
			throw new ApiException(HttpStatus.BAD_REQUEST, ResponseCode.FAIL.getCode(), null);
		}

		ClientSubscription clientSubscription = clientSubscriptionRepository
				.findByClientId(clientDetails.getClientId());

		if (clientSubscription != null) {

			if (clientSubscription.getSubscriptionPlanId() != null) {
				clientSubscriptionDto.setValidity(clientSubscription.getSubscriptionPlanId().getValidity());
				clientSubscriptionDto.setToken(clientSubscription.getSubscriptionPlanId().getToken());
				clientSubscriptionDto.setSupportedUsers(clientSubscription.getSubscriptionPlanId().getSupportedUsers());
				clientSubscriptionDto.setPlanTitle(clientSubscription.getSubscriptionPlanId().getPlanTitle());
			}

			clientSubscriptionDto.setExpiryDate(clientSubscription.getExpiryDate());
			clientSubscriptionDto.setSubscriptionDate(clientSubscription.getSubscriptionDate());

			clientSubscriptionResponse.setClientSubscriptionDto(clientSubscriptionDto);
			clientSubscriptionResponse.setCode(100);
			clientSubscriptionResponse.setStatus(HttpStatus.OK.value());
			clientSubscriptionResponse.setMessage("SUCCESS");
		} else {
			clientSubscriptionResponse.setClientSubscriptionDto(clientSubscriptionDto);
			clientSubscriptionResponse.setCode(ResponseCode.FAIL.getCode());
			clientSubscriptionResponse.setStatus(HttpStatus.OK.value());
			clientSubscriptionResponse.setMessage("SUCCESS");
		}

		return clientSubscriptionResponse;
	}

	/* (non-Javadoc)
	 * @see com.ireslab.sendx.electra.service.ClientProfileMgmtApiService#isClientOrNot(com.ireslab.sendx.electra.model.ClientSubscriptionRequest)
	 */
	@Override
	public ClientSubscriptionResponse isClientOrNot(ClientSubscriptionRequest clientSubscriptionRequest) {
		ClientSubscriptionResponse clientSubscriptionResponse = new ClientSubscriptionResponse();

		ClientSubscriptionDto clientSubscriptionDto = clientSubscriptionRequest.getClientSubscriptionDto();
		boolean isExpired = false;
		Client clientDetails = clientRepository.findByEmail(clientSubscriptionDto.getEmail());
		if (clientDetails == null) {
			clientSubscriptionDto.setClient(false);
		} else {
			clientSubscriptionDto.setClient(true);
			// Date currentDate = new Date();
			if (clientDetails.getClientSubscriptionId() != null) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				Date currentDate = null;
				Date expiryDate = null;
				try {
					currentDate = sdf.parse(sdf.format(new Date()));
					expiryDate = sdf.parse(sdf.format(clientDetails.getClientSubscriptionId().getExpiryDate()));
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				if (currentDate.compareTo(expiryDate) > 0) {
					isExpired = true;
				}

				clientSubscriptionDto
						.setPlanTitle(clientDetails.getClientSubscriptionId().getSubscriptionPlanId().getPlanTitle());
				clientSubscriptionDto.setExpiryDate(clientDetails.getClientSubscriptionId().getExpiryDate());

			}
			clientSubscriptionDto.setExpired(isExpired);

		}

		clientSubscriptionResponse.setClientSubscriptionDto(clientSubscriptionDto);
		clientSubscriptionResponse.setCode(100);
		clientSubscriptionResponse.setStatus(HttpStatus.OK.value());
		clientSubscriptionResponse.setMessage("SUCCESS");

		return clientSubscriptionResponse;
	}

	/* (non-Javadoc)
	 * @see com.ireslab.sendx.electra.service.ClientProfileMgmtApiService#getSubscriptionPlanList(java.lang.String)
	 */
	@Override
	public SubscriptionPlanResponse getSubscriptionPlanList(String countryId) {
		LOG.info("Request recieved for subscription plan list in service ");
		SubscriptionPlanResponse subscriptionPlanResponse = new SubscriptionPlanResponse();
		List<SubscriptionPlanDto> subscriptionPlanDtoList = new ArrayList<>();

		
		List<SubscriptionPlans> planList = subsPlanRepository.findSubscriptionPlanByCountryIdCust();
		for (SubscriptionPlans subscriptionPlans : planList) {
			SubscriptionPlanDto subscriptionPlanDto = new SubscriptionPlanDto();
			subscriptionPlanDto.setPlanTitle(subscriptionPlans.getPlanTitle());
			subscriptionPlanDto.setSubscriptionId(subscriptionPlans.getSubscriptionId());
			subscriptionPlanDto.setSupportedUsers(subscriptionPlans.getSupportedUsers());
			subscriptionPlanDto.setToken(subscriptionPlans.getToken());
			subscriptionPlanDto.setValidity(subscriptionPlans.getValidity());
			subscriptionPlanDtoList.add(subscriptionPlanDto);
		}

		subscriptionPlanResponse.setSubscriptionPlanDto(subscriptionPlanDtoList);
		subscriptionPlanResponse.setCode(100);
		subscriptionPlanResponse.setMessage("SUCCESS");
		subscriptionPlanResponse.setStatus(HttpStatus.OK.value());
		return subscriptionPlanResponse;
	}

	/* (non-Javadoc)
	 * @see com.ireslab.sendx.electra.service.ClientProfileMgmtApiService#updateClientSubscriptionPlan(com.ireslab.sendx.electra.model.ClientSubscriptionUpdateRequest)
	 */
	@Override
	public ClientSubscriptionUpdateResponse updateClientSubscriptionPlan(
			ClientSubscriptionUpdateRequest clientSubscriptionUpdateRequest) {
		ClientSubscriptionUpdateResponse clientSubscriptionUpdateResponse = new ClientSubscriptionUpdateResponse();
		List<Error> apiErrors = new ArrayList<>();

		Client clientDetails = clientRepository
				.findByClientCorrelationId(clientSubscriptionUpdateRequest.getClientCorrelationId());
		ClientAgentInvitation invitationByEmail = invitationRepository
				.findByEmailAddress(clientSubscriptionUpdateRequest.getEmail());
		ClientAgentInvitation invitationByMobileNo = invitationRepository
				.findByMobileNumber(clientSubscriptionUpdateRequest.getMobileNo());
		LOG.info("updating client subscription plan for correllationId : "
				+ clientSubscriptionUpdateRequest.getClientCorrelationId());
		ClientSubscription clientSubscription = clientSubscriptionRepository
				.findByClientId(clientDetails.getClientId());
		Integer availableUser = 0;

		if (clientSubscription != null) {

			if (invitationByEmail != null && invitationByMobileNo == null && (!invitationByEmail.isRegister())) {
				availableUser = clientSubscription.getAvailableUsers();
				invitationByEmail.setRegister(true);
				invitationRepository.save(invitationByEmail);

				availableUser = availableUser - 1;
				clientSubscription.setAvailableUsers(availableUser);
				clientSubscriptionRepository.save(clientSubscription);

			} else if (invitationByEmail == null && invitationByMobileNo != null
					&& (!invitationByMobileNo.isRegister())) {

				availableUser = clientSubscription.getAvailableUsers();
				invitationByMobileNo.setRegister(true);
				invitationRepository.save(invitationByMobileNo);

				availableUser = availableUser - 1;
				clientSubscription.setAvailableUsers(availableUser);
				clientSubscriptionRepository.save(clientSubscription);

			} else if (invitationByEmail != null && invitationByMobileNo != null) {
				availableUser = clientSubscription.getAvailableUsers();
				invitationByMobileNo.setRegister(true);
				invitationRepository.save(invitationByMobileNo);

				invitationByEmail.setRegister(true);
				invitationRepository.save(invitationByEmail);

				availableUser = availableUser - 1;
				clientSubscription.setAvailableUsers(availableUser);
				clientSubscriptionRepository.save(clientSubscription);

			} else {
				apiErrors.add(new Error(8001, "user", "Already Registered"));
				
			}

		}

		clientSubscriptionUpdateResponse.setErrors(apiErrors);
		clientSubscriptionUpdateResponse.setCode(100);
		clientSubscriptionUpdateResponse.setMessage("SUCCESS");
		clientSubscriptionUpdateResponse.setStatus(HttpStatus.OK.value());

		return clientSubscriptionUpdateResponse;
	}

	/* (non-Javadoc)
	 * @see com.ireslab.sendx.electra.service.ClientProfileMgmtApiService#checkMailidRegistered(com.ireslab.sendx.electra.model.ClientSubscriptionUpdateRequest)
	 */
	@Override
	public ClientSubscriptionUpdateResponse checkMailidRegistered(
			ClientSubscriptionUpdateRequest clientSubscriptionUpdateRequest) {

		ClientSubscriptionUpdateResponse clientSubscriptionUpdateResponse = new ClientSubscriptionUpdateResponse();
		Client client = clientRepository.findBycompanyCode(clientSubscriptionUpdateRequest.getCompanyCode());
		Client clientByMailId = clientRepository.findByEmail(clientSubscriptionUpdateRequest.getEmail());

		boolean isClient = false;
		if (client == null) {
			isClient = true;
			

		}

		if (client != null) {
			if (client.getClientSubscriptionId() != null && client.getClientSubscriptionId().getAvailableUsers() < 1) {

				clientSubscriptionUpdateResponse.setCode(104);
				clientSubscriptionUpdateResponse.setMessage("Asset Token not available.");
				clientSubscriptionUpdateResponse.setStatus(HttpStatus.OK.value());
			} else {

				ClientAgentInvitation invitationByMobile = invitationRepository.findByMobileNumberAndClientId(
						clientSubscriptionUpdateRequest.getMobileNo(), client.getClientId());
				ClientAgentInvitation invitationByEmail = invitationRepository.findByEmailAddressAndClientId(
						clientSubscriptionUpdateRequest.getEmail(), client.getClientId());

				

				if ((invitationByEmail != null && invitationByEmail.isRegister()) && !isClient) {
					clientSubscriptionUpdateResponse.setCode(102);
					clientSubscriptionUpdateResponse.setMessage("Already Exist");
					clientSubscriptionUpdateResponse.setStatus(HttpStatus.OK.value());

				} else if (invitationByEmail == null && !isClient) {
					if (invitationByMobile == null) {

						clientSubscriptionUpdateResponse.setCode(103);
						clientSubscriptionUpdateResponse.setMessage("Not invited user");
						clientSubscriptionUpdateResponse.setStatus(HttpStatus.OK.value());
					} else {

						clientSubscriptionUpdateResponse.setCode(100);
						clientSubscriptionUpdateResponse.setMessage("SUCCESS");
						clientSubscriptionUpdateResponse.setStatus(HttpStatus.OK.value());
					}
					
				} else {
					clientSubscriptionUpdateResponse.setCode(100);
					clientSubscriptionUpdateResponse.setMessage("SUCCESS");
					clientSubscriptionUpdateResponse.setStatus(HttpStatus.OK.value());
				}

			}
		} else {
			clientSubscriptionUpdateResponse.setCode(100);
			clientSubscriptionUpdateResponse.setMessage("SUCCESS");
			clientSubscriptionUpdateResponse.setStatus(HttpStatus.OK.value());
		}

		if (clientByMailId != null) {

			clientSubscriptionUpdateResponse.setCode(102);
			clientSubscriptionUpdateResponse.setMessage("Already Exist");
			clientSubscriptionUpdateResponse.setStatus(HttpStatus.OK.value());

		}
		return clientSubscriptionUpdateResponse;

	}

	/* (non-Javadoc)
	 * @see com.ireslab.sendx.electra.service.ClientProfileMgmtApiService#downloadExcelAndCsv(com.ireslab.sendx.electra.model.FilterLedgerRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	public FilterLedgerResponse downloadExcelAndCsv(FilterLedgerRequest filterLedgerRequest,
			HttpServletResponse response) {

		FilterLedgerResponse filterLedgerResponse = new FilterLedgerResponse();
		if (filterLedgerRequest.getFilterBy() != null && filterLedgerRequest.getFilterBy().equals("date")) {
			LOG.info("SERCHING BY DATE....!!");

			String fromDate = filterLedgerRequest.getFromDate() + " 00:00:00";
			String toDate = filterLedgerRequest.getToDate() + " 23:59:59";
			

			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

			Date fromDateObj = null;
			Date toDateObj = null;
			try {
				fromDateObj = simpleDateFormat.parse(fromDate);
				toDateObj = simpleDateFormat.parse(toDate);
			} catch (ParseException e) {

				e.printStackTrace();
			}

			LOG.info("DATE ... FROM :" + fromDateObj + " TO :" + toDateObj);

			Client client = clientRepository.findByClientCorrelationId(filterLedgerRequest.getClientCorrelationId());

			Country country = countryRepository.findCountryByCountryDialCode(client.getCountryDialCode());

			List<ClientUser> findByClient = clientUserRepo.findByClient(client);

			List<String> listString = new ArrayList<>();

			for (ClientUser clientUser : findByClient) {
				listString.add(clientUser.getUserCorrelationId());
			}
			listString.add(filterLedgerRequest.getClientCorrelationId());

			List<TransactionDetail> findBySourceCorrelationIdOrDestinationCorrelationId = txnDetailRepo
					.findTransactionByTransactionDateCustom(fromDateObj, toDateObj, listString, filterLedgerRequest.getOffline());

			List<TransactionDto> transactionDtoList = getTransactionDtoList(
					findBySourceCorrelationIdOrDestinationCorrelationId, country,filterLedgerRequest);

			filterLedgerResponse.setTransactionList(transactionDtoList);
			filterLedgerResponse.setCurrencySymbol(country.getCurrencySymbol());

		} else if (filterLedgerRequest.getFilterBy() != null && filterLedgerRequest.getFilterBy().equals("name")) {
			LOG.info("SERCHING BY NAME....!!");

			Client client = clientRepository.findByClientCorrelationId(filterLedgerRequest.getClientCorrelationId());

			Country country = countryRepository.findCountryByCountryDialCode(client.getCountryDialCode());

			List<ClientUser> findByClient = clientUserRepo.findByClient(client);

			List<String> listString = new ArrayList<>();

			for (ClientUser clientUser : findByClient) {
				listString.add(clientUser.getUserCorrelationId());
			}
			listString.add(filterLedgerRequest.getClientCorrelationId());

			List<TransactionDetail> findBySourceCorrelationIdOrDestinationCorrelationId = txnDetailRepo
					.findByAccountNameContainingCustom(filterLedgerRequest.getFilterData(),
							filterLedgerRequest.getFilterData(), listString, filterLedgerRequest.getOffline());

			List<TransactionDto> transactionDtoList = getTransactionDtoList(
					findBySourceCorrelationIdOrDestinationCorrelationId, country,filterLedgerRequest);

			filterLedgerResponse.setTransactionList(transactionDtoList);
			filterLedgerResponse.setCurrencySymbol(country.getCurrencySymbol());

		} else if (filterLedgerRequest.getFilterBy() != null && filterLedgerRequest.getFilterBy().equals("both")) {
			LOG.info("SEARCHING BY NAME AND DATE...!!");

			String fromDate = filterLedgerRequest.getFromDate() + " 00:00:00";
			String toDate = filterLedgerRequest.getToDate() + " 23:59:59";

			

			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

			Date fromDateObj = null;
			Date toDateObj = null;
			String filterData = filterLedgerRequest.getFilterData();
			try {
				fromDateObj = simpleDateFormat.parse(fromDate);
				toDateObj = simpleDateFormat.parse(toDate);
			} catch (ParseException e) {

				e.printStackTrace();
			}

			Client client = clientRepository.findByClientCorrelationId(filterLedgerRequest.getClientCorrelationId());

			Country country = countryRepository.findCountryByCountryDialCode(client.getCountryDialCode());

			List<ClientUser> findByClient = clientUserRepo.findByClient(client);
			LOG.debug("Client user list size " + findByClient.size());

			List<String> listString = new ArrayList<>();

			for (ClientUser clientUser : findByClient) {
				listString.add(clientUser.getUserCorrelationId());
			}
			listString.add(filterLedgerRequest.getClientCorrelationId());

			List<TransactionDetail> findBySourceCorrelationIdOrDestinationCorrelationId = txnDetailRepo
					.findTransactionByTransactionDateAndNameCustom(fromDateObj, toDateObj, filterData, filterData,
							listString, filterLedgerRequest.getOffline());

			List<TransactionDto> transactionDtoList = getTransactionDtoList(
					findBySourceCorrelationIdOrDestinationCorrelationId, country,filterLedgerRequest);

			filterLedgerResponse.setTransactionList(transactionDtoList);
			filterLedgerResponse.setCurrencySymbol(country.getCurrencySymbol());

		}

		else {

			Client client = clientRepository.findByClientCorrelationId(filterLedgerRequest.getClientCorrelationId());

			Country country = countryRepository.findCountryByCountryDialCode(client.getCountryDialCode());

			List<ClientUser> findByClient = clientUserRepo.findByClient(client);
			LOG.debug("Client user list size " + findByClient.size());

			List<String> listString = new ArrayList<>();

			for (ClientUser clientUser : findByClient) {
				listString.add(clientUser.getUserCorrelationId());
			}
			listString.add(filterLedgerRequest.getClientCorrelationId());

			List<TransactionDetail> findBySourceCorrelationIdOrDestinationCorrelationId = txnDetailRepo
					.customTransactionDetail(listString, filterLedgerRequest.getOffline());

			List<TransactionDto> transactionDtoList = getTransactionDtoList(
					findBySourceCorrelationIdOrDestinationCorrelationId, country,filterLedgerRequest);

			filterLedgerResponse.setTransactionList(transactionDtoList);
			filterLedgerResponse.setCurrencySymbol(country.getCurrencySymbol());

		}

		return filterLedgerResponse;

	}

	/* (non-Javadoc)
	 * @see com.ireslab.sendx.electra.service.ClientProfileMgmtApiService#getClientProfile(java.lang.String)
	 */
	@Override
	public UserProfileResponse getClientProfile(String clientCorrelationId) {
		UserProfileResponse userProfileResponse = null;

		
		Client client = clientRepository.findByClientCorrelationIdCust(clientCorrelationId);

		// Checking if user not exists
		if (client == null) {
			throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, ResponseCode.FAIL.getCode(), null);
		}

		if (!client.getStatus().equals(Status.ACTIVE.toString())) {

			userProfileResponse = new UserProfileResponse();
			userProfileResponse.setStatus(HttpStatus.OK.value());
			userProfileResponse.setCode(ResponseCode.Success.getCode());
			userProfileResponse.setUserProfile(new UserProfile().setStatus(Status.valueOf(client.getStatus())));
			return userProfileResponse;
		}

		StellarTransactionConfigDto stellarConfig = new StellarTransactionConfigDto();
		stellarConfig.setIsTestNetwork(
				commonService.getClientDetailsByCorrelationId(clientCorrelationId).isTestnetAccount());
		stellarConfig.setReceiverAccount(
				new StellarTransactionConfigDto.ReceiverAccount().setPublicKey(client.getBaseTxnAccountPublicKey()));

		// Getting consolidated account balance from stellar
		List<AssetDetails> accountBalances = stellarTransactionManager.getAccountBalance(stellarConfig);

		UserProfile userProfile = new UserProfile();
		userProfile.setRegistered(true);
		userProfile.setStatus(Status.ACTIVE);
		userProfile.setAssetDetails(accountBalances);
		userProfile.setEkycEkybApproved(client.isEkycEkybApproved());
		
		Country country = countryRepository.findCountryByCountryDialCode(client.getCountryDialCode());
		userProfile.setCountryDialCode(country.getCountryDialCode());
		userProfile.setCurrencySymbol(country.getCurrencySymbol());
		userProfile.setIso4217CurrencyAlphabeticCode(country.getIso4217CurrencyAlphabeticCode());
		userProfileResponse = new UserProfileResponse();
		userProfileResponse.setStatus(HttpStatus.OK.value());
		userProfileResponse.setCode(ResponseCode.Success.getCode());
		userProfileResponse.setUserProfile(userProfile);

		return userProfileResponse;
	}

	/**
	 * use to trulioo verification.
	 * 
	 * @param clientProfile
	 * @param country
	 * @return
	 */
	public VerifyRequest getTruliooVerifyRequest(ClientProfile clientProfile, Country country) {

		DataFields dataFields = new DataFields();

		Date date = null;
		try {
			date = new SimpleDateFormat("dd/MM/yyyy").parse(clientProfile.getDob());
		} catch (ParseException e) {
			
			e.printStackTrace();
		}
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH) + 1;
		int day = cal.get(Calendar.DAY_OF_MONTH);

		

		PersonInfo personInfo = new PersonInfo();
		personInfo.setFirstGivenName(clientProfile.getFirstName());
		personInfo.setFirstSurName(clientProfile.getLastName());
		personInfo.setGender(String.valueOf(clientProfile.getGender().charAt(0)));
		personInfo.setDayOfBirth(day);
		personInfo.setMonthOfBirth(month);
		personInfo.setYearOfBirth(year);
		dataFields.setPersonInfo(personInfo);

		Communication communication = new Communication();
		communication.setMobileNumber(String.valueOf(clientProfile.getContactNumber1()));
		communication.setEmailAddress(clientProfile.getEmailAddress());
		dataFields.setCommunication(communication);

		Location location = new Location();
		location.setCounty(country.getCountryName());
		

		LocationAdditionalFields locationAdditionField = new LocationAdditionalFields();
		locationAdditionField.setAddress1(clientProfile.getResidentialAddress());
		location.setAdditionalFields(locationAdditionField);
		dataFields.setLocation(location);

		List<NationalId> nationIdList = new ArrayList<NationalId>();

		Passport passport = new Passport();

		if (clientProfile.getScanDocumentType().equalsIgnoreCase("Passport")) {
			passport.setNumber(clientProfile.getScanDocumentId());
			passport.setMrz1("00000");
			passport.setMrz2("00000");
			dataFields.setPassport(passport);

			Document document = new Document();
			document.setDocumentFrontImage(clientProfile.getScanDocumentFrontPage());
			document.setDocumentBackImage(clientProfile.getScanDocumentBackPage());
			document.setDocumentType("Passport");
			dataFields.setDocument(document);
		} else if (clientProfile.getScanDocumentType().equalsIgnoreCase("NRIC/ ID")) {
			NationalId nationalIds = new NationalId();
			nationalIds.setNumber(clientProfile.getScanDocumentId());
			nationalIds.setType(country.getNationalId());
			nationIdList.add(nationalIds);
			dataFields.setNationalIds(nationIdList);

			Document document = new Document();
			document.setDocumentFrontImage(clientProfile.getScanDocumentFrontPage());
			document.setDocumentBackImage(clientProfile.getScanDocumentBackPage());
			document.setDocumentType("IdentityCard");
			dataFields.setDocument(document);
		} else {
			DriverLicence driverLicence = new DriverLicence();
			driverLicence.setNumber(clientProfile.getScanDocumentId());
			dataFields.setDriverLicence(driverLicence);

			Document document = new Document();
			document.setDocumentFrontImage(clientProfile.getScanDocumentFrontPage());
			document.setDocumentBackImage(clientProfile.getScanDocumentBackPage());
			document.setDocumentType("DrivingLicence");
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
	 * use to exchange rate.
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
