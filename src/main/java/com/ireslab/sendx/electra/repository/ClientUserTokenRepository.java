package com.ireslab.sendx.electra.repository;

import org.springframework.data.repository.CrudRepository;

import com.ireslab.sendx.electra.entity.ClientUserToken;

/**
 * @author Nitin
 *
 */
public interface ClientUserTokenRepository extends CrudRepository<ClientUserToken, Integer> {

	/**
	 * @param userCorrelationId
	 * @param tokenCorrelationId
	 * @return
	 */
	public ClientUserToken findByClientUser_UserCorrelationIdAndClientAssetToken_TokenCorrelationId(
			String userCorrelationId, String tokenCorrelationId);

}
