package com.ireslab.sendx.electra.service.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jvnet.hk2.annotations.Service;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import com.ireslab.sendx.electra.dto.ExchangeDto;
import com.ireslab.sendx.electra.entity.Exchange;
import com.ireslab.sendx.electra.model.ExchangeResponse;
import com.ireslab.sendx.electra.repository.ExchangeRepository;
import com.ireslab.sendx.electra.service.ExchangeService;
import com.ireslab.sendx.electra.utils.ResponseCode;

/**
 * @author iRESlab
 *
 */
@Service
public class ExchangeServiceImpl implements ExchangeService {

	@Autowired
	private ExchangeRepository exchangeRepo;

	@Autowired
	private ModelMapper modelMapper;

	/* (non-Javadoc)
	 * @see com.ireslab.sendx.electra.service.ExchangeService#addExchangeDetails(com.ireslab.sendx.electra.dto.ExchangeDto)
	 */
	@Override
	public ExchangeResponse addExchangeDetails(ExchangeDto exchangeDto) {

		Exchange exchange = modelMapper.map(exchangeDto, Exchange.class);
		
		exchangeRepo.save(exchange);
		ExchangeResponse exchangeResponse = new ExchangeResponse();
		exchangeResponse.setCode(ResponseCode.Success.getCode());
		return exchangeResponse;
	}

	/* (non-Javadoc)
	 * @see com.ireslab.sendx.electra.service.ExchangeService#getAllExchangeDetails()
	 */
	@Override
	public ExchangeResponse getAllExchangeDetails() {
		List<Exchange> exchangeList = exchangeRepo.findAll();
		
		

		java.lang.reflect.Type targetListType = new TypeToken<List<ExchangeDto>>() {
		}.getType();

		List<ExchangeDto> exchangeDetailsList = modelMapper.map(exchangeList, targetListType);
		ExchangeResponse exchangeResponse = new ExchangeResponse();
		exchangeResponse.setCode(ResponseCode.Success.getCode());
		exchangeResponse.setStatus(HttpStatus.OK.value());
		Set<String> currencyList = new HashSet<>();
		for (Exchange exchange : exchangeList) {
			currencyList.add(exchange.getExchangeToken());
		}
		exchangeResponse.setExchangeList(exchangeDetailsList);
		exchangeResponse.setCurrencyList(currencyList);
		return exchangeResponse;
	}

}
