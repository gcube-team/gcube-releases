package org.gcube.data.analysis.statisticalmanager.stubs.types;

import static org.gcube.data.analysis.statisticalmanager.stubs.SMConstants.TYPES_WSDL_NAMESPACE;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(namespace = TYPES_WSDL_NAMESPACE)
public class SMAlgorithm implements Serializable{
	@XmlElement()
	private String category;

	@XmlElement()
	private String name;

	@XmlElement()
	private String description;

	public SMAlgorithm() {
	}

	public SMAlgorithm(String category, String description, String name) {
		this.category = category;
		this.name = name;
		this.description = description;
	}

	public String category() {
		return category;
	}

	public void category(String category) {
		this.category = category;
	}

	public String name() {
		return name;
	}

	public void name(String name) {
		this.name = name;
	}

	public String description() {
		return description;
	}

	public void description(String description) {
		this.description = description;
	}

}
