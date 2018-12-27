package com.ireslab.sendx.electra.repository;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;
import com.ireslab.sendx.electra.entity.TransactionLimit;

public interface TransactionLimitRepository extends PagingAndSortingRepository<TransactionLimit, Integer>{
public List<TransactionLimit> findAll();

public TransactionLimit findBytransactionLimitId(Integer parseInt);


}
