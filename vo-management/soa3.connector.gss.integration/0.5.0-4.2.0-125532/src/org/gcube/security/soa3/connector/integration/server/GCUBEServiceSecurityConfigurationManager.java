package org.gcube.security.soa3.connector.integration.server;

import org.gcube.common.core.contexts.GCUBEServiceContext;
import org.gcube.common.core.security.GCUBEAuthzPolicy;
import org.gcube.common.core.security.GCUBEServiceSecurityManager;
import org.gcube.common.core.security.SecurityCredentials;
import org.gcube.common.core.security.impl.GSSSecurityCredentials;
import org.gcube.common.core.utils.events.GCUBEProducer;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.ietf.jgss.GSSCredential;


/**
 * 
 * Abstract class that contains the base methods of a standard security manager. 
 * 
 * @author Ciro Formisano
 *
 */
public abstract class GCUBEServiceSecurityConfigurationManager implements GCUBEServiceSecurityManager 
{

	/** Object logger. */
	protected GCUBELog logger; //object logger
	/** Service context.*/
	protected GCUBEServiceContext context;
	/** Inner producer of lifetime events. */
	protected GCUBEProducer<LifetimeTopic, Object> producer = new GCUBEProducer<LifetimeTopic,Object>();
	/** Inner security credentials */
	protected SecurityCredentials serviceCredentials;
	/** Authentication method **/
	protected AuthMethod authMethod;
	/**Use Caller credentials by default */
	protected boolean propagateCallerCredentials;
	
	public GCUBEServiceSecurityConfigurationManager() 
	{
		this.logger  = new GCUBELog(this);
		this.propagateCallerCredentials = true;
		this.authMethod  = AuthMethod.NONE;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public void initialise(GCUBEServiceContext ctxt) throws Exception 
	{
		this.logger.debug("Initialising...");
		this.context = ctxt;
		// triggers the notifier for the policies
		producer.notify(LifetimeTopic.POLICYUPDATE, new LifetimeEvent());
		this.logger.debug("Intialisation completed");
	}
	

	/** {@inheritDoc} */
	public void subscribe(LifetimeConsumer c, LifetimeTopic ...topics) {
		this.producer.subscribe(c, (topics==null | topics.length==0)?LifetimeTopic.values():topics);
	}
	
	
	/** {@inheritDoc} */
	public void unsubscribe(LifetimeConsumer c, LifetimeTopic ...topics) {
		this.producer.unsubscribe(c, (topics==null | topics.length==0)?LifetimeTopic.values():topics);
	}
	
	
	/** {@inheritDoc} */
	public SecurityCredentials getServiceCredentials() throws Exception 
	{
		return this.serviceCredentials;
	}
	
	/** {@inheritDoc} */
	public GCUBEAuthzPolicy getPolicy() 
	{
		return null;
	}
	
	
	/** {@inheritDoc} */
	public void setAuthMethod(AuthMethod m) 
	{
		this.authMethod = m;
		
	}
	
	/** {@inheritDoc} */
	public void propagateCallerCredentials(boolean propagateCallerCredentials) 
	{
		this.propagateCallerCredentials = propagateCallerCredentials;
		
	}

	public void useCredentials(GSSCredential credentials) throws Exception 
	{
		this.useCredentials(new GSSSecurityCredentials(credentials));
		
	}
	
	
    

}
