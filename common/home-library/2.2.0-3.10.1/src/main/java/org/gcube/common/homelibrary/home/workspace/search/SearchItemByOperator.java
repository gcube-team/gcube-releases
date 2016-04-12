/**
 * 
 */
package org.gcube.common.homelibrary.home.workspace.search;

/**
 * @author valentina
 *
 */
public interface SearchItemByOperator {
	
	/**
	 * Get operator
	 * @return operator
	 */
	public String getOperator();
	
	/**
	 * Get value
	 * @return 
	 * @return value
	 */
	public Object getValue();

	/**
	 * @return
	 */
	public String getMin();

	/**
	 * @return
	 */
	public String getMax();

	
}
