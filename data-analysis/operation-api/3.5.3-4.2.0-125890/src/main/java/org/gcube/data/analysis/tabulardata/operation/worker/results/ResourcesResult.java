package org.gcube.data.analysis.tabulardata.operation.worker.results;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import org.gcube.data.analysis.tabulardata.operation.worker.results.resources.ResourceDescriptorResult;

public class ResourcesResult implements Result, Serializable {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2211074862983670945L;

	private List<ResourceDescriptorResult> resources;
	
	public ResourcesResult(List<ResourceDescriptorResult> resources) {
		this.resources = resources;
	}
	
	public ResourcesResult(ResourceDescriptorResult resource) {
		this.resources = Collections.singletonList(resource);
	}
	
	public List<ResourceDescriptorResult> getResources(){
		return resources;
	}

	@Override
	public String toString() {
		return "ResourcesResult [resources=" + resources + "]";
	}
		
}
