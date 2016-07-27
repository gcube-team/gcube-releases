package org.gcube.portlets.user.shareupdates.server.opengraph;

import java.net.URL;

/**
 * Represents OpenGraph enabled meta data for a specific document
 * @author Callum Jones
 */
public class MetaElement
{
	private OpenGraphNamespace namespace; //either "og" an NS specific
	private String property;
	private String content;
	
	/**
	 * Construct the representation of an element
	 * @param namespace The namespace the element belongs to
	 * @param property The property key
	 * @param content The content or value of this element
	 */
	public MetaElement(OpenGraphNamespace namespace, String property, String content)
	{
		this.namespace = namespace;
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
	 * Fetch the OpenGraph namespace
	 */
	public OpenGraphNamespace getNamespace()
	{
		return namespace;
	}
	
	/**
	 * Fetch the property of the element
	 */
	public String getProperty()
	{
		return property;
	}	
}