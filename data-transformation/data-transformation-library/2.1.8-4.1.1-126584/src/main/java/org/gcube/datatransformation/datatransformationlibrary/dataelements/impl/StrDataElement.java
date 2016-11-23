package org.gcube.datatransformation.datatransformationlibrary.dataelements.impl;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;

import org.gcube.datatransformation.datatransformationlibrary.dataelements.DataElement;

/**
 * @author Dimitris Katris, NKUA
 * 
 * Implementation of {@link DataElement} class in which the content is kept in a {@link String}.
 */
public class StrDataElement extends DataElement implements Serializable {

	/**
	 * The universal version identifier for this {@link Serializable} class.
	 */
	private static final long serialVersionUID = -4094794672646197311L;

	/**
	 * Returns a new <tt>StrDataElement</tt> instance.
	 * 
	 * @return The new <tt>StrDataElement</tt> instance.
	 */
	public static StrDataElement getSourceDataElement(){
		return new StrDataElement();
	}
	
	/**
	 * Returns a new <tt>StrDataElement</tt> which contains all the attributes taken from the source {@link DataElement}.
	 * 
	 * @param sourceDataElement The {@link DataElement} from which the new one inherits all attributes.
	 * @return The new <tt>StrDataElement</tt> instance.
	 */
	public static StrDataElement getSinkDataElement(DataElement sourceDataElement){
		StrDataElement targetDataElement = new StrDataElement();
		
		/* Add every attribute of the source element to the new element */
		for (String attrName : sourceDataElement.getAllAttributes().keySet())
			targetDataElement.setAttribute(attrName, sourceDataElement.getAttributeValue(attrName));
		
		return targetDataElement;
	}
	
	/**
	 * <tt>StrDataElement</tt> instances have to be taken from the {@link StrDataElement#getSinkDataElement(DataElement)} or {@link StrDataElement#getSourceDataElement()} methods.
	 */
	private StrDataElement(){}
	
	/**
	 * The content of the {@link DataElement}.
	 */
	private String content;
	
	/**
	 * Sets the content of the {@link DataElement}.
	 * 
	 * @param content The content of the {@link DataElement}.
	 */
	public void setContent(String content){
		this.content=content;
	}
	
	/**
	 * @see org.gcube.datatransformation.datatransformationlibrary.dataelements.DataElement#getContent()
	 * @return The content of the <tt>DataElement</tt>.
	 */
	@Override
	public InputStream getContent() {
		return new ByteArrayInputStream(content.getBytes());
	}
	
	/**
	 * Returns the content as String.
	 * 
	 * @return The content as String.
	 */
	public String getStringContent(){
		return content;
	}

	@Override
	public void destroy() {
		content = null;
	}

}
