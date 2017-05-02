/**
 * 
 */
package org.gcube.portlets.user.tdcolumnoperation.client.specificoperation;

import java.util.ArrayList;
import java.util.List;

import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnData;
import org.gcube.portlets.user.tdcolumnoperation.client.properties.AggregateFunctionPropertiesCombo;
import org.gcube.portlets.user.tdcolumnoperation.client.properties.ColumnDataPropertiesCombo;
import org.gcube.portlets.user.tdcolumnoperation.client.resources.ResourceBundleOperation;
import org.gcube.portlets.user.tdcolumnoperation.shared.TdAggregateFunction;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.sencha.gxt.cell.core.client.form.ComboBoxCell.TriggerAction;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.widget.core.client.container.BoxLayoutContainer.BoxLayoutData;
import com.sencha.gxt.widget.core.client.container.HBoxLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.form.ComboBox;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Jun 9, 2014
 *
 */
public class ComboColumnDataFunctionPanel {

	
	private ComboBox<ColumnData> comboColumn;
	private ComboBox<TdAggregateFunction> comboFunction;
	private List<ColumnData> columns;
	private List<TdAggregateFunction> functions;
	//private int index;

	private HTML error = new HTML();
	private VerticalLayoutContainer vertical;
	
	private boolean isValid;
	
	private GroupByFormValidator validator = new GroupByFormValidator();
	private ListStore<TdAggregateFunction> storeFunctions;
	private List<ColumnData> originalColumns = null;
	private ListStore<ColumnData> storeColumns;
	private Image deleteAggImage = new Image(ResourceBundleOperation.INSTANCE.close());
	private Command deleteCommand;
	private HBoxLayoutContainer hBox;
	
	/**
	 * No deletable
	 * 
	 * @param index
	 * @param columns
	 * @param functions
	 */
	public ComboColumnDataFunctionPanel(int index, List<ColumnData> columns, List<TdAggregateFunction> functions) {
		this.columns = columns;
		this.originalColumns = new ArrayList<ColumnData>(columns);
		this.functions = functions;
		//this.index = index;
		initComboColumnName();
		initComboColumnFunction();
		deleteAggImage.setStyleName("image-pointer");
		deleteAggImage.setVisible(false);
		hBox = new HBoxLayoutContainer();
		
		hBox.add(comboFunction, new BoxLayoutData(new Margins(0)));
		hBox.add(deleteAggImage, new BoxLayoutData(new Margins(7,0,0,10)));
		
		VerticalLayoutContainer vertical1 = new VerticalLayoutContainer();
//		hBox.add(comboFunction, new BoxLayoutData(new Margins(0,0,0,10)));
		vertical1.add(hBox, new VerticalLayoutData(1, -1, new Margins(0,0,2,0)));
		vertical1.add(comboColumn);
	
		vertical = new VerticalLayoutContainer();
		//vertical.setScrollMode(ScrollMode.AUTOY);
		
		vertical.add(vertical1, new VerticalLayoutData(1, -1, new Margins(0,0,0,0)));
		
		vertical.add(error, new VerticalLayoutData(1, -1, new Margins(0,1,10,1)));
	}
	
	
	/**
	 * 
	 * @param index
	 * @param columns
	 * @param functions
	 * @param deletable
	 * @param command
	 */
	public ComboColumnDataFunctionPanel(int index, List<ColumnData> columns, List<TdAggregateFunction> functions, boolean deletable, final Command command) {
		this(index, columns, functions);
		this.deleteCommand = command;
		
		if(deletable){
			deleteAggImage.setVisible(true);
			deleteAggImage.addClickHandler(new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {

					if(deleteCommand!=null)
						deleteCommand.execute();
				}
			});
		}
		
	}
	/**
	 * 
	 */
	private void initComboColumnName() {
		
		// Column Data
		ColumnDataPropertiesCombo propsColumnData = GWT.create(ColumnDataPropertiesCombo.class);
		storeColumns = new ListStore<ColumnData>(propsColumnData.id());
		
		if(columns!=null)
			storeColumns.addAll(columns);
		
		comboColumn = new ComboBox<ColumnData>(storeColumns, propsColumnData.label());
		comboColumn.setAllowBlank(false);

		comboColumn.addSelectionHandler(new SelectionHandler<ColumnData>() {
			
			public void onSelection(SelectionEvent<ColumnData> event) {
				
			}

		});

		comboColumn.setEmptyText("Select a column...");
		comboColumn.setWidth(150);
		comboColumn.setTypeAhead(false);
		comboColumn.setEditable(false);
		comboColumn.setTriggerAction(TriggerAction.ALL);

	}
	
	
	/**
	 * 
	 */
	private void initComboColumnFunction() {
		
		// Column Data
		AggregateFunctionPropertiesCombo propsColumnData = GWT.create(AggregateFunctionPropertiesCombo.class);
		storeFunctions = new ListStore<TdAggregateFunction>(propsColumnData.id());

		if(functions!=null)
			storeFunctions.addAll(functions);
		
		comboFunction = new ComboBox<TdAggregateFunction>(storeFunctions, propsColumnData.label());
		comboFunction.setAllowBlank(false);

		comboFunction.addSelectionHandler(new SelectionHandler<TdAggregateFunction>() {
			
			public void onSelection(SelectionEvent<TdAggregateFunction> event) {
				errorText("", false);
				comboColumn.reset();
				comboColumn.clear();
				comboColumn.select(-1);
				ripristinateOriginalColumns();
				constraintsColumn(comboFunction.getCurrentValue());
				
				if(comboColumn.getStore().size()==0){
					errorText("No column available to apply "+getFunctionSelected().label()+" function!", true);
				}
			}

		});

		comboFunction.setEmptyText("Select a function...");
		comboFunction.setWidth(150);
		comboFunction.setTypeAhead(false);
		comboFunction.setEditable(false);
		comboFunction.setTriggerAction(TriggerAction.ALL);

	}
	
	public void errorText(String text, boolean visible){
		String html = "<p style=\"color:red\">"+text+"</p>";
		error.setHTML(html);
		vertical.forceLayout();
	}
	
	public boolean validate(List<ColumnData> selectedColumns){
		errorText("", false);
		
		if(comboFunction.getCurrentValue()==null){
//			UtilsGXT3.alert("Attention", "Insert field 'Function' for field: "+index);
//			errorText("You must select a function", true);
			comboFunction.markInvalid("You must select a function");
			return isValid = false;
		}else if(comboColumn.getCurrentValue()==null){
//			UtilsGXT3.alert("Attention", "Insert 'Column Name' for field : "+index);
//			errorText("You must select a column", true);
			comboColumn.markInvalid("You must select a column");
			return isValid = false;
		}else{
			
			validator.selectColumnsData(selectedColumns);
			ColumnData aggregate = getColumnDataSelected();
			validator.selectAggregateData(aggregate);
			if(validator.containsDataAggregate()){
				errorText("The aggregate column '"+aggregate.getLabel()+"' is also a group by column and it cannot be", true);
//				errorText("The selected group by column: '"+aggregate.getLabel() +"' must not be an aggregate column!", true);
				return isValid = false;
			}
		}
		
		return isValid = true;
	}
	
	public VerticalLayoutContainer getPanel(){
		return vertical;
	}
	
	public ColumnData getColumnDataSelected(){
		return comboColumn.getCurrentValue();
	}
	
	public TdAggregateFunction getFunctionSelected(){
		return comboFunction.getCurrentValue();
	}

	public void setValid(boolean isValid) {
		this.isValid = isValid;
	}

	public boolean isValid() {
		return isValid;
	}
	
	/**
	 * 
	 * @param functions
	 */
	public void updateFunctions(List<TdAggregateFunction> functions){
		if(functions!=null){
			storeFunctions.clear();
			this.functions = functions;
			storeFunctions.addAll(functions);
		}
	}

	/**
	 * 
	 * @param columns
	 */
	public void updateColumns(ArrayList<ColumnData> columns) {
		if(columns!=null){
			originalColumns.clear();
			originalColumns.addAll(columns);
			this.columns = columns;
			ripristinateOriginalColumns();
		}
		
	}
	
	public void ripristinateOriginalColumns(){
		storeColumns.clear();
		storeColumns.addAll(originalColumns);
		this.columns = storeColumns.getAll();
	}
	
	public void constraintsColumn(TdAggregateFunction function){
		GWT.log("constraints columns storeColumns is null "+(storeColumns==null));
		List<ColumnData> tempColumns = new ArrayList<ColumnData>(storeColumns.getAll().size());
		tempColumns.addAll(storeColumns.getAll());
		
		if(storeColumns!=null){
			for (ColumnData column : tempColumns) {
				String dataType = column.getDataTypeName()+"Type";
				GWT.log("column "+column.getName() + " type: "+dataType);
				GWT.log("removing?");
				if(!function.getAllowedDataTypesForName().contains(dataType)){
					storeColumns.remove(column);
					GWT.log("true!");
				}else
					GWT.log("false!");
				
			}
		}
	}

}
