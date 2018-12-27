package com.ireslab.sendx.electra.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.ireslab.sendx.electra.entity.ClientAgentInvitation;

public interface ClientAgentInvitationRepository extends CrudRepository<ClientAgentInvitation, Integer> {

	@Query("select d from ClientAgentInvitation d where d.clientId=:clientId and (d.emailAddress IN :list OR d.mobileNumber IN :list)")
	public List<ClientAgentInvitation> findByClientIdCust(@Param("clientId") Integer clientId,@Param("list") List<String> list);
	
	public List<ClientAgentInvitation> findByClientId(Integer clientId);
	
	public ClientAgentInvitation findByEmailAddress(String email);
	public ClientAgentInvitation findByMobileNumber(String mobile);

	public ClientAgentInvitation findByMobileNumberAndClientId(String mobileNo, Integer clientId);

	public ClientAgentInvitation findByEmailAddressAndClientId(String email, Integer clientId);
}
