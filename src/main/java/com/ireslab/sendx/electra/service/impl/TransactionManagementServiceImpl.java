package com.ireslab.sendx.electra.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.ireslab.sendx.electra.dto.EkycEkybApprovelDto;
import com.ireslab.sendx.electra.entity.Client;
import com.ireslab.sendx.electra.entity.ClientTransactionPurpose;
import com.ireslab.sendx.electra.entity.ClientUser;
import com.ireslab.sendx.electra.entity.Country;
import com.ireslab.sendx.electra.entity.Exchange;
import com.ireslab.sendx.electra.entity.TransactionDetail;
import com.ireslab.sendx.electra.entity.TransactionLimit;
import com.ireslab.sendx.electra.model.ApprovelRequest;
import com.ireslab.sendx.electra.model.ApprovelResponse;
import com.ireslab.sendx.electra.model.TokenTransferRequest;
import com.ireslab.sendx.electra.model.TokenTransferResponse;
import com.ireslab.sendx.electra.model.TransactionHistoryResponse;
import com.ireslab.sendx.electra.model.TransactionLimitRequest;
import com.ireslab.sendx.electra.model.TransactionLimitResponse;
import com.ireslab.sendx.electra.model.TransactionPurposeDto;
import com.ireslab.sendx.electra.model.TransactionPurposeRequest;
import com.ireslab.sendx.electra.model.TransactionPurposeResponse;
import com.ireslab.sendx.electra.properties.MessagesProperties;
import com.ireslab.sendx.electra.repository.ClientRepository;
import com.ireslab.sendx.electra.repository.ClientUserRepository;
import com.ireslab.sendx.electra.repository.CountryRepository;
import com.ireslab.sendx.electra.repository.ExchangeRepository;
import com.ireslab.sendx.electra.repository.TransactionDetailRepository;
import com.ireslab.sendx.electra.repository.TransactionLimitRepository;
import com.ireslab.sendx.electra.repository.TransactionPurposeRepository;
import com.ireslab.sendx.electra.service.TransactionManagementService;
import com.ireslab.sendx.electra.utils.CommonUtils;

/**
 * @author iRESlab
 *
 */

@Service
public class TransactionManagementServiceImpl implements TransactionManagementService {

	private static final Logger LOG = LoggerFactory.getLogger(TransactionManagementServiceImpl.class);
	@Autowired
	private MessagesProperties messageProperties;
	@Autowired
	private TransactionDetailRepository txnDetailRepo;
	
	@Autowired
	private TransactionLimitRepository txnLimitRepo;
	
	@Autowired
	private TransactionPurposeRepository txnPurposeRepo;

	@Autowired
	private ClientRepository clientRepo;
	
	@Autowired
	private CountryRepository countryRepository;
	
	@Autowired
	private ClientUserRepository clientUserRepo;
	
	@Autowired
	private TransactionLimitRepository txnLimitRepository;
	
	@Autowired
	private ExchangeRepository exchangeRepository;
	
	
	
	/* (non-Javadoc)
	 * @see com.ireslab.sendx.electra.service.TransactionManagementService#cashOutTokensHistory(java.lang.String, java.lang.String)
	 */
	@Override
	public TransactionHistoryResponse cashOutTokensHistory(String clientCorrelationId, String userCorrelationId) {
		/*LOG.debug("Fetching for transaction history for userCorrelationId - " + userCorrelationId
				+ " clientCorrelationId :" + clientCorrelationId);
		List<TransactionDetail> details = txnDetailRepo
				.findBySenderCorelationIdAndReceiverCorelationIdOrVisaVsersa(clientCorrelationId, userCorrelationId);

		List<TransactionDetailsDto> transactionDetailsDtoList = new ArrayList<>();
		LOG.debug("Iterating transaction history with size - " + details.size());
		details.stream().forEach(detail -> {

			TransactionDetailsDto txnDetails = new TransactionDetailsDto();

			txnDetails.setTransactionDate(CommonUtils.transactionDate(detail.getTransactionDate()));
			txnDetails.setTransactionTime(CommonUtils.transactionTime(detail.getTransactionDate()));
			txnDetails.setTxnDate(detail.getTransactionDate());
			txnDetails.setNoOfTokens(detail.getNoOfTokens());
			txnDetails.setTokenActivity(detail.getTokenActivity().name());
			txnDetails.setTransactionStatus(detail.getTransactionStatus());

			if (detail.getTokenActivity().equals(TokenActivity.LOAD_TOKENS)) {
				Client client = commonService.getClientDetailsByCorrelationId(detail.getSenderCorelationId());

				ClientUser clientUser = commonService.getClientUserByCorrelationId(detail.getReceiverCorelationId());

				txnDetails.setSenderFirstName(client.getClientName());
				txnDetails.setSenderLastName(null);
				txnDetails.setRecieverFirstName(clientUser.getFirstName());
				txnDetails.setRecieverLastName(clientUser.getLastName());

			} else if (detail.getTokenActivity().equals(TokenActivity.REDEEM_TOKENS)) {
				Client client = commonService.getClientDetailsByCorrelationId(detail.getReceiverCorelationId());
				ClientUser clientUser = commonService.getClientUserByCorrelationId(detail.getSenderCorelationId());

				txnDetails.setSenderFirstName(clientUser.getFirstName());
				txnDetails.setSenderLastName(clientUser.getLastName());
				txnDetails.setRecieverFirstName(client.getClientName());
				txnDetails.setRecieverLastName(null);

			}

			transactionDetailsDtoList.add(txnDetails);

		});
		LOG.debug("Sorting transaction history for userCorrelationId - " + userCorrelationId + " clientCorrelationId :"
				+ clientCorrelationId);
		Collections.sort(transactionDetailsDtoList);
		return new TransactionHistoryResponse(HttpStatus.OK.value(), ResponseCode.Success.getCode(), null,
				transactionDetailsDtoList);*/
		 
		 return null;
	}


	/* (non-Javadoc)
	 * @see com.ireslab.sendx.electra.service.TransactionManagementService#getTransactionLimitData()
	 */
	@Override
	public TransactionLimitResponse getTransactionLimitData() {
		TransactionLimitResponse transactionLimitResponse = new TransactionLimitResponse();
		List<TransactionLimit> list = txnLimitRepo.findAll();
		TransactionLimit transactionLimit = list.get(0);
		
		transactionLimitResponse.setDailyLimit(transactionLimit.getDailyLimit());
		transactionLimitResponse.setMonthlyLimit(transactionLimit.getMonthlyLimit());
		transactionLimitResponse.setTransactionsPerDay(transactionLimit.getTransactionsPerDay());
		transactionLimitResponse.setCode(200);
		transactionLimitResponse.setStatus(HttpStatus.OK.value());
		transactionLimitResponse.setMessage("Success");
		
		
		return transactionLimitResponse;
	}


	/* (non-Javadoc)
	 * @see com.ireslab.sendx.electra.service.TransactionManagementService#ekycEkybApprovelList()
	 */
	@Override
	public ApprovelResponse ekycEkybApprovelList() {
		
		ApprovelResponse approvelResponse = new ApprovelResponse();
		List<Client> allClientApprovelPendingList = clientRepo.findAllClientApprovelPending(false);
		List<EkycEkybApprovelDto> clientApprovelDtosList =new ArrayList<>();
		List<EkycEkybApprovelDto> ekycEkybApprovelList = new ArrayList<>();
		for (Client client : allClientApprovelPendingList) {
			EkycEkybApprovelDto  approvelDto =new EkycEkybApprovelDto();
			approvelDto.setCorellationId(client.getClientCorrelationId());
			approvelDto.setClient(true);
			approvelDto.setName(client.getClientName());
			approvelDto.setDate(CommonUtils.formatDate(client.getCreatedDate(), "yyyy-MM-dd"));
			approvelDto.setComment(messageProperties.ekycComment);
			approvelDto.setStatus("pending");
			
			approvelDto.setBusinessId(client.getBusinessId());
			
			
			approvelDto.setMobileNumber("("+client.getCountryDialCode()+")-" + CommonUtils.formatPhoneNumber(client.getContactNumber1()));
			approvelDto.setDocumentFrontPart(client.getScanDocumentFrontPart());
			approvelDto.setDocumentBackPart(client.getScanDocumentBackPart());
			approvelDto.setIdNo(client.getScanDocumentId());
			approvelDto.setAddress(client.getResidentialAddress());
			approvelDto.setProfileImage(client.getProfileImageUrl());
			approvelDto.setUploadedDocument(client.getScanDocumentType());
			approvelDto.setDob(client.getDob());
			if(client.getIdProofImage() == null) {
				approvelDto.setBusinessDocument("");
			}else {
				approvelDto.setBusinessDocument(client.getIdProofImage());
			}
			
			
			clientApprovelDtosList.add(approvelDto);
			ekycEkybApprovelList.add(approvelDto);
		}


		
		List<ClientUser> allClientUserApprovelPendingList = clientUserRepo.findAllClientUserApprovelPending(false);
		List<EkycEkybApprovelDto> clientUserapprovelDtosList =new ArrayList<>();
		for (ClientUser clientUser : allClientUserApprovelPendingList) {
			EkycEkybApprovelDto  approvalDto =new EkycEkybApprovelDto();
			approvalDto.setCorellationId(clientUser.getUserCorrelationId());
			approvalDto.setClient(false);
			approvalDto.setName(clientUser.getFirstName()+" "+clientUser.getLastName());
			approvalDto.setDate(CommonUtils.formatDate(clientUser.getCreatedDate(), "yyyy-MM-dd"));
			approvalDto.setComment(messageProperties.ekycComment);
			approvalDto.setStatus("pending");
			
			approvalDto.setBusinessId(clientUser.getBusinessId());
			//clientUser.getMobileNumber();
			
			approvalDto.setMobileNumber("("+clientUser.getCountryDialCode()+")-" + CommonUtils.formatPhoneNumber(clientUser.getMobileNumber().replace(clientUser.getCountryDialCode(), "")));
			approvalDto.setDocumentFrontPart(clientUser.getScanDocumentFrontPart());
			approvalDto.setDocumentBackPart(clientUser.getScanDocumentBackPart());
			approvalDto.setIdNo(clientUser.getScanDocumentId());
			approvalDto.setAddress(clientUser.getResidentialAddress());
			approvalDto.setProfileImage(clientUser.getProfileImageUrl());
			approvalDto.setUploadedDocument(clientUser.getScanDocumentType());
			approvalDto.setDob(clientUser.getDob());
			if(clientUser.getIdProofImage() == null) {
				approvalDto.setBusinessDocument("");
			}else {
				approvalDto.setBusinessDocument(clientUser.getIdProofImage());
			}
			
			clientUserapprovelDtosList.add(approvalDto);
			ekycEkybApprovelList.add(approvalDto);
		}
		//approvelResponse.setUserEkycEkybApprovelList(clientUserapprovelDtosList);
		approvelResponse.setEkycEkybApprovelList(ekycEkybApprovelList);
		return approvelResponse;
	}


	/* (non-Javadoc)
	 * @see com.ireslab.sendx.electra.service.TransactionManagementService#approveEkycEkyb(com.ireslab.sendx.electra.model.ApprovelRequest)
	 */
	@Override
	public ApprovelResponse approveEkycEkyb(ApprovelRequest approvelRequest) {
		ApprovelResponse approvelResponse =new ApprovelResponse();
		//System.out.println(approvelRequest.isClient());
		if(approvelRequest!=null && approvelRequest.isClient()) {
			Client client = clientRepo.findByClientCorrelationId(approvelRequest.getCorellationId());
			if(client!=null) {
				client.setEkycEkybApproved(true);
				client.setModifiedDate(new Date());
				clientRepo.save(client);
				approvelResponse.setCode(100);
				approvelResponse.setMessage("SUCCESS");
			}
		}else if (approvelRequest!=null && !approvelRequest.isClient()) {
			ClientUser clientUser = clientUserRepo.findByUserCorrelationId(approvelRequest.getCorellationId());
			if(clientUser!=null) {
				clientUser.setEkycEkybApproved(true);
				clientUser.setModifiedDate(new Date());
				clientUserRepo.save(clientUser);
				approvelResponse.setCode(100);
				approvelResponse.setMessage("SUCCESS");
			}
			
		}else {
			approvelResponse.setCode(100);
			approvelResponse.setMessage("Approval failed.");
		}
		
		return approvelResponse;
	}


	/* (non-Javadoc)
	 * @see com.ireslab.sendx.electra.service.TransactionManagementService#updateTransactionLimit(com.ireslab.sendx.electra.model.TransactionLimitRequest)
	 */
	@Override
	public TransactionLimitResponse updateTransactionLimit(TransactionLimitRequest transactionLimitRequest) {
		TransactionLimitResponse transactionLimitResponse = new TransactionLimitResponse();
		TransactionLimit transactionLimit=	txnLimitRepo.findBytransactionLimitId(Integer.parseInt(transactionLimitRequest.getTransactionLimitId()));
		if(transactionLimit!=null) {
			transactionLimit.setDailyLimit(transactionLimitRequest.getDailyLimit());
			transactionLimit.setTransactionsPerDay(transactionLimitRequest.getTransactionsPerDay());
			transactionLimit.setMonthlyLimit(transactionLimitRequest.getMonthlyLimit());
			transactionLimit.setModifiedDate(new Date());
			txnLimitRepo.save(transactionLimit);
			transactionLimitResponse.setCode(100);
			transactionLimitResponse.setMessage("SUCCESS");
		}else {
			transactionLimitResponse.setCode(101);
			transactionLimitResponse.setMessage("Transaction Limit configuration faild. ");
		}
		
		return transactionLimitResponse;
	}


	/* (non-Javadoc)
	 * @see com.ireslab.sendx.electra.service.TransactionManagementService#ekycEkybApprovedList()
	 */
	@Override
	public ApprovelResponse ekycEkybApprovedList() {
		ApprovelResponse approvelResponse = new ApprovelResponse();
		List<Client> allClientApprovelPendingList = clientRepo.findAllClientApprovelPending(true);
		List<EkycEkybApprovelDto> clientApprovelDtosList =new ArrayList<>();
		List<EkycEkybApprovelDto> ekycEkybApprovelList = new ArrayList<>();
		for (Client client : allClientApprovelPendingList) {
			EkycEkybApprovelDto  approvalDto =new EkycEkybApprovelDto();
			approvalDto.setCorellationId(client.getClientCorrelationId());
			approvalDto.setClient(true);
			approvalDto.setName(client.getClientName());
			approvalDto.setDate(CommonUtils.formatDate(client.getCreatedDate(), "yyyy-MM-dd"));
			approvalDto.setComment(messageProperties.ekycComment);
			approvalDto.setStatus("approved");
			clientApprovelDtosList.add(approvalDto);
			ekycEkybApprovelList.add(approvalDto);
		}
		
		
		List<ClientUser> allClientUserApprovelPendingList = clientUserRepo.findAllClientUserApprovelPending(true);
		List<EkycEkybApprovelDto> clientUserapprovelDtosList =new ArrayList<>();
		for (ClientUser clientUser : allClientUserApprovelPendingList) {
			EkycEkybApprovelDto  approvalDto =new EkycEkybApprovelDto();
			approvalDto.setCorellationId(clientUser.getUserCorrelationId());
			approvalDto.setClient(false);
			approvalDto.setName(clientUser.getFirstName()+" "+clientUser.getLastName());
			approvalDto.setDate(CommonUtils.formatDate(clientUser.getCreatedDate(), "yyyy-MM-dd"));
			approvalDto.setComment(messageProperties.ekycComment);	
			approvalDto.setStatus("approved");
			clientUserapprovelDtosList.add(approvalDto);
			ekycEkybApprovelList.add(approvalDto);
		}
		
		
		approvelResponse.setEkycEkybApprovelList(ekycEkybApprovelList);
		return approvelResponse;
	}


	/* (non-Javadoc)
	 * @see com.ireslab.sendx.electra.service.TransactionManagementService#getAllTransactionPurpose(java.lang.String)
	 */
	@Override
	public TransactionPurposeResponse getAllTransactionPurpose(String clientCorrelationId) {
		
		TransactionPurposeResponse transactionPurposeResponse = new TransactionPurposeResponse();;
		List<TransactionPurposeDto> purposeList = new ArrayList<>();
		
		
		Client client = clientRepo.findByClientCorrelationId(clientCorrelationId);
		 
		if(client!= null) {
			List<ClientTransactionPurpose> transactionPurposes =	txnPurposeRepo.findByClientId(client.getClientId());
		
			for(ClientTransactionPurpose transactionPurpose:transactionPurposes ) {
				TransactionPurposeDto transactionPurposeDto = new TransactionPurposeDto();
				transactionPurposeDto.setPurposeId(transactionPurpose.getClientPurposeId());
				transactionPurposeDto.setPurposeTitle(transactionPurpose.getPurposeTitle());
				purposeList.add(transactionPurposeDto);
			}
			
			transactionPurposeResponse.setPurposeList(purposeList);
			transactionPurposeResponse.setCode(100);
			transactionPurposeResponse.setMessage("Success");
		
		}
		else {
			transactionPurposeResponse.setCode(101);
			transactionPurposeResponse.setMessage("Purpose of Transaction not found");
			LOG.debug("Purpose of transaction not found with client correlationId - "+clientCorrelationId);
		}
		
		
		return transactionPurposeResponse;
	}


	/* (non-Javadoc)
	 * @see com.ireslab.sendx.electra.service.TransactionManagementService#transactionLimitsForAllowTransfer(com.ireslab.sendx.electra.model.TokenTransferRequest)
	 */
	@Override
	public TokenTransferResponse transactionLimitsForAllowTransfer(TokenTransferRequest tokenTransferRequest) {
		
		TokenTransferResponse tokenTransferResponse =new TokenTransferResponse();
		
		LOG.debug("Request recieved in service for verify transaction allow or not ");
		Double dailyLimit = 0.0; // 1000
		Double monthlyLimit = 0.0; // 4000
		Integer noOfTran = 0; // 4
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-M-dd");
		
		List<TransactionDetail> transactionDetailList = txnDetailRepo.findAllTransactionOfMonth(tokenTransferRequest.getSenderCorrelationId(), false);
		LOG.debug("transaction Detail List size - "+transactionDetailList.size());
		
		for (TransactionDetail transactionDetail : transactionDetailList) {
			
			
			
			//tokenConversion(String to, String from, String token)
			Country senderCountry = null;
			ClientUser senderUser = clientUserRepo.findByUserCorrelationId(transactionDetail.getSourceCorrelationId());
			if(senderUser == null) {
				Client senderClient = clientRepo.findByClientCorrelationIdCust(transactionDetail.getSourceCorrelationId());
				senderCountry = countryRepository.findCountryByCountryDialCode(senderClient.getCountryDialCode());
			}
			else {
				senderCountry = countryRepository.findCountryByCountryDialCode(senderUser.getCountryDialCode());
			}
			
			
			String transactionAmount = tokenConversion(senderCountry.getIso4217CurrencyAlphabeticCode(), transactionDetail.getAssetCode(), transactionDetail.getTnxData());

			if (simpleDateFormat.format(transactionDetail.getTransactionDate()).equals(simpleDateFormat.format(new Date()))) {
				
				
				dailyLimit = dailyLimit + Double.parseDouble(transactionAmount);
				noOfTran++;
			}
			
			monthlyLimit = monthlyLimit + Double.parseDouble(transactionAmount);

		}
		dailyLimit = dailyLimit + Double.parseDouble(tokenTransferRequest.getNoOfToken());
		monthlyLimit = monthlyLimit + Double.parseDouble(tokenTransferRequest.getNoOfToken());
		
		List<TransactionLimit> findAll = txnLimitRepository.findAll();
		TransactionLimit transactionLimit = findAll.get(0);
		if ((noOfTran >= Integer.parseInt(transactionLimit.getTransactionsPerDay()))
				|| (dailyLimit > Double.parseDouble(transactionLimit.getDailyLimit()))) {
		
			
			tokenTransferResponse.setCode(701);
			
			return tokenTransferResponse;

		}
		
		if ((monthlyLimit > Double.parseDouble(transactionLimit.getMonthlyLimit()))) {
			
			tokenTransferResponse.setCode(702);
			
			return tokenTransferResponse;

		}
		
		
		tokenTransferResponse.setCode(100);
		return tokenTransferResponse;
	}


	/* (non-Javadoc)
	 * @see com.ireslab.sendx.electra.service.TransactionManagementService#addAndUpdateTransactionPurpose(com.ireslab.sendx.electra.model.TransactionPurposeRequest)
	 */
	@Override
	public TransactionPurposeResponse addAndUpdateTransactionPurpose(
			TransactionPurposeRequest transactionPurposeRequest) {
		
		TransactionPurposeResponse transactionPurposeResponse = new TransactionPurposeResponse();
		
		String message = null;
		Client client = clientRepo.findByClientCorrelationIdCust(transactionPurposeRequest.getClientId());
		if(client != null) {
			
			ClientTransactionPurpose clientTransactionPurpose = null;
			
			if(transactionPurposeRequest.getPurposeId() != null) {
				
				clientTransactionPurpose = txnPurposeRepo.findByClientPurposeId(transactionPurposeRequest.getPurposeId());
				message = messageProperties.updateTransactionPurpose;
			}
			else {
				clientTransactionPurpose =  new ClientTransactionPurpose();
				message = messageProperties.addTransactionPurpose;
			}
			
			clientTransactionPurpose.setClientId(client.getClientId());
			clientTransactionPurpose.setPurposeTitle(transactionPurposeRequest.getPurposeTitle());
			clientTransactionPurpose.setCreatedDate(new Date());
			txnPurposeRepo.save(clientTransactionPurpose);
			
			transactionPurposeResponse.setCode(100);
			transactionPurposeResponse.setMessage(message);
			
		}else{
			transactionPurposeResponse.setCode(101);
			transactionPurposeResponse.setMessage("Client not found");
			LOG.debug("Client not found with correlation id : "+transactionPurposeRequest.getClientId());
		}
		
		return transactionPurposeResponse;
	}


	/* (non-Javadoc)
	 * @see com.ireslab.sendx.electra.service.TransactionManagementService#deleteTransactionPurpose(com.ireslab.sendx.electra.model.TransactionPurposeRequest)
	 */
	@Override
	public TransactionPurposeResponse deleteTransactionPurpose(TransactionPurposeRequest transactionPurposeRequest) {

		TransactionPurposeResponse transactionPurposeResponse = new TransactionPurposeResponse();
		ClientTransactionPurpose clientTransactionPurpose = txnPurposeRepo.findByClientPurposeId(transactionPurposeRequest.getPurposeId());
		
		if(clientTransactionPurpose != null) {
			txnPurposeRepo.delete(clientTransactionPurpose);
			
			transactionPurposeResponse.setCode(100);
			transactionPurposeResponse.setMessage(messageProperties.deleteTransactionPurpose);
		}
		else {
			transactionPurposeResponse.setCode(101);
			transactionPurposeResponse.setMessage("Purpose of transaction not found");
			LOG.debug("Purpose of transaction not found  with correlation id "+transactionPurposeRequest.getClientId());
		}
		
		
		return transactionPurposeResponse;
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
