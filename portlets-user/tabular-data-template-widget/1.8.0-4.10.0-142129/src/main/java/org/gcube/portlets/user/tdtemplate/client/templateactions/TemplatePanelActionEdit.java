/**
 * 
 */
package org.gcube.portlets.user.tdtemplate.client.templateactions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnData;
import org.gcube.portlets.user.tdtemplate.client.TdTemplateConstants;
import org.gcube.portlets.user.tdtemplate.client.TdTemplateController;
import org.gcube.portlets.user.tdtemplate.client.TdTemplateControllerActions;
import org.gcube.portlets.user.tdtemplate.client.ZIndexReference;
import org.gcube.portlets.user.tdtemplate.client.event.operation.UndoLastOperationEvent;
import org.gcube.portlets.user.tdtemplate.client.resources.TdTemplateAbstractResources;
import org.gcube.portlets.user.tdtemplate.client.templatecreator.view.EditableLabelColumnDefinitionView;
import org.gcube.portlets.user.tdtemplate.client.templatecreator.view.TemplateSwitcherInteface;
import org.gcube.portlets.user.tdtemplate.client.templatecreator.view.suggestion.HtmlLabel;
import org.gcube.portlets.user.tdtemplate.client.templatecreator.view.suggestion.HtmlLegend;
import org.gcube.portlets.user.tdtemplate.client.templatecreator.view.suggestion.HtmlSeeMore;
import org.gcube.portlets.user.tdtemplate.client.templatecreator.view.suggestion.SuggestionContainer;
import org.gcube.portlets.user.tdtemplate.shared.TdColumnDefinition;
import org.gcube.portlets.user.tdtemplate.shared.TdTColumnCategory;
import org.gcube.portlets.user.tdtemplate.shared.TdTDataType;
import org.gcube.portlets.user.tdtemplate.shared.TdTFormatReference;
import org.gcube.portlets.user.tdtemplate.shared.TemplateExpression;
import org.gcube.portlets.user.tdtemplate.shared.util.CutStringUtil;
import org.gcube.portlets.user.tdtemplateoperation.client.CreateTimeDimensionColumnDialog;
import org.gcube.portlets.user.tdtemplateoperation.client.NormalizeColumnDialog;
import org.gcube.portlets.user.tdtemplateoperation.shared.TdColumnData;

import com.extjs.gxt.ui.client.Style.ButtonArrowAlign;
import com.extjs.gxt.ui.client.Style.ButtonScale;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.IconAlign;
import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Text;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.SeparatorToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTMLTable;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.SimpleEventBus;


/**
 * The Class TemplatePanelActionUpdater.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Mar 30, 2015
 */
public class TemplatePanelActionEdit {

	public static final String DEFINITION_AND_VALIDATION_POST_OPERATIONS = "> Definition and Validation Post Operations";
	private int numColumns;
	private FlexTable flexTableTemplate = new FlexTable();
	private ToolBar toolbar;
	private ContentPanel tableContainer = new ContentPanel();
	private LayoutContainer flexTableContainer = new LayoutContainer();
	private SuggestionContainer suggestionContainer = new SuggestionContainer();
	protected LayoutContainer centralContainer = new LayoutContainer();
	private BorderLayout borderLayout;
	private BorderLayoutData centerData;
	private BorderLayoutData southData;
//	private HtmlLabel lastOperatioLabel = new HtmlLabel("", TdTemplateConstants.LATEST_OPERATION+": "+TdTemplateConstants.NONE, "");
	private HtmlSeeMore lastOperatioSeeMore = new HtmlSeeMore(TdTemplateConstants.LATEST_OPERATION+": "+TdTemplateConstants.NONE, 100);
	protected ContentPanel principalContainer;
	protected ContentPanel southContainer;
	
	private List<TdColumnDefinition> timeDimensionCls = new ArrayList<TdColumnDefinition>();
	private List<TdColumnDefinition> otherColumns = new ArrayList<TdColumnDefinition>();
	private List<TdColumnDefinition> allColumns = new ArrayList<TdColumnDefinition>();
	
	private Button aggregateByTimeButton;
	private SimpleEventBus actionControlleEventBus;
	private Button undoLastOperation;
	private Button historyActionsButton;
	
	private TemplateSwitcherInteface switchInterface;
	private TdTemplateController templateController;
	private Button addColumnButton;
	private Button removeColumnButton;
	private MenuActionBuilder menuBuilder = new MenuActionBuilder();
	private Button createTimeDimensionButton;
	private ZIndexReference zIndexReference;
	private Button normalizeButton;
	private MenuItem createMultiColumnRule;
	private MenuItem removeMultiColumnRule;
//	private MultiColumnRuleMng multiColumnRuleMng;
	private Button addMultiColumnRule;
	private ArrayList<TemplateExpression> listTableRules;
	
		/**
		 * Instantiates a new template panel action updater.
		 */
	public TemplatePanelActionEdit() {
		initContainers();
	}


	/**
	 * Instantiates a new template panel action updater.
	 *
	 * @param switcherInterface the switcher interface
	 * @param controller the controller
	 */
	public TemplatePanelActionEdit(TemplateSwitcherInteface switcherInterface, TdTemplateController controller){
		this.switchInterface = switcherInterface;
		this.templateController = controller;
		this.zIndexReference = new ZIndexReference(controller);
		this.lastOperatioSeeMore.addStyleName("seeMoreStyle");
		initContainers();
	}
	
	/**
	 * Gets the time dimension cls.
	 *
	 * @return the timeDimensionCls
	 */
	public List<TdColumnDefinition> getTimeDimensionCls() {
		return timeDimensionCls;
	}

	/**
	 * Gets the others columns.
	 *
	 * @return the othersColumns
	 */
	public List<TdColumnDefinition> getOthersColumns() {
		return otherColumns;
	}

		/**
		 * Gets the action controlle event bus.
		 *
		 * @return the actionControlleEventBus
		 */
	public SimpleEventBus getActionControlleEventBus() {
		return actionControlleEventBus;
	}

	/**
	 * Sets the action controlle event bus.
	 *
	 * @param actionControlleEventBus the actionControlleEventBus to set
	 */
	public void setActionControlleEventBus(SimpleEventBus actionControlleEventBus) {
		this.actionControlleEventBus = actionControlleEventBus;
	}


	/**
	 * Inits the containers.
	 */
	private void initContainers() {

		this.tableContainer.setHeaderVisible(false);
		this.tableContainer.setBorders(false);
		inizializeTableTemplate();
		inizializeToolBar();
	
		this.tableContainer.setScrollMode(Scroll.AUTOX);
		
		refreshSuggestion(TdTemplateConstants.SUGGESTION, TdTemplateConstants.DO_YOU_WANT_ADD_POST_ACTIONS);
//		suggestionContainer.getElement().getStyle().setBorderColor("#32CD32");

		tableContainer.setBodyBorder(false);
		tableContainer.setBorders(false);
		
		HtmlLabel htmlLabel = new HtmlLabel(DEFINITION_AND_VALIDATION_POST_OPERATIONS, "", "");
		tableContainer.add(htmlLabel);
		
		tableContainer.add(suggestionContainer);
		
		flexTableContainer.add(flexTableTemplate);
		tableContainer.add(flexTableContainer);
		tableContainer.add(lastOperatioSeeMore);
		
		centralContainer.add(tableContainer);
		
		HashMap<String, String> map = new HashMap<String, String>(2);
		map.put("Base Columns", "#D1E6E7");
		map.put("Action Columns", "#FFE3A8");
		HtmlLegend l1 = new HtmlLegend(map);
		
		centralContainer.add(l1);
		centralContainer.setBorders(false);
		
		centralContainer.setScrollMode(Scroll.AUTOY);

		suggestionContainer.addListener(Events.Render, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent be) {
				setBorderAsOnError(false);
				
			}
		});
		
		createBoderLayout();
	}
	
	/**
	 * Refresh last operation label.
	 *
	 * @param text the text
	 * @param subText the sub text
	 */
	public void refreshLastOperationLabel(String text, String subText){
		lastOperatioSeeMore.updateMsg(text);
	}
	
	/**
	 * Reload columns from service.
	 */
	public void reloadColumnsFromService(){
		
		TdTemplateController.tdTemplateServiceAsync.reloadColumns(new AsyncCallback<List<TdColumnDefinition>>() {

			@Override
			public void onFailure(Throwable caught) {
				MessageBox.alert("Error", caught.getMessage(), null);
				
			}

			@Override
			public void onSuccess(List<TdColumnDefinition> result) {
				
				if(result!=null)
					updateColumns(result);
				
			}
		});
	}
	
	/**
	 * Reset last operation label.
	 */
	public void resetLastOperationLabel(){
		lastOperatioSeeMore.updateMsg(TdTemplateConstants.LATEST_OPERATION+": "+TdTemplateConstants.NONE);
	}
	
	/**
	 * Sets the border as on error.
	 *
	 * @param bool the new border as on error
	 */
	public void setBorderAsOnError(boolean bool){
		
		if(suggestionContainer.isRendered() && suggestionContainer.getElement("body")!=null){
			if(bool)
				suggestionContainer.getElement("body").getStyle().setBorderColor("#FF2300");
			else
				suggestionContainer.getElement("body").getStyle().setBorderColor("#99BBE8");
		}
	}
	
	/**
	 * Creates the boder layout.
	 */
	private void createBoderLayout() {
		
		borderLayout = new BorderLayout();
		principalContainer = new ContentPanel(borderLayout);
		principalContainer.setHeaderVisible(false);
	
		centerData = new BorderLayoutData(LayoutRegion.CENTER);  
		centerData.setMargins(new Margins(0));		
		
	    southData = new BorderLayoutData(LayoutRegion.SOUTH, 200);  
	    southData.setSplit(true);  
	    southData.setCollapsible(true);  
	    southData.setFloatable(true);  
	    southData.setMargins(new Margins(5,0,0,0));  
//	    southData.setMaxSize(250);
//	    southData.setMinSize(50);
		southContainer = new ContentPanel();
		southContainer.setHeaderVisible(true);
		southContainer.setBorders(false);
		southContainer.setBodyBorder(false);
		southContainer.setHeading("Filters");
		southContainer.setScrollMode(Scroll.AUTOY);
		
//		centralContainer.setId("CentralContainerTemplatePanelActionUpdater");
//		tableContainer.setId("TableContainerTemplatePanelActionUpdater");
		centralContainer.setHeight(TdTemplateConstants.HEIGHT_PRINCIPAL);
		centralContainer.setScrollMode(Scroll.AUTOY);
		principalContainer.add(centralContainer, centerData);
//		principalContainer.add(southContainer, southData);
		
	}
	
	/**
	 * Refresh suggestion.
	 *
	 * @param title the title
	 * @param text the text
	 */
	public void refreshSuggestion(String title, String text){
		suggestionContainer.setSuggestion(title, text);
		tableContainer.layout();
	}
	
	/**
	 * Refresh suggestion.
	 *
	 * @param title the title
	 * @param text the text
	 * @param img the img
	 */
	public void refreshSuggestion(String title, String text, AbstractImagePrototype img){
		suggestionContainer.setSuggestion(title, text,  "", img);
		tableContainer.layout();
	}
	


	/**
	 * Update columns.
	 *
	 * @param columns the columns
	 */
	public void updateColumns(List<TdColumnDefinition> columns) {
		setColumns(columns);
	}
	
	/**
	 * Sets the columns.
	 *
	 * @param columns the new columns
	 */
	public void setColumns(List<TdColumnDefinition> columns){
		this.numColumns = columns.size();
//		this.columnsDefined = new ArrayList<ColumnDefinitionView>(numColumns);
		resetColumnsAndTable();
		allColumns.addAll(columns);
		
		initTableColumns(flexTableTemplate, 0, numColumns);
		
		for (TdColumnDefinition colm : columns) {
			updateColumnByTdColumnDefinition(colm);
		}
		
		updateOperationsAvailable();
		
//		flexTableContainer.layout(true);
	}
	
	private void resetColumnsAndTable(){
		flexTableTemplate.removeAllRows();
		timeDimensionCls.clear();
		otherColumns.clear();
		allColumns.clear();
	}
	
	/**
	 * Update operations available.
	 */
	private void updateOperationsAvailable()
	{
		menuBuilder.createMenuForRemoveColum(actionControlleEventBus, removeColumnButton, allColumns);
		menuBuilder.createMenuForTimeAggregation(actionControlleEventBus, aggregateByTimeButton, timeDimensionCls, otherColumns);
	}
	
	/**
	 * Inits the table columns.
	 *
	 * @param flexTable the flex table
	 * @param columnIndex the column index
	 * @param columnsOffset the columns offset
	 */
	public void initTableColumns(FlexTable flexTable, int columnIndex, int columnsOffset){
		
		for (int i = 0; i < columnsOffset; i++) {
			
			int indexOffset = columnIndex+i;
			
			HorizontalPanel hp = new HorizontalPanel();
			hp.setHorizontalAlign(HorizontalAlignment.CENTER);
			flexTable.setCellPadding(10);
			
			flexTable.setWidget(0, indexOffset, new Text("Column "+(indexOffset+1)));
			flexTable.setWidget(1, indexOffset, hp);
			
			flexTable.setWidget(2, indexOffset,  new Text(""));
			flexTable.getCellFormatter().getElement(2, indexOffset).setAttribute("height", "40px");
			
			
			HTMLTable.RowFormatter rf = flexTable.getRowFormatter();
			rf.addStyleName(0, "FlexTableTemplateActions-header-row");
			rf.addStyleName(1, "FlexTableTemplateActions-other-rows");
			rf.addStyleName(2, "FlexTableTemplateActions-other-rows");
		}
		
	}

	/**
	 * Inizialize table template.
	 */
	public void inizializeTableTemplate() {
		flexTableTemplate.addStyleName("FlexTableTemplateActions");
	}
	
	/**
	 * Sets the widget into table.
	 *
	 * @param rowIndex the row index
	 * @param columnDefined the column index
	 * @param widget the widget
	 */
	public void setWidgetIntoTable(int rowIndex, TdColumnDefinition columnDefined, Widget widget){
		HTMLTable.RowFormatter rf = flexTableTemplate.getRowFormatter();
//		HTMLTable.ColumnFormatter cf = flexTableTemplate.getColumnFormatter();
		
		HTMLTable.CellFormatter cellF = flexTableTemplate.getCellFormatter();
		
		if(rowIndex>0) //IS NOT HEADER COLUMN
			rf.addStyleName(rowIndex, "FlexTableTemplate-other-rows");
		else{ //IS HEADER COLUMN
			GWT.log("columnDefined "+columnDefined.getColumnName() +" is base column: "+columnDefined.isBaseColumn());
			if(columnDefined.isBaseColumn())
//				rf.addStyleName(rowIndex, "FlexTableTemplateActions-header-row"); //IS BASE COLUMN
				cellF.addStyleName(rowIndex, columnDefined.getIndex(), "FlexTableTemplateActions-header-row");
			else
//				rf.addStyleName(rowIndex, "FlexTableTemplateActions-header-row-action"); //IS ACTION COLUMN
				cellF.addStyleName(rowIndex, columnDefined.getIndex(), "FlexTableTemplateActions-header-row-action");
		}
		
		flexTableTemplate.setWidget(rowIndex, columnDefined.getIndex(), widget);
		
	}
	
	/**
	 * Clear cell.
	 *
	 * @param rowIndex the row index
	 * @param columnIndex the column index
	 */
	public void clearCell(int rowIndex, int columnIndex){
		
		try{
			GWT.log("Clear cell rowIndex, "+rowIndex+", column index "+columnIndex);
			flexTableTemplate.clearCell(rowIndex, columnIndex);	
		}catch (Exception e) {
			GWT.log("Last remove cell throw exception");
		}
	}
	
	/**
	 * Sets the column type opacity.
	 *
	 * @param row the row
	 * @param colIndex the col index
	 * @param opacity the opacity
	 */
	private void setColumnTypeOpacity(int row, int colIndex, double opacity) {
		flexTableTemplate.getCellFormatter().getElement(row, colIndex).getStyle().setOpacity(opacity);
	}
	
	/**
	 * Sets the column type opacity.
	 *
	 * @param colIndex the col index
	 * @param opacity the opacity
	 */
	private void setColumnTypeOpacity(int colIndex, double opacity) {
		setColumnTypeOpacity(1, colIndex, opacity);
	}
	
	/**
	 * Update column by td column definition.
	 *
	 * @param columnDefined the column defined
	 */
	public void updateColumnByTdColumnDefinition(TdColumnDefinition columnDefined){
		
		if(columnDefined.getIndex()>=0 && columnDefined.getIndex()<numColumns){
			
//			ColumnDefinitionView colDefView = columnsDefined.get(colm.getIndex());
			
			TdTColumnCategory tdCategory = columnDefined.getCategory();
			ColumnData columnDataRefDim = columnDefined.getColumnDataReference();
			TdTDataType tdDataType = columnDefined.getDataType();

			StringBuilder columnDefinition = new StringBuilder("");
			columnDefinition.append(tdCategory.getName());
			columnDefinition.append("<br/>");
			columnDefinition.append(tdDataType.getName());
			columnDefinition.append("<br/>");
			
			EditableLabelColumnDefinitionView edit = new EditableLabelColumnDefinitionView(zIndexReference.getZIndex()+1);
			HorizontalPanel editableLabel = edit.getEditableLabelPanel(columnDefined, zIndexReference);
			setWidgetIntoTable(0, columnDefined, editableLabel);
			//HAS A NAME??
			
			/*if(columnDefined.getColumnName()!=null){
				setWidgetIntoTable(0, columnDefined, new Text(columnDefined.getColumnName()));
			}
			else{ //HEADER WIHT DEFAULT VALUE
				GWT.log("Setting "+columnDefined.getColumnName() + " index: "+columnDefined.getIndex() + " header value: "+(columnDefined.getIndex()+1));
				setWidgetIntoTable(0, columnDefined, new Text("Column "+(columnDefined.getIndex()+1)));
			}*/
			
			//HAS A REFERENCE?
			if(tdDataType.getFormatReference()!=null){
				TdTFormatReference format = tdDataType.getFormatReference();
//				colTypeDialogMng.getScbDataTypeFormat().setValue(format);
				columnDefinition.append("["+format.getId()+"]");
				columnDefinition.append("<br/>");
		
			}

			GWT.log("ColumnData found: "+columnDataRefDim);
			if(columnDataRefDim!=null){
//				setWidgetIntoTable(3, columnDefined.getIndex(), new Html("Ref. -> "+columnDataRefDim.getLabel()));
			}
			
			if(columnDefined.getLocale()!=null){
				columnDefinition.append(columnDefined.getLocale());
				columnDefinition.append("<br/>");
			}
			
			if(columnDefined.getTimePeriod()!=null){
				columnDefinition.append(columnDefined.getTimePeriod().getName());
				columnDefinition.append("<br/>");
			}
			
			setWidgetIntoTable(1, columnDefined, new Html(columnDefinition.toString()));
			
			GWT.log("Rule Extends found: "+columnDefined.getRulesExtends());
			if(columnDefined.getRulesExtends()!=null){
				for (TemplateExpression expres : columnDefined.getRulesExtends()) {
					String label = CutStringUtil.cutString(expres.getHumanDescription(), 15);
					Text descr = new Text(label);
					descr.setTitle(expres.getHumanDescription());
					setWidgetIntoTable(2, columnDefined,descr);
				}
				
			}else
				setWidgetIntoTable(2, columnDefined, new Text("No Rules"));

			//UPDATING INTERNAL STRUCTURES - MUTEX
			if(columnDefined.getTimePeriod()!=null){
				timeDimensionCls.add(columnDefined);
			}else
				otherColumns.add(columnDefined);
		}
	}

	/**
	 * Inizialize tool bar.
	 */
	public void inizializeToolBar() {
		
		toolbar = new ToolBar();
		
		aggregateByTimeButton = new  Button(TdTemplateConstants.AGGREGATE_BY_TIME, TdTemplateAbstractResources.timeAggregate24());
		aggregateByTimeButton.setScale(ButtonScale.MEDIUM);
		aggregateByTimeButton.setIconAlign(IconAlign.TOP);
		aggregateByTimeButton.setArrowAlign(ButtonArrowAlign.RIGHT);
		aggregateByTimeButton.setMenu(new Menu());
		aggregateByTimeButton.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				if(aggregateByTimeButton.getMenu()!=null)
					aggregateByTimeButton.getMenu().show();
			}
		});
		
		addColumnButton = new  Button(TdTemplateConstants.ADD_COLUMN, TdTemplateAbstractResources.columnAdd());
		addColumnButton.setScale(ButtonScale.MEDIUM);
		addColumnButton.setIconAlign(IconAlign.TOP);
		addColumnButton.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				
				final AddColumnActionDialog addColumn = new AddColumnActionDialog(null, actionControlleEventBus, switchInterface, templateController, numColumns-1, allColumns);
				addColumn.show(200, 300, true);
			}
		});
		
		
		removeColumnButton = new  Button(TdTemplateConstants.REMOVE_COLUMN, TdTemplateAbstractResources.columnRemove());
		removeColumnButton.setScale(ButtonScale.MEDIUM);
		removeColumnButton.setIconAlign(IconAlign.TOP);
		removeColumnButton.setArrowAlign(ButtonArrowAlign.RIGHT);
		removeColumnButton.setMenu(new Menu());
		removeColumnButton.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				if(removeColumnButton.getMenu()!=null)
					removeColumnButton.getMenu().show();
			}
		});
		
		createTimeDimensionButton = new  Button(TdTemplateConstants.CREATE_TIME_DIMENSION, TdTemplateAbstractResources.timeGroup());
		createTimeDimensionButton.setScale(ButtonScale.MEDIUM);
		createTimeDimensionButton.setIconAlign(IconAlign.TOP);
		createTimeDimensionButton.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {

				List<TdColumnData> listColumns = new ArrayList<TdColumnData>(allColumns.size());
				
				CreateTimeDimensionColumnDialog timeGroup = new CreateTimeDimensionColumnDialog(null, actionControlleEventBus);
				
				int tmX = templateController.getWindowPositionX() - 50;
				int x = (tmX>0)?tmX:0;
				int y = templateController.getWindowPositionY();
				
				timeGroup.show(templateController.getWindowZIndex(), x, y, true);
				for (TdColumnDefinition cdef : allColumns) {
					TdColumnData td = TdTemplateControllerActions.convertColumnToTdColumnData(cdef);
					listColumns.add(td);
				}
				
				timeGroup.loadListColumns(listColumns);
			}
		});


		undoLastOperation = new  Button(TdTemplateConstants.UNDO_LATEST_OPERATION, TdTemplateAbstractResources.undo24());
		undoLastOperation.setScale(ButtonScale.MEDIUM);
		undoLastOperation.setIconAlign(IconAlign.TOP);
		undoLastOperation.setTitle("Undo the latest operation!");
		undoLastOperation.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				
				MessageBox.confirm("Confirm?", TdTemplateConstants.REMOVE_LATEST_POST_OPERATION, null).addCallback(new Listener<MessageBoxEvent>() {
					
					@Override
					public void handleEvent(MessageBoxEvent be) {
						String clickedButton = be.getButtonClicked().getItemId();
						if(clickedButton.equals(Dialog.YES)){
							actionControlleEventBus.fireEvent(new UndoLastOperationEvent());
						}
					}
				});
			}
		});
		
		historyActionsButton = new  Button(TdTemplateConstants.HISTORY_OPERATION, TdTemplateAbstractResources.history24());
		historyActionsButton.setScale(ButtonScale.MEDIUM);
		historyActionsButton.setIconAlign(IconAlign.TOP);
		historyActionsButton.setTitle(TdTemplateConstants.HISTORY_OF_THE_ACTIONS_APPLIED_TO_TEMPLATE);
		historyActionsButton.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				
				final HistoryAction historyAction = new HistoryAction(TdTemplateConstants.HISTORY_OF_THE_POST_OPERATION_APPLIED);
				historyAction.updateHistory();
				historyAction.showHistory();
			}
		});
		
		normalizeButton = new  Button(TdTemplateConstants.NORMALIZE, TdTemplateAbstractResources.normalize24());
		normalizeButton.setScale(ButtonScale.MEDIUM);
		normalizeButton.setIconAlign(IconAlign.TOP);
		normalizeButton.setTitle(TdTemplateConstants.NORMALIZE_COLUMNS_ACTION);
		normalizeButton.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				
				List<TdColumnData> listColumns = new ArrayList<TdColumnData>(allColumns.size());
				
				NormalizeColumnDialog normalize = new NormalizeColumnDialog(null, actionControlleEventBus);
				
				int tmX = templateController.getWindowPositionX() - 50;
				int x = (tmX>0)?tmX:0;
				int y = templateController.getWindowPositionY();
				
				normalize.show(templateController.getWindowZIndex(), x, y, true);
				for (TdColumnDefinition cdef : allColumns) {
					TdColumnData td = TdTemplateControllerActions.convertColumnToTdColumnData(cdef);
					listColumns.add(td);
				}
				normalize.loadListColumns(listColumns);
			}
		});
		
//		addMultiColumnRule = new SplitButton("Table Rule");
//		addMultiColumnRule.setTitle("Add a multi-column rule");
//		addMultiColumnRule.setIcon(TdTemplateAbstractResources.ruleTableAdd());
//		addMultiColumnRule.setScale(ButtonScale.MEDIUM);
//		addMultiColumnRule.setIconAlign(IconAlign.TOP);
//		addMultiColumnRule.setMenu(createAddMultiColumnRuleListener());
//		
//		addMultiColumnRule.addSelectionListener(new SelectionListener<ButtonEvent>() {
//
//			@Override
//			public void componentSelected(ButtonEvent ce) {
//				addMultiColumnRule.getMenu().show(addMultiColumnRule);
//			}
//		});
		
		
		addMultiColumnRule = new Button(TdTemplateConstants.TABLE_RULE);
		addMultiColumnRule.setTitle(TdTemplateConstants.ADD_A_MULTI_COLUMN_RULE);
		addMultiColumnRule.setIcon(TdTemplateAbstractResources.ruleTableAdd());
		addMultiColumnRule.setScale(ButtonScale.MEDIUM);
		addMultiColumnRule.setIconAlign(IconAlign.TOP);

		addMultiColumnRule.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				
				final MultiColumnRuleAction multiColumnRuleMng = new MultiColumnRuleAction(allColumns, actionControlleEventBus);
				multiColumnRuleMng.getMultiExpressionDialog().show();
			}
		});

		toolbar.add(addColumnButton);
		toolbar.add(removeColumnButton);
		toolbar.add(new SeparatorToolItem());
		toolbar.add(createTimeDimensionButton);
		toolbar.add(aggregateByTimeButton);
		toolbar.add(new SeparatorToolItem());
		toolbar.add(normalizeButton);
		toolbar.add(addMultiColumnRule);
		toolbar.add(new SeparatorToolItem());
		toolbar.add(new FillToolItem());
		toolbar.add(new SeparatorToolItem());
		toolbar.add(undoLastOperation);
		toolbar.add(historyActionsButton);
		tableContainer.setTopComponent(toolbar);
	}
//	
//	/**
//	 * Creates the flow menu and listener.
//	 *
//	 * @return the menu
//	 */
//	private Menu createAddMultiColumnRuleListener() {
//		  
//	    Menu menu = new Menu();
//
//	    listTableRules = new ArrayList<TemplateExpression>();
//	    
//	    createMultiColumnRule = new MenuItem("Create");
//	    createMultiColumnRule.setTitle("Create Multi-Column Rule");
//	    createMultiColumnRule.setIcon(TdTemplateAbstractResources.pencil());
//	    createMultiColumnRule.addSelectionListener(new SelectionListener<MenuEvent>() {
//
//			@Override
//			public void componentSelected(MenuEvent ce) {
//				GWT.log("Columns size is: "+allColumns.size());
//				final MultiColumnRuleMng multiColumnRuleMng = new MultiColumnRuleMng(allColumns, actionControlleEventBus);
//				Command cmd = new Command() {
//						
//						@Override
//					public void execute() {
//						TemplateExpression templateExpression = multiColumnRuleMng.getTemplateExpression();
//						listTableRules.add(templateExpression);
//					}
//				};
//					
//			
//				multiColumnRuleMng.getMultiExpressionDialog().show();
//			}
//	    });
//
////	    removeMultiColumnRule = new MenuItem("Remove");
////	    removeMultiColumnRule.setTitle("Remove a multi-column rule");
////	    removeMultiColumnRule.setIcon(TdTemplateAbstractResources.close());
////	    removeMultiColumnRule.addSelectionListener(new SelectionListener<MenuEvent>() {
////
////			@Override
////			public void componentSelected(MenuEvent ce) {
////				multiColumnRuleMng = null;
////			}
////	    });
////	    
////	    removeMultiColumnRule.setVisible(false);
//	    
//	    menu.add(createMultiColumnRule);
//	    menu.add(removeMultiColumnRule);
//	 
//	    return menu;  
//	}  
	
	/**
	 * Enable undo last operation.
	 *
	 * @param bool the bool
	 */
	public void enableUndoLastOperation(boolean bool){
		undoLastOperation.setEnabled(bool);
	}

	/**
	 * Reset.
	 */
	public void reset() {
		resetLastOperationLabel();
		resetrefreshSuggestion();
		resetColumnsAndTable();
	}

	/**
	 * Gets the panel.
	 *
	 * @return the panel
	 */
	public ContentPanel getPanel() {
		return principalContainer;
	}

	/**
	 * Sets the visible toolbar.
	 *
	 * @param b the new visible toolbar
	 */
	public void setVisibleToolbar(boolean b) {
		toolbar.setVisible(b);
	}

	/**
	 * Resetrefresh suggestion.
	 */
	public void resetrefreshSuggestion() {
		refreshSuggestion(TdTemplateConstants.SUGGESTION, TdTemplateConstants.DO_YOU_WANT_ADD_POST_ACTIONS);
	}

}
