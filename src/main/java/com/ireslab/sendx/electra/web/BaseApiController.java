package com.ireslab.sendx.electra.web;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.ireslab.sendx.electra.model.GenericRequest;
import com.ireslab.sendx.electra.model.GenericResponse;

/**
 * @author iRESlab
 *
 */
@Component
public class BaseApiController {

	private static final Logger LOG = LoggerFactory.getLogger(BaseApiController.class);

	@Autowired
	private ObjectWriter objectWriter;

	/**
	 * @author iRESlab
	 *
	 */
	public enum ApiRequestType {
		USER_REGISTRATION, LOAD_TOKENS, GET_USER_PROFILE, TRANSFER_TOKENS, CASHOUT_TOKENS, GET_USERS_DETAILS, USER_UPDATION,GET_AGENT, AGENT_REGISTER,
		CLIENT_STATUS_UPDATE, USER_STATUS_UPDATE, GET_CLIENT_PROFILE
	}

	/**
	 * @param clientId
	 * @param requestType
	 * @param genericRequest
	 * @throws JsonProcessingException
	 */
	public void nameRequestThread(String clientId, ApiRequestType requestType, GenericRequest genericRequest)
			throws JsonProcessingException {
		nameRequestThread(null, clientId, requestType, genericRequest, null);
	}

	/**
	 * @param clientId
	 * @param requestType
	 * @param genericRequest
	 * @param paramMap
	 * @throws JsonProcessingException
	 */
	public void nameRequestThread(String clientId, ApiRequestType requestType, GenericRequest genericRequest,
			Map<String, String> infoMap) throws JsonProcessingException {
		nameRequestThread(null, clientId, requestType, genericRequest, infoMap);
	}

	/**
	 * @param clientId
	 * @param requestType
	 * @param paramMap
	 * @throws JsonProcessingException
	 */
	public void nameRequestThread(String clientId, ApiRequestType requestType, Map<String, String> paramMap)
			throws JsonProcessingException {
		nameRequestThread(null, clientId, requestType, null, paramMap);
	}

	/**
	 * @param clientId
	 * @param requestType
	 * @throws JsonProcessingException
	 */
	public void nameRequestThread(String clientId, ApiRequestType requestType) throws JsonProcessingException {
		nameRequestThread(null, clientId, requestType, null, null);
	}

	/**
	 * @param threadName
	 * @param clientId
	 * @param requestType
	 * @throws JsonProcessingException
	 */
	public void nameRequestThread(String threadName, String clientId, ApiRequestType requestType,
			GenericRequest genericRequest, Map<String, String> infoMap) throws JsonProcessingException {

		if (threadName == null) {
			threadName = "Client Id=" + clientId;
		}

		if (infoMap != null && !infoMap.isEmpty()) {

			for (Map.Entry<String, String> entry : infoMap.entrySet()) {
				threadName = threadName + " | " + entry.getKey() + "=" + entry.getValue();
			}
		}

		threadName = threadName + " | Request Type=" + requestType;

		Thread.currentThread().setName(threadName);

		if (genericRequest != null) {
			LOG.debug("JSON Request : " + objectWriter.writeValueAsString(genericRequest));
		}
	}

	/**
	 * @param genericResponse
	 * @return
	 */
	protected ResponseEntity<GenericResponse> prepareApiResponse(GenericResponse genericResponse) {

		return new ResponseEntity<GenericResponse>(genericResponse, HttpStatus.OK);
	}
}
