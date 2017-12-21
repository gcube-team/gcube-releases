/**
 * 
 */
package org.gcube.portlets.user.td.csvimportwidget.client.data;

import com.sencha.gxt.data.shared.ModelKeyProvider;

/**
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 *
 */
public class CSVRowKeyProvider implements ModelKeyProvider<CSVRow> {

	/**
	 * {@inheritDoc}
	 */
	public String getKey(CSVRow item) {
		return item.getId();
	}

}
