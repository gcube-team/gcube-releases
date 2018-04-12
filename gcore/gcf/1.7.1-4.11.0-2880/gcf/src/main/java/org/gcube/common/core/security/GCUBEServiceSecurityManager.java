package org.gcube.common.core.security;

import org.gcube.common.core.contexts.GCUBEServiceContext;
import org.gcube.common.core.utils.events.GCUBEConsumer;
import org.gcube.common.core.utils.events.GCUBEEvent;
import org.gcube.common.core.utils.events.GCUBETopic;

/**
 * An extension of {@link GCUBESecurityManager} which operates in a service context, where client and service
 * credentials must be distinguished and where authorisation becomes relevant.
 * 
 * <p> A service security manager authorises incoming calls, extracts caller credentials from them, and retrieves 
 * service credentials to use for outgoing calls.
 * 
 * 
 * @author Fabio Simeoni (University of Strathclyde), Ciro Formisano (ENG)
 *
 */
public interface GCUBEServiceSecurityManager extends GCUBESecurityManager {
	
	/**Lifetime topic enumeration.*/
	public static enum LifetimeTopic implements GCUBETopic {POLICYUPDATE,CREDENTIALUPDATE};
	/**
	 * Initialise the manager with the context of the associated service.
	 * @param ctxt the context.
	 */
	public void initialise(GCUBEServiceContext ctxt) throws Exception;

	/** Lifetime event.*/
	public static class LifetimeEvent extends GCUBEEvent<LifetimeTopic, Object> {}
	/** Lifetime event consumer */
	public abstract static class LifetimeConsumer implements GCUBEConsumer<LifetimeTopic, Object> {
		public <T1 extends LifetimeTopic, P1 extends Object> void onEvent(GCUBEEvent<T1,P1> ... events) {
			if (events==null || events.length==0) return;
			for (GCUBEEvent<T1,P1> e : events) {
				LifetimeEvent event = (LifetimeEvent) e;
				switch (event.getTopic()) {
					case CREDENTIALUPDATE : this.onCredentialUpdate();break;
					case POLICYUPDATE : this.onPolicyUpdate();break;
				}
			}
		}
		protected void onPolicyUpdate() {}
		protected void onCredentialUpdate() {}
	}
	

	
	/**Subscribes a {@link LifetimeConsumer}.
	 * @param c the consumer. */
	public void subscribe(LifetimeConsumer c,LifetimeTopic ... topics);
	
	/**Unsubscribes a {@link LifetimeConsumer}.
	 * @param c the consumer. */
	public void unsubscribe(LifetimeConsumer c,LifetimeTopic ... topics);
	
	/**
	 * If security is enabled, it returns the service credentials.
	 * @return the credentials, or <code>null</code> if security is not enabled.
	 * @throws Exception if security is enabled, but service credentials could not be produced.
	 */
	public SecurityCredentials getServiceCredentials() throws Exception;
	
	/**
	 * If security is enabled, it extracts credentials from the current request.
	 * @return the credentials, of <code>null</code> if security is not enabled.
	 * @throws Exception if security is enabled, but credentials could not be extracted from incoming call.
	 */
	public SecurityCredentials getCallerCredentials() throws Exception;
	
	/**
	 * Returns the current authorisation policy.
	 * @return the policy
	 */
	public GCUBEAuthzPolicy getPolicy();
	
	
	/** Indicates whether the service makes use of service credentials. 
	 * @return <code>true</code> if it does, <code>false</code> if it does not.*/
	public boolean needServiceCredentials();
	
	/** Indicates whether the service, without any further configurationm, makes 
	 * use of caller credentials: by default is true and, if false, the service will
	 * use its own credentials (without further configuration) to perform calls 
	 */ 
	public void propagateCallerCredentials (boolean propagateCallerCredentials);
	
	
}