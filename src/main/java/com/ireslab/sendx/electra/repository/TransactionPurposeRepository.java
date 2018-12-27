/**
 * 
 */
package com.ireslab.sendx.electra.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.ireslab.sendx.electra.entity.ClientTransactionPurpose;


/**
 * @author ireslab
 *
 */
public interface TransactionPurposeRepository extends CrudRepository<ClientTransactionPurpose, Integer>{
	
	public List<ClientTransactionPurpose> findByClientId(Integer clientId);
	
	public ClientTransactionPurpose findByClientPurposeId(Integer clientPurposeId);

}
