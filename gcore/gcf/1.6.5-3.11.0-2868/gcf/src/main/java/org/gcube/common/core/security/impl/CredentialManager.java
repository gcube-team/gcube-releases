package org.gcube.common.core.security.impl;

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

import javax.xml.rpc.Stub;

import org.gcube.common.core.security.SecurityCredentials;
import org.gcube.common.core.security.context.SecurityContextFactory;
import org.gcube.common.core.utils.logging.GCUBELog;

public class CredentialManager 
{
	/** Cache of credentials indexed by thread id. */
	protected Map<Thread,SecurityCredentials>  callCredentials = Collections.synchronizedMap(new WeakHashMap<Thread,SecurityCredentials>());
	
	private GCUBELog logger;
	
	public CredentialManager() 
	{
		this.logger = new GCUBELog(this);
	}
	
	public SecurityCredentials getContainerCredentials ()
	{
		return new GSSSecurityCredentials(SecurityContextFactory.getInstance().getSecurityContext().getDefaultCredentials());
	}

	public synchronized void setCredentials(Thread thread, SecurityCredentials... credentials) throws Exception {

		if (credentials.length==0) {// if no credentials are provided, use the current ones.
			credentials=new SecurityCredentials[]{this.getCredentials()};
		}
		//debug some information
		SecurityCredentials gssCredentials = credentials[0];
		logger.debug("Using credentials of ("+gssCredentials.getCredentialsAsString()+") in thread "+thread.getName()+"("+thread.getId()+")");
		callCredentials.put(thread,gssCredentials);

	}
	
	public SecurityCredentials getCredentials() 
	{
		
		SecurityCredentials internalCred = this.callCredentials.get(Thread.currentThread());
		if (internalCred == null) return getContainerCredentials();
		else{
			return internalCred;
		}
	}
	
	public void associateCurrentCredentials (Stub stub)
	{
		logger.debug("Adding the current security credentials");
		stub._setProperty(org.globus.axis.gsi.GSIConstants.GSI_CREDENTIALS, getCredentials().getCredentialsAsObject());
		logger.debug("Security credentials added");
	}

}
