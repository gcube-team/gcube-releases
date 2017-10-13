package org.gcube.data.analysis.tabulardata.utils;

import org.gcube.data.analysis.tabulardata.model.resources.TableResource;

public class ResourceCreated {

	private TableResource resource;
	private String name;
	private String owner;
	
	public ResourceCreated(TableResource resource, String name, String owner) {
		super();
		this.resource = resource;
		this.name = name;
		this.owner = owner;
	}

	public TableResource getResource() {
		return resource;
	}

	public String getName() {
		return name;
	}

	public String getOwner() {
		return owner;
	}
		
}
