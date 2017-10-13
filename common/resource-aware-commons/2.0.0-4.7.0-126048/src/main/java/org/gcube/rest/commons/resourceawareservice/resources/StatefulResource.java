package org.gcube.rest.commons.resourceawareservice.resources;

import java.io.Serializable;
import java.util.Calendar;

import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.rest.commons.helpers.JSONConverter;
import org.gcube.rest.commons.helpers.XMLConverter;
import org.gcube.rest.commons.resourceawareservice.resources.exceptions.StatefulResourceException;

@XmlRootElement
public abstract class StatefulResource extends GeneralResource implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private Calendar created;
	
	private Calendar lastUpdated;

	@XmlElement
	public Calendar getCreated() {
		return created;
	}

	public void setCreated(Calendar created) {
		this.created = created;
	}

	@XmlElement
	public Calendar getLastUpdated() {
		return lastUpdated;
	}

	public void setLastUpdated(Calendar lastUpdated) {
		this.lastUpdated = lastUpdated;
	}
	
	
	public String toJSON(){
		return JSONConverter.convertToJSON(this);
	}
	
	public String toJSON(Boolean pretty){
		return JSONConverter.convertToJSON(this, pretty);
	}
	
	public String toXML() throws JAXBException {
		return XMLConverter.convertToXML(this);
	}
	
	public String toXML(Boolean pretty) throws JAXBException {
		return XMLConverter.convertToXML(this, pretty);
	}

	abstract public void onLoad() throws StatefulResourceException;
	abstract public void onClose() throws StatefulResourceException;
	abstract public void onDestroy() throws StatefulResourceException;
}
