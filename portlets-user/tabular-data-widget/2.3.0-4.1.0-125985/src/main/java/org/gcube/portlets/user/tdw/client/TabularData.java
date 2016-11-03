/**
 * 
 */
package org.gcube.portlets.user.tdw.client;

import org.gcube.portlets.user.tdw.client.event.CloseTableEvent;
import org.gcube.portlets.user.tdw.client.event.FailureEvent;
import org.gcube.portlets.user.tdw.client.event.FailureEventHandler;
import org.gcube.portlets.user.tdw.client.event.OpenTableEvent;
import org.gcube.portlets.user.tdw.server.datasource.DataSourceFactory;
import org.gcube.portlets.user.tdw.shared.model.TableDefinition;
import org.gcube.portlets.user.tdw.shared.model.TableId;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 *
 */
public class TabularData {
	
	protected static int seed = 0;
	
	protected int tdSessionId;
	protected String defaultDataSourceFactoryName;
	protected EventBus eventBus;
	protected TabularDataController controller;
	protected TabularDataGridPanel gridPanel;
	
	/**
	 * Creates a new {@link TabularData object setting the default {@link DataSourceFactory} name.
	 * @param defaultDataSourceFactoryName the default {@link DataSourceFactory} name.
	 */
	public TabularData(String defaultDataSourceFactoryName)
	{
		this.tdSessionId = seed++;
		this.defaultDataSourceFactoryName = defaultDataSourceFactoryName;
		eventBus = new SimpleEventBus();
		controller = new TabularDataController(tdSessionId, eventBus);
	}
	
	/**
	 * Creates a new {@link TabularData} object.
	 */
	public TabularData()
	{
		this(null);
	}
	
	/**
	 * @return the defaultDataSourceFactoryName
	 */
	public String getDefaultDataSourceFactoryName() {
		return defaultDataSourceFactoryName;
	}

	/**
	 * @param defaultDataSourceFactoryName the defaultDataSourceFactoryName to set
	 */
	public void setDefaultDataSourceFactoryName(String defaultDataSourceFactoryName) {
		this.defaultDataSourceFactoryName = defaultDataSourceFactoryName;
	}

	public TabularDataGridPanel getGridPanel()
	{
		if (gridPanel == null) {
			gridPanel = new TabularDataGridPanel(tdSessionId, eventBus);
		}
		return gridPanel;
	}
	
	/**
	 * Add a new {@link FailureEventHandler}.
	 * @param handler
	 */
	public void addFailureHandler(FailureEventHandler handler)
	{
		eventBus.addHandler(FailureEvent.TYPE, handler);
	}
	
	/**
	 * Opens a new table.
	 * @param id the table id.
	 */
	public void openTable(TableId id)
	{
		Log.trace("openTable id: "+id);
		eventBus.fireEvent(new OpenTableEvent(id));
	}
	
	/**
	 * Opens a new table. The default {@link DataSourceFactory} name is used.
	 * @param tableKey the table key.
	 */
	public void openTable(String tableKey)
	{
		Log.trace("openTable tableKey: "+tableKey);
		TableId tableId = getTableId(tableKey);
		eventBus.fireEvent(new OpenTableEvent(tableId));
	}
	
	/**
	 * Returns the current table definition.
	 * @return the table description, <code>null</code> if no table is open.
	 */
	public TableDefinition getCurrentTable()
	{
		return controller.getCurrentTable();
	}
	
	/**
	 * Returns the {@link TableDefinition} for the specified {@link TableId}.
	 * @param tableId the table id.
	 * @param callback the {@link AsyncCallback} called when the {@link TableDefinition} is retrieved.
	 */
	public void getTableDefinition(TableId tableId, AsyncCallback<TableDefinition> callback)
	{
		controller.getTableDefinition(tableId, callback);
	}
	
	public void getTableDefinition(String tableKey, AsyncCallback<TableDefinition> callback)
	{
		TableId tableId = getTableId(tableKey);
		controller.getTableDefinition(tableId, callback);
	}
	
	public void closeTable()
	{
		Log.trace("closeTable");
		eventBus.fireEvent(new CloseTableEvent());
	}
	
	protected TableId getTableId(String tableKey)
	{
		if (defaultDataSourceFactoryName == null) {
			Log.error("No default DataSourceFactoryName specified");
			throw new IllegalArgumentException("No default DataSourceFactoryName specified");
		}
		return new TableId(defaultDataSourceFactoryName, tableKey);
	}

}
