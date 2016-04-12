package it.eng.rdlab.soa3.authn.rest.impl;

import it.eng.rdlab.soa3.authn.rest.AuthenticationContext;

import org.springframework.ldap.core.ContextSource;
import org.springframework.ldap.core.simple.SimpleLdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;

public class LDAPAuthenticationContext implements AuthenticationContext 
{
	private String 	url,
	base,
	userDn,
	password;

	private LdapContextSource contextSource;
	private SimpleLdapTemplate ldapTemplate;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getBase() {
		return base;
	}

	public void setBase(String base) {
		this.base = base;
	}

	public String getUserDn() {
		return userDn;
	}

	public void setUserDn(String userDn) {
		this.userDn = userDn;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}


	private void generateContextSource ()
	{
		this.contextSource = new LdapContextSource();
		this.contextSource.setUrl(this.url);
		this.contextSource.setBase(this.base);
		this.contextSource.setUserDn(this.userDn);
		this.contextSource.setPassword(this.password);
		try {
			this.contextSource.afterPropertiesSet();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void generateLdapTemplate ()
	{
		if (this.contextSource == null) generateContextSource();

		this.ldapTemplate = new SimpleLdapTemplate(this.contextSource);

	}


	@Override
	public ContextSource getContextSource() 
	{
		if (this.contextSource == null) generateContextSource();

		return this.contextSource;
	}

	@Override
	public SimpleLdapTemplate getLdapTemplate() 
	{
		if (this.ldapTemplate == null) generateLdapTemplate();

		return this.ldapTemplate;
	}




}
