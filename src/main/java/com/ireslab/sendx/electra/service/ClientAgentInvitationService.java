package com.ireslab.sendx.electra.service;

import java.util.List;

import com.ireslab.sendx.electra.entity.ClientAgentInvitation;

/**
 * @author iRESlab
 *
 */
public interface ClientAgentInvitationService {
	
	public void saveInvitationDetail(ClientAgentInvitation agentInvitation);
	
	public List<ClientAgentInvitation> findClientAgentInvitationList(Integer clientId,List<String> list);
	
	public List<ClientAgentInvitation> findByClientId(Integer clientId);

}
