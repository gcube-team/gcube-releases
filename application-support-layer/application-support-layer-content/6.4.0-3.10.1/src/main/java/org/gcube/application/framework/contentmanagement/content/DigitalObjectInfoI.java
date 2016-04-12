package org.gcube.application.framework.contentmanagement.content;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;

import org.gcube.application.framework.core.session.ASLSession;

/**
 * @author Valia Tsagkalidou
 *
 */
public interface DigitalObjectInfoI {
	
	/**
	 * @return a Stream containing the object 
	 */
	public InputStream getObject(String elementType);
	
	//public InputStream getObjectNew();
	
	/**
	 * @param name the name of the Object
	 * @param collectionName the name of the collection
	 * @return a Stream containing the object
	 */
	public InputStream getObjectByName(String name, String collectionName);
	
	
	/**
	 * @return a hashmap containing the primary roles together with the corresponding Object Identifier of the related objects   
	 */
	//public HashMap<String, List<String>> getPrimaryRoles();
	
	/**
	 * @return a hashmap containing the secondary roles together with the corresponding Object Identifier of the related objects
	 */
	//public HashMap<String, List<String>> getSecondaryRoles();
	
	/**
	 * @return the available metadata schemata of the corresponding object
	 */
	public List<String> getAvailableSchemata();

	/**
	 * @return the metadata
	 */
	public String getMetadata(String schema);


	/**
	 * @return the object's Mime type
	 */
	public String getMimeType();
	
	//public String getMimeTypeNew();

	/**
	 * @return the object's length
	 */
	public long getLength();
	
	//public long getLengthNew();
	
	/**
	 * @return the object's name
	 */
	public String getName();
	
	//public String getNameNew();
	
	/**
	 * @return
	 */
	public String getHTMLrepresentation();
	
	/**
	 * @param html
	 */
	public void setHTMLrepresentation(String html);
	
	/**
	 * @param session
	 * @param rawContent
	 */
	public void updateContent(ASLSession session, byte[] rawContent);
	
	/**
	 * @param width
	 * @param height
	 * @param options
	 * @return
	 */
	public byte[] getThumbnail(int width, int height, String options);
	
	/**
	 * 
	 * @param collectionName
	 */
	public void setCollectionName (String collectionName);
	
	
	/**
	 * 
	 * @return
	 */
	public String getCollectionName();
	
	/**
	 * 
	 * @return
	 */
	public String getMetaRecord();
	
	/**
	 * 
	 * @param mr the metadata content of the metadata record
	 */
	public void setMetaRecord(String mr);
	
	
	public String getDocumentURI();
	
	public void setDocumentURI(String dURI);
	
}

