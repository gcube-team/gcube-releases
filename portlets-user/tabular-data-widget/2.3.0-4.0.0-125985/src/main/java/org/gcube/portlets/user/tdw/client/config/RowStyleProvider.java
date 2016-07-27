/**
 * 
 */
package org.gcube.portlets.user.tdw.client.config;

/**
 * Provides the CSS styles to apply to the provided row.
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 */
public interface RowStyleProvider {
	
	/**
	 * Returns the CSS style for the specified row.
	 * Multiple styles can be specified, each separate by space.
	 * @param row the row where to apply the style. Don't store the object, it will be reused in successive calls.
	 * @return the style/s to apply, empty {@link String} if no style have to be applied.
	 */
	public String getRowStyle(Row row);

}
