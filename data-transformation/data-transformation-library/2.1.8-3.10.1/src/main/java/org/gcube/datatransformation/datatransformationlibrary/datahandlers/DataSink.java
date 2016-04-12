package org.gcube.datatransformation.datatransformationlibrary.datahandlers;

import org.gcube.datatransformation.datatransformationlibrary.dataelements.DataElement;

/**
 * @author Dimitris Katris, NKUA
 * 
 * <p>The <tt>DataSink</tt> interface is implemented by all classes capable for storing {@link DataElement}s to the respective sink.</p>
 * 
 * <p>Caution: Sinks must also define a constructor (String output, Parameter[] outputParameters).
 * This constructor is used by the service to instantiate the DataSink.</p>
 */
public interface DataSink extends DataHandler{
	
	/**
	 * Appends the specified {@link DataElement} to this <tt>DataSink</tt>
	 * 
	 * @param element {@link DataElement} to be appended to this <tt>DataSink</tt>
	 */
	public void append(DataElement element);
	
	/**
	 * Returns the output of the transformation. 
	 * 
	 * @return The output of the transformation.
	 */
	public String getOutput();
}
