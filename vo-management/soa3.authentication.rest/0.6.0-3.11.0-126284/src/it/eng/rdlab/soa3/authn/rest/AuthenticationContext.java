package it.eng.rdlab.soa3.authn.rest;

import org.springframework.ldap.core.ContextSource;
import org.springframework.ldap.core.simple.SimpleLdapTemplate;


public interface AuthenticationContext
{
	public ContextSource getContextSource ();
	public SimpleLdapTemplate getLdapTemplate ();


}
