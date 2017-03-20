package org.gcube.data.analysis.tabulardata.operation.worker.results.resources;

import org.gcube.data.analysis.tabulardata.model.resources.InternalURI;
import org.gcube.data.analysis.tabulardata.model.resources.Resource;
import org.gcube.data.analysis.tabulardata.model.resources.ResourceScope;
import org.gcube.data.analysis.tabulardata.model.resources.ResourceType;

public class ImmutableURIResult implements ResourceDescriptorResult {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4754240010695558432L;

	private InternalURI uri;
	
	private ResourceScope resourceScope = ResourceScope.LOCAL;
	
	private String description;
	private String name;
	private ResourceType type;
		
	public ImmutableURIResult(InternalURI uri, String name,  String description, ResourceType type) {
		this.uri = uri;
		this.description = description;
		this.name = name;
		this.type = type;
	}
	
	public ImmutableURIResult(InternalURI uri, String description, String name, ResourceType type, ResourceScope resourceScope) {
		this(uri, name,  description, type);
		this.resourceScope = resourceScope;
	}

	@Override
	public Resource getResource() {
		return uri;
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
	public String toString() {
		return "ImmutableResourceDescriptorResult [uri=" + uri
				+ ", description=" + description + "]";
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
