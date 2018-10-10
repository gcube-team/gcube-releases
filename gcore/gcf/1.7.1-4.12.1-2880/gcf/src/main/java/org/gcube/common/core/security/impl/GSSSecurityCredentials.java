package org.gcube.common.core.security.impl;

import org.gcube.common.core.security.SecurityCredentials;
import org.ietf.jgss.GSSCredential;


public class GSSSecurityCredentials implements SecurityCredentials {

	private GSSCredential gssCredential;
	
	public GSSSecurityCredentials (GSSCredential gssCredential)
	{
		this.gssCredential = gssCredential;
	}
	
	@Override
	public String getCredentialsAsString() throws Exception 
	{
		return this.gssCredential.getName().toString();
	
	}

	@Override
	public Object getCredentialsAsObject() 
	{
		return this.gssCredential;
	}

}
