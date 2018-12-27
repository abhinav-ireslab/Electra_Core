/**
 * 
 */
package com.ireslab.sendx.electra.service.impl;


import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.ireslab.sendx.electra.entity.Country;
import com.ireslab.sendx.electra.entity.SubscriptionPlans;
import com.ireslab.sendx.electra.model.SubscriptionPlanDto;
import com.ireslab.sendx.electra.model.SubscriptionPlanRequest;
import com.ireslab.sendx.electra.model.SubscriptionPlanResponse;
import com.ireslab.sendx.electra.properties.MessagesProperties;
import com.ireslab.sendx.electra.repository.CountryRepository;
import com.ireslab.sendx.electra.repository.SubscriptionPlanRepository;
import com.ireslab.sendx.electra.service.ElectraAdminService;


/**
 * @author iRESlab
 *
 */
@Service
public class ElectraAdminServiceImpl implements ElectraAdminService {

	private static final Logger LOG = LoggerFactory.getLogger(ElectraAdminServiceImpl.class);

	@Autowired
	private ObjectWriter objectWriter;
	
	@Autowired
	private SubscriptionPlanRepository subscriptionRepo;
	
	@Autowired
	private CountryRepository countryRepo;
	
	@Autowired
	private MessagesProperties messageProperties;

	/* (non-Javadoc)
	 * @see com.ireslab.sendx.electra.service.ElectraAdminService#addUpdateSubscriptionPlan(com.ireslab.sendx.electra.model.SubscriptionPlanRequest)
	 */
	@Override
	public SubscriptionPlanResponse addUpdateSubscriptionPlan(SubscriptionPlanRequest subscriptionPlanRequest) {
		SubscriptionPlans subscriptionPlans = null;
		
		SubscriptionPlanResponse subscriptionPlanResponse = new SubscriptionPlanResponse();
		
		String message = null;
		if(subscriptionPlanRequest.getSubscriptionId() != null && !String.valueOf(subscriptionPlanRequest.getSubscriptionId()).isEmpty()) {
			try {
				LOG.debug("Request for update subscription plan : "+objectWriter.writeValueAsString(subscriptionPlanRequest));
			} catch (JsonProcessingException e) {
				
				e.printStackTrace();
			}
			subscriptionPlans = subscriptionRepo.findSubscriptionPlansBySubscriptionId(subscriptionPlanRequest.getSubscriptionId());
			message = messageProperties.updateSubscriptionPlan;
		}
		else {
			
			try {
				LOG.debug("Request for add new subscription plan : "+objectWriter.writeValueAsString(subscriptionPlanRequest));
			} catch (JsonProcessingException e) {
				
				e.printStackTrace();
			}
			subscriptionPlans = new SubscriptionPlans();
			message = messageProperties.addSubscriptionPlan;
		}
		Country country = countryRepo.findOne(subscriptionPlanRequest.getCountryId());
		
		subscriptionPlans.setPlanTitle(subscriptionPlanRequest.getPlanTitle());
		subscriptionPlans.setSupportedUsers(subscriptionPlanRequest.getSupportedUsers());
		subscriptionPlans.setToken(subscriptionPlanRequest.getToken());
		subscriptionPlans.setCountryId(country);
		subscriptionPlans.setValidity(subscriptionPlanRequest.getValidity());
		subscriptionRepo.save(subscriptionPlans);
		
		subscriptionPlanResponse.setCode(100);
		subscriptionPlanResponse.setMessage(message);
		
		
		return subscriptionPlanResponse;
	}

	/* (non-Javadoc)
	 * @see com.ireslab.sendx.electra.service.ElectraAdminService#deleteSubscriptionPlan(com.ireslab.sendx.electra.model.SubscriptionPlanRequest)
	 */
	@Override
	public SubscriptionPlanResponse deleteSubscriptionPlan(SubscriptionPlanRequest subscriptionPlanRequest) {
		SubscriptionPlanResponse subscriptionPlanResponse = new SubscriptionPlanResponse();
		
		SubscriptionPlans subscriptionPlans = subscriptionRepo.findSubscriptionPlansBySubscriptionId(subscriptionPlanRequest.getSubscriptionId());
		if(subscriptionPlans != null) {
			
			subscriptionPlans.setDeleted(true);
			subscriptionRepo.save(subscriptionPlans);
			subscriptionPlanResponse.setCode(100);
			subscriptionPlanResponse.setMessage(messageProperties.deleteSubscriptionPlan);
		}
		else {
			subscriptionPlanResponse.setCode(101);
			subscriptionPlanResponse.setMessage("Subscription plan not found with id");
		}
		return subscriptionPlanResponse;
	}

	/* (non-Javadoc)
	 * @see com.ireslab.sendx.electra.service.ElectraAdminService#subscriptionPlanListForAdmin(org.springframework.data.domain.Pageable)
	 */
	@Override
	public SubscriptionPlanResponse subscriptionPlanListForAdmin(Pageable pageable) {
		LOG.info("Request recieved for subscription plan list for admin  in service ");
		SubscriptionPlanResponse subscriptionPlanResponse = new SubscriptionPlanResponse();
		List<SubscriptionPlanDto> subscriptionPlanDtoList = new ArrayList<>();

		
		List<SubscriptionPlans> planList = subscriptionRepo.findAllCust(pageable);

		for (SubscriptionPlans subscriptionPlans : planList) {
			SubscriptionPlanDto subscriptionPlanDto = new SubscriptionPlanDto();
			subscriptionPlanDto.setPlanTitle(subscriptionPlans.getPlanTitle());
			subscriptionPlanDto.setSubscriptionId(subscriptionPlans.getSubscriptionId());
			subscriptionPlanDto.setSupportedUsers(subscriptionPlans.getSupportedUsers());
			subscriptionPlanDto.setToken(subscriptionPlans.getToken());
			subscriptionPlanDto.setValidity(subscriptionPlans.getValidity());
			subscriptionPlanDtoList.add(subscriptionPlanDto);
		}

		subscriptionPlanResponse.setSubscriptionPlanDto(subscriptionPlanDtoList);
		subscriptionPlanResponse.setCode(100);
		subscriptionPlanResponse.setMessage("SUCCESS");
		subscriptionPlanResponse.setStatus(HttpStatus.OK.value());
		return subscriptionPlanResponse;
	}
	
	
	
	
}
