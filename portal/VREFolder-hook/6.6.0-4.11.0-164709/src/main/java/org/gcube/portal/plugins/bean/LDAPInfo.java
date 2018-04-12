package org.gcube.portal.plugins.bean;

public class LDAPInfo {
	private String ldapUrl,ldapPassword,filter,principal;

	public LDAPInfo() {
		super();
	}
	public LDAPInfo(String ldapUrl, String ldapPassword, String filter, String principal) {
		super();
		this.ldapUrl = ldapUrl;
		this.ldapPassword = ldapPassword;
		this.filter = filter;
		this.principal = principal;
	}

	public String getLdapUrl() {
		return ldapUrl;
	}

	public void setLdapUrl(String ldapUrl) {
		this.ldapUrl = ldapUrl;
	}

	public String getLdapPassword() {
		return ldapPassword;
	}

	public void setLdapPassword(String ldapPassword) {
		this.ldapPassword = ldapPassword;
	}

	public String getFilter() {
		return filter;
	}

	public void setFilter(String filter) {
		this.filter = filter;
	}

	public String getPrincipal() {
		return principal;
	}

	public void setPrincipal(String principal) {
		this.principal = principal;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("LDAPInfo [ldapUrl=");
		builder.append(ldapUrl);
		builder.append(", ldapPassword=");
		builder.append(ldapPassword);
		builder.append(", filter=");
		builder.append(filter);
		builder.append(", principal=");
		builder.append(principal);
		builder.append("]");
		return builder.toString();
	}
	
}
