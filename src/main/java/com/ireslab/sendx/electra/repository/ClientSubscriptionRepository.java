package com.ireslab.sendx.electra.repository;

import org.springframework.data.repository.CrudRepository;

import com.ireslab.sendx.electra.entity.ClientSubscription;

public interface ClientSubscriptionRepository extends CrudRepository<ClientSubscription, Integer> {
	
	public ClientSubscription findByClientId(Integer clientId);

}
