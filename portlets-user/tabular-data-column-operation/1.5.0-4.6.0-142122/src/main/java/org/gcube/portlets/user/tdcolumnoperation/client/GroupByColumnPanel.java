package org.gcube.portlets.user.tdcolumnoperation.client;

import java.util.ArrayList;
import java.util.List;

import org.gcube.portlets.user.td.expressionwidget.client.properties.ColumnDataProperties;
import org.gcube.portlets.user.td.gwtservice.client.rpc.TDGWTServiceAsync;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTIsFinalException;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTIsLockedException;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTSessionExpiredException;
import org.gcube.portlets.user.td.monitorwidget.client.MonitorDialog;
import org.gcube.portlets.user.td.monitorwidget.client.MonitorDialogListener;
import org.gcube.portlets.user.td.widgetcommonevent.client.event.ChangeTableRequestEvent;
import org.gcube.portlets.user.td.widgetcommonevent.client.event.SessionExpiredEvent;
import org.gcube.portlets.user.td.widgetcommonevent.client.type.ChangeTableRequestType;
import org.gcube.portlets.user.td.widgetcommonevent.client.type.ChangeTableWhy;
import org.gcube.portlets.user.td.widgetcommonevent.client.type.SessionExpiredType;
import org.gcube.portlets.user.td.widgetcommonevent.shared.OperationResult;
import org.gcube.portlets.user.td.widgetcommonevent.shared.TRId;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnData;
import org.gcube.portlets.user.tdcolumnoperation.client.resources.HelperHTML;
import org.gcube.portlets.user.tdcolumnoperation.client.resources.ResourceBundleOperation;
import org.gcube.portlets.user.tdcolumnoperation.client.rpc.TdColumnOperationServiceAsync;
import org.gcube.portlets.user.tdcolumnoperation.client.specificoperation.ComboColumnDataFunctionPanel;
import org.gcube.portlets.user.tdcolumnoperation.client.specificoperation.GroupByAggregateManager;
import org.gcube.portlets.user.tdcolumnoperation.client.utils.UtilsGXT3;
import org.gcube.portlets.user.tdcolumnoperation.shared.AggregatePair;
import org.gcube.portlets.user.tdcolumnoperation.shared.AggregationColumnSession;
import org.gcube.portlets.user.tdcolumnoperation.shared.TdAggregateFunction;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.web.bindery.event.shared.EventBus;
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
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Jun 10, 2014
 * 
 */
public class GroupByColumnPanel extends FramedPanel implements
		DeletableContainer, MonitorDialogListener {
	private static final String WIDTH = "640px";
	private static final  String HEIGHT = "520px";
	private EventBus eventBus;
	private TRId trId;
	private String columnName;
	
	private TextButton groupByButton;

	private ListLoader<ListLoadConfig, ListLoadResult<ColumnData>> gridLoader;
	private Grid<ColumnData> grid;
	private CheckBoxSelectionModel<ColumnData> sm;
	
	private VerticalLayoutContainer verticalFunctionsLayout = new VerticalLayoutContainer();
	private List<TdAggregateFunction> aggregationFunctions;
	private ArrayList<ColumnData> columns;
	private GroupByAggregateManager aggregateFunctionMng;

	private ListStore<ColumnData> gridStore;
	private ComboColumnDataFunctionPanel baseFunctionPanel;
	
	private HelperHTML helperHtml = new HelperHTML("Group by Helper",
			ResourceBundleOperation.INSTANCE.groupbyHelper().getText());
	private TextButton buttonHelper = new TextButton("Helper");

	public GroupByColumnPanel(TRId trId, String columnName, EventBus eventBus) {
		this.trId = trId;
		this.columnName = columnName;
		this.eventBus = eventBus;
		this.aggregateFunctionMng = new GroupByAggregateManager(this);
		Log.debug("GroupByColumnPanel(): [" + trId.toString() + " columnName: "
				+ columnName + "]");
		init();
		build();

		Scheduler.get().scheduleDeferred(new ScheduledCommand() {

			public void execute() {
				loadAggregateFunctions();

			}
		});

		setEnableGroupByButton(false);

		this.buttonHelper.setIcon(ResourceBundleOperation.INSTANCE.help());

		buttonHelper.addSelectHandler(new SelectHandler() {

			@Override
			public void onSelect(SelectEvent event) {
				helperHtml.show();
			}
		});
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

		gridStore = new ListStore<ColumnData>(props.id());

		gridStore
				.addStoreDataChangeHandler(new StoreDataChangeHandler<ColumnData>() {

					@Override
					public void onDataChange(
							StoreDataChangeEvent<ColumnData> event) {
						List<ColumnData> cols = event.getSource().getAll();
						Log.debug("Columns:" + cols.size());
						if (columnName != null) {
							for (ColumnData c : cols) {
								if (c.getName().compareTo(columnName) == 0) {
									sm.select(c, false);
									sm.refresh();
									break;
								}
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
		gridLoader = new ListLoader<ListLoadConfig, ListLoadResult<ColumnData>>(
				proxy);

		gridLoader.setRemoteSort(false);
		gridLoader
				.addLoadHandler(new LoadResultListStoreBinding<ListLoadConfig, ColumnData, ListLoadResult<ColumnData>>(
						gridStore) {
				});

		grid = new Grid<ColumnData>(gridStore, cm) {
			@Override
			protected void onAfterFirstAttach() {
				super.onAfterFirstAttach();
				Scheduler.get().scheduleDeferred(new ScheduledCommand() {

					public void execute() {
						gridLoader.load();

					}
				});
			}
		};

		sm.setSelectionMode(SelectionMode.MULTI);
		grid.setLoader(gridLoader);
		grid.setSelectionModel(sm);
		// grid.getView().setAutoExpandColumn(labelCol);
		grid.setHeight(250);

		// grid.getView().getHeader().setEnabled(false);

		grid.getView().setStripeRows(true);
		grid.getView().setColumnLines(true);
		grid.getView().setAutoFill(true);
		grid.setBorders(false);
		grid.setLoadMask(true);
		grid.setColumnReordering(true);
		grid.setColumnResize(false);

		// Delete Button
		groupByButton = new TextButton("Group By");
		groupByButton.setIcon(ResourceBundleOperation.INSTANCE.tablegroupby());

		groupByButton.addSelectHandler(new SelectHandler() {

			@Override
			public void onSelect(SelectEvent event) {
				boolean isValidForm = validateGroupByForm();

				if (isValidForm)
					callStartGroupBy(getSelectedItems());

			}
		});

		sm.addSelectionHandler(new SelectionHandler<ColumnData>() {

			@Override
			public void onSelection(SelectionEvent<ColumnData> event) {
				if (getSelectedItems().size() > 0) {
					setEnableGroupByButton(true);
				} else {
					setEnableGroupByButton(false);
				}
			}
		});

		FieldLabel columnsLabel = new FieldLabel(null, "Group By Column/s");
		columnsLabel.setLabelWidth(150);

		VerticalLayoutContainer v = new VerticalLayoutContainer();
		v.setScrollMode(ScrollMode.AUTOY);
		v.setAdjustForScroll(true);
		v.add(columnsLabel, new VerticalLayoutData(1, -1, new Margins(2, 1, 5,
				1)));
		v.add(grid, new VerticalLayoutData(1, -1, new Margins(0)));
		
		v.add(new FieldLabel(null, "Aggregate For"),
				new VerticalLayoutData(1, -1, new Margins(0)));
		
		
		verticalFunctionsLayout = new VerticalLayoutContainer();
		verticalFunctionsLayout.setScrollMode(ScrollMode.AUTOY);
		verticalFunctionsLayout.setAdjustForScroll(true);
		
		baseFunctionPanel = new ComboColumnDataFunctionPanel(1,
				gridStore.getAll(), aggregationFunctions);
		verticalFunctionsLayout.add(baseFunctionPanel.getPanel(), new VerticalLayoutData(1, -1, new Margins(0)));

		Anchor anchor = new Anchor("Add Aggregate");
		anchor.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				appendAggregateFunction();
				updateAggregatorFunctions();

			}
		});

		
		verticalFunctionsLayout.add(anchor, new VerticalLayoutData(1, -1,
				new Margins(0, 0, 0, 2)));

		v.add(verticalFunctionsLayout, new VerticalLayoutData(1, -1,
				new Margins(0)));

		

		HBoxLayoutContainer hBox = new HBoxLayoutContainer();
		hBox.add(buttonHelper, new BoxLayoutData(new Margins(2, 5, 2, 5)));
		hBox.add(groupByButton, new BoxLayoutData(new Margins(2, 5, 2, 5)));
		v.add(hBox, new VerticalLayoutData(-1, -1, new Margins(10, 0, 10, 0)));

		add(v, new VerticalLayoutData(1, -1, new Margins(0)));

	}

	protected void setEnableGroupByButton(boolean bool) {
		groupByButton.setEnabled(bool);
	}

	
	protected ArrayList<ColumnData> getSelectedItems() {
		return new ArrayList<ColumnData>(grid.getSelectionModel()
				.getSelectedItems());

	}

	public void update(TRId trId, String columnName) {
		this.trId = trId;
		this.columnName = columnName;
		gridLoader.load();
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
								GWT.log(caught.getLocalizedMessage());
								UtilsGXT3.alert("Error Locked",
										caught.getLocalizedMessage());
							} else {
								if (caught instanceof TDGWTIsFinalException) {
									GWT.log(caught
											.getLocalizedMessage());
									UtilsGXT3
											.alert("Error Final",
													caught.getLocalizedMessage());
								} else {
									GWT.log("Error in operation : "
											+ caught.getLocalizedMessage());
									UtilsGXT3
											.alert("Error operation title",
													"Error operation description!");
								}
							}
						}

						// callback.onFailure(caught);

						/************************ TODO TEST CODE REMOVE THIS ************************/

						// TODO DEBUG
						// TdColumnOperation.addFakeColumn(callback);
						// columns = TdColumnOperation.fakeColumns();
						// baseFunctionPanel.updateColumns(columns);
					}

					public void onSuccess(ArrayList<ColumnData> result) {
						// Log.trace("loaded " + result.size() + " ColumnData");
						// callback.onSuccess(new
						// ListLoadResultBean<ColumnData>(
						// result));
						columns = result;
						baseFunctionPanel.updateColumns(columns);
						callback.onSuccess(new ListLoadResultBean<ColumnData>(
								columns));

						/************************ TODO TEST CODE REMOVE THIS ************************/
						// //TODO DEBUG
						// TdColumnOperation.addFakeColumn(callback);
						// baseFunctionPanel.updateColumns(TdColumnOperation.fakeColumns());
					}

				});

	}

	protected boolean validateGroupByForm() {

		ArrayList<ColumnData> selectedColumns = getSelectedItems();
		if (selectedColumns == null || selectedColumns.size() < 1) {
			UtilsGXT3.alert("Attention", "Attention no column selected!");
			return false;
		} else {

			boolean isValid = baseFunctionPanel.validate(selectedColumns);
			if (!isValid)
				return false;

			isValid = aggregateFunctionMng.validate(selectedColumns);
			if (!isValid)
				return false;

			return true;
		}
	}

	private void callStartGroupBy(ArrayList<ColumnData> columns) {

		GWT.log("Building AggregationColumnSession");

		AggregationColumnSession aggregationColumnSession = new AggregationColumnSession();

		aggregationColumnSession.setGroupColumns(getSelectedItems());
		aggregationColumnSession.setTrId(trId);

		List<ComboColumnDataFunctionPanel> aggregates = aggregateFunctionMng
				.getAggregatePanels();

		// ADDING BASE AGGREGATE
		AggregatePair pair = new AggregatePair();
		ColumnData cd = baseFunctionPanel.getColumnDataSelected();
		pair.setColumnData(cd);

		TdAggregateFunction function = baseFunctionPanel.getFunctionSelected();
		pair.setAggegrateFunction(function);

		aggregationColumnSession.addFunctionOnColumn(pair);

		// ADDING OTHERS AGGREGATE
		for (ComboColumnDataFunctionPanel comboColumnDataFunctionPanel : aggregates) {

			if (comboColumnDataFunctionPanel != null) {
				AggregatePair aggPair = new AggregatePair();
				ColumnData columnData = comboColumnDataFunctionPanel
						.getColumnDataSelected();
				aggPair.setColumnData(columnData);

				TdAggregateFunction funcSelected = comboColumnDataFunctionPanel
						.getFunctionSelected();
				aggPair.setAggegrateFunction(funcSelected);

				aggregationColumnSession.addFunctionOnColumn(aggPair);
			}
		}

		GWT.log("Builded AggregationColumnSession: " + aggregationColumnSession);

		TdColumnOperationServiceAsync.Util.getInstance().startGroupByOperation(
				aggregationColumnSession, new AsyncCallback<String>() {

					@Override
					public void onFailure(Throwable caught) {
						if (caught instanceof TDGWTSessionExpiredException)
							eventBus.fireEvent(new SessionExpiredEvent(
									SessionExpiredType.EXPIREDONSERVER));

						GWT.log("StartGroupByOperation FAILED");
						UtilsGXT3.alert("Error", caught.getMessage());
					}

					@Override
					public void onSuccess(String taskId) {
						GWT.log("StartGroupByOperation is OK, task Id is: "+taskId);
						openMonitorDialog(taskId);
					}
				});
	}

	private void loadAggregateFunctions() {

		TdColumnOperationServiceAsync.Util.getInstance()
				.getListAggregationFunctionIds(
						new AsyncCallback<List<TdAggregateFunction>>() {

							@Override
							public void onFailure(Throwable caught) {
								GWT.log("Failed functions loading: " + caught);
								UtilsGXT3
										.alert("Error",
												"An error occurred on retrieving aggregation funtions, please refresh page");

							}

							@Override
							public void onSuccess(
									List<TdAggregateFunction> result) {
								GWT.log("Aggregate functions loaded: " + result);
								aggregationFunctions = result;
								baseFunctionPanel
										.updateFunctions(aggregationFunctions);
							}
						});
	}

	private void appendAggregateFunction() {
		aggregateFunctionMng.appendAggregate(columns, aggregationFunctions);
	}

	private void updateAggregatorFunctions() {
		ComboColumnDataFunctionPanel functionPanel = aggregateFunctionMng
				.getLastColumnDataFunctionPanel();
		if (functionPanel != null)
			verticalFunctionsLayout.add(functionPanel.getPanel());

	}
	
	/*
	private void removeLastAggregateFunction() {
		aggregateFunctionMng.removeLastAggregate();
		verticalFunctionsLayout.forceLayout();
	}*/

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.gcube.portlets.user.tdcolumnoperation.client.DeletableContainer#
	 * deleteFired()
	 */
	@Override
	public void deleteFired(VerticalLayoutContainer panel) {
		try {
			verticalFunctionsLayout.remove(panel);
		} catch (Exception e) {
			GWT.log("error on deleting " + panel);
		}
		verticalFunctionsLayout.forceLayout();
	}

	
	
	
	protected void close() {
		/*
		 * if (parent != null) { parent.close(); }
		 */
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
				ChangeTableRequestType.GROUPBY, operationResult.getTrId(), why);
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
				ChangeTableRequestType.GROUPBY, operationResult.getTrId(), why);
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
