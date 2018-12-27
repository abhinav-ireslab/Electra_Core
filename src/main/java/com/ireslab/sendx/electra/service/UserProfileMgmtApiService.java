package com.ireslab.sendx.electra.service;

import javax.servlet.http.HttpServletRequest;

import com.ireslab.sendx.electra.model.SendxElectraRequest;
import com.ireslab.sendx.electra.model.SendxElectraResponse;
import com.ireslab.sendx.electra.model.UserAgentRegistrationRequest;
import com.ireslab.sendx.electra.model.UserAgentRegistrationResponse;
import com.ireslab.sendx.electra.model.UserAgentResponse;
import com.ireslab.sendx.electra.model.UserProfileResponse;
import com.ireslab.sendx.electra.model.UserRegistrationRequest;
import com.ireslab.sendx.electra.model.UserRegistrationResponse;


/**
 * @author iRESlab
 *
 */
public interface UserProfileMgmtApiService {
    /**
     * @param userRegistrationReq
     * @return
     */
    public UserRegistrationResponse registerUserAccounts(
        UserRegistrationRequest userRegistrationReq);

    /**
     * @param clientCorrelationId
     * @param userCorrelationId
     * @return
     */
    public UserProfileResponse getUserProfile(String clientCorrelationId,
        String userCorrelationId);

    /**
     * @param clientCorrelationId
     * @return
     */
    public UserRegistrationResponse findAllUsersByClientId(
        String clientCorrelationId);

    /**
     * @param userUpdationReq
     * @param userCorrelationId 
     * @return
     */
    public UserRegistrationResponse updateUserAccounts(
        UserRegistrationRequest userUpdationReq, String userCorrelationId);


	public UserAgentRegistrationResponse registerUserAgentAccounts(
			UserAgentRegistrationRequest agentRegistrationRequest, HttpServletRequest request);

	

	public UserAgentResponse getAgent(UserAgentRegistrationRequest agentRegistrationRequest);

	public UserAgentRegistrationResponse findAllAgentsByClientId(String userCorrelationId);

	public UserAgentResponse updateUserAgent(UserAgentRegistrationRequest agentUpdationReq);

	public UserRegistrationResponse userClientEntry(UserRegistrationRequest userRegistrationRequest,
			HttpServletRequest request);
	
	public SendxElectraResponse getAllUserTransactionalDetails(SendxElectraRequest sendxElectraRequest);
}
