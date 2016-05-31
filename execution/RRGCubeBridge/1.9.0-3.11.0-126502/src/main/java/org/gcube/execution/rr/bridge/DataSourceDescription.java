package org.gcube.execution.rr.bridge;

import java.io.StringReader;
import java.util.Calendar;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.gcube.rest.commons.resourceawareservice.resources.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@XmlRootElement(name="SourceProperties")
public class DataSourceDescription {

	private String id;
	private String name;
	private String description;
	private String type;
	private Calendar creationTime;
	private boolean isUser;
	
	public String getId() {
		return id;
	}
	
	@XmlTransient 
	public void setId(String id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	
	@XmlTransient 
	public void setName(String name) {
		this.name = name;
	}
	
	public String getType() {
		return type;
	}
	
	@XmlElement 
	public void setType(String type) {
		this.type = type;
	}
	
	public String getDescription() {
		return description;
	}
	
	@XmlTransient 
	public void setDescription(String descritpion) {
		this.description = descritpion;
	}
	
	public Calendar getCreationTime() {
		return creationTime;
	}
	
	@XmlElement 
	public void setCreationTime(Calendar creationTime) {
		this.creationTime = creationTime;
	}
	
	public boolean isUser() {
		return isUser;
	}

	@XmlElement 
	public void setUser(boolean isUser) {
		this.isUser = isUser;
	}
	
	
	
	@Override
	public String toString() {
		return "DataSourceDescription [id=" + id + ", name=" + name
				+ ", description=" + description + ", type=" + type
				+ ", creationTime=" + creationTime + ", isUser=" + isUser + "]";
	}



	private static JAXBContext jaxbContext = null;

	private static synchronized JAXBContext getJAXBContext() throws Exception{
		if (jaxbContext==null) jaxbContext = JAXBContext.newInstance(DataSourceDescription.class);
		return jaxbContext;
	}
	
	private static transient final Logger logger = LoggerFactory.getLogger(DataSourceDescription.class);

	protected static DataSourceDescription getCollection(Resource resource) throws JAXBException, Exception
	{
		String bodyText = resource.getBodyAsString();
		//bodyText = bodyText.substring("<doc>".length());
		//bodyText = bodyText.substring(0, bodyText.length() - "</doc>".length());
		
		logger.info("bodyText : " + bodyText);
		
		DataSourceDescription collectionDescription = (DataSourceDescription) getJAXBContext().createUnmarshaller().unmarshal(new StringReader(bodyText));

		return collectionDescription;
	}
		
}


