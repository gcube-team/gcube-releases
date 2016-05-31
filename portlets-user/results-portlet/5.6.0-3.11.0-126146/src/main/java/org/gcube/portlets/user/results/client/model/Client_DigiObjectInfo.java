package org.gcube.portlets.user.results.client.model;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * 
 * @author massi
 *
 */
public class Client_DigiObjectInfo implements IsSerializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * oid
	 */
	private String objectURI;
	/**
	 * object's name, title
	 */
	private String name;
	/**
	 *  object's length (in bytes)
	 */
	private long length;
	/**
	 * object's mime type
	 */
	private String mimetype;
	/**
	 * object collection id
	 */
	private String collectionID;

	/**
	 * 
	 *
	 */
	public Client_DigiObjectInfo() {
		super();
	}

	/**
	 * 
	 * @param oid object id
	 * @param belongsToOid The oid of the oid to which this Digital Object belongs to,
	 * 					if is a main object is its collectionID
	 * 					if is an alternative representation, a metadata and so on it is it's source 
	 * @param name object's name, title
	 * @param length object's length (in bytes)
	 * @param mimetype its mymetipe
	 * @param collectionID  colid
	 * @param rank  its rank
	 * @param annotationsNo amount of available annotations 
	 * @param availableMetadata available metadata
	 */
	
	public Client_DigiObjectInfo(String uri, String name,  long length, String mimetype, String collectionID) {
		super();
		this.objectURI = uri;
		this.name = name;
		this.length = length;
		this.mimetype = mimetype;
		this.collectionID = collectionID;
	}
	
	/**
	 * used to create simple DigiObjectInfo requested on demand
	 * @param oid
	 * @param name
	 * @param length
	 * @param mimetype
	 */
	public Client_DigiObjectInfo(String uri, String belongsToOid, String name, String collectionID, long length, String mimetype) {
		this.objectURI = uri;
		this.name = name;
		this.length = length;
		this.mimetype = mimetype;
		this.collectionID = collectionID;
	}
	
		
	public String getURI() {
		return objectURI;
	}

	public void setObjectURI(String uri) {
		this.objectURI = uri;
	}
	
	public String getCollectionID() {
		return collectionID;
	}

	public void setCollectionID(String collectionID) {
		this.collectionID = collectionID;
	}

	public long getLenght() {
		return length;
	}

	public void setLenght(long length) {
		this.length = length;
	}

	public String getMimetype() {
		return mimetype;
	}

	public void setMimetype(String mimetype) {
		this.mimetype = mimetype;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public long getLength() {
		return length;
	}

	public void setLength(long length) {
		this.length = length;
	}

	/**
	 * printable version of Digital Object
	 */
	public String toString() {

		String toReturn = "DIGITAL OBJECT: URI:" + objectURI;
		
		toReturn += "\nlength: " + length;
		toReturn += (name == null) ? "name: " : "name: " + name;
		toReturn += (mimetype == null) ? "\nmimetype: " : "\nmimetype: " + mimetype;

		return toReturn;
	}
}
