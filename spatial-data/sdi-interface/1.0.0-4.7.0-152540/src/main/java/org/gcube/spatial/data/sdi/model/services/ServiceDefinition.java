package org.gcube.spatial.data.sdi.model.services;

import java.util.HashMap;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
@Getter
@Setter
@EqualsAndHashCode
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
	private HashMap<String,String> properties;

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
	
	
	
	
	
	
}
