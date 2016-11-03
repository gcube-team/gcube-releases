package org.gcube.common.core.informationsystem.publisher;

import org.w3c.dom.Document;

/**
 * Information System resource
 * 
 * @author Manuele Simi (ISTI-CNR)
 *
 */
public interface ISResource {
	
	public enum ISRESOURCETYPE {PROFILE, RPD, WSDAIX};
	
    /**
     * Sets the resource name
     * @param documentName the resource name
     */
	public void setName(String name);
	
	
	/**
     * Sets the resource type
     * @param type the resource type
     */
	public void setType(ISRESOURCETYPE type);
	
	
	/**
     * Gets the resource type
     */
	public ISRESOURCETYPE getType();
	
	/**
	 * Gets the resource name
	 * @return the resource name
	 */
	public String getName();

	/**
	 * The payload of the resource
	 * @param document the document
	 * @throws InvalidISResourceParameterException
	 */
	public void setDocument(Document document);
	
	/**
	 * Gets the payload of the resource 
	 * @return the document
	 */
	public Document getDocument();
	
	/**
	 * The collection in which the resource is registered. 
	 * The name could be also a path where tokens are separated by a slash (e.g. mycollection/mychild)
	 * @param collection the collection name	
	 */
	public void setCollection(String collection);
	
	/**
	 * Gets the collection in which the resource is registered.
	 * @return the collection full path
	 */
	public String getCollection();
	
	/**
	 * The identifier assigned to the resource 
	 * @param id the identifier
	 */
	public void setID(String id);
	
	/**
	 * Gets the identifier assigned to the resource 
	 */
	public String getID();	
	
}
