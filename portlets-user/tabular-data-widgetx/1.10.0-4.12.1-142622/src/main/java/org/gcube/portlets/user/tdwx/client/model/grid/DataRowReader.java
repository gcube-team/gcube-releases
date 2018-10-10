/**
 * 
 */
package org.gcube.portlets.user.tdwx.client.model.grid;

import java.util.List;

import org.gcube.portlets.user.tdwx.client.model.json.JSonTable;
import org.gcube.portlets.user.tdwx.client.model.json.JSonValue;
import org.gcube.portlets.user.tdwx.shared.model.DataRow;
import org.gcube.portlets.user.tdwx.shared.model.TableDefinition;

import com.google.gwt.core.client.JsArray;
import com.sencha.gxt.data.shared.loader.DataReader;
import com.sencha.gxt.data.shared.loader.ListLoadResult;
import com.sencha.gxt.data.shared.loader.ListLoadResultBean;

/**
 * 
 * @author "Giancarlo Panichi" 
 * <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public class DataRowReader implements DataReader<ListLoadResult<DataRow>, String> {
	
	protected TableDefinition definition;
	protected JSonValueConverter converter;
	
	/**
	 * @param field
	 */
	public DataRowReader(TableDefinition definition) {
		this.definition = definition;
		converter = new JSonValueConverter(definition.getColumnsAsList());
	}

	/**
	 * {@inheritDoc}
	 */
	public ListLoadResult<DataRow> read(Object loadConfig, String data) {
		JSonTable jSonData = JSonTable.getJSonTable(data);
		JsArray<JSonValue> array = jSonData.getRows(definition.getJsonRowsField());
		List<DataRow> rows = converter.convertToDataRow(array);
		return new ListLoadResultBean<DataRow>(rows);
	}

}
