package com.ireslab.sendx.electra.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.ireslab.sendx.electra.entity.ClientTokenConfiguration;

/**
 * @author Nitin
 *
 */
public interface ClientTokenConfigurationRepository extends CrudRepository<ClientTokenConfiguration, Integer> {

	List<ClientTokenConfiguration> findByClientIdAndTokenId(Integer clientId, Integer tokenId
		);

}
