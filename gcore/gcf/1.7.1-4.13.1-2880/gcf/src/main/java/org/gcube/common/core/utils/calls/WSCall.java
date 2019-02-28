package org.gcube.common.core.utils.calls;

import java.util.ArrayList;
import java.util.List;

import org.apache.axis.message.addressing.EndpointReferenceType;
import org.gcube.common.core.contexts.GCUBEServiceContext;
import org.gcube.common.core.contexts.GHNContext;
import org.gcube.common.core.informationsystem.client.AtomicCondition;
import org.gcube.common.core.informationsystem.client.ISClient;
import org.gcube.common.core.informationsystem.client.RPDocument;
import org.gcube.common.core.informationsystem.client.queries.WSResourceQuery;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.scope.GCUBEScopeManager;
import org.gcube.common.core.security.GCUBESecurityManager;
import org.gcube.common.core.utils.handlers.GCUBEServiceClientImpl;
import org.gcube.common.core.utils.handlers.GCUBEStatefulServiceHandler;
import org.gcube.common.scope.api.ScopeProvider;

/**
 * Partial specialisation of a {@link GCUBECall} to WS-Resources that subsumes 
 * the best-effort interaction strategy defined by a {@link GCUBEStatefulServiceHandler}.
 * @author Fabio Simeoni (University of Strathclyde)
 *
 */
abstract public class WSCall extends GCUBECall<WSResourceQuery> { 

	/**
	 * Creates an instance with a scope manager and a security manager.
	 * @param scopeManager the scope manager.
	 * @param securityManager the security manager.
	 * @throws Exception if the instance could not be created.
	 */
	public WSCall(GCUBEScopeManager scopeManager,GCUBESecurityManager ... securityManager) throws Exception {
		super(scopeManager,securityManager);}

	/**
	 * Creates an instance with a scope and a security manager.
	 * @param scope the scope.
	 * @param securityManager the security manager.
	 * @throws Exception if the instance could not be created.
	 */
	public WSCall(final GCUBEScope scope, GCUBESecurityManager ... securityManager) throws Exception {
		super(scope,securityManager);}
	
	/**
	 * Creates an instance with a service context.
	 * @param ctxt the context.
	 * @throws Exception if the instance could not be created.
	 */
	public WSCall(GCUBEServiceContext ctxt) throws Exception {super(ctxt,ctxt);}


	/**{@inheritDoc}*/
	protected WSResourceQuery getInitQuery() throws Exception {
		WSResourceQuery q = GHNContext.getImplementation(ISClient.class).getQuery(WSResourceQuery.class);
		q.addAtomicConditions(
				new AtomicCondition("/gc:ServiceName",getServiceName()),
				new AtomicCondition("/gc:ServiceClass",getServiceClass()));
		return q;
	}	
	
	/**
	 * Abstract specialisation of {@link GCUBEStatefulServiceHandler} for embedding in {@link WSCall}s.
	 * @author Fabio Simeoni (University of Strathclyde)
	 */
	public abstract class WSCallHandler extends GCUBEStatefulServiceHandler<GCUBEServiceClientImpl>  {

		
		/** Creates a new instance.*/
		public WSCallHandler() {
			super(clients.get(WSCall.this.getClass()));this.setLogger(WSCall.this.getLogger());
		}
		
		/**{@inheritDoc}*/
		protected void _interact(EndpointReferenceType epr) throws Exception {
			super._interact(epr);
			setEndpointReference(epr);
		}
		
		/**{@inheritDoc}*/
		protected EndpointReferenceType getCachedEPR() {
			return getEndpointReference()==null?super.getCachedEPR():getEndpointReference();
		}
		
		/**{@inheritDoc}*/
		protected String getTargetPortTypeName() {return getPortTypeName();}
		
		/**{@inheritDoc}*/
		@Override public GCUBEScopeManager getScopeManager() {return WSCall.this.getScopeManager();}
		
		/**{@inheritDoc}*/
		protected List<RPDocument> findWSResources() throws Exception {
			List<RPDocument> documents = new ArrayList<RPDocument>();
			//filter results by port-type name in case there are multiple ws-resource types per service
			for (RPDocument d : GHNContext.getImplementation(ISClient.class).execute(getQuery(),getScopeManager().getScope()))
				if (d.getEndpoint().getAddress().getPath().endsWith(WSCall.this.getPortTypeName())) documents.add(d);
			return documents;
		}
		
	}

	
}
