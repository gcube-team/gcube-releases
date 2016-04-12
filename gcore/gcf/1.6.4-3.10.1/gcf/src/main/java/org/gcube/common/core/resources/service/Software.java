package org.gcube.common.core.resources.service;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * Software package
 * 
 * @author Manuele Simi (ISTI-CNR)
 */
public class Software extends Package {

	public static enum Type 
	{
		library(){public String toString(){return "library";}},
		application(){public String toString(){return "application";}},
		webapplication(){public String toString(){return "webapplication";}},
	}
	
	protected List<String> files= new ArrayList<String>();
	protected List<String> entrypoints= new ArrayList<String>();
	protected Type type = Type.library; 
	protected URI uri;	
	
	public List<String> getFiles() {return this.files;}
	public void setFiles(List<String> files) {this.files=files;}
	public Type getType() {return type;}
	public void setType(Type type) {this.type = type;}
	public URI getURI() {return uri;}
	public void setURI(URI uri) {this.uri = uri;}
	
	/**
	 * @return the entrypoints
	 */
	public List<String> getEntrypoints() {
		return entrypoints;
	}
	/**
	 * @param endpoints the entrypoints to set
	 */
	public void setEntrypoints(List<String> entrypoints) {
		this.entrypoints = entrypoints;
	}
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;

		final Software other = (Software) obj;

		if (files == null) {
			if (other.files != null)
				return false;
		} else if (! files.equals(other.files))
			return false;
		
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (! type.equals(other.type))
			return false;
		
		if (uri == null) {
			if (other.uri != null)
				return false;
		} else if (! uri.equals(other.uri))
			return false;

		return super.equals(obj);
	}




}
