package org.gcube.data.analysis.tabulardata.commons.utils;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class SharingEntity {

	
	
	@SuppressWarnings("unused")
	private SharingEntity() {
		super();
	}

	public static SharingEntity user(String identifier){
		return new SharingEntity(identifier, Type.USER);
	}
	
	public static SharingEntity group(String identifier){
		return new SharingEntity(identifier, Type.GROUP);
	}
	
	private String identifier;
	
	public enum Type {
		USER, 
		GROUP
	}
	
	private Type type;

	protected SharingEntity(String identifier, Type type) {
		super();
		this.identifier = identifier;
		this.type = type;
	}

	public String getIdentifier() {
		return identifier;
	}

	public Type getType() {
		return type;
	}
	
}
