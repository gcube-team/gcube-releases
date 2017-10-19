package org.gcube.portlets.admin.accountingmanager.shared.workspace;

import java.io.Serializable;

/**
 * 
  * @author Giancarlo Panichi
 *
 *
 */
public class ItemDescription implements Serializable {

	private static final long serialVersionUID = -6624452446980057923L;

	private String id;
	private String name;
	private String owner;
	private String path;
	private String type;
	private String publicLink;

	public ItemDescription() {
		super();
	}

	public ItemDescription(String id, String name, String owner, String path,
			String type) {
		super();
		this.id = id;
		this.name = name;
		this.owner = owner;
		this.path = path;
		this.type = type;
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

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getPublicLink() {
		return publicLink;
	}

	public void setPublicLink(String publicLink) {
		this.publicLink = publicLink;
	}

	/**
	 * Compare basic information with another ItemDescription not null
	 * 
	 * @param itemDescription item description
	 * @return boolean  true if item description match
	 *
	 */
	public boolean compareInfo(ItemDescription itemDescription){
		if(itemDescription!=null && id.compareTo(itemDescription.getId())==0 && 
				name.compareTo(itemDescription.getName())==0 &&
				owner.compareTo(itemDescription.getOwner())==0 && 
				path.compareTo(itemDescription.getPath())==0 &&
				type.compareTo(itemDescription.getType())==0
				){
			return true;
		} else {
			return false;
		}
		
	}

	@Override
	public String toString() {
		return "ItemDescription [id=" + id + ", name=" + name + ", owner="
				+ owner + ", path=" + path + ", type=" + type + ", publicLink="
				+ publicLink + "]";
	}

}
