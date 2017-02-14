package org.gcube.portlets.user.tdtemplateoperation.client;

import java.util.ArrayList;
import java.util.List;

import org.gcube.portlets.user.td.monitorwidget.client.utils.UtilsGXT3;
import org.gcube.portlets.user.tdtemplateoperation.client.event.ActionCompletedEvent;
import org.gcube.portlets.user.tdtemplateoperation.client.operation.AggregateDataFunctionPanel;
import org.gcube.portlets.user.tdtemplateoperation.client.operation.GroupByAggregateManager;
import org.gcube.portlets.user.tdtemplateoperation.client.properties.ComboTimeTypeProperties;
import org.gcube.portlets.user.tdtemplateoperation.client.properties.TdColumnDataPropertiesAccess;
import org.gcube.portlets.user.tdtemplateoperation.client.resources.ResourceBundleTemplateOperation;
import org.gcube.portlets.user.tdtemplateoperation.shared.AggregatePair;
import org.gcube.portlets.user.tdtemplateoperation.shared.ServerObjectId;
import org.gcube.portlets.user.tdtemplateoperation.shared.TdAggregateFunction;
import org.gcube.portlets.user.tdtemplateoperation.shared.TdBaseComboDataBean;
import org.gcube.portlets.user.tdtemplateoperation.shared.TdColumnData;
import org.gcube.portlets.user.tdtemplateoperation.shared.TdPeriodType;
import org.gcube.portlets.user.tdtemplateoperation.shared.action.TimeAggregationColumnAction;

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
import com.google.gwt.user.client.ui.HTML;
import com.google.web.bindery.event.shared.EventBus;
import com.sencha.gxt.cell.core.client.form.ComboBoxCell.TriggerAction;
import com.sencha.gxt.core.client.IdentityValueProvider;
import com.sencha.gxt.core.client.Style.SelectionMode;
import com.sencha.gxt.core.client.dom.ScrollSupport.ScrollMode;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.event.StoreDataChangeEvent;
import com.sencha.gxt.data.shared.event.StoreDataChangeEvent.StoreDataChangeHandler;
import com.sencha.gxt.data.shared.loader.ListLoadConfig;
import com.sencha.gxt.data.shared.loader.ListLoadResult;
import com.sencha.gxt.data.shared.loader.ListLoadResultBean;
import com.sencha.gxt.data.shared.loader.ListLoader;
import com.sencha.gxt.widget.core.client.FramedPanel;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.BoxLayoutContainer.BoxLayoutData;
import com.sencha.gxt.widget.core.client.container.HBoxLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.ComboBox;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.form.TextField;
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
public class AggregateByTimeColumnPanel extends FramedPanel implements DeletableContainer {
	private static final int GRID_HEIGHT = 250;
	protected String WIDTH = "640px";
	protected String HEIGHT = "520px";
	protected EventBus eventBus;
	protected TextField label = null;
	protected String columnName;
	// protected ColumnData column;

	protected TextButton aggregateByTimeButton;

	protected ListLoader<ListLoadConfig, ListLoadResult<TdColumnData>> gridLoader;
	protected Grid<TdColumnData> grid;
	protected CheckBoxSelectionModel<TdColumnData> sm;
	protected FieldLabel columnsSelectLabel;

	private VerticalLayoutContainer verticalFunctionsLayout;
	private List<TdAggregateFunction> aggregationFunctions;

	private GroupByAggregateManager aggregateFunctionMng;

	private ListStore<TdColumnData> gridStore;
	private AggregateDataFunctionPanel baseFunctionPanel;

	private ListStore<TdPeriodType> storeCombo;
	private ComboBox<TdPeriodType> comboTimeTypes;

	private List<TdColumnData> timeDimensionsColumns = new ArrayList<TdColumnData>(1);
	private List<TdColumnData> otherColumns;
	private HTML error = new HTML();
	private boolean errorCase = false;
	private ServerObjectId serverObjectId;

	public AggregateByTimeColumnPanel(ServerObjectId serverObjectId, String columnName,
			EventBus eventBus) {
		this.columnName = columnName;
		this.serverObjectId = serverObjectId;
		this.eventBus = eventBus;
		this.aggregateFunctionMng = new GroupByAggregateManager(this);
//		Log.debug("AggregateByTimeColumnPanel(): [" + serverObjectId.toString()
//				+ " columnName: " + columnName + "]");
		init();
		build();

		Scheduler.get().scheduleDeferred(new ScheduledCommand() {

			public void execute() {
				loadAggregateFunctions();

			}
		});

		setEnableGroupByButton(false);

	}

	public void init() {
		setWidth(WIDTH);
		setHeight(HEIGHT);
		setHeaderVisible(false);
		setBodyBorder(false);
	}

	public void errorText(String text, boolean visible) {
		String html = "<p><img src=\""
				+ ResourceBundleTemplateOperation.INSTANCE.alert().getSafeUri()
						.asString()
				+ "\"/><span style=\"color:red; font-size:11px; margin-left:1px; vertical-align:middle;\">"
				+ text + "</span></p>";
		error.setHTML(html);
	}

	protected void build() {

		TdColumnDataPropertiesAccess props = GWT.create(TdColumnDataPropertiesAccess.class);

		ColumnConfig<TdColumnData, String> labelCol = new ColumnConfig<TdColumnData, String>(props.label());
		
		IdentityValueProvider<TdColumnData> identity = new IdentityValueProvider<TdColumnData>();
		sm = new CheckBoxSelectionModel<TdColumnData>(identity);

		List<ColumnConfig<TdColumnData, ?>> l = new ArrayList<ColumnConfig<TdColumnData, ?>>();
		l.add(sm.getColumn());
		l.add(labelCol);
		ColumnModel<TdColumnData> cm = new ColumnModel<TdColumnData>(l);

		gridStore = new ListStore<TdColumnData>(props.id());

		gridStore.addStoreDataChangeHandler(new StoreDataChangeHandler<TdColumnData>() {

					@Override
					public void onDataChange(
							StoreDataChangeEvent<TdColumnData> event) {
						List<TdColumnData> cols = event.getSource().getAll();
						Log.debug("Columns:" + cols.size());
						if (columnName != null) {
							for (TdColumnData c : cols) {
								if (c.getName().compareTo(columnName) == 0) {
									sm.select(c, false);
									sm.refresh();
									break;
								}
							}
						}

					}
				});
		grid = new Grid<TdColumnData>(gridStore, cm);

		sm.setSelectionMode(SelectionMode.MULTI);
		grid.setLoader(gridLoader);
		grid.setSelectionModel(sm);
		grid.setHeight(GRID_HEIGHT);

		grid.getView().setStripeRows(true);
		grid.getView().setColumnLines(true);
		grid.getView().setAutoFill(true);
		grid.setBorders(false);
		grid.setLoadMask(true);
		grid.setColumnReordering(true);
		grid.setColumnResize(false);

		// Delete Button
		aggregateByTimeButton = new TextButton("Time Aggregation");
		aggregateByTimeButton.setIcon(ResourceBundleTemplateOperation.INSTANCE
				.timeaggregate());

		aggregateByTimeButton.addSelectHandler(new SelectHandler() {

			@Override
			public void onSelect(SelectEvent event) {
				boolean isValidForm = validateGroupByTimeForm();

				if (isValidForm)
					callStartAggreagateByTime(getSelectedItems());

			}
		});

		sm.addSelectionHandler(new SelectionHandler<TdColumnData>() {

			@Override
			public void onSelection(SelectionEvent<TdColumnData> event) {
				if (getSelectedItems().size() > 0) {
					// verticalFunctionsLayoutEnable(true);
					if (!errorCase)
						setEnableGroupByButton(true);
				} else {
					// verticalFunctionsLayoutEnable(false);
					setEnableGroupByButton(false);
				}
			}
		});

		FieldLabel columnsLabel = new FieldLabel(null, "Group Column/s:");
		columnsLabel.setLabelWidth(150);

		VerticalLayoutContainer v = new VerticalLayoutContainer();
		v.setScrollMode(ScrollMode.AUTOY);
		v.setAdjustForScroll(true);
		v.add(columnsLabel, new VerticalLayoutData(1, -1, new Margins(2, 1, 5,
				1)));
		v.add(grid, new VerticalLayoutData(1, -1, new Margins(0)));

		initComboTimeTypes();

		v.add(new FieldLabel(null, "Choose Period"), new VerticalLayoutData(1,
				-1));
		v.add(comboTimeTypes, new VerticalLayoutData(1, -1, new Margins(0)));

		v.add(new FieldLabel(null, "Aggregate For"), new VerticalLayoutData(1,
				-1));

		verticalFunctionsLayout = new VerticalLayoutContainer();
		verticalFunctionsLayout.setScrollMode(ScrollMode.AUTOY);
		verticalFunctionsLayout.setAdjustForScroll(true);

		baseFunctionPanel = new AggregateDataFunctionPanel(1,gridStore.getAll(), aggregationFunctions);
		verticalFunctionsLayout.add(baseFunctionPanel.getPanel(),
				new VerticalLayoutData(1, -1, new Margins(0)));

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
		hBox.add(aggregateByTimeButton, new BoxLayoutData(new Margins(2, 5, 2,
				5)));
		v.add(hBox, new VerticalLayoutData(-1, -1, new Margins(10, 0, 10, 0)));

		v.add(error, new VerticalLayoutData(1, -1, new Margins(0, 1, 10, 1)));

		add(v, new VerticalLayoutData(1, -1, new Margins(0)));

	}

	protected void setEnableGroupByButton(boolean bool) {
		aggregateByTimeButton.setEnabled(bool);
	}

	protected void verticalFunctionsLayoutEnable(boolean bool) {
		verticalFunctionsLayout.setEnabled(bool);
	}

	protected ArrayList<TdColumnData> getSelectedItems() {
		return new ArrayList<TdColumnData>(grid.getSelectionModel()
				.getSelectedItems());

	}

	public void update(String columnName) {
		this.columnName = columnName;
		gridLoader.load();
	}
	
	protected void loadOthersTdColumnData(List<TdColumnData> result){
		otherColumns = (ArrayList<TdColumnData>) result;
		gridStore.clear();
		gridStore.addAll(otherColumns);
		baseFunctionPanel.updateColumns(otherColumns);
	}

	protected void loadTimeDimensionData(List<TdColumnData> result) {
		
		if (result != null) {
			timeDimensionsColumns = (ArrayList<TdColumnData>) result;
			if (timeDimensionsColumns.isEmpty()) {
				UtilsGXT3.alert("Attention", "No time dimension present in this Template!");
				errorText("Operation not available TIME DIMENSION column not found", true);
				errorHandler(true);

			} else {
				getHierarchicalRelationshipPeriodDataType(timeDimensionsColumns.get(0));
			}
		}
	}

	protected void getHierarchicalRelationshipPeriodDataType(final TdColumnData columnData) {
		
		if(columnData.getPeriodType()!=null){
		
		TdTemplateOperation.templateOperationService.getSuperiorPeriodType(columnData.getPeriodType(), new AsyncCallback<List<TdPeriodType>>() {

			@Override
			public void onFailure(Throwable arg0) {

				UtilsGXT3.alert("Error","Error on recovering PeriodType!");
				
			}

			@Override
			public void onSuccess(List<TdPeriodType> types) {
				
				if (types == null || types.size() == 0) {
					errorText("Operation not available for Period Type: "+ columnData.getPeriodType(), true);
					errorHandler(true);
				} else {
					comboTimeTypes.clear();
					comboTimeTypes.reset();
					comboTimeTypes.getStore().clear();
					comboTimeTypes.getStore().addAll(types);
					comboTimeTypes.redraw();

				}
				
			}
		});
		}else {
			errorText("Operation not available for this: "+columnData.getServerId().getType(), true);
			errorHandler(true);
		}
	}

	protected boolean validateGroupByTimeForm() {

		ArrayList<TdColumnData> selectedColumns = getSelectedItems();
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

			isValid = comboTimeTypes.isValid();
			if (!isValid) {
				comboTimeTypes.markInvalid("You must select a Period");
				return false;
			}

			return true;
		}
	}

	private void callStartAggreagateByTime(ArrayList<TdColumnData> columns) {

		GWT.log("Building AggregationColumnSession");

		TimeAggregationColumnAction aggregationColumnAction = new TimeAggregationColumnAction();

		aggregationColumnAction.setGroupColumns(getSelectedItems());
		aggregationColumnAction.setServerObjectId(serverObjectId);

		List<AggregateDataFunctionPanel> aggregates = aggregateFunctionMng
				.getAggregatePanels();

		// ADDING BASE AGGREGATE
		AggregatePair pair = new AggregatePair();
		TdColumnData cd = baseFunctionPanel.getColumnDataSelected();
		pair.setColumnData(cd);

		TdAggregateFunction function = baseFunctionPanel.getFunctionSelected();
		pair.setAggegrateFunction(function);

		aggregationColumnAction.addFunctionOnColumn(pair);

		// ADDING OTHERS AGGREGATE
		for (AggregateDataFunctionPanel comboColumnDataFunctionPanel : aggregates) {

			if (comboColumnDataFunctionPanel != null) {
				AggregatePair aggPair = new AggregatePair();
				TdColumnData columnData = comboColumnDataFunctionPanel
						.getColumnDataSelected();
				aggPair.setColumnData(columnData);

				TdAggregateFunction funcSelected = comboColumnDataFunctionPanel
						.getFunctionSelected();
				aggPair.setAggegrateFunction(funcSelected);

				aggregationColumnAction.addFunctionOnColumn(aggPair);
			}
		}

		aggregationColumnAction.setTimeColumns(timeDimensionsColumns);
		aggregationColumnAction.setPeriodType(comboTimeTypes.getCurrentValue());
		GWT.log("Builded TimeAggregationColumnAction: " + aggregationColumnAction);
		eventBus.fireEvent(new ActionCompletedEvent(aggregationColumnAction));
	}

	private void loadAggregateFunctions() {

		TdTemplateOperation.templateOperationService.getListAggregationFunctionIds(
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
		aggregateFunctionMng.appendAggregate(otherColumns, aggregationFunctions);
	}

	private void updateAggregatorFunctions() {
		AggregateDataFunctionPanel functionPanel = aggregateFunctionMng
				.getLastColumnDataFunctionPanel();
		if (functionPanel != null)
			verticalFunctionsLayout.add(functionPanel.getPanel(),
					new VerticalLayoutData(1, -1, new Margins(0)));

	}

	@Override
	public void deleteFired(VerticalLayoutContainer panel) {
		try {
			verticalFunctionsLayout.remove(panel);
		} catch (Exception e) {
			GWT.log("error on deleting " + panel);
		}
		verticalFunctionsLayout.forceLayout();
	}

	/**
	 * 
	 */
	protected void initComboTimeTypes() {

		// Column Data
		ComboTimeTypeProperties propsTimeTypeCombo = GWT
				.create(ComboTimeTypeProperties.class);
		storeCombo = new ListStore<TdPeriodType>(propsTimeTypeCombo.id());

		GWT.log("StoreTimeType created");

		comboTimeTypes = new ComboBox<TdPeriodType>(storeCombo,
				propsTimeTypeCombo.label());

		GWT.log("Combo TimeType created");

		addHandlersForComboOperator(propsTimeTypeCombo.label());

		// comboTimeTypes.setLoader(loader);
		comboTimeTypes.setEmptyText("Select a time type...");
		comboTimeTypes.setWidth(150);
		comboTimeTypes.setTypeAhead(false);
		comboTimeTypes.setEditable(false);
		comboTimeTypes.setTriggerAction(TriggerAction.ALL);

		comboTimeTypes.setAllowBlank(false);
	}

	protected void addHandlersForComboOperator(
			final LabelProvider<TdBaseComboDataBean> labelProvider) {
		comboTimeTypes
				.addSelectionHandler(new SelectionHandler<TdPeriodType>() {
					public void onSelection(SelectionEvent<TdPeriodType> event) {
						TdPeriodType periodType = event.getSelectedItem();
						updateComboOperatorStatus(periodType);
					}

				});
	}

	protected void updateComboOperatorStatus(TdPeriodType periodType) {

	}

	/**
	 * @param loadConfig
	 * @param callback
	 */
	protected void loadDataForTimeTypes(ListLoadConfig loadConfig,
			final AsyncCallback<ListLoadResult<TdPeriodType>> callback) {

		GWT.log("loadDataForPeriodType");
		TdTemplateOperation.templateOperationService.getListTimeTypes(
				new AsyncCallback<List<TdPeriodType>>() {

					@Override
					public void onFailure(Throwable caught) {
						callback.onFailure(caught);

					}

					@Override
					public void onSuccess(List<TdPeriodType> result) {
						GWT.log("loaded TdPeriodType having size: "
								+ result.size());
						callback.onSuccess(new ListLoadResultBean<TdPeriodType>(
								result));
					}
				});

	}

	protected void close() {
		/*
		 * if (parent != null) { parent.close(); }
		 */
	}

	public boolean isErrorCase() {
		return errorCase;
	}

	public void errorHandler(boolean error) {
		this.errorCase = error;
		setEnableGroupByButton(!error);
	}

}
