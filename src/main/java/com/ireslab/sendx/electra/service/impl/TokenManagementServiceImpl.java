package com.ireslab.sendx.electra.service.impl;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.stellar.sdk.responses.SubmitTransactionResponse;

import com.ireslab.sendx.electra.ElectraConfig;
import com.ireslab.sendx.electra.dto.CashOutDto;
import com.ireslab.sendx.electra.dto.StellarTransactionConfigDto;
import com.ireslab.sendx.electra.dto.StellarTransactionConfigDto.IssuingAccount;
import com.ireslab.sendx.electra.dto.StellarTransactionConfigDto.ReceiverAccount;
import com.ireslab.sendx.electra.dto.StellarTransactionConfigDto.SenderAccount;
import com.ireslab.sendx.electra.dto.TransactionDto;
import com.ireslab.sendx.electra.entity.CashOut;
import com.ireslab.sendx.electra.entity.Client;
import com.ireslab.sendx.electra.entity.ClientUser;
import com.ireslab.sendx.electra.entity.ClientUserToken;
import com.ireslab.sendx.electra.entity.Country;
import com.ireslab.sendx.electra.entity.Exchange;
import com.ireslab.sendx.electra.entity.MasterAccounts;
import com.ireslab.sendx.electra.entity.TransactionDetail;
import com.ireslab.sendx.electra.exceptions.ApiException;
import com.ireslab.sendx.electra.model.AssetDetails;
import com.ireslab.sendx.electra.model.Error;
import com.ireslab.sendx.electra.model.TokenLifecycleManagementRequest;
import com.ireslab.sendx.electra.model.TokenLifecycleManagementResponse;
import com.ireslab.sendx.electra.properties.MessagesProperties;
import com.ireslab.sendx.electra.repository.CashOutRepository;
import com.ireslab.sendx.electra.repository.ClientRepository;
import com.ireslab.sendx.electra.repository.ClientUserRepository;
import com.ireslab.sendx.electra.repository.ClientUserTokenRepository;
import com.ireslab.sendx.electra.repository.CountryRepository;
import com.ireslab.sendx.electra.repository.ExchangeRepository;
import com.ireslab.sendx.electra.repository.MasterAccountRepository;
import com.ireslab.sendx.electra.service.CommonService;
import com.ireslab.sendx.electra.service.TokenManagementService;
import com.ireslab.sendx.electra.stellar.StellarTransactionManager;
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
public class TokenManagementServiceImpl implements TokenManagementService {

	private static final Logger LOG = LoggerFactory.getLogger(TokenManagementServiceImpl.class);
	@Autowired
	private ExchangeRepository exchangeRepository;
	@Autowired
	private ElectraConfig electraConfig;

	@Autowired
	private StellarTransactionManager stellarTxnManager;

	@Autowired
	private ClientUserTokenRepository clientUserTokenRepo;

	@Autowired
	private CommonService commonService;

	@Autowired
	private MessagesProperties messageProperties;

	@Autowired
	private ClientUserRepository clientUserRepository;

	@Autowired
	private MasterAccountRepository masterAccountRepo;

	@Autowired
	private CountryRepository countryRepository;

	@Autowired
	private ClientRepository clientRepository;
	
	@Autowired
	private CashOutRepository cashOutRepository;
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ireslab.sendx.electra.service.TokenManagementService#loadTokens(com.
	 * ireslab.sendx.electra.model.TokenLifecycleManagementRequest)
	 */

	@Override
	@Transactional(rollbackOn = Exception.class)
	public TokenLifecycleManagementResponse loadTokens(TokenLifecycleManagementRequest tokenManagementRequest) {

		ClientUser userDetails = null;
		TokenLifecycleManagementResponse tokenLifecycleManagementResponse = null;
		Integer code = null;

		LOG.debug("Getting UserDetails for userCorrelationId - " + tokenManagementRequest.getUserCorrelationId()
				+ " & TokenDetails for tokenCorrelationId - " + tokenManagementRequest.getTokenCorrelationId());

		LOG.debug("Fetching client using clientCorrelationId - " + tokenManagementRequest.getClientId());
		
		boolean isCorrelationIdIsOfClient = false;
		Client clientInfo = commonService
				.getClientDetailsByCorrelationId(tokenManagementRequest.getUserCorrelationId());
		/*ClientUser userInfo = clientUserRepository
				.findByUserCorrelationId(tokenManagementRequest.getUserCorrelationId());*/
	

		if (clientInfo != null) {
			isCorrelationIdIsOfClient = true;
		}

		if (isCorrelationIdIsOfClient) {
			System.out.println("Loding token in client account.");

			if (clientInfo == null) {
				throw new ApiException(HttpStatus.PRECONDITION_FAILED,
						Arrays.asList(new Error(ResponseCode.TOKEN_MANAGEMENT_USER_NOT_FOUND.getCode(),
								messageProperties.tokenManagementUserNotFound)));
			}

			if (!(clientInfo.getStatus().equals(Status.ACTIVE.toString()))) {
				throw new ApiException(HttpStatus.PRECONDITION_FAILED,
						Arrays.asList(new Error(ResponseCode.TOKEN_MANAGEMENT_USER_TOKEN_LOCK.getCode(),
								messageProperties.tokenManagementUserLock)));
			}

			StellarTransactionConfigDto stellarTxnConfig = new StellarTransactionConfigDto();
			if (clientInfo.getCountryDialCode() == null) {
				throw new ApiException(HttpStatus.PRECONDITION_FAILED,
						Arrays.asList(new Error(ResponseCode.COUNTRY_DIAL_CODE_NOT_FOUND.getCode(),
								messageProperties.userClientCountryDialCodeNotFound)));
			}

			Country country = countryRepository.findCountryByCountryDialCode(clientInfo.getCountryDialCode());

			stellarTxnConfig.setAssetCode(country.getIso4217CurrencyAlphabeticCode());
			stellarTxnConfig.setIsTestNetwork(clientInfo.isTestnetAccount());
			stellarTxnConfig.setNoOfTokens(tokenManagementRequest.getNoOfTokens());
			stellarTxnConfig.setAssetLimit(String.valueOf(StellarConstants.ASSET_TRUSTLINE_LIMIT));

			SenderAccount senderAccount = null;
			IssuingAccount issuingAccount = null;

			List<MasterAccounts> masterAccountsList = (List<MasterAccounts>) masterAccountRepo.findAll();
			MasterAccounts masterAccounts = masterAccountsList.get(0);

			issuingAccount = new IssuingAccount().setPublicKey(masterAccounts.getIssuingAccountPublicKey())
					.setSecretSeed(masterAccounts.getIssuingAccountSecretKey());

			stellarTxnConfig.setIssuingAccount(issuingAccount);
			stellarTxnConfig
					.setReceiverAccount(new ReceiverAccount().setSecretSeed(clientInfo.getBaseTxnAccountSecretKey())
							.setPublicKey(clientInfo.getBaseTxnAccountPublicKey())); // Setting client as Receiver
																						// Account details.

			LOG.info("Creating Trustline for userCorrelationId - " + tokenManagementRequest.getUserCorrelationId());
			stellarTxnManager.createTrustLine(stellarTxnConfig);

			
			senderAccount = new SenderAccount().setPublicKey(masterAccounts.getBaseTxnAccountPublicKey())
					.setSecretSeed(masterAccounts.getBaseTxnAccountSecretKey());
			stellarTxnConfig.setSenderAccount(senderAccount);

			// Transfer Tokens from Base Txn Account to Client's account
			try {
				SubmitTransactionResponse transactionXdr = stellarTxnManager.transferToken(stellarTxnConfig);
				if (transactionXdr.isSuccess()) {
					code = ResponseCode.Success.getCode();
				} else {
					code = ResponseCode.TOKEN_MANAGEMENT_PAYMENT_TRANSACTION_FAILURE.getCode();
				}

				LOG.debug("Token transfer transaction XDR - " + transactionXdr);

				

				// Amount is
				commonService.saveTransactionDetails(TokenOperation.Payment, TokenActivity.LOAD_TOKENS,
						"stellarRequest", "stellarResponse", masterAccounts.getAccountName(), false,
						masterAccounts.getAccountName(), clientInfo.getClientName(), true,
						clientInfo.getClientCorrelationId(), (short) 1, stellarTxnConfig.getNoOfTokens(),
						stellarTxnConfig.getAssetCode(), transactionXdr.getHash(), null, new Date(), new Date(),
						tokenManagementRequest.getTransactionPurpose(), false, false);

			} catch (ApiException exp) {
				
				commonService.saveTransactionDetails(TokenOperation.Payment, TokenActivity.LOAD_TOKENS,
						"stellarRequest", "stellarResponse", masterAccounts.getAccountName(), false,
						masterAccounts.getAccountName(), clientInfo.getClientName(), true,
						clientInfo.getClientCorrelationId(), (short) 3, stellarTxnConfig.getNoOfTokens(),
						stellarTxnConfig.getAssetCode(), null, null, new Date(), new Date(),
						tokenManagementRequest.getTransactionPurpose(), false, false);
				code = ResponseCode.TOKEN_MANAGEMENT_PAYMENT_TRANSACTION_FAILURE.getCode();

				throw exp;

			} catch (Exception exp) {

				LOG.error("Error while Transferring token: " + ExceptionUtils.getStackTrace(exp));

				
				commonService.saveTransactionDetails(TokenOperation.Payment, TokenActivity.LOAD_TOKENS,
						"stellarRequest", "stellarResponse", masterAccounts.getAccountName(), false,
						masterAccounts.getAccountName(), clientInfo.getClientName(), true,
						clientInfo.getClientCorrelationId(), (short) 2, stellarTxnConfig.getNoOfTokens(),
						stellarTxnConfig.getAssetCode(), null, null, new Date(), new Date(),
						tokenManagementRequest.getTransactionPurpose(), false, false);

				code = ResponseCode.TOKEN_MANAGEMENT_PAYMENT_TRANSACTION_FAILURE.getCode();
				throw new ApiException(HttpStatus.OK,
						Arrays.asList(new Error(ResponseCode.TOKEN_MANAGEMENT_PAYMENT_TRANSACTION_FAILURE.getCode(),
								messageProperties.tokenManagementPaymentTransactionFailure)));
			}

			tokenLifecycleManagementResponse = new TokenLifecycleManagementResponse();
			tokenLifecycleManagementResponse.setStatus(HttpStatus.OK.value());
			tokenLifecycleManagementResponse.setCode(code);

			// Getting updated account balance from stellar
			List<AssetDetails> accountBalances = stellarTxnManager.getAccountBalance(stellarTxnConfig);

			for (AssetDetails accountBalance : accountBalances) {
				if (accountBalance.getAssetCode().equalsIgnoreCase(stellarTxnConfig.getAssetCode())) {
					tokenLifecycleManagementResponse.setAccountBalance(accountBalance.getAssetQuantity());
				}
			}


		} else {
			userDetails = clientUserRepository.findByUserCorrelationId(tokenManagementRequest.getUserCorrelationId());
			if (userDetails == null) {
				throw new ApiException(HttpStatus.PRECONDITION_FAILED,
						Arrays.asList(new Error(ResponseCode.TOKEN_MANAGEMENT_USER_NOT_FOUND.getCode(),
								messageProperties.tokenManagementUserNotFound)));
			}

			if (!(userDetails.getStatus().equals(Status.ACTIVE))) {
				throw new ApiException(HttpStatus.PRECONDITION_FAILED,
						Arrays.asList(new Error(ResponseCode.TOKEN_MANAGEMENT_USER_TOKEN_LOCK.getCode(),
								messageProperties.tokenManagementUserLock)));
			}

			StellarTransactionConfigDto stellarTxnConfig = new StellarTransactionConfigDto();

			

			if (userDetails.getCountryDialCode() == null) {
				throw new ApiException(HttpStatus.PRECONDITION_FAILED,
						Arrays.asList(new Error(ResponseCode.COUNTRY_DIAL_CODE_NOT_FOUND.getCode(),
								messageProperties.userClientCountryDialCodeNotFound)));
			}

			Country country = countryRepository.findCountryByCountryDialCode(userDetails.getCountryDialCode());

			stellarTxnConfig.setAssetCode(country.getIso4217CurrencyAlphabeticCode());
			stellarTxnConfig.setIsTestNetwork(userDetails.getClient().isTestnetAccount());
			stellarTxnConfig.setNoOfTokens(tokenManagementRequest.getNoOfTokens());
			stellarTxnConfig.setAssetLimit(String.valueOf(StellarConstants.ASSET_TRUSTLINE_LIMIT));

			SenderAccount senderAccount = null;
			IssuingAccount issuingAccount = null;

			

			List<MasterAccounts> masterAccountsList = (List<MasterAccounts>) masterAccountRepo.findAll();
			MasterAccounts masterAccounts = masterAccountsList.get(0);

			issuingAccount = new IssuingAccount().setPublicKey(masterAccounts.getIssuingAccountPublicKey())
					.setSecretSeed(masterAccounts.getIssuingAccountSecretKey());

			stellarTxnConfig.setIssuingAccount(issuingAccount);
			stellarTxnConfig.setReceiverAccount(new ReceiverAccount().setSecretSeed(userDetails.getAccountSecretKey())
					.setPublicKey(userDetails.getAccountPublicKey())); // Setting Receiver Account details.

			LOG.info("Creating Trustline for userCorrelationId - " + tokenManagementRequest.getUserCorrelationId());
			stellarTxnManager.createTrustLine(stellarTxnConfig);

			
			senderAccount = new SenderAccount().setPublicKey(masterAccounts.getBaseTxnAccountPublicKey())
					.setSecretSeed(masterAccounts.getBaseTxnAccountSecretKey());
			stellarTxnConfig.setSenderAccount(senderAccount);

			// Transfer Tokens from Base Txn Account to User's account
			try {
				SubmitTransactionResponse transactionXdr = stellarTxnManager.transferToken(stellarTxnConfig);
				if (transactionXdr.isSuccess()) {
					code = ResponseCode.Success.getCode();
				} else {
					code = ResponseCode.TOKEN_MANAGEMENT_PAYMENT_TRANSACTION_FAILURE.getCode();
				}

				LOG.debug("Token transfer transaction XDR - " + transactionXdr);
				

				commonService.saveTransactionDetails(TokenOperation.Payment, TokenActivity.LOAD_TOKENS,
						"stellarRequest", "stellarResponse", masterAccounts.getAccountName(), false,
						masterAccounts.getAccountName(), userDetails.getFirstName() + " " + userDetails.getLastName(),
						false, userDetails.getUserCorrelationId(), (short) 1, stellarTxnConfig.getNoOfTokens(),
						stellarTxnConfig.getAssetCode(), transactionXdr.getHash(), null, new Date(), new Date(),
						tokenManagementRequest.getTransactionPurpose(), false, false);

			} catch (ApiException exp) {
				

				commonService.saveTransactionDetails(TokenOperation.Payment, TokenActivity.LOAD_TOKENS,
						"stellarRequest", "stellarResponse", masterAccounts.getAccountName(), false,
						masterAccounts.getAccountName(), userDetails.getFirstName() + " " + userDetails.getLastName(),
						false, userDetails.getUserCorrelationId(), (short) 3, stellarTxnConfig.getNoOfTokens(),
						stellarTxnConfig.getAssetCode(), null, null, new Date(), new Date(),
						tokenManagementRequest.getTransactionPurpose(), false, false);

				code = ResponseCode.TOKEN_MANAGEMENT_PAYMENT_TRANSACTION_FAILURE.getCode();

				throw exp;

			} catch (Exception exp) {

				LOG.error("Error while Transferring token: " + ExceptionUtils.getStackTrace(exp));

				commonService.saveTransactionDetails(TokenOperation.Payment, TokenActivity.LOAD_TOKENS,
						"stellarRequest", "stellarResponse", masterAccounts.getAccountName(), false,
						masterAccounts.getAccountName(), userDetails.getFirstName() + " " + userDetails.getLastName(),
						false, userDetails.getUserCorrelationId(), (short) 2, stellarTxnConfig.getNoOfTokens(),
						stellarTxnConfig.getAssetCode(), null, null, new Date(), new Date(),
						tokenManagementRequest.getTransactionPurpose(), false, false);

				code = ResponseCode.TOKEN_MANAGEMENT_PAYMENT_TRANSACTION_FAILURE.getCode();
				throw new ApiException(HttpStatus.OK,
						Arrays.asList(new Error(ResponseCode.TOKEN_MANAGEMENT_PAYMENT_TRANSACTION_FAILURE.getCode(),
								messageProperties.tokenManagementPaymentTransactionFailure)));
			}

			tokenLifecycleManagementResponse = new TokenLifecycleManagementResponse();
			tokenLifecycleManagementResponse.setStatus(HttpStatus.OK.value());
			tokenLifecycleManagementResponse.setCode(code);

			// Getting updated account balance from stellar
			List<AssetDetails> accountBalances = stellarTxnManager.getAccountBalance(stellarTxnConfig);

			for (AssetDetails accountBalance : accountBalances) {
				if (accountBalance.getAssetCode().equalsIgnoreCase(stellarTxnConfig.getAssetCode())) {
					tokenLifecycleManagementResponse.setAccountBalance(accountBalance.getAssetQuantity());
				}
			}
		}

		return tokenLifecycleManagementResponse;
	}

	/* (non-Javadoc)
	 * @see com.ireslab.sendx.electra.service.TokenManagementService#transferTokens(com.ireslab.sendx.electra.model.TokenLifecycleManagementRequest)
	 */
	@Override
	public TokenLifecycleManagementResponse transferTokens(TokenLifecycleManagementRequest tokenManagementRequest) {

		ClientUser senderDetails = null;
		ClientUser receiverDetails = null;
		TokenLifecycleManagementResponse tokenLifecycleManagementResponse = null;
		TransactionDetail transactionDetail = null;

		LOG.debug("Request recieved for transfer token : " + tokenManagementRequest.toString());


		boolean senderIsClient = false;
		boolean receiverIsClient = false;

		String senderName = null;
		String senderCorrelationId = null;
		String receiverName = null;
		String receiverCorrelationId = null;

		Client clientSender = clientRepository.findByClientCorrelationId(tokenManagementRequest.getUserCorrelationId());
		Client clientReceiver = clientRepository
				.findByClientCorrelationId(tokenManagementRequest.getBeneficiaryCorrelationId());
		if (clientSender != null) {
			senderIsClient = true;
		}

		if (clientReceiver != null) {
			receiverIsClient = true;
		}

		if (clientSender != null || clientReceiver != null) {
			LOG.info("Implement Client Transfer...!!!");
			if (senderIsClient && !receiverIsClient) {
				Client senderClientAccount = clientRepository
						.findByClientCorrelationId(tokenManagementRequest.getUserCorrelationId());
				senderDetails = new ClientUser();
				senderDetails.setCountryDialCode(senderClientAccount.getCountryDialCode());
				senderDetails.setStatus(Enum.valueOf(Status.class, senderClientAccount.getStatus()));
				senderDetails.setAccountSecretKey(senderClientAccount.getBaseTxnAccountSecretKey());
				senderDetails.setAccountPublicKey(senderClientAccount.getBaseTxnAccountPublicKey());

				receiverDetails = clientUserRepository
						.findByUserCorrelationId(tokenManagementRequest.getBeneficiaryCorrelationId());
				

				
				// sender details
				senderName = senderClientAccount.getClientName();
				senderCorrelationId = senderClientAccount.getClientCorrelationId();
				// receiver details
				receiverName = receiverDetails.getFirstName() + " " + receiverDetails.getLastName();
				receiverCorrelationId = receiverDetails.getUserCorrelationId();

			} else if (receiverIsClient && !senderIsClient) {
				Client receiverClientAccount = clientRepository
						.findByClientCorrelationId(tokenManagementRequest.getBeneficiaryCorrelationId());

				receiverDetails = new ClientUser();
				receiverDetails.setCountryDialCode(receiverClientAccount.getCountryDialCode());
				receiverDetails.setStatus(Enum.valueOf(Status.class, receiverClientAccount.getStatus()));
				receiverDetails.setAccountSecretKey(receiverClientAccount.getBaseTxnAccountSecretKey());
				receiverDetails.setAccountPublicKey(receiverClientAccount.getBaseTxnAccountPublicKey());

				senderDetails = clientUserRepository
						.findByUserCorrelationId(tokenManagementRequest.getUserCorrelationId());

				
				// sender details
				senderName = senderDetails.getFirstName() + " " + senderDetails.getLastName();
				senderCorrelationId = senderDetails.getUserCorrelationId();
				// receiver details
				receiverName = receiverClientAccount.getClientName();
				receiverCorrelationId = receiverClientAccount.getClientCorrelationId();

			} else {

				Client senderClientAccount = clientRepository
						.findByClientCorrelationId(tokenManagementRequest.getUserCorrelationId());
				senderDetails = new ClientUser();
				senderDetails.setCountryDialCode(senderClientAccount.getCountryDialCode());
				senderDetails.setStatus(Enum.valueOf(Status.class, senderClientAccount.getStatus()));
				senderDetails.setAccountSecretKey(senderClientAccount.getBaseTxnAccountSecretKey());
				senderDetails.setAccountPublicKey(senderClientAccount.getBaseTxnAccountPublicKey());

				Client receiverClientAccount = clientRepository
						.findByClientCorrelationId(tokenManagementRequest.getBeneficiaryCorrelationId());

				receiverDetails = new ClientUser();
				receiverDetails.setCountryDialCode(receiverClientAccount.getCountryDialCode());
				receiverDetails.setStatus(Enum.valueOf(Status.class, receiverClientAccount.getStatus()));
				receiverDetails.setAccountSecretKey(receiverClientAccount.getBaseTxnAccountSecretKey());
				receiverDetails.setAccountPublicKey(receiverClientAccount.getBaseTxnAccountPublicKey());

				
				// sender details
				senderName = senderClientAccount.getClientName();
				senderCorrelationId = senderClientAccount.getClientCorrelationId();
				// receiver details
				receiverName = receiverClientAccount.getClientName();
				receiverCorrelationId = receiverClientAccount.getClientCorrelationId();

			}

			

			if (senderDetails.getCountryDialCode() == null) {
				throw new ApiException(HttpStatus.PRECONDITION_FAILED,
						Arrays.asList(new Error(ResponseCode.COUNTRY_DIAL_CODE_NOT_FOUND.getCode(),
								messageProperties.userClientCountryDialCodeNotFound)));
			}

			Country country = countryRepository.findCountryByCountryDialCode(senderDetails.getCountryDialCode());

			StellarTransactionConfigDto stellarTxnConfig = new StellarTransactionConfigDto();

			
			stellarTxnConfig.setAssetCode(country.getIso4217CurrencyAlphabeticCode());
			stellarTxnConfig.setIsTestNetwork(electraConfig.isTestnetAccount);
			stellarTxnConfig.setNoOfTokens(tokenManagementRequest.getNoOfTokens());
			stellarTxnConfig.setAssetLimit(String.valueOf(StellarConstants.ASSET_TRUSTLINE_LIMIT));

			

			if ((senderDetails.getStatus().equals(Status.TERMINATED))) {
				throw new ApiException(HttpStatus.PRECONDITION_FAILED,
						Arrays.asList(new Error(ResponseCode.TOKEN_MANAGEMENT_USER_TOKEN_LOCK.getCode(),
								messageProperties.tokenManagementUserLock)));
			}

			IssuingAccount issuingAccount = null;

			List<MasterAccounts> masterAccountsList = (List<MasterAccounts>) masterAccountRepo.findAll();
			MasterAccounts masterAccounts = masterAccountsList.get(0);
			issuingAccount = new IssuingAccount().setPublicKey(masterAccounts.getIssuingAccountPublicKey())
					.setSecretSeed(masterAccounts.getIssuingAccountSecretKey());

			stellarTxnConfig.setIssuingAccount(issuingAccount);
			stellarTxnConfig.setReceiverAccount(new ReceiverAccount().setSecretSeed(senderDetails.getAccountSecretKey())
					.setPublicKey(senderDetails.getAccountPublicKey()));

			

			if ((receiverDetails.getStatus().equals(Status.TERMINATED))) {
				throw new ApiException(HttpStatus.PRECONDITION_FAILED,
						Arrays.asList(new Error(ResponseCode.TOKEN_MANAGEMENT_USER_TOKEN_LOCK.getCode(),
								messageProperties.tokenManagementUserLock)));
			}

			stellarTxnConfig
					.setReceiverAccount(new ReceiverAccount().setSecretSeed(receiverDetails.getAccountSecretKey())
							.setPublicKey(receiverDetails.getAccountPublicKey()));

			

			LOG.debug("Creating Trustline for RecieverCorrelationId - "
					+ tokenManagementRequest.getBeneficiaryCorrelationId());
			Country receiverCountry = countryRepository.findCountryByCountryDialCode(receiverDetails.getCountryDialCode());
			   stellarTxnConfig.setAssetCode(receiverCountry.getIso4217CurrencyAlphabeticCode());
			   
			   
		
			stellarTxnConfig.setAssetCode(country.getIso4217CurrencyAlphabeticCode());
			stellarTxnConfig.setSenderAccount(new SenderAccount().setPublicKey(senderDetails.getAccountPublicKey())
					.setSecretSeed(senderDetails.getAccountSecretKey()).setSenderCountryDailCode(senderDetails.getCountryDialCode()));
			stellarTxnConfig
					.setReceiverAccount(new ReceiverAccount().setPublicKey(receiverDetails.getAccountPublicKey())
							.setSecretSeed(receiverDetails.getAccountSecretKey()).setReceiverCountryDailCode(receiverDetails.getCountryDialCode()));
			stellarTxnConfig.setIsTestNetwork(electraConfig.isTestnetAccount);
			stellarTxnConfig.setNoOfTokens(tokenManagementRequest.getNoOfTokens());

			try {
				
				SubmitTransactionResponse transactionXdr =  crossBoarderTransfer(stellarTxnConfig);

				LOG.debug("Token transfer transaction XDR - " + transactionXdr);
				

				transactionDetail = commonService.saveTransactionDetails(TokenOperation.Payment, TokenActivity.TRANSFER_TOKENS,
						"stellarRequest", "stellarResponse", senderName, senderIsClient, senderCorrelationId,
						receiverName, receiverIsClient, receiverCorrelationId, (short) 1,
						stellarTxnConfig.getNoOfTokens(), stellarTxnConfig.getAssetCode(), transactionXdr.getHash(),
						null, new Date(), new Date(), tokenManagementRequest.getTransactionPurpose(),
						tokenManagementRequest.getIsFee(), false);

			} catch (ApiException exp) {
				

				transactionDetail = commonService.saveTransactionDetails(TokenOperation.Payment, TokenActivity.TRANSFER_TOKENS,
						"stellarRequest", "stellarResponse", senderName, senderIsClient, senderCorrelationId,
						receiverName, receiverIsClient, receiverCorrelationId, (short) 3,
						stellarTxnConfig.getNoOfTokens(), stellarTxnConfig.getAssetCode(), null, null, new Date(),
						new Date(), tokenManagementRequest.getTransactionPurpose(), tokenManagementRequest.getIsFee(), false);
				throw exp;

			} catch (Exception exp) {
				

				transactionDetail = commonService.saveTransactionDetails(TokenOperation.Payment, TokenActivity.TRANSFER_TOKENS,
						"stellarRequest", "stellarResponse", senderName, senderIsClient, senderCorrelationId,
						receiverName, receiverIsClient, receiverCorrelationId, (short) 2,
						stellarTxnConfig.getNoOfTokens(), stellarTxnConfig.getAssetCode(), null, null, new Date(),
						new Date(), tokenManagementRequest.getTransactionPurpose(), tokenManagementRequest.getIsFee(), false);

				LOG.error("Error while Transferring token: " + ExceptionUtils.getStackTrace(exp));
				throw new ApiException(HttpStatus.OK,
						Arrays.asList(new Error(ResponseCode.TOKEN_MANAGEMENT_PAYMENT_TRANSACTION_FAILURE.getCode(),
								messageProperties.tokenManagementPaymentTransactionFailure)));
			}

			tokenLifecycleManagementResponse = new TokenLifecycleManagementResponse();
			
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
			
			tokenLifecycleManagementResponse.setTransactionDto(transactionDto);
			tokenLifecycleManagementResponse.setStatus(HttpStatus.OK.value());
			tokenLifecycleManagementResponse.setCode(ResponseCode.Success.getCode());

			
			List<AssetDetails> accountBalances = stellarTxnManager.getAccountBalance(stellarTxnConfig
					.setReceiverAccount(new ReceiverAccount().setPublicKey(senderDetails.getAccountPublicKey())));

			for (AssetDetails accountBalance : accountBalances) {
				if (accountBalance.getAssetCode().equalsIgnoreCase(stellarTxnConfig.getAssetCode())) {
					tokenLifecycleManagementResponse.setAccountBalance(accountBalance.getAssetQuantity());
				}
			}

			

		} else {

			

			LOG.debug("Associated Client Correlation Id :- " + tokenManagementRequest.getClientId());

			//Client clientDetails = commonService.getClientDetailsByCorrelationId(tokenManagementRequest.getClientId());

			senderDetails = new ClientUser();
			senderDetails = clientUserRepository.findByUserCorrelationId(tokenManagementRequest.getUserCorrelationId());

			if (senderDetails.getCountryDialCode() == null) {
				throw new ApiException(HttpStatus.PRECONDITION_FAILED,
						Arrays.asList(new Error(ResponseCode.COUNTRY_DIAL_CODE_NOT_FOUND.getCode(),
								messageProperties.userClientCountryDialCodeNotFound)));
			}

			Country country = countryRepository.findCountryByCountryDialCode(senderDetails.getCountryDialCode());

			StellarTransactionConfigDto stellarTxnConfig = new StellarTransactionConfigDto();

			
			stellarTxnConfig.setAssetCode(country.getIso4217CurrencyAlphabeticCode());
			
			stellarTxnConfig.setIsTestNetwork(electraConfig.isTestnetAccount);
			stellarTxnConfig.setNoOfTokens(tokenManagementRequest.getNoOfTokens());
			stellarTxnConfig.setAssetLimit(String.valueOf(StellarConstants.ASSET_TRUSTLINE_LIMIT));

			

			if (!(senderDetails.getStatus().equals(Status.ACTIVE))) {
				throw new ApiException(HttpStatus.PRECONDITION_FAILED,
						Arrays.asList(new Error(ResponseCode.TOKEN_MANAGEMENT_USER_TOKEN_LOCK.getCode(),
								messageProperties.tokenManagementUserLock)));
			}

			IssuingAccount issuingAccount = null;

			List<MasterAccounts> masterAccountsList = (List<MasterAccounts>) masterAccountRepo.findAll();
			MasterAccounts masterAccounts = masterAccountsList.get(0);
			issuingAccount = new IssuingAccount().setPublicKey(masterAccounts.getIssuingAccountPublicKey())
					.setSecretSeed(masterAccounts.getIssuingAccountSecretKey());

			stellarTxnConfig.setIssuingAccount(issuingAccount);
			stellarTxnConfig.setReceiverAccount(new ReceiverAccount().setSecretSeed(senderDetails.getAccountSecretKey())
					.setPublicKey(senderDetails.getAccountPublicKey()));

			

			receiverDetails = clientUserRepository
					.findByUserCorrelationId(tokenManagementRequest.getBeneficiaryCorrelationId());

			if (!(receiverDetails.getStatus().equals(Status.ACTIVE))) {
				throw new ApiException(HttpStatus.PRECONDITION_FAILED,
						Arrays.asList(new Error(ResponseCode.TOKEN_MANAGEMENT_USER_TOKEN_LOCK.getCode(),
								messageProperties.tokenManagementUserLock)));
			}

			stellarTxnConfig
					.setReceiverAccount(new ReceiverAccount().setSecretSeed(receiverDetails.getAccountSecretKey())
							.setPublicKey(receiverDetails.getAccountPublicKey()));

			
			stellarTxnConfig.setAssetCode(country.getIso4217CurrencyAlphabeticCode());
			stellarTxnConfig.setSenderAccount(new SenderAccount().setPublicKey(senderDetails.getAccountPublicKey())
					.setSecretSeed(senderDetails.getAccountSecretKey()).setSenderCountryDailCode(senderDetails.getCountryDialCode()));
			stellarTxnConfig
					.setReceiverAccount(new ReceiverAccount().setPublicKey(receiverDetails.getAccountPublicKey())
							.setSecretSeed(receiverDetails.getAccountSecretKey()).setReceiverCountryDailCode(receiverDetails.getCountryDialCode()));
			
			stellarTxnConfig.setIsTestNetwork(electraConfig.isTestnetAccount);
			stellarTxnConfig.setNoOfTokens(tokenManagementRequest.getNoOfTokens());

			try {
				
				SubmitTransactionResponse transactionXdr =  crossBoarderTransfer(stellarTxnConfig);
				
				

				LOG.debug("Token transfer transaction XDR - " + transactionXdr);
				
				
				transactionDetail = commonService.saveTransactionDetails(TokenOperation.Payment, TokenActivity.TRANSFER_TOKENS,
						"stellarRequest", "stellarResponse",
						senderDetails.getFirstName() + " " + senderDetails.getLastName(), false,
						senderDetails.getUserCorrelationId(),
						receiverDetails.getFirstName() + " " + receiverDetails.getLastName(), false,
						receiverDetails.getUserCorrelationId(), (short) 1, stellarTxnConfig.getNoOfTokens(),
						stellarTxnConfig.getAssetCode(), transactionXdr.getHash(), null, new Date(), new Date(),
						tokenManagementRequest.getTransactionPurpose(), tokenManagementRequest.getIsFee(), false);

			} catch (ApiException exp) {
				
				transactionDetail = commonService.saveTransactionDetails(TokenOperation.Payment, TokenActivity.TRANSFER_TOKENS,
						"stellarRequest", "stellarResponse",
						senderDetails.getFirstName() + " " + senderDetails.getLastName(), false,
						senderDetails.getUserCorrelationId(),
						receiverDetails.getFirstName() + " " + receiverDetails.getLastName(), false,
						receiverDetails.getUserCorrelationId(), (short) 3, stellarTxnConfig.getNoOfTokens(),
						stellarTxnConfig.getAssetCode(), null, null, new Date(), new Date(),
						tokenManagementRequest.getTransactionPurpose(), tokenManagementRequest.getIsFee(), false);
				throw exp;

			} catch (Exception exp) {
				
				transactionDetail = commonService.saveTransactionDetails(TokenOperation.Payment, TokenActivity.TRANSFER_TOKENS,
						"stellarRequest", "stellarResponse",
						senderDetails.getFirstName() + " " + senderDetails.getLastName(), false,
						senderDetails.getUserCorrelationId(),
						receiverDetails.getFirstName() + " " + receiverDetails.getLastName(), false,
						receiverDetails.getUserCorrelationId(), (short) 2, stellarTxnConfig.getNoOfTokens(),
						stellarTxnConfig.getAssetCode(), null, null, new Date(), new Date(),
						tokenManagementRequest.getTransactionPurpose(), tokenManagementRequest.getIsFee(), false);
				LOG.error("Error while Transferring token: " + ExceptionUtils.getStackTrace(exp));
				throw new ApiException(HttpStatus.OK,
						Arrays.asList(new Error(ResponseCode.TOKEN_MANAGEMENT_PAYMENT_TRANSACTION_FAILURE.getCode(),
								messageProperties.tokenManagementPaymentTransactionFailure)));
			}

			tokenLifecycleManagementResponse = new TokenLifecycleManagementResponse();
			
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
			
			tokenLifecycleManagementResponse.setTransactionDto(transactionDto);
			tokenLifecycleManagementResponse.setStatus(HttpStatus.OK.value());
			tokenLifecycleManagementResponse.setCode(ResponseCode.Success.getCode());

			// Getting updated account balance from stellar
			List<AssetDetails> accountBalances = stellarTxnManager.getAccountBalance(stellarTxnConfig
					.setReceiverAccount(new ReceiverAccount().setPublicKey(senderDetails.getAccountPublicKey())));

			for (AssetDetails accountBalance : accountBalances) {
				if (accountBalance.getAssetCode().equalsIgnoreCase(stellarTxnConfig.getAssetCode())) {
					tokenLifecycleManagementResponse.setAccountBalance(accountBalance.getAssetQuantity());
				}
			}

		}
		return tokenLifecycleManagementResponse;

	}

	/* (non-Javadoc)
	 * @see com.ireslab.sendx.electra.service.TokenManagementService#transferFeeToMasterAccount(com.ireslab.sendx.electra.model.TokenLifecycleManagementRequest)
	 */
	@Override
	public TokenLifecycleManagementResponse transferFeeToMasterAccount(
			TokenLifecycleManagementRequest tokenManagementRequest) {

		ClientUser senderDetails = new ClientUser();
		ClientUser receiverDetails = null;
		
		TokenLifecycleManagementResponse tokenLifecycleManagementResponse = null;

		LOG.debug("Request recieved for transfer token : " + tokenManagementRequest.toString());


		List<MasterAccounts> masterAccountsList = (List<MasterAccounts>) masterAccountRepo.findAll();
		MasterAccounts masterAccounts = masterAccountsList.get(0);

		boolean senderIsClient = false;
		boolean receiverIsClient = false;

		String senderName = null;
		String senderCorrelationId = null;
		String receiverName = null;
		String receiverCorrelationId = null;

		Client clientSender = clientRepository.findByClientCorrelationId(tokenManagementRequest.getUserCorrelationId());
		Client clientReceiver = clientRepository
				.findByClientCorrelationId(tokenManagementRequest.getBeneficiaryCorrelationId());
		if (clientSender != null) {
			senderIsClient = true;
		}

		if (clientReceiver != null) {
			receiverIsClient = true;
		}

		senderDetails = clientUserRepository.findByUserCorrelationId(tokenManagementRequest.getUserCorrelationId());
		if (senderDetails == null) {
			Client senderClientAccount = clientRepository
					.findByClientCorrelationId(tokenManagementRequest.getUserCorrelationId());
			senderDetails = new ClientUser();
			senderDetails.setCountryDialCode(senderClientAccount.getCountryDialCode());
			senderDetails.setStatus(Enum.valueOf(Status.class, senderClientAccount.getStatus()));
			senderDetails.setAccountSecretKey(senderClientAccount.getBaseTxnAccountSecretKey());
			senderDetails.setAccountPublicKey(senderClientAccount.getBaseTxnAccountPublicKey());

			senderName = senderClientAccount.getClientName();
		} else {
			senderName = senderDetails.getFirstName() + " " + senderDetails.getLastName();
		}
		senderCorrelationId = tokenManagementRequest.getUserCorrelationId();

		receiverDetails = new ClientUser();
		receiverDetails.setAccountPublicKey(masterAccounts.getBaseTxnAccountPublicKey());
		receiverDetails.setAccountSecretKey(masterAccounts.getBaseTxnAccountSecretKey());
		receiverName = masterAccounts.getAccountName();
		receiverCorrelationId = masterAccounts.getAccountName();

		

		if (senderDetails.getCountryDialCode() == null) {
			throw new ApiException(HttpStatus.PRECONDITION_FAILED,
					Arrays.asList(new Error(ResponseCode.COUNTRY_DIAL_CODE_NOT_FOUND.getCode(),
							messageProperties.userClientCountryDialCodeNotFound)));
		}

		Country country = countryRepository.findCountryByCountryDialCode(senderDetails.getCountryDialCode());

		StellarTransactionConfigDto stellarTxnConfig = new StellarTransactionConfigDto();

		
		stellarTxnConfig.setAssetCode(country.getIso4217CurrencyAlphabeticCode());
		stellarTxnConfig.setIsTestNetwork(electraConfig.isTestnetAccount);
		stellarTxnConfig.setNoOfTokens(tokenManagementRequest.getNoOfTokens());
		stellarTxnConfig.setAssetLimit(String.valueOf(StellarConstants.ASSET_TRUSTLINE_LIMIT));

		

		if (!(senderDetails.getStatus().equals(Status.ACTIVE))) {
			throw new ApiException(HttpStatus.PRECONDITION_FAILED,
					Arrays.asList(new Error(ResponseCode.TOKEN_MANAGEMENT_USER_TOKEN_LOCK.getCode(),
							messageProperties.tokenManagementUserLock)));
		}

		IssuingAccount issuingAccount = null;

		
		issuingAccount = new IssuingAccount().setPublicKey(masterAccounts.getIssuingAccountPublicKey())
				.setSecretSeed(masterAccounts.getIssuingAccountSecretKey());

		stellarTxnConfig.setIssuingAccount(issuingAccount);
		stellarTxnConfig.setReceiverAccount(new ReceiverAccount().setSecretSeed(senderDetails.getAccountSecretKey())
				.setPublicKey(senderDetails.getAccountPublicKey()));

		

		LOG.debug("Creating Trustline on Stellar");
		stellarTxnManager.createTrustLine(stellarTxnConfig);
		LOG.debug("Trustline created successfully");

		

		stellarTxnConfig.setReceiverAccount(new ReceiverAccount().setSecretSeed(receiverDetails.getAccountSecretKey())
				.setPublicKey(receiverDetails.getAccountPublicKey()));

		

		LOG.info("Creating Trustline for RecieverCorrelationId - "
				+ tokenManagementRequest.getBeneficiaryCorrelationId());

		
		stellarTxnConfig.setAssetCode(country.getIso4217CurrencyAlphabeticCode());
		stellarTxnConfig.setSenderAccount(new SenderAccount().setPublicKey(senderDetails.getAccountPublicKey())
				.setSecretSeed(senderDetails.getAccountSecretKey()));
		stellarTxnConfig.setReceiverAccount(new ReceiverAccount().setPublicKey(receiverDetails.getAccountPublicKey())
				.setSecretSeed(receiverDetails.getAccountSecretKey()));
		stellarTxnConfig.setIsTestNetwork(electraConfig.isTestnetAccount);
		stellarTxnConfig.setNoOfTokens(tokenManagementRequest.getNoOfTokens());

		try {
			SubmitTransactionResponse transactionXdr = stellarTxnManager.transferToken(stellarTxnConfig);

			LOG.debug("Token transfer transaction XDR - " + transactionXdr);
			
			LOG.info("TRANSFER TOKEN...!!");

			commonService.saveTransactionDetails(TokenOperation.Payment, TokenActivity.TRANSFER_TOKENS,
					"stellarRequest", "stellarResponse", senderName, senderIsClient, senderCorrelationId, receiverName,
					receiverIsClient, receiverCorrelationId, (short) 1, stellarTxnConfig.getNoOfTokens(),
					stellarTxnConfig.getAssetCode(), transactionXdr.getHash(), null, new Date(), new Date(),
					tokenManagementRequest.getTransactionPurpose(), tokenManagementRequest.getIsFee(), false);

		} catch (ApiException exp) {
			

			commonService.saveTransactionDetails(TokenOperation.Payment, TokenActivity.TRANSFER_TOKENS,
					"stellarRequest", "stellarResponse", senderName, senderIsClient, senderCorrelationId, receiverName,
					receiverIsClient, receiverCorrelationId, (short) 3, stellarTxnConfig.getNoOfTokens(),
					stellarTxnConfig.getAssetCode(), null, null, new Date(), new Date(),
					tokenManagementRequest.getTransactionPurpose(), tokenManagementRequest.getIsFee(), false);
			throw exp;

		} catch (Exception exp) {
			

			commonService.saveTransactionDetails(TokenOperation.Payment, TokenActivity.TRANSFER_TOKENS,
					"stellarRequest", "stellarResponse", senderName, senderIsClient, senderCorrelationId, receiverName,
					receiverIsClient, receiverCorrelationId, (short) 2, stellarTxnConfig.getNoOfTokens(),
					stellarTxnConfig.getAssetCode(), null, null, new Date(), new Date(),
					tokenManagementRequest.getTransactionPurpose(), tokenManagementRequest.getIsFee(), false);

			LOG.error("Error while Transferring token: " + ExceptionUtils.getStackTrace(exp));
			throw new ApiException(HttpStatus.OK,
					Arrays.asList(new Error(ResponseCode.TOKEN_MANAGEMENT_PAYMENT_TRANSACTION_FAILURE.getCode(),
							messageProperties.tokenManagementPaymentTransactionFailure)));
		}

		tokenLifecycleManagementResponse = new TokenLifecycleManagementResponse();
		tokenLifecycleManagementResponse.setStatus(HttpStatus.OK.value());
		tokenLifecycleManagementResponse.setCode(ResponseCode.Success.getCode());

		// Getting updated account balance from stellar
		List<AssetDetails> accountBalances = stellarTxnManager.getAccountBalance(stellarTxnConfig
				.setReceiverAccount(new ReceiverAccount().setPublicKey(senderDetails.getAccountPublicKey())));

		for (AssetDetails accountBalance : accountBalances) {
			if (accountBalance.getAssetCode().equalsIgnoreCase(stellarTxnConfig.getAssetCode())) {
				tokenLifecycleManagementResponse.setAccountBalance(accountBalance.getAssetQuantity());
			}
		}

		

		return tokenLifecycleManagementResponse;

	}

	
	/* (non-Javadoc)
	 * @see com.ireslab.sendx.electra.service.TokenManagementService#cashOutTokens(com.ireslab.sendx.electra.model.TokenLifecycleManagementRequest)
	 */
	@Override
	public TokenLifecycleManagementResponse cashOutTokens(TokenLifecycleManagementRequest tokenManagementRequest) {

		ClientUser userDetails = null;
		
		TokenLifecycleManagementResponse tokenLifecycleManagementResponse = null;

		LOG.info("Getting UserDetails for userCorrelationId - " + tokenManagementRequest.getUserCorrelationId()
				+ " & TokenDetails for tokenCorrelationId - " + tokenManagementRequest.getTokenCorrelationId());


		Client client = clientRepository.findByClientCorrelationId(tokenManagementRequest.getUserCorrelationId());

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

		} else {
			userDetails = clientUserRepository.findByUserCorrelationId(tokenManagementRequest.getUserCorrelationId());
		}

		if (userDetails == null) {
			throw new ApiException(HttpStatus.PRECONDITION_FAILED,
					Arrays.asList(new Error(ResponseCode.TOKEN_MANAGEMENT_USER_NOT_FOUND.getCode(),
							messageProperties.tokenManagementUserNotFound)));
		}

		if (!(userDetails.getStatus().equals(Status.ACTIVE))) {
			throw new ApiException(HttpStatus.PRECONDITION_FAILED,
					Arrays.asList(new Error(ResponseCode.TOKEN_MANAGEMENT_USER_TOKEN_LOCK.getCode(),
							messageProperties.tokenManagementUserLock)));
		}

		
		Country country = countryRepository.findCountryByCountryDialCode(userDetails.getCountryDialCode());
		final String tokenCode = country.getIso4217CurrencyAlphabeticCode();
		IssuingAccount issuingAccount = null;
		ReceiverAccount receiverAccount = null;

		StellarTransactionConfigDto stellarTxnConfig = new StellarTransactionConfigDto();
		stellarTxnConfig.setAssetCode(tokenCode);
		stellarTxnConfig.setIsTestNetwork(userDetails.getClient().isTestnetAccount());
		stellarTxnConfig.setNoOfTokens(tokenManagementRequest.getNoOfTokens());

		// Getting account balance for user
		List<AssetDetails> assetDetails = stellarTxnManager.getAccountBalance(stellarTxnConfig
				.setReceiverAccount(new ReceiverAccount().setSecretSeed(userDetails.getAccountSecretKey())
						.setPublicKey(userDetails.getAccountPublicKey())));

		// Check if asset tokens are found
		if (!CollectionUtils.isNotEmpty(assetDetails)) {

			LOG.error("No available token balance in user account");
			throw new ApiException(HttpStatus.PRECONDITION_FAILED,
					Arrays.asList(new Error(ResponseCode.STELLAR_ACCOUNT_BALANCE_OPERATION_FAILED.getCode(),
							messageProperties.stellarAccountBalanceOperationFailed)));
		}

		

		assetDetails.forEach(assetDetail -> {
			if (assetDetail.getAssetCode().equalsIgnoreCase(tokenCode)
					&& Double.parseDouble(assetDetail.getAssetQuantity()) <= Double
							.parseDouble(tokenManagementRequest.getNoOfTokens())) {

				LOG.error("Insufficient account balance");
				throw new ApiException(HttpStatus.PRECONDITION_FAILED,
						Arrays.asList(new Error(ResponseCode.STELLAR_ACCOUNT_INSUFFICIENT_BALANCE.getCode(),
								messageProperties.stellarAccountBalanceInsufficient)));
			}
		});

		List<MasterAccounts> masterAccountsList = (List<MasterAccounts>) masterAccountRepo.findAll();
		MasterAccounts masterAccounts = masterAccountsList.get(0);

		

		issuingAccount = new IssuingAccount().setSecretSeed(masterAccounts.getIssuingAccountSecretKey())
				.setPublicKey(masterAccounts.getIssuingAccountPublicKey());

		stellarTxnConfig.setIssuingAccount(issuingAccount);

		
		receiverAccount = new ReceiverAccount().setSecretSeed(masterAccounts.getBaseTxnAccountSecretKey())
				.setPublicKey(masterAccounts.getBaseTxnAccountPublicKey());

		stellarTxnConfig.setReceiverAccount(receiverAccount);

		stellarTxnConfig.setSenderAccount(new SenderAccount().setSecretSeed(userDetails.getAccountSecretKey())
				.setPublicKey(userDetails.getAccountPublicKey()));

		
		try {
			SubmitTransactionResponse transactionXdr = stellarTxnManager.transferToken(stellarTxnConfig);
			LOG.debug("Token transfer transaction XDR - " + transactionXdr);

			
			CashOutDto	cashOutTokensRequest =tokenManagementRequest.getCashOutDto();
			CashOut cashOut = new CashOut();
			cashOut.setUserCorrelationId(cashOutTokensRequest.getUserCorrelationId());
			cashOut.setNoOfTokens(cashOutTokensRequest.getNoOfTokens());
			cashOut.setFee(cashOutTokensRequest.getFee());
			cashOut.setInstitutionName(cashOutTokensRequest.getInstitutionName());
			cashOut.setInstitutionAccountNumber(cashOutTokensRequest.getInstitutionAccountNumber());
			cashOut.setAdditionalInstitutionInfo("");
			cashOut.setBeneficiaryMobileNumber(userDetails.getMobileNumber());
			cashOut.setCashOut(true);
			cashOut.setCreatedDate(new Date());
			cashOut.setModifiedDate((new Date()));
			cashOut.setStatus(Status.NEW.name());
			cashOut.setCountryDialCode(userDetails.getCountryDialCode());
			cashOut.setBeneficiaryMobileNumber(userDetails.getMobileNumber());
			cashOut.setRequesterName(userDetails.getFirstName()+ " "+userDetails.getLastName());
			cashOutRepository.save(cashOut);
			//---------------------------------
		} catch (Exception exp) {

			LOG.error("Error while transferring token: " + ExceptionUtils.getStackTrace(exp));
			

			if (exp instanceof ApiException) {
				throw (ApiException) exp;
			}
			throw new ApiException(HttpStatus.OK,
					Arrays.asList(new Error(ResponseCode.TOKEN_MANAGEMENT_PAYMENT_TRANSACTION_FAILURE.getCode(),
							messageProperties.tokenManagementPaymentTransactionFailure)));
		}

		assetDetails = stellarTxnManager.getAccountBalance(stellarTxnConfig.setReceiverAccount(new ReceiverAccount()
				.setSecretSeed(userDetails.getAccountSecretKey()).setPublicKey(userDetails.getAccountPublicKey())));

		String accountBalance = assetDetails.stream().filter(asset -> asset.getAssetCode().equalsIgnoreCase(tokenCode))
				.findAny().map(balance -> balance.getAssetQuantity()).orElse(null);

		tokenLifecycleManagementResponse = new TokenLifecycleManagementResponse();
		tokenLifecycleManagementResponse.setAccountBalance(accountBalance);
		tokenLifecycleManagementResponse.setStatus(HttpStatus.OK.value());
		tokenLifecycleManagementResponse.setCode(ResponseCode.Success.getCode());

		return tokenLifecycleManagementResponse;
	}

	/**
	 * @param noOfTokens
	 * @param senderCorelationId
	 * @param senderType
	 * @param receiverCorelationId
	 * @param receiverType
	 * @param transactionXDR
	 * @param status
	 * @param tokenActivity
	 * @return
	 *//*
		 * private TransactionDetail saveTransactionDetails(String noOfTokens, String
		 * senderCorelationId, UserType senderType, String receiverCorelationId,
		 * UserType receiverType, String transactionXDR, Short status, TokenActivity
		 * tokenActivity) { TransactionDetail transactionDetail = new
		 * TransactionDetail(); transactionDetail.setNoOfTokens(noOfTokens);
		 * 
		 * transactionDetail.setSenderCorelationId(senderCorelationId);
		 * 
		 * transactionDetail.setReceiverCorelationId(receiverCorelationId);
		 * transactionDetail.setReceiverType(receiverType);
		 * transactionDetail.setSenderType(senderType);
		 * 
		 * transactionDetail.setTokenActivity(tokenActivity);
		 * 
		 * transactionDetail.setModifiedDate(new Date());
		 * transactionDetail.setTransactionDate(new Date());
		 * transactionDetail.setTransactionStatus(status);
		 * transactionDetail.setTransactionXdr(transactionXDR); if (status != null &&
		 * status.compareTo((short) 1) == 0)
		 * transactionDetail.setTransactionStatusMessage("Success"); else
		 * transactionDetail.setTransactionStatusMessage("Failed");
		 * 
		 * txnDetailRepo.save(transactionDetail); return transactionDetail; }
		 */

	
	/* (non-Javadoc)
	 * @see com.ireslab.sendx.electra.service.TokenManagementService#updateTokenManagement(com.ireslab.sendx.electra.model.TokenLifecycleManagementRequest)
	 */
	@Override
	public TokenLifecycleManagementResponse updateTokenManagement(
			TokenLifecycleManagementRequest tokenManagementRequest) {
		

		TokenLifecycleManagementResponse tokenLifecycleManagementResponse = null;
		LOG.info("Getting UserDetails for userCorrelationId - " + tokenManagementRequest.getUserCorrelationId()
				+ " & TokenDetails for tokenCorrelationId - " + tokenManagementRequest.getTokenCorrelationId());

		ClientUserToken clientUserToken = clientUserTokenRepo
				.findByClientUser_UserCorrelationIdAndClientAssetToken_TokenCorrelationId(
						tokenManagementRequest.getUserCorrelationId(), tokenManagementRequest.getTokenCorrelationId());

		if (clientUserToken == null) {
			throw new ApiException(HttpStatus.PRECONDITION_FAILED,
					Arrays.asList(new Error(ResponseCode.TOKEN_MANAGEMENT_CLIENT_ASSET_TOKEN_NOT_FOUND.getCode(),
							messageProperties.tokenManagementClientAssetToken)));
		}

		if (tokenManagementRequest.getAccountStatus() == null || !tokenManagementRequest.getAccountStatus()) {
			LOG.info("Block tokens with UserDetails for userCorrelationId - "
					+ tokenManagementRequest.getUserCorrelationId() + " & TokenDetails for tokenCorrelationId - "
					+ tokenManagementRequest.getTokenCorrelationId());
			// ClientAssetToken clientAssetToken = clientUserToken.getClientAssetToken();

			clientUserToken.setStatus(tokenManagementRequest.getStatus());
			clientUserTokenRepo.save(clientUserToken);

		} else {
			LOG.info("Block account with UserDetails for userCorrelationId - "
					+ tokenManagementRequest.getUserCorrelationId() + " & TokenDetails for tokenCorrelationId - "
					+ tokenManagementRequest.getTokenCorrelationId());

			ClientUser userDetails = clientUserToken.getClientUser();
			if (userDetails == null) {
				throw new ApiException(HttpStatus.PRECONDITION_FAILED,
						Arrays.asList(new Error(ResponseCode.TOKEN_MANAGEMENT_USER_NOT_FOUND.getCode(),
								messageProperties.tokenManagementUserNotFound)));
			}
			userDetails.setStatus(tokenManagementRequest.getStatus());
			commonService.updateClientUser(userDetails);
		}
		tokenLifecycleManagementResponse = new TokenLifecycleManagementResponse();
		tokenLifecycleManagementResponse.setStatus(HttpStatus.OK.value());
		tokenLifecycleManagementResponse.setCode(ResponseCode.Success.getCode());

		return tokenLifecycleManagementResponse;
	}
	
	/**
	 * @param stellarTransactionConfigDto
	 * @return
	 */
	private SubmitTransactionResponse crossBoarderTransfer(StellarTransactionConfigDto stellarTransactionConfigDto ) {
		SubmitTransactionResponse transactionXdr =null;
		SenderAccount senderAccount = stellarTransactionConfigDto.getSenderAccount();
		ReceiverAccount receiverAccount =stellarTransactionConfigDto.getReceiverAccount();
		
		LOG.info("sender Account country code : "+senderAccount.getSenderCountryDailCode()+"receiver Account country code :"+receiverAccount.getReceiverCountryDailCode());
		
		List<MasterAccounts> masterAccountsList = (List<MasterAccounts>) masterAccountRepo.findAll();
		MasterAccounts masterAccounts = masterAccountsList.get(0);
		
		ReceiverAccount receiverMasterAccount = new ReceiverAccount();
		receiverMasterAccount.setPublicKey(masterAccounts.getBaseTxnAccountPublicKey());
		receiverMasterAccount.setSecretSeed(masterAccounts.getBaseTxnAccountSecretKey());
		stellarTransactionConfigDto.setSenderAccount(senderAccount);
		stellarTransactionConfigDto.setReceiverAccount(receiverMasterAccount);
		LOG.debug("Inside crossBoarderTransfer  - NO of token :- " + stellarTransactionConfigDto.getNoOfTokens()
		+ "\n sender PublicKey :- " + senderAccount.getPublicKey()
		+ "\n Receiver PublicKey id :- " + receiverMasterAccount.getPublicKey());
		//stellarTransactionConfigDto.setNoOfTokens(tokenConversion("INR","USD"));
		try {
			transactionXdr = stellarTxnManager.transferToken(stellarTransactionConfigDto);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		SenderAccount senderMasterAccount = new SenderAccount();
		senderMasterAccount.setPublicKey(masterAccounts.getBaseTxnAccountPublicKey());
		senderMasterAccount.setSecretSeed(masterAccounts.getBaseTxnAccountSecretKey());
		stellarTransactionConfigDto.setSenderAccount(senderMasterAccount);
		stellarTransactionConfigDto.setReceiverAccount(receiverAccount);
		
		Country country = countryRepository.findCountryByCountryDialCode(receiverAccount.getReceiverCountryDailCode());
		
		stellarTransactionConfigDto.setAssetCode(country.getIso4217CurrencyAlphabeticCode());
		
		stellarTransactionConfigDto.setNoOfTokens(tokenConversion(countryRepository.findCountryByCountryDialCode(senderAccount.getSenderCountryDailCode()).getIso4217CurrencyAlphabeticCode(),
				countryRepository.findCountryByCountryDialCode(receiverAccount.getReceiverCountryDailCode()).getIso4217CurrencyAlphabeticCode(), stellarTransactionConfigDto.getNoOfTokens()));
		
		
		try {
			transactionXdr = stellarTxnManager.transferToken(stellarTransactionConfigDto);
			Country countrySender = countryRepository.findCountryByCountryDialCode(senderAccount.getSenderCountryDailCode());
			stellarTransactionConfigDto.setAssetCode(countrySender.getIso4217CurrencyAlphabeticCode());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	return transactionXdr;
		
		
	}
	
	 /**
	  * use to exchange currency.
	  * 
	 * @param to
	 * @param from
	 * @param token
	 * @return
	 */
	public  String tokenConversion(String to, String from, String token) {
	    	
	    	double ammoutToBeTransfered=0.0;
	    	
	    	Exchange exchange = exchangeRepository.findByExchangeTokenAndNativeCurrency(to,from);
	    	
	    	ammoutToBeTransfered =Double.parseDouble(token)*Double.parseDouble(exchange.getExchangeRate());
	    	
	    	return ""+ammoutToBeTransfered;
	    	
	    }
}
