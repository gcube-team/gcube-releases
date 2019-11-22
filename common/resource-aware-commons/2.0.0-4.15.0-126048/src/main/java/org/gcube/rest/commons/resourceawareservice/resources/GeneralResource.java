package org.gcube.rest.commons.resourceawareservice.resources;

import java.io.Serializable;

import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.rest.commons.helpers.JSONConverter;
import org.gcube.rest.commons.helpers.XMLConverter;

@XmlRootElement
public class GeneralResource implements Serializable {
	private static final long serialVersionUID = 1L;

	private String resourceID;

	@XmlElement
	public final String getResourceID() {
		return this.resourceID;
	}

	public void setResourceID(String resourceID) {
		this.resourceID = resourceID;
	}

	public String toXML() throws JAXBException {
		return XMLConverter.convertToXML(this);
	}
	
	public String toXML(Boolean pretty) throws JAXBException {
		return XMLConverter.convertToXML(this, pretty);
	}
	
	public String toJSON(){
		return JSONConverter.convertToJSON(this);
	}
	
	public String toJSON(Boolean pretty){
		return JSONConverter.convertToJSON(this, pretty);
	}
}
