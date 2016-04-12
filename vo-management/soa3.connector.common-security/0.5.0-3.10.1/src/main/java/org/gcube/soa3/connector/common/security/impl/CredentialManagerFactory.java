package org.gcube.soa3.connector.common.security.impl;

import org.gcube.soa3.connector.common.security.CredentialManager;

public class CredentialManagerFactory 
{
	
	public static CredentialManager getCredentialManager ()
	{
		return new CredentialManagerImpl();
	}

}
