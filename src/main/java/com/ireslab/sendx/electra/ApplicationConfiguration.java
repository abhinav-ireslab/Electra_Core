package com.ireslab.sendx.electra;

import javax.sql.DataSource;

import org.modelmapper.ModelMapper;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.orm.jpa.support.OpenEntityManagerInViewFilter;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.oauth2.provider.ClientDetailsService;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.gson.Gson;
import com.ireslab.sendx.electra.service.ExchangeService;
import com.ireslab.sendx.electra.service.ProfileImageService;
import com.ireslab.sendx.electra.service.impl.ClientDetailsServiceImpl;
import com.ireslab.sendx.electra.service.impl.ExchangeServiceImpl;
import com.ireslab.sendx.electra.service.impl.ProfileImageServiceImpl;

/**
 * @author Nitin
 *
 */
@Configuration
@EnableAsync
@EnableConfigurationProperties
public class ApplicationConfiguration {

	 @Bean
	  public FilterRegistrationBean openEntityManagerInViewFilter() {
	      FilterRegistrationBean reg = new FilterRegistrationBean();
	      reg.setName("OpenEntityManagerInViewFilter");
	      reg.setFilter(new OpenEntityManagerInViewFilter());
	      return reg;
	  }
	
	/**
	 * @return
	 */
	@Primary
	@Bean(name = "clientDetailsServiceImpl")
	public ClientDetailsService getClientDetailsService() {
		return new ClientDetailsServiceImpl();
	}

	/**
	 * @return
	 */
	@Bean(name = "modelMapper")
	public ModelMapper getModelMapper() {
		return new ModelMapper();
	}

	/**
	 * @return
	 */
	@Bean(name = "objectWriter")
	public ObjectWriter getObjectWriter() {
		return new ObjectMapper().writerWithDefaultPrettyPrinter();
	}

	/**
	 * @return
	 */
	@Bean(name = "OAuth")
	@ConfigurationProperties(prefix = "spring.datasource")
	public DataSource secondaryDataSource() {
		return DataSourceBuilder.create().build();
	}

	/**
	 * @return
	 */
	@Bean(name = "gson")
	public Gson getGson() {
		return new Gson();
	}
	
	@Bean
	public ExchangeService getExchangeService() {
		return new ExchangeServiceImpl();
	}
	
	
	
	@Bean
	public ProfileImageService getProfileImageService() {
		return new ProfileImageServiceImpl();
	}
}
