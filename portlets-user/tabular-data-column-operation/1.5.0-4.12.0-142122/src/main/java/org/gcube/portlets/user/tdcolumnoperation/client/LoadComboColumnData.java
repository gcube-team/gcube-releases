/**
 * 
 */
package org.gcube.portlets.user.tdcolumnoperation.client;

import java.util.ArrayList;

import org.gcube.portlets.user.td.gwtservice.client.rpc.TDGWTServiceAsync;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTSessionExpiredException;
import org.gcube.portlets.user.td.widgetcommonevent.client.event.SessionExpiredEvent;
import org.gcube.portlets.user.td.widgetcommonevent.client.type.SessionExpiredType;
import org.gcube.portlets.user.td.widgetcommonevent.shared.TRId;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnData;
import org.gcube.portlets.user.tdcolumnoperation.client.properties.ColumnDataPropertiesCombo;
import org.gcube.portlets.user.tdcolumnoperation.client.utils.UtilsGXT3;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.event.shared.EventBus;
import com.sencha.gxt.cell.core.client.form.ComboBoxCell.TriggerAction;
import com.sencha.gxt.data.client.loader.RpcProxy;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.loader.ListLoadConfig;
import com.sencha.gxt.data.shared.loader.ListLoadConfigBean;
import com.sencha.gxt.data.shared.loader.ListLoadResult;
import com.sencha.gxt.data.shared.loader.ListLoadResultBean;
import com.sencha.gxt.data.shared.loader.ListLoader;
import com.sencha.gxt.data.shared.loader.LoadResultListStoreBinding;
import com.sencha.gxt.widget.core.client.form.ComboBox;
import com.sencha.gxt.widget.core.client.info.Info;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Jun 3, 2014
 *
 */
public class LoadComboColumnData {

	protected String columnName;
	protected ListLoader<ListLoadConfig, ListLoadResult<ColumnData>> loader;
	
	protected ComboBox<ColumnData> comboColumn = null;
	private boolean allowBlank;
	private ListStore<ColumnData> storeCombo;
	protected TRId trId;
	protected EventBus eventBus;
	
	
	/**
	 * 
	 */
	public LoadComboColumnData(TRId trId, EventBus eventBus, String columnName, boolean allowBlank) {
		this.trId = trId;
		this.eventBus = eventBus;
		this.columnName = columnName;
		this.allowBlank = allowBlank;
		initComboColumnName();
	}
	
	/**
	 * 
	 */
	private void initComboColumnName() {
		
		GWT.log("initComboColumnName: [" + trId+ " columnName: "+ columnName + "]");

		// Column Data
		ColumnDataPropertiesCombo propsColumnData = GWT.create(ColumnDataPropertiesCombo.class);
		storeCombo = new ListStore<ColumnData>(propsColumnData.id());

		GWT.log("StoreCombo created");

		RpcProxy<ListLoadConfig, ListLoadResult<ColumnData>> proxy = new RpcProxy<ListLoadConfig, ListLoadResult<ColumnData>>() {

			public void load(ListLoadConfig loadConfig, final AsyncCallback<ListLoadResult<ColumnData>> callback) {
				loadData(loadConfig, callback);
			}
		};

		loader = new ListLoader<ListLoadConfig, ListLoadResult<ColumnData>>(
				proxy) {
			@Override
			protected ListLoadConfig newLoadConfig() {
				return (ListLoadConfig) new ListLoadConfigBean();
			}

		};

		loader.addLoadHandler(new LoadResultListStoreBinding<ListLoadConfig, ColumnData, ListLoadResult<ColumnData>>(storeCombo));
		GWT.log("LoaderCombo created");

		comboColumn = new ComboBox<ColumnData>(storeCombo, propsColumnData.label()) {

			protected void onAfterFirstAttach() {
				super.onAfterFirstAttach();
				Scheduler.get().scheduleDeferred(new ScheduledCommand() {
					public void execute() {
						loader.load();
					}
				});
			}
		};
		
		comboColumn.setAllowBlank(allowBlank);
		
		GWT.log("Combo ColumnData created");

		addHandlersForComboColumn(propsColumnData.label());

		comboColumn.setLoader(loader);
		comboColumn.setEmptyText("Select a column...");
		comboColumn.setWidth(150);
		comboColumn.setTypeAhead(false);
		comboColumn.setEditable(false);
		comboColumn.setTriggerAction(TriggerAction.ALL);
		
	}
	
	protected void addHandlersForComboColumn(final LabelProvider<ColumnData> labelProvider) {
		comboColumn.addSelectionHandler(new SelectionHandler<ColumnData>() {
			
			public void onSelection(SelectionEvent<ColumnData> event) {
				Info.display(
						"Column Selected",
						"You selected "
								+ (event.getSelectedItem() == null ? "nothing"
										: labelProvider.getLabel(event
												.getSelectedItem()) + "!"));
				GWT.log("ComboColumn selected: " + event.getSelectedItem());
				ColumnData columnData = event.getSelectedItem();
				updateComboStatus(columnData);
			}

		});
	}
	
	protected void updateComboStatus(ColumnData cd) {
		
		GWT.log("UpdateComboStatus as: " + cd);
		comboColumn.setValue(cd);
	}
	
	
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
											"TIMEDIMENSION") == 0) {
								removables.add(c);
							}
						}
						if (removables.size() > 0) {
							result.removeAll(removables);
						}
						setComboStatus(result);
						callback.onSuccess(new ListLoadResultBean<ColumnData>(result));
					}

				});

	}
	
	protected void setComboStatus(ArrayList<ColumnData> result) {
		GWT.log("ColumnName:" + columnName);
		if (columnName != null) {
			for (ColumnData cd : result) {
				GWT.log("ColumnData name:" + cd.getName());
				if (cd.getName().compareTo(columnName) == 0) {
					updateComboStatus(cd);
				}
			}
		}
	}
	
	public ColumnData getComboValue(){
		return comboColumn.getCurrentValue();
	}
	
	
	public String getColumnName() {
		return columnName;
	}
	/**
	 * @return
	 */
	public ColumnData getCurrentValue() {
		return getComboValue();
	}

	public ListStore<ColumnData> getStoreCombo() {
		return storeCombo;
	}

}
