package org.gcube.datatransformation.datatransformationlibrary.dataelements.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import javax.activation.URLDataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.gcube.datatransformation.datatransformationlibrary.dataelements.DataElement;
import org.gcube.datatransformation.datatransformationlibrary.model.ContentType;

/**
 * @author Dimitris Katris, NKUA
 *
 * Implementation of {@link DataElement} class in which the content is kept in a local file.
 */
public class LocalFileDataElement extends DataElement {

	/**
	 * Logs operations performed by <tt>LocalFileDataElement</tt> class.
	 */
	private static Logger log = LoggerFactory.getLogger(LocalFileDataElement.class);
	
	/**
	 * The local file.
	 */
	private File file;
	
	/**
	 * Sets the file which contains the content of the {@link DataElement}.
	 * 
	 * @param file The content of the {@link DataElement}.
	 */
	public void setContent(File file){
		this.file=file;
	}
	
	/**
	 * @see org.gcube.datatransformation.datatransformationlibrary.dataelements.DataElement#getContent()
	 * @return The content of the DataElement.
	 */
	@Override
	public InputStream getContent() {
		try {
			return new FileInputStream(file);
		} catch (FileNotFoundException e) {
			log.error("File "+file.getAbsolutePath()+" could not be found", e);
			return null;
		}
	}
	
	/**
	 * Returns the content as file.
	 * 
	 * @return The content as file.
	 */
	public File getFileContent(){
		return file;
	}

	/**
	 * @see org.gcube.datatransformation.datatransformationlibrary.dataelements.DataElement#getContentType()
	 * @return The ContentType of the DataElement.
	 */
	@Override
	public ContentType getContentType(){
		if(super.getContentType()==null){
			try {
				URLDataSource urlDS = new URLDataSource(file.toURL());
				ContentType contentFormat = new ContentType();
				String contentTypeEvalueated = urlDS.getContentType();
				log.trace("The content type of "+file.toURL()+" is "+contentTypeEvalueated);
				contentFormat.setMimeType(contentTypeEvalueated);
				setContentType(contentFormat);
			} catch (Exception e) {
				log.error("Did not manage to evaluate content format of file "+file);
			}
		}
		return super.getContentType();
	}
	
	/**
	 * Returns a new <tt>LocalFileDataElement</tt>.
	 * 
	 * @return The new <tt>LocalFileDataElement</tt>.
	 */
	public static LocalFileDataElement getSourceDataElement(){
		return new LocalFileDataElement();
	}
	
	/**
	 * Returns a new <tt>LocalFileDataElement</tt> which contains all the attributes taken from the source {@link DataElement}.
	 * 
	 * @param sourceDataElement The {@link DataElement} from which the new one inherits all attributes.
	 * @return The new <tt>LocalFileDataElement</tt>.
	 */
	public static LocalFileDataElement getSinkDataElement(DataElement sourceDataElement){
		LocalFileDataElement targetDataElement = new LocalFileDataElement();
		
		/* Add every attribute of the source element to the new element */
		for (String attrName : sourceDataElement.getAllAttributes().keySet())
			targetDataElement.setAttribute(attrName, sourceDataElement.getAttributeValue(attrName));
		
		return targetDataElement;
	}

	@Override
	public void destroy() {
		try {
//			file.setLastModified(0);
			if(file.exists()){
				file.delete();
			}
		} catch (Exception e) {
		}
	}
}
