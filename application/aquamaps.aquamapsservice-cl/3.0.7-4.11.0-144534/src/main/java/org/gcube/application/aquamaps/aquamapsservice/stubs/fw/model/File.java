package org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model;

import static org.gcube.application.aquamaps.aquamapsservice.stubs.fw.AquaMapsServiceConstants.aquamapsTypesNS;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(namespace=aquamapsTypesNS)
public class File {

	@XmlElement(namespace=aquamapsTypesNS)
	private String name;
	@XmlElement(namespace=aquamapsTypesNS)
	private String type;
	@XmlElement(namespace=aquamapsTypesNS)
	private String url;
	
	
	public File() {
		// TODO Auto-generated constructor stub
	}


	public File(String name, String type, String url) {
		super();
		this.name = name;
		this.type = type;
		this.url = url;
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
	 * @return the url
	 */
	public String url() {
		return url;
	}


	/**
	 * @param url the url to set
	 */
	public void url(String url) {
		this.url = url;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("File [name=");
		builder.append(name);
		builder.append(", type=");
		builder.append(type);
		builder.append(", url=");
		builder.append(url);
		builder.append("]");
		return builder.toString();
	}
	
	
}
