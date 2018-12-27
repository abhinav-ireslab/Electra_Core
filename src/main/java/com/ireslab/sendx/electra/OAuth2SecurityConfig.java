package com.ireslab.sendx.electra;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;

/**
 * @author Nitin
 *
 */
public class OAuth2SecurityConfig {

	@Configuration
	@EnableResourceServer
	protected static class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.springframework.security.oauth2.config.annotation.web.configuration.
		 * ResourceServerConfigurerAdapter#configure(org.springframework.security.config
		 * .annotation.web.builders.HttpSecurity)
		 */
		@Override
		public void configure(HttpSecurity http) throws Exception {
			http.authorizeRequests().antMatchers("/v2/api-docs", "/configuration/ui", "/swagger-resources/**",
					"/configuration/**", "/swagger-ui.html", "/webjars/**", "/test/**").permitAll();
			http.authorizeRequests().anyRequest().authenticated();

		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.springframework.security.oauth2.config.annotation.web.configuration.
		 * ResourceServerConfigurerAdapter#configure(org.springframework.security.oauth2
		 * .config.annotation.web.configurers.ResourceServerSecurityConfigurer)
		 */
		@Override
		public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
			resources.resourceId("sendxRestAPI").stateless(false);
		}
	}

	@Configuration
	@EnableAuthorizationServer
	protected static class AuthorizationServerConfiguration extends AuthorizationServerConfigurerAdapter {

		@Resource(name = "OAuth")
		@Autowired
		private DataSource dataSource;

		@Autowired
		@Qualifier("clientDetailsService")
		private ClientDetailsService clientDetailsService;

		@Autowired
		private AuthenticationManager authenticationManager;

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.springframework.security.oauth2.config.annotation.web.configuration.
		 * AuthorizationServerConfigurerAdapter#configure(org.springframework.security.
		 * oauth2.config.annotation.web.configurers.
		 * AuthorizationServerEndpointsConfigurer)
		 */
		@Override
		public void configure(AuthorizationServerEndpointsConfigurer configurer) throws Exception {
			configurer.authenticationManager(authenticationManager);
			configurer.tokenStore(tokenStore());
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.springframework.security.oauth2.config.annotation.web.configuration.
		 * AuthorizationServerConfigurerAdapter#configure(org.springframework.security.
		 * oauth2.config.annotation.web.configurers.
		 * AuthorizationServerSecurityConfigurer)
		 */
		@Override
		public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
			security.passwordEncoder(passwordEncoder());
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.springframework.security.oauth2.config.annotation.web.configuration.
		 * AuthorizationServerConfigurerAdapter#configure(org.springframework.security.
		 * oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer)
		 */
		@Override
		public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
			clients.withClientDetails(clientDetailsService);
		}

		/**
		 * @return
		 */
		@Bean
		public PasswordEncoder passwordEncoder() {
			return new BCryptPasswordEncoder();
		}

		/**
		 * @return
		 */
		@Bean
		public TokenStore tokenStore() {
			return new JdbcTokenStore(dataSource);
		}
	}
}
