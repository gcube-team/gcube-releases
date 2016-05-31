package org.gcube.portlets.user.collectionsnavigatorportlet.shared;

import com.google.gwt.user.client.rpc.IsSerializable;


public class CollectionInfo implements IsSerializable {
	
	
	boolean isCollectionGroup;
	String id;
	String name;
	String description;
	String recNo;
	String creationDate;
	
	
	public CollectionInfo() {
		this.id = "";
		this.name = "";
		this.description = "";
		this.recNo = "";
		this.creationDate = "";
		this.isCollectionGroup = false;
	}
	
	/**
	 * @return collection's description
	 */
	public String getDescription() {
		return description;
	}
	/**
	 * @param description  collection's description
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	/**
	 * @return  collection's ID
	 */
	public String getId() {
		return id;
	}
	/**
	 * @param id collection's ID
	 */
	public void setId(String id) {
		this.id = id;
	}
	/**
	 * @return  collection's name 
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name  collection's name 
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	public String getCreationDate() {
		return this.creationDate;
	}
	
	public String getRecNo() {
		return this.recNo;
	}

	public boolean isCollectionGroup() {
		return isCollectionGroup;
	}

	public void setCollectionGroup(boolean isCollectionGroup) {
		this.isCollectionGroup = isCollectionGroup;
	}
	
	

}
