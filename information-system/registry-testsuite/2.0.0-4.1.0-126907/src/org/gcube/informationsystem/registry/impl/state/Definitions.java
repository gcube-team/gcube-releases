package org.gcube.informationsystem.registry.impl.state;

import org.gcube.common.core.contexts.GHNContext;
import org.gcube.common.core.resources.GCUBECS;
import org.gcube.common.core.resources.GCUBECSInstance;
import org.gcube.common.core.resources.GCUBECollection;
import org.gcube.common.core.resources.GCUBEExternalRunningInstance;
import org.gcube.common.core.resources.GCUBEGenericResource;
import org.gcube.common.core.resources.GCUBEHostingNode;
import org.gcube.common.core.resources.GCUBEMCollection;
import org.gcube.common.core.resources.GCUBEResource;
import org.gcube.common.core.resources.GCUBERunningInstance;
import org.gcube.common.core.resources.GCUBEService;

/**
* 
* Collection of definitions
*
*/


public class Definitions {
	
	
	/** The Resource Type */
	public static enum ResourceType {
		RunningInstance() {public GCUBEResource getResourceClass() throws Exception {return GHNContext.getImplementation(GCUBERunningInstance.class);}},
		ExternalRunningInstance() {public GCUBEResource getResourceClass() throws Exception {return GHNContext.getImplementation(GCUBEExternalRunningInstance.class);}},
		Service() {public GCUBEResource getResourceClass() throws Exception {return  GHNContext.getImplementation(GCUBEService.class);}},
		Collection() {public GCUBEResource getResourceClass() throws Exception {return  GHNContext.getImplementation(GCUBECollection.class);}},
		CS() {public GCUBEResource getResourceClass() throws Exception {return GHNContext.getImplementation(GCUBECS.class);}},
		CSInstance() {public GCUBEResource getResourceClass() throws Exception {return GHNContext.getImplementation(GCUBECSInstance.class);}},
		GHN() {public GCUBEResource getResourceClass() throws Exception {return GHNContext.getImplementation(GCUBEHostingNode.class);}},
		gLiteSE() {public GCUBEResource getResourceClass() throws Exception {return null;}},
		gLiteCE() {public GCUBEResource getResourceClass() throws Exception {return null;}},
		gLiteSite() {public GCUBEResource getResourceClass()throws Exception {return null;}},
		gLiteService() {public GCUBEResource getResourceClass() throws Exception {return null;}},		
		GenericResource() {public GCUBEResource getResourceClass() throws Exception {return GHNContext.getImplementation(GCUBEGenericResource.class);}},		
		MetadataCollection() {public GCUBEResource getResourceClass() throws Exception {return GHNContext.getImplementation(GCUBEMCollection.class);}};
		
		abstract public GCUBEResource getResourceClass() throws Exception;
		
		
	};
	
	public static enum OperationType {
		create, update, destroy
	};

}

	 
	 
