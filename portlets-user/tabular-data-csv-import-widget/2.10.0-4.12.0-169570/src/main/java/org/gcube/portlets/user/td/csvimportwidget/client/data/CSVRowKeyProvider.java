/**
 * 
 */
package org.gcube.portlets.user.td.csvimportwidget.client.data;

import com.sencha.gxt.data.shared.ModelKeyProvider;

/**
 * @author Giancarlo Panichi
 *
 */
public class CSVRowKeyProvider implements ModelKeyProvider<CSVRow> {

	
	public String getKey(CSVRow item) {
		return item.getId();
	}

}
