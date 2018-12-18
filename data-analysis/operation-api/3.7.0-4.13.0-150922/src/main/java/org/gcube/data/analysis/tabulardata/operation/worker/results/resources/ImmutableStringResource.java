package org.gcube.data.analysis.tabulardata.operation.worker.results.resources;

import org.gcube.data.analysis.tabulardata.model.resources.StringResource;
import org.gcube.data.analysis.tabulardata.model.resources.Resource;
import org.gcube.data.analysis.tabulardata.model.resources.ResourceScope;
import org.gcube.data.analysis.tabulardata.model.resources.ResourceType;


public class ImmutableStringResource implements ResourceDescriptorResult{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2378033798749961208L;

	private StringResource stringRes;
	private String name;
	private String description;
	private ResourceScope resourceScope = ResourceScope.LOCAL;
	private ResourceType type;
	
	public ImmutableStringResource(StringResource json, String name, 
			String description , ResourceType type) {
		super();
		this.stringRes = json;
		this.description = description;
		this.name = name;
		this.type = type;
	}
	
	public ImmutableStringResource(StringResource stringRes, String name, String description, ResourceType type,
			ResourceScope resourceScope) {
		super();
		this.stringRes = stringRes;
		this.description = description;
		this.resourceScope = resourceScope;
		this.name = name;
	}

	@Override
	public Resource getResource() {
		return stringRes;
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
