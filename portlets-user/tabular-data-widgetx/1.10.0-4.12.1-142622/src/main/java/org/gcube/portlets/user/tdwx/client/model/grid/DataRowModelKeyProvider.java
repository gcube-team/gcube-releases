/**
 * 
 */
package org.gcube.portlets.user.tdwx.client.model.grid;

import org.gcube.portlets.user.tdwx.shared.model.ColumnKey;
import org.gcube.portlets.user.tdwx.shared.model.DataRow;

import com.sencha.gxt.data.shared.ModelKeyProvider;

/**
 * 
 * @author "Giancarlo Panichi" 
 * <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public class DataRowModelKeyProvider implements ModelKeyProvider<DataRow> {

	protected ColumnKey key;	
	
	public DataRowModelKeyProvider(ColumnKey key) {
		this.key = key;
	}

	
	public String getKey(DataRow row) {
		return String.valueOf(row.get(key));
	}

}
