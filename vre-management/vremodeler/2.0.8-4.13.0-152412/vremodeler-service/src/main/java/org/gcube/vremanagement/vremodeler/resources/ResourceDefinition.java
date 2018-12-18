package org.gcube.vremanagement.vremodeler.resources;

import java.io.Serializable;
import java.util.List;

import org.apache.axis.components.uuid.UUIDGenFactory;
import org.gcube.vremanagement.vremodeler.impl.peristentobjects.ResourceInterface;

public abstract class ResourceDefinition<T extends ResourceInterface> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String description;
	private String id;
	
	private int minSelectable;
	private int maxSelectable;
	
	public ResourceDefinition() {
		super();
		this.id = UUIDGenFactory.getUUIDGen().nextUUID(); 
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

		
	/**
	 * @return the minSelectable
	 */
	public int getMinSelectable() {
		return minSelectable;
	}

	/**
	 * @param minSelectable the minSelectable to set
	 */
	public void setMinSelectable(int minSelectable) {
		this.minSelectable = minSelectable;
	}

	/**
	 * @return the maxSelectable
	 */
	public int getMaxSelectable() {
		return maxSelectable;
	}

	/**
	 * @param maxSelectable the maxSelectable to set
	 */
	public void setMaxSelectable(int maxSelectable) {
		this.maxSelectable = maxSelectable;
	}

	public abstract List<T> getResources() throws Exception;
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ResourceDefinition other = (ResourceDefinition) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
	
	
	
}
