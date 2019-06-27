/**
 * 
 */
package org.gcube.portlets.user.tdwx.client.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.gcube.portlets.user.tdwx.shared.model.ColumnDefinition;
import org.gcube.portlets.user.tdwx.shared.model.ColumnKey;
import org.gcube.portlets.user.tdwx.shared.model.ColumnType;
import org.gcube.portlets.user.tdwx.shared.model.DataRow;
import org.gcube.portlets.user.tdwx.shared.model.TableDefinition;

import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.widget.core.client.grid.GridViewConfig;

/**
 * 
 * @author "Giancarlo Panichi" <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 * 
 *         A bridge between the GridViewConfig and the TableViewConfig.
 */
public class TabularDataGridViewConfig implements GridViewConfig<DataRow> {

	protected RowStyleProvider rowStyleProvider;
	protected Row row;
	protected TableDefinition tableDefinition;
	protected ArrayList<ColumnDefinition> validationColumns;

	public TabularDataGridViewConfig(TableViewConfig config,
			TableDefinition definition) {
		this.rowStyleProvider = config.getRowStyleProvider();
		this.tableDefinition = definition;
		validationColumns=new ArrayList<ColumnDefinition>();
		Map<String, ColumnKey> keys = new HashMap<String, ColumnKey>();
		
		for (ColumnDefinition column : definition.getColumnsAsList()) {
			keys.put(column.getColumnLocalId(), column.getKey());
			if (column.getType() == ColumnType.VALIDATION){
				validationColumns.add(column);
			} 
		}
		row = new Row(keys);
		
	}

	/**
	 * {@inheritDoc}
	 */
	public String getColStyle(DataRow model,
			ValueProvider<? super DataRow, ?> valueProvider, int rowIndex,
			int colIndex) {
		row.setDataRow(model);
		return rowStyleProvider.getColStyle(row, validationColumns, valueProvider,
				rowIndex, colIndex);
	}

	/**
	 * {@inheritDoc}
	 */
	public String getRowStyle(DataRow model, int rowIndex) {
		row.setDataRow(model);
		return rowStyleProvider.getRowStyle(row, validationColumns);
	}

}
