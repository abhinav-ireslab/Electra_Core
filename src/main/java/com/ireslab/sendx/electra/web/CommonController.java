package com.ireslab.sendx.electra.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.ireslab.sendx.electra.model.BankDetailResponse;
import com.ireslab.sendx.electra.model.BankDetailsRequest;
import com.ireslab.sendx.electra.model.ExchangeRequest;
import com.ireslab.sendx.electra.model.ExchangeResponse;
import com.ireslab.sendx.electra.model.PaymentRequest;
import com.ireslab.sendx.electra.model.PaymentResponse;
import com.ireslab.sendx.electra.model.ProductAvailabilityRequest;
import com.ireslab.sendx.electra.model.ProductAvailabilityResponse;
import com.ireslab.sendx.electra.model.ProductGroupResponse;
import com.ireslab.sendx.electra.model.ProductRequest;
import com.ireslab.sendx.electra.model.ProductResponse;
import com.ireslab.sendx.electra.model.SendxElectraRequest;
import com.ireslab.sendx.electra.model.SendxElectraResponse;
import com.ireslab.sendx.electra.model.TokenTransferRequest;
import com.ireslab.sendx.electra.model.UserProfileResponse;
import com.ireslab.sendx.electra.service.CommonService;

/**
 * @author iRESlab
 *
 */
@RestController
@RequestMapping(value = "/v1/invoice/**")
public class CommonController {

	private static final Logger LOG = LoggerFactory.getLogger(CommonController.class);

	@Autowired
	private ObjectWriter objectWriter;

	@Autowired
	private CommonService commonService;
	
	

	/**
	 * use to get available product list.
	 * 
	 * @param productRequest
	 * @return
	 * @throws JsonProcessingException
	 */
	@RequestMapping(value = "products/{clientCorrelationId}", method = RequestMethod.POST)
	public ResponseEntity<ProductResponse> getProducetList(@RequestBody ProductRequest productRequest)
			throws JsonProcessingException {
		LOG.info("Product Request received : " + objectWriter.writeValueAsString(productRequest));
		ProductResponse productResponse = commonService.getProductList(productRequest);
		LOG.debug("Product Response send : " + objectWriter.writeValueAsString(productResponse));
		return new ResponseEntity<>(productResponse, HttpStatus.OK);
	}
	
	

	/**
	 * use to transfer amount to seller's account and update ledger.
	 * 
	 * @param paymentRequest
	 * @return
	 * @throws JsonProcessingException
	 */
	@RequestMapping(value = "makePayment", method = RequestMethod.POST)
	public ResponseEntity<PaymentResponse> getProducetList(@RequestBody PaymentRequest paymentRequest)
			throws JsonProcessingException {
		LOG.debug("Product Request received : " + objectWriter.writeValueAsString(paymentRequest));
	    PaymentResponse productResponse = commonService.makePayment(paymentRequest);
		LOG.info("Product Response send : " + objectWriter.writeValueAsString(productResponse));
		return new ResponseEntity<>(productResponse, HttpStatus.OK);
	}
	
	/**
	 * use to update offline ledger.
	 * 
	 * @param tokenTransferRequest
	 * @return
	 * @throws JsonProcessingException
	 */
	@RequestMapping(value = "makeOfflinePayment", method = RequestMethod.POST)
	public ResponseEntity<PaymentResponse> makeOfflinePayment(@RequestBody TokenTransferRequest tokenTransferRequest)
			throws JsonProcessingException {
		LOG.debug("Request received for make offline payment : " + objectWriter.writeValueAsString(tokenTransferRequest));
	    PaymentResponse productResponse = commonService.makeOfflinePayment(tokenTransferRequest);
		LOG.info("Product Response send : " + objectWriter.writeValueAsString(productResponse));
		return new ResponseEntity<>(productResponse, HttpStatus.OK);
	}

	/**
	 * use to get currency exchange details.
	 * 
	 * @param exchangeRequest
	 * @return
	 * @throws JsonProcessingException
	 */
	@RequestMapping(value="getExchangeRate",method=RequestMethod.POST)
    public ResponseEntity<ExchangeResponse> getExchangeRate(@RequestBody ExchangeRequest exchangeRequest) throws JsonProcessingException{
		LOG.info("Request received for get exchange rate :"+objectWriter.writeValueAsString(exchangeRequest));
		ExchangeResponse exchangeResponse = commonService.getExchangeRate(exchangeRequest);
		LOG.info("Response sent for exchange rate");
		return new ResponseEntity<>(exchangeResponse,HttpStatus.OK);
	}
	
	/**
	 * use to save purchased product details in database.
	 * 
	 * @param paymentRequest
	 * @return
	 * @throws JsonProcessingException
	 */
	@RequestMapping(value="savePurchasedProduct",method=RequestMethod.POST)
	public ResponseEntity<PaymentResponse> savePurchasedProduct(@RequestBody PaymentRequest paymentRequest) throws JsonProcessingException{
		LOG.info("Product Request for save received ");
	    PaymentResponse paymentResponse = commonService.savePurchasedProduct(paymentRequest);
	    LOG.info("Response sent save purchased product : "+objectWriter.writeValueAsString(paymentResponse));
		return new ResponseEntity<>(paymentResponse, HttpStatus.OK);
	}
	
	/**
	 * use to update invoiced quantity of product.
	 * 
	 * @param paymentRequest
	 * @return
	 * @throws JsonProcessingException
	 */
	@RequestMapping(value="updateInvoicedProduct",method=RequestMethod.POST)
	public ResponseEntity<PaymentResponse> updateInvoicedProduct(@RequestBody PaymentRequest paymentRequest) throws JsonProcessingException{
		LOG.info("Request recieved for update invoiced product qty : "+objectWriter.writeValueAsString(paymentRequest));
	    PaymentResponse paymentResponse = commonService.updateInvoicedProduct(paymentRequest);
	    LOG.info("Response sent for update invoiced product qty : "+objectWriter.writeValueAsString(paymentResponse));
		return new ResponseEntity<>(paymentResponse, HttpStatus.OK);
	}

	/**
	 * use to check product availability.
	 * 
	 * @param productAvailabilityRequest
	 * @return
	 * @throws JsonProcessingException
	 */
	@RequestMapping(value = "checkProductAvailability", method = RequestMethod.POST)
	public ResponseEntity<ProductAvailabilityResponse> checkProductAvailability(
			@RequestBody ProductAvailabilityRequest productAvailabilityRequest) throws JsonProcessingException {
		LOG.info("Request received check product availability ");
		ProductAvailabilityResponse checkProductAvailability = commonService
				.checkProductAvailability(productAvailabilityRequest);
		LOG.info("Response sent for check product availability ");
		return new ResponseEntity<>(checkProductAvailability, HttpStatus.OK);
	}

	/**
	 * use to get product group.
	 * 
	 * @return
	 * @throws JsonProcessingException
	 */
	@RequestMapping(value = "getProductGroups", method = RequestMethod.GET)
	public ResponseEntity<ProductGroupResponse> getProductGroupList()
			throws JsonProcessingException {
		LOG.info("Get ProductGroup Request received ");
		ProductGroupResponse productGroupResponse = commonService.getProductGroupList();
		LOG.info("Response sent for product group ");
		return new ResponseEntity<>(productGroupResponse, HttpStatus.OK);
	}

	
/**
 * use to save bank details of client.
 * 
 * @param bankDetailsRequest
 * @return
 * @throws JsonProcessingException
 */
@RequestMapping(value="saveBankDetails",method=RequestMethod.POST)
	public ResponseEntity<BankDetailResponse> saveBankDetails(@RequestBody BankDetailsRequest bankDetailsRequest) throws JsonProcessingException{
		LOG.info("Request for save bank details received ");
		BankDetailResponse bankDetailResponse = commonService.saveBankDetails(bankDetailsRequest);
		LOG.info("Response sent for bank details ");
		return new ResponseEntity<>(bankDetailResponse,HttpStatus.OK);
		
	}

/**
 * use to get bank details of client by mail id.
 * 
 * @param bankDetailsRequest
 * @return
 * @throws JsonProcessingException
 */
@RequestMapping(value="getBankDetailsByClientEmail",method=RequestMethod.POST)
public ResponseEntity<BankDetailResponse> getBankDetailsByClientEmail(@RequestBody BankDetailsRequest bankDetailsRequest) throws JsonProcessingException{
	LOG.info("Request for get Bank details received : "+objectWriter.writeValueAsString(bankDetailsRequest));
	BankDetailResponse bankDetailResponse = commonService.getBankDetailsByClientEmail(bankDetailsRequest);
	LOG.info("Response sent  for get Bank details  ");
	return new ResponseEntity<>(bankDetailResponse,HttpStatus.OK);
	
}
	
	
	
	/**
	 * use to get client or user details by unique code.
	 * 
	 * @param uniqueCode
	 * @return
	 * @throws JsonProcessingException
	 */
	@RequestMapping(value = "getClientOrUserDetailsByUniqueCode/{uniqueCode}", method = RequestMethod.GET)
	public ResponseEntity<UserProfileResponse> getClientOrUserDetailsByUniqueCode(@PathVariable("uniqueCode") String uniqueCode)
			throws JsonProcessingException {
		LOG.info("To get Client Or User Details By UniqueCode Request received : " + uniqueCode);
		UserProfileResponse userProfileResponse = commonService.getClientOrUserDetailsByUniqueCode(uniqueCode);
		LOG.info("Response sent for client or user details by unique code  ");
		return new ResponseEntity<>(userProfileResponse, HttpStatus.OK);
	}
	
	
	/**
	 *  use to get client or user details by mobile number.
	 * 
	 * @param mobileNumber
	 * @param countryDailCode
	 * @return
	 * @throws JsonProcessingException
	 */
	@RequestMapping(value = "getClientOrUserDetailsMobileNumber/{countryDailCode}/{mobileNumber}", method = RequestMethod.GET)
	public ResponseEntity<UserProfileResponse> getClientOrUserDetailsByMobileNumber(@PathVariable("mobileNumber") String mobileNumber,@PathVariable("countryDailCode") String countryDailCode)
			throws JsonProcessingException {
		LOG.info("To get Client Or User Details By MobileNumber Request received : " + objectWriter.writeValueAsString(mobileNumber));
		UserProfileResponse userProfileResponse = commonService.getClientOrUserDetailsByMobileNumber(countryDailCode,mobileNumber);
		LOG.info("Response sent for client or user details by mobile number  ");
		return new ResponseEntity<>(userProfileResponse, HttpStatus.OK);
	}
	
	
	
	/**
	 * use to update specific parameter for send push notification.
	 * 
	 * @param sendxElectraRequest
	 * @return
	 * @throws JsonProcessingException
	 */
	@RequestMapping(value = "updateDeviceSpecificParameter", method = RequestMethod.POST)
	public ResponseEntity<SendxElectraResponse> updateDeviceSpecificParameter(@RequestBody SendxElectraRequest sendxElectraRequest)
			throws JsonProcessingException {
		LOG.debug("Request received update device specific parameter : " + objectWriter.writeValueAsString(sendxElectraRequest));
		SendxElectraResponse sendxElectraResponse = commonService.updateDeviceSpecificParameter(sendxElectraRequest);
		LOG.info("Response sent for update device specific parameter");
		return new ResponseEntity<>(sendxElectraResponse, HttpStatus.OK);
	}

}
