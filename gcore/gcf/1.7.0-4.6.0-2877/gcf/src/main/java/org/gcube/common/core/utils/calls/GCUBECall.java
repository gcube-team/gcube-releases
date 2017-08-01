package org.gcube.common.core.utils.calls;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.axis.message.addressing.EndpointReferenceType;
import org.gcube.common.core.contexts.GCUBEServiceContext;
import org.gcube.common.core.contexts.GHNContext;
import org.gcube.common.core.informationsystem.client.AtomicCondition;
import org.gcube.common.core.informationsystem.client.ISClient;
import org.gcube.common.core.informationsystem.client.ISQuery;
import org.gcube.common.core.informationsystem.client.queries.GCUBERIQuery;
import org.gcube.common.core.resources.GCUBERunningInstance;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.scope.GCUBEScopeManager;
import org.gcube.common.core.scope.GCUBEScopeManagerImpl;
import org.gcube.common.core.security.GCUBESecurityManager;
import org.gcube.common.core.utils.handlers.GCUBEServiceClientImpl;
import org.gcube.common.core.utils.handlers.GCUBEServiceHandler;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.common.scope.api.ScopeProvider;

/**
 * Partial implementation for calls to port-types of gCube services.
 * <p>
 * The call subsumes the best-effort interaction strategy of a {@link GCUBEServiceHandler}. 
 * Equally, it may target a port-type at a known endpoint.
 * Call scope <em>must</em> be specified at creation time, with a {@link GCUBEScope} or via a {@link GCUBEScopeManager}.
 * Security <em>may</em> be specified at creation via a {@link GCUBESecurityManager}.
 * 
 *@param QUERY the type of the query used for the best-effort strategy.
 *@author Fabio Simeoni (University of Strathclyde)
 */
public abstract class GCUBECall<QUERY extends ISQuery<?>> { 

	protected static Map<Class<?>,GCUBEServiceClientImpl> clients = Collections.synchronizedMap(new HashMap<Class<?>,GCUBEServiceClientImpl>()); 
	
	/**
	 * Convenience holder for exporting invocation results from within anonymous specialisation of 
	 * @author Fabio Simeoni (University of Strathclyde)
	 *
	 * @param <RESULT> the type of results.
	 */
	public static class ResultHolder<RESULT>{public RESULT value;}
	
	/**Object logger.*/
	protected GCUBELog logger = new GCUBELog(this);
	
	/** The scope manager. */
	private GCUBEScopeManager scopeManager;
	
	/** The security manager. */
	private GCUBESecurityManager securityManager;
	
	/** The query. */
	private QUERY query;
	
	/** The port-type's endpoint.**/
	private EndpointReferenceType epr;
	
	/**
	 * Creates an instance with a scope manager and a security manager.
	 * @param scopeManager the scope manager.
	 * @param securityManager the security manager.
	 * @throws Exception if the instance could not be created.
	 */
	public GCUBECall(GCUBEScopeManager scopeManager,GCUBESecurityManager ... securityManager) throws Exception {
		this.scopeManager=scopeManager;
		if (securityManager!=null && securityManager.length>0) this.securityManager=securityManager[0];
		this.setQuery(getInitQuery());
		if (!clients.containsKey(this.getClass())) clients.put(this.getClass(), 
				new GCUBEServiceClientImpl() {public GCUBEScope getScope(){
					return GCUBEScope.getScope(ScopeProvider.instance.get());}});
	}

	/**
	 * Creates an instance with a scope and a security manager.
	 * @param scope the scope.
	 * @param securityManager the security manager.
	 * @throws Exception if the instance could not be created.
	 */
	public GCUBECall(final GCUBEScope scope, GCUBESecurityManager ... securityManager) throws Exception {
		this(new GCUBEScopeManagerImpl() {public GCUBEScope getScope() {return scope;}},securityManager);
	}
	
	/**
	 * Creates an instance with a service context.
	 * @param ctxt the context.
	 * @throws Exception if the instance could not be created.
	 */
	public GCUBECall(GCUBEServiceContext ctxt) throws Exception {this(ctxt,ctxt);}
	
	/**Sets the call's logger.
	 * @param logger the logger.*/
	public void setLogger(GCUBELog logger) {this.logger=logger;}
	
	/**
	 * Returns the call's logger.
	 * @return the logger.
	 */
	public GCUBELog getLogger() {return this.logger;}

	/**Returns the name of the target port-type.
	 * @return the name.*/
	abstract protected String getPortTypeName();
	/**
	 * Returns the name of the service of the target port-type.
	 * @return the name.
	 */
	abstract protected String getServiceName();
	/**
	 * Returns the class of the service of the target port-type.
	 * @return the name.
	 */
	abstract protected String getServiceClass();
	
	/**
	 * Returns the security manager used for the call.
	 * @return the security manager.
	 */
	public GCUBESecurityManager getSecurityManager() {return this.securityManager;}
	/**
	 * Sets the security manager used for the call.
	 * @param securityManager the security manager.
	 */
	public void setSecurityManager(GCUBESecurityManager securityManager) {this.securityManager=securityManager;}
	/**
	 * Returns the scope manager used for the call.
	 * @return the scope manager.
	 */
	public GCUBEScopeManager getScopeManager() {return this.scopeManager;}
	/**
	 * Sets the scope manager used for the call.
	 * @param scopeManager the scope manager.
	 */
	public void setScopeManager(GCUBEScopeManager scopeManager) {this.scopeManager=scopeManager;}
	/**
	 * Set the query for the best-effort strategy.
	 * @param query the query.
	 */
	public void setQuery(QUERY query) {this.query=query;}
	/**
	 * Returns the query for the best-effort strategy.
	 * @return the query.
	 */
	public QUERY getQuery() {return this.query;}	
	/**
	 * Returns the initial query for the best-effort strategy.
	 * @return the query.
	 * @throws Exception if the query could not be returned (typically because it could not be built).
	 */
	protected abstract QUERY getInitQuery() throws Exception;	

	/**
	 * Sets the target endpoint.
	 * @param epr a reference to the endpoint.
	 */
	public void setEndpointReference(EndpointReferenceType epr) {this.epr=epr;}
	/**
	 * Returns the target endpoint.
	 * @return a reference to the endpoint.
	 */
	public EndpointReferenceType getEndpointReference(){return this.epr;}
	
	/**
	 * Helper method that returns a query for Running Instances of the service of the target port-type. 
	 * @return the query.
	 * @throws Exception if the query could not returned.
	 */
	protected GCUBERIQuery getRIQuery() throws Exception {
		GCUBERIQuery q = GHNContext.getImplementation(ISClient.class).getQuery(GCUBERIQuery.class);
		q.addAtomicConditions(
				new AtomicCondition("//ServiceName",getServiceName()),
				new AtomicCondition("//ServiceClass",getServiceClass()));
		return q;
	}

	/**
	 * Helper method that executes a given query to return a list of references to endpoints of a given port-type. 
	 * @param query the query.
	 * @param portType the port-type name.
	 * @return the references.
	 * @throws Exception if the query could not returned.
	 */
	protected List<EndpointReferenceType> findPortType(GCUBERIQuery query,String portType) throws Exception {
		ISClient client = GHNContext.getImplementation(ISClient.class);
		List<EndpointReferenceType> eprs = new ArrayList<EndpointReferenceType>();
		for (GCUBERunningInstance instance : client.execute(query,getScopeManager().getScope())) 
			if (instance.getAccessPoint().getEndpoint(portType)!=null)
				eprs.add(instance.getAccessPoint().getEndpoint(portType));
		return eprs;
	}
	
}
