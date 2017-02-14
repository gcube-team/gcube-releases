package org.gcube.data.spd.model;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.gcube.data.spd.model.util.Capabilities;

public class PluginDescription {

	private String name;
	private String description;
	private RepositoryInfo info;
	
	private Map<Capabilities, List<Conditions>> supportedCapabilities = Collections.emptyMap();
	private boolean isRemote = false;
	
	public PluginDescription(){}
	
	public PluginDescription(String name, String description,
			RepositoryInfo info) {
		super();
		this.name = name;
		this.description = description;
		this.info = info;
	}

	public Map<Capabilities, List<Conditions>> getSupportedCapabilities() {
		return supportedCapabilities;
	}

	public void setSupportedCapabilities(Map<Capabilities, List<Conditions>> supportedCapabilities) {
		this.supportedCapabilities = supportedCapabilities;
	}


	public boolean isRemote() {
		return isRemote;
	}

	public void setRemote(boolean isRemote) {
		this.isRemote = isRemote;
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

	public RepositoryInfo getInfo() {
		return info;
	}

	public void setInfo(RepositoryInfo info) {
		this.info = info;
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
		PluginDescription other = (PluginDescription) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "PluginDescription [name=" + name + ", description="
				+ description + ", info=" + info + ", supportedCapabilities="
				+ supportedCapabilities + ", isRemote=" + isRemote + "]";
	}
	
	
}
