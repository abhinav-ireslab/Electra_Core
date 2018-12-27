package com.ireslab.sendx.electra.web;

import java.util.List;

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
import com.ireslab.sendx.electra.dto.ExchangeDto;
import com.ireslab.sendx.electra.model.AccountDetailsResponse;
import com.ireslab.sendx.electra.model.ClientAssetTokenRequest;
import com.ireslab.sendx.electra.model.ClientAssetTokenResponse;
import com.ireslab.sendx.electra.model.ClientInfoRequest;
import com.ireslab.sendx.electra.model.ClientInfoResponse;
import com.ireslab.sendx.electra.model.ClientPageRequest;
import com.ireslab.sendx.electra.model.ClientPageResponse;
import com.ireslab.sendx.electra.model.ClientProfile;
import com.ireslab.sendx.electra.model.ClientProfileResponse;
import com.ireslab.sendx.electra.model.ClientRegistrationRequest;
import com.ireslab.sendx.electra.model.ClientRegistrationResponse;
import com.ireslab.sendx.electra.model.ClientSubscriptionRequest;
import com.ireslab.sendx.electra.model.ClientSubscriptionResponse;
import com.ireslab.sendx.electra.model.ClientSubscriptionUpdateRequest;
import com.ireslab.sendx.electra.model.ClientSubscriptionUpdateResponse;
import com.ireslab.sendx.electra.model.CompanyCodeResponse;
import com.ireslab.sendx.electra.model.CountryListResponse;
import com.ireslab.sendx.electra.model.ExchangeResponse;
import com.ireslab.sendx.electra.model.SubscriptionPlanResponse;
import com.ireslab.sendx.electra.model.UserProfileResponse;
import com.ireslab.sendx.electra.service.ClientAssetTokenService;
import com.ireslab.sendx.electra.service.ClientProfileMgmtApiService;
import com.ireslab.sendx.electra.service.CommonService;
import com.ireslab.sendx.electra.service.ExchangeService;

/**
 * @author iRESlab
 *
 */
@RestController
@RequestMapping(value = "/v1/*", produces = MediaType.APPLICATION_JSON_VALUE)
public class ClientProfileMgmtController extends BaseApiController {

	private static final Logger LOG = LoggerFactory.getLogger(ClientProfileMgmtController.class);

	@Autowired
	private ObjectWriter objectWriter;

	@Autowired
	private ClientProfileMgmtApiService apiService;

	@Autowired
	private ClientAssetTokenService clientAssetTokenService;

	@Autowired
	private CommonService commonService;

	@Autowired
	private ExchangeService exchangeService;

	/**
	 * This API creates a new user account in electra platform.
	 * 
	 * @return
	 * @throws JsonProcessingException
	 */
	@RequestMapping(value = "client", method = RequestMethod.POST)

	public ResponseEntity<ClientRegistrationResponse> registerUser(
			@RequestBody ClientRegistrationRequest registrationRequest) throws JsonProcessingException {

		
		LOG.info("Request received for Client Registration - ");

		ClientRegistrationResponse registrationResponse = apiService.registerUserAccounts(registrationRequest);

		
		LOG.info("Response sent for Client Registration - ");

		return new ResponseEntity<>(registrationResponse, HttpStatus.OK);
	}

	@RequestMapping(value = "client/{clientCorrelationId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public UserProfileResponse getClientDetails(@PathVariable(value = "clientCorrelationId") String clientCorrelationId)
			throws JsonProcessingException {

		UserProfileResponse userProfileResponse = apiService.getClientProfile(clientCorrelationId);

		return userProfileResponse;
	}

	@RequestMapping(value = "/generateCompanyCode", method = RequestMethod.GET)
	public CompanyCodeResponse generateCompanyCode() throws JsonProcessingException {

		LOG.info("Request received for Generating company code ");

		CompanyCodeResponse companyCodeResponse = apiService.generateCompanyCode();

		LOG.info("Response for Generating company code - " + companyCodeResponse.toString());

		return companyCodeResponse;
	}

	/**
	 * use to get client details by company code.
	 * 
	 * @param companyCode
	 * @return
	 * @throws JsonProcessingException
	 */
	@RequestMapping(value = "{companyCode}/clientByCompanyCode", method = RequestMethod.GET)
	public ClientRegistrationResponse getClientByCompanyCode(@PathVariable(value = "companyCode") String companyCode)
			throws JsonProcessingException {

		LOG.info("Request received for get client company code ");

		ClientRegistrationResponse clientRegistrationResponse = apiService.getClientByCompanyCode(companyCode);

		LOG.info("Response for get client company code - " + clientRegistrationResponse.toString());

		return clientRegistrationResponse;
	}

	/**
	 * use to activate client credential by correlation id.
	 * 
	 * @param clientCorrelationId
	 * @param registrationRequest
	 * @return
	 * @throws JsonProcessingException
	 */
	@RequestMapping(value = "activateClientCredential/{clientCorrelationId}", method = RequestMethod.PUT)

	public ResponseEntity<ClientRegistrationResponse> activateClientCredential(
			@PathVariable(value = "clientCorrelationId") String clientCorrelationId,
			@RequestBody ClientRegistrationRequest registrationRequest) throws JsonProcessingException {

		LOG.info("Request received for activating ClientCredential - "
				+ objectWriter.writeValueAsString(registrationRequest));

		ClientRegistrationResponse registrationResponse = apiService.activateclientCredentials(
				(ClientRegistrationRequest) registrationRequest.setClientId(clientCorrelationId));

		LOG.debug("Response sent for Client Registration - " + objectWriter.writeValueAsString(registrationRequest));

		return new ResponseEntity<>(registrationResponse, HttpStatus.OK);
	}

	/**
	 * use to get client details by correlationId.
	 * 
	 * @param clientCorrelationId
	 * @return
	 * @throws JsonProcessingException
	 */
	@RequestMapping(value = "getClientByCorrelationId/{clientCorrelationId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ClientProfile> getClientByCorrelationId(
			@PathVariable(value = "clientCorrelationId") String clientCorrelationId) throws JsonProcessingException {
		LOG.info("Request received for get client details by correlationId - " + clientCorrelationId);
		ClientProfile clientProfile = apiService.getClientByCorrelationId(clientCorrelationId);
		LOG.debug("JSON Response - " + objectWriter.writeValueAsString(clientProfile));

		return new ResponseEntity<>(clientProfile, HttpStatus.OK);
	}

	/**
	 * use to get client details by user name.
	 * 
	 * @param updationRequest
	 * @return
	 * @throws JsonProcessingException
	 */
	@RequestMapping(value = "getClientByUserName", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ClientProfile> getClientByUserName(@RequestBody ClientRegistrationRequest updationRequest)
			throws JsonProcessingException {
		List<ClientProfile> profileList = updationRequest.getClientProfile();

		LOG.info("Request recieved for get client detail by user name : ");
		ClientProfile clientProfile = apiService.getClientByUserName(profileList.get(0).getUserName());
		LOG.debug("JSON Response - " + objectWriter.writeValueAsString(clientProfile));

		return new ResponseEntity<>(clientProfile, HttpStatus.OK);
	}

	/**
	 * use to get client details by emailId.
	 * 
	 * @param updationRequest
	 * @return
	 * @throws JsonProcessingException
	 */
	@RequestMapping(value = "getClientByEmailId", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ClientProfile> getClientByEmailId(@RequestBody ClientRegistrationRequest updationRequest)
			throws JsonProcessingException {
		List<ClientProfile> profileList = updationRequest.getClientProfile();
		LOG.info("Request recieved for get client detail by email Id : "+objectWriter.writeValueAsString(updationRequest));

		ClientProfile clientProfile = apiService.getClientByEmailId(profileList.get(0).getEmailAddress());
		LOG.debug("JSON Response - " + objectWriter.writeValueAsString(clientProfile));

		return new ResponseEntity<>(clientProfile, HttpStatus.OK);
	}

	/**
	 * use to get client details by reset token.
	 * 
	 * @param token
	 * @return
	 * @throws JsonProcessingException
	 */
	@RequestMapping(value = "getClientByResetToken/{token}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ClientProfile> getClientByResetToken(@PathVariable(value = "token") String token)
			throws JsonProcessingException {

		LOG.info("Request recieved for get client detail by reset token : "+token);
		ClientProfile clientProfile = apiService.getClientByResetToken(token);
		LOG.debug("JSON Response - " + objectWriter.writeValueAsString(clientProfile));

		return new ResponseEntity<>(clientProfile, HttpStatus.OK);
	}

	/**
	 * use to update client details.
	 * 
	 * @param clientCorrelationId
	 * @param updationRequest
	 * @return
	 * @throws JsonProcessingException
	 */
	@RequestMapping(value = "client/{clientCorrelationId}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ClientRegistrationResponse> updateUser(
			@PathVariable(value = "clientCorrelationId") String clientCorrelationId,
			@RequestBody ClientRegistrationRequest updationRequest) throws JsonProcessingException {
		// Request Logging
		nameRequestThread(clientCorrelationId, ApiRequestType.USER_UPDATION, updationRequest);
		ClientRegistrationResponse updationResponse = apiService
				.updateUserAccounts((ClientRegistrationRequest) updationRequest.setClientId(clientCorrelationId));

		LOG.debug("Response sent for client Updation - " + clientCorrelationId);

		return new ResponseEntity<>(updationResponse, HttpStatus.OK);
	}

	/**
	 * use to update client account by correlation id.
	 * 
	 * @param clientCorrelationId
	 * @param updationRequest
	 * @return
	 * @throws JsonProcessingException
	 */
	@RequestMapping(value = "updateClient/{clientCorrelationId}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ClientRegistrationResponse> updateClient(
			@PathVariable(value = "clientCorrelationId") String clientCorrelationId,
			@RequestBody ClientRegistrationRequest updationRequest) throws JsonProcessingException {

		LOG.info("Request received for Client Updation - " + clientCorrelationId);

		nameRequestThread(clientCorrelationId, ApiRequestType.CLIENT_STATUS_UPDATE, updationRequest);
		ClientRegistrationResponse updationResponse = apiService
				.updateClient((ClientRegistrationRequest) updationRequest.setClientId(clientCorrelationId));

		LOG.debug("Response sent for Client Updation - " + objectWriter.writeValueAsString(updationResponse));

		return new ResponseEntity<>(updationResponse, HttpStatus.OK);
	}

	/**
	 * use to update client status.by correlation id.
	 * 
	 * @param clientCorrelationId
	 * @param updationRequest
	 * @return
	 * @throws JsonProcessingException
	 */
	@RequestMapping(value = "updateClientStatus/{clientCorrelationId}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ClientRegistrationResponse> updateClientStatus(
			@PathVariable(value = "clientCorrelationId") String clientCorrelationId,
			@RequestBody ClientRegistrationRequest updationRequest) throws JsonProcessingException {

		LOG.info("Request sent for Client Status Updation by correlation id : " + clientCorrelationId+" \n"
				+ objectWriter.writeValueAsString(updationRequest));

		nameRequestThread(clientCorrelationId, ApiRequestType.CLIENT_STATUS_UPDATE, updationRequest);
		ClientRegistrationResponse updationResponse = apiService
				.updateClientStatus((ClientRegistrationRequest) updationRequest.setClientId(clientCorrelationId));

		LOG.debug("Response sent for Client Status Updation - " + objectWriter.writeValueAsString(updationResponse));

		return new ResponseEntity<>(updationResponse, HttpStatus.OK);
	}
	

	/**
	 * use to get client credential by client correlation id.
	 * 
	 * @param clientCorrelationId
	 * @return
	 * @throws JsonProcessingException
	 */
	@RequestMapping(value = "clientCredentials/{clientCorrelationId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ClientProfileResponse ClientCredentials(
			@PathVariable(value = "clientCorrelationId") String clientCorrelationId) throws JsonProcessingException {
        LOG.info("Request received for get client credentials by cliet correlaion id : "+clientCorrelationId);
		ClientProfileResponse clientProfileResponse = apiService.getClientCredentials(clientCorrelationId);
		LOG.debug("JSON Response - " + objectWriter.writeValueAsString(clientProfileResponse));

		return clientProfileResponse;
	}

	// Configure client Asset Token

	/**
	 * use to configure client assets by client correlation id.
	 * 
	 * @param clientCorrelationId
	 * @param clientAssetTokenRequest
	 * @return
	 * @throws JsonProcessingException
	 */
	@RequestMapping(value = "configureClientAssetToken/{clientCorrelationId}", method = RequestMethod.POST)
	public ResponseEntity<ClientAssetTokenResponse> configureClientAssetToken(
			@PathVariable(value = "clientCorrelationId") String clientCorrelationId,
			@RequestBody ClientAssetTokenRequest clientAssetTokenRequest) throws JsonProcessingException {

		LOG.info("Request received for Configuring client Asset Token - "
				+ clientCorrelationId);

		ClientAssetTokenResponse clientAssetTokenResponse = apiService
				.clientAssetTokenConfiguration(clientAssetTokenRequest);

		LOG.debug("Response sent for Configuring client Asset Token - "
				+ objectWriter.writeValueAsString(clientAssetTokenResponse));

		return new ResponseEntity<>(clientAssetTokenResponse, HttpStatus.OK);
	}

	/**
	 * use to get client details by client correlation id.
	 * 
	 * @param clientCorrelationId
	 * @return
	 * @throws JsonProcessingException
	 */
	@RequestMapping(value = "getClientDetails/{clientCorrelationId}", method = RequestMethod.GET)
	public ResponseEntity<ClientProfileResponse> configureClientAssetToken(
			@PathVariable(value = "clientCorrelationId") String clientCorrelationId) throws JsonProcessingException {

		LOG.info("Client Details request received for correlation  - "
				+ clientCorrelationId);

		ClientProfileResponse clientProfileResponse = apiService.getClientDetail(clientCorrelationId);

		return new ResponseEntity<>(clientProfileResponse, HttpStatus.OK);
	}

	// Method to get client all client asset token.
	/**
	 * use to get all client asset tokens.
	 * 
	 * @param clientCorrelationId
	 * @return
	 * @throws JsonProcessingException
	 */
	@RequestMapping(value = "getAllClientAssetToken/{clientCorrelationId}", method = RequestMethod.GET)
	public ResponseEntity<ClientAssetTokenResponse> getAllClientAssetToken(
			@PathVariable(value = "clientCorrelationId") String clientCorrelationId) throws JsonProcessingException {

		LOG.info("Client All Asset Details request received for correlation  - "
				+ objectWriter.writeValueAsString(clientCorrelationId));

		ClientAssetTokenResponse clientAssetTokenResponse = apiService.getAllClientAssetToken(clientCorrelationId);


		return new ResponseEntity<>(clientAssetTokenResponse, HttpStatus.OK);
	}

	/**
	 * use to get all client asset tokens with pagination.
	 * 
	 * @param clientCorrelationId
	 * @param pageable
	 * @return
	 * @throws JsonProcessingException
	 */
	@RequestMapping(value = "getAllClientAssetTokenPages/{clientCorrelationId}", method = RequestMethod.GET)
	public ResponseEntity<ClientAssetTokenResponse> /* Page<ClientAssetToken> */ getAllClientAssetTokenPageData(
			@PathVariable(value = "clientCorrelationId") String clientCorrelationId, Pageable pageable)
			throws JsonProcessingException {

		LOG.info("Client All Asset Details request received for correlation  - "
				+ clientCorrelationId);

		ClientAssetTokenResponse clientAssetTokenResponse = clientAssetTokenService.findByClient(clientCorrelationId,
				pageable);
		

		return new ResponseEntity<>(clientAssetTokenResponse, HttpStatus.OK);
	}

	/**
	 * use to get client details by account public key.
	 * 
	 * @param accountPublicKey
	 * @return
	 * @throws JsonProcessingException
	 */
	@RequestMapping(value = "getAccountDetails/{accountPublicKey}", method = RequestMethod.GET)
	public ResponseEntity<AccountDetailsResponse> getAccountDetails(
			@PathVariable(value = "accountPublicKey") String accountPublicKey) throws JsonProcessingException {
		LOG.info("Request received for get account details by public key - "
				+ accountPublicKey);
		
		AccountDetailsResponse accountDetailsResponse = commonService.getAccountDetails(accountPublicKey);
		return new ResponseEntity<>(accountDetailsResponse, HttpStatus.OK);
	}

	/**
	 * use to add exchange rate for corrency.
	 * 
	 * @param clientCorrelationId
	 * @param clientExchangeRequest
	 * @return
	 * @throws JsonProcessingException
	 */
	@RequestMapping(value = "{clientCorrelationId}/configureExchange", method = RequestMethod.POST)
	public ResponseEntity<ExchangeResponse> configureExchange(
			@PathVariable(value = "clientCorrelationId") String clientCorrelationId,
			@RequestBody ExchangeDto clientExchangeRequest) throws JsonProcessingException {

		LOG.info("Request received for Configuring Exchange ");

		ExchangeResponse exchangeResponse = exchangeService.addExchangeDetails(clientExchangeRequest);

		LOG.debug("Response sent for Exchnage - " + objectWriter.writeValueAsString(exchangeResponse));

		return new ResponseEntity<>(exchangeResponse, HttpStatus.OK);
	}

	/**
	 * use to exchange rate list.
	 * 
	 * @return
	 * @throws JsonProcessingException
	 */
	@RequestMapping(value = "getClientExchangeDetails", method = RequestMethod.GET)
	public ResponseEntity<ExchangeResponse> getAccountDetails() throws JsonProcessingException {

		LOG.info("Request received for ExchangeDetails  - ");
		ExchangeResponse exchangeResponse = exchangeService.getAllExchangeDetails();
		return new ResponseEntity<>(exchangeResponse, HttpStatus.OK);
	}

	/**
	 * use to get active and suspended client list.
	 * 
	 * @param clientCorrelationId
	 * @param clientPageRequest
	 * @return
	 * @throws JsonProcessingException
	 */
	@RequestMapping(value = "getAllClientCustom/{clientCorrelationId}", method = RequestMethod.POST)
	public ClientPageResponse getAllClientCustom(
			@PathVariable(value = "clientCorrelationId") String clientCorrelationId,
			@RequestBody ClientPageRequest clientPageRequest) throws JsonProcessingException {

		LOG.info("Request sent for get all client custom ");

		ClientPageResponse clientResponse = apiService.getAllClientCustom(clientPageRequest);

		LOG.info("Response sent for get all client data");

		return clientResponse;
	}

	/**
	 * use to save client purchased subscription plan in database 
	 * 
	 * @param clientCorrelationId
	 * @param clientSubscriptionRequest
	 * @return
	 * @throws JsonProcessingException
	 */
	@RequestMapping(value = "{clientCorrelationId}/clientSubscriptionPlan", method = RequestMethod.POST)
	public ClientSubscriptionResponse clientSubscriptionPlan(
			@PathVariable(value = "clientCorrelationId") String clientCorrelationId,
			@RequestBody ClientSubscriptionRequest clientSubscriptionRequest) throws JsonProcessingException {

		LOG.info("Request sent for save subscription plan for client ");

		ClientSubscriptionResponse subscriptionResponse = apiService.saveClientSubscriptionPlan(
				(ClientSubscriptionRequest) clientSubscriptionRequest.setClientId(clientCorrelationId));

		LOG.info("Response sent after save client subscription plan");

		return subscriptionResponse;
	}

	/**
	 * use to get client's subscription plan details.
	 * 
	 * @param clientSubscriptionRequest
	 * @return
	 * @throws JsonProcessingException
	 */
	@RequestMapping(value = "{clientCorrelationId}/getClientSubscriptionPlan", method = RequestMethod.POST)
	public ClientSubscriptionResponse getClientSubscriptionPlan(
			@RequestBody ClientSubscriptionRequest clientSubscriptionRequest) throws JsonProcessingException {

		LOG.info("Request sent for get client subscription plan");

		ClientSubscriptionResponse subscriptionResponse = apiService
				.getClientSubscriptionPlan(clientSubscriptionRequest);

		LOG.info("Response sent after get client subscription plan");

		return subscriptionResponse;
	}

	/**
	 * use to identify user registered as client or not.
	 * 
	 * @param clientSubscriptionRequest
	 * @return
	 * @throws JsonProcessingException
	 */
	@RequestMapping(value = "{clientCorrelationId}/isClientOrNot", method = RequestMethod.POST)
	public ClientSubscriptionResponse isClientOrNot(@RequestBody ClientSubscriptionRequest clientSubscriptionRequest)
			throws JsonProcessingException {

		LOG.info("Request sent for check exist as client or not");

		ClientSubscriptionResponse subscriptionResponse = apiService.isClientOrNot(clientSubscriptionRequest);

		LOG.info("Response sent after check exist as client or not ");

		return subscriptionResponse;
	}

	/**
	 * use to update client's subscription plan after registration of invited agent.
	 * 
	 * @param clientSubscriptionUpdateRequest
	 * @return
	 * @throws JsonProcessingException
	 */
	@RequestMapping(value = "{clientCorrelationId}/updateClientSubscriptionPlan", method = RequestMethod.POST)
	public ClientSubscriptionUpdateResponse updateClientSubscriptionPlan(
			@RequestBody ClientSubscriptionUpdateRequest clientSubscriptionUpdateRequest)
			throws JsonProcessingException {

		LOG.info("Request sent for update client subscription plan ");

		ClientSubscriptionUpdateResponse updateResponse = apiService
				.updateClientSubscriptionPlan(clientSubscriptionUpdateRequest);

		LOG.info("Response sent after update client subscription plan");

		return updateResponse;
	}

	/**
	 * use to check provided mail id registered or not.
	 * 
	 * @param clientSubscriptionUpdateRequest
	 * @return
	 * @throws JsonProcessingException
	 */
	@RequestMapping(value = "{clientCorrelationId}/checkMailidRegistered", method = RequestMethod.POST)
	public ClientSubscriptionUpdateResponse checkMailidRegistered(
			@RequestBody ClientSubscriptionUpdateRequest clientSubscriptionUpdateRequest)
			throws JsonProcessingException {

		LOG.info("Request sent for check mailid registered ");

		ClientSubscriptionUpdateResponse updateResponse = apiService
				.checkMailidRegistered(clientSubscriptionUpdateRequest);

		LOG.info("Response sent after check mailid registered");

		return updateResponse;
	}

	/**
	 * use to get country list.
	 * 
	 * @param clientCorrelationId
	 * @return
	 * @throws JsonProcessingException
	 */
	@RequestMapping(value = "getCountryList/{clientCorrelationId}", method = RequestMethod.GET)
	public CountryListResponse getCountryList(@PathVariable(value = "clientCorrelationId") String clientCorrelationId)
			throws JsonProcessingException {

		LOG.info("Request sent for get country list");

		CountryListResponse countryListResponse = commonService.getCountryList(clientCorrelationId);

		LOG.info("Response sent after getting country list");

		return countryListResponse;
	}

	/**
	 * use to get available subscription plan list for client.
	 * 
	 * @param countryId
	 * @return
	 * @throws JsonProcessingException
	 */
	@RequestMapping(value = "{countryId}/getSubscriptionPlanList", method = RequestMethod.GET)
	public SubscriptionPlanResponse getSubscriptionPlanList(@PathVariable(value = "countryId") String countryId) throws JsonProcessingException {

		LOG.debug("Request received for get subscription plan list ");

		SubscriptionPlanResponse subscriptionPlanResponse = apiService.getSubscriptionPlanList(countryId);

		LOG.debug("Response for subscription plan list ");

		return subscriptionPlanResponse;
	}

	/**
	 * use to get client details by company code.
	 * 
	 * @return
	 * @throws JsonProcessingException
	 */
	@RequestMapping(value = "getClientInfo/{companyCode}", method = RequestMethod.GET)
	public ResponseEntity<ClientInfoResponse> getClientInfo() throws JsonProcessingException {

		LOG.info("Request received for ClientInfo   ");
		ClientInfoResponse clientInfo = null;
		return new ResponseEntity<>(clientInfo, HttpStatus.OK);
	}

	/**
	 * use to get client details by email id.
	 * 
	 * @param clientInfoRequest
	 * @return
	 * @throws JsonProcessingException
	 */
	@RequestMapping(value = "clientInformation", method = RequestMethod.POST)
	public ClientInfoResponse clientInformation(@RequestBody ClientInfoRequest clientInfoRequest)
			throws JsonProcessingException {

		LOG.info("Request received for client details info.");
		ClientInfoResponse clientInfoResponse = commonService.getclientInformation(clientInfoRequest);

		LOG.debug("Response send for clientInformation :" + objectWriter.writeValueAsString(clientInfoResponse));
		return clientInfoResponse;
	}

	/**
	 * use to get exchange rate list.
	 * 
	 * @param userCorrelationId
	 * @return
	 * @throws JsonProcessingException
	 */
	@RequestMapping(value = "getUserExchangeDetails/{userCorrelationId}", method = RequestMethod.GET)
	public ResponseEntity<ExchangeResponse> getUserExchangeDetails(
			@PathVariable("userCorrelationId") String userCorrelationId) throws JsonProcessingException {
		LOG.info("Request received for ExchangeDetails ");
		ExchangeResponse exchangeResponse = exchangeService.getAllExchangeDetails();
		return new ResponseEntity<>(exchangeResponse, HttpStatus.OK);
	}

}
