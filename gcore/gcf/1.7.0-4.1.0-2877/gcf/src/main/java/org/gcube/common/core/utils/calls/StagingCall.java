package org.gcube.common.core.utils.calls;

import java.util.List;

import org.apache.axis.message.addressing.EndpointReferenceType;
import org.gcube.common.core.contexts.GCUBEServiceContext;
import org.gcube.common.core.contexts.GHNContext;
import org.gcube.common.core.informationsystem.client.ISClient;
import org.gcube.common.core.informationsystem.client.RPDocument;
import org.gcube.common.core.informationsystem.client.queries.GCUBERIQuery;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.scope.GCUBEScopeManager;
import org.gcube.common.core.security.GCUBESecurityManager;
import org.gcube.common.core.utils.handlers.GCUBEServiceClientImpl;
import org.gcube.common.core.utils.handlers.GCUBEStagingServiceHandler;
import org.gcube.common.core.utils.handlers.GCUBEStatefulServiceHandler;
import org.gcube.common.scope.api.ScopeProvider;

/**
 * Partial specialisation of a {@link WSCall} to WS-Resources that subsumes 
 * the best-effort interaction strategy defined by a {@link GCUBEStagingServiceHandler}.
 * @author Fabio Simeoni (University of Strathclyde)
 *
 */
abstract public class StagingCall extends WSCall { 

	/** The query for factories. */
	private GCUBERIQuery factoryQuery;
	
	/**
	 * Creates an instance with a scope manager and a security manager.
	 * @param scopeManager the scope manager.
	 * @param securityManager the security manager.
	 * @throws Exception if the instance could not be created.
	 */
	public StagingCall(GCUBEScopeManager scopeManager,GCUBESecurityManager ... securityManager) throws Exception {
		super(scopeManager,securityManager);}

	/**
	 * Creates an instance with a scope and a security manager.
	 * @param scope the scope.
	 * @param securityManager the security manager.
	 * @throws Exception if the instance could not be created.
	 */
	public StagingCall(final GCUBEScope scope, GCUBESecurityManager ... securityManager) throws Exception {
		super(scope,securityManager);
		this.factoryQuery=this.getInitFactoryQuery();}
	
	/**
	 * Creates an instance with a service context.
	 * @param ctxt the context.
	 * @throws Exception if the instance could not be created.
	 */
	public StagingCall(GCUBEServiceContext ctxt) throws Exception {super(ctxt,ctxt);}

	/**Returns the name of the factory port-type.
	 * @return the name.*/
	public abstract String getFactoryPortTypeName();

	/**
	 * Returns the initial query for the best-effort strategy for factories.
	 * @return the query.
	 * @throws Exception if the query could not be returned (typically because it could not be built).
	 */
	protected GCUBERIQuery getInitFactoryQuery() throws Exception {return getRIQuery();}
	
	/**
	 * Abstract specialisation of {@link GCUBEStatefulServiceHandler} for embedding in {@link StagingCall}s.
	 * @author Fabio Simeoni (University of Strathclyde)
	 */
	public abstract class StagingHandler extends GCUBEStagingServiceHandler<GCUBEServiceClientImpl>  {
		
		/** Creates a new instance.*/
		public StagingHandler() {super(clients.get(StagingCall.this.getClass()));this.setLogger(StagingCall.this.getLogger());}
		/**{@inheritDoc}*/
		protected void _interact(EndpointReferenceType epr) throws Exception {super._interact(epr);setEndpointReference(epr);}
		/**{@inheritDoc}*/
		public void run() throws Exception {if (getEndpointReference()==null) super.run(); else interact(getEndpointReference());}
		/**{@inheritDoc}*/
		protected String getTargetPortTypeName() {return getPortTypeName();}
		/**{@inheritDoc}*/
		protected List<RPDocument> findWSResources() throws Exception {
			return GHNContext.getImplementation(ISClient.class).execute(getQuery(),GCUBEScope.getScope(ScopeProvider.instance.get()));
		}
		/**{@inheritDoc}*/
		@Override public GCUBEScopeManager getScopeManager() {return StagingCall.this.getScopeManager();}
		
		/**{@inheritDoc}*/
		protected List<EndpointReferenceType> findFactories() throws Exception {return findPortType(factoryQuery, getFactoryPortTypeName());}
		
	}
	
}
