/**
 * 
 */
package org.gcube.portlets.user.geoexplorer.client.beans;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * @author ceras
 *
 */
public class Workspace implements IsSerializable {
	
	private String description;
	private String name;

	/**
	 * 
	 */
	public Workspace() {
	}

	/**
	 * @param description
	 * @param name
	 */
	public Workspace(String description, String name) {
		super();
		this.description = description;
		this.name = name;
	}
	
	/**
	 * @param ws
	 */
	public Workspace(String workspaceStr) throws Exception {
		try {
			String[] strArray = workspaceStr.split(":");
			this.description = strArray[0];
			this.name = strArray[1];
		} catch (Exception e) {
			throw new Exception("Error transforming workspace string into workspace object", e);
		}
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
	
	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}
	
	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return this.description + ": " + this.name;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Workspace other = (Workspace) obj;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
}
