package com.ireslab.sendx.electra.service;

import org.springframework.data.domain.Pageable;

import com.ireslab.sendx.electra.model.SubscriptionPlanRequest;
import com.ireslab.sendx.electra.model.SubscriptionPlanResponse;

/**
 * @author iRESlab
 *
 */
public interface ElectraAdminService {

	SubscriptionPlanResponse addUpdateSubscriptionPlan(SubscriptionPlanRequest subscriptionPlanRequest);

	SubscriptionPlanResponse deleteSubscriptionPlan(SubscriptionPlanRequest subscriptionPlanRequest);

	SubscriptionPlanResponse subscriptionPlanListForAdmin(Pageable pageable);

	
	

}
