/**
 * 
 */
package org.gcube.portlets.user.td.csvimportwidget.client.data;

import com.sencha.gxt.core.client.ValueProvider;

/**
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 *
 */
public class CSVRowValueProvider implements ValueProvider<CSVRow, String> {
	
	protected String column;

	/**
	 * @param column
	 */
	public CSVRowValueProvider(String column) {
		this.column = column;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getValue(CSVRow row) {
		return row.getField(column);
	}

	/**
	 * {@inheritDoc}
	 */
	public void setValue(CSVRow object, String value) {
		
	}

	/**
	 * {@inheritDoc}
	 */
	public String getPath() {
		return column;
	}

}
