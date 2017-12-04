/**
 * 
 */
package org.gcube.portlets.user.tdw.client.config;

import java.util.HashMap;
import java.util.Map;

import org.gcube.portlets.user.tdw.shared.model.ColumnDefinition;
import org.gcube.portlets.user.tdw.shared.model.ColumnKey;
import org.gcube.portlets.user.tdw.shared.model.DataRow;
import org.gcube.portlets.user.tdw.shared.model.TableDefinition;

import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.widget.core.client.grid.GridViewConfig;

/**
 * A bridge between the GridViewConfig and the TableViewConfig.
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 */
public class TabularDataGridViewConfig implements GridViewConfig<DataRow> {
	
	protected RowStyleProvider rowStyleProvider;
	protected Row row;
	
	public TabularDataGridViewConfig(TableViewConfig config, TableDefinition definition)
	{
		this.rowStyleProvider = config.getRowStyleProvider();
		
		Map<String, ColumnKey> keys = new HashMap<String, ColumnKey>();
		for (ColumnDefinition column:definition.getColumnsAsList()) keys.put(column.getLabel(), column.getKey());
		row = new Row(keys);
	}

	/**
	 * {@inheritDoc}
	 */
	public String getColStyle(DataRow model, ValueProvider<? super DataRow, ?> valueProvider, int rowIndex,	int colIndex) {
		return "";
	}

	/**
	 * {@inheritDoc}
	 */
	public String getRowStyle(DataRow model, int rowIndex) {
		row.setDataRow(model);
		return rowStyleProvider.getRowStyle(row);
	}

}
