/**
 * 
 */
package org.gcube.portlets.user.tdwx.client;

import org.gcube.portlets.user.tdwx.client.event.CloseTableEvent;
import org.gcube.portlets.user.tdwx.client.event.CloseTableEvent.CloseTableEventHandler;
import org.gcube.portlets.user.tdwx.client.event.ColumnsReorderingEvent;
import org.gcube.portlets.user.tdwx.client.event.FailureEvent;
import org.gcube.portlets.user.tdwx.client.event.OpenTableEvent;
import org.gcube.portlets.user.tdwx.client.event.OpenTableEvent.OpenTableEventHandler;
import org.gcube.portlets.user.tdwx.client.event.TableReadyEvent;
import org.gcube.portlets.user.tdwx.client.rpc.TabularDataXService;
import org.gcube.portlets.user.tdwx.client.rpc.TabularDataXServiceAsync;
import org.gcube.portlets.user.tdwx.shared.ColumnsReorderingConfig;
import org.gcube.portlets.user.tdwx.shared.model.TableDefinition;
import org.gcube.portlets.user.tdwx.shared.model.TableId;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * 
 * @author "Giancarlo Panichi" 
 * <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 * Defines the controller of grid
 */
public class TabularDataXController {
	
	private int tdSessionId;
	private EventBus eventBus;
	private TabularDataXServiceAsync service;
	
	private TableDefinition currentTable;

	/**
	 * @param eventBus
	 */
	protected TabularDataXController(int tdSessionId, EventBus eventBus) {
		this.tdSessionId = tdSessionId;
		this.eventBus = eventBus;
		service = GWT.create(TabularDataXService.class);
		bindEventBus();
	}

	protected void bindEventBus()
	{
		eventBus.addHandler(OpenTableEvent.TYPE, new OpenTableEventHandler() {
			
			
			public void onOpenTable(OpenTableEvent event) {
				doOpenTable(event.getTableId());			
			}
		});
		eventBus.addHandler(CloseTableEvent.TYPE, new CloseTableEventHandler() {

			public void onCloseTable(CloseTableEvent event) {
				doCloseTable();
			}
		});
		
		eventBus.addHandler(ColumnsReorderingEvent.TYPE, new ColumnsReorderingEvent.ColumnsReorderingEventHandler() {
			
			@Override
			public void onColumnsReordering(ColumnsReorderingEvent event) {
				doSetCurrentTableColumnsReordering(event);
				
			}
		});
	}

	protected void doCloseTable()
	{
		service.closeTable(tdSessionId, new AsyncCallback<Void>() {

			public void onFailure(Throwable caught) {
				eventBus.fireEvent(new FailureEvent(caught, "Table closing failed."));
			}

			public void onSuccess(Void result) {
				Log.trace("table closed");
				currentTable = null;				
			}
		});
	}
	
	protected void doOpenTable(TableId tableId)
	{
		service.openTable(tdSessionId, tableId, new AsyncCallback<TableDefinition>() {
			
			public void onFailure(Throwable caught) {
				eventBus.fireEvent(new FailureEvent(caught, "Tabledefinition loading failed."));
			}
			
			public void onSuccess(TableDefinition result) {
				Log.trace("table definition: "+result);
				currentTable = result;
				eventBus.fireEvent(new TableReadyEvent(result));				
			}
		});
	}
	
	
	
	
	protected void doSetCurrentTableColumnsReordering(ColumnsReorderingEvent event)
	{
		ColumnsReorderingConfig columnReorderingConfig=event.getColumnsReorderingConfig();
		service.setCurrentTableColumnsReordering(tdSessionId, columnReorderingConfig, new AsyncCallback<TableDefinition>() {
			
			public void onFailure(Throwable caught) {
				eventBus.fireEvent(new FailureEvent(caught, "Column Reordering failed."));
			}
			
			public void onSuccess(TableDefinition result) {
				Log.trace("table definition: "+result);
				currentTable = result;
				eventBus.fireEvent(new TableReadyEvent(result));				
			}
		});
	}

	protected TableDefinition getCurrentTable()
	{
		return currentTable;
	}
	
	protected void getTableDefinition(TableId tableId, AsyncCallback<TableDefinition> callback)
	{
		service.getTableDefinition(tableId, callback);
	}
	
}
