/**
 * 
 */
package org.gcube.portlets.user.td.csvimportwidget.client.data;

import com.sencha.gxt.core.client.ValueProvider;

/**
 * 
 * @author Giancarlo Panichi
 *
 */
public class CSVRowValueProvider implements ValueProvider<CSVRow, String> {
	
	protected String column;

	public CSVRowValueProvider(String column) {
		this.column = column;
	}

	public String getValue(CSVRow row) {
		return row.getField(column);
	}

	public void setValue(CSVRow object, String value) {
		
	}

	public String getPath() {
		return column;
	}

}
