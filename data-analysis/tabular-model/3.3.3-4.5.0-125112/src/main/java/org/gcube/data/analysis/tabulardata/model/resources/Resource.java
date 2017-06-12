package org.gcube.data.analysis.tabulardata.model.resources;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlSeeAlso;

@XmlSeeAlso(value={InternalURI.class, StringResource.class, TableResource.class})

public abstract class Resource implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2723666857485423637L;

	public abstract String getStringValue();
	
	public abstract Class<? extends Resource> getResourceType();

}
