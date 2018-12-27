package com.ireslab.sendx.electra.service;

import javax.servlet.http.HttpServletResponse;

import org.springframework.data.domain.Pageable;

import com.ireslab.sendx.electra.model.ClientAssetTokenRequest;
import com.ireslab.sendx.electra.model.ClientAssetTokenResponse;
import com.ireslab.sendx.electra.model.ClientCredentials;
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
import com.ireslab.sendx.electra.model.FilterLedgerRequest;
import com.ireslab.sendx.electra.model.FilterLedgerResponse;
import com.ireslab.sendx.electra.model.SubscriptionPlanResponse;
import com.ireslab.sendx.electra.model.UserProfileResponse;


/**
 * @author iRESlab
 *
 */
public interface ClientProfileMgmtApiService {
	
	
    public ClientRegistrationResponse registerUserAccounts(ClientRegistrationRequest userRegistrationReq);
    
    public ClientRegistrationResponse updateUserAccounts(ClientRegistrationRequest clientRegistrationRequest);
    
    public void createClientCredentials(ClientCredentials clientCredentials);

    public ClientProfileResponse getClientCredentials(String clientCorrelationId);
    
    public ClientProfileResponse getClientDetail(String clientCorrelationId);
    
    public ClientAssetTokenResponse clientAssetTokenConfiguration(ClientAssetTokenRequest clientAssetTokenRequest);

	public ClientRegistrationResponse activateclientCredentials(ClientRegistrationRequest clientUpdationRequest);
	
	public ClientAssetTokenResponse getAllClientAssetToken(String clientCorrelationId);

	public ClientRegistrationResponse updateClientStatus(ClientRegistrationRequest clientUpdationRequest);

	public ClientProfile getClientByCorrelationId(String clientCorrelationId);

	public ClientProfile getClientByUserName(String userName);

	public ClientProfile getClientByEmailId(String emailId);

	public ClientProfile getClientByResetToken(String token);

	public ClientRegistrationResponse updateClient(ClientRegistrationRequest clientUpdationRequest);

	public ClientRegistrationResponse getAllClient();

	public ClientPageResponse getAllClientCustom(ClientPageRequest clientPageRequest);

	public CompanyCodeResponse generateCompanyCode();

	public ClientRegistrationResponse getClientByCompanyCode(String companyCode);

	public FilterLedgerResponse getAllFilterLedgerInfo(FilterLedgerRequest filterLedgerRequest,Pageable pageable, HttpServletResponse response);

	public ClientSubscriptionResponse saveClientSubscriptionPlan(ClientSubscriptionRequest clientSubscriptionRequest);

	public SubscriptionPlanResponse getSubscriptionPlanList(String countryId);

	public ClientSubscriptionUpdateResponse updateClientSubscriptionPlan(
			ClientSubscriptionUpdateRequest clientSubscriptionUpdateRequest);

	public ClientSubscriptionResponse getClientSubscriptionPlan(ClientSubscriptionRequest clientSubscriptionRequest);

	public ClientSubscriptionResponse isClientOrNot(ClientSubscriptionRequest clientSubscriptionRequest);

	public ClientSubscriptionUpdateResponse checkMailidRegistered(
			ClientSubscriptionUpdateRequest clientSubscriptionUpdateRequest);

	public FilterLedgerResponse downloadExcelAndCsv(FilterLedgerRequest filterLedgerRequest,
			HttpServletResponse response);

	public UserProfileResponse getClientProfile(String clientCorrelationId);

	public FilterLedgerResponse getOnlineOfflineLedger(FilterLedgerRequest filterLedgerRequest, Pageable pageable,
			HttpServletResponse response);
	
	
		/*public FilterLedgerResponse getAllFilterLedgerInfo(FilterLedgerRequest filterLedgerRequest);*/

}
