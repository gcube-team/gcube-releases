package org.gcube.vremanagement.vremodel.cl.stubs.types;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import static org.gcube.vremanagement.vremodel.cl.Constants.TYPES_NAMESPACE;

@XmlRootElement(namespace=TYPES_NAMESPACE)
public class Report {

	@XmlElement(namespace=TYPES_NAMESPACE)
	private String id;
	@XmlElement(namespace=TYPES_NAMESPACE)
	private String name;
	@XmlElement(namespace=TYPES_NAMESPACE)
	private String description;
	@XmlElement(namespace=TYPES_NAMESPACE)
	private String state;
	
	protected Report(){}
	
	public Report(String id, String name, String description, String state) {
		super();
		this.id = id;
		this.name = name;
		this.description = description;
		this.state = state;
	}
	
	public String id() {
		return id;
	}
	public void id(String id) {
		this.id = id;
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
	public String state() {
		return state;
	}
	public void state(String state) {
		this.state = state;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Report [id=" + id + ", name=" + name + ", description="
				+ description + ", state=" + state + "]";
	}
	
}
