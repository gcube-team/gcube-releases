package org.gcube.data.analysis.statisticalmanager.stubs.types;
import static org.gcube.data.analysis.statisticalmanager.stubs.SMConstants.TYPES_WSDL_NAMESPACE;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
@XmlRootElement(namespace=TYPES_WSDL_NAMESPACE)

public class SMParameter {
	@XmlElement()
	private String  name;

	@XmlElement()
	private String  defaultValue;
	@XmlElement()
	private String  description;
	@XmlElement()
	private SMTypeParameter  type;
	
	 public SMParameter() {
	    }
	 
	 public SMParameter(
	           String defaultValue,
	           String description,
	           String name,
	           SMTypeParameter type) {
	           this.name = name;
	           this.defaultValue = defaultValue;
	           this.description = description;
	           this.type = type;
	    }


	public String name() {
		return name;
	}

	/**
	 * @param resource the resource to set
	 */
	public void name(String name) {
		this.name = name;
	}
	
	

	public String defaultValue() {
		return defaultValue;
	}

	/**
	 * @param resource the resource to set
	 */
	public void defaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}
	

	public String description() {
		return description;
	}

	/**
	 * @param resource the resource to set
	 */
	public void description(String description) {
		this.description = description;
	}
	
	

	public SMTypeParameter type() {
		return type;
	}

	/**
	 * @param resource the resource to set
	 */
	public void type(SMTypeParameter type) {
		this.type = type;
	}
	
}
