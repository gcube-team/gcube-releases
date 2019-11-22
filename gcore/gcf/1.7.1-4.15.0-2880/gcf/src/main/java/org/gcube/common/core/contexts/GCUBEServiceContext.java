package org.gcube.common.core.contexts;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Constructor;
import java.rmi.Remote;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.management.ObjectName;

import org.gcube.common.core.contexts.GHNContext.Mode;
import org.gcube.common.core.contexts.ghn.Events.GHNLifeTimeEvent;
import org.gcube.common.core.contexts.ghn.Events.GHNTopic;
import org.gcube.common.core.contexts.ghn.GHNConsumer;
import org.gcube.common.core.contexts.service.Builder;
import org.gcube.common.core.contexts.service.Consumer;
import org.gcube.common.core.faults.GCUBEException;
import org.gcube.common.core.faults.GCUBERetryEquivalentException;
import org.gcube.common.core.informationsystem.publisher.ISPublisher;
import org.gcube.common.core.instrumentation.RI;
import org.gcube.common.core.persistence.GCUBERINoPersistenceManager;
import org.gcube.common.core.persistence.GCUBERIPersistenceManager;
import org.gcube.common.core.persistence.GCUBERIPersistenceManagerProfile;
import org.gcube.common.core.plugins.GCUBEPluginManager;
import org.gcube.common.core.plugins.GCUBEPluginManagerProfile;
import org.gcube.common.core.resources.GCUBEResource;
import org.gcube.common.core.resources.GCUBEResource.AddScopeEvent;
import org.gcube.common.core.resources.GCUBEResource.RemoveScopeEvent;
import org.gcube.common.core.resources.GCUBEResource.ResourceConsumer;
import org.gcube.common.core.resources.GCUBERunningInstance;
import org.gcube.common.core.resources.GCUBEService;
import org.gcube.common.core.resources.service.ServiceDependency;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.scope.GCUBEScopeManager;
import org.gcube.common.core.scope.GCUBEScopeManagerImpl;
import org.gcube.common.core.security.GCUBEAuthzPolicy;
import org.gcube.common.core.security.GCUBEDefaultSecurityConfiguration;
import org.gcube.common.core.security.GCUBEServiceAuthenticationController;
import org.gcube.common.core.security.GCUBEServiceAuthorizationController;
import org.gcube.common.core.security.GCUBEServiceSecurityController;
import org.gcube.common.core.security.GCUBEServiceSecurityManager;
import org.gcube.common.core.security.SecurityCredentials;
import org.gcube.common.core.security.context.SecurityContextFactory;
import org.gcube.common.core.security.impl.GCUBESimpleServiceSecurityManager;
import org.gcube.common.core.utils.events.GCUBEEvent;
import org.gcube.common.core.utils.events.GCUBEProducer;
import org.gcube.common.core.utils.events.GCUBETopic;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.common.core.utils.proxies.AccessControlProxyContext.Restricted;
import org.gcube.common.core.utils.proxies.ReadOnlyProxyContext.ReadOnly;
import org.gcube.common.scope.api.ScopeProvider;
import org.ietf.jgss.GSSCredential;

/**
 * Partial implementation of contexts for gCube services.
 * A {@link GCUBEServiceContext} exposes the configuration of the service and manages 
 * the lifetime of its Running Instance on the GHN, notifying interesting parties 
 * of key lifetime events. It also acts as a {@link GCUBEServiceSecurityManager} and a 
 * {@link GCUBEScopeManager} in the rest of the 
 * service implementation . 
 *  
 *  <p>Typically, the creation of the context is cached. 
 *  The following example illustrates a simple but thread-safe caching pattern
 *  for a hypothetical subclass <code>MyServiceContext</code>:
 *  <p> 
 * <code>
 *  class MyServiceContext {<br>
 *  &nbsp;...<br>
 * 	&nbsp;static private MyServiceContext cache = new MyServiceContext();//cache on load<br>
 *  &nbsp;...<br>
 * 	&nbsp;private MyServiceContext(){} //force caching<br>
 * 	&nbsp;...<br>
 *  &nbsp;public static MyServiceContext getContext() {<br>
 * 	 &nbsp;&nbsp;return cache;<br>
 * &nbsp;}<br>
 * </code>
 * 
 * @author Fabio Simeoni (University of Strathclyde), Manuele Simi (CNR-ISTI), Ciro Formisano (ENG)
 *
 */
public abstract class GCUBEServiceContext extends GCUBEContext implements GCUBEServiceSecurityManager, GCUBEScopeManager {
	
	/** enum of the security status available **/
	public enum ServiceSecurityStatus {ENABLED, DISABLED, NOT_SET};
	/** service security status	**/
	private ServiceSecurityStatus serviceSecurityStatus;
	/** Name of the configuration directory JNDI environment.*/
	public static final String CONFIG_DIR_JNDI_NAME="configDir";
	/** Name of Service Profile JNDI environment.*/
	public static final String PROFILE_FILE_NAME="profile.xml";
	/**Name of Persistence Manager JNDI environment.*/
	public static final String PERSISTENCE_MANAGER_JNDI_NAME = "persistenceManagerProfile";
	/**Name of Plugin Manager JNDI environment.*/
	public static final String PLUGIN_MANAGER_JNDI_NAME = "pluginManagerProfile";
	/**Name of Start Scopes JNDI environment.*/
	public static final String START_SCOPES_JNDI_NAME="startScopes";
	/** The name of the host to publish in the local profiles, if different from the container's one */
	public static final String PUBLISHED_HOST_JNDI_NAME = "publishedHost";
	/** The name of the port to publish in the local profiles, if different from the container's one */
	public static final String PUBLISHED_PORT_JNDI_NAME = "publishedPort";
	/** Name of the RI serialisation file.*/
	public static final String RIPROFILE_FILENAME="RIProfile.xml";
	/** Interval for the scheduling of the local credentials listener. */
	protected static final long LISTENER_UPDATE_INTERVAL=2;
	/**Name of Security Manager JNDI environment.*/
	public static final String SECURITY_MANAGER_JNDI_NAME="securityManagerClass";
	/**Name of Authentication Manager JNDI environment.*/
	public static final String AUTHENTICATION_MANAGER_JNDI_NAME="authenticationManagerClass";
	/**Name of Authorisation Manager JNDI environment.*/
	public static final String AUTHORISATION_MANAGER_JNDI_NAME="authorisationManagerClass";
	/**Use Caller Credentials property JNDI environment.*/
	public static final String PROPAGATE_CALLER_CREDENTIALS_JNDI_NAME="propagateCallerCredentials";
	/** The management bean of the Running Instance. */
	private RI mbean; 
	/** Internal RI event consumers.*/
	private Consumer[] internalRIListeners = new Consumer[]{new Initialiser(),new Stager(),new Subscriber(), new Updater()};

	/** Creates and preinitialises an instance. */
	protected GCUBEServiceContext() {//this will take place at gHN startup
		try {
			//subscribe internal event consumers to relevant RI lifetime topics
			for (Consumer consumer : internalRIListeners) {
				consumer.setLogger(this.logger);
				this.subscribeLifetTime(consumer, consumer.getTopics());
			}
			this.setStatus(Status.DEPLOYED);//triggers first status-change event
		}
		catch(Exception e) {
		  logger.fatal("could not initialise Running Instance",e);
		  this.setStatus(Status.FAILED);
		}	
	}
	
	/**Returns the context's logger.
	 * @return the logger.*/
	public GCUBELog getLogger() {return this.logger;}
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////////////CONFIGURATION MANAGEMENT
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	
	/** Embedded persistence manager. */
	private GCUBERIPersistenceManager persistenceManager;
	/** The {@link org.gcube.common.core.resources.GCUBERunningInstance GCUBERunningInstance}. */
	private GCUBERunningInstance instance;
	/** The {@link org.gcube.common.core.resources.GCUBEService GCUBEService}. */
	private GCUBEService service;
	
	/**
	 * Return the name of the port-type in the JNDI configuration
	 * @return the JNDI name.
	 */
	protected abstract String getJNDIName();
	
	/** Returns the {@link org.gcube.common.core.resources.GCUBEService} resource of the service.
    * @return the resource.*/
   @ReadOnly public GCUBEService getService() {return service;}
   
   /** Returns the {@link org.gcube.common.core.resources.GCUBERunningInstance} resource of the service.
    * @return the resource.*/
   @ReadOnly public GCUBERunningInstance getInstance() {return this.instance;}
   
	/**
	 * Return the management bean of the Running Instance.
	 * @return the bean.
	 */
	public RI getManagementBean() {return this.mbean;}
	
    /** {@inheritDoc} */
	public File getFile(String path, boolean ... writeMode) throws IllegalArgumentException {
		return super.getFile(GHNContext.getContext().getLocation()+File.separator+(String)this.getProperty(CONFIG_DIR_JNDI_NAME,true)+File.separator+path,writeMode);	
	}
	
	/**
	* Gives read or write access to a {@link java.io.File} that will persist across redployments of the Running Instance.
	* @param path the file path.
    * @param writeMode (optional) the access mode, <code>true</code> for write access and <code>false</code> for read access (default).
    * @return the file.
    * @throws IllegalArgumentException if access is in write mode and the path is to a folder.
    * @see GCUBEContext#getFile(String, boolean...)
    */
	public File getPersistentFile(String path, boolean ... writeMode) throws IllegalArgumentException {
		return super.getFile(GHNContext.getContext().getStorageRoot()+File.separatorChar+this.getName()+File.separatorChar+path, writeMode);	
	}
	
	/**
	 * Returns the root of persistence of the Running Instance on the local file system.
	 * Anything below it will be persisted across re-deployments of the Running Instance.
	 * @return the file representation
	 */
	public File getPersistenceRoot() {return this.getPersistentFile("");}
	
	/** {@inheritDoc} */
	public Object getProperty(String prop, boolean ...required) throws RuntimeException {
		return super.getProperty(GHNContext.JNDI_SERVICES_BASE_NAME+this.getJNDIName()+"/"+prop, required);
	}

   /**
	 * Returns the service identifier. 
	 * @return the identifier.
	 */
	public String getID() {return this.service!=null?this.getService().getID():null;}
   
	/** Returns the name of the service.
	 * @return the name.*/	
   public String getServiceClass() {return this.service==null?null:this.service.getServiceClass();}
   
   /** Returns the name of the service.
	 * @return the name.*/	
   public String getName() {return this.service==null?super.getName():this.service.getServiceName();}
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////////////////// STATUS MANAGEMENT
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/** Enumerates the possible statuses of the service. */
	public static enum Status {
		DEPLOYED(Type.START) {List<Status> previous(){return Arrays.asList(Status.DEPLOYED, Status.UNREACHABLE);}public String toString(){return "started";}},
		INITIALISED(Type.PRIVATE){List<Status> previous(){return Arrays.asList(Status.DEPLOYED,Status.UNREACHABLE);}public String toString(){return "initialised";}},
		READIED(Type.PUBLIC){List<Status> previous(){return Arrays.asList(Status.INITIALISED,Status.UNREACHABLE);} public String toString(){return "ready";}},
		UPDATED(Type.PSEUDO){List<Status> previous(){return Arrays.asList(Status.DEPLOYED,Status.INITIALISED,Status.READIED,Status.FAILED,Status.DOWN,Status.UNREACHABLE);}public String toString(){return "updated";}},
		STATECHANGED(Type.PSEUDO){List<Status> previous(){return Arrays.asList(Status.INITIALISED,Status.READIED,Status.UNREACHABLE);}public String toString(){return "state changed";}},
		FAILED(Type.PUBLIC){List<Status> previous(){return Arrays.asList(Status.DEPLOYED,Status.INITIALISED,Status.READIED,Status.STATECHANGED,Status.FAILED,Status.UNREACHABLE);}public String toString(){return "failed";}},
		DOWN(Type.PUBLIC){List<Status> previous(){return Arrays.asList(Status.DEPLOYED,Status.INITIALISED,Status.READIED,Status.STATECHANGED,Status.FAILED,Status.UNREACHABLE,Status.DOWN);}public String toString(){return "down";}},
		UNREACHABLE(Type.PUBLIC){List<Status> previous(){return Arrays.asList(Status.READIED,Status.STATECHANGED,Status.UNREACHABLE);}public String toString(){return "unreachable";}};
		/**Enumerates the status types.*/
		public static enum Type {START,PSEUDO,PRIVATE,PUBLIC};//imply different actions upon transition
		/**The status type.*/
		protected final Type type;
		/**Creates an instance of a given type.
		 * @param type the type.*/
		private Status(Type type) {this.type=type;}
		/**Returns the list of statuses from which this status may be reached. Overridden by each status instance.
		 * @return the status list.*/
		abstract List<Status> previous();
	}
	/** Illegal state transition exception (runtime). */
	public static class IllegalStateTransitionException extends IllegalArgumentException{private static final long serialVersionUID = 1L;};
	/** Illegal state transition exception (runtime). */
	public static class StateTransitionException extends RuntimeException{private static final long serialVersionUID = 1L;StateTransitionException(Exception e){super(e);}}
	
	/** The current status of the service. */
	private volatile Status status = Status.DEPLOYED;
	
	
   /** Sets the current status of the service.
	 * @param status the status.
	 * @throws IllegalStateTransitionException if the transition from the current state to the required state is illegal.
	*/
	@SuppressWarnings({ "unchecked" })
	@Restricted(Restricted.GCORE) public synchronized void setStatus(Status status) throws IllegalStateTransitionException,StateTransitionException  {

		Status currentStatus=this.status;
		try {
				
			if (!status.previous().contains(currentStatus)) throw new IllegalStateTransitionException();//check on legal transitions

			 if (status.type!=Status.Type.PSEUDO) { //only real transitions take place
					this.status=status; 
					if (status.type!=Status.Type.START) logger.info("moved to status "+status.toString().toUpperCase());//only non-start states are logged.
					if (status.type==Status.Type.PUBLIC) this.setStatus(Status.UPDATED);//only public states trigger an updated
				 }
			 else logger.trace("RI has been "+status.toString().toUpperCase());
			 
			 switch(status) {
			 	case DEPLOYED: this.LTEventProducer.notify(RILifetimeTopic.DEPLOYED, new RILifetimeEvent());break;
			 	case INITIALISED: this.LTEventProducer.notify(RILifetimeTopic.INITIALISED, new RILifetimeEvent());break;
				case READIED : 	this.LTEventProducer.notify(RILifetimeTopic.READY,new RILifetimeEvent());break;
				case UPDATED :this.LTEventProducer.notify(RILifetimeTopic.UPDATED, new RILifetimeEvent());this.onUpdate();break;
				case STATECHANGED:this.LTEventProducer.notify(RILifetimeTopic.STATECHANGE, new RILifetimeEvent());this.onStateChange();break;
				case FAILED :this.LTEventProducer.notify(RILifetimeTopic.FAILED,new RILifetimeEvent());this.onFailure();break;
				case DOWN :this.LTEventProducer.notify(RILifetimeTopic.DOWN,new RILifetimeEvent());this.onShutdown();break;
			 }
							 
			}catch (Exception e) {
				 String errMsg = "could not complete transition from "+currentStatus+" to "+status;
				 //logs and throw exception in line with severity 
				 if (status.type==Status.Type.PSEUDO) logger.warn(errMsg,e); //log warning for pseudo states
				 else {logger.fatal(errMsg,e);this.status=Status.FAILED;throw new StateTransitionException(e);} //log and throw error for all other states 
			}
	}
	
	public ServiceSecurityStatus getServiceSecurityStatus ()
	{
		if (this.serviceSecurityStatus == null)
		{
			logger.debug("Loading service security status");
			Boolean serviceSecurity = (Boolean) getProperty(GHNContext.SECURITY_JNDI_NAME);
			
			if (serviceSecurity == null)
			{
				this.logger.debug("Service security not set");
				this.serviceSecurityStatus = ServiceSecurityStatus.NOT_SET;
			}
			else if (serviceSecurity == false)
			{
				this.logger.debug("Service security set to false");
				this.serviceSecurityStatus = ServiceSecurityStatus.DISABLED;
			}
			else // if (serviceSecurity == true)
			{
				this.logger.debug("Service security set to true");
				this.serviceSecurityStatus = ServiceSecurityStatus.ENABLED;
			}
		}
		
		return this.serviceSecurityStatus;
	}
	
    /** Returns the current status of the service.
    * @return the status.*/
	public Status getStatus() {return this.status;}

	/** Returns the default exception
	 * @param msg the fault message.
	 * @param cause (optional) the cause of the fault.
	 * @return the default {@link org.gcube.common.core.faults.GCUBEFault GCUBEFault}.*/
	public GCUBEException getDefaultException(String msg, Throwable cause) {return new GCUBERetryEquivalentException(msg,cause);}

	/** Returns the default exception
	 * @param cause the cause of the fault.
	 * @return the default {@link org.gcube.common.core.faults.GCUBEFault GCUBEFault}.*/
	public GCUBEException getDefaultException(Throwable cause) {return new GCUBERetryEquivalentException(cause);}
	
	
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////// SECURITY MANAGEMENT
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    

	/** Embedded security manager. */ 
	private volatile GCUBEServiceSecurityManager securityManager;
	
	/**
	* Returns the {@link org.gcube.common.core.security.GCUBEServiceSecurityManager GCUBEServiceSecurityManager} 
	* to which the context will delegate security-related functionality. 
	* 
	* <p>
	* If not otherwise configured, it returns a {@link GCUBESimpleServiceSecurityManager} 
	* suitable for services which, when deployed in a secure environment, either propagate caller credentials or else do not use security altogether. 
	* Services which instead need to use their own credentials can configure the service to use a {@link GCUBEServiceSecurityManagerImpl}
	* or, if available, an alternative implementation of the {@link GCUBEServiceSecurityManager} interface.
	* 
	* @return the manager.
	*/
	public GCUBEServiceSecurityManager getSecurityManager() {return this.securityManager;}
    /**{@inheritDoc}*/
	@Restricted public void setSecurity(Remote s, AuthMode e, DelegationMode d) throws Exception {this.securityManager.setSecurity(s, e, d);}
	/**{@inheritDoc}.*/
	public boolean isSecurityEnabled() {return this.securityManager.isSecurityEnabled();}
	/**{@inheritDoc}.*/
	@Restricted public SecurityCredentials getCredentials() {return this.securityManager.getCredentials();}
	/**{@inheritDoc}.*/
	public GCUBEAuthzPolicy getPolicy() {return this.securityManager.getPolicy();}
	/**{@inheritDoc}.*/
	public void initialise(GCUBEServiceContext ctxt) throws Exception {}//do nothing
	/**{@inheritDoc}.*/
	@Restricted public SecurityCredentials getServiceCredentials() throws Exception {return this.securityManager.getServiceCredentials();}
	/**{@inheritDoc}.*/
	@Restricted public SecurityCredentials getCallerCredentials() throws Exception {return this.securityManager.getCallerCredentials();}
	/**{@inheritDoc}.*/
	@Restricted public void useCredentials(SecurityCredentials credentials) throws Exception {this.securityManager.useCredentials(credentials);}
	/**{@inheritDoc}.*/
	@Restricted public void useCredentials(Thread thread, SecurityCredentials ... credentials) throws Exception {this.securityManager.useCredentials(thread,credentials);}
	/**{@inheritDoc}.*/
	@Restricted public boolean needServiceCredentials() {return this.securityManager.needServiceCredentials();}
	/**{@inheritDoc}.*/
	@Restricted public void propagateCallerCredentials(boolean propagateCallerCredentials) { this.securityManager.propagateCallerCredentials(propagateCallerCredentials);};
	/**{@inheritDoc}.*/
	@Restricted public void setAuthMethod(AuthMethod m) { this.securityManager.setAuthMethod(m);};
	/**{@inheritDoc}.*/
	@Deprecated
	@Restricted public void useCredentials(GSSCredential credentials) throws Exception {this.securityManager.useCredentials(credentials);}
	
//	/**{@inheritDoc}.*/
//	public void authoriseCall(GCUBECall authzRequest) throws GCUBEException {this.securityManager.authoriseCall(authzRequest);}
		/**{@inheritDoc}.*/
	public void subscribe(LifetimeConsumer c,LifetimeTopic ... topics) {this.securityManager.subscribe(c,topics);}
	/**{@inheritDoc}.*/
	public void unsubscribe(LifetimeConsumer c,LifetimeTopic ... topics) {this.securityManager.unsubscribe(c,topics);};
		
	
	
	// AUTH MANAGER
	private GCUBEServiceAuthenticationController authenticationManager;
	private GCUBEServiceAuthorizationController authorizationManager;

	
	
	public GCUBEServiceAuthenticationController getAuthenticationManager() {
		return authenticationManager;
	}

	public GCUBEServiceAuthorizationController getAuthorizationManager() {
		return authorizationManager;
	}
	
	



	/**
	 * If security is enabled, it indicates that outgoing calls in a given thread 
	 * must use the credentials associated with the incoming call. 
	 * It has no effect otherwise.
	 * <p> An invocation of this method is equivalent to: 
	 * <code>context.useCredentials(context.getCallerCredentials())</code> or <code>context.useCredentials(thread, context.getCallerCredentials())</code>
	 * @param thread (optional) the thread. If omitted, the current thread is assumed.
	 * @throws Exception if security is enabled but the caller credentials could not be used.
	 */
	@Restricted public void useCallerCredentials(Thread ... thread) throws Exception {
		if (!this.securityManager.isSecurityEnabled()) return;
		SecurityCredentials callerCredentials = this.securityManager.getCallerCredentials();
		logger.debug("Executing on behalf of caller in VRE "+ScopeProvider.instance.get());
		if (thread.length==0) {
			this.securityManager.useCredentials(callerCredentials);	
		}
		else {
			this.securityManager.useCredentials(thread[0], callerCredentials);
		}	
	}

	/**
	 * If security is enabled, it indicates that outgoing calls in the
	 * current thread must use service credentials. It has no effect otherwise.
	 * <p> An invocation of this method is equivalent to: 
	 * <p><code>context.useCredentials(context.getServiceCredentials(name))</code> or
	 * <code>context.useCredentials(thread,context.getServiceCredentials(name))</code>
	 *  * @param thread (optional) the thread. If omitted, the current thread is assumed.
	 * @throws Exception if security is enabled but service credentials could not be produced.
	 */
	@Restricted public void useServiceCredentials(Thread ... thread) throws Exception {
		if (!this.securityManager.isSecurityEnabled()) return;
		SecurityCredentials credentials = this.securityManager.getServiceCredentials();
		this.securityManager.useCredentials(credentials);		
		if (thread.length==0) {
			this.securityManager.useCredentials(credentials);	
		}
		else {
			this.securityManager.useCredentials(thread[0], credentials);
		}
	}


	
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////// SCOPE MANAGEMENT
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
		
     /**Gets the start scopes of the RI.
     * @return the scopes.*/
    public GCUBEScope[] getStartScopes() {
    	GCUBEScope[] configScopes = null;
		String config = ((String) this.getProperty(GCUBEServiceContext.START_SCOPES_JNDI_NAME));
		//inherits GHN scope in the lack of explicit configuration
		if (config==null) configScopes = GHNContext.getContext().getGHN().getScopes().values().toArray(new GCUBEScope[0]); 
		else {
			String[] startScopes =  config.split(",");
			configScopes = new GCUBEScope[startScopes.length];
    		for (int idx=0; idx<startScopes.length;idx++) configScopes[idx] = GCUBEScope.getScope(startScopes[idx].trim());
		}
		return configScopes;    	    
    }
	
    private GCUBEScopeManager scopeManager = new GCUBEScopeManagerImpl();
    
	/**Returns the inner scope manager used by the context.
	 * @return the scope manager.
	 * @deprecated as to 1.6.0 use {@link ScopeProvider#instance} instead
	 * */
    @Deprecated
	@Restricted public GCUBEScopeManager getScopeManager() {
		return scopeManager;
	}
	
	/**
	 * @deprecated as to 1.6.0 use {@link ScopeProvider#instance#get()}.
	 **/
    @Deprecated
	@Restricted public GCUBEScope getScope() {
    	String currentScope = ScopeProvider.instance.get();
		return GCUBEScope.getScope(currentScope==null?null:currentScope);
	}
	
	/**
	 * @deprecated as to 1.6.0 calls are prepared internally to {@link GCUBERemotePortTypeContext}s.
	 * */
    @Deprecated
	@Restricted public void prepareCall(Remote remote, String clazz, String name, GCUBEScope ... scope) {
				
		//some sanity checks on scope.
		if (scope!=null && scope.length>0) {
			if (!this.getInstance().inScope(scope[0])) throw new IllegalScopeException();
		}
		else {
			GCUBEScope currentScope = GCUBEScope.getScope(ScopeProvider.instance.get());
			if (!this.getInstance().inScope(currentScope)) throw new IllegalScopeException();
		}
		
		scopeManager.prepareCall(remote, clazz, name,scope);
	}
	
	/**
	 * Associates a scope with the current thread.
	 * 
	 * */
	@Restricted 
	public void setScope(GCUBEScope scope) throws IllegalScopeException {
		//accepts only scopes consistent with RI's
		if (!this.getInstance().inScope(scope)) throw new IllegalScopeException(scope.toString());
		ScopeProvider.instance.set(scope==null?null:scope.toString());
	}
	
	/**
	 * @deprecated as to 1.6.0, using {@link ScopeProvider#instance} does not require setting scopes on child threads. 
	 * */
	//TODO:ONLY FOR BINARY COMPATIBILITY: MUST ELIMINATE SOONER OR LATER
	@Restricted 
	@Deprecated
	public void setScope(Thread thread, GCUBEScope scope) throws IllegalScopeException {
		scopeManager.setScope(thread, scope);
	}
	
	/**
	 * @deprecated as to 1.6.0, using {@link ScopeProvider#instance} does not require setting scopes on child threads. 
	 * */
	@Restricted 
	@Deprecated
	public void setScope(Thread thread, GCUBEScope ... scope) throws IllegalScopeException {
		if (scope!=null && scope.length>0)
			if (!this.getInstance().inScope(scope[0])) throw new IllegalScopeException(scope[0].toString());
		scopeManager.setScope(thread, scope);
	}
	
	/**
	 * Adds one or more scopes to the service instance.  
	 * Only scopes which are enclosed in some scope of the GHN are actually added.
	 * @param scopes the scopes.
	 * @return the list of scopes actually added to the service instance. 
	 * @throws IllegalArgumentException if no scopes are provided in input.
	 */
	@Restricted public synchronized Set<GCUBEScope> addScope(GCUBEScope ... scopes) {
		
		//sanity check, we need at least one scope before manipulating list
		if (scopes==null || scopes.length==0) throw new IllegalArgumentException();	
		
		//filter wrt GHN and service dependencies.
		Set<GCUBEScope> acceptedScopes = new HashSet<GCUBEScope>();
		for (GCUBEScope  scope : scopes)
			if (GHNContext.getContext().getGHN().inScope(scope) && this.getService().inScope(scope))
				acceptedScopes.add(scope);
		
		//(safer to delegate to internal checks in case there are in the future) and publish
		// only if there are survivors 
		if (acceptedScopes.size()>0) { 
				acceptedScopes = this.getInstance().addScope(acceptedScopes.toArray(new GCUBEScope[0]));
				if (acceptedScopes.size()>0) this.setStatus(Status.UPDATED);
		}
		
		return acceptedScopes; //feedback to callers
	}
	
	/**
	 * Removes one or more scopes from the service instance.
	 * Only scopes currently associated with the instance are actually removed.
	 * @param scopes the scopes.
	 * @return the list of scopes actually removed from the service instance.
	 * @throws IllegalArgumentException if no scopes are provided in input.
	 */
	@Restricted public synchronized Set<GCUBEScope> removeScope(GCUBEScope ... scopes) {
		
		Set<GCUBEScope> accepted = this.getInstance().removeScope(scopes);
		if (accepted.size()>0) {
			
			ISPublisher publisher = null;//fetch publisher implementation
			try {publisher = GHNContext.getImplementation(ISPublisher.class);} 
			catch(Exception e) {throw new RuntimeException(e);}

			if (accepted.size()>0) logger.info("unpublishing RI ("+ this.getName() + ") profile from scope(s) "	+ accepted); 
			for (GCUBEScope scope : accepted) {//unpublish from scope
				if (GHNContext.getContext().getMode() != Mode.STANDALONE) { 			
					try {
						publisher.removeGCUBEResource(this.instance.getID(), GCUBERunningInstance.TYPE, scope, this.securityManager);			
					} catch (Exception e) {logger.error("unable to unpublish the RI ("+ this.getName() + ") profile from scope " + scope, e);} 
				}
			}
			
			this.setStatus(Status.UPDATED);//update only if some scope was actually removed.
	
		}
		return accepted;
	}
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////////PLUGIN MANAGEMENT
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/** The plugin manager */
	GCUBEPluginManager<?> pluginManager;
	
	/**Returns the plugin manager.
	 * @return the manager.*/
	public GCUBEPluginManager<?> getPluginManager() {return this.pluginManager;}
	
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////////RI LIFETIME EVENT MANAGEMENT
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/** Enumerates RI lifetime topics.*/
	public static enum RILifetimeTopic implements GCUBETopic {DEPLOYED,INITIALISED,READY,FAILED,UPDATED,STATECHANGE,DOWN}

	/** Embedded {@link org.gcube.common.core.utils.events.GCUBEProducer GCUBEProducer} for RI lifetime events. */
	protected GCUBEProducer<RILifetimeTopic,GCUBEServiceContext> LTEventProducer = new GCUBEProducer<RILifetimeTopic,GCUBEServiceContext>();  
	
	/** RI lifetime event. */
	public class RILifetimeEvent extends GCUBEEvent<RILifetimeTopic,GCUBEServiceContext> {
		public RILifetimeEvent() {this.payload = GCUBEServiceContext.this;this.producer=GCUBEServiceContext.this.LTEventProducer;}
	}	
	
	/**
    * Subscribes a consumer to RI lifetime events for one or more RI lifetime topics.
    * @param consumer the consumer.
    * @param topics the topics of interest.
    * @throws Exception if the subscription could not be completed.
    */
   public void subscribeLifetTime(Consumer consumer, RILifetimeTopic ...topics) throws Exception {
	   if (topics.length==0) topics = RILifetimeTopic.values();
	   this.LTEventProducer.subscribe(consumer,topics);
   }
   
   /**
    * Unsubscribes a consumer from RI lifetime events for one or more RI lifetime topics.
    * @param consumer the consumer.
    * @param topics the topics.
    */
   public void unsubscribeLifetTime(Consumer consumer, RILifetimeTopic ...topics) {
	   if (topics.length==0) topics = RILifetimeTopic.values();
	   this.LTEventProducer.unsubscribe(consumer,topics);
   }
	   
   /**
    * Notifies of a change to the persistent state of the RI.
    */
   public void notifyStateChange() {this.setStatus(Status.STATECHANGED);}
	
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////INTERNAL CONSUMERS
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
    /**
     * A {@link Consumer} for initialised DEPLOYED RIs.
     * @author Fabio Simeoni (University of Strathclyde)
     *
     */
    @SuppressWarnings("unchecked")
    class Initialiser extends Consumer {

		@Override /**{@inheritDoc} */ 
		public RILifetimeTopic[] getTopics() {return new RILifetimeTopic[]{RILifetimeTopic.DEPLOYED};}
		
		@Override /**{@inheritDoc} */
		protected void onRIDeployed(RILifetimeEvent event) throws Exception  {
			
			//de-serialise and initialise service 
		    service = GHNContext.getImplementation(GCUBEService.class);
		    //NOTE: the line above is the first call to the GHN from service code.
		    //first service that does causes GHNCOntext loading and its initialisation.
		    //if that service passes this line, the gHN is ok. otherwise all goes dead (see static initialiser in GHNContext).
		    service.load(new FileReader(getFile(PROFILE_FILE_NAME)));

		    logger.setContext(GCUBEServiceContext.this); //update logger so as to use real service name (not it has read one)
		    service.setLogger(GCUBEServiceContext.this.logger);
		    Thread.currentThread().setName(service.getServiceName()+"-"+Thread.currentThread().getName());
		    service.addScope(GHNContext.getContext().getStartScopes()[0].getInfrastructure());
			logger.info("INITIALISING RUNNING INSTANCE");
		    			
			//register RI mbean
			mbean = new RI(GCUBEServiceContext.this);
			ManagementFactory.getPlatformMBeanServer().registerMBean(mbean, new ObjectName(GHNContext.MBEANS_PREFIX+":type=RI,value="+getName()+"."+getServiceClass())); 
			
		    //deserialise or builds RI
		    instance = GHNContext.getImplementation(GCUBERunningInstance.class);
		    instance.setLogger(GCUBEServiceContext.this.logger);
		    File profile = getPersistentFile(RIPROFILE_FILENAME);
		    
		    Builder builder = new Builder(GCUBEServiceContext.this);
		    builder.setLogger(logger);
		    if (profile.exists()) {
			   	try {
			   		 logger.trace("loading RI profile");
			   		 instance.load(new FileReader(profile));
			   		 builder.updateRIResource();
			   	 }
			   	 catch (Exception e) {
			   		logger.warn("could not load RI profile from profile..regenerating it",e);
			   		builder.createRIResource();
			   	 }
			}
			else builder.createRIResource();//build RI from scratch
		    
		    securityManager = configureServicesecurityManager();
		    authenticationManager = (GCUBEServiceAuthenticationController) configureServiceSecurityControlManager(AUTHENTICATION_MANAGER_JNDI_NAME,GCUBEServiceAuthenticationController.class,securityManager);
		    authorizationManager = (GCUBEServiceAuthorizationController) configureServiceSecurityControlManager(AUTHORISATION_MANAGER_JNDI_NAME,GCUBEServiceAuthorizationController.class,securityManager);

//		    authenticationManager = SecurityContext.getInstance().generateServiceAuthenticationController(GCUBEServiceContext.this, securityManager);
//		    authorizationManager = SecurityContext.getInstance().generateServiceAuthorizationController(GCUBEServiceContext.this, securityManager);
		    
			//configure the persistence manager
			GCUBERIPersistenceManagerProfile persistenceManagerProfile = (GCUBERIPersistenceManagerProfile) getProperty(PERSISTENCE_MANAGER_JNDI_NAME);
			if (persistenceManagerProfile!=null) {
				//uses impl's classloader, in case it brings along its own persistent manager
				Class<GCUBERIPersistenceManager> persistenceManagerClass = (Class<GCUBERIPersistenceManager>) getClass().getClassLoader().loadClass(persistenceManagerProfile.getClassName());//placate compiler
	    		if (!GCUBERIPersistenceManager.class.isAssignableFrom(persistenceManagerClass)) //but do run-time check
	    			throw new Exception(persistenceManagerProfile.getClassName()+" does not implement "+GCUBERIPersistenceManager.class.getName());
	    		persistenceManager=persistenceManagerClass.newInstance();
			}
			else persistenceManager=new GCUBERINoPersistenceManager(GCUBEServiceContext.this,new GCUBERIPersistenceManagerProfile());
			persistenceManager.exclude(RIPROFILE_FILENAME+".*");
			persistenceManager.setLogger(logger);
	 	    logger.info("managing remote persistence with a "+persistenceManager.getClass().getSimpleName());
	 	    
	 	    //configure the plugin manager
	 	    GCUBEPluginManagerProfile pluginManagerProfile = (GCUBEPluginManagerProfile) getProperty(PLUGIN_MANAGER_JNDI_NAME);
	 	    if (pluginManagerProfile!=null) {
	 	    	//uses impl's classloader, in case it brings along its own plugin manager
	 	    	Class<GCUBEPluginManager<?>> pluginManagerClass = (Class<GCUBEPluginManager<?>>) getClass().getClassLoader().loadClass(pluginManagerProfile.getClassName());//placate compiler
	 	    	if (!GCUBEPluginManager.class.isAssignableFrom(pluginManagerClass)) //but do run-time check
	 	    		throw new Exception(pluginManagerProfile.getClassName()+" does not implement "+GCUBEPluginManager.class.getName());
	 	    	Constructor<GCUBEPluginManager<?>> constructor = pluginManagerClass.getConstructor(new Class[] {});
	    		pluginManager=constructor.newInstance(new Object[]{});
	    		pluginManager.initialise(GCUBEServiceContext.this, pluginManagerProfile);
	    		pluginManager.setLogger(logger);
	    		persistenceManager.exclude(GCUBEPluginManager.PLUGINS_DIRECTORY_NAME);
	    		logger.info("managing plugins with a "+pluginManager.getClass().getSimpleName());
	 	    }
			
		    //register the service in the node context
		    GHNContext.getContext().registerService(GCUBEServiceContext.this);
		    onInitialisation();//callback to specific service implementation
		    setStatus(Status.INITIALISED);//triggers state change
		    
		    LTEventProducer.unsubscribe(this, this.getTopics()); //disengage now
			
		}
	
    }
    

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private GCUBEServiceSecurityManager configureServicesecurityManager () throws Exception
    {
    	logger.info("Generating security manager...");
    	GCUBEServiceSecurityManager securityManager = null;
	    //configure security manager
	    String securityManagerClassName = (String) getProperty(SECURITY_MANAGER_JNDI_NAME);
    	if (securityManagerClassName!=null) {
    		//get class
    		Class<GCUBEServiceSecurityManager> securityManagerClass = (Class) Class.forName(securityManagerClassName);//placate compiler
    		if (!GCUBEServiceSecurityManager.class.isAssignableFrom(securityManagerClass)) //but do run-time check
    			throw new Exception(securityManagerClassName+" does not implement "+GCUBEServiceSecurityManager.class.getName());
    		//invoke constructor reflectively
    		securityManager=securityManagerClass.newInstance();
    	}
    	else securityManager=GHNContext.getImplementation(GCUBEServiceSecurityManager.class);
    	securityManager.initialise(GCUBEServiceContext.this);
    	
    	GCUBEDefaultSecurityConfiguration defaultSecurityConfiguration = SecurityContextFactory.getInstance().getSecurityContext().getDefaultServiceSecurityConfiguration();
    	
    	if (defaultSecurityConfiguration != null && defaultSecurityConfiguration.defaultCredentialPropagationSet()&& defaultSecurityConfiguration.propagateCallerCredentialsOverride())
    	{
    		logger.debug("Override every propagate configuration with the container configuration");
    		securityManager.propagateCallerCredentials(defaultSecurityConfiguration.propagateCallerCredentials());
    	}
    	else 
    	{
    		logger.debug("Loading credentials propagation property from jndi service environment");
    		Boolean useCallerCredentials = (Boolean) getProperty(PROPAGATE_CALLER_CREDENTIALS_JNDI_NAME);
    		logger.debug("Credential propagation property = "+useCallerCredentials);
    		
    		if (useCallerCredentials != null) securityManager.propagateCallerCredentials(useCallerCredentials);
    		else if (defaultSecurityConfiguration != null && defaultSecurityConfiguration.defaultCredentialPropagationSet())
    		{
    			logger.debug("Loading default configuration");
    			securityManager.propagateCallerCredentials(defaultSecurityConfiguration.propagateCallerCredentials());
    		}
    		else
    		{
    			logger.warn("No service propagation property set and no default configuration available");
    		}
    		
    		
    	}
    		
    	logger.info("managing security with a "+securityManager.getClass().getSimpleName());
    	return securityManager;
    }

    
      /**
     * Service security manager generator class
     * @param securityManagerJNDIName the JNDI name of the class to be initialised
     * @param securityManagerClass the Class (in implementation.properties) to be initialised 
     * @return
     * @throws Exception
     */
    @SuppressWarnings ({ "unchecked"})
    private GCUBEServiceSecurityController configureServiceSecurityControlManager (String securityControlManagerJNDIName, Class<? extends GCUBEServiceSecurityController> securityControlManagerClass, GCUBEServiceSecurityManager securityManager) throws Exception
    {
    	logger.info("Generating security control manager "+securityControlManagerClass.getCanonicalName());
    	GCUBEServiceSecurityController securityControlManager = null;
	    //configure security manager
	    String securityControlManagerClassName = (String) getProperty(securityControlManagerJNDIName);
    	if (securityControlManagerClassName!=null) {
    		//get class
    		Class<GCUBEServiceSecurityController> securityManagerConcreteClass = (Class<GCUBEServiceSecurityController>) Class.forName(securityControlManagerClassName);//placate compiler
    		if (!GCUBEServiceSecurityController.class.isAssignableFrom(securityManagerConcreteClass)) //but do run-time check
    			throw new Exception(securityControlManagerClassName+" does not implement "+GCUBEServiceSecurityController.class.getName());
    		//invoke constructor reflectively
    		securityControlManager=securityManagerConcreteClass.newInstance();
    	}
    	else securityControlManager=GHNContext.getImplementation(securityControlManagerClass);
    	securityControlManager.initialise(GCUBEServiceContext.this,securityManager);
    	logger.info("managing security control with a "+securityControlManager.getClass().getSimpleName());
    	return securityControlManager;
    }
    
    
    
 
    
    List<GCUBEServiceContext> codeployed = new ArrayList<GCUBEServiceContext>();
    
    /**
     * A {@link Consumer} for staging INITIALISED RIs.
     * @author Fabio Simeoni (University of Strathclyde)
     *
     */
    protected class Stager extends Consumer 
    {
    	/** Count of staging tasks still to do */
    	byte toDo;
    	/** Failure observed by any of the staging task */
    	Exception failure;
    	/**Adds a staging task.*/
    	synchronized void addTask() {this.toDo++;}
    	/** Marks task completion. */
    	synchronized void doneTask() {this.toDo--;this.notify();}
    	/**Indicates whether the staging tasks are completed.
    	 * @return <code>true</code> if they are, <code>false</code> otherwise.*/
    	synchronized boolean finished() {return this.toDo==0;}
    	/**
    	 * Reports the first failure of a staging task.
    	 * @param e the failure.
    	 */
    	synchronized void failedTask(Exception e) 
    	{
    		if (this.failure==null) 
    			{
    			this.failure=e;this.notify();
    				
    			}
    	}
 
    	/**
    	 * Indicates whether any of the staging tasks has failed.
    	 * @return <code>true</code> if some task has failed, <code>false</code> otherwise.
    	 */
    	synchronized boolean hasFailed() 
    	{
    		return this.failure!=null;
    	}
    	
    	@Override /**{@inheritDoc} */ 
		public RILifetimeTopic[] getTopics() 
    	{
    		return new RILifetimeTopic[]{RILifetimeTopic.INITIALISED};
    	}
		
    	@Override /**{@inheritDoc} */
		protected void onRIInitialised(RILifetimeEvent event) throws Exception 
		{
			Thread.currentThread().setName(service.getServiceName()+"-"+Thread.currentThread().getName());
			//if needed, subscribe and wait for service credentials
    		Thread first = new Thread("SecurityMonitor") 
    		{
    			private int count = 2;
    			private GCUBEServiceSecurityManager.LifetimeConsumer consumer = new GCUBEServiceSecurityManager.LifetimeConsumer() 
    			{
					protected void onPolicyUpdate() 
					{
						done();
					}
					
					protected void onCredentialUpdate() 
					{
						done();
					}
				};
 
				private synchronized void done() 
				{
    				count--;
    				if (count==0) {doneTask();securityManager.unsubscribe(consumer);}
    			}
    			
				public void run() {GCUBEServiceContext.this.securityManager.subscribe(consumer);}
    		};
    		
    		//recover state
    		Thread second = new Thread ("StateStager") 
    		{
    			public void run() {try {persistenceManager.recover();doneTask();}catch(Exception e){failedTask(e);}}
    		};
    		
    		//manage codependencies
    		Thread third = new Thread("DependencyMonitor") 
    		{
    			class CyclicDependencyException extends RuntimeException
    			{
    				private static final long serialVersionUID = 1L;
    			}
    	 		
    			public void run() 
    			{
    	 			final List<String> codependencies = new ArrayList<String>();//prepare for codeployed dependencies to worry about
    	 			
    	 			try 
    	 			{
    	 				 GHNConsumer consumer = new GHNConsumer() 
    	 				 {
    	 					boolean waitForDependency(GCUBEServiceContext context, ServiceDependency dependency,List<String> services, String chain)
    	 					{
    	 						logger.debug("checking if the service is waiting for dependencies");
    	 						//note: return value matters only for top-level invocations.
    	 						//the others are only used for cycle detection, which is signaled via exception.
    	 						//this maximises code-reuse though it is a tad dirty.
    	 						String nextChain = chain+"->"+dependency.getName();
    	 						//if we have seen this dependency before the we closing a cycle to one of ours transitive dependencies, not our business.
    	 						//but we need to stop recursion
	 							
    	 						if (services.contains(dependency.getClazz()+dependency.getName())) 
    	 						{
	 								//logger.debug("closing co-dependency cycle:"+nextChain);
    	 							logger.debug("waiting...");
	 								return true; 
	 							}
    	 						
	 							//is this a co-dependency?
    	 						GCUBEServiceContext dependencyContext=null;
	 							
    	 						try
    	 						{
    	 							dependencyContext = GHNContext.getContext().getServiceContext(dependency.getClazz(), dependency.getName());//which is codeployed
	 							}
    	 						catch (Exception e) 
    	 						{
    	 							logger.debug("not waiting");
    	 							return false;
    	 						} //no
	 							
	 							//is there any point waiting for it?
	 							if (dependencyContext.getStatus()==Status.READIED || dependencyContext.getStatus()==Status.FAILED) 
	 							{
	 								logger.debug("not waiting");
	 								//logger.debug("dependency chain:"+nextChain+" already resolved because "+dependencyContext.getName()+" is ready or has failed");
	 								return false;//no
	 							}
	 							
	 							//third, does it depend on us?
	 							if (dependency.getClazz().equals(GCUBEServiceContext.this.getServiceClass()) && 
 										dependency.getName().equals(GCUBEServiceContext.this.getName())) 
	 							{
	 								logger.warn("detected cyclic co-dependency chain:"+nextChain); 
	 								throw new CyclicDependencyException();//let us distinguish no need to wait from cannot wait
	 								//return false; //yes
	 							}
	 							
	 							//fifth, does it transitively depend on us?
	 							List<String> newContexts = new ArrayList<String>(services);
	 							newContexts.add(dependency.getClazz()+dependency.getName());
	 							
	 							for (ServiceDependency dep : dependencyContext.getService().getDependencies())
	 								//we detect cycles here, return value does not matter only exception does (see above)
	 								waitForDependency(dependencyContext,dep,newContexts,nextChain);
	 							logger.debug("waiting...");
	 							return true; 
    	 					}
    	 					
    	 					void checkIfDone()
    	 					{
    	 						if (codependencies.size()==0) 
    	 						{
    	 							doneTask();
    	 						}
    	 					}//anyone to wait on?
  
    	 					protected void onGHNReady(GHNLifeTimeEvent event)  
    	 					{
    	 						//when we are sure we know all deployed services;
    	 						super.onGHNReady(event);
    	 						Consumer sconsumer = new Consumer() 
    	 						{
    	 							//prepare a consumer for codeployed dependencies
									protected void onRIReady(RILifetimeEvent event) throws Exception 
									{//so that, when one becomes ready
										codependencies.remove(event.getPayload().getServiceClass()+event.getPayload().getName());//we stop worrying about it
										checkIfDone();//and check if there are other we we still need to worry about 
										event.getPayload().unsubscribeLifetTime(this,RILifetimeTopic.READY,RILifetimeTopic.FAILED);//free resources
									}
									
									protected void onRIFailed(RILifetimeEvent event)throws Exception{this.onRIReady(event);}//do as before
    	 						};
    	 						
    	 						for (ServiceDependency dep : getService().getDependencies())//for each logical dependency
    	 							try 
    	 								{
    	 								if (waitForDependency(GCUBEServiceContext.this,dep, new ArrayList<String>(),GCUBEServiceContext.this.getName())) 
    	 								{ //check whether we should wait for it
    	 									logger.trace("waiting on "+dep.getClazz()+":"+dep.getName());		
    	 									GHNContext.getContext().getServiceContext(dep.getClazz(),dep.getName()).subscribeLifetTime(sconsumer,RILifetimeTopic.READY,RILifetimeTopic.FAILED);//subscribe to be informed on its progress
    	 									codependencies.add(dep.getClazz()+dep.getName());//keep track of it
    	 								}	
    	 							} 
    	 							catch (CyclicDependencyException e) 
    	 							{}
    	 						    catch (Exception e) 
    	 						    {
    	 						    	logger.warn("could not synchronise with codependency "+dep.getClazz()+":"+dep.getName(),e);
    	 						    }		 
    	 					
    	 						checkIfDone();//check whether we need to wait  on anybody at all
    	 						GHNContext.getContext().unsubscribeGHNEvents(this,GHNTopic.READY);//free resources
    	 				    }
    	 				};
    	 				
    	 				GHNContext.getContext().subscribeGHNEvents(consumer, GHNTopic.READY);//wait to know who's on this gHN and who's not
    	    		} 
    	 			catch(Exception e)
    	 			{
    	 				failedTask(e);
    	 			}	
    			}
    		};

    		
    		first.setName(Thread.currentThread().getName());
    		second.setName(Thread.currentThread().getName());
    		third.setName(Thread.currentThread().getName());
    		addTask();first.start();
    		addTask();second.start();
    		addTask();third.start();
    	
    		
    		synchronized (this) 
    		{
	    		while (!finished()) 
	    		{// wait until all tasks have finished
	    			try 
	    			{
	    				Stager.this.wait();
	    			} 
	    			catch (InterruptedException e) 
	    			{
	    				return;
	    			}
	    			if (hasFailed()) 
	    			{
	    				//someone failed?
	    				logger.debug("staging error",this.failure);
	    				GCUBEServiceContext.this.setStatus(Status.FAILED);
	    				return;
	    			}	
	    		}	
    		}
    		// no point waiting if no longer ready-able
			if (Status.READIED.previous().contains(GCUBEServiceContext.this.getStatus())) 
			{
	    		onReady(); //callback to specific service implementation
		    	GCUBEServiceContext.this.setStatus(Status.READIED); //trigger state transition;
			}
	    	
			LTEventProducer.unsubscribe(this, this.getTopics()); //disengage now
		}
    	
        
    }

    /**
     * A {@link Consumer} for handling UPDATED RIs.
     * @author Fabio Simeoni (University of Strathclyde)
     *
     */
    class Updater extends Consumer {
    
    	@Override /**{@inheritDoc} */ 
		public RILifetimeTopic[] getTopics() {return new RILifetimeTopic[]{RILifetimeTopic.UPDATED};}
		
    	@Override /**{@inheritDoc} */
		protected void onRIUpdated(RILifetimeEvent event) throws Exception {
    		try {
				getInstance().getDeploymentData().setState(getStatus().toString());
				File file = getPersistentFile(RIPROFILE_FILENAME,true);
				FileWriter writer = new FileWriter(file);
				try {getInstance().store(writer);}  
				catch(Exception e) {logger.warn("could not serialise profile ("+ getName() + ")",e);}//we catch this one to avoid RI failure in case of error						
				ISPublisher publisher = GHNContext.getImplementation(ISPublisher.class);
				if (instance.getScopes().values().size()>0) logger.trace("publishing RI profile in scope(s) "	+ instance.getScopes().values());
				for (GCUBEScope scope : instance.getScopes().values())
					if (GHNContext.getContext().getMode() != Mode.STANDALONE)
						try {publisher.updateGCUBEResource(getInstance(), scope, getSecurityManager());} 
						catch (Exception e){logger.error("unable to publish the RI profile in scope " + scope, e);}
			} catch (Exception e) {logger.warn("could not update RI",e);}
		}		
		
    }
    
    /**
     * A {@link Consumer} for subscribing READY RIs to GHN-related events.
     * @author Fabio Simeoni (University of Strathclyde)
     *
     */
    class Subscriber extends Consumer {
        
    	@Override /**{@inheritDoc} */ 
		public RILifetimeTopic[] getTopics() {return new RILifetimeTopic[]{RILifetimeTopic.INITIALISED};}
		@Override /**{@inheritDoc} */
		protected void onRIInitialised(RILifetimeEvent event) throws Exception {
			Thread.currentThread().setName(service.getServiceName()+"-"+Thread.currentThread().getName());
			//register a consumer for GHN failure, tolerating failures because used for non critical cleanup
		    GHNConsumer consumer = new GHNConsumer() {
				protected void onGHNShutdown(GHNLifeTimeEvent event) {GCUBEServiceContext.this.setStatus(Status.DOWN);}
			};	    
			logger.info("registering for GHN shutdown events");
			GHNContext.getContext().subscribeGHNEvents(consumer,GHNTopic.SHUTDOWN);
			
			//register consumer for GHN scope events. 
			if (GCUBEServiceContext.this.getClass().getPackage().getName().startsWith(Restricted.GCORE)) {//for local services register a consumer for both removal and add events on GHN 
				logger.info("registering for GHN scope events as a local service");
				GHNContext.getContext().getGHN().subscribeResourceEvents(new LocalResourceConsumer(), GCUBEResource.ResourceTopic.ADDSCOPE, GCUBEResource.ResourceTopic.REMOVESCOPE);
			}
			else {//for the others register a consumer only for remove events
				logger.info("registering for GHN scope removal events");
				GHNContext.getContext().getGHN().subscribeResourceEvents(new BaseResourceConsumer(), GCUBEResource.ResourceTopic.REMOVESCOPE);
			}
			
		    LTEventProducer.unsubscribe(this, this.getTopics()); //disengage now
		}
		
		/** Internal extensions of {@link ResourceConsumer}.*/
		public class BaseResourceConsumer extends ResourceConsumer {
	     	protected void onRemoveScope(RemoveScopeEvent event) {
				Set<GCUBEScope> scopes = new HashSet<GCUBEScope>();
				for (GCUBEScope GHNScope : event.getPayload())
					for (GCUBEScope RIScope : GCUBEServiceContext.this.getInstance().getScopes().values())
						if (RIScope.isEnclosedIn(GHNScope)) scopes.add(RIScope);
				GCUBEServiceContext.this.removeScope(scopes.toArray(new GCUBEScope[0]));
	    	};	   
		}

		/** Internal extensions of {@link BaseResourceConsumer} specific to local services.*/
		public class LocalResourceConsumer extends BaseResourceConsumer {
	     	protected void onAddScope(AddScopeEvent event) {GCUBEServiceContext.this.addScope(event.getPayload());};	  
		}

    }
    
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////CALLBACKS ADAPTERS
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    
    /** Invoked when the Running Instance has completed initialisation. If needed, override in accordance with service semantics.
	 * @throws Exception if the callback did not complete successfully.*/
	protected void onInitialisation() throws Exception {}
	/** Invoked when the Running Instance is ready to operate. If needed, override in accordance with service semantics. 
	 * @throws Exception if the callback did not complete successfully.*/
	protected void onReady() throws Exception{} 
	/** Invoked when the Running Instance is going down. If needed, override in accordance with service semantics.
	 * @throws Exception if the callback did not complete successfully.*/
	protected void onShutdown() throws Exception{}
	/** Invoked when the Running Instance fails. If needed, override in accordance with service semantics.
	 * @throws Exception if the callback did not complete successfully.*/
	protected void onFailure() throws Exception{} 
	///** Invoked when the Running Instance is redeployed. If needed, override in accordance with service semantics.
	// * @throws Exception if the callback did not complete successfully.*/
	//protected void onRedeployment() throws Exception{} 
	/** Invoked when the Runnning Instance is updated. If needed, override in accordance with service semantics.
	 * @throws Exception if the callback did not complete successfully.*/
	protected void onUpdate() throws Exception{}; 
	/** Invoked upon a change to the RI's stateful resources, if any. If needed, override in accordance with service semantics.
	 * @throws Exception if the callback did not complete successfully.*/
	protected void onStateChange() throws Exception{}; 
	
}
