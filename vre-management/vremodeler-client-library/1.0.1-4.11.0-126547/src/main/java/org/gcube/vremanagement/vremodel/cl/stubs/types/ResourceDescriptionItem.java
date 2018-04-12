package org.gcube.vremanagement.vremodel.cl.stubs.types;

import static org.gcube.vremanagement.vremodel.cl.Constants.TYPES_NAMESPACE;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(namespace=TYPES_NAMESPACE)
public class ResourceDescriptionItem {

	@XmlElement(namespace=TYPES_NAMESPACE)
	private String id;
	@XmlElement(namespace=TYPES_NAMESPACE)
	private String description;
	@XmlElement(namespace=TYPES_NAMESPACE)
	private List<ResourceItem> resource;
	@XmlElement(namespace=TYPES_NAMESPACE)
	private int minSelectable;
	@XmlElement(namespace=TYPES_NAMESPACE)
	private int maxSelectable;
	
	protected ResourceDescriptionItem() {
		super();
	}

	public ResourceDescriptionItem(String id, String description) {
		super();
		this.id = id;
		this.description = description;
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
	 * @return the resource
	 */
	public List<ResourceItem> resources() {
		return resource;
	}

	/**
	 * @param resource the resource to set
	 */
	public void resources(List<ResourceItem> resource) {
		this.resource = resource;
	}

	/**
	 * @return the minSelectable
	 */
	public int minSelectable() {
		return minSelectable;
	}

	/**
	 * @return the maxSelectable
	 */
	public int maxSelectable() {
		return maxSelectable;
	}
		
	
	
}
