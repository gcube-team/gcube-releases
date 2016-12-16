package org.gcube.smartgears.configuration.application;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

@XmlRootElement(name="exclude")
@XmlAccessorType(XmlAccessType.FIELD)
public class Exclude {

	@XmlAttribute(name="handlers")
	private List<String> handlers = new ArrayList<String>();
	
	@XmlValue
	private String path;

	public List<String> getHandlers() {
		return handlers;
	}

	public String getPath() {
		return path;
	}

	protected Exclude() {}

	public Exclude(String path) {
		super();
		this.path = path;
	}
	
	public Exclude(List<String> handlers, String path) {
		super();
		this.handlers = handlers;
		this.path = path;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((path == null) ? 0 : path.hashCode());
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
		Exclude other = (Exclude) obj;
		if (handlers == null) {
			if (other.handlers != null)
				return false;
		} else if (!handlers.equals(other.handlers))
			return false;
		if (path == null) {
			if (other.path != null)
				return false;
		} else if (!path.equals(other.path))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Exclude [handlers=" + handlers + ", path=" + path + "]";
	}
		
}
