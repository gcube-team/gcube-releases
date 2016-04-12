package org.gcube.portlets.user.results.client.model;

import org.gcube.portlets.user.results.shared.ObjectType;
import org.gcube.portlets.user.results.shared.ResultRecord;

import com.google.gwt.user.client.rpc.IsSerializable;


/**
 * @author Massimiliano Assante ISTI-CNR
 */
public class ResultObj implements IsSerializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2538951874622318563L;
	/**
	 * The user who asked for results in the portal, needed to ask for the content
	 */
	protected String currUserName;
	/***
	 * 
	 */
	protected String title;
	/**
	 * 
	 */
	protected String objectURI;
	
	/**
	 * Object's type
	 */
	protected ObjectType objectType;
	
	protected String payload;
	
	/**
	 * resultRec Record
	 */
	protected ResultRecord resultRec;
	/**
	 * htmlText text
	 */
	protected String htmlText;
	
	/**
	 * the mimetype of the object
	 */
	protected String mimetype;
	
	/**
	 *  object's colelction id
	 */
	private String collectionID;
	/**
	 *  object's collection name
	 */
	private String collectionName;
	
	
	/**
	 * its rank
	 */
	private String rank;
	
	/**
	 * constructor
	 *
	 */
	public ResultObj(){	}
	/**
	 * 	
	 * @param docURI
	 * @param currUserName
	 * @param title
	 * @param resultRec
	 * @param htmlText
	 * @param mimetype
	 * @param collectionID
	 * @param rank
	 * @param collectionName
	 */
	public ResultObj(String docURI, String currUserName, String title, ResultRecord resultRec, String htmlText, String mimetype, String collectionID, String rank, String collectionName, ObjectType objectType) {
		super();
		this.objectURI = docURI;
		this.currUserName = currUserName;
		this.title = title;
		this.resultRec = resultRec;
		this.htmlText = htmlText;
		this.mimetype = mimetype;
		this.collectionID = collectionID;
		this.rank = rank;
		this.collectionName = collectionName;
		this.objectType = objectType;
	}

	public String getObjectURI() {
		return objectURI;
	}

	public void setObjectURI(String objectURI) {
		this.objectURI = objectURI;
	}
	
	public ObjectType getObjectType() {
		return objectType;
	}

	public void setObjectType(ObjectType objectType) {
		this.objectType = objectType;
	}


	public String getCollectionID() {
		return collectionID;
	}

	public void setCollectionID(String collectionID) {
		this.collectionID = collectionID;
	}

	public String getCurrUserName() {
		return currUserName;
	}

	public void setCurrUserName(String currUserName) {
		this.currUserName = currUserName;
	}

	public String getRank() {
		return rank;
	}

	public void setRank(String rank) {
		this.rank = rank;
	}

	public String getTitle() {
		return title;
	}


	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getPayload() {
		return payload;
	}
	public void setPayload(String payload) {
		this.payload = payload;
	}


	/**
	 * @return htmlText
	 */
	public String getHtmlText() {
		return htmlText;
	}
	/**
	 * @param htmlText htmlText
	 */
	public void setHtmlText(String htmlText) {
		this.htmlText = htmlText;
	}
	/**
	 * @return result Record
	 */
//	public ResultRecord getResultRec() {
//		return resultRec;
//	}
	/**
	 * @param resultRec result Record
	 */
	public void setResultRec(ResultRecord resultRec) {
		this.resultRec = resultRec;
	}

	public String getMimetype() {
		return mimetype;
	}
	public void setMimetype(String mimetype) {
		this.mimetype = mimetype;
	}
	public String getCollectionName() {
		return collectionName;
	}
	public void setCollectionName(String collectionName) {
		this.collectionName = collectionName;
	}
	
}
