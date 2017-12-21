package org.gcube.vremanagement.vremodel.cl.stubs.types;

import static org.gcube.vremanagement.vremodel.cl.Constants.TYPES_NAMESPACE;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(namespace=TYPES_NAMESPACE)
public class ResourceItem {
	
	@XmlElement(namespace=TYPES_NAMESPACE)
	private String id;
	@XmlElement(namespace=TYPES_NAMESPACE)
	private String name;
	@XmlElement(namespace=TYPES_NAMESPACE)
	private String description;
	@XmlElement(namespace=TYPES_NAMESPACE)
	private boolean selected;
	
	protected ResourceItem() {
		super();
	}

	public ResourceItem(String id, String name, String description,
			boolean selected) {
		super();
		this.id = id;
		this.name = name;
		this.description = description;
		this.selected = selected;
	}

	/**
	 * @return the id
	 */
	public String id() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void id(String id) {
		this.id = id;
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
	 * @return the description
	 */
	public String description() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void description(String description) {
		this.description = description;
	}

	/**
	 * @return the selected
	 */
	public boolean selected() {
		return selected;
	}

	/**
	 * @param selected the selected to set
	 */
	public void selected(boolean selected) {
		this.selected = selected;
	}
	
		
	
}
