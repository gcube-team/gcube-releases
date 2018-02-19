/**
 * 
 */
package org.gcube.portlets.user.tdw.client.model.grid;

import org.gcube.portlets.user.tdw.shared.model.ColumnKey;
import org.gcube.portlets.user.tdw.shared.model.DataRow;

import com.sencha.gxt.data.shared.ModelKeyProvider;

/**
 * @author "Federico De Faveri defaveri@isti.cnr.it"
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
