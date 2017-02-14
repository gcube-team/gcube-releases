/**
 * 
 */
package org.gcube.data.spd.obisplugin;

/**
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 *
 */
public interface Writer<T> {
	
	/**
	 * Writes the specified element.
	 * @param item the item to write.
	 * @return <code>false</code> if the writer has been closed and no more elements are accepted.
	 */
	public boolean write(T item);

}
