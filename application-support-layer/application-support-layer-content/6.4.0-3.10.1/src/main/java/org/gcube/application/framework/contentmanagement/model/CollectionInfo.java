package org.gcube.application.framework.contentmanagement.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;
//import org.apache.axis.message.addressing.EndpointReferenceType;



/**
 * Collection's information
 * @author valia, giotak, UoA
 *
 */
public class CollectionInfo implements Serializable {

	private static final long serialVersionUID = 1L;
	protected String id;
	protected String name;
	protected String description;
	protected String reference;
	protected Vector<String> schemata;
	protected Vector<String> metadataIDs;
	protected Vector<String> languages;
//	protected HashMap<String, List<EndpointReferenceType>> forward;
	protected String creationDate;
	protected String recno;
	protected boolean isCollectionGroup;
	protected String queryTemplate;
	
//	protected Vector<HashMap<String, List<EndpointReferenceType>>> forwardVector;
	
	
	
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
	
	public String getQueryTemplate() {
		return queryTemplate;
	}
	
	public void setQueryTemplate(String qt) {
		queryTemplate = qt;
	}
	/**
	 * @param name  collection's name 
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 *  Constructor of the class
	 */
	public CollectionInfo() {
		id = "";
		name = "";
		description = "";
		reference = "";
		schemata = new Vector<String>();
		metadataIDs = new Vector<String>();
		languages = new Vector<String>();
//		forward = new HashMap<String, List<EndpointReferenceType>>();
		creationDate = "";
		recno = "";
		isCollectionGroup = false;
		
//		forwardVector = new Vector<HashMap<String, List<EndpointReferenceType>>>();
	}
	
	public  boolean isCollectionGroup() {
		return isCollectionGroup;
	}
	
	public void setCollectionGroup(boolean isGroup) {
		isCollectionGroup = isGroup;
	}
	

//	public Vector<HashMap<String, List<EndpointReferenceType>>> getForwardVector() {
//		return forwardVector;
//	}
//	
//	public void addForward (HashMap<String, List<EndpointReferenceType>> newforward) {
//		forwardVector.add(newforward);
//	}
//	
//	public HashMap<String, List<EndpointReferenceType>> getForward (int i) {
//		return forwardVector.get(i);
//	}
	
	
	/**
	 * @param i the position of the metadata schema
	 * @return the schema in the specified position.
	 */
	public String getSchema(int i) {
		return schemata.get(i);
	}

	/**
	 * @param i  the position of the metadata schema
	 * @return metadata's ID 
	 */
	public String getMetadataID(int i) {
		return metadataIDs.get(i);
	}
	
	/**
	 * @param i the position of the metadata schema
	 * @return the language for this schema
	 */
	public String getLanguage(int i) {
		return languages.get(i);
	}
	
		
	
	
	/**
	 * @param schema collection's schema name (e.g. dc, tei)
	 * @param metaID the ID of the metadata collection
	 * @param language metadata collection's language
	 * @param index the indices that collection has
	 */
	public void setMetadataCollection(String schema, String metaID, String language)
	{
		this.schemata.add(schema);
		this.metadataIDs.add(metaID);
		this.languages.add(language);
	}
	
	
	public String getCreationDate () {
		return creationDate;
	}
	
	public void setCreationDate(String date) {
		creationDate = date;
	}
	
	public String getRecno() {
		return recno;
	}
	
	public void setRecno (String recnum) {
		recno = recnum;
	}
	
	/**
	 * @return the reference to this collection (usually a url)
	 */
	public String getReference() {
		return reference;
	}
	
	/**
	 * @param reference  the reference to this collection (usually a url)
	 */
	public void setReference(String reference) {
		this.reference = reference;
	}

	/**
	 * @param schema metadata collection's schema 
	 * @return true if this collection has a corresponding metadata collection with this schema, otherwise false.
	 */
	public boolean hasSchema(String schema)
	{
		return this.schemata.contains(schema);
	}
	
	/**
	 * @param schema metadata collection's schema 
	 * @return true if this collection has a corresponding metadata collection with this schema, otherwise false.
	 */
	public int getIndexOfSchema(String schema)
	{
		return this.schemata.indexOf(schema);
	}
	
	/**
	 * @return the number of corrsponding metadata collections 
	 */
	public int getMetadataSize()
	{
		return this.metadataIDs.size();
	}
	

	/**
	 * @return the forward indices stored in  a hasmap. The key to the hasmap is collection together with the field, and the value is an EPR to the index. 
	 */
//	public HashMap<String, List<EndpointReferenceType>> getForward() {
//		return forward;
//	}
	
}
