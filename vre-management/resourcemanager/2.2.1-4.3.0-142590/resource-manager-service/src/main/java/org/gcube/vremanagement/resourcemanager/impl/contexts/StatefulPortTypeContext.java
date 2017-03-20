package org.gcube.vremanagement.resourcemanager.impl.contexts;

import org.gcube.common.core.contexts.GCUBEServiceContext;
import org.gcube.common.core.contexts.GCUBEStatefulPortTypeContext;

/**
 *  
 * @author Manuele Simi (ISTI-CNR)
 *
 */
public class StatefulPortTypeContext extends GCUBEStatefulPortTypeContext {

	/** Singleton resource name*/
	public static final String SINGLETON_RESOURCE_KEY = "ManagerState";

	static StatefulPortTypeContext cache = new StatefulPortTypeContext();
	
	@Override
	public String getJNDIName() {
		return "gcube/vremanagement/ResourceManager";
	}
	
	@Override
	public String getNamespace() {
		return "http://gcube-system.org/namespaces/vremanagement/resourcemanager";
	}

	@Override
	public GCUBEServiceContext getServiceContext() {
		return ServiceContext.getContext();
	}		

	/**
	 * 
	 * @return the stateful context
	 */
	public static GCUBEStatefulPortTypeContext getContext() {
		return cache;
	}		
	
	
}

