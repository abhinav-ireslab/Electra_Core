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
import com.ireslab.sendx.electra.model.SendxElectraRequest;
import com.ireslab.sendx.electra.model.SendxElectraResponse;
import com.ireslab.sendx.electra.service.CommonService;

/**
 * @author iRESlab
 *
 */
@RestController
@RequestMapping(value = "/v1/settlement/**")
public class SettlementReportController {
	
	private static final Logger LOG = LoggerFactory.getLogger(SettlementReportController.class);

	@Autowired
	private CommonService commonService;
	
	
	/**
	 * use to get all settlement report.
	 * 
	 * @return
	 * @throws JsonProcessingException
	 */
	@RequestMapping(value="getAllSettlementReports",method=RequestMethod.GET)
    public ResponseEntity<SendxElectraResponse> getAllSettlementReports() throws JsonProcessingException{
		LOG.info("Request received to get all settlement reports");
		SendxElectraResponse sendxElectraResponse = commonService.getAllSettlementReports();
		LOG.info("Response sent for get all settlement reports");
		return new ResponseEntity<>(sendxElectraResponse,HttpStatus.OK);
	}
	
	/**
	 * use to update settlement report.
	 *  
	 * @param sendxElectraRequest
	 * @return
	 * @throws JsonProcessingException
	 */
	@RequestMapping(value="updateSettlementReport",method=RequestMethod.POST)
    public ResponseEntity<SendxElectraResponse> updateSettlementReport(@RequestBody SendxElectraRequest sendxElectraRequest) throws JsonProcessingException{
		LOG.info("Request received to update settlement report");
		SendxElectraResponse sendxElectraResponse = commonService.updateSettlementReport(sendxElectraRequest);
		LOG.info("Response sent after update settlement report");
		return new ResponseEntity<>(sendxElectraResponse,HttpStatus.OK);
	}
	
	
	/**
	 * use to get all settlement report by correlation id.
	 * 
	 * @param correlationId
	 * @return
	 * @throws JsonProcessingException
	 */
	@RequestMapping(value="getAllSettlementReports/{correlationId}",method=RequestMethod.GET)
    public ResponseEntity<SendxElectraResponse> getAllNotification(@PathVariable("correlationId") String correlationId) throws JsonProcessingException{
		LOG.info("Request received to get settlement report by correlation id");
		SendxElectraResponse sendxElectraResponse = commonService.getAllSettlementReports(correlationId);
		LOG.info("Response sent after get settlement report by correlation id");
		return new ResponseEntity<>(sendxElectraResponse,HttpStatus.OK);
	}

}
