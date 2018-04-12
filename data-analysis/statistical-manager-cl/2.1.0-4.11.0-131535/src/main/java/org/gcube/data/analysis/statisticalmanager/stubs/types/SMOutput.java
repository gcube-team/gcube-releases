package org.gcube.data.analysis.statisticalmanager.stubs.types;
import static org.gcube.data.analysis.statisticalmanager.stubs.SMConstants.TYPES_WSDL_NAMESPACE;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
@XmlRootElement(namespace=TYPES_WSDL_NAMESPACE)

public class SMOutput {
	@XmlElement()
	private String  name;

	@XmlElement()
	private int  size;
	@XmlElement()
	private String  description;
	@XmlElement()
	private String  type;
	
	 public SMOutput() {
	    }
	 
	 public SMOutput(
	           int size,
	           String description,
	           String name,
	           String type) {
	           this.name = name;
	           this.size = size;
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
	
	

	public int size() {
		return this.size;
	}

	/**
	 * @param resource the resource to set
	 */
	public void size(int size) {
		this.size = size;
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
	
	

	public String type() {
		return type;
	}

	/**
	 * @param resource the resource to set
	 */
	public void type(String type) {
		this.type = type;
	}
	
}
