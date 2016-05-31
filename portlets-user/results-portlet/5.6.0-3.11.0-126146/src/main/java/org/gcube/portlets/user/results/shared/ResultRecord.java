package org.gcube.portlets.user.results.shared;


import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * The <code> ResultRecord </code> represents the element of an XML Result Record
 *
 * @author massimiliano.assante@isti.cnr.it
 * @version march 2007 (1.0) 
 */

public class ResultRecord implements IsSerializable {

	
//	 INSTANCE VARIABLES ***********************************************

	protected String docId;
	protected String title;
	protected String creator;
	protected String date;
	protected String collection;
	protected String collectionName;
	protected String rank;
	protected String type;
	protected String content;
	protected int length;
	protected String mimeType;
	protected String label;
	
	

//	 CONSTRUCTORS *****************************************************
	

	/**
	 * the Default Constuctor is needed for the serialization
	 */
	
	public ResultRecord() {
		super();
		this.docId = null;
		this.title = null;
		this.type = null;
		this.creator = null;
		this.date = null;
		this.collection = null;
		this.collectionName = null;
		this.content = null;
		this.rank = null;
		this.length = 0;
	}
	
	/**
	 * Constructs a ResultRecord Object from a parsed XML RSRecord
	 * 
	 * @param docId d
	 * @param title d
	 * @param type d
	 * @param creator d
	 * @param date d
	 * @param collection d
	 * @param metadata d
	 * @param content d
	 * @param rank r
	 */
	
	public ResultRecord(String docId, String title, String type, String creator, String date, String collection,  String metadata, String content, String rank) {
		this.mimeType = "text/XML";
		this.label = title;
		this.type = "rsRecord";
		this.docId = docId;
		this.title = title;
		this.type = type;
		this.creator = creator;
		this.date = date;
		this.collection = collection;
		this.content = content;
		this.rank = rank;
	}
	
	/**
	 * Constructs a ResultRecord Object from a RSRecord XML String
	 * 
	 * @param XMLString
	 */

	
//	 INSTANCE METHODS *************************************************
	
	/**
	 * @return docId
	 */
	public String getDocId() { return this.docId; }
	
	/**
	 * @return title
	 */
	public String getTitle() { return this.title; }
	/**
	 * @return creator
	 */
	public String getCreator() { return this.creator; }
	/**
	 * @return date
	 */
	public String getDate() { return this.date; }
	/**
	 * @return collection
	 */
	public String getCollection() { return this.collection; }
	/**
	 * @return rank
	 */
	public String getRank() { return this.rank; }
	
	/**
	 * 
	 * @param s s
	 */
	public void setDocId(String s) { this.docId = s; }
	
	/**
	 * 
	 * @param s s
	 */
	public void setTitle(String s) { this.title = s; }
	/**
	 * 
	 * @param s s
	 */
	public void setCreator(String s) {  this.creator = s; }
	
	/**
	 * 
	 * @param s s
	 */
	public void setDate(String s) {  this.date = s; }
	/**
	 * 
	 * @param s s
	 */
	public void setCollection(String s) {  this.collection = s; }
	/**
	 * 
	 * @param s s
	 */
	public void setRank(String s) {  this.rank = s; }
	
	/**
	 * 
	 * @return c
	 */
	public String getCollectionName() {
		return collectionName;
	}

	/**
	 * 
	 * @param collectionName c
	 */
	public void setCollectionName(String collectionName) {
		this.collectionName = collectionName;
	}
	
	/**
	 * 
	 * @return len
	 */
	public int getLength() {
		return length;
	}

	/**
	 * 
	 * @param length len
	 */
	public void setLength(int length) {
		this.length = length;
	}
	
	/**
	 * 
	 * @param length len
	 */
	public void setLength(String length) {
		this.length = Integer.parseInt(length);
	}
}
