package com.ireslab.sendx.electra.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.ireslab.sendx.electra.model.ApprovelRequest;
import com.ireslab.sendx.electra.model.ApprovelResponse;
import com.ireslab.sendx.electra.model.SubscriptionPlanRequest;
import com.ireslab.sendx.electra.model.SubscriptionPlanResponse;
import com.ireslab.sendx.electra.model.TransactionLimitRequest;
import com.ireslab.sendx.electra.model.TransactionLimitResponse;
import com.ireslab.sendx.electra.service.ElectraAdminService;
import com.ireslab.sendx.electra.service.TransactionManagementService;

/**
 * @author iRESlab
 *
 */
@RestController
@RequestMapping(value = "/v1/admin/**")
public class ElectraAdminController {
	
	private static final Logger LOG = LoggerFactory.getLogger(ElectraAdminController.class);

	
	@Autowired
	private ObjectWriter objectWriter;
	
	@Autowired
	private ElectraAdminService adminService;
	
	@Autowired
	private TransactionManagementService txnManagementService;
	
	
	/**
	 * use to get transaction limit data for transactions.
	 * 
	 * @return
	 * @throws JsonProcessingException
	 */
	@RequestMapping(value="transactionLimitData",method=RequestMethod.GET)
	public ResponseEntity<TransactionLimitResponse> transactionLimitData() throws JsonProcessingException{
		LOG.info("Transaction Limit Data Request received : ");
		TransactionLimitResponse transactionLimitResponse =txnManagementService.getTransactionLimitData();
		LOG.info("Response sent for Transaction Limit Data  ");
		
		return new ResponseEntity<>(transactionLimitResponse,HttpStatus.OK);
	}
	
	/**
	 * use to update transaction limit for transactions.
	 * 
	 * @param transactionLimitRequest
	 * @return
	 * @throws JsonProcessingException
	 */
	@RequestMapping(value="updateTransactionLimit",method=RequestMethod.POST)
	public ResponseEntity<TransactionLimitResponse> updateTransactionLimit(@RequestBody TransactionLimitRequest transactionLimitRequest) throws JsonProcessingException{
		LOG.debug("Request received for update transaction limit : "+objectWriter.writeValueAsString(transactionLimitRequest));
		TransactionLimitResponse transactionLimitResponse =txnManagementService.updateTransactionLimit(transactionLimitRequest);
		LOG.info("Response sent for update transaction limit  ");
		return new ResponseEntity<>(transactionLimitResponse,HttpStatus.OK);
	}
	
	
	
	
	/**
	 * use to get ekyc/ekyb approval list of client, agents and users.
	 * 
	 * @return
	 * @throws JsonProcessingException
	 */
	@RequestMapping(value="ekycEkybApprovelList",method=RequestMethod.GET)
	public ResponseEntity<ApprovelResponse> ekycEkybApprovelList() throws JsonProcessingException{
		LOG.info("Request received for ekyc/ekyb approval list ");
		ApprovelResponse approvelResponse =txnManagementService.ekycEkybApprovelList();
		LOG.info("Response sent for ekyc/ekyb approval list");
		return new ResponseEntity<>(approvelResponse,HttpStatus.OK);
	}
	
	/**
	 * use to get ekyb/ekyc approved list of client, agents and users.
	 * 
	 * @return
	 * @throws JsonProcessingException
	 */
	@RequestMapping(value="ekycEkybApprovedList",method=RequestMethod.GET)
	public ResponseEntity<ApprovelResponse> ekycEkybApprovedList() throws JsonProcessingException{
		LOG.info("Request received for ekyc/ekyb approved list  ");
		ApprovelResponse approvelResponse =txnManagementService.ekycEkybApprovedList();
		LOG.info("Response sent for ekyc/ekyb approved list");
		return new ResponseEntity<>(approvelResponse,HttpStatus.OK);
	}
	
	
	
	/**
	 * use to approve ekyc/ekyb of client, agent and users.
	 * 
	 * @param approvelRequest
	 * @return
	 * @throws JsonProcessingException
	 */
	@RequestMapping(value="approveEkycEkyb",method=RequestMethod.POST)
	public ResponseEntity<ApprovelResponse> approveEkycEkyb(@RequestBody ApprovelRequest approvelRequest) throws JsonProcessingException{
		LOG.debug("Request received for approve ekyc/ekyb: "+objectWriter.writeValueAsString(approvelRequest));
		ApprovelResponse approvelResponse =txnManagementService.approveEkycEkyb(approvelRequest);
		LOG.info("Response sent for approve ekyc/ekyb ");
		return new ResponseEntity<>(approvelResponse,HttpStatus.OK);
	}
	
	/**
	 * use to add or update existing subscription plan.
	 * 
	 * @param subscriptionPlanRequest
	 * @return
	 * @throws JsonProcessingException
	 */
	@RequestMapping(value="addUpdateSubscriptionPlan",method=RequestMethod.POST)
	public ResponseEntity<SubscriptionPlanResponse> addUpdateSubscriptionPlan(@RequestBody SubscriptionPlanRequest subscriptionPlanRequest) throws JsonProcessingException{
		LOG.debug("Request for add or update subscription plan : "+objectWriter.writeValueAsString(subscriptionPlanRequest));
		SubscriptionPlanResponse subscriptionPlanResponse =adminService.addUpdateSubscriptionPlan(subscriptionPlanRequest);
		LOG.info("Response sent for add or update subscription plan ");
		return new ResponseEntity<>(subscriptionPlanResponse,HttpStatus.OK);
		
	}
	
	/**
	 * use to delete subscription plan.
	 * 
	 * @param subscriptionPlanRequest
	 * @return
	 * @throws JsonProcessingException
	 */
	@RequestMapping(value="deleteSubscriptionPlan",method=RequestMethod.POST)
	public ResponseEntity<SubscriptionPlanResponse> deleteSubscriptionPlan(@RequestBody SubscriptionPlanRequest subscriptionPlanRequest) throws JsonProcessingException{
		LOG.debug("Request for delete subscription plan : "+objectWriter.writeValueAsString(subscriptionPlanRequest));
		SubscriptionPlanResponse subscriptionPlanResponse =adminService.deleteSubscriptionPlan(subscriptionPlanRequest);
		LOG.info("Response sent for delete subscription plan ");
		return new ResponseEntity<>(subscriptionPlanResponse,HttpStatus.OK);
		
	}
	
	/**
	 * use to get subscription plan list for admin
	 * 
	 * @param pageable
	 * @return
	 * @throws JsonProcessingException
	 */
	@RequestMapping(value="subscriptionPlanListForAdmin",method=RequestMethod.POST)
	public ResponseEntity<SubscriptionPlanResponse> subscriptionPlanListForAdmin(Pageable pageable) throws JsonProcessingException{
		LOG.info("Request for get for subscription plan list for admin ");
		SubscriptionPlanResponse subscriptionPlanResponse =adminService.subscriptionPlanListForAdmin(pageable);
		LOG.info("Response sent for get subscription plan list ");
		return new ResponseEntity<>(subscriptionPlanResponse,HttpStatus.OK);
		
	}
	
	
	
}
