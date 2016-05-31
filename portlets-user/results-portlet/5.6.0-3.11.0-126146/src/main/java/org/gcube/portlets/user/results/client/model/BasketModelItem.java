package org.gcube.portlets.user.results.client.model;

import org.gcube.portlets.user.results.client.util.QuerySearchType;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * 
 * @author massi
 *
 */
public class BasketModelItem implements IsSerializable {
		
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * BasketItem name.
	 */
	private String name;
	/**
	 * BasketItem description. 
	 */
	private String description;	
	/**
	 * BasketItem type
	 */
	private BasketModelItemType itemType;
	/**
	 * Storage oid.
	 */
	private String oid;
	/**
	 * Storage oid.
	 */
	private String uri;
	
	/**
	 * object collection id
	 */
	private String collectionID;	
	
	private Boolean isNew;
	
	private QuerySearchType searchType;
	
	/**
	 * 
	 *
	 */
	public BasketModelItem() {
		super();
	}
	
	
	public BasketModelItem(String uri, String oid, String name, String description, String collectionID, BasketModelItemType itemType, Boolean isNew) {
		super();
		this.uri = uri;
		this.oid = oid;
		this.name = name;
		this.description = description;
		this.collectionID = collectionID;
		this.itemType = itemType;
		this.isNew = isNew;
	}
	
	public BasketModelItem(String oid, String name, String description, String collectionID, BasketModelItemType itemType, Boolean isNew, QuerySearchType searchType) {
		super();
		this.name = name;
		this.description = description;
		this.itemType = itemType;
		this.oid = oid;
		this.collectionID = collectionID;
		this.isNew = isNew;
		this.searchType = searchType;
	}


	public String getUri() {
		return uri;
	}


	public void setUri(String uri) {
		this.uri = uri;
	}


	public QuerySearchType getSearchType() {
		return searchType;
	}


	public void setSearchType(QuerySearchType searchType) {
		this.searchType = searchType;
	}


	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public BasketModelItemType getItemType() {
		return itemType;
	}
	public void setItemType(BasketModelItemType itemType) {
		this.itemType = itemType;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getOid() {
		return oid;
	}
	public void setOid(String oid) {
		this.oid = oid;
	}
	public Boolean isNew() {
		return isNew;
	}
	public void setIsNew(Boolean isNew) {
		this.isNew = isNew;
	}
	public String getCollectionID() {
		return collectionID;
	}
	public void setCollectionID(String collectionID) {
		this.collectionID = collectionID;
	}	
	/**
	 * 
	 */
	public String toString() {
		String toReturn = "\nid: "+oid; 
		toReturn += "\nname: "+name;
		toReturn += "\ndescription: "+description;
		toReturn += "\nitemType: "+itemType;
		toReturn += "\ncollectionid: "+collectionID;
		toReturn += "\nisNew?: "+isNew;
		
		return toReturn;
	}
}
