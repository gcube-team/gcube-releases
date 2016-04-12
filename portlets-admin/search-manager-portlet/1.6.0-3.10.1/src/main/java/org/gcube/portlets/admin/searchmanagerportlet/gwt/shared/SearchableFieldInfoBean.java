package org.gcube.portlets.admin.searchmanagerportlet.gwt.shared;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.IsSerializable;

public class SearchableFieldInfoBean implements IsSerializable {
	
	private String ID;
	private String tempID;
	
	private String collectionID;
	private String collectionName;
	private String sourceLocator; // the ws-resource index ID
	private ArrayList<String> indexCapabilities; // equal, or, nor, etc
	private String indexQueryExpression; // e.g. the path to the field. It is needed for the XML indexer
	private boolean isSortable;
	
	public SearchableFieldInfoBean() {super();}

	public SearchableFieldInfoBean(String id, String collectionID, String collectionName, String sourceLocator, ArrayList<String> indexCapabilities, String indexQueryExpression,
			boolean isSortable) {
		super();
		this.ID = id;
		this.collectionID = collectionID;
		this.collectionName = collectionName;
		this.sourceLocator = sourceLocator;
		this.indexCapabilities = indexCapabilities;
		this.indexQueryExpression = indexQueryExpression;
		this.isSortable = isSortable;
	}

	public void setID(String iD) {
		ID = iD;
	}

	public String getCollectionID() {
		return collectionID;
	}
	
	public String getCollectionName() {
		return collectionName;
	}

	public String getSourceLocator() {
		return sourceLocator;
	}

	public ArrayList<String> getIndexCapabilities() {
		return indexCapabilities;
	}

	public String getIndexQueryLanguage() {
		return indexQueryExpression;
	}

	public boolean isSortable() {
		return isSortable;
	}
	
	public void setCollectionID(String collectionID) {
		this.collectionID = collectionID;
	}
	
	public void setCollectionName(String collectionName) {
		this.collectionName = collectionName;
	}

	public void setSourceLocator(String sourceLocator) {
		this.sourceLocator = sourceLocator;
	}

	public void setIndexCapabilities(ArrayList<String> indexCapabilities) {
		this.indexCapabilities = indexCapabilities;
	}

	public void setIndexQueryLanguage(String indexQueryLanguage) {
		this.indexQueryExpression = indexQueryLanguage;
	}

	public void setSortable(boolean isSortable) {
		this.isSortable = isSortable;
	}

	public void setTempID(String id) {
		this.tempID = SMConstants.TEMPIDOFFSET + id;
	}
	
	public String getTempID() {
		return tempID;
	}
	
	public String getID() {
		return ID;
	}
	
}
