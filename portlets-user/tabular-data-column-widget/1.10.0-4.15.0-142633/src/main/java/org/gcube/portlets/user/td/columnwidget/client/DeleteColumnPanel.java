package org.gcube.portlets.user.td.columnwidget.client;

import java.util.ArrayList;
import java.util.List;

import org.gcube.portlets.user.td.columnwidget.client.resources.ResourceBundle;
import org.gcube.portlets.user.td.columnwidget.client.utils.UtilsGXT3;
import org.gcube.portlets.user.td.expressionwidget.client.properties.ColumnDataProperties;
import org.gcube.portlets.user.td.gwtservice.client.rpc.TDGWTServiceAsync;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTIsFinalException;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTIsLockedException;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTSessionExpiredException;
import org.gcube.portlets.user.td.gwtservice.shared.tr.column.DeleteColumnSession;
import org.gcube.portlets.user.td.monitorwidget.client.MonitorDialog;
import org.gcube.portlets.user.td.monitorwidget.client.MonitorDialogListener;
import org.gcube.portlets.user.td.widgetcommonevent.client.CommonMessages;
import org.gcube.portlets.user.td.widgetcommonevent.client.event.ChangeTableRequestEvent;
import org.gcube.portlets.user.td.widgetcommonevent.client.event.SessionExpiredEvent;
import org.gcube.portlets.user.td.widgetcommonevent.client.type.ChangeTableRequestType;
import org.gcube.portlets.user.td.widgetcommonevent.client.type.ChangeTableWhy;
import org.gcube.portlets.user.td.widgetcommonevent.client.type.SessionExpiredType;
import org.gcube.portlets.user.td.widgetcommonevent.shared.OperationResult;
import org.gcube.portlets.user.td.widgetcommonevent.shared.TRId;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnData;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.shared.UmbrellaException;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.event.shared.EventBus;
import com.sencha.gxt.cell.core.client.ButtonCell.IconAlign;
import com.sencha.gxt.core.client.IdentityValueProvider;
import com.sencha.gxt.core.client.Style.SelectionMode;
import com.sencha.gxt.core.client.dom.ScrollSupport.ScrollMode;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.data.client.loader.RpcProxy;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.event.StoreDataChangeEvent;
import com.sencha.gxt.data.shared.event.StoreDataChangeEvent.StoreDataChangeHandler;
import com.sencha.gxt.data.shared.loader.ListLoadConfig;
import com.sencha.gxt.data.shared.loader.ListLoadResult;
import com.sencha.gxt.data.shared.loader.ListLoadResultBean;
import com.sencha.gxt.data.shared.loader.ListLoader;
import com.sencha.gxt.data.shared.loader.LoadResultListStoreBinding;
import com.sencha.gxt.widget.core.client.FramedPanel;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.BoxLayoutContainer.BoxLayoutData;
import com.sencha.gxt.widget.core.client.container.HBoxLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.grid.CheckBoxSelectionModel;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;

/**
 * 
 * ChangeColumnTypePanel is the panel for change column type
 * 
 * 
 * @author "Giancarlo Panichi" <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 * 
 */
public class DeleteColumnPanel extends FramedPanel implements
		MonitorDialogListener {
	private static final String WIDTH = "640px";
	private static final String HEIGHT = "520px";
	private EventBus eventBus;
	private DeleteColumnDialog parent;
	private TRId trId;
	private String columnName;


	private TextButton deleteBtn;

	private ListLoader<ListLoadConfig, ListLoadResult<ColumnData>> loader;
	private Grid<ColumnData> grid;
	private CheckBoxSelectionModel<ColumnData> sm;

	private DeleteColumnSession deleteColumnSession;

	private ListStore<ColumnData> store;
	private DeleteColumnMessages msgs;
	private CommonMessages msgsCommon;

	public DeleteColumnPanel(TRId trId, String columnName, EventBus eventBus) {
		
		this.trId = trId;
		this.columnName = columnName;
		this.eventBus = eventBus;
		Log.debug("DeleteColumnPanel(): [" + trId.toString() + " columnName: "
				+ columnName + "]");
		initMessages();
		init();
		build();
	}

	protected void initMessages(){
		msgs = GWT.create(DeleteColumnMessages.class);
		msgsCommon = GWT.create(CommonMessages.class);
	}
	
	
	public void init() {
		setWidth(WIDTH);
		setHeight(HEIGHT);
		setHeaderVisible(false);
		setBodyBorder(false);
	}

	protected void build() {
		ColumnDataProperties props = GWT.create(ColumnDataProperties.class);

		ColumnConfig<ColumnData, String> labelCol = new ColumnConfig<ColumnData, String>(
				props.label());

		IdentityValueProvider<ColumnData> identity = new IdentityValueProvider<ColumnData>();
		sm = new CheckBoxSelectionModel<ColumnData>(identity);

		List<ColumnConfig<ColumnData, ?>> l = new ArrayList<ColumnConfig<ColumnData, ?>>();
		l.add(sm.getColumn());
		l.add(labelCol);
		ColumnModel<ColumnData> cm = new ColumnModel<ColumnData>(l);

		store = new ListStore<ColumnData>(props.id());

		store.addStoreDataChangeHandler(new StoreDataChangeHandler<ColumnData>() {

			@Override
			public void onDataChange(StoreDataChangeEvent<ColumnData> event) {
				List<ColumnData> cols = event.getSource().getAll();
				Log.debug("Columns:" + cols.size());
				for (ColumnData c : cols) {
					if (c.getName().compareTo(columnName) == 0) {
						sm.select(c, false);
						sm.refresh();
						break;
					}
				}

			}
		});

		RpcProxy<ListLoadConfig, ListLoadResult<ColumnData>> proxy = new RpcProxy<ListLoadConfig, ListLoadResult<ColumnData>>() {

			public void load(ListLoadConfig loadConfig,
					final AsyncCallback<ListLoadResult<ColumnData>> callback) {
				loadData(loadConfig, callback);
			}
		};
		loader = new ListLoader<ListLoadConfig, ListLoadResult<ColumnData>>(
				proxy);

		loader.setRemoteSort(false);
		loader.addLoadHandler(new LoadResultListStoreBinding<ListLoadConfig, ColumnData, ListLoadResult<ColumnData>>(
				store) {
		});

		grid = new Grid<ColumnData>(store, cm) {
			@Override
			protected void onAfterFirstAttach() {
				super.onAfterFirstAttach();
				Scheduler.get().scheduleDeferred(new ScheduledCommand() {

					public void execute() {
						loader.load();

					}
				});
			}
		};

		sm.setSelectionMode(SelectionMode.MULTI);
		grid.setLoader(loader);
		grid.setSelectionModel(sm);
		// grid.getView().setAutoExpandColumn(labelCol);
		grid.setHeight(360);
		grid.getView().setStripeRows(true);
		grid.getView().setColumnLines(true);
		grid.getView().setAutoFill(true);
		grid.setBorders(false);
		grid.setLoadMask(true);
		grid.setColumnReordering(true);
		grid.setColumnResize(false);

		// Delete Button
		deleteBtn = new TextButton(msgs.deleteBtnText());
		deleteBtn.setIcon(ResourceBundle.INSTANCE.columnDelete());
		deleteBtn.setIconAlign(IconAlign.RIGHT);
		deleteBtn.setToolTip(msgs.deleteBtnToolTip());

		SelectHandler deleteHandler = new SelectHandler() {

			public void onSelect(SelectEvent event) {
				onDeleteColumns();

			}
		};
		deleteBtn.addSelectHandler(deleteHandler);

		FieldLabel columnsLabel = new FieldLabel(null, msgs.columnsLabel());
		columnsLabel.getElement().applyStyles("font-weight:bold");
		HBoxLayoutContainer hBox = new HBoxLayoutContainer();
		hBox.add(deleteBtn, new BoxLayoutData(new Margins(2, 5, 2, 5)));

		VerticalLayoutContainer v = new VerticalLayoutContainer();
		v.setScrollMode(ScrollMode.AUTOY);
		v.setAdjustForScroll(true);
		v.add(columnsLabel, new VerticalLayoutData(-1, -1, new Margins(2, 1, 5,
				1)));
		v.add(grid, new VerticalLayoutData(1, -1, new Margins(0)));
		v.add(hBox, new VerticalLayoutData(-1, -1, new Margins(10, 0, 10, 0)));
		add(v, new VerticalLayoutData(1, -1, new Margins(0)));

	}

	protected ArrayList<ColumnData> getSelectedItems() {
		return new ArrayList<ColumnData>(grid.getSelectionModel()
				.getSelectedItems());

	}

	public void update(TRId trId, String columnName) {
		this.trId = trId;
		this.columnName = columnName;
		loader.load();
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
							if (caught instanceof TDGWTIsLockedException) {
								Log.error(caught.getLocalizedMessage());
								UtilsGXT3.alert(msgsCommon.errorLocked(),
										caught.getLocalizedMessage());
							} else {
								if (caught instanceof TDGWTIsFinalException) {
									Log.error(caught.getLocalizedMessage());
									UtilsGXT3.alert(msgsCommon.errorFinal(),
											caught.getLocalizedMessage());
								} else {
									Log.error("load columns failure:"
											+ caught.getLocalizedMessage());
									UtilsGXT3.alert(msgs.errorRetrievingColumnsHead(),
											msgs.errorRetrievingColumns());
								}
							}
						}
						callback.onFailure(caught);

					}

					public void onSuccess(ArrayList<ColumnData> result) {
						try {
							Log.debug("loaded " + result.size() + " ColumnData");
							callback.onSuccess(new ListLoadResultBean<ColumnData>(
									result));

						} catch (UmbrellaException e) {
							Log.debug("Umbrella exception "
									+ e.getLocalizedMessage());
						} catch (com.google.web.bindery.event.shared.UmbrellaException e) {
							Log.debug("Umbrella exception "
									+ e.getLocalizedMessage());
						}

					}

				});

	}

	protected void onDeleteColumns() {
		ArrayList<ColumnData> columns = getSelectedItems();
		if (columns == null || columns.size() < 1) {
			UtilsGXT3.alert(msgsCommon.attention(), msgs.attentionNoColumnSelected());
			return;
		} else {
			callDeleteColumn(columns);
		}
	}

	private void callDeleteColumn(ArrayList<ColumnData> columns) {
		deleteColumnSession = new DeleteColumnSession(trId, columns);

		TDGWTServiceAsync.INSTANCE.startDeleteColumn(deleteColumnSession,
				new AsyncCallback<String>() {

					public void onFailure(Throwable caught) {
						if (caught instanceof TDGWTSessionExpiredException) {
							eventBus.fireEvent(new SessionExpiredEvent(
									SessionExpiredType.EXPIREDONSERVER));
						} else {
							if (caught instanceof TDGWTIsLockedException) {
								Log.error(caught.getLocalizedMessage());
								UtilsGXT3.alert(msgsCommon.errorLocked(),
										caught.getLocalizedMessage());
							} else {
								if (caught instanceof TDGWTIsFinalException) {
									Log.error(caught.getLocalizedMessage());
									UtilsGXT3.alert(msgsCommon.errorFinal(),
											caught.getLocalizedMessage());
								} else {
									Log.debug("Delete Column Error: "
											+ caught.getLocalizedMessage());
									UtilsGXT3
											.alert(msgs.deleteColumnErrorHead(),
													msgs.deleteColumnError());
								}
							}
						}
					}

					public void onSuccess(String taskId) {
						openMonitorDialog(taskId);
					}

				});

	}

	public void close() {
		if (parent != null) {
			parent.close();
		}
	}

	// /
	protected void openMonitorDialog(String taskId) {
		MonitorDialog monitorDialog = new MonitorDialog(taskId, eventBus);
		monitorDialog.addProgressDialogListener(this);
		monitorDialog.show();
	}

	@Override
	public void operationComplete(OperationResult operationResult) {
		ChangeTableWhy why = ChangeTableWhy.TABLEUPDATED;
		ChangeTableRequestEvent changeTableRequestEvent = new ChangeTableRequestEvent(
				ChangeTableRequestType.DELETECOLUMN, operationResult.getTrId(), why);
		eventBus.fireEvent(changeTableRequestEvent);
		close();
	}

	@Override
	public void operationFailed(Throwable caught, String reason, String details) {
		UtilsGXT3.alert(reason, details);
		close();

	}

	@Override
	public void operationStopped(OperationResult operationResult, String reason, String details) {
		ChangeTableWhy why = ChangeTableWhy.TABLECURATION;
		ChangeTableRequestEvent changeTableRequestEvent = new ChangeTableRequestEvent(
				ChangeTableRequestType.DELETECOLUMN, operationResult.getTrId(), why);
		eventBus.fireEvent(changeTableRequestEvent);
		close();

	}

	@Override
	public void operationAborted() {
		close();

	}

	@Override
	public void operationPutInBackground() {
		close();

	}

}
