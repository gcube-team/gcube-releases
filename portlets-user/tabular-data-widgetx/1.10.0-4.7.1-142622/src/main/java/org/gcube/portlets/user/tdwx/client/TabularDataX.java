/**
 * 
 */
package org.gcube.portlets.user.tdwx.client;

import org.gcube.portlets.user.tdwx.client.event.CloseTableEvent;
import org.gcube.portlets.user.tdwx.client.event.FailureEvent;
import org.gcube.portlets.user.tdwx.client.event.FailureEvent.FailureEventHandler;
import org.gcube.portlets.user.tdwx.client.event.GridReadyEvent;
import org.gcube.portlets.user.tdwx.client.event.GridReadyEvent.GridReadyEventHandler;
import org.gcube.portlets.user.tdwx.client.event.OpenTableEvent;
import org.gcube.portlets.user.tdwx.server.datasource.DataSourceXFactory;
import org.gcube.portlets.user.tdwx.shared.model.TableDefinition;
import org.gcube.portlets.user.tdwx.shared.model.TableId;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * 
 * @author "Giancarlo Panichi" <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 * 
 *         Master class that contains controller and grid
 */
public class TabularDataX {

	private static int seed = 0;

	private int tdSessionId;
	private String defaultDataSourceFactoryName;
	private EventBus eventBus;
	private TabularDataXController controller;
	private TabularDataXGridPanel gridPanel;

	/**
	 * Creates a new {@link TabularDataX object setting the default {
	 * @link DataSourceXFactory} name.
	 * 
	 * @param defaultDataSourceFactoryName
	 *            the default {@link DataSourceXFactory} name.
	 */
	public TabularDataX(String defaultDataSourceFactoryName) {
		this.tdSessionId = seed++;
		this.defaultDataSourceFactoryName = defaultDataSourceFactoryName;
		eventBus = new SimpleEventBus();
		controller = new TabularDataXController(tdSessionId, eventBus);
	}

	/**
	 * Creates a new {@link TabularDataX} object.
	 */
	public TabularDataX() {
		this(null);
	}

	/**
	 * @return the defaultDataSourceFactoryName
	 */
	public String getDefaultDataSourceFactoryName() {
		return defaultDataSourceFactoryName;
	}

	/**
	 * @param defaultDataSourceFactoryName
	 *            the defaultDataSourceFactoryName to set
	 */
	public void setDefaultDataSourceFactoryName(
			String defaultDataSourceFactoryName) {
		this.defaultDataSourceFactoryName = defaultDataSourceFactoryName;
	}

	public TabularDataXGridPanel getGridPanel() {
		if (gridPanel == null) {
			gridPanel = new TabularDataXGridPanel(tdSessionId, eventBus);
		}
		return gridPanel;
	}

	/**
	 * Add a new {@link FailureEventHandler}.
	 * 
	 * @param handler
	 */
	public void addFailureHandler(FailureEventHandler handler) {
		eventBus.addHandler(FailureEvent.TYPE, handler);
	}

	/**
	 * Add a new {@link FailureEventHandler}.
	 * 
	 * @param handler
	 */
	public void addGridReadyHandler(GridReadyEventHandler handler) {
		eventBus.addHandler(GridReadyEvent.TYPE, handler);
	}

	/**
	 * Opens a new table.
	 * 
	 * @param id
	 *            the table id.
	 */
	public void openTable(TableId id) {
		Log.trace("openTable id: " + id);
		eventBus.fireEvent(new OpenTableEvent(id));
	}

	/**
	 * Opens a new table. The default {@link DataSourceXFactory} name is used.
	 * 
	 * @param tableKey
	 *            the table key.
	 */
	public void openTable(String tableKey) {
		Log.trace("openTable tableKey: " + tableKey);
		TableId tableId = getTableId(tableKey);
		eventBus.fireEvent(new OpenTableEvent(tableId));
	}

	/**
	 * Returns the current table definition.
	 * 
	 * @return the table description, <code>null</code> if no table is open.
	 */
	public TableDefinition getCurrentTable() {
		return controller.getCurrentTable();
	}

	/**
	 * Returns the {@link TableDefinition} for the specified {@link TableId}.
	 * 
	 * @param tableId
	 *            the table id.
	 * @param callback
	 *            the {@link AsyncCallback} called when the
	 *            {@link TableDefinition} is retrieved.
	 */
	public void getTableDefinition(TableId tableId,
			AsyncCallback<TableDefinition> callback) {
		controller.getTableDefinition(tableId, callback);
	}

	public void getTableDefinition(String tableKey,
			AsyncCallback<TableDefinition> callback) {
		TableId tableId = getTableId(tableKey);
		controller.getTableDefinition(tableId, callback);
	}

	public void closeTable() {
		Log.trace("closeTable");
		eventBus.fireEvent(new CloseTableEvent());
	}

	protected TableId getTableId(String tableKey) {
		if (defaultDataSourceFactoryName == null) {
			Log.error("No default DataSourceFactoryName specified");
			throw new IllegalArgumentException(
					"No default DataSourceFactoryName specified");
		}
		return new TableId(defaultDataSourceFactoryName, tableKey);
	}

	/*
	 * 
	 * Disabled direct grid Add Rows on Tabular Resources
	 * 
	 * public void addRow(ArrayList<String> rows) { if (gridPanel != null) {
	 * gridPanel.addRow(rows); } }
	 */

	/**
	 * 
	 * @param i
	 *            index of column in ColumnModel
	 * @return id of column and equals to column name on service
	 */
	public String getColumnName(int i) {
		String columnId = null;
		if (gridPanel != null) {
			columnId = gridPanel.getColumnName(i);
		}
		return columnId;
	}

	/**
	 * 
	 * @param i
	 *            index of column in ColumnModel
	 * @return id of column and equals to column name on service
	 */
	public String getColumnLocalId(int i) {
		String columnLocalId = null;
		if (gridPanel != null) {
			columnLocalId = gridPanel.getColumnLocalId(i);
		}
		return columnLocalId;
	}

}
