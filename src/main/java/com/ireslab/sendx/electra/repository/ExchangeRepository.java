package com.ireslab.sendx.electra.repository;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.ireslab.sendx.electra.entity.Exchange;

public interface ExchangeRepository extends PagingAndSortingRepository<Exchange,Integer> {

	public List<Exchange> findAll();
	
	public Exchange findByExchangeTokenAndNativeCurrency(String exchangeToken,String nativeCurrency);
}
