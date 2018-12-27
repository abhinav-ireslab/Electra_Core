/**
 * 
 */
package com.ireslab.sendx.electra.repository;

import java.math.BigInteger;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.ireslab.sendx.electra.entity.ClientUserAgent;

public interface UserAgentRepository extends CrudRepository<ClientUserAgent, Integer> {

	public ClientUserAgent findBymobileNumber(BigInteger mobileNumber);
	
	@Query(value = "SELECT cua FROM ClientUserAgent cua WHERE cua.clientId=:clientId AND cua.status NOT IN ('TERMINATED', 'terminated')")
	 public List<ClientUserAgent> findByClientIdAndStatusNot(@Param("clientId")Integer clientId);
	 
	 public ClientUserAgent findByClientId(Integer clientId);
	 
	

}
