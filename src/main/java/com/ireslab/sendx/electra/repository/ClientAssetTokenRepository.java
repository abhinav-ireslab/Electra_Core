package com.ireslab.sendx.electra.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.ireslab.sendx.electra.entity.Client;
import com.ireslab.sendx.electra.entity.ClientAssetToken;

/**
 * @author Nitin
 *
 */
/*public interface ClientAssetTokenRepository extends CrudRepository<ClientAssetToken, Integer> {*/
public interface ClientAssetTokenRepository extends PagingAndSortingRepository<ClientAssetToken,Integer> {

	/**
	 * Retrieve token details based on token correlation id
	 * 
	 * @param tokenCorrelationId
	 * @return+
	 */
	public ClientAssetToken findByTokenCorrelationId(String tokenCorrelationId);
	public ClientAssetToken findByTokenId(Integer tokenId);
	public List<ClientAssetToken> findByClient(Client client);
	public ClientAssetToken findByTokenName(String tokenName);
	public ClientAssetToken findByTokenCode(String tokenName);
	public ClientAssetToken findByClientAndTokenCode(Client client,String tokenCode);
	//Page<ClientAssetToken> findByClientId(Integer client,Pageable pageable);
	
	Page<ClientAssetToken> findByClient(Client client,Pageable pageable);
	
	
	
	
	
	

}
