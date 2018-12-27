package com.ireslab.sendx.electra.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.ireslab.sendx.electra.entity.Client;
import com.ireslab.sendx.electra.entity.ClientAssetToken;
import com.ireslab.sendx.electra.model.ClientAssetTokenRequest;
import com.ireslab.sendx.electra.model.ClientAssetTokenResponse;
import com.ireslab.sendx.electra.repository.ClientAssetTokenRepository;
import com.ireslab.sendx.electra.repository.ClientRepository;
import com.ireslab.sendx.electra.service.ClientAssetTokenService;
import com.ireslab.sendx.electra.utils.CommonUtils;


/**
 * @author iRESlab
 *
 */
@Service
public class ClientAssetTokenServiceImpl implements ClientAssetTokenService {
	
	
	@Autowired
	private ClientAssetTokenRepository clientAssetTokenRepo;
	
	@Autowired
	private ClientRepository clientRepository;

	@Override
	public List<ClientAssetToken> findByClient(Client client) {
		
		
		return clientAssetTokenRepo.findByClient(client);
	}

	@Override
	public ClientAssetTokenResponse findByClient(String clientId, Pageable pageable) {
		
		ClientAssetTokenResponse clientAssetTokenResponse =new ClientAssetTokenResponse();
		
		Client client =clientRepository.findByClientCorrelationId(clientId);
		Page<ClientAssetToken> pages = clientAssetTokenRepo.findByClient(client, pageable);
		if (client != null) {
			List<ClientAssetToken> clientAssetTokenList = pages.getContent();

		   
			List<ClientAssetTokenRequest> list  = new ArrayList<ClientAssetTokenRequest>();
			
			for(ClientAssetToken clientAssetToken:clientAssetTokenList) {
				ClientAssetTokenRequest clientAssetTokenRequest = new ClientAssetTokenRequest();
				clientAssetTokenRequest.setClientCorrelationId(clientAssetToken.getClient().getClientCorrelationId());
				clientAssetTokenRequest.setTokenCorrelationId(clientAssetToken.getTokenCorrelationId());
				clientAssetTokenRequest.setTokenName(clientAssetToken.getTokenName());
				clientAssetTokenRequest.setTokenCode(clientAssetToken.getTokenCode());
				clientAssetTokenRequest.setBatchQuantity(clientAssetToken.getBatchQuantity());
				clientAssetTokenRequest.setCreatedDate(CommonUtils.formatDate(clientAssetToken.getCreatedDate(), "yyyy-MM-dd"));
				clientAssetTokenRequest.setModifiedDate(clientAssetToken.getModifiedDate());
				list.add(clientAssetTokenRequest);
				
			}
			
			clientAssetTokenResponse.setAssetTokenRequestsList(list);
			clientAssetTokenResponse.setNumberOfElements(String.valueOf(pages.getNumberOfElements()));
			clientAssetTokenResponse.setTotalPages(String.valueOf(pages.getTotalPages()));
			clientAssetTokenResponse.setTotalElements(String.valueOf(pages.getTotalElements()));
			
		}
		
		
		return clientAssetTokenResponse;
	}

	

}
