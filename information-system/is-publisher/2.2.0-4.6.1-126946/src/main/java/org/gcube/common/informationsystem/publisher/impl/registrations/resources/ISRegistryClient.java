package org.gcube.common.informationsystem.publisher.impl.registrations.resources;

import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.scope.GCUBEScopeManager;
import org.gcube.common.core.scope.GCUBEScopeManagerImpl;
import org.gcube.common.core.utils.handlers.GCUBEServiceClientImpl;


/**
 * State for {@link ISRegistryServiceHandler} 
 * 
 * @author Manuele Simi (ISTI-CNR)
 *
 */
public class ISRegistryClient extends GCUBEServiceClientImpl {
	

	GCUBEScopeManager manager =  new GCUBEScopeManagerImpl();
	
	//we can use a single instance of this, since the map handled by the GCUBEScopeManagerImpl is indexed by thread ID
	//therefore concurrent uses do not clash
	static ISRegistryClient singleton = new ISRegistryClient();
	
	private ISRegistryClient() {}
	
	/**
	 * 
	 * @return the ISRegistryClient
	 */
	public static ISRegistryClient getISRegistryClient() {
		return singleton;
	}
	
	/**
	 * Gets the current scope
	 * @return the scope
	 */
	@Override
	public GCUBEScope getScope() {
		return manager.getScope();
	}

	/**
	 * Sets the current scope
	 * @param scope the scope to set
	 */
	public void setScope(GCUBEScope scope) {
		manager.setScope(scope);
	}

	
}
