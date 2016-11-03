package org.gcube.datatransformation.datatransformationlibrary.dataelements;

import java.io.InputStream;
import java.util.List;

/**
 * @author Dimitris Katris, NKUA
 * <p>
 * Abstract class representing a multipart data element.
 * </p>
 */
public abstract class CompoundDataElement extends DataElement {

	/**
	 * @see org.gcube.datatransformation.datatransformationlibrary.dataelements.DataElement#getContent()
	 * @return null
	 */
	@Override
	public InputStream getContent() {return null;}

	/**
	 * @return The parts of the data element.
	 */
	public abstract List<DataElement> getParts();
}
