package org.gcube.informationsystem.registry.impl.state;

import org.gcube.common.core.contexts.GHNContext;
import org.gcube.common.core.resources.GCUBECollection;
import org.gcube.common.core.resources.GCUBEExternalRunningInstance;
import org.gcube.common.core.resources.GCUBEGenericResource;
import org.gcube.common.core.resources.GCUBEHostingNode;
import org.gcube.common.core.resources.GCUBEMCollection;
import org.gcube.common.core.resources.GCUBEResource;
import org.gcube.common.core.resources.GCUBERunningInstance;
import org.gcube.common.core.resources.GCUBERuntimeResource;
import org.gcube.common.core.resources.GCUBEService;

/**
* 
* Collection of definitions
*
*/


public class Definitions {
	
	
	/** The Resource Types */
	public static enum ResourceMappings {
		RuntimeResource() {public GCUBEResource getResourceImplementation() throws Exception {return GHNContext.getImplementation(GCUBERuntimeResource.class);}},
		RunningInstance() {public GCUBEResource getResourceImplementation() throws Exception {return GHNContext.getImplementation(GCUBERunningInstance.class);}},
		ExternalRunningInstance() {public GCUBEResource getResourceImplementation() throws Exception {return GHNContext.getImplementation(GCUBEExternalRunningInstance.class);}},
		Service() {public GCUBEResource getResourceImplementation() throws Exception {return  GHNContext.getImplementation(GCUBEService.class);}},
		Collection() {public GCUBEResource getResourceImplementation() throws Exception {return  GHNContext.getImplementation(GCUBECollection.class);}},
		GHN() {public GCUBEResource getResourceImplementation() throws Exception {return GHNContext.getImplementation(GCUBEHostingNode.class);}},
		gLiteSE() {public GCUBEResource getResourceImplementation() throws Exception {return null;}},
		gLiteCE() {public GCUBEResource getResourceImplementation() throws Exception {return null;}},
		gLiteSite() {public GCUBEResource getResourceImplementation()throws Exception {return null;}},
		gLiteService() {public GCUBEResource getResourceImplementation() throws Exception {return null;}},		
		GenericResource() {public GCUBEResource getResourceImplementation() throws Exception {return GHNContext.getImplementation(GCUBEGenericResource.class);}},	
		MetadataCollection() {public GCUBEResource getResourceImplementation() throws Exception {return GHNContext.getImplementation(GCUBEMCollection.class);}};
		
		abstract public GCUBEResource getResourceImplementation() throws Exception;
		
		
	};
	
	public static enum OperationType {
		create, update, destroy
	};

}

	 
	 
