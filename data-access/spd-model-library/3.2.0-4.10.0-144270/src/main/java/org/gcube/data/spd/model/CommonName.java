package org.gcube.data.spd.model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class CommonName implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@XmlAttribute
	private String language;
	@XmlAttribute
	private String name;
	@XmlAttribute
	private String locality;
	
	protected CommonName(){}
	
	public CommonName(String language, String name) {
		super();
		this.language = language;
		this.name = name;
	}

	public String getLocality() {
		return locality;
	}

	public void setLocality(String locality) {
		this.locality = locality;
	}

	public String getLanguage() {
		return language;
	}

	public String getName() {
		return name;
	}

	public String toString(){
		StringBuilder toReturn = new StringBuilder();
		toReturn.append("[ language: "+this.language+"]");
		toReturn.append("[ name: "+this.name+"]");
		return toReturn.toString();
	}
	
}
