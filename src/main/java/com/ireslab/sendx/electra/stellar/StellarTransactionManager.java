package com.ireslab.sendx.electra.stellar;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.stellar.sdk.Asset;
import org.stellar.sdk.AssetTypeNative;
import org.stellar.sdk.ChangeTrustOperation;
import org.stellar.sdk.CreateAccountOperation;
import org.stellar.sdk.KeyPair;
import org.stellar.sdk.Network;
import org.stellar.sdk.PaymentOperation;
import org.stellar.sdk.Server;
import org.stellar.sdk.Transaction;
import org.stellar.sdk.responses.AccountResponse;
import org.stellar.sdk.responses.AccountResponse.Balance;
import org.stellar.sdk.responses.SubmitTransactionResponse;
import org.stellar.sdk.responses.SubmitTransactionResponse.Extras.ResultCodes;
import org.stellar.sdk.responses.TransactionResponse;
import org.stellar.sdk.xdr.XdrDataOutputStream;

import com.google.gson.Gson;
import com.ireslab.sendx.electra.dto.StellarAccountDetailsDto;
import com.ireslab.sendx.electra.dto.StellarTransactionConfigDto;
import com.ireslab.sendx.electra.dto.StellarTransactionConfigDto.SenderAccount;
import com.ireslab.sendx.electra.exceptions.ApiException;
import com.ireslab.sendx.electra.model.AssetDetails;
//import com.ireslab.sendx.electra.model.Error;
import com.ireslab.sendx.electra.properties.MessagesProperties;
import com.ireslab.sendx.electra.utils.AppConstants;
import com.ireslab.sendx.electra.utils.ResponseCode;
import com.ireslab.sendx.electra.utils.StellarConstants;
import com.ireslab.sendx.electra.model.Error;


/**
 * @author Nitin
 *
 */
/**
 * @author Akhilesh
 *
 */
@Component
@PropertySource(value = AppConstants.STELLAR_CONFIG_FILE)
@ConfigurationProperties("stellar")
public class StellarTransactionManager {

	@Autowired
	private MessagesProperties messageProperties;

	@Autowired
	private Gson gson;

	private static final Logger LOG = LoggerFactory.getLogger(StellarTransactionManager.class);

	private String testnetHorizonUrl;
	private String livenetHorizonUrl;
	private String testnetFriendbotUrl;
	private String testLumensAccountSecretKey;
	private String testLumensAccountPublicKey;

	/**
	 * @param stellarConfig
	 * @return
	 */
	public StellarAccountDetailsDto createAccount(StellarTransactionConfigDto stellarConfig) {

		String connectionUrl = null;
		KeyPair newAccountKeyPair = null;
		StellarAccountDetailsDto stellarAccountDetails = null;

		try {
			if (stellarConfig.isTestNetwork()) {

				Network.useTestNetwork();
				newAccountKeyPair = KeyPair.random();
				connectionUrl = String.format(testnetFriendbotUrl, newAccountKeyPair.getAccountId());

				LOG.debug("Creating user account on Stellar Test Network - " + connectionUrl);

				try {
					URLConnection urlConnection = new URL(connectionUrl).openConnection();
					urlConnection.addRequestProperty(StellarConstants.REQUEST_PROPERTY_USER_AGENT_KEY,
							StellarConstants.REQUEST_PROPERTY_USER_AGENT_VALUE);

					@SuppressWarnings("resource")
					String stellarResponse = new Scanner(urlConnection.getInputStream(), "UTF-8").useDelimiter("\\A")
							.next();

					TransactionResponse transactionResponse = gson.fromJson(stellarResponse, TransactionResponse.class);
					if (transactionResponse.getResultXdr() != null && transactionResponse.getEnvelopeXdr() != null) {
						LOG.debug("New stellar account created successfully - " + stellarResponse);

					} else {
						throw new Exception("Unexpected response received from Stellar Friendbot - " + stellarResponse);
					}

				} catch (Exception exp) {

					LOG.error("Funding request failed at Stellar friendbot due to error - "
							+ ExceptionUtils.getStackTrace(exp));

					stellarConfig.setIsLoadInitialLumens(true);
					stellarConfig.setIsNativeAssetOperation(true);
					stellarConfig.setNoOfTokens("5");
					stellarConfig.setSenderAccount(new SenderAccount().setPublicKey(testLumensAccountPublicKey)
							.setSecretSeed(testLumensAccountSecretKey));
				}

			} else {
				Network.usePublicNetwork();
				newAccountKeyPair = KeyPair.random();

				LOG.debug("Creating User Account on Stellar Live Network - " + livenetHorizonUrl
						+ " with Initial Native Lumens (XLM) balance - "
						+ stellarConfig.getInitialLumensLoadQuantity());

				Server horizonServer = new Server(livenetHorizonUrl);
				KeyPair issuingAccountKeyPair = KeyPair
						.fromSecretSeed(stellarConfig.getSenderAccount().getSecretSeed());

				AccountResponse issuingAccount = horizonServer.accounts().account(issuingAccountKeyPair);
				Transaction accountCreationTxn = new Transaction.Builder(issuingAccount)
						.addOperation(new CreateAccountOperation.Builder(newAccountKeyPair,
								stellarConfig.getInitialLumensLoadQuantity()).setSourceAccount(issuingAccountKeyPair)
										.build())
						.build();
				accountCreationTxn.sign(issuingAccountKeyPair);

				SubmitTransactionResponse accountCreationTxnResponse = horizonServer
						.submitTransaction(accountCreationTxn);

				LOG.debug("Sequence Number - " + accountCreationTxn.getSequenceNumber() + ",\nFee - "
						+ accountCreationTxn.getFee() + ",\nisSuccess - " + accountCreationTxnResponse.isSuccess()
						+ ",\nResult XDR - " + accountCreationTxnResponse.getResultXdr() + ",\nLedger - "
						+ accountCreationTxnResponse.getLedger() + ",\nEnvelope XDR - "
						+ accountCreationTxnResponse.getEnvelopeXdr());

				// Transaction not success
				if (!accountCreationTxnResponse.isSuccess()) {

					ResultCodes resultCodes = accountCreationTxnResponse.getExtras().getResultCodes();
					LOG.error("Transaction Result Code - " + resultCodes.getTransactionResultCode()
							+ ",\nTransaction Operation Result Code - "
							+ resultCodes.getOperationsResultCodes().get(0));
					throw new Exception();
				}
			}

			if (stellarConfig.isLoadInitialLumens()) {
				try {
					LOG.debug("Loading user account with initial Lumens (XLM). . .");
					stellarConfig.setReceiverAccount(new StellarTransactionConfigDto.ReceiverAccount()
							.setSecretSeed(new String(newAccountKeyPair.getSecretSeed()))
							.setPublicKey(newAccountKeyPair.getAccountId()));
					transferToken(stellarConfig);

				} catch (Exception e) {
					LOG.error("User Account cannot be loaded with initial Lumens (XLM) - "
							+ ExceptionUtils.getStackTrace(e));
					throw new ApiException(HttpStatus.OK,
							Arrays.asList(new Error()
									.setCode(ResponseCode.TRANSACTION_MANAGER_TRANSFER_LUMENS_TOKEN_FAIL.getCode())
									.setMessage(messageProperties.transactionManagerTransferLumensTokenFail)));
				}
			}

			stellarAccountDetails = new StellarAccountDetailsDto(new String(newAccountKeyPair.getSecretSeed()),
					newAccountKeyPair.getAccountId());

		} catch (ApiException ex) {
			throw ex;
		} catch (Exception exp) {
			LOG.debug("Error occurred while generating stellar account - " + ExceptionUtils.getStackTrace(exp));
			throw new ApiException(HttpStatus.OK,
					Arrays.asList(new Error(ResponseCode.STELLAR_ACCOUNT_CREATION_FAILED.getCode(),
							messageProperties.stellarAccountCreationFailed)));
		}
		return stellarAccountDetails;
	}

	/**
	 * @param senderSecretKey
	 * @param receiverSecretKey
	 * @param amount
	 * @return
	 * @throws IOException
	 * @throws Exception
	 */
	public SubmitTransactionResponse transferToken(StellarTransactionConfigDto stellarConfig) throws IOException {

		String transactionXDR = null;
		Asset assetToTransfer = null;
		Server horizonServer = null;

		String assetName = stellarConfig.isNativeAssetOperation() ? "Native Lumens (XLM)"
				: "Tokens (" + stellarConfig.getAssetCode() + ")";

		if (stellarConfig.isTestNetwork()) {
			Network.useTestNetwork();
			horizonServer = new Server(testnetHorizonUrl);

		} else {
			Network.usePublicNetwork();
			horizonServer = new Server(livenetHorizonUrl);
		}

		KeyPair senderAccountKeyPair = KeyPair.fromSecretSeed(stellarConfig.getSenderAccount().getSecretSeed());
		KeyPair receiverAccountKeyPair = KeyPair.fromSecretSeed(stellarConfig.getReceiverAccount().getSecretSeed());

		if (stellarConfig.isNativeAssetOperation()) {
			assetToTransfer = new AssetTypeNative();
		} else {
			assetToTransfer = Asset.createNonNativeAsset(stellarConfig.getAssetCode(),
					KeyPair.fromSecretSeed(stellarConfig.getIssuingAccount().getSecretSeed()));
		}

		LOG.debug("\nInitiating transfer of " + assetName + " from sender account to receiver account . . ."
				+ "\n\t=> Source AccountId - " + senderAccountKeyPair.getAccountId()
				+ ",\n\t=> Destination AccountId - " + receiverAccountKeyPair.getAccountId() + ",\n\t=> No of "
				+ assetName + " - " + stellarConfig.getNoOfTokens());

		try {
			horizonServer.accounts().account(receiverAccountKeyPair);

		} catch (Exception exp) {
			LOG.debug("Destination account does not exists - " + ExceptionUtils.getStackTrace(exp));
			throw new ApiException(HttpStatus.OK,
					Arrays.asList(new Error().setCode(ResponseCode.STELLAR_DESTINATION_ACCOUNT_DOESNOT_EXISTS.getCode())
							.setMessage(messageProperties.stellarDestinationAccountDoesnotExists)));
		}

		/*
		 * From base to user account so transaction need to be built and signed by
		 * Issuing keys
		 */
		SubmitTransactionResponse tokenTransferToReceiverTxnResponse = null;
		Transaction tokenTransferToReceiverAccTxn = null;

		String pubKey = new String(stellarConfig.getSenderAccount().getPublicKey()).intern();

		synchronized (pubKey) {
			tokenTransferToReceiverAccTxn = new Transaction.Builder(
					horizonServer.accounts().account(senderAccountKeyPair))
							.addOperation(new PaymentOperation.Builder(receiverAccountKeyPair, assetToTransfer,
									stellarConfig.getNoOfTokens()).build())
							.build();

			tokenTransferToReceiverAccTxn.sign(senderAccountKeyPair);

			LOG.debug("\nSubmitting " + assetName + " transfer request . . ." + "\n\t=> Sequence Number - "
					+ tokenTransferToReceiverAccTxn.getSequenceNumber() + ",\n\t=> Fee - "
					+ tokenTransferToReceiverAccTxn.getFee());
			tokenTransferToReceiverTxnResponse = horizonServer.submitTransaction(tokenTransferToReceiverAccTxn);
		}

		if (!tokenTransferToReceiverTxnResponse.isSuccess()) {
			ResultCodes resultCodes = tokenTransferToReceiverTxnResponse.getExtras().getResultCodes();
			LOG.error("\nError occurred while executing " + assetName + " transfer transaction on Stellar :"
					+ "\n\t=> Transaction Result Code - " + resultCodes.getTransactionResultCode()
					+ ",\n\t=> Transaction Operation Result Codes - " + resultCodes.getOperationsResultCodes());

			if (resultCodes.getOperationsResultCodes() != null && resultCodes.getOperationsResultCodes().size() > 0) {

				resultCodes.getOperationsResultCodes().forEach(operationResultCode -> LOG
						.error("Transaction Operation Result Codes - " + operationResultCode));
			}

			if (resultCodes.getOperationsResultCodes().get(0).equalsIgnoreCase("op_underfunded")) {
				throw new ApiException(HttpStatus.OK,
						Arrays.asList(new Error(ResponseCode.STELLAR_ACCOUNT_INSUFFICIENT_BALANCE.getCode(),
								messageProperties.insufficientAccountBalance)));
			}
			
			if (resultCodes.getOperationsResultCodes().get(0).equalsIgnoreCase("op_line_full")) {
				throw new ApiException(HttpStatus.OK,
						Arrays.asList(new Error(ResponseCode.FAIL.getCode(),
								messageProperties.loadTokenFailed)));
			}
		}

		if (tokenTransferToReceiverTxnResponse.isSuccess()) {
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			org.stellar.sdk.xdr.Transaction.encode(new XdrDataOutputStream(byteArrayOutputStream),
					tokenTransferToReceiverAccTxn.toXdr());
			transactionXDR = new Base64().encodeAsString(byteArrayOutputStream.toByteArray());

			LOG.debug("\n" + assetName + " transferred successfully :" + "\n\t=> Ledger - "
					+ tokenTransferToReceiverTxnResponse.getLedger() + ",\n\t=> Result XDR - "
					+ tokenTransferToReceiverTxnResponse.getResultXdr() + ",\n\t=> Envelope XDR - "
					+ tokenTransferToReceiverTxnResponse.getEnvelopeXdr() + ",\n\t=> Transaction XDR - "
					+ transactionXDR);

			return tokenTransferToReceiverTxnResponse;
		}

		return tokenTransferToReceiverTxnResponse;
	}

	/**
	 * Execute Create Trustline transaction and return XDR
	 * 
	 * @param accountSecretKey
	 * @param limit
	 * @return
	 */
	public SubmitTransactionResponse createTrustLine(StellarTransactionConfigDto stellarConfig) {

		Server horizonServer = null;
		String trustlineLimit = null;
		Asset trustlineAsset = null;
		String xdrTrustlineTransaction = null;

		if (stellarConfig.isTestNetwork()) {
			Network.useTestNetwork();
			horizonServer = new Server(testnetHorizonUrl);

		} else {
			Network.usePublicNetwork();
			horizonServer = new Server(livenetHorizonUrl);
		}

		KeyPair userAccountKeyPair = KeyPair.fromSecretSeed(stellarConfig.getReceiverAccount().getSecretSeed());

		try {
			trustlineLimit = stellarConfig.getAssetLimit();

			// SendX Asset Token Asset tokenAsset =
			trustlineAsset = Asset.createNonNativeAsset(stellarConfig.getAssetCode(),
					KeyPair.fromSecretSeed(stellarConfig.getIssuingAccount().getSecretSeed()));

			LOG.debug("Creating and submitting trustline transaction to Stellar Network for Asset Token : "
					+ stellarConfig.getAssetCode() + " with Limit : " + trustlineLimit);
			Transaction userAccTrustLineTxn = new Transaction.Builder(
					horizonServer.accounts().account(userAccountKeyPair)).addOperation(

							new ChangeTrustOperation.Builder(trustlineAsset, trustlineLimit).build()).build();
			userAccTrustLineTxn.sign(userAccountKeyPair);

			SubmitTransactionResponse baseTrustLineTxnResponse = horizonServer.submitTransaction(userAccTrustLineTxn);
			LOG.debug("Sequence Number - " + userAccTrustLineTxn.getSequenceNumber() + ",\nFee - "
					+ userAccTrustLineTxn.getFee() + ",\nisSuccess - " + baseTrustLineTxnResponse.isSuccess()
					+ ",\nResult XDR - " + baseTrustLineTxnResponse.getResultXdr() + ",\nLedger - "
					+ baseTrustLineTxnResponse.getLedger() + ",\nEnvelope XDR - "
					+ baseTrustLineTxnResponse.getEnvelopeXdr());

			// Transaction not success
			if (!baseTrustLineTxnResponse.isSuccess()) {

				ResultCodes resultCodes = baseTrustLineTxnResponse.getExtras().getResultCodes();
				LOG.error("Transaction Result Code - " + resultCodes.getTransactionResultCode()
						+ ",\nTransaction Operation Result Code - " + resultCodes.getOperationsResultCodes().get(0));
				throw new ApiException(HttpStatus.OK,
						Arrays.asList(new Error(ResponseCode.TOKEN_MANAGEMENT_PAYMENT_TRANSACTION_FAILURE.getCode(),
								messageProperties.tokenManagementPaymentTransactionFailure)));

			} else {
				ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
				org.stellar.sdk.xdr.Transaction.encode(new XdrDataOutputStream(byteArrayOutputStream),
						userAccTrustLineTxn.toXdr());
				xdrTrustlineTransaction = new Base64().encodeAsString(byteArrayOutputStream.toByteArray());

				LOG.debug("Account trustline transaction XDR - " + xdrTrustlineTransaction);
				return baseTrustLineTxnResponse;
			}

		} catch (ApiException ex) {
			LOG.error("Error occurred while creating trustline for user account having Account Id : "
					+ userAccountKeyPair.getAccountId() + "\n" + ExceptionUtils.getStackTrace(ex));
			throw ex;

		} catch (Exception exp) {
			LOG.error("Error occurred while creating trustline for user account having Account Id : "
					+ userAccountKeyPair.getAccountId() + "\n" + ExceptionUtils.getStackTrace(exp));
			throw new ApiException(HttpStatus.OK,
					Arrays.asList(new Error(ResponseCode.STELLAR_TRUSTLINE_CREATION_FAILED.getCode(),
							messageProperties.stellarTrustlineCreationFailed)));
		}
	}

	/**
	 * @param userAccountPublicKey
	 * @return
	 * @throws IOException
	 */
	public List<AssetDetails> getAccountBalance(StellarTransactionConfigDto stellarConfig) {

		List<AssetDetails> accountBalances = null;
		Server horizonServer = null;

		// LOG.debug("Getting account balance for Account Id - " +
		// stellarConfig.getReceiverAccount().getPublicKey());

		if (stellarConfig.isTestNetwork()) {
			Network.useTestNetwork();
			horizonServer = new Server(testnetHorizonUrl);

		} else {
			Network.usePublicNetwork();
			horizonServer = new Server(livenetHorizonUrl);
		}

		KeyPair keyPair = KeyPair.fromAccountId(stellarConfig.getReceiverAccount().getPublicKey());

		try {
			accountBalances = new ArrayList<>();

			AccountResponse accountResponse = horizonServer.accounts().account(keyPair);
			for (Balance accountBalance : accountResponse.getBalances()) {

				if (accountBalance.getAssetCode() != null && !accountBalance.getAssetCode()
						.equalsIgnoreCase(StellarConstants.STELLAR_NATIVE_CURRENCY_CODE)) {

					// LOG.debug("Account Balance for '" + accountBalance.getAssetCode() + "' Token
					// is : "
					// + accountBalance.getBalance());

					accountBalances.add(new AssetDetails().setAssetCode(accountBalance.getAssetCode())
							.setAssetQuantity(accountBalance.getBalance()));
				}
			}
		} catch (Exception exp) {

			LOG.error("Error while getting account balance from stellar - " + ExceptionUtils.getStackTrace(exp));
			throw new ApiException(HttpStatus.OK,
					Arrays.asList(new Error(ResponseCode.STELLAR_ACCOUNT_BALANCE_OPERATION_FAILED.getCode(),
							messageProperties.stellarAccountBalanceOperationFailed)));
		}

		return accountBalances;
	}

	/**
	 * @return the testnetHorizonUrl
	 */
	public String getTestnetHorizonUrl() {
		return testnetHorizonUrl;
	}

	/**
	 * @param testnetHorizonUrl
	 *            the testnetHorizonUrl to set
	 */
	public void setTestnetHorizonUrl(String testnetHorizonUrl) {
		this.testnetHorizonUrl = testnetHorizonUrl;
	}

	/**
	 * @return the livenetHorizonUrl
	 */
	public String getLivenetHorizonUrl() {
		return livenetHorizonUrl;
	}

	/**
	 * @param livenetHorizonUrl
	 *            the livenetHorizonUrl to set
	 */
	public void setLivenetHorizonUrl(String livenetHorizonUrl) {
		this.livenetHorizonUrl = livenetHorizonUrl;
	}

	/**
	 * @return the testnetFriendbotUrl
	 */
	public String getTestnetFriendbotUrl() {
		return testnetFriendbotUrl;
	}

	/**
	 * @param testnetFriendbotUrl
	 *            the testnetFriendbotUrl to set
	 */
	public void setTestnetFriendbotUrl(String testnetFriendbotUrl) {
		this.testnetFriendbotUrl = testnetFriendbotUrl;
	}

	public String getTestLumensAccountSecretKey() {
		return testLumensAccountSecretKey;
	}

	public void setTestLumensAccountSecretKey(String testLumensAccountSecretKey) {
		this.testLumensAccountSecretKey = testLumensAccountSecretKey;
	}

	public String getTestLumensAccountPublicKey() {
		return testLumensAccountPublicKey;
	}

	public void setTestLumensAccountPublicKey(String testLumensAccountPublicKey) {
		this.testLumensAccountPublicKey = testLumensAccountPublicKey;
	}

}
