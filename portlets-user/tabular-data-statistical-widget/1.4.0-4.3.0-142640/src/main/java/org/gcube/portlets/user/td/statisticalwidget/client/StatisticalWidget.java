package org.gcube.portlets.user.td.statisticalwidget.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.gcube.portlets.user.td.gwtservice.client.rpc.TDGWTServiceAsync;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTSessionExpiredException;
import org.gcube.portlets.user.td.gwtservice.shared.tr.TabResource;
import org.gcube.portlets.user.td.statisticalwidget.client.stat.TDExternalTable;
import org.gcube.portlets.user.td.statisticalwidget.client.stat.TDSubmissionHandler;
import org.gcube.portlets.user.td.statisticalwidget.client.utils.UtilsGXT3;
import org.gcube.portlets.user.td.widgetcommonevent.client.event.SessionExpiredEvent;
import org.gcube.portlets.user.td.widgetcommonevent.client.type.SessionExpiredType;
import org.gcube.portlets.user.td.widgetcommonevent.shared.TRId;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnData;
import org.gcube.portlets.widgets.StatisticalManagerAlgorithmsWidget.client.ExternalTable;
import org.gcube.portlets.widgets.StatisticalManagerAlgorithmsWidget.client.StatisticalManagerExperimentsWidget;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.event.shared.EventBus;

/**
 * 
 * Statistical Widget
 * 
 * 
 * @author "Giancarlo Panichi" <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 * 
 */
public class StatisticalWidget {
	private String id;
	private String label;
	private Map<String,String> columns;
	
	protected EventBus eventBus;
	protected TRId trId;
	protected StatisticalManagerExperimentsWidget statisticalManagerExperimentsWidget;
	
	
	/**
	 * 
	 * @param trId
	 * @param eventBus
	 */
	public StatisticalWidget(TRId trId, EventBus eventBus) {
		this.trId = trId;
		this.eventBus = eventBus;
		Log.debug("Statistical Widget: " + trId);

		retrieveTabularResourceInfo();

	}
	
	protected void retrieveTabularResourceInfo() {
		TDGWTServiceAsync.INSTANCE.getTabResourceInformation(trId, 
				new AsyncCallback<TabResource>() {

			public void onFailure(Throwable caught) {
				if (caught instanceof TDGWTSessionExpiredException) {
					eventBus.fireEvent(new SessionExpiredEvent(
							SessionExpiredType.EXPIREDONSERVER));
				} else {

					Log.error("Error retrieving tabular resource informations: "
							+ caught.getLocalizedMessage());
					UtilsGXT3.alert("Error", "Error retrieving tabular resource informations");

				}
			}

			public void onSuccess(TabResource tabResource) {
				Log.debug("TabResouce: " + tabResource);
				createTableInfo(tabResource);
			}

		});
	}
	
	
	protected void createTableInfo(TabResource tabResource){
		id=tabResource.getTrId().getTableId();
		label=tabResource.getName();
		
		retrieveTabularResourceColumns();
		
	}
	
	
	protected void retrieveTabularResourceColumns() {
		TDGWTServiceAsync.INSTANCE.getColumnsForStatistical(trId, new AsyncCallback<ArrayList<ColumnData>>() {

			public void onFailure(Throwable caught) {
				if (caught instanceof TDGWTSessionExpiredException) {
					eventBus.fireEvent(new SessionExpiredEvent(
							SessionExpiredType.EXPIREDONSERVER));
				} else {

					Log.error("Error retrieving columns informations: "
							+ caught.getLocalizedMessage());
					UtilsGXT3.alert("Error", "Error retrieving columns informations");

				}
			}

			public void onSuccess(ArrayList<ColumnData> columnsResult) {
				Log.debug("Columns: " + columnsResult);
				createColumnsMap(columnsResult);
			}

		});
	}
	
	protected void createColumnsMap(ArrayList<ColumnData> columnsList){
		columns= new HashMap<String, String>();
		
		for(ColumnData columnData:columnsList){
			columns.put(columnData.getColumnId(), columnData.getLabel());
		}
		
		openStatisticalWidget();
		
	}
	
	protected void openStatisticalWidget(){
		ArrayList<ExternalTable> tables = new ArrayList<ExternalTable>();
		TDExternalTable tdExternalTable=new TDExternalTable(trId, eventBus, id, label,columns);
		tables.add(tdExternalTable);
		TDSubmissionHandler tdSubmissionHandler = new TDSubmissionHandler(this, trId,
				eventBus);
		
	
		statisticalManagerExperimentsWidget = new StatisticalManagerExperimentsWidget(
				null, tables, "ExecutionComputationDefault",
				tdSubmissionHandler);
		
	}
	
	
	public void closeStatisticalWidget(){
		statisticalManagerExperimentsWidget.hide();
	}
}
