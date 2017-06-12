package org.gcube.common.core.security;

import static org.globus.axis.gsi.GSIConstants.GSI_MODE;
import static org.globus.axis.gsi.GSIConstants.GSI_MODE_FULL_DELEG;
import static org.globus.axis.gsi.GSIConstants.GSI_MODE_LIMITED_DELEG;
import static org.globus.gsi.GSIConstants.ENCRYPTION;
import static org.globus.gsi.GSIConstants.SIGNATURE;
import static org.globus.wsrf.security.Constants.AUTHORIZATION;
import static org.globus.wsrf.security.Constants.GSI_SEC_CONV;

import java.rmi.Remote;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

import javax.xml.rpc.Stub;

import org.gcube.common.core.security.impl.GSSSecurityCredentials;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.globus.wsrf.impl.security.authentication.Constants;
import org.globus.wsrf.impl.security.authorization.HostAuthorization;
import org.ietf.jgss.GSSCredential;

/**
 * Partial implementation of <code>GCUBESecurityManager</code>. Subclasses must implement {@link #isSecurityEnabled()}
 * to discriminate between secure and unsecure contexts.
 *
 * @author Fabio Simeoni (University of Strathclyde), Manuele Simi (CNR)
 *
 */
public abstract class GCUBESecurityManagerImpl implements GCUBESecurityManager   {

	/** Object logger. */
	protected GCUBELog logger = new GCUBELog(this); //object logger
	
	/** Cache of credentials indexed by thread id. */
	protected Map<Thread,GSSCredential>  callCredentials = Collections.synchronizedMap(new WeakHashMap<Thread,GSSCredential>());
	
	/** The name of the manager. **/
	protected String name = this.getClass().getSimpleName();
	
	protected AuthMethod authMethod = AuthMethod.GSI_CONV;

	/**Sets the manager's logger.
	 * @param logger the logger.*/
	public void setLogger(GCUBELog logger) {this.logger=logger;}
	
	/**
	 * Returns the name with which the manager should log events.
	 * If it is not overridden, the manager logs for itself.
	 * @return the name.
	 */
	protected String getName() {
		return this.name;
	}
	
	/**
	 * Sets the name with which the manager should log events
	 * @param name the name
	 */
	protected void setName(String name) {
	    this.name = name;
	    logger.setPrefix(name);
	}
	/**
	 * {@inheritDoc}
	 */
	public abstract boolean isSecurityEnabled();
	
	
	/**
	 * 
	 * Deprecated method used for backward compatibility: use useCredentials(SecurityCredentials credentials)
	 * 
	 * @param credentials
	 * @throws Exception
	 */
	@Deprecated
	public void useCredentials(GSSCredential credentials)  throws Exception {
		this.useCredentials(Thread.currentThread(),credentials);
	}
	
	/**
	 * 
	 * Deprecated method used for backward compatibility: use useCredentials(Thread thread, SecurityCredentials ... credentials)
	 * 
	 * @param credentials
	 * @throws Exception
	 */
	@Deprecated
	public void useCredentials(Thread thread, GSSCredential ... credentials) throws Exception {

		GSSSecurityCredentials [] gssSecurityCredentials = new GSSSecurityCredentials [credentials.length];
		
		for (int y=0; y< credentials.length; y++)
		{
			gssSecurityCredentials [y] = new GSSSecurityCredentials(credentials [y]);
		}
		
		
		this.useCredentials(thread, gssSecurityCredentials);

	}
	
	/**
	 * {@inheritDoc}
	 */
	public void useCredentials(SecurityCredentials credentials)  throws Exception {
		this.useCredentials(Thread.currentThread(),credentials);
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void useCredentials(Thread thread, SecurityCredentials ... credentials) throws Exception {
		
		if (!isSecurityEnabled()) return;	
		
		
		if (credentials.length==0) {// if no credentials are provided, use the current ones.
			credentials=new SecurityCredentials[]{this.getCredentials()};
		}
		//debug some information
		GSSCredential gssCredentials = (GSSCredential) credentials[0].getCredentialsAsObject();
		logger.debug("Using credentials of ("+gssCredentials.getName()+") in thread "+thread.getName()+"("+thread.getId()+")");
				
		callCredentials.put(thread,gssCredentials);
	
	}
	
	/**
	 * {@inheritDoc}
	 */
	public SecurityCredentials getCredentials() 
	{
		GSSCredential internalCred = this.callCredentials.get(Thread.currentThread());
		if (internalCred == null) return null;
		else{
			return new GSSSecurityCredentials(internalCred);
		}
		
		
	}
	
	
	/**
	 * {@inheritDoc}
	 */
	public synchronized void setSecurity(Remote s, AuthMode e, DelegationMode d) throws Exception {
		
		if (!isSecurityEnabled()) return; //nothing to do
		
		Stub stub = (Stub) s;
		
		//sets credentials (caller or service)
		stub._setProperty(org.globus.axis.gsi.GSIConstants.GSI_CREDENTIALS, getCredentials());
		//limits lifetime of security context resource
		stub._setProperty(org.globus.wsrf.impl.security.authentication.Constants.CONTEXT_LIFETIME, 300);
			
		String authMethodString = null;
		
		switch (this.authMethod)
		{
		case GSI_CONV: authMethodString = GSI_SEC_CONV;break;
		case GSI_TRANS: authMethodString = Constants.GSI_TRANSPORT;break;
		
		}
		
		//sets authentication
		switch(e) {
			case INTEGRITY:stub._setProperty(authMethodString, SIGNATURE);break;
			case PRIVACY:stub._setProperty(authMethodString, ENCRYPTION);break;
			case BOTH:stub._setProperty(authMethodString, SIGNATURE);stub._setProperty(authMethodString, ENCRYPTION);
		}

		//sets delegation
		switch(d) {
		case FULL:stub._setProperty(AUTHORIZATION, HostAuthorization.getInstance());
				  stub._setProperty(GSI_MODE, GSI_MODE_FULL_DELEG) ;break;
		case LIMITED:stub._setProperty(AUTHORIZATION, HostAuthorization.getInstance());
					stub._setProperty(GSI_MODE, GSI_MODE_LIMITED_DELEG) ;break;
		}
		
		logger.debug("Setting authentication="+e.name()+" and delegation="+d.name()+" on "+stub.getClass().getSimpleName());	
		
	}
	
	@Override
	/**
	 * {@inheritDoc}
	 */
	public void setAuthMethod(AuthMethod m) 
	{
		this.authMethod = m;
		
	}
	
}
