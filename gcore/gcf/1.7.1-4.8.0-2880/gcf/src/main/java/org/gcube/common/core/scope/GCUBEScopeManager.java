package org.gcube.common.core.scope;

import java.rmi.Remote;

import org.gcube.common.core.contexts.GCUBERemotePortTypeContext;
import org.gcube.common.scope.api.ScopeProvider;

/**
 * The interface of managers of scope information within service implementations.
 * @author Fabio Simeoni (University of Strathclyde)
 * @deprecate as to 1.6.0, use {@link ScopeProvider} instead.
 */
@Deprecated
public interface GCUBEScopeManager {

	/**
	 * Shared {@link GCUBEScopeManager}
	 * @deprecated as to 1.6.0, use {@link ScopeProvider#instance} instead to manage the current scope.
	 */
	@Deprecated
	public static final GCUBEScopeManager DEFAULT = new GCUBEScopeManagerImpl();
	
	/** Name of the service class call header. */
	public static final String CLASS_HEADER_NAME="serviceClass";
	/** Name of the service name call header. */
	public static final String NAME_HEADER_NAME="serviceName";
	/** Name of the scope call header. */
	public static final String SCOPE_HEADER_NAME="scope";
	/** Namespace of scope-related headers */
	public static final String SCOPE_NS="http://gcube-system.org/namespaces/scope";

	/*Models a scope which is not valid with respect to the context of usage. */
	public static class IllegalScopeException extends RuntimeException {
		static final long serialVersionUID = 1L;
		public IllegalScopeException() {
			super();
		}
		public IllegalScopeException(String msg) {
			super(msg);
		}
	}
	/**
	 * Sets the scope of outgoing calls in the current thread.
	 * @param scope the scope.
	 * @deprecated as to 1.6.0, use {@link ScopeProvider#instance#set(String)} instead
	 */
	public void setScope(GCUBEScope scope) throws IllegalScopeException;
		
	/**
	 * Sets the scope of outgoing calls in a given thread.
	 * @param thread the thread.
	 * @param scope (optional) the scope. If omitted, it defaults to the scope of the current thread.
	 * @deprecated as to 1.6.0 made superfluous by {@link ScopeProvider#instance}
	 */
	@Deprecated
	public void setScope(Thread thread, GCUBEScope ... scope);

	/**
	 * Gets the scope for outgoing calls in the current thread.
	 * @return the scope;
	 * @deprecated as to 1.6.0, use {@link ScopeProvider#instance#get()} instead
	 */
	@Deprecated
	public GCUBEScope getScope();

	/**
	 * Sets the scope for an outgoing call to a target gCube service.
	 * @param remote the stub of the target port-type.
	 * @param clazz the gCube class of the target service.
	 * @param name the gCube name of the target service.
	 * 
	 * @deprecated as to 1.6.0 calls are prepared internally to {@link GCUBERemotePortTypeContext}s. 
	 * Use {@link GCUBERemotePortTypeContext#getProxy(Remote, org.gcube.common.core.security.GCUBESecurityManager...)}
	 * {@link GCUBERemotePortTypeContext#getProxy(Remote, int org.gcube.common.core.security.GCUBESecurityManager...)}
	 * for calls from a client context. Use {@link GCUBERemotePortTypeContext#getProxy(Remote, org.gcube.common.core.contexts.GCUBEServiceContext)}
	 * or {@link GCUBERemotePortTypeContext#getProxy(Remote, org.gcube.common.core.contexts.GCUBEServiceContext, int))} for
	 * calls from a service context.
	 */
	@Deprecated
	public void prepareCall(Remote remote, String clazz, String name, GCUBEScope ... scope);

}