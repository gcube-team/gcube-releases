package org.gcube.datatransformation.datatransformationlibrary.dataelements;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.gcube.datatransformation.datatransformationlibrary.model.ContentType;

/**
 * @author Dimitris Katris, NKUA
 * 
 * This class provides the base implementation of each <tt>DataElement</tt>.
 */
public abstract class DataElement {

	/**
	 * The id of the <tt>DataElement</tt>.
	 */
	private String id;
	
	/**
	 * The {@link ContentType} of the <tt>DataElement</tt>.
	 */
	private ContentType contentType;
	
	/** The (name, value) attribute pairs */
	protected HashMap<String, String> attributes = new HashMap<String,String>();
	
	/**
	 * Returns the id of the <tt>DataElement</tt>.
	 * 
	 * @return The id of the <tt>DataElement</tt>.
	 */
	public String getId() {
		return id;
	}
	
	/**
	 * Sets the id of the <tt>DataElement</tt>.
	 * 
	 * @param id the id of the <tt>DataElement</tt>.
	 */
	public void setId(String id) {
		this.id = id;
	}
	
	/**
	 * Returns the {@link ContentType} of the <tt>DataElement</tt>.
	 * 
	 * @return The {@link ContentType} of the <tt>DataElement</tt>.
	 */
	public ContentType getContentType() {
		return contentType;
	}
	
	/**
	 * Sets the {@link ContentType} of the <tt>DataElement</tt>.
	 * 
	 * @param contentType The {@link ContentType} of the <tt>DataElement</tt>.
	 */
	public void setContentType(ContentType contentType) {
		this.contentType = contentType;
	}
	
	/**
	 * Puts an attribute in the <tt>DataElement</tt>.
	 * 
	 * @param attrName The name of the attribute.
	 * @param attrVal The value of the attribute.
	 */
	public void setAttribute(String attrName, String attrVal) {
		this.attributes.put(attrName, attrVal);
	}

	/**
	 * Deletes an attribute of the <tt>DataElement</tt>.
	 * 
	 * @param attrName The name of the attribute which will be deleted.
	 */
	public void deleteAttribute(String attrName) {
		this.attributes.remove(attrName);
	}

	/**
	 * Returns the value of the attribute with name attrName.
	 * 
	 * @param attrName The name of the attribute whose value will be returned.
	 * @return The value of the attribute.
	 */
	public String getAttributeValue(String attrName) {
		return this.attributes.get(attrName);
	}

	/**
	 * Returns a {@link Map} which contains all the attributes of the <tt>DataElement</tt>.
	 * 
	 * @return The {@link Map} with all the attributes of the <tt>DataElement</tt>.
	 */
	public Map<String,String> getAllAttributes() {
		return attributes;
	}

	/**
	 * Abstract method which returns the content of the <tt>DataElement</tt> in a stream. 
	 * 
	 * @return The content of the <tt>DataElement</tt>.
	 */
	public abstract InputStream getContent();
	
	public abstract void destroy();

}