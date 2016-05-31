package org.gcube.data.harmonization.occurrence.impl.model;

import org.gcube.data.harmonization.occurrence.impl.model.types.ResourceType;

public class Resource {

	private Operation operation;
	//Resource
	private String id;
	private String resourceDescription;
	private ResourceType type;
	private String name;
	
	public Resource() {
		// TODO Auto-generated constructor stub
	}

	public Resource(Operation operation, String id, String resourceDescription,
			ResourceType type, String name) {
		super();
		this.operation = operation;
		this.id = id;
		this.resourceDescription = resourceDescription;
		this.type = type;
		this.name = name;
	}

	/**
	 * @return the operation
	 */
	public Operation getOperation() {
		return operation;
	}

	/**
	 * @param operation the operation to set
	 */
	public void setOperation(Operation op) {
		this.operation = op;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the resourceDescription
	 */
	public String getResourceDescription() {
		return resourceDescription;
	}

	/**
	 * @param resourceDescription the resourceDescription to set
	 */
	public void setResourceDescription(String resourceDescription) {
		this.resourceDescription = resourceDescription;
	}

	/**
	 * @return the type
	 */
	public ResourceType getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(ResourceType type) {
		this.type = type;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Resource [operation=");
		builder.append(operation);
		builder.append(", id=");
		builder.append(id);
		builder.append(", resourceDescription=");
		builder.append(resourceDescription);
		builder.append(", type=");
		builder.append(type);
		builder.append(", name=");
		builder.append(name);
		builder.append("]");
		return builder.toString();
	}

	
	
		
}
