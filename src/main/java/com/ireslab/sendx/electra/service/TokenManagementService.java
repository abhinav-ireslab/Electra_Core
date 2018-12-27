package com.ireslab.sendx.electra.service;

import com.ireslab.sendx.electra.model.TokenLifecycleManagementRequest;
import com.ireslab.sendx.electra.model.TokenLifecycleManagementResponse;

/**
 * @author iRESlab
 *
 */
public interface TokenManagementService {

	/**
	 * @param tokenLifecycleManagementRequest
	 * @return
	 */
	TokenLifecycleManagementResponse loadTokens(TokenLifecycleManagementRequest tokenManagementRequest);

	/**
	 * @param tokenManagementRequest
	 * @return
	 */
	TokenLifecycleManagementResponse cashOutTokens(TokenLifecycleManagementRequest tokenManagementRequest);

	/**
	 * @param tokenManagementRequest
	 * @return
	 */
	TokenLifecycleManagementResponse updateTokenManagement(TokenLifecycleManagementRequest tokenManagementRequest);

	/**
	 * @param tokenManagementRequest
	 * @return
	 */
	TokenLifecycleManagementResponse transferTokens(TokenLifecycleManagementRequest tokenManagementRequest);

	TokenLifecycleManagementResponse transferFeeToMasterAccount(TokenLifecycleManagementRequest tokenManagementRequest);

}
