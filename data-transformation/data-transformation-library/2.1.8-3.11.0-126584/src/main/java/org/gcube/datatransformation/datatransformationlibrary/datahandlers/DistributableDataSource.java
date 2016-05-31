package org.gcube.datatransformation.datatransformationlibrary.datahandlers;

import org.gcube.datatransformation.datatransformationlibrary.dataelements.DataElement;
import org.gcube.datatransformation.datatransformationlibrary.model.Parameter;

/**
 * @author Dimitris Katris, NKUA
 * 
 * <tt>DistributableDataSource</tt> is the interface implemented by {@link DataSource}s whose {@link DataElement}s are able to be distributed in workers.
 */
public interface DistributableDataSource {

	/**
	 * Returns a {@link DataElement} instance from its id.
	 * 
	 * @param dataElementID The id of the {@link DataElement}.
	 * @return The {@link DataElement} instance.
	 * @throws Exception If an error occurred in getting the {@link DataElement}.
	 */
	public DataElement getDataElement(String dataElementID) throws Exception;
	
	/**
	 * Returns the next {@link DataElement} id which will be send to the worker.
	 * 
	 * @return The next data element id. 
	 * @throws Exception If the <tt>DistributableDataSource</tt> did not manage to get another data element id.
	 */
	public String getNextDataElementID() throws Exception;
	
	/**
	 * Returns true if there is another {@link DataElement} id available.
	 * 
	 * @return true if there is another {@link DataElement} id available.
	 */
	public boolean hasNext();
	
	/**
	 * Initializes the <tt>DistributableDataSource</tt>.
	 * 
	 * @param input The input of the <tt>DistributableDataSource</tt>
	 * @param inputParameters Any input parameters required by the <tt>DistributableDataSource</tt>.
	 * @throws Exception If the <tt>DistributableDataSource</tt> could not be initialized.
	 */
	public void initializeDistributableDataSource(String input, Parameter[] inputParameters) throws Exception ;
}
