package org.gcube.common.core.informationsystem.client;

/**
 * An input stream for IS results
 * 
 * @author manuele
 *
 * @param <RESULT> the type of the expected results.
 */
public interface ISInputStream<RESULT> extends Iterable<RESULT> {
	
	
	/**
     * Tests if this stream has no elements.
     *
     * @return  <tt>true</tt> if this list has no elements;
     *          <tt>false</tt> otherwise.
     */
	public boolean isEmpty();
		
	
	/**
	 * Closes this stream and releases any system resources associated with the stream
	 */
	public void close();
	
}
