package com.ireslab.sendx.electra.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.ireslab.sendx.electra.entity.SubscriptionPlans;

public interface SubscriptionPlanRepository extends CrudRepository<SubscriptionPlans, Integer> {
	
	public SubscriptionPlans findSubscriptionPlansBySubscriptionId(Integer subscriptionId);
	
	@Query(value = "SELECT subs FROM SubscriptionPlans subs where subs.isDeleted=false ORDER BY subs.token ASC")
	public List<SubscriptionPlans> findAllCust(Pageable pageable);
	
	//@Query(value = "SELECT subs FROM SubscriptionPlans subs where subs.countryId.countryId =:countryId and subs.isDeleted=false ORDER BY subs.token ASC")
	//public List<SubscriptionPlans> findSubscriptionPlanByCountryIdCust(@Param("countryId")Integer countryId );
	@Query(value = "SELECT subs FROM SubscriptionPlans subs where subs.countryId.countryId IS NULL and subs.isDeleted=false ORDER BY subs.token ASC")
	public List<SubscriptionPlans> findSubscriptionPlanByCountryIdCust();

}
