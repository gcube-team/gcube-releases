package org.gcube.vremanagement.resourcemanager.impl.resources;

import org.gcube.common.core.resources.GCUBEHostingNode;
import org.gcube.common.core.resources.GCUBERunningInstance;
import org.gcube.common.core.resources.GCUBEService;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.utils.logging.GCUBELog;

/**
 * Factory for creating {@link ScopedResource}s
 * @author manuele simi (CNR)
 *
 */
public class ScopedResourceFactory {
	
	protected static GCUBELog logger = new GCUBELog(ScopedResourceFactory.class);
	
	protected ScopedResourceFactory() {}
	
	/**
	 * Creates a new {@link ScopedResource}
	 * 
	 * @param id the gCube Resource identifier
	 * @param type the resource type
	 * @param scope the {@link GCUBEScope} assigned to the resource
	 * @return the newly created resource
	 */
	public static ScopedResource newResource(GCUBEScope scope, String id, String type) throws Exception {
		
		if ((id == null) || (id.compareTo("") == 0))
			throw new IllegalArgumentException("invalid resource id specified");
		
		if ((type == null) || (type.compareTo("") == 0))
				throw new IllegalArgumentException("invalid resource type specified");
		
		ScopedResource sresource = null;		
		logger.trace("Creating a new resource " + id + "");
		if (type.compareToIgnoreCase(GCUBEHostingNode.TYPE) == 0) 
			sresource = new ScopedGHN(id, scope);								
		 else if (type.compareToIgnoreCase(GCUBERunningInstance.TYPE) == 0) 
			sresource = new ScopedRunningInstance(id, scope);
		 else if (type.compareToIgnoreCase(GCUBEService.TYPE) == 0) 
			sresource = new ScopedDeployedSoftware(id, scope);
		 else		
			sresource = new ScopedAnyResource(id, type, scope);
		
		return sresource;
	}
	
}
