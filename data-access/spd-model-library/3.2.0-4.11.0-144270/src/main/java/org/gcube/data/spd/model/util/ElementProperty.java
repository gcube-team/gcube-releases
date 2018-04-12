package org.gcube.data.spd.model.util;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class ElementProperty implements Serializable{
	
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@XmlAttribute	
	private String name;
	@XmlAttribute
	private String value;
	
	protected ElementProperty() {
		super();
	}

	public ElementProperty(String name, String value) {
		super();
		this.name = name;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public String getValue() {
		return value;
	}
	
}
