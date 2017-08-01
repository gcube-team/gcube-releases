package org.gcube.vremanagement.vremodel.cl.stubs.types;

import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import static org.gcube.vremanagement.vremodel.cl.Constants.TYPES_NAMESPACE;

@XmlRootElement(namespace=TYPES_NAMESPACE)
public class FunctionalityItem {

	@XmlElement(namespace=TYPES_NAMESPACE)
	private int id;
	@XmlElement(namespace=TYPES_NAMESPACE)
	private String name;
	@XmlElement(namespace=TYPES_NAMESPACE)
	private String description;
	@XmlElement(namespace=TYPES_NAMESPACE)
	private boolean selected;
	@XmlElement(namespace=TYPES_NAMESPACE)
	private boolean mandatory;
	@XmlElement(namespace=TYPES_NAMESPACE)
	private List<ResourceDescriptionItem> selectableResourcesDescription;
	@XmlElement(namespace=TYPES_NAMESPACE, name="childs")
	private List<FunctionalityItem> children;
	
	protected  FunctionalityItem() {
		super();
	}
	
	public FunctionalityItem(int id, String name, String description,
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
	public int id() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void id(int id) {
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
	
	/**
	 * @return the mandatory
	 */
	public boolean mandatory() {
		return mandatory;
	}

	/**
	 * @param mandatory the mandatory to set
	 */
	public void mandatory(boolean mandatory) {
		this.mandatory = mandatory;
	}

	/**
	 * @return the selectableResourcesDescription
	 */
	public List<ResourceDescriptionItem> selectableResourcesDescription() {
		if (selectableResourcesDescription==null) return Collections.emptyList();
		return selectableResourcesDescription;
	}
	/**
	 * @param selectableResourcesDescription the selectableResourcesDescription to set
	 */
	public void selectableResourcesDescription(
			List<ResourceDescriptionItem> selectableResourcesDescription) {
		this.selectableResourcesDescription = selectableResourcesDescription;
	}
	/**
	 * @return the childs
	 */
	public List<FunctionalityItem> children() {
		if (children==null) return Collections.emptyList();
		return children;
	}
	/**
	 * @param childs the childs to set
	 */
	public void children(List<FunctionalityItem> children) {
		this.children = children;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "FunctionalityItem [id=" + id + ", name=" + name
				+ ", description=" + description + ", selected=" + selected
				+ ", selectableResourcesDescription="
				+ selectableResourcesDescription + ", children=" + children
				+ "]";
	}
	
	
}

