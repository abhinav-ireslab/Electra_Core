/**
 * 
 */
package com.ireslab.sendx.electra.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.ireslab.sendx.electra.entity.BankDetails;

/**
 * @author ireslab
 *
 */
public interface BankDetailsRepository  extends CrudRepository<BankDetails, Integer> {
	
	public BankDetails findByAccountNumber(String accountNumber);
	
	public BankDetails findByClientId(Integer clientId);
	
	@Query(value = "SELECT bd FROM BankDetails bd WHERE bd.clientId=:clientId and  bd.isClient =:isClient")
	public BankDetails findByClientIdCust(@Param("clientId")Integer clientId, @Param("isClient") boolean isClient);

}
