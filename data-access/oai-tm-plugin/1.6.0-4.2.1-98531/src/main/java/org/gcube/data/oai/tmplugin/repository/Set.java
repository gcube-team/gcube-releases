/**
 * 
 */
package org.gcube.data.oai.tmplugin.repository;

import java.io.Serializable;

/**
 * @author Fabio Simeoni
 *
 */
public class Set implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private final String id;
	private final String name;
	private final String description;
	
	public Set(String id, String name, String description) {
		this.id=id;
		this.name=name;
		this.description=description;
	}
	

	public String id() {
		return id;
	}
	
	public String name() {
		return name;
	}
	
	public String description() {
		return description;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Set))
			return false;
		Set other = (Set) obj;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}


	@Override
	public String toString() {
		return "Set [description=" + description + ", id=" + id + ", name="
				+ name + "]";
	}
}
