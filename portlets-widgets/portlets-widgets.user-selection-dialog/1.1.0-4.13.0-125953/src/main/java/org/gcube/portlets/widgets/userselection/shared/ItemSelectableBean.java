package org.gcube.portlets.widgets.userselection.shared;

import java.io.Serializable;

@SuppressWarnings("serial")
public class ItemSelectableBean implements Serializable {
	private String id;
	private String name;
	private String iconURL;
	private String type;
	
	public ItemSelectableBean() {
		super();
	}


	public ItemSelectableBean(String id, String name, String iconURL,
			String type) {
		super();
		this.id = id;
		this.name = name;
		this.iconURL = iconURL;
		this.type = type;
	}


	public ItemSelectableBean(String id, String fullName, String pictureURL) {
		super();
		this.id = id;
		this.name = fullName;
		this.iconURL = pictureURL;
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

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	public String getIconURL() {
		return iconURL;
	}
	public void setIconURL(String pictureURL) {
		this.iconURL = pictureURL;
	}


	@Override
	public String toString() {
		return "ItemSelectableBean [id=" + id + ", name=" + name
				+ ", iconURL=" + iconURL + ", type=" + type + "]";
	}
	
}
