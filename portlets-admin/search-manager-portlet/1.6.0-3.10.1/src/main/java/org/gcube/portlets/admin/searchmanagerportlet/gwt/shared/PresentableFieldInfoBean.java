package org.gcube.portlets.admin.searchmanagerportlet.gwt.shared;

import java.util.Set;

import com.google.gwt.user.client.rpc.IsSerializable;

public class PresentableFieldInfoBean implements IsSerializable {
	
	private String ID;
	private String tempID;
	
	private String collectionID; //mandatory
	private String collectionName;
	private String sourceLocator; //mandatory
//	private boolean projection = true; // TODO: for now always set it to true
	private boolean isSortable;

	private String queryExpression; // xpath of the field. Needed by the XMLIndexer optional!
	private Set<String> presentationInfo; // value. what to present etc optional!
	
	
	public void setID(String iD) {
		ID = iD;
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



	/*public void setProjection(boolean projection) {
		this.projection = projection;
	}*/



	public void setSortable(boolean isSortable) {
		this.isSortable = isSortable;
	}



	public void setQueryExpression(String queryExp) {
		this.queryExpression = queryExp;
	}



	public void setPresentationInfo(Set<String> presentationInfo) {
		this.presentationInfo = presentationInfo;
	}



	public String getID() {
		return ID;
	}



	/**
	 * Default constructor
	 */
	public PresentableFieldInfoBean() {
		super();
	}



	public PresentableFieldInfoBean(String id, String collectionID, String collectionName, String sourceLocator, boolean isSortable, String queryExpression, Set<String> presentationInfo) {
		super();
		this.ID = id;
		this.collectionID = collectionID;
		this.collectionName = collectionName;
		this.sourceLocator = sourceLocator;
	//	this.projection = projection;
		this.isSortable = isSortable;
		this.queryExpression = queryExpression;
		this.presentationInfo = presentationInfo;
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



	/*public boolean isProjection() {
		return projection;
	}*/



	public boolean isSortable() {
		return isSortable;
	}



	public String getQueryExpression() {
		return queryExpression;
	}



	public Set<String> getPresentationInfo() {
		return presentationInfo;
	}
	
	public void setTempID(String id) {
		this.tempID = SMConstants.TEMPIDOFFSET + id;
	}
	
	public String getTempID() {
		return tempID;
	}
	
	
	
}
