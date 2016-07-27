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
 * ChangeColumnTypePanel is the panel for change column type
 * 
 * 
 * @author "Giancarlo Panichi" <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 * 
 * @author changed by Francesco Mangiacrapa
 * 
 */
public class SplitColumnPanel extends FramedPanel implements
		MonitorDialogListener {

	// protected ChangeColumnTypeDialog parent;
	protected TextField textFieldValue = null;
	protected TextField textFieldColumnName1 = null;
	protected TextField textFieldColumnName2 = null;
	protected TextButton submit;

	protected OperationID operationId = OperationID.SPLIT;

	protected TdColumnOperationServiceAsync serviceAsync = TdColumnOperationServiceAsync.Util
			.getInstance();

	protected LoadComboColumnData comboColumnData;
	protected AbstactSplitMergeOperation abstractOperation;

	private EventBus eventBus;
	protected String columnName;
	protected TRId trId;

	protected ComboBox<ColumnTypeCodeElement> comboColumnTypeCode1;
	protected ComboBox<ColumnTypeCodeElement> comboColumnTypeCode2;

	private SplitColumnPanel INSTANCE = this;

	private ComboBox<ColumnDataTypeElement> comboMeasureType1;
	private ComboBox<ColumnDataTypeElement> comboMeasureType2;
	private ComboBox<ColumnDataTypeElement> comboAttributeType1;
	private ComboBox<ColumnDataTypeElement> comboAttributeType2;

	private ColumnDataTypeElement selectedDataType1;
	private ColumnDataTypeElement selectedDataType2;

	private CheckBox checkBoxDeleteSrcColumns;
	private HelperHTML helperHtml = new HelperHTML("Split and Merge Helper",
			ResourceBundleOperation.INSTANCE.smHelper().getText());
	private TextButton buttonHelper = new TextButton("Helper");

	public SplitColumnPanel(TRId trId, String columnName, EventBus extBus) {
		this.columnName = columnName;
		this.trId = trId;

		setWidth(ConstantsSplitMergeOperation.WIDTH);
		setHeight(ConstantsSplitMergeOperation.HEIGHT);
		setHeaderVisible(false);
		setBodyBorder(false);
		this.eventBus = extBus;

		this.buttonHelper.setIcon(ResourceBundleOperation.INSTANCE.help());

		buttonHelper.addSelectHandler(new SelectHandler() {

			@Override
			public void onSelect(SelectEvent event) {
				helperHtml.show();
			}
		});

		abstractOperation = new AbstactSplitMergeOperation() {

			@Override
			protected void initAbstactSplitMergeOperation(TRId trId,
					OperationID operationID, EventBus bus) throws Exception {

				this.eventBus = bus;
				this.operationID = operationID;
				this.trId = trId;

				if (operationID == null)
					throw new Exception(
							"Input operation id is null. You must pass an OperationID not null");

				if (trId == null)
					throw new Exception(
							"TRId is null. You must pass a valid TRId");

				this.initComboOperatorForOperationId();
				this.comboOperator.setAllowBlank(false);
			}

			@Override
			protected void updateComboOperatorStatus(
					TdOperatorComboOperator operator) {
				String[] constraints = operator.getValueConstraints();

				if (constraints != null && constraints.length > 0) {
					textFieldValue.clear();
					textFieldValue.reset();
					for (String valueCs : constraints) {
						if (valueCs.compareTo(String.class.getName()) == 0) {
							if (operator.getOperator().equals(
									TdOperatorEnum.CHAR_SEQUENCE))
								textFieldValue.setEmptyText("es. ,");
							else if (operator.getOperator().equals(
									TdOperatorEnum.REGEX))
								textFieldValue.setEmptyText("es. [a-z]");
						} else if (valueCs.compareTo(Integer.class.getName()) == 0) {
							textFieldValue.setEmptyText("es. 1");
						} else
							textFieldValue.setEmptyText("");
					}
				}

			}

			@Override
			protected void setListener(MonitorDialogListener listener) {
				this.progressListener = listener;
			}

		};

		updateForm();
	}

	/**
	 * 
	 */
	protected void updateForm() {

		try {
			abstractOperation.initAbstactSplitMergeOperation(trId, operationId,
					eventBus);
			abstractOperation.setListener(INSTANCE);
		} catch (Exception e) {
			GWT.log("Error:" + e.getCause());
		}
		comboColumnData = new LoadComboColumnDataForSplit(trId, eventBus, columnName,
				false);
		initForm();
		forceLayout();
	}

	public void update(TRId trId, String columnName) {
		this.trId = trId;
		this.columnName = columnName;
		// Update panel status
		forceLayout();
	}

	private void initForm() {

		textFieldValue = new TextField();
		textFieldValue.setAllowBlank(false);

		textFieldColumnName1 = new TextField();
		textFieldColumnName1.setEmptyText("First Column Name");
		textFieldColumnName1.setAllowBlank(false);

		textFieldColumnName2 = new TextField();
		textFieldColumnName2.setEmptyText("Second Column Name");
		textFieldColumnName2.setAllowBlank(false);

		submit = new TextButton("Split Column");
		submit.setIcon(ResourceBundleOperation.INSTANCE.columnSplit16());
		submit.setIconAlign(IconAlign.RIGHT);

		submit.addSelectHandler(new SelectHandler() {

			public void onSelect(SelectEvent event) {

				if (isValidForm()) {
					String value = textFieldValue.getCurrentValue();
					String label1 = textFieldColumnName1.getCurrentValue();
					String label2 = textFieldColumnName2.getCurrentValue();
					ColumnData columnData = comboColumnData.getCurrentValue();
					boolean deleteSourceColumn = checkBoxDeleteSrcColumns
							.getValue();
					TdOperatorComboOperator operator = abstractOperation.comboOperator
							.getCurrentValue();
					abstractOperation.doOperationSubmit(value, operator,
							columnData, null, label1, label2,
							comboColumnTypeCode1.getCurrentValue(),
							comboColumnTypeCode2.getCurrentValue(),
							selectedDataType1, selectedDataType2,
							deleteSourceColumn);
				}
			}
		});

		VerticalLayoutContainer v = new VerticalLayoutContainer();
		v.add(new FieldLabel(comboColumnData.comboColumn, "Column"),
				new VerticalLayoutData(1, -1));
		v.add(new FieldLabel(abstractOperation.comboOperator,
				abstractOperation.operationID + " by"), new VerticalLayoutData(
				1, -1));
		v.add(new FieldLabel(textFieldValue, "Value"), new VerticalLayoutData(
				1, -1));

		v.add(new FieldLabel(textFieldColumnName1, "Column Label 1"),
				new VerticalLayoutData(1, -1));
		v.add(new FieldLabel(textFieldColumnName2, "Column Label 2"),
				new VerticalLayoutData(1, -1));

		comboColumnTypeCode1 = ComboColumnTypeUtils.createComboColumType(trId);
		
		comboColumnTypeCode2 = ComboColumnTypeUtils.createComboColumType(trId);

		comboMeasureType1 = ComboColumnTypeUtils.createComboMeausureType();

		comboMeasureType1.addSelectionHandler(new SelectionHandler<ColumnDataTypeElement>() {

					@Override
					public void onSelection(
							SelectionEvent<ColumnDataTypeElement> event) {

						ColumnDataTypeElement selected = comboMeasureType1.getCurrentValue();
						if (selected != null)
							selectedDataType1 = selected;
					}

				});

		comboMeasureType2 = ComboColumnTypeUtils.createComboMeausureType();

		comboMeasureType2.addSelectionHandler(new SelectionHandler<ColumnDataTypeElement>() {

					@Override
					public void onSelection(
							SelectionEvent<ColumnDataTypeElement> event) {

						ColumnDataTypeElement selected = comboMeasureType2
								.getCurrentValue();
						if (selected != null)
							selectedDataType2 = selected;
					}

				});

		final FieldLabel comboMeasureTypeLabel1 = new FieldLabel(comboMeasureType1, "Data Type 1");
		final FieldLabel comboMeasureTypeLabel2 = new FieldLabel(comboMeasureType2, "Data Type 2");

		comboAttributeType1 = ComboColumnTypeUtils.createComboAttributeType();

		comboAttributeType1.addSelectionHandler(new SelectionHandler<ColumnDataTypeElement>() {

					@Override
					public void onSelection(SelectionEvent<ColumnDataTypeElement> event) {

						ColumnDataTypeElement selected = comboAttributeType1.getCurrentValue();
						if (selected != null)
							selectedDataType1 = selected;
						
						GWT.log("Selected selectedDataType1 as: "+selectedDataType1);
					}

		});

		comboAttributeType2 = ComboColumnTypeUtils.createComboAttributeType();

		comboAttributeType2.addSelectionHandler(new SelectionHandler<ColumnDataTypeElement>() {

					@Override
					public void onSelection(
							SelectionEvent<ColumnDataTypeElement> event) {

						ColumnDataTypeElement selected = comboAttributeType2.getCurrentValue();
						if (selected != null)
							selectedDataType2 = selected;
						
						GWT.log("Selected selectedDataType2 as: "+selectedDataType2);
					}

		});

		final FieldLabel comboAttributeTypeLabel1 = new FieldLabel(
				comboAttributeType1, "Data Type 1");
		final FieldLabel comboAttributeTypeLabel2 = new FieldLabel(
				comboAttributeType2, "Data Type 2");

		/* COMMENTED GENERIC BEHAVIOUR 01/09/2014 
			comboColumnTypeCode1.addSelectionHandler(new SelectionHandler<ColumnTypeCodeElement>() {
	
				@Override
				public void onSelection(SelectionEvent<ColumnTypeCodeElement> event) {
	
					comboMeasureTypeLabel1.setVisible(false);
					comboAttributeTypeLabel1.setVisible(false);
	
					ColumnTypeCodeElement selected = comboColumnTypeCode1.getCurrentValue();
	
					if (selected != null) {
	
						boolean isMeasure = selected.getCode().equals(ColumnTypeCode.MEASURE);
						comboMeasureTypeLabel1.setVisible(isMeasure);
	
						if (isMeasure)
							return;
	
						boolean isAttribute = selected.getCode().equals(ColumnTypeCode.ATTRIBUTE);
						comboAttributeTypeLabel1.setVisible(isAttribute);
	
						if (isAttribute)
							return;
	
						comboMeasureTypeLabel1.setVisible(false);
						comboAttributeTypeLabel1.setVisible(false);
						selectedDataType1 = new ColumnDataTypeElement(4,ColumnDataType.Text);
					}
				}
			});

			comboColumnTypeCode2.addSelectionHandler(new SelectionHandler<ColumnTypeCodeElement>() {

				@Override
				public void onSelection(SelectionEvent<ColumnTypeCodeElement> event) {

					comboMeasureTypeLabel2.setVisible(false);
					comboAttributeTypeLabel2.setVisible(false);

					ColumnTypeCodeElement selected = comboColumnTypeCode2
							.getCurrentValue();

					if (selected != null) {

						boolean isMeasure = selected.getCode().equals(ColumnTypeCode.MEASURE);
						comboMeasureTypeLabel2.setVisible(isMeasure);

						if (isMeasure)
							return;

						boolean isAttribute = selected.getCode().equals(ColumnTypeCode.ATTRIBUTE);
						comboAttributeTypeLabel2.setVisible(isAttribute);

						if (isAttribute)
							return;

						comboMeasureTypeLabel2.setVisible(false);
						comboAttributeTypeLabel2.setVisible(false);
						selectedDataType2 = new ColumnDataTypeElement(4,ColumnDataType.Text);
					}
				}
			});

		
			v.add(new FieldLabel(comboColumnTypeCode1, "Column Type 1"),new VerticalLayoutData(-1, -1, new Margins(10, 0, 10, 0)));
			v.add(comboMeasureTypeLabel1, new VerticalLayoutData(-1, -1,new Margins(10, 0, 10, 0)));
			v.add(comboAttributeTypeLabel1, new VerticalLayoutData(-1, -1,new Margins(10, 0, 10, 0)));
			v.add(new FieldLabel(comboColumnTypeCode2, "Column Type 2"),new VerticalLayoutData(-1, -1, new Margins(10, 0, 10, 0)));
			v.add(comboMeasureTypeLabel2, new VerticalLayoutData(-1, -1,new Margins(10, 0, 10, 0)));
			v.add(comboAttributeTypeLabel2, new VerticalLayoutData(-1, -1,new Margins(10, 0, 10, 0)));
		 */
		
		//SET ColumnType COMBOs SELECTION AS ATTRIBUTE OR ANNOTATION
		abstractOperation.setColumnTypeSelectedValue(comboColumnTypeCode1, ColumnTypeCode.ATTRIBUTE);
		abstractOperation.setColumnTypeSelectedValue(comboColumnTypeCode2, ColumnTypeCode.ATTRIBUTE);
		
		//SET AttributeType COMBOs SELECTION AS TEXT
		abstractOperation.setDataTypeSelectedValue(comboAttributeType1, ColumnDataType.Text);
		abstractOperation.setDataTypeSelectedValue(comboAttributeType2, ColumnDataType.Text);
		selectedDataType1 = comboAttributeType1.getCurrentValue();
		GWT.log("Selected selectedDataType1 as: "+selectedDataType1);
		selectedDataType2 = comboAttributeType2.getCurrentValue();
		GWT.log("Selected selectedDataType2 as: "+selectedDataType2);
		
		
		checkBoxDeleteSrcColumns = new CheckBox();
		checkBoxDeleteSrcColumns.setValue(true);
		// v.add(checkBoxDeleteSrcColumns, new VerticalLayoutData(100, -1, new
		// Margins(10, 0, 10, 0)));
		FieldLabel fieldDeleteColumns = new FieldLabel(
				checkBoxDeleteSrcColumns, "Delete source column");
		v.add(fieldDeleteColumns, new VerticalLayoutData(118, -1, new Margins(
				10, 0, 10, 0)));

		v.add(buttonHelper, new VerticalLayoutData(-1, -1, new Margins(5, 0,
				10, 0)));
		v.add(submit, new VerticalLayoutData(-1, -1, new Margins(10, 0, 10, 0)));
		add(v, new VerticalLayoutData(-1, -1, new Margins()));

		comboMeasureTypeLabel1.setVisible(false);
		comboMeasureTypeLabel2.setVisible(false);
		comboAttributeTypeLabel1.setVisible(false);
		comboAttributeTypeLabel2.setVisible(false);

	}

	private boolean isValidForm() {
		String value = textFieldValue.getCurrentValue();
		String columnName1 = textFieldColumnName1.getCurrentValue();
		String columnName2 = textFieldColumnName2.getCurrentValue();

		if (comboColumnData.comboColumn.getCurrentValue() == null) {
			UtilsGXT3.alert("Attention", "You must pick a Column!");
			return false;
		} else if (abstractOperation.comboOperator.getCurrentValue() == null) {
			UtilsGXT3.alert("Attention", "You must pick an Operator!");
			return false;
		} else if (value == null || value.isEmpty()) {
			UtilsGXT3.alert("Attention", "Insert field 'Value'!");
			return false;
		} else if (columnName1 == null || columnName1.isEmpty()) {
			UtilsGXT3.alert("Attention", "Insert field 'First Column Name'!");
			return false;
		} else if (columnName2 == null || columnName2.isEmpty()) {
			UtilsGXT3.alert("Attention", "Insert field 'Second Column Name'!");
			return false;
		} else if (comboColumnTypeCode1.getCurrentValue() == null) {
			UtilsGXT3.alert("Attention", "Insert type 'Column 1'!");
			return false;
		} else if (comboColumnTypeCode2.getCurrentValue() == null) {
			UtilsGXT3.alert("Attention", "Insert type 'Column 2'!");
			return false;
		} else if (selectedDataType1 == null) {
			UtilsGXT3.alert("Attention", "Data type 'Column 1' is null!");
			return false;
		} else if (selectedDataType2 == null) {
			UtilsGXT3.alert("Attention", "Data type 'Column 2' is null!");
			return false;
		} else {
			TdOperatorComboOperator operator = abstractOperation.comboOperator
					.getCurrentValue();
			String[] valueConstraint = operator.getValueConstraints();

			if (FieldValidator.validateByClassName(String.class,
					valueConstraint)) {
				return true;
			}

			if (FieldValidator.validateByClassName(Integer.class,
					valueConstraint)) {
				try {
					Integer.parseInt(value);
					return true;
				} catch (Exception e) {
					UtilsGXT3.alert("Attention",
							"Field value must be an Integer!");
					return false;
				}
			}
		}

		return true;
	}

	public OperationID getOperationID() {
		return abstractOperation.operationID;
	}

	public ColumnDataTypeElement getSelectedDataType1() {
		return selectedDataType1;
	}

	public ColumnDataTypeElement getSelectedDataType2() {
		return selectedDataType2;
	}

	public void setSelectedDataType1(ColumnDataTypeElement selectedDataType1) {
		this.selectedDataType1 = selectedDataType1;
	}

	public void setSelectedDataType2(ColumnDataTypeElement selectedDataType2) {
		this.selectedDataType2 = selectedDataType2;
	}

	protected void close() {
		/*
		 * if (parent != null) { parent.close(); }
		 */
	}

	@Override
	public void operationComplete(OperationResult operationResult) {
		ChangeTableWhy why = ChangeTableWhy.TABLEUPDATED;
		ChangeTableRequestEvent changeTableRequestEvent = new ChangeTableRequestEvent(
				ChangeTableRequestType.SPLITCOLUMN, operationResult.getTrId(), why);
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
				ChangeTableRequestType.SPLITCOLUMN, operationResult.getTrId(), why);
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
