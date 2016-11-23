package org.gcube.datatransformation.datatransformationlibrary.dataelements.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;

import javax.activation.MimetypesFileTypeMap;
import javax.activation.URLDataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.gcube.datatransformation.datatransformationlibrary.dataelements.DataElement;
import org.gcube.datatransformation.datatransformationlibrary.model.ContentType;

/**
 * @author Dimitris Katris, NKUA
 *
 * <p>
 * <tt>DataElement</tt> whose content is accessible from a url connection.
 * </p>
 */
public class URLDataElement extends DataElement implements Serializable {
	
	private static final long serialVersionUID = 1712943374738652846L;
	
	private static Logger log = LoggerFactory.getLogger(URLDataElement.class);
	
	/**
	 * Instantiates and returns a new <tt>URLDataElement</tt>.
	 * @return The new <tt>URLDataElement</tt>.
	 */
	public static URLDataElement getSourceDataElement(){
		return new URLDataElement();
	}
	
	/**
	 * Instantiates a new <tt>URLDataElement</tt>.
	 * @param url The url with the content.
	 * @throws Exception If the url is not valid.
	 */
	public URLDataElement(String url) throws Exception {
		this.url = new URL(url);
		setId(url);//TODO: Create random maybe?
	}
	
//	public static URLDataElement getSinkDataElement(DataElement sourceDataElement){
//		URLDataElement targetDataElement = new URLDataElement();
//		
//		/* Add every attribute of the source element to the new element */
//		for (String attrName : sourceDataElement.getAllAttributes().keySet())
//			targetDataElement.setAttribute(attrName, sourceDataElement.getAttributeValue(attrName));
//		
//		return targetDataElement;
//	}
	
	private URLDataElement(){}
	
	/**
	 * @see org.gcube.datatransformation.datatransformationlibrary.dataelements.DataElement#getContent()
	 * @return The content of the <tt>DataElement</tt>.
	 */
	@Override
	public InputStream getContent() {
		try {
			return url.openStream();
		} catch (IOException e) {
			log.error("Could not open stream of the object with url "+url.toString());
		}
		return null;
	}
	
	private URL url;

	/**
	 * Sets the content of the <tt>URLDataElement</tt>.
	 * @param url The content of the <tt>URLDataElement</tt>.
	 */
	public void setContent(URL url){
		this.url=url;
	}
	
	/**
	 * @see org.gcube.datatransformation.datatransformationlibrary.dataelements.DataElement#getContentType()
	 * @return The <tt>ContentType</tt> of the <tt>URLDataElement</tt>.
	 */
	@Override
	public ContentType getContentType(){
		if(super.getContentType()==null){
			URLDataSource urlDS = new URLDataSource(url);
			ContentType contentFormat = new ContentType();
			String contentTypeEvalueated = urlDS.getContentType();
			log.trace("The content type of "+url+" is "+contentTypeEvalueated);
			if (contentTypeEvalueated.equalsIgnoreCase("application/octet-stream")){
				log.trace("The content type of "+url+" was not detected properly");
				contentTypeEvalueated = new MimetypesFileTypeMap().getContentType(url.toString());
				log.trace("The content type of "+url+" is reset to be "+contentTypeEvalueated);
			}
			contentFormat.setMimeType(contentTypeEvalueated);
			setContentType(contentFormat);
		}
		return super.getContentType();
	}

	@Override
	public void destroy() {
		url = null;
	}
}
