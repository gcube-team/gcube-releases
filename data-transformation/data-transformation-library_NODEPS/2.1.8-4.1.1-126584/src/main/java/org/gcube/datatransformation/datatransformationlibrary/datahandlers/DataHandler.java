package org.gcube.datatransformation.datatransformationlibrary.datahandlers;

import org.gcube.datatransformation.datatransformationlibrary.dataelements.DataElement;

/**
 * @author Dimitris Katris, NKUA
 * 
 * <tt>DataHandler</tt> are classes responsible to fetch or store {@link DataElement}s.
 */
public interface DataHandler {
	/**
	 * Closes this <tt>DataHandler</tt> and releases any resources associated with it.
	 */
	public void close();
	
	/**
	 * Returns the closed state of the <tt>DataHandler</tt>. 
	 * 
	 * @return true if the <tt>DataHandler</tt> has been closed.
	 */
	public boolean isClosed();
}
