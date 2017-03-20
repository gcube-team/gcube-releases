package org.gcube.common.core.security.impl;

import java.rmi.Remote;

import org.gcube.common.core.contexts.GCUBEServiceContext;
import org.gcube.common.core.faults.GCUBEException;
import org.gcube.common.core.security.GCUBEAuthzPolicy;
import org.gcube.common.core.security.GCUBEServiceSecurityManager;
import org.gcube.common.core.security.SecurityCredentials;
import org.gcube.common.core.utils.calls.GCUBECall;
import org.gcube.common.core.utils.events.GCUBEProducer;
import org.ietf.jgss.GSSCredential;

/**
 * 
 * Simple security manager: it does nothing. To be used in the non-sec infrastructure or for testing purposes 
 * 
 * @author Ciro Formisano
 *
 */
@SuppressWarnings("all")
public class GCUBESimpleServiceSecurityManager implements GCUBEServiceSecurityManager {

	/** Inner producer of lifetime events. */
	private GCUBEProducer<LifetimeTopic, Object> producer = new GCUBEProducer<LifetimeTopic,Object>();
	
	public void authoriseCall(GCUBECall authzRequest) throws GCUBEException 
	{
		//DOES NOTHING
	}

	public GCUBEAuthzPolicy getPolicy() 
	{
		//RETURNS NOTHING
		return null;
	}


	public void initialise(GCUBEServiceContext ctxt) throws Exception 
	{
		//DOES NOTHING
	}

	public boolean needServiceCredentials() 
	{
		//RETURNS FALSE
		return false;
	}

	public void subscribe(LifetimeConsumer c, LifetimeTopic... topics) 
	{

		this.producer.subscribe(c, LifetimeTopic.values());
		this.producer.notify(LifetimeTopic.CREDENTIALUPDATE, new LifetimeEvent());
		this.producer.notify(LifetimeTopic.POLICYUPDATE, new LifetimeEvent());	
	}

	public void unsubscribe(LifetimeConsumer c, LifetimeTopic... topics) 
	{
		//DOES NOTHING
		
	}



	public boolean isSecurityEnabled() 
	{
		//RETURNS FALSE
		return false;
	}

	public void setSecurity(Remote s, AuthMode e, DelegationMode d) throws Exception 
	{
		//DOES NOTHING
		
	}

	@Deprecated
	public void useCredentials(GSSCredential credentials) throws Exception 
	{
		//DOES NOTHING
		
	}

	@Deprecated
	public void useCredentials(Thread thread, GSSCredential... credentials) throws Exception 
	{
		//DOES NOTHING
		
	}

	@Override
	public void useCredentials(SecurityCredentials credentials) throws Exception
	{
		//DOES NOTHING
		
	}

	@Override
	public void useCredentials(Thread thread, SecurityCredentials... credentials) throws Exception 
	{
		//DOES NOTHING
		
	}

	@Override
	public void setAuthMethod(AuthMethod m) 
	{
		//DOES NOTHING
		
	}

	@Override
	public void propagateCallerCredentials(boolean propagateCallerCredentials)
	{
		//DOES NOTHING
		
	}

	@Override
	public SecurityCredentials getCredentials() 
	{
		//RETURNS NOTHING
		return null;
	}

	@Override
	public SecurityCredentials getServiceCredentials() throws Exception 
	{
		//RETURNS NOTHING
		return null;
	}

	@Override
	public SecurityCredentials getCallerCredentials() throws Exception 
	{
		//RETURNS NOTHING
		return null;
	}



}
