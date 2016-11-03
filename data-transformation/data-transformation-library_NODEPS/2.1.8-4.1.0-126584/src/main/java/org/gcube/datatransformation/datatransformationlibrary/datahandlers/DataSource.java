package org.gcube.datatransformation.datatransformationlibrary.datahandlers;

import org.gcube.datatransformation.datatransformationlibrary.dataelements.DataElement;

/**
 * @author Dimitris Katris, NKUA
 * 
 * <p>The <tt>DataSource</tt> interface is implemented by all classes capable for fetching {@link DataElement}s from the respective source.</p>
 * 
 * <p>Caution: Sources must also define a constructor (String input, Parameter[] inputParameters).
 * This constructor is used by the service to instantiate the DataSource.</p>
 */
public interface DataSource extends DataHandler, ContentTypeDataSource {

	/**
	 * Returns true if the <tt>DataSource</tt> has more {@link DataElement}s.
	 * 
	 * @return true if the <tt>DataSource</tt> has more elements.
	 */
	public boolean hasNext();
	
	/**
	 * Returns the next element of the <tt>DataSource</tt>.
	 * 
	 * @return the next element of the <tt>DataSource</tt>.
	 */
	public DataElement next() throws Exception;

}
