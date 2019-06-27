package org.gcube.spatial.data.sdi.model.services;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.spatial.data.sdi.model.ParameterType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
public abstract class ServiceDefinition {

	public static enum Type{
		THREDDS,GEOSERVER,GEONETWORK
	}
	
	
	@XmlElement(name ="hostname")
	@NonNull
	private String hostname;
	
	@XmlElement(name ="majorVersion")
	@NonNull
	private Short majorVersion;
	
	@XmlElement(name ="minorVersion")
	@NonNull
	private Short minorVersion;
	
	@XmlElement(name ="releaseVersion")	
	private Short releaseVersion;
	
	@XmlElement(name ="type")
	@NonNull
	private Type type;
	
	@XmlElement(name ="adminPassword")
	@NonNull
	private String adminPassword;
	
	@XmlElement(name ="properties")	
	private ArrayList<ParameterType> properties;

	@XmlElement(name ="description")
	private String description;
	
	@XmlElement(name ="name")
	@NonNull
	private String name;

	@Override
	public String toString() {
		return "ServiceDefinition [hostname=" + hostname + ", majorVersion=" + majorVersion + ", minorVersion="
				+ minorVersion + ", releaseVersion=" + releaseVersion + ", type=" + type + ", properties=" + properties
				+ ", description=" + description + ", name=" + name + "]";
	}
	
	

	public void addProperty(String name,String value) {
		if(properties==null) properties=new ArrayList<>();
		properties.add(new ParameterType(name,value));
	}



	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((adminPassword == null) ? 0 : adminPassword.hashCode());
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((hostname == null) ? 0 : hostname.hashCode());
		result = prime * result + ((majorVersion == null) ? 0 : majorVersion.hashCode());
		result = prime * result + ((minorVersion == null) ? 0 : minorVersion.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		
		if(!(properties == null || properties.isEmpty()))
			for(ParameterType param:properties) 
				result=prime*result+param.hashCode();
		
		
		result = prime * result + ((releaseVersion == null) ? 0 : releaseVersion.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		ServiceDefinition other = (ServiceDefinition) obj;
		if (adminPassword == null) {
			if (other.adminPassword != null)
				return false;
		} else if (!adminPassword.equals(other.adminPassword))
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (hostname == null) {
			if (other.hostname != null)
				return false;
		} else if (!hostname.equals(other.hostname))
			return false;
		if (majorVersion == null) {
			if (other.majorVersion != null)
				return false;
		} else if (!majorVersion.equals(other.majorVersion))
			return false;
		if (minorVersion == null) {
			if (other.minorVersion != null)
				return false;
		} else if (!minorVersion.equals(other.minorVersion))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		
		
		if (properties == null || properties.isEmpty()) {
			if (!(other.properties == null || other.properties.isEmpty()))
				return false;
		} else if (properties.size()!=other.properties.size())
			return false;
		else  
			if(!other.properties.containsAll(this.properties)) return false;
		
		
		
		if (releaseVersion == null) {
			if (other.releaseVersion != null)
				return false;
		} else if (!releaseVersion.equals(other.releaseVersion))
			return false;
		if (type != other.type)
			return false;
		return true;
	}
	
	
	
}
