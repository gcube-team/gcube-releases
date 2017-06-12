package org.gcube.portlets.admin.vredeployer.shared;

import java.io.Serializable;
import java.util.ArrayList;


@SuppressWarnings("serial")
public class ResourceCategory implements Serializable {
	
	private String id;
	private String name;
	private ArrayList<ResourceCategoryItem> items;
	
	public ResourceCategory() {
		super();
	}
	
	public ResourceCategory(String id, String name,
			ArrayList<ResourceCategoryItem> items) {
		super();
		this.id = id;
		this.name = name;
		this.items = items;
	}
	
	public ResourceCategory(String id, String name) {
		super();
		this.id = id;
		this.name = name;
		this.items = new ArrayList<ResourceCategoryItem>();
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
	public ArrayList<ResourceCategoryItem> getItems() {
		return items;
	}
	public void setItems(ArrayList<ResourceCategoryItem> items) {
		this.items = items;
	}
	
	public void addResourceItem(ResourceCategoryItem item) {
		items.add(item);
	}
	
}
