package com.ireslab.sendx.electra.repository;

import org.springframework.data.repository.CrudRepository;

import com.ireslab.sendx.electra.entity.PaymentTerm;

public interface PaymentTermRepository extends CrudRepository<PaymentTerm, Integer>{

	public PaymentTerm findPaymentTermByPaymentTermId(Integer paymentTermId);
}
