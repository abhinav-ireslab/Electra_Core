package com.ireslab.sendx.electra.repository;

import org.springframework.data.repository.CrudRepository;

import com.ireslab.sendx.electra.entity.ProductGroup;

public interface ProductGroupRepository extends CrudRepository<ProductGroup, Integer> {

	// @Query(value = "SELECT abc FROM ProductGroup")
	// public List<ProductGroup> findAll();

}
