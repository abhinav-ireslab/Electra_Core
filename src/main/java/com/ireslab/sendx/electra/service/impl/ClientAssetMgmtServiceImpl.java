package com.ireslab.sendx.electra.service.impl;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.stellar.sdk.responses.SubmitTransactionResponse;

import com.ireslab.sendx.electra.dto.ClientAssetToken;
import com.ireslab.sendx.electra.dto.StellarTransactionConfigDto;
import com.ireslab.sendx.electra.dto.StellarTransactionConfigDto.IssuingAccount;
import com.ireslab.sendx.electra.dto.StellarTransactionConfigDto.ReceiverAccount;
import com.ireslab.sendx.electra.dto.StellarTransactionConfigDto.SenderAccount;
import com.ireslab.sendx.electra.entity.Client;
//import com.ireslab.sendx.electra.entity.ClientAssetToken;
import com.ireslab.sendx.electra.exceptions.ApiException;
import com.ireslab.sendx.electra.model.ClientAssetMgmtRequest;
import com.ireslab.sendx.electra.repository.ClientRepository;
import com.ireslab.sendx.electra.service.ClientAssetMgmtService;
import com.ireslab.sendx.electra.stellar.StellarTransactionManager;
import com.ireslab.sendx.electra.utils.StellarConstants;
import com.ireslab.sendx.electra.utils.TokenActivity;
import com.ireslab.sendx.electra.utils.TokenOperation;

/**
 * @author iRESlab
 *
 */
@Service
public class ClientAssetMgmtServiceImpl implements ClientAssetMgmtService {

	private static Logger LOG = LoggerFactory.getLogger(ClientAssetMgmtServiceImpl.class);

	@Autowired
	private ClientRepository clientRepository;

	@Autowired
	private StellarTransactionManager stellarTxnManager;
	
	@Autowired
	private CommonServiceImpl commonService;

	@Override
	public void createAccountOnStellar(ClientAssetMgmtRequest clientAssetMgmtRequest) {

		
		ClientAssetToken clientAssetToken = clientAssetMgmtRequest.getClientAssetToken();
		SenderAccount senderAccount = new SenderAccount();
		IssuingAccount issuingAccount = null;
		ReceiverAccount receiverAccount = new ReceiverAccount();

		Client client = clientRepository.findByClientCorrelationId(clientAssetMgmtRequest.getClientCorrelationId());

		StellarTransactionConfigDto stellarTxnConfig = new StellarTransactionConfigDto();
		stellarTxnConfig.setAssetCode(clientAssetToken.getTokenCode());
		stellarTxnConfig.setIsTestNetwork(client.isTestnetAccount());
		stellarTxnConfig.setNoOfTokens(clientAssetMgmtRequest.getNoOfTokens());
		stellarTxnConfig.setAssetLimit(String.valueOf(StellarConstants.ASSET_TRUSTLINE_LIMIT));

		
		issuingAccount = new IssuingAccount().setSecretSeed(client.getIssuingAccountSecretKey())
				.setPublicKey(client.getIssuingAccountPublicKey());
		

		senderAccount.setPublicKey(client.getIssuingAccountPublicKey());
		senderAccount.setSecretSeed(client.getIssuingAccountSecretKey());

		stellarTxnConfig.setIssuingAccount(issuingAccount);
		stellarTxnConfig.setReceiverAccount(new ReceiverAccount().setSecretSeed(client.getBaseTxnAccountPublicKey())
				.setPublicKey(client.getBaseTxnAccountSecretKey()));

		// Receiver Account Details.
		receiverAccount.setPublicKey(client.getBaseTxnAccountPublicKey());
		receiverAccount.setSecretSeed(client.getBaseTxnAccountSecretKey());

		stellarTxnConfig.setSenderAccount(senderAccount);
		stellarTxnConfig.setReceiverAccount(receiverAccount);

		LOG.info("stellarTxnConfig done");

		try {
			LOG.debug("transfering token..!!");
			LOG.debug("creating truse lile..!!");
			SubmitTransactionResponse createTrustLine = stellarTxnManager.createTrustLine(stellarTxnConfig);
			
			
			commonService.saveTransactionDetails(TokenOperation.ChangeTrust, TokenActivity.TRANSFER_TOKENS, "stellarRequest", "stellarResponse",client.getClientName(),
					true, client.getClientCorrelationId(), client.getClientName(), true, 
					client.getClientCorrelationId(), (short) 1, stellarTxnConfig.getNoOfTokens(), stellarTxnConfig.getAssetCode(), createTrustLine.getHash(), null, new Date(), 
					new Date(), "", false, false);
		
			
			
			
			LOG.debug(" truse line created..!!");
			SubmitTransactionResponse transactionResponse = stellarTxnManager.transferToken(stellarTxnConfig);
			
			
			commonService.saveTransactionDetails(TokenOperation.Payment, TokenActivity.TRANSFER_TOKENS, "stellarRequest", "stellarResponse",client.getClientName(),
					true, client.getClientCorrelationId(), client.getClientName(), true, 
					client.getClientCorrelationId(), (short) 1, stellarTxnConfig.getNoOfTokens(), stellarTxnConfig.getAssetCode(), createTrustLine.getHash(), null, new Date(), 
					new Date(), "", false, false);
			
			LOG.debug("token transfered..!! - " + transactionResponse.toString());

		} catch (ApiException exp) {
			LOG.debug("ApiException..!!!");

		} catch (Exception exp) {

			LOG.debug("Exception..!!!");
			exp.printStackTrace();
		}

	}

}
