package com.ireslab.sendx.electra.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.auth0.jwt.internal.org.apache.commons.lang3.exception.ExceptionUtils;
import com.ireslab.sendx.electra.exceptions.BusinessException;
import com.nexmo.client.NexmoClient;
import com.nexmo.client.auth.AuthMethod;
import com.nexmo.client.auth.TokenAuthMethod;
import com.nexmo.client.sms.SmsClient;
import com.nexmo.client.sms.SmsSubmissionResult;
import com.nexmo.client.sms.messages.TextMessage;

/**
 * @author Nitin
 *
 */
@Component
@PropertySource(value = "classpath:sms_config.properties")
@ConfigurationProperties
public class SMSService {

	private static final Logger LOG = LoggerFactory.getLogger(SMSService.class);

	private String apiKey;
	private String apiSecret;
	private String fromNumber;
	private String activationCodeMessage;
	private String downloadAppMessage;
    private String shortCodeApiUrl;
	private String shortCodeApiActivationCodeMessage;
	private String electraRemittanceAppUrl;
	private String agentInvitationAppMessage;

	private static final String SHORT_CODE_API_STATUS_SUCCESS = "0";

	private static RestTemplate restTemplate = new RestTemplate();

	/**
	 * @param mobileNumber
	 * @param msg
	 */
	public boolean sendMessage(String mobileNumber, String msg, boolean useShortCodeApi) {

		SmsClient smsClient = new NexmoClient(new TokenAuthMethod(apiKey, apiSecret)).getSmsClient();
		LOG.debug("Sending SMS message : '" + msg + "' to Mobile Number : " + mobileNumber);

		try {

			if (useShortCodeApi) {

				LOG.debug("Using short code API to send SMS message to US Number - " + mobileNumber);

				HttpHeaders headers = new HttpHeaders();
				headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

				MultiValueMap<String, String> requestMap = new LinkedMultiValueMap<String, String>();
				requestMap.add("api_key", apiKey);
				requestMap.add("api_secret", apiSecret);
				requestMap.add("to", mobileNumber);
				requestMap.add("pin", msg);

				HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(
						requestMap, headers);

				ResponseEntity<NexmoShortCodeApiResponse> shortCodeApiResponse = restTemplate
						.postForEntity(shortCodeApiUrl, request, NexmoShortCodeApiResponse.class);

				NexmoShortCodeApiResponseMessage apiResponseMessage = shortCodeApiResponse.getBody().getMessages()
						.get(0);
				if (apiResponseMessage.getStatus().equalsIgnoreCase(SHORT_CODE_API_STATUS_SUCCESS)) {

					LOG.debug(apiResponseMessage.toString());
					return true;
				}

				throw new Exception(apiResponseMessage.getError_text());

			} else {

				LOG.debug("Using Nexmo Normal SMS API to send message to number - " + mobileNumber);

				SmsSubmissionResult[] responses = smsClient
						.submitMessage(new TextMessage(fromNumber, mobileNumber, msg));
				for (SmsSubmissionResult response : responses) {

					if (response.getStatus() != 0 || response.getTemporaryError()) {
						LOG.error("Error occurred while sending Activation Code - " + response.getErrorText());
						return false;
					}
					return true;
				}
			}
		} catch (Exception exp) {
			LOG.error("Exception occurred while sending SMS - " + ExceptionUtils.getStackTrace(exp));
			throw new BusinessException(HttpStatus.OK, 101,
				"INTERNAL_SERVER_ERROR", exp);
		}

		return false;
	}
	
	public boolean sendInvitationMessage(String mobileNumber, String msg) {
		SmsClient smsClient = new NexmoClient(new TokenAuthMethod(apiKey, apiSecret)).getSmsClient();
		LOG.debug("Sending SMS message : '" + msg + "' to Mobile Number : " + mobileNumber);
		
		
		try {
			
			mobileNumber=mobileNumber.replace("+", "");
				if (!isUsCountryDailCode(mobileNumber)) {
					LOG.debug("Using Nexmo Normal SMS API to send message to number - " + mobileNumber);
					SmsSubmissionResult[] responses = smsClient
							.submitMessage(new TextMessage(fromNumber, mobileNumber, msg));
					for (SmsSubmissionResult response : responses) {

						if (response.getStatus() != 0 || response.getTemporaryError()) {
							LOG.error("Error occurred while sending Activation Code - " + response.getErrorText());
							return false;
						}
						return true;
					} 
				}else {
					
					
					
					
					
					
					LOG.debug("Using short code API to send SMS message to US Number - " + mobileNumber);

					HttpHeaders headers = new HttpHeaders();
					headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

					MultiValueMap<String, String> requestMap = new LinkedMultiValueMap<String, String>();
					requestMap.add("api_key", apiKey);
					requestMap.add("api_secret", apiSecret);
					requestMap.add("to", mobileNumber);
					requestMap.add("pin", msg);

					HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(
							requestMap, headers);

					ResponseEntity<NexmoShortCodeApiResponse> shortCodeApiResponse = restTemplate
							.postForEntity(shortCodeApiUrl, request, NexmoShortCodeApiResponse.class);

					NexmoShortCodeApiResponseMessage apiResponseMessage = shortCodeApiResponse.getBody().getMessages()
							.get(0);
					if (apiResponseMessage.getStatus().equalsIgnoreCase(SHORT_CODE_API_STATUS_SUCCESS)) {

						LOG.debug(apiResponseMessage.toString());
						return true;
					}

					throw new Exception(apiResponseMessage.getError_text());
					
					
					
				}
		}catch (Exception exp) {
				LOG.error("Exception occurred while sending SMS - " + ExceptionUtils.getStackTrace(exp));
				throw new BusinessException(HttpStatus.OK, 101,
					"INTERNAL_SERVER_ERROR", exp);
			}
	return false;
	}
	
	public boolean isUsCountryDailCode(String mobileNumber) {
		
		
		if(mobileNumber.charAt(0) == '1') {
			return true;
		}
		
		return false;
	}

	public static void main(String[] args) throws Exception {

		// Testnet - Alan Account
		/*
		 * String API_KEY = "347f7c72"; String API_SECRET = "88d3a0d736fab7f7";
		 */

		// Livenet - Kaustuv Account
		String API_KEY = "d20b9360";
		String API_SECRET = "25341c7f7ab8b886";

		// String TO_NUMBER = "917042410721";

		// 6597762380
		// String TO_NUMBER = "6592353702";

		// Pradeep = 15104693350

		String TO_NUMBER = "918750857419";
		String FROM_NUMBER = "SendX";

		AuthMethod auth = new TokenAuthMethod(API_KEY, API_SECRET);
		NexmoClient client = new NexmoClient(auth);
		System.out.println(FROM_NUMBER);

		SmsSubmissionResult[] responses = client.getSmsClient().submitMessage(
				new TextMessage(FROM_NUMBER, TO_NUMBER, "Please confirm if you receive this message. Nitin !"));
		for (SmsSubmissionResult response : responses) {
			System.out.println(response);
		}
	}

	/**
	 * @param apiKey
	 *            the apiKey to set
	 */
	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}

	/**
	 * @param apiSecret
	 *            the apiSecret to set
	 */
	public void setApiSecret(String apiSecret) {
		this.apiSecret = apiSecret;
	}

	/**
	 * @param fromNumber
	 *            the fromNumber to set
	 */
	public void setFromNumber(String fromNumber) {
		this.fromNumber = fromNumber;
	}

	/**
	 * @param activationCodeMessage
	 *            the activationCodeMessage to set
	 */
	public void setActivationCodeMessage(String activationCodeMessage) {
		this.activationCodeMessage = activationCodeMessage;
	}

	/**
	 * @return the activationCodeMessage
	 */
	public String getActivationCodeMessage() {
		return activationCodeMessage;
	}

	/**
	 * @return the downloadAppMessage
	 */
	public String getDownloadAppMessage() {
		return downloadAppMessage;
	}

	/**
	 * @param downloadAppMessage
	 *            the downloadAppMessage to set
	 */
	public void setDownloadAppMessage(String downloadAppMessage) {
		this.downloadAppMessage = downloadAppMessage;
	}

	/**
	 * @return the shortCodeApiActivationCodeMessage
	 */
	public String getShortCodeApiActivationCodeMessage() {
		return shortCodeApiActivationCodeMessage;
	}

	/**
	 * @param shortCodeApiActivationCodeMessage
	 *            the shortCodeApiActivationCodeMessage to set
	 */
	public void setShortCodeApiActivationCodeMessage(String shortCodeApiActivationCodeMessage) {
		this.shortCodeApiActivationCodeMessage = shortCodeApiActivationCodeMessage;
	}

	/**
	 * @return the shortCodeApiUrl
	 */
	public String getShortCodeApiUrl() {
		return shortCodeApiUrl;
	}

	/**
	 * @param shortCodeApiUrl
	 *            the shortCodeApiUrl to set
	 */
	public void setShortCodeApiUrl(String shortCodeApiUrl) {
		this.shortCodeApiUrl = shortCodeApiUrl;
	}

	/**
	 * @return the electraRemittanceAppUrl
	 */
	public String getElectraRemittanceAppUrl() {
		return electraRemittanceAppUrl;
	}

	/**
	 * @param electraRemittanceAppUrl the electraRemittanceAppUrl to set
	 */
	public void setElectraRemittanceAppUrl(String electraRemittanceAppUrl) {
		this.electraRemittanceAppUrl = electraRemittanceAppUrl;
	}

	/**
	 * @return the agentInvitationAppMessage
	 */
	public String getAgentInvitationAppMessage() {
		return agentInvitationAppMessage;
	}

	/**
	 * @param agentInvitationAppMessage the agentInvitationAppMessage to set
	 */
	public void setAgentInvitationAppMessage(String agentInvitationAppMessage) {
		this.agentInvitationAppMessage = agentInvitationAppMessage;
	}
	
	
}

enum SMS_TYPE {

	SIGNUP_ACTIVATION_CODE
}
