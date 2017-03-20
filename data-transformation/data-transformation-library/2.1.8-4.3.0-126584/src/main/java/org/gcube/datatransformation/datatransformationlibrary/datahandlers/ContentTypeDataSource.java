package org.gcube.datatransformation.datatransformationlibrary.datahandlers;

import org.gcube.datatransformation.datatransformationlibrary.dataelements.DataElement;
import org.gcube.datatransformation.datatransformationlibrary.model.ContentType;

/**
 * @author john.gerbesiotis
 *         <p>
 *         The {@link ContentTypeDataSource} interface is implemented by those
 *         {@link DataSource} classes capable of fetching the
 *         {@link ContentType} of their {@link DataElement}'s
 *         </p>
 */
public interface ContentTypeDataSource {
	/**
	 * Returns the next element's {@link ContentType}
	 * 
	 * @return the next element's {@link ContentType}
	 */
	public ContentType nextContentType();
	
	/**
	 * Returns true if the {@link DataSource} has more {@link DataElement}s.
	 * 
	 * @return true if the {@link DataSource} has more elements.
	 */
	public boolean hasNext();
}
