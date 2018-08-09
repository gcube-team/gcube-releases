package org.gcube.data.analysis.tabulardata.operation.worker.results.resources;

import org.gcube.data.analysis.tabulardata.model.resources.Resource;
import org.gcube.data.analysis.tabulardata.model.resources.ResourceScope;
import org.gcube.data.analysis.tabulardata.model.resources.ResourceType;
import org.gcube.data.analysis.tabulardata.model.resources.SDMXResource;


public class ImmutableSDMXResource implements ResourceDescriptorResult{


	/**
	 * 
	 */
	private static final long serialVersionUID = 1671357769331845989L;
	private SDMXResource sdmxResource;
	private String name;
	private String description;
	private ResourceScope resourceScope = ResourceScope.GLOBAL;
	private ResourceType type;
	
	public ImmutableSDMXResource(SDMXResource sdmxResource, String name, 
			String description , ResourceType type) {
		super();
		this.sdmxResource = sdmxResource;
		this.description = description;
		this.name = name;
		this.type = type;
	}
	
	public ImmutableSDMXResource(SDMXResource sdmxResource, String name, String description, ResourceType type,
			ResourceScope resourceScope) {
		super();
		this.sdmxResource = sdmxResource;
		this.description = description;
		this.resourceScope = resourceScope;
		this.name = name;
	}

	@Override
	public Resource getResource() {
		return sdmxResource;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public ResourceScope getResourceScope() {
		return resourceScope;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public ResourceType getResourceType() {
		return type;
	}
		
}
