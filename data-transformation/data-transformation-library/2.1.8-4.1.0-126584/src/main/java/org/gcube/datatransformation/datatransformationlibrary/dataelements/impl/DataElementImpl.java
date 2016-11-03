package org.gcube.datatransformation.datatransformationlibrary.dataelements.impl;

import java.io.IOException;
import java.io.InputStream;

import org.gcube.datatransformation.datatransformationlibrary.dataelements.DataElement;

/**
 * @author Dimitris Katris, NKUA
 *
 * Generic implementation of {@link DataElement} class. Content is an {@link InputStream}.
 */
public class DataElementImpl extends DataElement {
	
	/**
	 * Returns a new <tt>DataElementImpl</tt>.
	 * 
	 * @return The new <tt>DataElementImpl</tt>.
	 */
	public static DataElementImpl getSourceDataElement(){
		return new DataElementImpl();
	}
	
	/**
	 * Returns a new <tt>DataElementImpl</tt> which contains all the attributes taken from the source {@link DataElement}.
	 * 
	 * @param sourceDataElement The {@link DataElement} from which the new one inherits all attributes.
	 * @return The new <tt>DataElementImpl</tt>.
	 */
	public static DataElementImpl getSinkDataElement(DataElement sourceDataElement){
		DataElementImpl targetDataElement = new DataElementImpl();
		
		/* Add every attribute of the source element to the new element */
		for (String attrName : sourceDataElement.getAllAttributes().keySet())
			targetDataElement.setAttribute(attrName, sourceDataElement.getAttributeValue(attrName));
		
		return targetDataElement;
	}
	
	/**
	 * <tt>DataElementImpl</tt> instances have to be taken from the {@link DataElementImpl#getSinkDataElement(DataElement)} or {@link DataElementImpl#getSourceDataElement()} methods.
	 */
	private DataElementImpl(){}
	
	/**
	 * The content of the {@link DataElement}.
	 */
	private InputStream content;

	/**
	 * @see org.gcube.datatransformation.datatransformationlibrary.dataelements.DataElement#getContent()
	 * @return The content of the DataElement.
	 */
	@Override
	public InputStream getContent() {
		return content;
	}
	
	/**
	 * Sets the content of the {@link DataElement}.
	 * 
	 * @param content The content of the {@link DataElement}.
	 */
	public void setContent(InputStream content) {
		this.content = content;
	}

	@Override
	public void destroy() {
		try {
			content.close();
			content = null;
		} catch (IOException e) {
		}
	}
}
