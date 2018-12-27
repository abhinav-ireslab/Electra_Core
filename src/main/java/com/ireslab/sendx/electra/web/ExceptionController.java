package com.ireslab.sendx.electra.web;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ireslab.sendx.electra.exceptions.ApiException;
import com.ireslab.sendx.electra.model.GenericResponse;
import com.ireslab.sendx.electra.utils.ResponseCode;

/**
 * @author iRESlab
 *
 */
@ControllerAdvice
public class ExceptionController {

	private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionController.class);

	/**
	 * Handle API exception.
	 */
	@ExceptionHandler(ApiException.class)
	public @ResponseBody ResponseEntity<GenericResponse> handleApiException(ApiException exception) {

		LOGGER.error("API Exception | Error description-" + ExceptionUtils.getStackTrace(exception));

		GenericResponse genericResponse = new GenericResponse();

		genericResponse.setErrors(exception.getErrors());
		genericResponse.setCode(exception.getCode() == null ? ResponseCode.FAIL.getCode() : exception.getCode());
		genericResponse.setMessage(exception.getMessage());
		genericResponse.setStatus(exception.getHttpStatus() == null ? HttpStatus.INTERNAL_SERVER_ERROR.value()
				: exception.getHttpStatus().value());

		HttpStatus httpStatus = exception.getHttpStatus();
		if (httpStatus == null) {
			httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
		}

		return new ResponseEntity<>(genericResponse, httpStatus);
	}

	/**
	 * Handle all exception.
	 */
	@ExceptionHandler(Exception.class)
	public @ResponseBody ResponseEntity<GenericResponse> handleAllException(Exception exception) {

		LOGGER.error("Exception | Error description-" + ExceptionUtils.getStackTrace(exception));

		GenericResponse genericResponse = new GenericResponse();

		return new ResponseEntity<GenericResponse>(genericResponse, HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
