/**
 * 
 */
package org.gcube.portlets.user.tdw.client;

import org.gcube.portlets.user.tdw.client.event.CloseTableEvent;
import org.gcube.portlets.user.tdw.client.event.CloseTableEventHandler;
import org.gcube.portlets.user.tdw.client.event.FailureEvent;
import org.gcube.portlets.user.tdw.client.event.OpenTableEvent;
import org.gcube.portlets.user.tdw.client.event.OpenTableEventHandler;
import org.gcube.portlets.user.tdw.client.event.TableReadyEvent;
import org.gcube.portlets.user.tdw.client.rpc.TabularDataService;
import org.gcube.portlets.user.tdw.client.rpc.TabularDataServiceAsync;
import org.gcube.portlets.user.tdw.shared.model.TableDefinition;
import org.gcube.portlets.user.tdw.shared.model.TableId;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 *
 */
public class TabularDataController {
	
	protected int tdSessionId;
	protected EventBus eventBus;
	protected TabularDataServiceAsync service;
	
	protected TableDefinition currentTable;

	/**
	 * @param eventBus
	 */
	protected TabularDataController(int tdSessionId, EventBus eventBus) {
		this.tdSessionId = tdSessionId;
		this.eventBus = eventBus;
		service = GWT.create(TabularDataService.class);
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
	}

	protected void doOpenTable(TableId tableId)
	{
		service.openTable(tdSessionId, tableId, new AsyncCallback<TableDefinition>() {
			
			
			public void onSuccess(TableDefinition result) {
				Log.trace("table definition: "+result);
				currentTable = result;
				eventBus.fireEvent(new TableReadyEvent(result));				
			}
			
			public void onFailure(Throwable caught) {
				eventBus.fireEvent(new FailureEvent(caught, "Tabledefinition loading failed."));
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
	
	protected TableDefinition getCurrentTable()
	{
		return currentTable;
	}
	
	protected void getTableDefinition(TableId tableId, AsyncCallback<TableDefinition> callback)
	{
		service.getTableDefinition(tableId, callback);
	}

}
