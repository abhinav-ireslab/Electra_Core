package com.ireslab.sendx.electra.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import com.ireslab.sendx.electra.entity.GstSacCode;
public interface GstSacCodeRepository extends PagingAndSortingRepository<GstSacCode,Integer> {

	
	//TODO write query to search data from database 
	@Query(value="select gsc from GstSacCode gsc where gsc.sacCode like %:searchData% OR gsc.description like %:searchData%  OR gsc.alsoCheck like %:searchData% ")
	Page<GstSacCode> findAllCustom(@Param("searchData")String searchData,Pageable pageable);

}
