package org.gcube.common.core.utils.calls;

import java.util.List;

import org.apache.axis.message.addressing.AttributedURI;
import org.apache.axis.message.addressing.EndpointReferenceType;
import org.gcube.common.core.contexts.GCUBEServiceContext;
import org.gcube.common.core.informationsystem.client.queries.GCUBERIQuery;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.scope.GCUBEScopeManager;
import org.gcube.common.core.security.GCUBESecurityManager;
import org.gcube.common.core.utils.handlers.GCUBEServiceClientImpl;
import org.gcube.common.core.utils.handlers.GCUBEServiceHandler;

/**
 * Partial specialisation of a {@link GCUBECall} for plain port-types discovered with a {@link GCUBERIQuery}.
 * @author Fabio Simeoni (University of Strathclyde)
 *
 */
abstract public class RICall extends GCUBECall<GCUBERIQuery> { 

	/**
	 * Creates an instance with a scope manager and a security manager.
	 * @param scopeManager the scope manager.
	 * @param securityManager the security manager.
	 * @throws Exception if the instance could not be created.
	 */
	public RICall(GCUBEScopeManager scopeManager,GCUBESecurityManager ... securityManager) throws Exception {
		super(scopeManager,securityManager);}

	/**
	 * Creates an instance with a scope and a security manager.
	 * @param scope the scope.
	 * @param securityManager the security manager.
	 * @throws Exception if the instance could not be created.
	 */
	public RICall(final GCUBEScope scope, GCUBESecurityManager ... securityManager) throws Exception {
		super(scope,securityManager);}
	
	/**
	 * Creates an instance with a service context.
	 * @param ctxt the context.
	 * @throws Exception if the instance could not be created.
	 */
	public RICall(GCUBEServiceContext ctxt) throws Exception {super(ctxt,ctxt);}

	/**{@inheritDoc}*/
	protected GCUBERIQuery getInitQuery() throws Exception {return getRIQuery();}
	
	/**
	 * Convenience method to set the target endpoint from host name and port.  
	 * @param hostname the host name.
	 * @param port the port.
	 * @throws Exception if a reference to the endpoint could not be built.
	 */
	public void setEndpoint(String hostname,String port) throws Exception {
		setEndpointReference(new EndpointReferenceType(new AttributedURI("http://"+hostname+":"+port+"/wsrf/services/"+getPortTypeName())));
	}
	
	/**
	 * Abstract specialisation of {@link GCUBEServiceHandler} for embedding in {@link RICall}s.
	 * @author Fabio Simeoni (University of Strathclyde)
	 */
	public abstract class RICallHandler extends GCUBEServiceHandler<GCUBEServiceClientImpl>  {
		
		/** Creates a new instance.*/
		public RICallHandler() {
			super(clients.get(RICall.this.getClass()));
			this.setLogger(RICall.this.getLogger());
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
		protected List<EndpointReferenceType> findInstances() throws Exception {
			return findPortType(getQuery(),getPortTypeName());
		}
		
		/**{@inheritDoc}*/
		@Override public GCUBEScopeManager getScopeManager() {return RICall.this.getScopeManager();}
		
		/**{@inheritDoc}*/
		protected String getTargetPortTypeName() {return getPortTypeName();}
		
	}
	
}
