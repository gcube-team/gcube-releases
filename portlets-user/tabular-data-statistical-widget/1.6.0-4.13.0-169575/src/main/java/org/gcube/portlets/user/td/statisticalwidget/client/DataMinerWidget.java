package org.gcube.portlets.user.td.statisticalwidget.client;

import java.util.ArrayList;

import org.gcube.data.analysis.dataminermanagercl.shared.data.ColumnItem;
import org.gcube.portlets.user.td.gwtservice.client.rpc.TDGWTServiceAsync;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTSessionExpiredException;
import org.gcube.portlets.user.td.gwtservice.shared.tr.TabResource;
import org.gcube.portlets.user.td.statisticalwidget.client.stat.TDSubmissionHandler;
import org.gcube.portlets.user.td.statisticalwidget.client.utils.UtilsGXT3;
import org.gcube.portlets.user.td.widgetcommonevent.client.event.SessionExpiredEvent;
import org.gcube.portlets.user.td.widgetcommonevent.client.type.SessionExpiredType;
import org.gcube.portlets.user.td.widgetcommonevent.shared.TRId;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnData;
import org.gcube.portlets.widgets.dataminermanagerwidget.client.DataMinerManagerDialog;
import org.gcube.portlets.widgets.dataminermanagerwidget.client.tr.TabularResourceData;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.event.shared.EventBus;

/**
 * 
 * DataMiner Widget
 * 
 * 
 * @author Giancarlo Panichi
 *         
 * 
 */
public class DataMinerWidget {
	private String id;
	private String name;
	private String description;
	private String type;
	private ArrayList<ColumnItem> columns;

	private EventBus eventBus;
	private TRId trId;
	private DataMinerManagerDialog dataMinerManagerDialog;

	
	public DataMinerWidget(TRId trId, EventBus eventBus) {
		this.trId = trId;
		this.eventBus = eventBus;
		Log.debug("DataMinerWidget: " + trId);

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
							UtilsGXT3
									.alert("Error",
											"Error retrieving tabular resource informations");

						}
					}

					public void onSuccess(TabResource tabResource) {
						Log.debug("TabResouce: " + tabResource);
						createTableInfo(tabResource);
					}

				});
	}

	protected void createTableInfo(TabResource tabResource) {
		id = tabResource.getTrId().getTableId();
		name = tabResource.getName();
		description = tabResource.getDescription();
		type = tabResource.getTableTypeName();

		retrieveTabularResourceColumns();

	}

	protected void retrieveTabularResourceColumns() {
		TDGWTServiceAsync.INSTANCE.getColumnsForStatistical(trId,
				new AsyncCallback<ArrayList<ColumnData>>() {

					public void onFailure(Throwable caught) {
						if (caught instanceof TDGWTSessionExpiredException) {
							eventBus.fireEvent(new SessionExpiredEvent(
									SessionExpiredType.EXPIREDONSERVER));
						} else {

							Log.error("Error retrieving columns informations: "
									+ caught.getLocalizedMessage());
							UtilsGXT3.alert("Error",
									"Error retrieving columns informations");

						}
					}

					public void onSuccess(ArrayList<ColumnData> columnsResult) {
						Log.debug("Columns: " + columnsResult);
						createColumnsInfo(columnsResult);
					}

				});
	}

	protected void createColumnsInfo(ArrayList<ColumnData> columnsList) {
		columns = new ArrayList<>();

		for (ColumnData columnData : columnsList) {
			ColumnItem columnItem = new ColumnItem(columnData.getColumnId(),
					columnData.getLabel());
			columns.add(columnItem);
		}

		openDataMinerWidget();

	}

	protected void openDataMinerWidget() {
		TabularResourceData tabularResourceData = new TabularResourceData(id,
				name, description, type, columns);

		dataMinerManagerDialog = new DataMinerManagerDialog();
		dataMinerManagerDialog.setTabularResourceData(tabularResourceData);

		TDSubmissionHandler tdSubmissionHandler = new TDSubmissionHandler(this,
				trId, eventBus);

		dataMinerManagerDialog
				.addExternalExecutionEventHandler(tdSubmissionHandler);
		dataMinerManagerDialog.show();

	}

	public void closeDataMinerWidget() {
		dataMinerManagerDialog.hide();
	}
}
