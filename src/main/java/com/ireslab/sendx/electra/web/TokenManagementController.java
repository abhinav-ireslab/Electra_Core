package com.ireslab.sendx.electra.web;

import java.util.HashMap;

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
import com.ireslab.sendx.electra.model.TokenLifecycleManagementRequest;
import com.ireslab.sendx.electra.model.TokenLifecycleManagementResponse;
import com.ireslab.sendx.electra.model.TokenTransferRequest;
import com.ireslab.sendx.electra.model.TokenTransferResponse;
import com.ireslab.sendx.electra.service.CommonService;
import com.ireslab.sendx.electra.service.TokenManagementService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * @author iRESlab
 *
 */
@RestController
@RequestMapping(value = "/v1/{clientCorrelationId}/*", produces = MediaType.APPLICATION_JSON_VALUE)
@Api(value = "tokenManagement", tags = "Token Management", description = "API's to manage tokens of register client's user by a authorized client")
public class TokenManagementController extends BaseApiController {

	private static final Logger LOG = LoggerFactory.getLogger(TokenManagementController.class);

	@Autowired
	private ObjectWriter objectWriter;

	@Autowired
	private TokenManagementService tokenManagementService;
	@Autowired
	private CommonService commonService;
	
	

	
	/**
	 * use to load token in app wallet.
	 * 
	 * @param clientCorrelationId
	 * @param userCorrelationId
	 * @param tokenManagementRequest
	 * @return
	 * @throws JsonProcessingException
	 */
	@SuppressWarnings({ "serial" })
	@RequestMapping(value = "users/{userCorrelationId}/tokens", method = RequestMethod.POST)
	@ApiOperation(notes = " Response Codes : <br>"
			+ "<table class='fullwidth response-messages'><tr><th data-sw-translate> HTTP STATUS</th><th data-sw-translate > APPLICATION CODE</th><th data-sw-translate>REASON</th></tr><tr><td>200</td>"
			+ "<td>200</td>" + "<td>Success</td></tr>"
			+ "<tr><td>412</td><td>1001</td><td>Client token not found</td></tr>"
			+ "<tr><td>412</td><td>1002</td><td>User account not active </td></tr>"
			+ "<tr><td>412</td><td>1003</td><td>User token not active</td></tr>"
			+ "<tr><td>412</td><td>1000</td><td>User not found</td></tr>"
			+ "<tr><td>200</td><td>1301</td><td>Error occurred while creating trustline for user account</td></tr>"
			+ "<tr><td>200</td><td>1102</td><td>Transaction fail while transferring tokens</td></tr>"
			+ "<tr><td>200</td><td>1300</td><td>Destination account doesnot exists</td></tr>"
			+ "<tr><td>200</td><td>1200</td><td>Insufficient account balance</td></tr></table><br>"
			+ "<p style='color=\"red\"'>NOTE : accountStatus and status field not required to sent in this request</p>", response = TokenLifecycleManagementResponse.class, httpMethod = "POST", value = "Load token in user account")

	public ResponseEntity<TokenLifecycleManagementResponse> loadTokens(
			@PathVariable(value = "clientCorrelationId") String clientCorrelationId,
			@PathVariable("userCorrelationId") String userCorrelationId,
			@RequestBody TokenLifecycleManagementRequest tokenManagementRequest) throws JsonProcessingException {

		nameRequestThread(clientCorrelationId, ApiRequestType.LOAD_TOKENS, tokenManagementRequest,
				new HashMap<String, String>() {
					{
						put("UserCorrelationId", userCorrelationId);
						put("TokenCorrelationId", tokenManagementRequest.getTokenCorrelationId());
					}
				});

		tokenManagementRequest.setClientId(clientCorrelationId);
		tokenManagementRequest.setUserCorrelationId(userCorrelationId);
		TokenLifecycleManagementResponse lifecycleManagementResponse = tokenManagementService
				.loadTokens(tokenManagementRequest);

		LOG.info("JSON Response for load tokens - " + objectWriter.writeValueAsString(lifecycleManagementResponse));
		return new ResponseEntity<TokenLifecycleManagementResponse>(lifecycleManagementResponse, HttpStatus.OK);
	}

	/**
	 * use to transfer tokens.
	 * 
	 * @param clientCorrelationId
	 * @param userCorrelationId
	 * @param tokenManagementRequest
	 * @return
	 * @throws JsonProcessingException
	 */
	@SuppressWarnings("serial")
	@RequestMapping(value = "users/{userCorrelationId}/tokens/transfer", method = RequestMethod.POST)
	public ResponseEntity<TokenLifecycleManagementResponse> transferTokens(
			@PathVariable(value = "clientCorrelationId") String clientCorrelationId,
			@PathVariable("userCorrelationId") String userCorrelationId,
			@RequestBody TokenLifecycleManagementRequest tokenManagementRequest) throws JsonProcessingException {

		nameRequestThread(clientCorrelationId, ApiRequestType.TRANSFER_TOKENS, tokenManagementRequest,
				new HashMap<String, String>() {
					{
						put("UserCorrelationId", userCorrelationId);
						put("TokenCorrelationId", tokenManagementRequest.getTokenCorrelationId());
					}
				});

		tokenManagementRequest.setClientId(clientCorrelationId);
		tokenManagementRequest.setUserCorrelationId(userCorrelationId);
		TokenLifecycleManagementResponse lifecycleManagementResponse = tokenManagementService
				.transferTokens(tokenManagementRequest);

		LOG.info("JSON Response for transfer tokens - " + objectWriter.writeValueAsString(lifecycleManagementResponse));
		return new ResponseEntity<TokenLifecycleManagementResponse>(lifecycleManagementResponse, HttpStatus.OK);
	}

	/**
	 * use to cash out tokens from app wallet.
	 * 
	 * @param clientCorrelationId
	 * @param userCorrelationId
	 * @param tokenManagementRequest
	 * @return
	 * @throws JsonProcessingException
	 */
	@SuppressWarnings("serial")
	@RequestMapping(value = "users/{userCorrelationId}/tokens/cashout", method = RequestMethod.POST)
	@ApiOperation(notes = " Response Codes : <br>"
			+ "<table class='fullwidth response-messages'><tr><th data-sw-translate> HTTP STATUS</th><th data-sw-translate > APPLICATION CODE</th><th data-sw-translate>REASON</th></tr><tr><td>200</td>"
			+ "<td>200</td><td>Success</td></tr>"
			+ "<tr><td>412</td><td>1000</td><td>User not found</td></tr><tr><td>412</td><td>1001</td><td>Client token not found</td></tr>"
			+ "<tr><td>412</td><td>1002</td><td>User account not active </td></tr>"
			+ "<tr><td>412</td><td>1003</td><td>User token not active</td></tr>"
			+ "<tr><td>200</td><td>1102</td><td>Transaction fail while transferring tokens</td></tr>"
			+ "<tr><td>200</td><td>1200</td><td>Insufficient account balance</td></tr>"
			+ "<tr><td>200</td><td>1300</td><td>Destination account doesnot exists</td></tr>"
			+ "<tr><td>200</td><td>1301</td><td>Error occurred while creating trustline for user account</td></tr></table><br>"
			+ "<p style='color=\"red\"'>NOTE : accountStatus and status field not required to sent in this request</p>", response = TokenLifecycleManagementResponse.class, httpMethod = "POST", value = "Redeem tokens, transfer from user account to base account")

	public ResponseEntity<TokenLifecycleManagementResponse> cashOutTokens(
			@PathVariable(value = "clientCorrelationId") String clientCorrelationId,
			@PathVariable("userCorrelationId") String userCorrelationId,
			@RequestBody TokenLifecycleManagementRequest tokenManagementRequest) throws JsonProcessingException {

		nameRequestThread(clientCorrelationId, ApiRequestType.CASHOUT_TOKENS, tokenManagementRequest,
				new HashMap<String, String>() {
					{
						put("UserCorrelationId", userCorrelationId);
						put("TokenCorrelationId", tokenManagementRequest.getTokenCorrelationId());
					}
				});

		tokenManagementRequest.setClientId(clientCorrelationId);
		tokenManagementRequest.setUserCorrelationId(userCorrelationId);
		TokenLifecycleManagementResponse lifecycleManagementResponse = tokenManagementService
				.cashOutTokens(tokenManagementRequest);

		LOG.info("JSON Response for cashout - " + objectWriter.writeValueAsString(lifecycleManagementResponse));
		return new ResponseEntity<>(lifecycleManagementResponse, HttpStatus.OK);
	}

	/**
	 * use to block tokens.
	 * 
	 * @param clientCorrelationId
	 * @param userCorrelationId
	 * @param tokenManagementRequest
	 * @return
	 */
	@RequestMapping(value = "users/{userCorrelationId}/account/token/management", method = RequestMethod.POST)
	@ApiOperation(notes = " Response Codes : <br>" + "<table class='fullwidth response-messages'>"
			+ "<tr><th data-sw-translate> HTTP STATUS</th><th data-sw-translate > APPLICATION CODE</th><th data-sw-translate>REASON</th></tr>"
			+ "<tr><td>200</td><td>200</td><td>Success</td></tr>"
			+ "<tr><td>412</td><td>1000</td><td>User not found</td></tr>"
			+ "<tr><td>412</td><td>1001</td><td>Client token not found</td></tr>" + "</table>"
			+ " <p style='color=\"red\"'>NOTE : If accountStatus = true it will update the  status of User account "
			+ "	If accountStatus = false it will update the status of Tokens associated with User account </p>", response = TokenLifecycleManagementResponse.class, httpMethod = "POST", value = "Update client's user account or token status")

	public ResponseEntity<TokenLifecycleManagementResponse> blockTokens(
			@PathVariable(value = "clientCorrelationId") String clientCorrelationId,
			@PathVariable("userCorrelationId") String userCorrelationId,
			@RequestBody TokenLifecycleManagementRequest tokenManagementRequest) {

		LOG.info("Request received for block account or  tokens . . . .");

		tokenManagementRequest.setClientId(clientCorrelationId);
		tokenManagementRequest.setUserCorrelationId(userCorrelationId);

		TokenLifecycleManagementResponse lifecycleManagementResponse = tokenManagementService
				.updateTokenManagement(tokenManagementRequest);

		return new ResponseEntity<>(lifecycleManagementResponse, HttpStatus.OK);
	}

	
	/**
	 * use to transfer purchaser account to client account.
	 * 
	 * @param tokenTransferRequest
	 * @param clientCorrelationId
	 * @return
	 * @throws JsonProcessingException
	 */
	@RequestMapping(value = "transferToClientAccount", method = RequestMethod.POST)
	public ResponseEntity<TokenTransferResponse> transferTokensToClientAccount(
			@RequestBody TokenTransferRequest tokenTransferRequest,@PathVariable(value = "clientCorrelationId") String clientCorrelationId) throws JsonProcessingException {
		TokenTransferResponse tokenTransferResponse =null;
		
		LOG.info("Transfer to Client Account JSON Request - " + objectWriter.writeValueAsString(tokenTransferRequest));
		
		commonService.transferToClientAccount(tokenTransferRequest);
		LOG.info("Response sent after transrfer to client account ");
		return new ResponseEntity<TokenTransferResponse>(tokenTransferResponse, HttpStatus.OK);
	}
	
	/**
	 * use to transfer fee to master account.
	 * 
	 * @param tokenTransferRequest
	 * @param clientCorrelationId
	 * @return
	 * @throws JsonProcessingException
	 */
	@RequestMapping(value = "transferFeeToMasterAccount", method = RequestMethod.POST)
	public ResponseEntity<TokenTransferResponse> transferFeeToMasterAccount(
			@RequestBody TokenTransferRequest tokenTransferRequest,@PathVariable(value = "clientCorrelationId") String clientCorrelationId) throws JsonProcessingException {
		TokenTransferResponse tokenTransferResponse =null;
		
		LOG.info("Transfer fee to Master Account JSON Request - " + objectWriter.writeValueAsString(tokenTransferRequest));
		//System.out.println("Tested..!!");
		//tokenTransferRequest.setClientId(clientCorrelationId);
		commonService.transferFeeToMasterAccount(tokenTransferRequest);
		LOG.info("Response sent after transrfer fee to master account ");
		return new ResponseEntity<TokenTransferResponse>(tokenTransferResponse, HttpStatus.OK);
	}
	
	
}
