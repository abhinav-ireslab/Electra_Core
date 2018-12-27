/**
 * 
 */
package com.ireslab.sendx.electra.repository;

import org.springframework.data.repository.CrudRepository;

import com.ireslab.sendx.electra.entity.Country;

/**
 * @author ireslab
 *
 */
public interface CountryRepository extends CrudRepository<Country, Integer> {
	
	public Country findCountryByCountryDialCode(String countryDialCode);

}
