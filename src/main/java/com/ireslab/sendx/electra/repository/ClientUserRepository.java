package com.ireslab.sendx.electra.repository;

import com.ireslab.sendx.electra.entity.Client;
import com.ireslab.sendx.electra.entity.ClientUser;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @author Nitin
 *
 */
public interface ClientUserRepository extends CrudRepository<ClientUser, Integer> {
	/**
	 * Retrieve user details based on user correlation id
	 *
	 * @param userCorrelationId
	 * @return
	 */
	//@Query(value = "SELECT cs FROM ClientUser cs WHERE cs.userCorrelationId=:userCorrelationId AND cs.status NOT IN ('TERMINATED', 'terminated')")
	//public ClientUser findByUserCorrelationId(@Param("userCorrelationId")String userCorrelationId);
	
	public ClientUser findByUserCorrelationId(String userCorrelationId);
	
	public ClientUser findByUniqueCode(String uniqueCode);

	@Query(value = "SELECT cu from ClientUser cu WHERE cu.client = ?1 AND cu.status NOT IN ('TERMINATED', 'terminated')")
	public List<ClientUser> findByClient(Client client);

	public ClientUser findByUserCorrelationIdAndClient_ClientCorrelationId(String userCorrelationId,
			String clientCorrelationId);

	public ClientUser findByAccountPublicKey(String accountPublicKey);
	
	//@Query(value = "SELECT c FROM ClientUser c WHERE c.isEkycEkybApproved =:isEkycEkybApproved AND c.client.clientId=1")
	@Query(value = "SELECT c FROM ClientUser c WHERE c.isEkycEkybApproved =:isEkycEkybApproved")
	public List<ClientUser> findAllClientUserApprovelPending(@Param("isEkycEkybApproved") boolean isEkycEkybApproved);
	
	@Query(value = "SELECT cu from ClientUser cu WHERE cu.mobileNumber =:mobileNumber  AND cu.status NOT IN ('TERMINATED', 'terminated')")
	public ClientUser findByMobileNumber(@Param("mobileNumber")String mobileNumber);
	
	
}
