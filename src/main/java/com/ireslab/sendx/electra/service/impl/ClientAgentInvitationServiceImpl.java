package com.ireslab.sendx.electra.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ireslab.sendx.electra.entity.ClientAgentInvitation;
import com.ireslab.sendx.electra.repository.ClientAgentInvitationRepository;
import com.ireslab.sendx.electra.service.ClientAgentInvitationService;


/**
 * @author iRESlab
 *
 */
@Service
public class ClientAgentInvitationServiceImpl implements ClientAgentInvitationService {

	
	@Autowired
	private ClientAgentInvitationRepository clientAgentRepo;
	@Override
	public void saveInvitationDetail(ClientAgentInvitation agentInvitation) {
		clientAgentRepo.save(agentInvitation);
     }
	@Override
	public List<ClientAgentInvitation> findClientAgentInvitationList(Integer clientId, List<String> list) {
		
		return clientAgentRepo.findByClientIdCust(clientId, list);
	}
	@Override
	public List<ClientAgentInvitation> findByClientId(Integer clientId) {
		return clientAgentRepo.findByClientId(clientId);
	}

}
