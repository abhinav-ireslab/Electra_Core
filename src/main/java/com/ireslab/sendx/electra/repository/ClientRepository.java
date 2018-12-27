package com.ireslab.sendx.electra.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.ireslab.sendx.electra.entity.Client;

/**
 * @author Nitin
 *
 */
public interface ClientRepository extends CrudRepository<Client, Integer> {

	/**
	 * @param clientId
	 * @return
	 */
	
	@Query(value = "SELECT c FROM Client c WHERE c.status NOT IN ('TERMINATED', 'terminated') ORDER BY c.createdDate DESC")
	public Page<Client> findAllCustom(Pageable pageable);
	
	@Query(value = "SELECT c FROM Client c WHERE c.status NOT IN ('TERMINATED', 'terminated')")
	public List<Client> findAllClientCustom();
	
	@Query(value = "SELECT c FROM Client c WHERE c.clientCorrelationId=:clientId AND c.status NOT IN ('TERMINATED', 'terminated')")
	public Client findByClientCorrelationId(@Param("clientId") String clientId);
	
	@Query(value = "SELECT c FROM Client c WHERE c.username=:userName AND c.status NOT IN ('TERMINATED', 'terminated')")
	public Client findByUsername(@Param("userName")String userName);
	
	@Query(value = "SELECT c FROM Client c WHERE c.emailAddress=:email AND c.status NOT IN ('TERMINATED', 'terminated')")
	public Client findByEmail(@Param("email")String email);
	
	public Client findUserByResetToken(String token);
	
	public Client findByClientId(Integer clientId);
	
	public Client findByCompanyCode(String companyCode);
	
	@Query(value = "SELECT c FROM Client c WHERE c.companyCode=:companyCode and c.status NOT IN ('TERMINATED', 'terminated')")
	public Client findBycompanyCode(@Param("companyCode")String companyCode);
	
	@Query(value = "SELECT c FROM Client c WHERE c.contactNumber1=:contactNumber1 AND c.status NOT IN ('TERMINATED', 'terminated')")
	public Client findByContactNumber1(@Param("contactNumber1") String contactNumber1);
	
	Client findByIssuingAccountPublicKey(String account);
	Client findByBaseTxnAccountPublicKey(String account);
	Client findBycontactNumber1(String contactNumber);

	
	@Query(value = "SELECT c FROM Client c WHERE c.isEkycEkybApproved =:isEkycEkybApproved")
	public List<Client> findAllClientApprovelPending(@Param("isEkycEkybApproved") boolean isEkycEkybApproved);
	
	@Query(value = "SELECT c FROM Client c WHERE c.clientCorrelationId=:clientId")
	public Client findByClientCorrelationIdCust(@Param("clientId") String clientId);
	
	public Client findByuniqueCode(String uniqueCode);
}
