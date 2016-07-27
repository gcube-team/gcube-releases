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
	
	/**
	 * @param name the name of the Object
	 * @param collectionName the name of the collection
	 * @return a Stream containing the object
	 */
	public InputStream getObjectByName(String name, String collectionName);
	
	
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
	
	/**
	 * @return the object's length
	 */
	public long getLength();
	
	/**
	 * @return the object's name
	 */
	public String getName();
	
	public String getHTMLrepresentation();
	
	public void setHTMLrepresentation(String html);
	
	public void updateContent(ASLSession session, byte[] rawContent);
	
	public byte[] getThumbnail(int width, int height, String options);
	
	public void setCollectionName (String collectionName);
	
	
	public String getCollectionName();
	
	public String getMetaRecord();
	
	/**
	 * 
	 * @param mr the metadata content of the metadata record
	 */
	public void setMetaRecord(String mr);
	
	
	public String getDocumentURI();
	
	public void setDocumentURI(String dURI);
	
}

