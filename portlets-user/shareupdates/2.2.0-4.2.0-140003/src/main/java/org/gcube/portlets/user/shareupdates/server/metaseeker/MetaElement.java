package org.gcube.portlets.user.shareupdates.server.metaseeker;

import java.net.URL;

/**
 * Represents OpenGraph enabled meta data for a specific document
 * @author Callum Jones
 */
public class MetaElement
{
	private String property;
	private String content;
	
	/**
	 * Construct the representation of an element
	 * @param property The property key
	 * @param content The content or value of this element
	 */
	public MetaElement(String property, String content)
	{
		this.property = property;
		this.content = content;
	}
	
	/**
	 * Fetch the content string of the element
	 */
	public String getContent()
	{
		return content;
	}
	
	/**
	 * Fetch the property of the element
	 */
	public String getProperty()
	{
		return property;
	}	
}