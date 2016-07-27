package it.eng.rdlab.soa3.authn.rest.impl;

import it.eng.rdlab.soa3.authn.rest.AuthenticationContext;
import it.eng.rdlab.soa3.authn.rest.IAuthenticationService;
import it.eng.rdlab.soa3.config.ConfigurationManager;

import org.apache.log4j.Logger;
import org.springframework.ldap.core.simple.SimpleLdapTemplate;
import org.springframework.ldap.filter.AndFilter;
import org.springframework.ldap.filter.EqualsFilter;

/**
 * Authentication service that authenticates users against LDAP
 * 
 * @author Kanchanna Ramasamy Balraj
 * @author Ermanno Travaglino
 * 
 */

public class AuthenticationServiceImpl implements IAuthenticationService 
{
	static Logger logger = Logger.getLogger(AuthenticationServiceImpl.class.getName());

	private SimpleLdapTemplate ldapTemplate;
	private AuthenticationContext authenticationContext;

	public AuthenticationServiceImpl() 
	{
		configureLdap();
	}


	/**
	 * 
	 * @param userName
	 * @param organizationName
	 * @param password
	 * @return
	 */
	public boolean authenticate(String userName, String organizationName, String password) 
	{

		logger.debug("Ldap configuration completed successfully..");

		AndFilter filter = new AndFilter();
		filter.and((new EqualsFilter("uid", userName)));
		logger.debug("contacting ldap with filter " + filter);
		
		if (organizationName == null) organizationName = "";
		
		logger.debug("Organization name = "+organizationName);
		
		return ldapTemplate.authenticate("dc="+organizationName, filter.toString(),password);

	}

	private void configureLdap() 
	{
		this.authenticationContext = ConfigurationManager.getInstance().getAuthenticationContext();
		this.ldapTemplate = this.authenticationContext.getLdapTemplate();

	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isUserAuthenticated(String userName,String organizationName,String password) {
		return authenticate(userName,organizationName, password);

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AuthenticationContext getAuthenticationContext() 
	{

		return this.authenticationContext;
	}






}
