package com.ireslab.sendx.electra.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.ireslab.sendx.electra.entity.Product;

public interface ProductRepository extends CrudRepository<Product, Integer>{

	@Query(value = "SELECT p FROM Product p WHERE p.clientId=:clientId AND  p.availableQuantity >0")
	public List<Product> findByClientIdCust(@Param("clientId")Integer clientId);
	
	public List<Product> findByClientId(Integer clientId);
	
	public Product findByProductCode(String productCode);
	public Product findByProductId(Integer productId);

	

}
