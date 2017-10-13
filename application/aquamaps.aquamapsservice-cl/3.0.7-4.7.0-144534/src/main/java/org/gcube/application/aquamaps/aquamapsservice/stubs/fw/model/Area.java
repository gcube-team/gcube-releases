package org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model;

import static org.gcube.application.aquamaps.aquamapsservice.stubs.fw.AquaMapsServiceConstants.aquamapsTypesNS;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(namespace=aquamapsTypesNS)
public class Area {
	
	@XmlElement(namespace=aquamapsTypesNS)
	private String type;
	@XmlElement(namespace=aquamapsTypesNS)
	private String name;
	@XmlElement(namespace=aquamapsTypesNS)
	private String code;
	
	public Area() {
		// TODO Auto-generated constructor stub
	}

	public Area(String type, String name, String code) {
		super();
		this.type = type;
		this.name = name;
		this.code = code;
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
	 * @return the code
	 */
	public String code() {
		return code;
	}

	/**
	 * @param code the code to set
	 */
	public void code(String code) {
		this.code = code;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Area [type=");
		builder.append(type);
		builder.append(", name=");
		builder.append(name);
		builder.append(", code=");
		builder.append(code);
		builder.append("]");
		return builder.toString();
	}
	
	
	
}
