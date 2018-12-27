package com.ireslab.sendx.electra.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.ireslab.sendx.electra.model.TokenTransferRequest;
import com.ireslab.sendx.electra.model.TokenTransferResponse;
import com.ireslab.sendx.electra.model.TransactionHistoryResponse;
import com.ireslab.sendx.electra.model.TransactionPurposeRequest;
import com.ireslab.sendx.electra.model.TransactionPurposeResponse;
import com.ireslab.sendx.electra.service.TransactionManagementService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * @author iRESlab
 *
 */
@RestController
@RequestMapping(value = "/v1/{clientCorrelationId}/*", produces = MediaType.APPLICATION_JSON_VALUE)
@Api(value="transactionManagement", tags = "Transaction Management", description="API's to fetch transaction history of register client's user by a authorized client")
public class TransactionManagementController extends BaseApiController {

	private static final Logger LOG = LoggerFactory.getLogger(TransactionManagementController.class);

	@Autowired
	private TransactionManagementService txnManagementService;
	
	@Autowired
	private ObjectWriter objectWriter;

	
	/**
	 * use to get transaction history of cash out.
	 * 
	 * @param clientCorrelationId
	 * @param userCorrelationId
	 * @return
	 */
	@RequestMapping(value = "users/{userCorrelationId}/txnhistory", method = RequestMethod.GET)
	@ApiOperation(notes = " Response Codes : <br>"
			+ "<table class='fullwidth response-messages'><tr><th data-sw-translate> HTTP STATUS</th><th data-sw-translate > APPLICATION CODE</th><th data-sw-translate>REASON</th></tr><tr><td>200</td>"
			+ "<td>200</td>"
			+ "<td>Success</td></tr></table>"
			, response = TransactionHistoryResponse.class, httpMethod="GET", value = "Get transaction history for user account")
	public ResponseEntity<TransactionHistoryResponse> cashOutTokensHistory(
			@PathVariable(value = "clientCorrelationId") String clientCorrelationId,
			@PathVariable("userCorrelationId") String userCorrelationId) {

		LOG.info("Request received for transaction history for userCorrelationId - " + userCorrelationId);

		TransactionHistoryResponse transactionHistoryResponse = txnManagementService.cashOutTokensHistory(clientCorrelationId,
				userCorrelationId);

		LOG.info("Response sent for transaction history for userCorrelationId - " + userCorrelationId);
		return new ResponseEntity<TransactionHistoryResponse>(transactionHistoryResponse, HttpStatus.OK);
	}
	
	/**
	 * use to get all transaction purpose list.
	 * 
	 * @param clientCorrelationId
	 * @return
	 * @throws JsonProcessingException
	 */
	@RequestMapping(value = "getAllTransactionPurpose", method = RequestMethod.GET)
	public ResponseEntity<TransactionPurposeResponse> transferTokensToClientAccount(
			@PathVariable("clientCorrelationId") String clientCorrelationId) throws JsonProcessingException {
		
		LOG.info("Getting All Transaction Purposes, client correlation Id - " + clientCorrelationId);
		
		TransactionPurposeResponse transactionPurposeResponse = txnManagementService.getAllTransactionPurpose(clientCorrelationId);
		
		LOG.info("Response sent for purpose of transaction  ");
		
		return new ResponseEntity<TransactionPurposeResponse>(transactionPurposeResponse, HttpStatus.OK);
	}
	
	/**
	 * use to check transaction allow or not according to transaction limit.
	 * 
	 * @param tokenTransferRequest
	 * @return
	 * @throws JsonProcessingException
	 */
	@RequestMapping(value="transactionLimitsForAllowTransfer",method=RequestMethod.POST)
	public ResponseEntity<TokenTransferResponse> transactionLimitsForAllowTransfer(@RequestBody TokenTransferRequest tokenTransferRequest) throws JsonProcessingException{
		LOG.info("Request for check transfer allow or not of source correlation id : "+objectWriter.writeValueAsString(tokenTransferRequest));
		TokenTransferResponse tokenTransferResponse =txnManagementService.transactionLimitsForAllowTransfer(tokenTransferRequest);
		LOG.info("Response sent for purpose of transaction  ");
		return new ResponseEntity<>(tokenTransferResponse,HttpStatus.OK);
	}

	/**
	 * use to add and update existing transaction purpose.
	 * 
	 * @param clientCorrelationId
	 * @param transactionPurposeRequest
	 * @return
	 * @throws JsonProcessingException
	 */
	@RequestMapping(value = "addAndUpdateTransactionPurpose", method = RequestMethod.POST)
	public ResponseEntity<TransactionPurposeResponse> addAndUpdateTransactionPurpose(
			@PathVariable("clientCorrelationId") String clientCorrelationId, @RequestBody TransactionPurposeRequest transactionPurposeRequest ) throws JsonProcessingException {
		
		LOG.info("Request for add and update transaction purpose  "+objectWriter.writeValueAsString(transactionPurposeRequest)+"\n client correlation Id - " + clientCorrelationId);
		
		TransactionPurposeResponse transactionPurposeResponse = txnManagementService.addAndUpdateTransactionPurpose((TransactionPurposeRequest) transactionPurposeRequest.setClientId(clientCorrelationId));
		
		LOG.info("Response for add or update purpose of transaction  ");
		
		return new ResponseEntity<TransactionPurposeResponse>(transactionPurposeResponse, HttpStatus.OK);
	}
	
	/**
	 * use to delete transaction purpose.
	 * 
	 * @param clientCorrelationId
	 * @param transactionPurposeRequest
	 * @return
	 * @throws JsonProcessingException
	 */
	@RequestMapping(value = "deleteTransactionPurpose", method = RequestMethod.POST)
	public ResponseEntity<TransactionPurposeResponse> deleteTransactionPurpose(
			@PathVariable("clientCorrelationId") String clientCorrelationId, @RequestBody TransactionPurposeRequest transactionPurposeRequest ) throws JsonProcessingException {
		
		LOG.debug("Request for add and update transaction purpose  "+objectWriter.writeValueAsString(transactionPurposeRequest)+"\n client correlation Id - " + clientCorrelationId);
		
		TransactionPurposeResponse transactionPurposeResponse = txnManagementService.deleteTransactionPurpose((TransactionPurposeRequest) transactionPurposeRequest.setClientId(clientCorrelationId));
		
		LOG.info("Response for delete purpose of transaction ");
		
		return new ResponseEntity<TransactionPurposeResponse>(transactionPurposeResponse, HttpStatus.OK);
	}
	
	
}
