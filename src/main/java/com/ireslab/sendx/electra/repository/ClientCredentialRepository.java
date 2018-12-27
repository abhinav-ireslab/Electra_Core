package com.ireslab.sendx.electra.repository;

import org.springframework.data.repository.CrudRepository;

import com.ireslab.sendx.electra.entity.ClientCredential;

/**
 * @author Nitin
 *
 */
public interface ClientCredentialRepository extends CrudRepository<ClientCredential, Integer> {
	
	/**
	 * @param clientApiKey
	 * @return
	 */
	public ClientCredential findByClientApiKey(String clientApiKey);

	public ClientCredential findByclientId(Integer clientId);
}
