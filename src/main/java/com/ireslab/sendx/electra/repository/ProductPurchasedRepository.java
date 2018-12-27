/**
 * 
 */
package com.ireslab.sendx.electra.repository;

import org.springframework.data.repository.CrudRepository;

import com.ireslab.sendx.electra.entity.ProductPurchaseHistory;

/**
 * @author ireslab
 *
 */
public interface ProductPurchasedRepository extends CrudRepository<ProductPurchaseHistory, Integer>{

}
