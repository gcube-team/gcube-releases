package org.gcube.data.analysis.tabulardata.operation.worker.results.resources;

import org.gcube.data.analysis.tabulardata.model.resources.Resource;
import org.gcube.data.analysis.tabulardata.model.resources.ResourceScope;
import org.gcube.data.analysis.tabulardata.model.resources.ResourceType;
import org.gcube.data.analysis.tabulardata.model.resources.TableResource;


public class ImmutableTableResource implements ResourceDescriptorResult{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3369581598451116331L;

	private TableResource table;
	private String description;
	private String name;
	private ResourceScope resourceScope = ResourceScope.LOCAL;

	private ResourceType type;

	public ImmutableTableResource(TableResource table, String name,
			String description, ResourceType type) {
		this.table = table;
		this.name = name;
		this.description = description;
		this.type = type;
	}

	public ImmutableTableResource(TableResource table, String name, String description, ResourceType type,
			ResourceScope resourceScope) {
		this(table, name, description, type);
		this.resourceScope = resourceScope;
	}

	@Override
	public Resource getResource() {
		return table;
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
