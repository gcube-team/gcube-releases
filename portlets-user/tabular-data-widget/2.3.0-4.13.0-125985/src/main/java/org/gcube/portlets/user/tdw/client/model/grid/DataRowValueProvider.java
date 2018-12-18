/**
 * 
 */
package org.gcube.portlets.user.tdw.client.model.grid;

import org.gcube.portlets.user.tdw.shared.model.ColumnKey;
import org.gcube.portlets.user.tdw.shared.model.DataRow;

import com.sencha.gxt.core.client.ValueProvider;

/**
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 *
 */
public class DataRowValueProvider<V> implements ValueProvider<DataRow, V> {

	protected ColumnKey key;

	public DataRowValueProvider(ColumnKey key) {
		this.key = key;
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public V getValue(DataRow row) {
		return (V)row.get(key);
	}

	/**
	 * {@inheritDoc}
	 */
	public void setValue(DataRow row, V value) {
		row.set(key, value);
	}

	/**
	 * {@inheritDoc}
	 */
	public String getPath() {
		return key.getJSonIndex();
	}

}
