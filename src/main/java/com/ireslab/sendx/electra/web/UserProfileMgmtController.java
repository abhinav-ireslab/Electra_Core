package com.ireslab.sendx.electra.web;

import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
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
import com.ireslab.sendx.electra.model.ClentAgentInvitationRequest;
import com.ireslab.sendx.electra.model.ClentAgentInvitationResponse;
import com.ireslab.sendx.electra.model.ExchangeResponse;
import com.ireslab.sendx.electra.model.FilterLedgerRequest;
import com.ireslab.sendx.electra.model.FilterLedgerResponse;
import com.ireslab.sendx.electra.model.SendxElectraRequest;
import com.ireslab.sendx.electra.model.SendxElectraResponse;
import com.ireslab.sendx.electra.model.UserAgentRegistrationRequest;
import com.ireslab.sendx.electra.model.UserAgentRegistrationResponse;
import com.ireslab.sendx.electra.model.UserAgentResponse;
import com.ireslab.sendx.electra.model.UserProfile;
import com.ireslab.sendx.electra.model.UserProfileResponse;
import com.ireslab.sendx.electra.model.UserRegistrationRequest;
import com.ireslab.sendx.electra.model.UserRegistrationResponse;
import com.ireslab.sendx.electra.service.ClientProfileMgmtApiService;
import com.ireslab.sendx.electra.service.CommonService;
import com.ireslab.sendx.electra.service.ExchangeService;
import com.ireslab.sendx.electra.service.UserProfileMgmtApiService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * @author iRESlab
 *
 */
@RestController
@RequestMapping(value = "/v1/{clientCorrelationId}/*", produces = MediaType.APPLICATION_JSON_VALUE)
@Api(value = "userRegistration", tags = "Userr Registration", description = "API's to register client's user by a authorized client")
public class UserProfileMgmtController extends BaseApiController {

	private static final Logger LOG = LoggerFactory.getLogger(UserProfileMgmtController.class);

	@Autowired
	private ObjectWriter objectWriter;

	@Autowired
	private UserProfileMgmtApiService userProfileMgmtService;

	@Autowired
	private ExchangeService exchangeService;
	
	@Autowired
	private ClientProfileMgmtApiService apiService;
	
	
	@Autowired
	private CommonService commonService;

	/**
	 * This API creates a new user account in electra platform.
	 * 
	 * @return
	 * @throws JsonProcessingException
	 */
	@RequestMapping(value = "users", method = RequestMethod.POST)
	@ApiOperation(notes = " Response Codes : <br>"
			+ "<table class='fullwidth response-messages'><tr><th data-sw-translate> HTTP STATUS</th><th data-sw-translate > APPLICATION CODE</th><th data-sw-translate>REASON</th></tr><tr><td>400</td>"
			+ "<td>4000</td><td>User List is null or empty</td></tr>"
			+ "<tr><td>200</td><td>1100</td><td>Error while transferring or loading initial lumens</td></tr>"
			+ "<tr><td>200</td><td>1303</td><td>Error while creating account</td></tr><tr><td>200</td><td>200</td>"
			+ "<td>Success</td></tr></table>"

			, response = UserRegistrationResponse.class, httpMethod = "POST", value = "Create a user account")
	public ResponseEntity<UserRegistrationResponse> registerUser(
			@PathVariable(value = "clientCorrelationId") String clientCorrelationId,
			@RequestBody UserRegistrationRequest registrationRequest) throws JsonProcessingException {

		

		nameRequestThread(clientCorrelationId, ApiRequestType.USER_REGISTRATION);
		
		List<UserProfile> userProfileList =  registrationRequest.getUsers();
		UserProfile userProfile = userProfileList.get(0);

		
		LOG.debug("Request received for register user  ");

		UserRegistrationResponse registrationResponse = userProfileMgmtService
				.registerUserAccounts((UserRegistrationRequest) registrationRequest.setClientId(userProfile.getClientCorrelationId()));

		

		return new ResponseEntity<>(registrationResponse, HttpStatus.OK);
	}

	/**
	 * use to register as a agent.
	 * 
	 * @param clientCorrelationId
	 * @param agentRegistrationRequest
	 * @param request
	 * @return
	 * @throws JsonProcessingException
	 */
	@RequestMapping(value = "userAgent", method = RequestMethod.POST)
	public ResponseEntity<UserAgentRegistrationResponse> userAgentRegister(
			@PathVariable(value = "clientCorrelationId") String clientCorrelationId,
			@RequestBody UserAgentRegistrationRequest agentRegistrationRequest, HttpServletRequest request)
			throws JsonProcessingException {

		// Request Logging
		nameRequestThread(clientCorrelationId, ApiRequestType.AGENT_REGISTER, agentRegistrationRequest);

		LOG.debug("Request received for Agent Registration - " + agentRegistrationRequest.toString());

		UserAgentRegistrationResponse agentRegistrationResponse = userProfileMgmtService.registerUserAgentAccounts(
				(UserAgentRegistrationRequest) agentRegistrationRequest.setClientId(clientCorrelationId), request);

		return new ResponseEntity<>(agentRegistrationResponse, HttpStatus.OK);
	}

	
	/**
	 * use to get user or agent details.
	 * 
	 * @param clientCorrelationId
	 * @param userCorrelationId
	 * @return
	 * @throws JsonProcessingException
	 */
	@SuppressWarnings("serial")
	@RequestMapping(value = "users/{userCorrelationId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public UserProfileResponse getUserDetails(@PathVariable(value = "clientCorrelationId") String clientCorrelationId,
			@PathVariable("userCorrelationId") String userCorrelationId) throws JsonProcessingException {

		nameRequestThread(clientCorrelationId, ApiRequestType.GET_USER_PROFILE, new HashMap<String, String>() {
			{
				put("UserCorrelationId", userCorrelationId);
			}
		});

		UserProfileResponse userProfileResponse = userProfileMgmtService.getUserProfile(clientCorrelationId,
				userCorrelationId);

		LOG.debug("User profile response :"+objectWriter.writeValueAsString(userProfileResponse));
		return userProfileResponse;
	}

	
	/**
	 * use to get user agent list. 
	 * 
	 * @param clientCorrelationId
	 * @param agentRegistrationRequest
	 * @return
	 * @throws JsonProcessingException
	 */
	@RequestMapping(value = "getAgent", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public UserAgentResponse getAgent(@PathVariable(value = "clientCorrelationId") String clientCorrelationId,
			@RequestBody UserAgentRegistrationRequest agentRegistrationRequest) throws JsonProcessingException {

		nameRequestThread(clientCorrelationId, ApiRequestType.GET_AGENT, agentRegistrationRequest);

		LOG.debug("Request received for retrieving agent account data - "
				+ objectWriter.writeValueAsString(agentRegistrationRequest));

		UserAgentResponse UserAgentResponse = userProfileMgmtService.getAgent(agentRegistrationRequest);
		LOG.info("Response sent after get user agents list");

		return UserAgentResponse;
	}

	

	
	/**
	 * use to update user details.
	 * 
	 * @param clientCorrelationId
	 * @param userCorrelationId
	 * @param registrationRequest
	 * @return
	 * @throws JsonProcessingException
	 */
	@RequestMapping(value = "usersUpdate/{userCorrelationId}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<UserRegistrationResponse> updateUser(
			@PathVariable(value = "clientCorrelationId") String clientCorrelationId,
			@PathVariable("userCorrelationId") String userCorrelationId,
			@RequestBody UserRegistrationRequest registrationRequest) throws JsonProcessingException {

		nameRequestThread(clientCorrelationId, ApiRequestType.USER_UPDATION, registrationRequest);

		LOG.info("Request received for User Status Updation - " + objectWriter.writeValueAsString(registrationRequest)
				+ ", for clientCorrelationId - " + clientCorrelationId + " and userCorrelationId - "
				+ userCorrelationId);

		// Request Logging

		UserRegistrationResponse registrationResponse = userProfileMgmtService.updateUserAccounts(
				(UserRegistrationRequest) registrationRequest.setClientId(clientCorrelationId), userCorrelationId);
		LOG.info("Response sent after update users details");
		return new ResponseEntity<>(registrationResponse, HttpStatus.OK);
	}
	
	/**
	 * use to save client data as a user's data in database.
	 * 
	 * @param clientCorrelationId
	 * @param registrationRequest
	 * @param request
	 * @return
	 * @throws JsonProcessingException
	 */
	@RequestMapping(value = "userClientEntry", method = RequestMethod.POST)
	public ResponseEntity<UserRegistrationResponse> userClientEntry(
			@PathVariable(value = "clientCorrelationId") String clientCorrelationId,
			@RequestBody UserRegistrationRequest registrationRequest, HttpServletRequest request)
			throws JsonProcessingException {

		// Request Logging
		nameRequestThread(clientCorrelationId, ApiRequestType.USER_REGISTRATION, registrationRequest);

		LOG.debug("Request received for User Client Registration - " + registrationRequest.toString());

		UserRegistrationResponse userRegistrationResponse = userProfileMgmtService.userClientEntry(registrationRequest, request);
		LOG.info("Response sent after save user's details");
		return new ResponseEntity<>(userRegistrationResponse, HttpStatus.OK);
	}


	/**
	 * use to update user agent status.
	 * 
	 * @param agentRegistrationRequest
	 * @return
	 * @throws JsonProcessingException
	 */
	@RequestMapping(value = "updateAgentStatus", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<UserAgentResponse> updateAgentStatus(
			@RequestBody UserAgentRegistrationRequest agentRegistrationRequest) throws JsonProcessingException {
		
		LOG.debug("Response sent for Agent Updation - " + objectWriter.writeValueAsString(agentRegistrationRequest));
		UserAgentResponse userAgentResponse = userProfileMgmtService.updateUserAgent(agentRegistrationRequest);
		LOG.info("Response sent after update user agent status");
		return new ResponseEntity<>(userAgentResponse, HttpStatus.OK);
	}

	
	/**
	 * use to get all users list of client.
	 * 
	 * @param clientCorrelationId
	 * @return
	 * @throws JsonProcessingException
	 */
	@RequestMapping(value = "users", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<UserRegistrationResponse> getAllUsersDetails(
			@PathVariable(value = "clientCorrelationId") String clientCorrelationId) throws JsonProcessingException {
		

		LOG.info("Request received to get the list of all users for client with clientCorrelationID - "
				+ clientCorrelationId);
		nameRequestThread(clientCorrelationId, ApiRequestType.GET_USERS_DETAILS);

		UserRegistrationResponse clientUsersInfoResponse = userProfileMgmtService
				.findAllUsersByClientId(clientCorrelationId);

		LOG.info("Response sent for get all users list by client correlation id");

		return new ResponseEntity<>(clientUsersInfoResponse, HttpStatus.OK);

	}

	/**
	 * use to get user agents list by user correlation id.
	 * 
	 * @param userCorrelationId
	 * @return
	 * @throws JsonProcessingException
	 */
	@RequestMapping(value = "getAgentListByUserCorrelationId/{userCorrelationId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<UserAgentRegistrationResponse> getAgentListByUserCorrelationId(
			@PathVariable(value = "userCorrelationId") String userCorrelationId) throws JsonProcessingException {

		nameRequestThread(userCorrelationId, ApiRequestType.GET_AGENT);
		LOG.info("Request received for get user agent list by user correlation id - "+ userCorrelationId);
		UserAgentRegistrationResponse UserAgentRegResponse = userProfileMgmtService
				.findAllAgentsByClientId(userCorrelationId);

		LOG.info("Response sent for get all user agents list by user correlation id");

		return new ResponseEntity<>(UserAgentRegResponse, HttpStatus.OK);

	}

	
	/**
	 * use to get rate exchange details.
	 * 
	 * @param userCorrelationId
	 * @return
	 * @throws JsonProcessingException
	 */
	@RequestMapping(value = "getUserExchangeDetails/{userCorrelationId}", method = RequestMethod.GET)
	public ResponseEntity<ExchangeResponse> getAccountDetails(
			@PathVariable("userCorrelationId") String userCorrelationId) throws JsonProcessingException {
		LOG.info("Request received for ExchangeDetails ");
		ExchangeResponse exchangeResponse = exchangeService.getAllExchangeDetails();
		LOG.info("Response sent for exchange details ");
		return new ResponseEntity<>(exchangeResponse, HttpStatus.OK);
	}
	


	/**
	 * use to get ledger with search and pagination.
	 * 
	 * @param clientCorrelationId
	 * @param filterLedgerRequest
	 * @param pageable
	 * @param response
	 * @return
	 * @throws JsonProcessingException
	 */
	@RequestMapping(value = "getFilterLedger", method = RequestMethod.POST)
    public ResponseEntity<FilterLedgerResponse> getFilterLedger(
			@PathVariable(value = "clientCorrelationId") String clientCorrelationId,
			@RequestBody FilterLedgerRequest filterLedgerRequest, Pageable pageable, HttpServletResponse response)
			throws JsonProcessingException {

		filterLedgerRequest.setClientCorrelationId(clientCorrelationId);

		LOG.debug("FilterLedgerRequest  received for Client Registration - "
				+ objectWriter.writeValueAsString(filterLedgerRequest));

		FilterLedgerResponse allFilterLedgerInfo = apiService.getAllFilterLedgerInfo(filterLedgerRequest, pageable,
				response);

		LOG.info("Response sent for ledger ");

		return new ResponseEntity<>(allFilterLedgerInfo, HttpStatus.OK);
	}

	/**
	 * use to get client's online offline ledger based on request with pagination.
	 * 
	 * @param clientCorrelationId
	 * @param filterLedgerRequest
	 * @param pageable
	 * @param response
	 * @return
	 * @throws JsonProcessingException
	 */
	@RequestMapping(value = "getOnlineOfflineLedger", method = RequestMethod.POST)
	public ResponseEntity<FilterLedgerResponse> getOnlineOfflineLedger(
			@PathVariable(value = "clientCorrelationId") String clientCorrelationId,
			@RequestBody FilterLedgerRequest filterLedgerRequest, Pageable pageable, HttpServletResponse response)
			throws JsonProcessingException {
		filterLedgerRequest.setClientCorrelationId(clientCorrelationId);

		LOG.debug("Request received for client's online offline ledger - "
				+ objectWriter.writeValueAsString(filterLedgerRequest));

		FilterLedgerResponse filterLedgerResponse = apiService.getOnlineOfflineLedger(filterLedgerRequest, pageable,
				response);

		LOG.info("Response sent for Client's online offline ledger  ");

		return new ResponseEntity<>(filterLedgerResponse, HttpStatus.OK);
	}



/**
 * use to get user transaction details.
 * 
 * @param userCorrelationId
 * @param clientCorrelationId
 * @param sendxElectraRequest
 * @param pageable
 * @return
 * @throws JsonProcessingException
 */
@RequestMapping(value = "getUserTransactionalDetails/{userCorrelationId}", method = RequestMethod.POST)

public ResponseEntity<SendxElectraResponse> getUserTransactionalDetails(@PathVariable(value = "userCorrelationId") String userCorrelationId,
		@PathVariable(value = "clientCorrelationId") String clientCorrelationId,
		@RequestBody SendxElectraRequest sendxElectraRequest,Pageable pageable) throws JsonProcessingException {
	
	sendxElectraRequest.setClientId(clientCorrelationId);
	
	LOG.info("FilterLedgerRequest  received for Client Registration - "
			+ objectWriter.writeValueAsString(sendxElectraRequest));
    SendxElectraResponse allUserTransactionalDetailsResponse = userProfileMgmtService.getAllUserTransactionalDetails(sendxElectraRequest);
    LOG.info("Response sent for user transaction details ");
    return new ResponseEntity<>(allUserTransactionalDetailsResponse, HttpStatus.OK);
}


/**
 * use to save agent invitation in database.
 * 
 * @param clientCorrelationId
 * @param clentAgentInvitationRequest
 * @return
 * @throws JsonProcessingException
 */
@RequestMapping(value = "getClientAgentInvitationDetails", method = RequestMethod.POST)
public ResponseEntity<ClentAgentInvitationResponse> getUserInvitationDetails(
		@PathVariable(value = "clientCorrelationId") String clientCorrelationId,
		@RequestBody ClentAgentInvitationRequest clentAgentInvitationRequest) throws JsonProcessingException {
	
	clentAgentInvitationRequest.setClientId(clientCorrelationId);
	
	LOG.info("Client Agent Invitation received - "
			+ objectWriter.writeValueAsString(clentAgentInvitationRequest));
	ClentAgentInvitationResponse clentAgentInvitationResponse = commonService.saveClientInvitationDetails(clentAgentInvitationRequest);
	LOG.info("Response sent after save agent invitation ");
	return new ResponseEntity<>(clentAgentInvitationResponse, HttpStatus.OK);
}


	/**
	 * use to download excel and csv files.
	 * 
	 * @param clientCorrelationId
	 * @param filterLedgerRequest
	 * @param response
	 * @return
	 * @throws JsonProcessingException
	 */
	@RequestMapping(value = "downloadExcelAndCsv", method = RequestMethod.POST)
	public ResponseEntity<FilterLedgerResponse> downloadExcelAndCsv(
			@PathVariable(value = "clientCorrelationId") String clientCorrelationId,
			@RequestBody FilterLedgerRequest filterLedgerRequest, HttpServletResponse response)
			throws JsonProcessingException {

		filterLedgerRequest.setClientCorrelationId(clientCorrelationId);

		LOG.info("request received for download and csv - " + objectWriter.writeValueAsString(filterLedgerRequest));

		FilterLedgerResponse allFilterLedgerInfo = apiService.downloadExcelAndCsv(filterLedgerRequest, response);

		LOG.info("Response sent after download excel and csv ");
		return new ResponseEntity<>(allFilterLedgerInfo, HttpStatus.OK);
	}



}
