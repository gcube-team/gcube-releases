package org.gcube.data.analysis.tabulardata.operation.worker.results.resources;

import java.io.Serializable;

import org.gcube.data.analysis.tabulardata.model.resources.Resource;
import org.gcube.data.analysis.tabulardata.model.resources.ResourceScope;
import org.gcube.data.analysis.tabulardata.model.resources.ResourceType;

public interface ResourceDescriptorResult extends Serializable{
	
	Resource getResource();
	
	String getName();
	
	String getDescription();
		
	ResourceScope getResourceScope();
	
	ResourceType getResourceType();
}
