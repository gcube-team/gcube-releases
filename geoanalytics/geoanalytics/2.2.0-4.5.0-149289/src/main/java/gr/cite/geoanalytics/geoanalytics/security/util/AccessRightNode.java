package gr.cite.geoanalytics.geoanalytics.security.util;

import java.util.UUID;

public abstract class AccessRightNode  {
	
	private UUID id = null;
	private String name = null;
	private String description = null;
	private AccessRightNode parent = null;
	
	public UUID getId() {
		return id;
	}
	
	public void setId(UUID id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}

	public AccessRightNode getParent() {
		return parent;
	}
	
	public void setParent(AccessRightNode parent) {
		this.parent = parent;
	}
	
	public abstract boolean isLeaf();
}
