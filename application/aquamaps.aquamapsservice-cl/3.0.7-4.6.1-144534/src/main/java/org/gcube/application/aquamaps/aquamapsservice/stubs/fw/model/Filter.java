package org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model;

import static org.gcube.application.aquamaps.aquamapsservice.stubs.fw.AquaMapsServiceConstants.aquamapsTypesNS;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(namespace=aquamapsTypesNS)
public class Filter {
	@XmlElement(namespace=aquamapsTypesNS)
	private String type;
	@XmlElement(namespace=aquamapsTypesNS)
	private String name;
	@XmlElement(namespace=aquamapsTypesNS)
	private String value;
	@XmlElement(namespace=aquamapsTypesNS)
	private String fieldType;
	
	public Filter() {
		// TODO Auto-generated constructor stub
	}

	public Filter(String type, String name, String value, String fieldType) {
		super();
		this.type = type;
		this.name = name;
		this.value = value;
		this.fieldType = fieldType;
	}

	/**
	 * @return the type
	 */
	public String type() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void type(String type) {
		this.type = type;
	}

	/**
	 * @return the name
	 */
	public String name() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void name(String name) {
		this.name = name;
	}

	/**
	 * @return the value
	 */
	public String value() {
		return value;
	}

	/**
	 * @param value the value to set
	 */
	public void value(String value) {
		this.value = value;
	}

	/**
	 * @return the fieldType
	 */
	public String fieldType() {
		return fieldType;
	}

	/**
	 * @param fieldType the fieldType to set
	 */
	public void fieldType(String fieldType) {
		this.fieldType = fieldType;
	}
	
	
}
