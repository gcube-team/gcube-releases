package org.gcube.portlets.user.tdcolumnoperation.client;


import org.gcube.portlets.user.td.expressionwidget.client.store.ColumnDataTypeElement;
import org.gcube.portlets.user.td.expressionwidget.client.store.ColumnTypeCodeElement;
import org.gcube.portlets.user.td.monitorwidget.client.MonitorDialogListener;
import org.gcube.portlets.user.td.widgetcommonevent.client.event.ChangeTableRequestEvent;
import org.gcube.portlets.user.td.widgetcommonevent.client.type.ChangeTableRequestType;
import org.gcube.portlets.user.td.widgetcommonevent.client.type.ChangeTableWhy;
import org.gcube.portlets.user.td.widgetcommonevent.shared.OperationResult;
import org.gcube.portlets.user.td.widgetcommonevent.shared.TRId;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnData;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnDataType;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnTypeCode;
import org.gcube.portlets.user.tdcolumnoperation.client.resources.HelperHTML;
import org.gcube.portlets.user.tdcolumnoperation.client.resources.ResourceBundleOperation;
import org.gcube.portlets.user.tdcolumnoperation.client.rpc.TdColumnOperationServiceAsync;
import org.gcube.portlets.user.tdcolumnoperation.client.utils.ComboColumnTypeUtils;
import org.gcube.portlets.user.tdcolumnoperation.client.utils.UtilsGXT3;
import org.gcube.portlets.user.tdcolumnoperation.shared.FieldValidator;
import org.gcube.portlets.user.tdcolumnoperation.shared.OperationID;
import org.gcube.portlets.user.tdcolumnoperation.shared.TdOperatorComboOperator;
import org.gcube.portlets.user.tdcolumnoperation.shared.TdOperatorEnum;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.web.bindery.event.shared.EventBus;
import com.sencha.gxt.cell.core.client.ButtonCell.IconAlign;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.widget.core.client.FramedPanel;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.CheckBox;
import com.sencha.gxt.widget.core.client.form.ComboBox;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.form.TextField;

/**
 * 
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Jun 3, 2014
 *
 */
public class MergeColumnPanel extends FramedPanel implements MonitorDialogListener {

//	protected ChangeColumnTypeDialog parent;
	protected TextField textField = null;
	protected TextButton submit;
	
	protected TextButton buttonHelper = new TextButton("Helper");
	
	protected OperationID operationId = OperationID.MERGE;

	protected TdColumnOperationServiceAsync serviceAsync = TdColumnOperationServiceAsync.Util.getInstance();
	
	protected LoadComboColumnData firstColumnData;
	protected LoadComboColumnData secondColumnData;
	protected AbstactSplitMergeOperation abstractOperation;
	protected String columnName;
	protected TRId trId;
	private EventBus eventBus;

	private MergeColumnPanel INSTANCE = this;
	private TextField textFieldColumnName1;
	private ComboBox<ColumnTypeCodeElement> comboColumnTypeCode;
	
	private ColumnDataTypeElement selectedDataType;
	private ComboBox<ColumnDataTypeElement> comboAttributeType;
	private ComboBox<ColumnDataTypeElement> comboMeasureType;
	
	private HelperHTML helperHtml = new HelperHTML("Split and Merge Helper", ResourceBundleOperation.INSTANCE.smHelper().getText());
	private CheckBox checkBoxDeleteSrcColumns;
	
	public MergeColumnPanel(TRId trId, String columnName, EventBus eventBus) {
		this.columnName = columnName;
		this.trId=trId;
		this.eventBus = eventBus;
		
		this.buttonHelper.setIcon(ResourceBundleOperation.INSTANCE.help());
		
		buttonHelper.addSelectHandler(new SelectHandler() {
			
			@Override
			public void onSelect(SelectEvent event) {
				helperHtml.show();
			}
		});
		
		setWidth(ConstantsSplitMergeOperation.WIDTH);
		setHeight(ConstantsSplitMergeOperation.HEIGHT);
		setHeaderVisible(false);
		setBodyBorder(false);

		abstractOperation = new AbstactSplitMergeOperation() {

			@Override
			protected void initAbstactSplitMergeOperation(TRId trId, OperationID operationID, EventBus bus) throws Exception {
				
				this.eventBus = bus;
				this.operationID = operationID;
				this.trId = trId;
				
				if(operationID==null)
					throw new Exception("Input operation id is null. You must pass an OperationID not null");
				
				if(trId==null)
					throw new Exception("TRId is null. You must pass a valid TRId");
				
				this.initComboOperatorForOperationId();
				this.comboOperator.setAllowBlank(false);
			}

			@Override
			protected void updateComboOperatorStatus(TdOperatorComboOperator operator) {
				String[] constraints = operator.getValueConstraints();
				
				if(constraints!=null && constraints.length>0){
					textField.clear();
					textField.reset();
					for (String valueCs : constraints) {
						if(valueCs.compareTo(String.class.getName())==0){
							if(operator.getOperator().equals(TdOperatorEnum.CHAR_SEQUENCE))
								textField.setEmptyText("es. ,");
							else if(operator.getOperator().equals(TdOperatorEnum.REGEX))
								textField.setEmptyText("es. [a-z]");
						}else if(valueCs.compareTo(Integer.class.getName())==0){
							textField.setEmptyText("es. 1");
						}else
							textField.setEmptyText("");
					}
				}
				
			}

			@Override
			protected void setListener(MonitorDialogListener listener) {
				progressListener = listener;
			}
			
		};
		
//		abstractOperation.initAbstactSplitMergeOperation(trId, operationId, eventBus);
//		firstColumnData = new LoadComboColumnData(trId, eventBus, columnName, false);
//		secondColumnData = new LoadComboColumnData(trId, eventBus, columnName, false);
//		initForm();
		
		updateForm();
	}
	
	/**
	 * 
	 */
	protected void updateForm() {
		
		try {
			abstractOperation.initAbstactSplitMergeOperation(trId, operationId, eventBus);
			abstractOperation.setListener(INSTANCE);
			
		} catch (Exception e) {
			GWT.log("Error: "+e.getCause());
			
		}
		firstColumnData = new LoadComboColumnData(trId, eventBus, columnName, false);
		secondColumnData = new LoadComboColumnData(trId, eventBus, null, false);
		initForm();
		forceLayout();
	}
	
	public void update(TRId trId, String columnName) {
		this.trId = trId;
		this.columnName = columnName;
		//Update panel status
		forceLayout();
	}
	
	private void initForm(){

		textField = new TextField();
		textField.setAllowBlank(true);
		textField.setEmptyText("Values separator");
		
		textFieldColumnName1 = new TextField();
		textFieldColumnName1.setEmptyText("Column Name");
		textFieldColumnName1.setAllowBlank(false);

		submit = new TextButton("Merge Column");
		submit.setIcon(ResourceBundleOperation.INSTANCE.columnMerge16());
//		submit.setIcon(ResourceBundle.INSTANCE.columnSplit());
		submit.setIconAlign(IconAlign.RIGHT);
		
		submit.addSelectHandler(new SelectHandler() {

			public void onSelect(SelectEvent event) {

				if(isValidForm()){
					String value = textField.getCurrentValue();
					String label1 = textFieldColumnName1.getCurrentValue();
					ColumnData columnData1 = firstColumnData.getCurrentValue();
					ColumnData columnData2 = secondColumnData.getCurrentValue();
					TdOperatorComboOperator operator = 	abstractOperation.getDefaultValue();
					boolean deleteSourceColumn = checkBoxDeleteSrcColumns.getValue();
					value = value==null?"":value;
					abstractOperation.doOperationSubmit(value,operator,columnData1, columnData2,label1,null, comboColumnTypeCode.getCurrentValue(),null, selectedDataType, null,deleteSourceColumn);
				}
			}
		});

		VerticalLayoutContainer v = new VerticalLayoutContainer();
		v.add(new FieldLabel(firstColumnData.comboColumn, "Column One"), new VerticalLayoutData(1,-1));
		v.add(new FieldLabel(secondColumnData.comboColumn, "Column Two"), new VerticalLayoutData(1,-1));
		FieldLabel mergeLabel = new FieldLabel(abstractOperation.comboOperator, abstractOperation.operationID +" by");
		mergeLabel.setVisible(false);
		v.add(mergeLabel,new VerticalLayoutData(1,-1));
		v.add(new FieldLabel(textField, "Value"), new VerticalLayoutData(1, -1));
		v.add(new FieldLabel(textFieldColumnName1, "Column Label"), new VerticalLayoutData(1, -1));
		
		comboColumnTypeCode = ComboColumnTypeUtils.createComboColumType(trId);
		
		comboMeasureType = ComboColumnTypeUtils.createComboMeausureType();
		
		comboMeasureType.addSelectionHandler(new SelectionHandler<ColumnDataTypeElement>() {

			@Override
			public void onSelection(SelectionEvent<ColumnDataTypeElement> event) {
				
				ColumnDataTypeElement selected = comboMeasureType.getCurrentValue();
				if(selected!=null)
					selectedDataType = selected;
			}
			
		});
		
		comboAttributeType = ComboColumnTypeUtils.createComboAttributeType();
		comboAttributeType.addSelectionHandler(new SelectionHandler<ColumnDataTypeElement>() {

			@Override
			public void onSelection(SelectionEvent<ColumnDataTypeElement> event) {
				
				ColumnDataTypeElement selected = comboAttributeType.getCurrentValue();
				if(selected!=null)
					selectedDataType = selected;
			}
			
		});
		
		final FieldLabel comboAttributeTypeLabel = new FieldLabel(comboAttributeType,"Attribute Type");
		final FieldLabel comboMeasureTypeLabel = new FieldLabel(comboMeasureType, "Measure Type");
		
		/* COMMENTED GENERIC BEHAVIOUR 01/09/2014 
			comboColumnTypeCode.addSelectionHandler(new SelectionHandler<ColumnTypeCodeElement>() {
				
				@Override
				public void onSelection(SelectionEvent<ColumnTypeCodeElement> event) {
					
					comboMeasureTypeLabel.setVisible(false);
					comboAttributeTypeLabel.setVisible(false);
	
					
					ColumnTypeCodeElement selected = comboColumnTypeCode.getCurrentValue();
					
					if(selected!=null){
						
						boolean isMeasure = selected.getCode().equals(ColumnTypeCode.MEASURE);
						comboMeasureTypeLabel.setVisible(isMeasure);
						
						if(isMeasure)
							return;
						
						boolean isAttribute = selected.getCode().equals(ColumnTypeCode.ATTRIBUTE);
						comboAttributeTypeLabel.setVisible(isAttribute);
						
						if(isAttribute)
							return;
						
						comboMeasureTypeLabel.setVisible(false);
						comboAttributeTypeLabel.setVisible(false);
						selectedDataType = new ColumnDataTypeElement(4,ColumnDataType.Text);
					}
				}
			});
	
			v.add(new FieldLabel(comboColumnTypeCode, "Column Type"), new VerticalLayoutData(-1, -1, new Margins(10, 0, 10, 0)));
			v.add(comboMeasureTypeLabel, new VerticalLayoutData(-1, -1, new Margins(10, 0, 10, 0)));
			v.add(comboAttributeTypeLabel, new VerticalLayoutData(-1, -1, new Margins(10, 0, 10, 0)));
		*/
		
		//SET ColumnTypeCode COMBO SELECTION AS ATTRIBUTE OR ANNOTATION
		abstractOperation.setColumnTypeSelectedValue(comboColumnTypeCode, ColumnTypeCode.ATTRIBUTE);
		//SET AttributeType COMBO SELECTION AS TEXT
		abstractOperation.setDataTypeSelectedValue(comboAttributeType, ColumnDataType.Text);
		
		selectedDataType = comboAttributeType.getCurrentValue();
		GWT.log("Selected selectedDataType as "+selectedDataType);
		
		checkBoxDeleteSrcColumns = new CheckBox();
		checkBoxDeleteSrcColumns.setValue(true);
//		v.add(checkBoxDeleteSrcColumns, new VerticalLayoutData(100, -1, new Margins(10, 0, 10, 0)));
		FieldLabel fieldDeleteColumns = new FieldLabel(checkBoxDeleteSrcColumns, "Delete source columns");
		v.add(fieldDeleteColumns, new VerticalLayoutData(118, -1, new Margins(10, 0, 10, 0)));
		
		v.add(buttonHelper, new VerticalLayoutData(-1, -1, new Margins(5, 0, 10, 0)));
		
		v.add(submit, new VerticalLayoutData(-1, -1, new Margins(10, 0, 10, 0)));
		add(v, new VerticalLayoutData(-1, -1, new Margins()));
		
		comboAttributeTypeLabel.setVisible(false);
		comboMeasureTypeLabel.setVisible(false);
	}
	
	private boolean isValidForm(){
		String value = textField.getCurrentValue();
		
		ColumnData firstColumn = firstColumnData.comboColumn.getCurrentValue();
		ColumnData secondColum = secondColumnData.comboColumn.getCurrentValue();
		
		if(firstColumn==null){
			UtilsGXT3.alert("Attention", "You must pick the Column One!");
			return false;
		}else if(secondColum==null){
			UtilsGXT3.alert("Attention", "You must pick the Column Two!");
			return false;
		}else if (comboColumnTypeCode.getCurrentValue() == null) {
			UtilsGXT3.alert("Attention", "Insert type 'Column'!");
			return false;
		}else if (selectedDataType == null) {
				UtilsGXT3.alert("Attention", "Data type 'Column' is null!");
				return false;
		}else if(firstColumn.getName().compareTo(secondColum.getName())==0){
			if(firstColumn.getColumnId()!=null && secondColum.getColumnId()!=null && firstColumn.getColumnId().compareTo(secondColum.getColumnId())==0){
				UtilsGXT3.alert("Attention", "Column One and Column Two cannot refer the same column!");
				return false;
			}
		}
		
		String columnName1 = textFieldColumnName1.getCurrentValue();
		
//		if (value == null || value.isEmpty()) {
//			UtilsGXT3.alert("Attention", "Insert field 'Value'!");
//			return false;
//		}else 
		if (columnName1 == null || columnName1.isEmpty()) {
			UtilsGXT3.alert("Attention", "Insert field 'Column Name'!");
			return false;
		}else {
			TdOperatorComboOperator operator = 	abstractOperation.getDefaultValue();
			String[] valueConstraint = operator.getValueConstraints();
			
			if(FieldValidator.validateByClassName(String.class, valueConstraint)){
				return true;
			}
			
			if(FieldValidator.validateByClassName(Integer.class, valueConstraint)){
				try{
					Integer.parseInt(value);
					return true;
				}catch (Exception e) {
					UtilsGXT3.alert("Attention", "Field value must be an Integer!");
					return false;
				}
			}
		} 
		
		return true;
	}

	public OperationID getOperationID() {
		return abstractOperation.operationID;
	}
	
	
	
	
	protected void close() {
		/*
		 * if (parent != null) { parent.close(); }
		 */
	}

	// /
	@Override
	public void operationComplete(OperationResult operationResult) {
		ChangeTableWhy why = ChangeTableWhy.TABLEUPDATED;
		ChangeTableRequestEvent changeTableRequestEvent = new ChangeTableRequestEvent(
				ChangeTableRequestType.MERGECOLUMN, operationResult.getTrId(), why);
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
				ChangeTableRequestType.MERGECOLUMN, operationResult.getTrId(), why);
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
