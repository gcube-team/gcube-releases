package org.gcube.portlets.widgets.pickitem.shared;

import java.io.Serializable;

@SuppressWarnings("serial")
public class ItemBean implements Serializable {
	private String id;
	private String name;
	private String alternativeName;
	private String thumbnailURL;
	private boolean isItemGroup;

	public ItemBean() {
		super();
	}
	/**
	 * Use it when the Item represents a user.
	 * @param id
	 * @param username
	 * @param fullName
	 * @param thumbnailURL
	 */
	public ItemBean(String id, String username, String fullName, String thumbnailURL) {
		super();
		this.id = id;
		this.name = username;
		this.alternativeName = fullName;
		this.thumbnailURL = thumbnailURL;
	}
	/**
	 * Use it when the Item represents a group of users (namely a team). 
	 * @param id
	 * @param teamName
	 */
	public ItemBean(String id, String teamName) {
		super();
		this.id = id;
		this.name = teamName;
		this.alternativeName = teamName;
		this.isItemGroup = true;
	}
	public boolean isItemGroup() {
		return isItemGroup;
	}
	public void setItemGroup(boolean isItemGroup) {
		this.isItemGroup = isItemGroup;
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
	public String getAlternativeName() {
		return alternativeName;
	}
	public void setAlternativeName(String altname) {
		this.alternativeName = altname;
	}

	public String getThumbnailURL() {
		return thumbnailURL;
	}
	public void setThumbnailURL(String thumbnailURL) {
		this.thumbnailURL = thumbnailURL;
	}
	@Override
	public String toString() {
		return "ItemBean [id=" + id + ", name=" + name + ", alternativeName="
				+ alternativeName + ", thumbnailURL=" + thumbnailURL
				+ ", isItemGroup=" + isItemGroup + "]";
	}	
}
