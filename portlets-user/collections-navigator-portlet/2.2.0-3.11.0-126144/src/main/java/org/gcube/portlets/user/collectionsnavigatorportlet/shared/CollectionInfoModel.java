package org.gcube.portlets.user.collectionsnavigatorportlet.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

public class CollectionInfoModel implements IsSerializable {

	private String collectionID;

	private String collectionName;

	private String collectionDesc;
	
	private boolean isActualCollection;

	private boolean isCollectionSelected;

	private boolean isCollectionOpen;

	private String recordNumber;

	private String creationDate;

	public CollectionInfoModel() {

	}

	public CollectionInfoModel(String ID, String name, String description, String recordNum, String creationDate, boolean isColSelected, boolean isColOpen, boolean isRealCol) {
		this.collectionID = ID;
		this.collectionName = name;
		this.collectionDesc = description;
		this.recordNumber = recordNum;
		this.creationDate = creationDate;
		this.isCollectionSelected = isColSelected;
		this.isCollectionOpen = isColOpen;
		this.isActualCollection = isRealCol;
	}

	public boolean isCollectionSelected() {
		return isCollectionSelected;
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
	
	
}
