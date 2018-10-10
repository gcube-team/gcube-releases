package org.gcube.portlets.user.tdtemplateoperation.client;

import java.util.ArrayList;
import java.util.List;

import org.gcube.portlets.user.td.monitorwidget.client.utils.UtilsGXT3;
import org.gcube.portlets.user.tdtemplateoperation.client.event.ActionCompletedEvent;
import org.gcube.portlets.user.tdtemplateoperation.client.properties.ComboBaseDataPropertiesCombo;
import org.gcube.portlets.user.tdtemplateoperation.client.properties.ComboColumnDataPropertiesCombo;
import org.gcube.portlets.user.tdtemplateoperation.client.resources.ResourceBundleTemplateOperation;
import org.gcube.portlets.user.tdtemplateoperation.shared.CreateTimeDimensionOptions;
import org.gcube.portlets.user.tdtemplateoperation.shared.ServerObjectId;
import org.gcube.portlets.user.tdtemplateoperation.shared.TdBaseData;
import org.gcube.portlets.user.tdtemplateoperation.shared.TdColumnData;
import org.gcube.portlets.user.tdtemplateoperation.shared.TdPeriodType;
import org.gcube.portlets.user.tdtemplateoperation.shared.action.CreateTimeDimensionColumnAction;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.web.bindery.event.shared.EventBus;
import com.sencha.gxt.cell.core.client.form.ComboBoxCell.TriggerAction;
import com.sencha.gxt.core.client.dom.ScrollSupport.ScrollMode;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.data.shared.ListStore;
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

/**
 * 
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Jun 10, 2014
 * 
 */
public class CreateTimeDimensionColumnPanel extends FramedPanel implements DeletableContainer {
	protected String WIDTH = "500px";
	protected String HEIGHT = "250px";
	protected EventBus eventBus;
	protected TextField label = null;
	protected String columnName;
	// protected ColumnData column;

	protected TextButton createTimeGroupButton = new TextButton("Group by Time");

	protected ListLoader<ListLoadConfig, ListLoadResult<TdColumnData>> gridLoader;
//	protected Grid<TdColumnData> grid;
	protected CheckBoxSelectionModel<TdColumnData> sm;
	protected FieldLabel columnsSelectLabel;

	private VerticalLayoutContainer verticalOptionsLayout;
	private ListStore<TdColumnData> gridStore;

	private ListStore<TdPeriodType> storeTimeCombo;
//	private ComboBox<TdPeriodType> comboTimeTypes;

	
	private HTML error = new HTML();
	private boolean errorCase = false;
	private ServerObjectId serverObjectId;
	
	private ListStore<TdBaseData> storeUsingColumns;
	private ComboBox<TdBaseData> comboUsingColumns;

	private ListStore<TdColumnData> storeYearColumns;
	private ComboBox<TdColumnData> comboYearColumns;
	
	private ListStore<TdColumnData> storeMonthColumns;
	private ComboBox<TdColumnData> comboMonthColumns;
	
	private ListStore<TdColumnData> storeQuarterColumns;
	private ComboBox<TdColumnData> comboQuarterColumns;
	
	private ListStore<TdColumnData> storeDayColumns;
	private ComboBox<TdColumnData> comboDayColumns;
	
	private List<TdColumnData> listColumns;
	
//	private ListStore<TdColumnData> storeOtherColumns;
//	private ComboBox<TdColumnData> comboOtherColumns;
	
//	private TdPeriodType nonePeriod = new TdPeriodType("None", "<None>");
	private TdPeriodType yearPeriod;
//	private SimpleCheckBox checkBoxActiveMultiDimension;
	private Command onClose;

	
	public CreateTimeDimensionColumnPanel(ServerObjectId serverObjectId, List<TdColumnData> listColumns, EventBus eventBus, Command onClose) {
		this.serverObjectId = serverObjectId;
		this.eventBus = eventBus;
		this.listColumns = listColumns;
		this.onClose = onClose;
		init();
		build();
		setEnableGroupByButton(false);
		
		TdTemplateOperation.templateOperationService.getYearTimeDimension(new AsyncCallback<TdPeriodType>() {

			@Override
			public void onFailure(Throwable caught) {
			}

			@Override
			public void onSuccess(TdPeriodType result) {
				yearPeriod = result;
			}
		});
		
		
	}

	public void init() {
		setWidth(WIDTH);
		setHeight(HEIGHT);
		setHeaderVisible(false);
		setBodyBorder(false);
		
//		storeUsingColumns = new ListStore<TdColumnData>(keyProvider)
//		comboUsingColumns 
		
		initComboUsingColumns();
		initComboYearColumns();
		initComboMonthColumns();
		initComboQuarterColumns();
		initComboDayColumns();
//		initComboTimeTypes();
//		initComboOtherColumns();
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

		VerticalLayoutContainer v = new VerticalLayoutContainer();
		v.setScrollMode(ScrollMode.AUTOY);
		v.setAdjustForScroll(true);

		v.add(new FieldLabel(null, "Create using column/s"));
		v.add(comboUsingColumns, new VerticalLayoutData(1, -1, new Margins(0)));

		verticalOptionsLayout = new VerticalLayoutContainer();
		verticalOptionsLayout.setScrollMode(ScrollMode.AUTOY);
		verticalOptionsLayout.setAdjustForScroll(true);

		v.add(verticalOptionsLayout);
		
		HBoxLayoutContainer hBox = new HBoxLayoutContainer();
		hBox.add(createTimeGroupButton, new BoxLayoutData(new Margins(2, 5, 2,5)));
		v.add(hBox, new VerticalLayoutData(-1, -1, new Margins(10, 0, 10, 0)));

		v.add(error, new VerticalLayoutData(1, -1, new Margins(0, 1, 10, 1)));

		add(v, new VerticalLayoutData(1, -1, new Margins(0)));
		
		createTimeGroupButton.addSelectHandler(new SelectHandler() {
			
			@Override
			public void onSelect(SelectEvent event) {
				boolean isValidForm = validateGroupByTimeForm();

				if (isValidForm)
					callStartGropuByTime();
				
			}
		});
	}

	protected void setEnableGroupByButton(boolean bool) {
		createTimeGroupButton.setEnabled(bool);
	}

	protected void verticalFunctionsLayoutEnable(boolean bool) {
		verticalOptionsLayout.setEnabled(bool);
	}

	public void update(String columnName) {
		this.columnName = columnName;
		gridLoader.load();
	}
	
	protected void loadColumns(List<TdColumnData> result){
		listColumns = (ArrayList<TdColumnData>) result;
		gridStore.clear();
		gridStore.addAll(listColumns);
	}

	private void callStartGropuByTime() {
		TdBaseData usingCol = comboUsingColumns.getCurrentValue();
		CreateTimeDimensionOptions option = CreateTimeDimensionOptions.valueOf(usingCol.getId());
		TdColumnData[] tdColumnData = null;
		switch (option) {
		case YEAR:
			tdColumnData = new TdColumnData[1];
			tdColumnData[0] = comboYearColumns.getCurrentValue();
			break;

		case YEAR_MONTH:
			tdColumnData = new TdColumnData[2];
			tdColumnData[0] = comboYearColumns.getCurrentValue();
			tdColumnData[1] = comboMonthColumns.getCurrentValue();
			break;
			
		case YEAR_QUARTER:
			tdColumnData = new TdColumnData[2];
			tdColumnData[0] = comboYearColumns.getCurrentValue();
			tdColumnData[1] = comboQuarterColumns.getCurrentValue();
			break;
			
		case YEAR_MONTH_DAY:
			tdColumnData = new TdColumnData[3];
			tdColumnData[0] = comboYearColumns.getCurrentValue();
			tdColumnData[1] = comboMonthColumns.getCurrentValue();
			tdColumnData[2] = comboDayColumns.getCurrentValue();
			break;
		}
		

		CreateTimeDimensionColumnAction groupAction = new CreateTimeDimensionColumnAction(option, tdColumnData);
		
		GWT.log("Builded GroupTimeColumnAction: " + groupAction);
//		GWT.log("eventBus: " + eventBus);
		eventBus.fireEvent(new ActionCompletedEvent(groupAction));
		
		if(onClose!=null)
			onClose.execute();
	}

	@Override
	public void deleteFired(VerticalLayoutContainer panel) {
		try {
			verticalOptionsLayout.remove(panel);
		} catch (Exception e) {
			GWT.log("error on deleting " + panel);
		}
		verticalOptionsLayout.forceLayout();
	}
	
	private void resetPanel() {
		verticalOptionsLayout.clear();
		
		comboYearColumns.reset();
		comboYearColumns.clear();
		
		comboMonthColumns.reset();
		comboMonthColumns.clear();
		
		comboQuarterColumns.reset();
		comboQuarterColumns.clear();
		
		comboDayColumns.reset();
		comboDayColumns.clear();
	}
	
	private void setPanelToOption(CreateTimeDimensionOptions valueOf) {
		switch (valueOf) {
			case YEAR:
				verticalOptionsLayout.add(new FieldLabel(null, "Year"), new VerticalLayoutData(1,-1));
				verticalOptionsLayout.add(comboYearColumns, new VerticalLayoutData(1, -1, new Margins(0)));
				break;
	
			case YEAR_MONTH:
				verticalOptionsLayout.add(new FieldLabel(null, "Year"), new VerticalLayoutData(1,-1));
				verticalOptionsLayout.add(comboYearColumns, new VerticalLayoutData(1, -1, new Margins(0)));
				verticalOptionsLayout.add(new FieldLabel(null, "Month"), new VerticalLayoutData(1,-1));
				verticalOptionsLayout.add(comboMonthColumns, new VerticalLayoutData(1, -1, new Margins(0)));
				break;
				
			case YEAR_QUARTER:
				verticalOptionsLayout.add(new FieldLabel(null, "Year"), new VerticalLayoutData(1,-1));
				verticalOptionsLayout.add(comboYearColumns, new VerticalLayoutData(1, -1, new Margins(0)));
				verticalOptionsLayout.add(new FieldLabel(null, "Quarter"), new VerticalLayoutData(1,-1));
				verticalOptionsLayout.add(comboQuarterColumns, new VerticalLayoutData(1, -1, new Margins(0)));
				break;
				
			case YEAR_MONTH_DAY:
				verticalOptionsLayout.add(new FieldLabel(null, "Year"), new VerticalLayoutData(1,-1));
				verticalOptionsLayout.add(comboYearColumns, new VerticalLayoutData(1, -1, new Margins(0)));
				verticalOptionsLayout.add(new FieldLabel(null, "Month"), new VerticalLayoutData(1,-1));
				verticalOptionsLayout.add(comboMonthColumns, new VerticalLayoutData(1, -1, new Margins(0)));
				verticalOptionsLayout.add(new FieldLabel(null, "Day"), new VerticalLayoutData(1,-1));
				verticalOptionsLayout.add(comboDayColumns, new VerticalLayoutData(1, -1, new Margins(0)));
				break;
		}
		
	}
	
	/**
	 * 
	 */
	protected void initComboUsingColumns() {
		
		// Column Data
		ComboBaseDataPropertiesCombo propsColumnData = GWT.create(ComboBaseDataPropertiesCombo.class);
		storeUsingColumns = new ListStore<TdBaseData>(propsColumnData.id());
		
		for (CreateTimeDimensionOptions option : CreateTimeDimensionOptions.values()) {
			storeUsingColumns.add(new TdBaseData(option.name(), option.getLabel()) {
			});
		}
		
		comboUsingColumns = new ComboBox<TdBaseData>(storeUsingColumns, propsColumnData.label());
		comboUsingColumns.setAllowBlank(false);

		comboUsingColumns.addSelectionHandler(new SelectionHandler<TdBaseData>() {
			
			public void onSelection(SelectionEvent<TdBaseData> event) {
					resetPanel();
					TdBaseData option = comboUsingColumns.getCurrentValue();
					setPanelToOption(CreateTimeDimensionOptions.valueOf(option.getId()));
			}
		});

		comboUsingColumns.setEmptyText("Select...");
		comboUsingColumns.setWidth(150);
		comboUsingColumns.setTypeAhead(false);
		comboUsingColumns.setEditable(false);
		comboUsingColumns.setTriggerAction(TriggerAction.ALL);
	}
	
	/**
	 * 
	 */
	protected void initComboYearColumns() {
		
		// Column Data
		ComboColumnDataPropertiesCombo propsColumnData = GWT.create(ComboColumnDataPropertiesCombo.class);
		storeYearColumns = new ListStore<TdColumnData>(propsColumnData.id());
		
		if(listColumns!=null)
			storeYearColumns.addAll(listColumns);
		
		comboYearColumns = new ComboBox<TdColumnData>(storeYearColumns, propsColumnData.label());
		comboYearColumns.setAllowBlank(false);

		comboYearColumns.addSelectionHandler(new SelectionHandler<TdColumnData>() {
			
			public void onSelection(SelectionEvent<TdColumnData> event) {
				TdBaseData option = comboUsingColumns.getCurrentValue();
				changeYearDependencyColumns(CreateTimeDimensionOptions.valueOf(option.getId()));
				setEnableGroupByButton(true);
			}

		});

		comboYearColumns.setEmptyText("Select a column...");
		comboYearColumns.setWidth(150);
		comboYearColumns.setTypeAhead(false);
		comboYearColumns.setEditable(false);
		comboYearColumns.setTriggerAction(TriggerAction.ALL);
	}
	
	/**
	 * 
	 */
	private void initComboQuarterColumns() {
		// Column Data
		ComboColumnDataPropertiesCombo propsColumnData = GWT.create(ComboColumnDataPropertiesCombo.class);
		storeQuarterColumns = new ListStore<TdColumnData>(propsColumnData.id());
		
		comboQuarterColumns = new ComboBox<TdColumnData>(storeQuarterColumns, propsColumnData.label());
		comboQuarterColumns.setAllowBlank(false);

		comboQuarterColumns.addSelectionHandler(new SelectionHandler<TdColumnData>() {
			
			public void onSelection(SelectionEvent<TdColumnData> event) {
				
			}

		});

		comboQuarterColumns.setEmptyText("Select a column...");
		comboQuarterColumns.setWidth(150);
		comboQuarterColumns.setTypeAhead(false);
		comboQuarterColumns.setEditable(false);
		comboQuarterColumns.setTriggerAction(TriggerAction.ALL);
		
	}
	
	/**
	 * 
	 */
	protected void initComboMonthColumns() {
		
		// Column Data
		ComboColumnDataPropertiesCombo propsColumnData = GWT.create(ComboColumnDataPropertiesCombo.class);
		storeMonthColumns = new ListStore<TdColumnData>(propsColumnData.id());
		
		comboMonthColumns = new ComboBox<TdColumnData>(storeMonthColumns, propsColumnData.label());
		comboMonthColumns.setAllowBlank(false);

		comboMonthColumns.addSelectionHandler(new SelectionHandler<TdColumnData>() {
			
			public void onSelection(SelectionEvent<TdColumnData> event) {
				changeMonthDependencyColumns();
			}

		});

		comboMonthColumns.setEmptyText("Select a column...");
		comboMonthColumns.setWidth(150);
		comboMonthColumns.setTypeAhead(false);
		comboMonthColumns.setEditable(false);
		comboMonthColumns.setTriggerAction(TriggerAction.ALL);
	}
	
	/**
	 * 
	 */
	private void initComboDayColumns() {
		
		// Column Data
		ComboColumnDataPropertiesCombo propsColumnData = GWT.create(ComboColumnDataPropertiesCombo.class);
		storeDayColumns = new ListStore<TdColumnData>(propsColumnData.id());
		
		comboDayColumns = new ComboBox<TdColumnData>(storeDayColumns, propsColumnData.label());
		comboDayColumns.setAllowBlank(false);

		comboDayColumns.addSelectionHandler(new SelectionHandler<TdColumnData>() {
			
			public void onSelection(SelectionEvent<TdColumnData> event) {
			}
		});

		comboDayColumns.setEmptyText("Select a column...");
		comboDayColumns.setWidth(150);
		comboDayColumns.setTypeAhead(false);
		comboDayColumns.setEditable(false);
		comboDayColumns.setTriggerAction(TriggerAction.ALL);
		
	}
	
	/**
	 * 
	 */
	@SuppressWarnings("incomplete-switch")
	protected void changeYearDependencyColumns(CreateTimeDimensionOptions option) {
		comboDayColumns.reset();
		comboDayColumns.clear();
		
		TdColumnData yearSelected = comboYearColumns.getCurrentValue();
		
		if(yearSelected==null)
			return;
		
		GWT.log("changeYearDependencyColumns option: "+option);
		
		switch (option) {
		case YEAR_MONTH:
			comboMonthColumns.reset();
			comboMonthColumns.clear();
			storeMonthColumns.clear();
			
			for (TdColumnData col : listColumns) {
				GWT.log("Compare result: "+col.compareTo(yearSelected) +" with yearSelected: "+yearSelected);
				if(col.compareTo(yearSelected)!=0){
					storeMonthColumns.add(col);
				}
			}
			
			comboMonthColumns.redraw();
			break;

		case YEAR_MONTH_DAY:
			comboMonthColumns.reset();
			comboMonthColumns.clear();
			storeMonthColumns.clear();
			
			for (TdColumnData col : listColumns) {
				GWT.log("Compare result: "+col.compareTo(yearSelected) +" with yearSelected: "+yearSelected);
				if(col.compareTo(yearSelected)!=0){
					storeMonthColumns.add(col);
				}
			}
			
			comboMonthColumns.redraw();
			changeMonthDependencyColumns();
			break;
			
		case YEAR_QUARTER:
			comboQuarterColumns.reset();
			comboQuarterColumns.clear();
			storeQuarterColumns.clear();
			for (TdColumnData col : listColumns) {
				GWT.log("Compare result: "+col.compareTo(yearSelected) +" with yearSelected: "+yearSelected);
				if(col.compareTo(yearSelected)!=0){
					storeQuarterColumns.add(col);
				}
			}
			
			comboQuarterColumns.redraw();
			break;
		}
	}
	
	/**
	 * 
	 */
	protected void changeMonthDependencyColumns() {
		comboDayColumns.reset();
		comboDayColumns.clear();
		storeDayColumns.clear();
		
		TdColumnData monthSelected = comboMonthColumns.getCurrentValue();
		TdColumnData yearSelected = comboYearColumns.getCurrentValue();
		if(monthSelected==null)
			return;
		
		for (TdColumnData col : listColumns) {
			GWT.log("Compare result: "+col.compareTo(monthSelected) +" with monthSelected: "+monthSelected +" and year selected: "+yearSelected);
			if(col.compareTo(monthSelected)!=0 && col.compareTo(yearSelected)!=0){
				storeDayColumns.add(col);
			}
		}
		
		comboDayColumns.redraw();
	}
	
	protected void addListColumns(List<TdColumnData> columns){
		if(listColumns!=null)
			listColumns.clear();
		else{
			listColumns = new ArrayList<TdColumnData>(columns.size());
		}
		
		listColumns.addAll(columns);
		storeYearColumns.addAll(listColumns);
		storeDayColumns.addAll(listColumns);
		storeMonthColumns.addAll(listColumns);
		storeQuarterColumns.addAll(listColumns);
	}

	@SuppressWarnings("incomplete-switch")
	protected boolean validateGroupByTimeForm() {

		TdColumnData selectedColumn = comboYearColumns.getCurrentValue();
		if (selectedColumn == null) {
			UtilsGXT3.alert("Attention", "Set a Year!");
			return false;
			
		}else {
			
			 TdBaseData usingCol = comboUsingColumns.getCurrentValue();
			 CreateTimeDimensionOptions option = CreateTimeDimensionOptions.valueOf(usingCol.getId());
				
			switch (option) {
			case YEAR_MONTH:
				
				if(comboMonthColumns.getCurrentValue()==null){
					UtilsGXT3.alert("Attention", "Set a Month!");
					return false;
				}
				return true;

			case YEAR_QUARTER:
				
				if(comboQuarterColumns.getCurrentValue()==null){
					UtilsGXT3.alert("Attention", "Set a Quarter!");
					return false;
				}
				return true;
				
			case YEAR_MONTH_DAY:
				
				if(comboMonthColumns.getCurrentValue()==null){
					UtilsGXT3.alert("Attention", "Set a Month!");
					return false;
				}
				
				if(comboDayColumns.getCurrentValue()==null){
					UtilsGXT3.alert("Attention", "Set a Day!");
					return false;
				}
				return true;
			}
		
			return true;
		}
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
