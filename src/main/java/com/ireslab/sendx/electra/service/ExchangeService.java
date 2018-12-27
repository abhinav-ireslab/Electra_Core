package com.ireslab.sendx.electra.service;

import com.ireslab.sendx.electra.dto.ExchangeDto;
import com.ireslab.sendx.electra.model.ExchangeResponse;

/**
 * @author iRESlab
 *
 */
public interface ExchangeService {
	
	public ExchangeResponse addExchangeDetails(ExchangeDto exchangeDto);
	
	public ExchangeResponse getAllExchangeDetails();

}
