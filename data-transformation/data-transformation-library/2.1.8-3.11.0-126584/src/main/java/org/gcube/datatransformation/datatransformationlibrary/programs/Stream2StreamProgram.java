package org.gcube.datatransformation.datatransformationlibrary.programs;

import java.io.InputStream;
import java.util.List;

import org.gcube.datatransformation.datatransformationlibrary.dataelements.DataElement;
import org.gcube.datatransformation.datatransformationlibrary.dataelements.impl.DataElementImpl;
import org.gcube.datatransformation.datatransformationlibrary.model.ContentType;
import org.gcube.datatransformation.datatransformationlibrary.model.Parameter;

/**
 * @author Dimitris Katris, NKUA
 * <p>
 * Abstract helper class that implements the {@link Program} interface.
 * </p>
 * <p>
 * Each class that extends the <tt>Stream2StreamProgram</tt> shall implement the {@link Stream2StreamProgram#transformStream(InputStream, ContentType, List, ContentType)} method which takes as input an {@link InputStream} and returns an {@link InputStream} of the transformed content.  
 * </p>
 */
public abstract class Stream2StreamProgram extends Elm2ElmProgram {
	
	/**
	 * @see org.gcube.datatransformation.datatransformationlibrary.programs.Elm2ElmProgram#transformDataElement(org.gcube.datatransformation.datatransformationlibrary.dataelements.DataElement, java.util.List, org.gcube.datatransformation.datatransformationlibrary.model.ContentType)
	 * @param sourceDataElement The source <tt>DataElement</tt>.
	 * @param programParameters The parameters of the {@link Program}.
	 * @param targetContentType The <tt>ContentType</tt> in which the <tt>DataElement</tt> will be transformed.
	 * @return The transformed <tt>DataElement</tt>.
	 * @throws Exception If the <tt>Program</tt> is not capable to transform <tt>DataElements</tt>.
	 */
	@Override
	public DataElement transformDataElement(DataElement sourceDataElement, List<Parameter> programParameters, ContentType targetContentType) throws Exception {
		InputStream transformedcontent;
		try {
			transformedcontent = transformStream(sourceDataElement.getContent(), sourceDataElement.getContentType(), programParameters, targetContentType);
			if(transformedcontent==null)
				throw new NullPointerException();
		} catch (Exception e) {
			throw new Exception("Could not transform stream");
		}
		DataElementImpl transformedObject = DataElementImpl.getSinkDataElement(sourceDataElement);
		transformedObject.setId(sourceDataElement.getId());
		transformedObject.setContentType(targetContentType);
		transformedObject.setContent(transformedcontent);
		return transformedObject;
	}
	
	/**
	 * Transforms the <tt>content</tt> to the <tt>targetContentType</tt> and returns the transformed content as a stream.
	 * 
	 * @param content The content of the source {@link DataElement}.
	 * @param sourceContentType The <tt>ContentType</tt> of the source {@link DataElement}. 
	 * @param programParameters The parameters of the {@link Program}.
	 * @param targetContentType The <tt>ContentType</tt> in which the <tt>DataElement</tt> will be transformed.
	 * @return The transformed content
	 * @throws Exception If the <tt>Program</tt> is not capable to transform <tt>DataElements</tt>.
	 */
	public abstract InputStream transformStream(InputStream content, ContentType sourceContentType, List<Parameter> programParameters, ContentType targetContentType) throws Exception;
}
