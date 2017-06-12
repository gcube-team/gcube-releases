/**
 * 
 */
package org.gcube.portlets.user.tdwx.client.config;

import java.util.ArrayList;

import org.gcube.portlets.user.tdwx.shared.model.ColumnDefinition;
import org.gcube.portlets.user.tdwx.shared.model.DataRow;

import com.sencha.gxt.core.client.ValueProvider;

/**
 * Provides the CSS styles to apply to the provided row.
 * 
 * @author "Giancarlo Panichi"
 * email: <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public interface RowStyleProvider {
	
	
	/**
	 *  
	 * @param row the row where to apply the style. Don't store the object, it will be reused in successive calls.
	 * @param tableDefinition table definition.
	 * @return the CSS name style for the specified row, multiple names styles can be specified, each separate by space.
	 */
	public String getRowStyle(Row row, ArrayList<ColumnDefinition> validationColumns);
	
	/**
	 * 
	 * @param row
	 * @param tableDefinition
	 * @return
	 */
	public String getColStyle(Row row, ArrayList<ColumnDefinition> validationColumns,
			ValueProvider<? super DataRow, ?> valueProvider, int rowIndex,	int colIndex);

}
