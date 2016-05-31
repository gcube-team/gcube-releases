package org.gcube.portlets.user.tdcolumnoperation.client;

import java.util.ArrayList;

import org.gcube.portlets.user.td.gwtservice.client.rpc.TDGWTServiceAsync;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTSessionExpiredException;
import org.gcube.portlets.user.td.widgetcommonevent.client.event.SessionExpiredEvent;
import org.gcube.portlets.user.td.widgetcommonevent.client.type.SessionExpiredType;
import org.gcube.portlets.user.td.widgetcommonevent.shared.TRId;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnData;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnDataType;
import org.gcube.portlets.user.tdcolumnoperation.client.utils.UtilsGXT3;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.event.shared.EventBus;
import com.sencha.gxt.data.shared.loader.ListLoadConfig;
import com.sencha.gxt.data.shared.loader.ListLoadResult;
import com.sencha.gxt.data.shared.loader.ListLoadResultBean;

/**
 * 
 * @author giancarlo
 * email: <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public class LoadComboColumnDataForSplit extends LoadComboColumnData {
	public LoadComboColumnDataForSplit(TRId trId, EventBus eventBus,
			String columnName, boolean allowBlank) {
		super(trId, eventBus, columnName, allowBlank);

	}

	@Override
	protected void loadData(ListLoadConfig loadConfig,
			final AsyncCallback<ListLoadResult<ColumnData>> callback) {
		TDGWTServiceAsync.INSTANCE.getColumns(trId,
				new AsyncCallback<ArrayList<ColumnData>>() {

					public void onFailure(Throwable caught) {
						if (caught instanceof TDGWTSessionExpiredException) {
							eventBus.fireEvent(new SessionExpiredEvent(
									SessionExpiredType.EXPIREDONSERVER));
						} else {
							GWT.log("load combo failure:"
									+ caught.getLocalizedMessage());
							UtilsGXT3.alert("Error",
									"Error retrieving columns of tabular resource:"
											+ trId.getId());
						}
						callback.onFailure(caught);
					}

					public void onSuccess(ArrayList<ColumnData> result) {
						GWT.log("loaded " + result.size() + " ColumnData");
						ArrayList<ColumnData> removables = new ArrayList<ColumnData>();
						for (ColumnData c : result) {
							if (c.getTypeCode().compareTo("DIMENSION") == 0
									|| c.getTypeCode().compareTo(
											"TIMEDIMENSION") == 0
									|| ColumnDataType.getColumnDataTypeFromId(
											c.getDataTypeName()).compareTo(
											ColumnDataType.Text) != 0) {
								removables.add(c);
							}
						}
						if (removables.size() > 0) {
							result.removeAll(removables);
						}
						if(result.size()<=0){
							GWT.log("No text column in this tabular resource!");
							UtilsGXT3.alert("Attention","No text column in this tabular resource!");
						}
						setComboStatus(result);
						callback.onSuccess(new ListLoadResultBean<ColumnData>(
								result));
					}

				});

	}

}
