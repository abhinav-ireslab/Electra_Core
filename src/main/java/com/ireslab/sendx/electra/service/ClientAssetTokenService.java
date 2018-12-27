package com.ireslab.sendx.electra.service;

import java.util.List;

import org.springframework.data.domain.Pageable;

import com.ireslab.sendx.electra.entity.Client;
import com.ireslab.sendx.electra.entity.ClientAssetToken;
import com.ireslab.sendx.electra.model.ClientAssetTokenResponse;

/**
 * @author iRESlab
 *
 */
public interface ClientAssetTokenService {
	
	public List<ClientAssetToken> findByClient(Client client);
	
	ClientAssetTokenResponse findByClient(String client,Pageable pageable);
	

}
