package com.ireslab.sendx.electra.repository;
import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.ireslab.sendx.electra.entity.CashOut;
import com.ireslab.sendx.electra.entity.Notification;




public interface CashOutRepository extends PagingAndSortingRepository<CashOut,Integer>{

	CashOut findBySettlementId(Integer settlementId);

	List<CashOut> findAllByUserCorrelationId(String correlationId);


	
	
	
	

}
