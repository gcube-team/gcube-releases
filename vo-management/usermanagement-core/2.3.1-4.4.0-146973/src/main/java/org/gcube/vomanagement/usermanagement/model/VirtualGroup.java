package org.gcube.vomanagement.usermanagement.model;

import java.io.Serializable;
/**
 * 
 * @author Massimiliano Assante, CNR-ISTI
 * Virtual Groups are another way to group VREs indipendently from the scope, 
 * they are modeled as Custom attributes in Liferay Sites table wtih 
 */
@SuppressWarnings("serial")
public class VirtualGroup implements Serializable{
	private String name;
	private String description;
	public VirtualGroup() {}
	public VirtualGroup(String name, String description) {
		super();
		this.name = name;
		this.description = description;
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
	@Override
	public String toString() {
		return "VirtualGroup [name=" + name + ", description=" + description
				+ "]";
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		VirtualGroup other = (VirtualGroup) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
	
}
