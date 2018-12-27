package com.ireslab.sendx.electra.stellar;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

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
import org.stellar.sdk.KeyPair;
import org.stellar.sdk.Network;
import org.stellar.sdk.PaymentOperation;
import org.stellar.sdk.Server;
import org.stellar.sdk.Transaction;
import org.stellar.sdk.responses.SubmitTransactionResponse;
import org.stellar.sdk.responses.SubmitTransactionResponse.Extras.ResultCodes;
import org.stellar.sdk.xdr.XdrDataOutputStream;

import com.google.gson.Gson;
import com.ireslab.sendx.electra.dto.StellarTransactionConfigDto;
import com.ireslab.sendx.electra.dto.StellarTransactionConfigDto.ReceiverAccount;
import com.ireslab.sendx.electra.dto.StellarTransactionConfigDto.SenderAccount;
import com.ireslab.sendx.electra.exceptions.ApiException;
import com.ireslab.sendx.electra.model.Error;
import com.ireslab.sendx.electra.properties.MessagesProperties;
import com.ireslab.sendx.electra.utils.AppConstants;
import com.ireslab.sendx.electra.utils.ResponseCode;


@Component
@PropertySource(value = AppConstants.STELLAR_CONFIG_FILE)
@ConfigurationProperties("stellar")
public class AccountMergeOperation {
	
	@Autowired
	private MessagesProperties messageProperties;

	@Autowired
	private Gson gson;

	private static final Logger LOG = LoggerFactory.getLogger(StellarTransactionManager.class);

	private String testnetHorizonUrl;
	private String livenetHorizonUrl;
	
	
	private void psvm() {
		StellarTransactionConfigDto stellarConfig =new StellarTransactionConfigDto();
		stellarConfig.setIsNativeAssetOperation(true);
		stellarConfig.setSenderAccount(new SenderAccount().setPublicKey("GB4YL3IPIFG4SXTUI5WUJ2GKWYULLYXYHOYOYCO2GGUTJLQ7KAL5FPGK").setSecretSeed("SBIPCUZUN6KJKTAM73UVHYVRCKFAKLZ6LRO3QGUN7UMAOVR3544PFIBX"));
		stellarConfig.setReceiverAccount(new ReceiverAccount().setPublicKey("GC5MK36G2KKCSB2GLT7KPHOOQQIV3T2KQXFCQMF3S3SFURHH2TD2RU2G").setSecretSeed("SAZCAKLXX277MT4JQX4QH267HB4O6PAYTR6CVB24IWOND4W7NM2VKLEM"));
		
	}
	
	
	
	public SubmitTransactionResponse mergeAccount(StellarTransactionConfigDto stellarConfig) throws IOException {

		/*stellarConfig =new StellarTransactionConfigDto();
		stellarConfig.setIsNativeAssetOperation(true);
		stellarConfig.setSenderAccount(new SenderAccount().setPublicKey("GB4YL3IPIFG4SXTUI5WUJ2GKWYULLYXYHOYOYCO2GGUTJLQ7KAL5FPGK").setSecretSeed("SBIPCUZUN6KJKTAM73UVHYVRCKFAKLZ6LRO3QGUN7UMAOVR3544PFIBX"));
		stellarConfig.setReceiverAccount(new ReceiverAccount().setPublicKey("GC5MK36G2KKCSB2GLT7KPHOOQQIV3T2KQXFCQMF3S3SFURHH2TD2RU2G").setSecretSeed("SAZCAKLXX277MT4JQX4QH267HB4O6PAYTR6CVB24IWOND4W7NM2VKLEM"));
		stellarConfig.setIsTestNetwork(true);*/
		
		
		
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
		

		SubmitTransactionResponse tokenTransferToReceiverTxnResponse = null;
		Transaction tokenTransferToReceiverAccTxn = null;

		String pubKey = new String(stellarConfig.getSenderAccount().getPublicKey()).intern();

		synchronized (pubKey) {
			tokenTransferToReceiverAccTxn = new Transaction.Builder(
					horizonServer.accounts().account(senderAccountKeyPair))
							.addOperation(new org.stellar.sdk.AccountMergeOperation.Builder(receiverAccountKeyPair).build())
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
	
	
	
	
	
	
	
	
	
	
	
	public static void main(String args[]) throws IOException {
		
		new AccountMergeOperation().mergeAccount(null);
		
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

	
	
}
