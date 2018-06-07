package org.gcube.portlets.admin.vredefinition.shared;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * A resource category class element.
 * @author Costantino Perciante at ISTI-CNR 
 * (costantino.perciante@isti.cnr.it)
 *
 */
@SuppressWarnings("serial")
public class ResourceCategory implements Serializable{

	private String id;
	private String name;
	private ArrayList<Resource> items;

	/**
	 * needed for serialization
	 */
	public ResourceCategory() {
		super();
	}

	public ResourceCategory(String id, String name) {
		super();
		this.id = id;
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ArrayList<Resource> getItems() {
		return items;
	}

	public void setItems(ArrayList<Resource> items) {
		this.items = items;
	}

	@Override
	public String toString() {
		return "ResourceCategory [id=" + id + ", name=" + name + ", items="
				+ items + "]";
	}
}
