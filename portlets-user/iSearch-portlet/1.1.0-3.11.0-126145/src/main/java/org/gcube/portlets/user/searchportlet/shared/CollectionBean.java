package org.gcube.portlets.user.searchportlet.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * A class that represents a Collection object
 * 
 * @author Panagiota Koltsida, NKUA
 *
 */
public class CollectionBean implements IsSerializable {

	private String collectionID;

	private String collectionName;

	private String collectionDesc;
	
	private boolean isActualCollection;

	private boolean isCollectionSelected;

	private boolean isCollectionOpen;

	private String recordNumber;

	private String creationDate;
	
	private String type;

	public CollectionBean() {

	}

	public CollectionBean(String ID, String name, String description, String recordNum, String creationDate, String type, boolean isColSelected, boolean isColOpen, boolean isRealCol) {
		this.collectionID = ID;
		this.collectionName = name;
		this.collectionDesc = description;
		this.recordNumber = recordNum;
		this.creationDate = creationDate;
		this.type = type;
		this.isCollectionSelected = isColSelected;
		this.isCollectionOpen = isColOpen;
		this.isActualCollection = isRealCol;
	}

	public boolean isCollectionSelected() {
		return isCollectionSelected;
	}
	
	public void setSelected(boolean isSelected) {
		this.isCollectionSelected = isSelected;
	}

	public boolean isCollectionOpen() {
		return isCollectionOpen;
	}

	public String getRecordNumber() {
		return recordNumber;
	}

	public String getCreationDate() {
		return creationDate;
	}

	public String getCollectionID() {
		return this.collectionID;
	}

	public String getCollectionName() {
		return this.collectionName;
	}

	public String getCollecionDescription() {
		return this.collectionDesc;
	}

	public boolean isActualCollection() {
		return isActualCollection;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	
	
	
}
