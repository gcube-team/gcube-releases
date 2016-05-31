/**
 * 
 */
package org.gcube.dataaccess.spd.havingengine;

/**
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 *
 * @param <T> the type of the element checked.
 */
public interface HavingStatement<T> {
	
	/**
	 * Checks if the passed element can be accepted.
	 * @param element the element to check.
	 * @return <code>true</code> if it is accepted, <code>false</code> otherwise.
	 */
	public boolean accept(T element);

}
