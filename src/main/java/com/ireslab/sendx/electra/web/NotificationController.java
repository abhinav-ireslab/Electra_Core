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
import com.ireslab.sendx.electra.model.NotificationRequest;
import com.ireslab.sendx.electra.model.NotificationResponse;
import com.ireslab.sendx.electra.model.SendxElectraRequest;
import com.ireslab.sendx.electra.model.SendxElectraResponse;
import com.ireslab.sendx.electra.service.CommonService;

/**
 * @author iRESlab
 *
 */
@RestController
@RequestMapping(value = "/v1/notification/**")
public class NotificationController {
	
	
	private static final Logger LOG = LoggerFactory.getLogger(CommonController.class);

	@Autowired
	private CommonService commonService;
	
	
	
	/**
	 * use to save notification data in the database.
	 * 
	 * @param notificationRequest
	 * @return
	 * @throws JsonProcessingException
	 */
	@RequestMapping(value = "saveNotification", method = RequestMethod.POST)
	public ResponseEntity<NotificationResponse> saveNotification(@RequestBody NotificationRequest notificationRequest)
			throws JsonProcessingException {
		LOG.info("Request received for save notification  ");
		NotificationResponse notificationResponse = commonService.saveNotification(notificationRequest);
		LOG.info("Response send after save Notification  " );
		return new ResponseEntity<>(notificationResponse, HttpStatus.OK);
	}
	
	/**
	 * use to delete notification data from database.
	 * 
	 * @param notificationRequest
	 * @return
	 * @throws JsonProcessingException
	 */
	@RequestMapping(value = "deleteNotification", method = RequestMethod.POST)
	public ResponseEntity<NotificationResponse> deleteNotification(@RequestBody NotificationRequest notificationRequest)
			throws JsonProcessingException {
		LOG.info("Request received for delete notification " );
		NotificationResponse notificationResponse = commonService.deleteNotification(notificationRequest);
		LOG.info("Response send after delete notification  ");
		return new ResponseEntity<>(notificationResponse, HttpStatus.OK);
	}
	
	
	/**
	 * use to update notification data.
	 * 
	 * @param sendxElectraRequest
	 * @return
	 * @throws JsonProcessingException
	 */
	@RequestMapping(value="updateNotification",method=RequestMethod.POST)
    public ResponseEntity<SendxElectraResponse> updateNotification(@RequestBody SendxElectraRequest sendxElectraRequest) throws JsonProcessingException{
		LOG.info("Request received to update Notification ");
		SendxElectraResponse sendxElectraResponse = commonService.updateNotification(sendxElectraRequest);
		LOG.info("Response send after update notification  ");
		return new ResponseEntity<>(sendxElectraResponse,HttpStatus.OK);
	}
	
	/**
	 * use to get all notification list.
	 * 
	 * @param correlationId
	 * @return
	 * @throws JsonProcessingException
	 */
	@RequestMapping(value="getAllNotification/{correlationId}",method=RequestMethod.GET)
    public ResponseEntity<SendxElectraResponse> getAllNotification(@PathVariable("correlationId") String correlationId) throws JsonProcessingException{
		LOG.info("Request received to get all Notification list");
		SendxElectraResponse sendxElectraResponse = commonService.getAllNotification(correlationId);
		LOG.info("Response sent for to get all Notification list ");
		return new ResponseEntity<>(sendxElectraResponse,HttpStatus.OK);
	}
	
	

}
