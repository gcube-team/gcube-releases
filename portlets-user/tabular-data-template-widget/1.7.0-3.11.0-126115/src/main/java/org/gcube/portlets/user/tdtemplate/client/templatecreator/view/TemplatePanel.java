/**
 * 
 */
package org.gcube.portlets.user.tdtemplate.client.templatecreator.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnData;
import org.gcube.portlets.user.tdtemplate.client.TdTemplateConstants;
import org.gcube.portlets.user.tdtemplate.client.TdTemplateController;
import org.gcube.portlets.user.tdtemplate.client.ZIndexReference;
import org.gcube.portlets.user.tdtemplate.client.event.SetColumnTypeCompletedEvent;
import org.gcube.portlets.user.tdtemplate.client.event.TemplateCompletedEvent;
import org.gcube.portlets.user.tdtemplate.client.resources.TdTemplateAbstractResources;
import org.gcube.portlets.user.tdtemplate.client.templatecreator.SetColumnTypeDefinition;
import org.gcube.portlets.user.tdtemplate.client.templatecreator.SetColumnTypeDialogManager;
import org.gcube.portlets.user.tdtemplate.client.templatecreator.flow.WindowFlowCreate;
import org.gcube.portlets.user.tdtemplate.client.templatecreator.view.external.updater.UpdateColumnDataByReference;
import org.gcube.portlets.user.tdtemplate.client.templatecreator.view.suggestion.ConstraintSuggestionLabel;
import org.gcube.portlets.user.tdtemplate.client.templatecreator.view.suggestion.HtmlLabel;
import org.gcube.portlets.user.tdtemplate.client.templatecreator.view.suggestion.SuggestionContainer;
import org.gcube.portlets.user.tdtemplate.shared.TdFlowModel;
import org.gcube.portlets.user.tdtemplate.shared.TdTColumnCategory;
import org.gcube.portlets.user.tdtemplate.shared.TdTDataType;
import org.gcube.portlets.user.tdtemplate.shared.TdTFormatReference;
import org.gcube.portlets.user.tdtemplate.shared.TdTTimePeriod;
import org.gcube.portlets.user.tdtemplate.shared.TemplateExpression;

import com.extjs.gxt.ui.client.Style.ButtonScale;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.IconAlign;
import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.button.SplitButton;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTMLTable;
import com.google.gwt.user.client.ui.Widget;

/**
 * The Class TemplatePanel.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Jan 8, 2014
 */
public class TemplatePanel {

	protected FlexTable flexTableTemplate = new FlexTable();
	protected LayoutContainer flexTableContainer = new LayoutContainer();
	protected int numColumns;
	protected ContentPanel tableContainer = new ContentPanel();
	private SuggestionContainer suggestionContainer = new SuggestionContainer();
	protected List<ColumnDefinitionView> columnsDefined;
	protected TemplateSwitcherInteface templateSwitcherInteface;
	protected SetColumnTypeDefinition setColumnTypeDefinition;
	protected TdTemplateController controller;
	protected LayoutContainer centralContainer = new LayoutContainer();
	protected ContentPanel principalContainer;
	protected ContentPanel southContainer;
//	protected Button filterBySelection;
	private BorderLayout borderLayout;
	private BorderLayoutData centerData;
	private BorderLayoutData southData;
	private Button addFlow;
	private MenuItem createFlow;
	private MenuItem removeFlow;
	private SelectionListener<ButtonEvent> listenerAddFlow;
	protected ToolBar toolbar;
	private Button actionsButton;
	private boolean validateTemplateEventFire = true; //USED BY ADD COLUMN ACTION
	protected HtmlLabel htmlTitleLabel = new HtmlLabel(TdTemplateConstants.DEFINITION_AND_VALIDATION_TEMPLATE, "", "");
	private boolean isValidTemplate = false;
	protected ConstraintSuggestionLabel constraintSuggestionLabel;
	protected ZIndexReference zIndexReference;
	private SplitButton cloneColumn = new SplitButton("Clone Column");
	//USED TO CREATE ONLY POST-OPERATIONS
	/**
	 * Instantiates a new template panel.
	 */
	public TemplatePanel(){
		initContainers();
	}
	
	/**
	 * Inits the containers.
	 */
	protected void initContainers() {

		this.tableContainer.setHeaderVisible(false);
		this.tableContainer.setBorders(false);
		inizializeTableTemplate();
		inizializeToolBar();
	
		this.tableContainer.setScrollMode(Scroll.AUTOX);
		this.tableContainer.setEnabled(false);
		
		refreshSuggestion(TdTemplateConstants.SUGGESTION, TdTemplateConstants.PLEASE_SET_TYPE_TO_ALL_COLUMNS);
//		suggestionContainer.getElement().getStyle().setBorderColor("#32CD32");

		tableContainer.setBodyBorder(false);
		tableContainer.setBorders(false);

		tableContainer.add(htmlTitleLabel);
		tableContainer.add(suggestionContainer);
		
		flexTableContainer.add(flexTableTemplate);
		tableContainer.add(flexTableContainer);

		centralContainer.add(tableContainer);
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
	 * Instantiates a new template panel.
	 *
	 * @param templateSwitcherInteface the template switcher inteface
	 * @param controller the controller
	 */
	public TemplatePanel(TemplateSwitcherInteface templateSwitcherInteface, TdTemplateController controller) {
		this.numColumns = templateSwitcherInteface.getNumberOfColumns();
		this.controller = controller;
		this.templateSwitcherInteface = templateSwitcherInteface;
		this.zIndexReference = new ZIndexReference(controller);
		
		this.columnsDefined = new ArrayList<ColumnDefinitionView>(numColumns);
		
		setColumnTypeDefinition = new SetColumnTypeDefinition(templateSwitcherInteface, true) {
			
			@Override
			public void updateListCategory() {
				GWT.log("Init table");
				tableContainer.setEnabled(true);
				initTableColumns(flexTableTemplate, 0, numColumns);
				updateMenuOfCloneableColumns();
				TemplatePanel.this.controller.getInternalBus().fireEvent(new SetColumnTypeCompletedEvent());
			}
		};
		
		initContainers();
		String title = templateSwitcherInteface.getType() + " columns constraints";
		constraintSuggestionLabel = new ConstraintSuggestionLabel(title, templateSwitcherInteface.getTdTTemplateType().getConstraintDescription(), false);
		centralContainer.add(constraintSuggestionLabel);
	}
	
	

	/**
	 * @return the templateSwitcherInteface
	 */
	public TemplateSwitcherInteface getTemplateSwitcherInteface() {
		return templateSwitcherInteface;
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

		southContainer = new ContentPanel();
		southContainer.setHeaderVisible(true);
		southContainer.setBorders(false);
		southContainer.setBodyBorder(false);
		southContainer.setHeading("Filters");
		southContainer.setScrollMode(Scroll.AUTOY);
		
		centralContainer.setHeight(TdTemplateConstants.HEIGHT_PRINCIPAL);
		centralContainer.setScrollMode(Scroll.AUTOY);
		principalContainer.add(centralContainer, centerData);	
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
	
//	private void setBodyColumnOpacity(FlexTable table, int colIndex, double opacity) {
//		for (int i = 1; i < table.getRowCount(); i++){
////			table.getCellFormatter().setVisible(i, colIndex, b);
//			table.getCellFormatter().getElement(i, colIndex).getStyle().setOpacity(opacity);
//		}
//	}
	
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
	 * Inizialize tool bar.
	 */
	public void inizializeToolBar() {
		
		toolbar = new ToolBar();

		// Add a button that will add more rows to the table
	    Button addColumnButton = new Button("Add Column", TdTemplateAbstractResources.columnAdd());
	    addColumnButton.setTitle("Append a new Column");
		addColumnButton.setScale(ButtonScale.MEDIUM);
		addColumnButton.setIconAlign(IconAlign.TOP);
//		addColumnButton.setArrowAlign(ButtonArrowAlign.BOTTOM);
		
		addColumnButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
			
			@Override
			public void componentSelected(ButtonEvent ce) {
				addColumn(flexTableTemplate, true);
				updateMenuOfCloneableColumns();
			}
		});

		Button removeRowButton = new Button("Remove Column", TdTemplateAbstractResources.columnRemove());
		removeRowButton.setTitle("Remove Last Column");
		removeRowButton.setScale(ButtonScale.MEDIUM);
		removeRowButton.setIconAlign(IconAlign.TOP);
//		removeRowButton.setArrowAlign(ButtonArrowAlign.BOTTOM);
		
		removeRowButton.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				if(numColumns>1){
					removeColumn(flexTableTemplate, numColumns-1);
					setNumColumns(numColumns-1);
					updateMenuOfCloneableColumns();
				}
			}
		});
		
		cloneColumn.setIcon(TdTemplateAbstractResources.columnClone());
		cloneColumn.setTitle("Create a clone column of an existing column");
		cloneColumn.setScale(ButtonScale.MEDIUM);
		cloneColumn.setIconAlign(IconAlign.TOP);
//		removeRowButton.setArrowAlign(ButtonArrowAlign.BOTTOM);
		
		cloneColumn.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				if(numColumns<1){
					MessageBox.info("Info..", "You must define at least one column to create a clone", null);
					return;
				}
				cloneColumn.getMenu().show(cloneColumn);
			}
		});
		
		addFlow = new SplitButton("Flow");
		addFlow.setIcon(TdTemplateAbstractResources.flow24());
		addFlow.setScale(ButtonScale.MEDIUM);
		addFlow.setIconAlign(IconAlign.TOP);
		addFlow.setMenu(createFlowMenuAndListener());
		
		listenerAddFlow = new SelectionListener<ButtonEvent>() {
			
			@Override
			public void componentSelected(ButtonEvent ce) {
				addFlow.getMenu().show(addFlow);
				getFlow();
			}
		};
		
		addFlow.addSelectionListener(listenerAddFlow);
		enableFlow(false);

		toolbar.add(addColumnButton);
		toolbar.add(removeRowButton);
		toolbar.add(cloneColumn);
		toolbar.add(new FillToolItem());
		toolbar.add(addFlow);
		tableContainer.setTopComponent(toolbar);
	}
	
	private void updateMenuOfCloneableColumns(){
		if(cloneColumn.getMenu()!=null)
			cloneColumn.getMenu().removeAll();
		else
			cloneColumn.setMenu(new Menu());
		
		for (final ColumnDefinitionView columnDefinitionView : columnsDefined) {
			
			MenuItem item = new MenuItem(columnDefinitionView.getColumnLabel());
			item.addSelectionListener(new SelectionListener<MenuEvent>() {

				@Override
				public void componentSelected(MenuEvent ce) {
					cloneColumn(flexTableTemplate, columnDefinitionView.getColumnIndex(), 1);
				}
			});
			
			cloneColumn.getMenu().add(item);
		}
	}

	/**
	 * Gets the toolbar.
	 *
	 * @return the toolbar
	 */
	public ToolBar getToolbar() {
		return toolbar;
	}
	
	/**
	 * Gets the sets the column type definition.
	 *
	 * @return the setColumnTypeDefinition
	 */
	public SetColumnTypeDefinition getSetColumnTypeDefinition() {
		return setColumnTypeDefinition;
	}
	
	/**
	 * Creates the flow menu and listener.
	 *
	 * @return the menu
	 */
	private Menu createFlowMenuAndListener() {
		  
	    Menu menu = new Menu();
	    
	    createFlow = new MenuItem(TdTemplateConstants.CREATE_FLOW);
	    createFlow.setIcon(TdTemplateAbstractResources.pencil());
	    createFlow.addSelectionListener(new SelectionListener<MenuEvent>() {

			@Override
			public void componentSelected(MenuEvent ce) {
				WindowFlowCreate.geInstance().show();
			}
	    });
	    
		WindowFlowCreate.geInstance().addListener(Events.Hide, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent be) {
				
				GWT.log("Hide Create/Update flow");
				
				boolean flowCreated = WindowFlowCreate.geInstance().flowExists();
				
				GWT.log("flowCreated: "+flowCreated);
				
				if(flowCreated){
					createFlow.setText(TdTemplateConstants.EDIT_FLOW);
					addFlow.setIcon(TdTemplateAbstractResources.flow24Ok());
					BaloonPanel baloon = new BaloonPanel("Flow attached!", true);

					int zi = zIndexReference.getZIndex()+1;
					baloon.getElement().getStyle().setZIndex(zi);
					baloon.showRelativeTo(addFlow);
				}
				else{
					createFlow.setText(TdTemplateConstants.CREATE_FLOW);
					addFlow.setIcon(TdTemplateAbstractResources.flow24());
				}
				
				removeFlow.setVisible(flowCreated);
			}
		});
	    
	    removeFlow = new MenuItem("Remove Flow");
	    removeFlow.setIcon(TdTemplateAbstractResources.close());
	    removeFlow.addSelectionListener(new SelectionListener<MenuEvent>() {

			@Override
			public void componentSelected(MenuEvent ce) {
				WindowFlowCreate.geInstance().resetFlowCreatePanel();
				removeFlow.setVisible(false);
				createFlow.setText(TdTemplateConstants.CREATE_FLOW);
				addFlow.setIcon(TdTemplateAbstractResources.flow24());
			}
	    });
	    
	    removeFlow.setVisible(false);
	    
	    menu.add(createFlow);
	    menu.add(removeFlow);
	 
	    return menu;  
	}  
	
	/**
	 * Enable flow.
	 *
	 * @param bool the bool
	 */
	public void enableFlow(boolean bool){
		addFlow.setEnabled(bool);
	}

	/**
	 * Enable filter.
	 *
	 * @param bool the bool
	 */
	public void enableFilter(boolean bool){
//		filterBySelection.setEnabled(bool);
		
		if(!bool)
			borderLayout.collapse(LayoutRegion.SOUTH);
		else
			borderLayout.expand(LayoutRegion.SOUTH);
		
		southContainer.setEnabled(bool);
	}
	
	/**
	 * Enanble column types.
	 *
	 * @param bool the bool
	 */
	public void enableColumnTypes(boolean bool){
//		flexTableContainer.setEnabled(bool);
		
		for (int i=0; i< columnsDefined.size(); i++) {
			ColumnDefinitionView column = columnsDefined.get(i);
			column.setColumnTypeAsReadOnly(!bool);
			
			if(bool)
				setColumnTypeOpacity(i, 1);
			else
				setColumnTypeOpacity(i, 0.5);
		}
	}
	
	/**
	 * Initialize this example.
	 */

	public void inizializeTableTemplate() {
		// Create a Flex Table
		flexTableTemplate.addStyleName("FlexTableTemplate");
		flexTableTemplate.ensureDebugId("cwFlexTable");
	}

	/**
	 * Add a row to the flex table.
	 *
	 * @param flexTable the flex table
	 */
	private void addRow(FlexTable flexTable) {
		int numRows = flexTable.getRowCount();
//		flexTable.setWidget(numRows, 0, new Image(ResourcesTemplate.INSTANCE.getArrowDown()));
//		flexTable.setWidget(numRows, 1, new Image(ResourcesTemplate.INSTANCE.getArrowDown()));
//		flexTable.getFlexCellFormatter().setRowSpan(0, 1, numRows + 1);
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

			final ColumnDefinitionView columnDef = new ColumnDefinitionView(this, indexOffset, new SetColumnTypeDialogManager(setColumnTypeDefinition.getListCategory(), controller));
			
			EditableLabelColumnDefinitionView editableLabel = new EditableLabelColumnDefinitionView(zIndexReference.getZIndex()+1);
			HorizontalPanel hpHeader = editableLabel.getEditableLabelPanel(columnDef, zIndexReference);
			flexTable.setWidget(0, indexOffset, hpHeader);
			columnDef.setEditableLabel(editableLabel);
			
			HorizontalPanel hp = new HorizontalPanel();
			hp.setHorizontalAlign(HorizontalAlignment.CENTER);

			hp.add(columnDef.getSetColumnTypeViewManager().getTypingContainer());
			flexTable.setWidget(1, indexOffset, hp);
			
			flexTable.setWidget(2, indexOffset, columnDef.getColumnDescription());
			flexTable.getCellFormatter().getElement(2, indexOffset).setAttribute("height", "40px");
			
			
			HTMLTable.RowFormatter rf = flexTable.getRowFormatter();
			rf.addStyleName(0, "FlexTableTemplate-header-row");
			rf.addStyleName(1, "FlexTableTemplate-other-rows");
			rf.addStyleName(2, "FlexTableTemplate-other-rows");

			try{
				GWT.log("Setting indexOffset: "+indexOffset);
				columnsDefined.set(indexOffset, columnDef);
				
			}catch (Exception e) {
				GWT.log("Warn: indexOffset "+indexOffset +" doesn't exists into columnsDefined adding");
		
				columnsDefined.add(indexOffset, columnDef);
			}
		}
		
	}
	/**
	 * Sets the widget into table.
	 *
	 * @param rowIndex the row index
	 * @param columnIndex the column index
	 * @param widget the widget
	 */
	public void setWidgetIntoTable(int rowIndex, int columnIndex, Widget widget){
		HTMLTable.RowFormatter rf = flexTableTemplate.getRowFormatter();
		
		if(rowIndex>0)
			rf.addStyleName(rowIndex, "FlexTableTemplate-other-rows");
		
		flexTableTemplate.setWidget(rowIndex, columnIndex, widget);
		
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
	 * Adds the column.
	 *
	 * @param flexTable the flex table
	 * @param validateTemplate the validate template
	 */
	private void addColumn(FlexTable flexTable, boolean validateTemplate) {

		initTableColumns(flexTable, numColumns, 1);
		setNumColumns(numColumns+1);
		if(validateTemplate)
			validateTemplate();
	}


	/**
	 * Clone column.
	 *
	 * @param indexSourceColumn the index source column
	 * @param numberOfClones the number of clones
	 */
	private void cloneColumn(FlexTable flexTable, int indexSourceColumn, int numberOfClones) {

		//RANGE VALIDATION
		if(indexSourceColumn>=0 && indexSourceColumn<columnsDefined.size()){
			ColumnDefinitionView source = columnsDefined.get(indexSourceColumn);
			
			for (int i=0; i<numberOfClones;i++) {
				addColumn(flexTable, false);
				ColumnDefinitionView clone = columnsDefined.get(numColumns-1); //get current latest column
				copyColumnFields(source, clone);
			}
			validateTemplate();
		}	
	}
	
	private void copyColumnFields(ColumnDefinitionView source, ColumnDefinitionView destination){
		
		if(source.getColumnName()!=null && !source.getColumnName().isEmpty())
			destination.setColumnHeaderValue("Clone "+destination.getColumnName());
		else
			destination.setColumnHeaderValue("A Cloned Column");
		
		//SOURCE FIELDS
		TdTColumnCategory sTdCategory = source.getSelectedColumnCategory();
		TdTDataType sTdDataType = source.getSelectedDataType();
		ColumnData sColumnData = source.getReferenceColumnData();
		
		if(sTdCategory==null)
			return;
		
		//SET VALUE HERE
		SetColumnTypeDialogManager dColTypeDialogMng = destination.getSetColumnTypeViewManager();

		destination.setSpecialCategoryType(source.getSpecialCategoryType()); //SETTING SPECIAL CATEGORY
		dColTypeDialogMng.getScbCategory().setSimpleValue(sTdCategory.getName());
		
		if(sTdDataType!=null)
			dColTypeDialogMng.getScbDataType().setValue(sTdDataType);
		
		//HAS IT A FORMAT REFERENCE?
		if(sTdDataType.getFormatReferenceIndexer()!=null){
			TdTFormatReference format = source.getSelectedDataTypeFormat();
			GWT.log("FORMAT: "+format);
			dColTypeDialogMng.getScbDataTypeFormat().setValue(format);
		}
		
		//IS A DIMENSION?
		if(sColumnData!=null){
			String tabularResourceName = source.getSetColumnTypeDialogManager().getReferenceTabularResourceName()!=null?source.getSetColumnTypeDialogManager().getReferenceTabularResourceName():"";
			dColTypeDialogMng.setTabularResourceName(tabularResourceName);
			new UpdateColumnDataByReference(dColTypeDialogMng, sColumnData);
		}
		
//		if(source.getSelectedLocale()!=null){
//			colTypeDialogMng.setSelectedLocale(destination.getSelectedLocale(), true);
//		}
		
		//IS A TIMEDIMENSION?
		if(source.getTimePeriod()!=null){
			Map<String, String> valueFormats = new HashMap<String, String>();
			valueFormats.put(source.getTimePeriodFormat().getId(), ""); //the formatIdentifier
			
			GWT.log("Value Format : "+source.getTimePeriodFormat() + ", to Time Period: "+source.getTimePeriod());
			dColTypeDialogMng.setSelectTimePeriod(new TdTTimePeriod(source.getTimePeriod(),valueFormats));
		}
		
		//HAS IT A RULE?
		if(source.getRulesExpressions()!=null){
			for (TemplateExpression expres : source.getRulesExpressions()) {
				destination.addRule(expres, false, true);
			}
		}
		
	}
	

	/**
	 * Sets the num columns.
	 *
	 * @param numColumns the new num columns
	 */
	private void setNumColumns(int numColumns) {
		this.numColumns = numColumns;
		
	}
	
	/**
 * Remove a row from the flex table.
 *
 * @param flexTable the flex table
 * @param columnIndex the column index
 */
	private void removeColumn(FlexTable flexTable, int columnIndex) {
		GWT.log("Remove column at index: "+columnIndex);
		if (columnIndex > 0) {
			int numRows = flexTable.getRowCount();
			GWT.log("Current Table row size: "+numRows);
			try{
				for (int i=0; i<numRows; i++) {
					GWT.log("RemoveCell i, "+i+", column index "+columnIndex);
					flexTable.removeCell(i, columnIndex);
				}
			}catch (Exception e) {
				GWT.log("Last remove cell throw exception");
			}
	
			GWT.log("Remove column defined at index: "+columnIndex);
			columnsDefined.remove(columnIndex);
			validateTemplate();
//			flexTable.getFlexCellFormatter().setRowSpan(0, 1, columnIndex - 1);
		}
	}

	/**
	 * Remove a row from the flex table.
	 *
	 * @param flexTable the flex table
	 */
	private void removeRow(FlexTable flexTable) {
		int numRows = flexTable.getRowCount();
		if (numRows > 1) {
			flexTable.removeRow(numRows - 1);
			flexTable.getFlexCellFormatter().setRowSpan(0, 1, numRows - 1);
		}
	}


	/**
	 * Gets the num columns.
	 *
	 * @return the num columns
	 */
	public int getNumColumns() {
		return numColumns;
	}


	/**
	 * Gets the panel.
	 *
	 * @return the panel
	 */
	public LayoutContainer getPanel() {
		return principalContainer;
	}



	/**
	 * Gets the columns defined.
	 *
	 * @return the columns defined
	 */
	public List<ColumnDefinitionView> getColumnsDefined() {
		return columnsDefined;
	}
	

	/**
	 * Validate template.
	 */
	public void validateTemplate() {

		boolean isValid = true;
		for (ColumnDefinitionView col : columnsDefined) {
			if(!col.isValid()){
				isValid = false;
				refreshSuggestion(TdTemplateConstants.SUGGESTION, TdTemplateConstants.PLEASE_SET_TYPE_TO_COLUMN_NUMBER+(col.getColumnIndex()+1));
				
				if(validateTemplateEventFire)
					controller.getInternalBus().fireEvent(new TemplateCompletedEvent(false));
				
				enableFlow(false);
				break;
			}
		}
		
		isValidTemplate = isValid;
		
		if(isValid){	
			refreshSuggestion(TdTemplateConstants.TEMPLATE_COMPLETED, TdTemplateConstants.NOW_IS_POSSIBLE_TO_GENERATE_THE_TEMPLATE_CREATED, TdTemplateAbstractResources.handsUP());
			
			if(validateTemplateEventFire)
				controller.getInternalBus().fireEvent(new TemplateCompletedEvent(true));
				
			enableFlow(true);
		}
	}


	/**
	 * Checks if is valid template.
	 *
	 * @return the isValidTemplate
	 */
	public boolean isValidTemplate() {
		return isValidTemplate;
	}

	/**
	 * Gets the flow.
	 *
	 * @return flow is FLOW is created or read only, otherwise null
	 */
	public TdFlowModel getFlow() {

		if(WindowFlowCreate.geInstance().flowExists() || WindowFlowCreate.geInstance().flowIsReadOnly())
			return WindowFlowCreate.geInstance().getFlow();

		return null;
	}
	
	/**
	 * Sets the flow as read only.
	 *
	 * @param bool the new flow as read only
	 */
	public void setFlowAsReadOnly(boolean bool){
		WindowFlowCreate.geInstance().setFlowAsReadOnly(bool);
		
		if(bool){
			addFlow.setEnabled(true);
			removeFlow.setVisible(false);
			createFlow.setText("View Flow");
			createFlow.setIcon(TdTemplateAbstractResources.view());
			createFlow.setVisible(true);
			
			addFlow.removeSelectionListener(listenerAddFlow);
			
			addFlow.addSelectionListener(new SelectionListener<ButtonEvent>() {

				@Override
				public void componentSelected(ButtonEvent ce) {
					WindowFlowCreate.geInstance().show();
					
				}
			});
		}
	}
	
	/**
	 * Sets the adds the flow as visible.
	 *
	 * @param bool the new adds the flow as visible
	 */
	public void setAddFlowAsVisible(boolean bool) {
		addFlow.setVisible(bool);
	}
	
	/**
	 * Sets the visible toolbar.
	 *
	 * @param bool the new visible toolbar
	 */
	public void setVisibleToolbar(boolean bool){
		toolbar.setVisible(bool);
	}

	/**
	 * Gets the controller.
	 *
	 * @return the controller
	 */
	public TdTemplateController getController() {
		return controller;
	}

	/**
	 * Sets the visible suggests.
	 *
	 * @param b the new visible suggests
	 */
	public void setVisibleSuggests(boolean b) {
		suggestionContainer.setVisible(b);
		constraintSuggestionLabel.setVisible(b);
	}

	/**
	 * Enable validate template.
	 *
	 * @param b the b
	 */
	public void enableValidateTemplate(boolean b) {
		validateTemplateEventFire = b;
	}
	
	/**
	 * Sets the visible add rule.
	 *
	 * @param columnIndex the column index
	 * @param b the b
	 */
	public void setVisibleAddRule(int columnIndex, boolean b) {
//		columnDefinitionViews.get(columnIndex).setVisibleAddRule(b);
		columnsDefined.get(columnIndex).setVisibleAddRule(b);
		if(!b){
			setWidgetIntoTable(2, columnIndex, new Html("Rule (unavailable)"));
		}
	}


	/**
	 * Sets the title visible.
	 *
	 * @param b the new title visible
	 */
	public void setVisibleTitle(boolean b) {
		GWT.log("HTML SET VISIBLE: "+b);
		htmlTitleLabel.setVisible(b);
		if(!b){
			htmlTitleLabel.createHtml("", "", "");
		}
		
		tableContainer.layout(true);
	}
	
	/**
	 * Sets the column header value.
	 *
	 * @param columnIndex the column index
	 * @param headerValue the header value
	 */
	public void setColumnHeaderValue(int columnIndex, String headerValue){
		columnsDefined.get(columnIndex).setColumnHeaderValue(headerValue);
	}
	
	/**
	 * Sets the editable header value.
	 *
	 * @param columnIndex the column index
	 * @param b the b
	 */
	public void setEditableHeaderValue(int columnIndex, boolean b) {
		columnsDefined.get(columnIndex).getEdiTableLabel().setEditableVisible(b);
	}
}
