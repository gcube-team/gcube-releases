package org.gcube.common.core.security.context;

public class SecurityContextFactory 
{
	private static SecurityContextFactory instance;
	private SecurityContext internalSecurityContext;
	
	private SecurityContextFactory ()
	{
		
	}
	
	public static final SecurityContextFactory getInstance ()
	{
		if (instance == null) instance = new SecurityContextFactory();
		
		return instance;
	}
	
	public void setSecurityContext (SecurityContext context)
	{
		this.internalSecurityContext = context;
	}
	
	public SecurityContext getSecurityContext ()
	{
		return internalSecurityContext;
	}

}
