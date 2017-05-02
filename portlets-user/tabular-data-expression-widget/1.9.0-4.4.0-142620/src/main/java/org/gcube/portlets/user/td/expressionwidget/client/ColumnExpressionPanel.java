package org.gcube.portlets.user.td.expressionwidget.client;

import java.util.ArrayList;
import java.util.Date;

import org.gcube.portlets.user.td.expressionwidget.client.expression.ConditionWidget;
import org.gcube.portlets.user.td.expressionwidget.client.notification.ExpressionWrapperNotification;
import org.gcube.portlets.user.td.expressionwidget.client.properties.ColumnDataPropertiesCombo;
import org.gcube.portlets.user.td.expressionwidget.client.properties.ColumnDataTypeProperties;
import org.gcube.portlets.user.td.expressionwidget.client.resources.ExpressionResources;
import org.gcube.portlets.user.td.expressionwidget.client.store.ColumnDataTypeElement;
import org.gcube.portlets.user.td.expressionwidget.client.store.ColumnDataTypeStore;
import org.gcube.portlets.user.td.expressionwidget.shared.exception.ConditionTypeMapException;
import org.gcube.portlets.user.td.expressionwidget.shared.model.logical.C_Not;
import org.gcube.portlets.user.td.gwtservice.shared.rule.RuleScopeType;
import org.gcube.portlets.user.td.gwtservice.shared.rule.description.RuleDescriptionData;
import org.gcube.portlets.user.td.gwtservice.shared.rule.type.TDBaseColumnRuleType;
import org.gcube.portlets.user.td.gwtservice.shared.rule.type.TDRuleType;
import org.gcube.portlets.user.td.monitorwidget.client.utils.UtilsGXT3;
import org.gcube.portlets.user.td.widgetcommonevent.client.expression.C_ExpressionContainer;
import org.gcube.portlets.user.td.widgetcommonevent.client.expression.ExpressionWrapper;
import org.gcube.portlets.user.td.widgetcommonevent.shared.expression.C_Expression;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnData;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnDataType;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnMockUp;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnTypeCode;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.web.bindery.event.shared.EventBus;
import com.sencha.gxt.cell.core.client.ButtonCell.IconAlign;
import com.sencha.gxt.cell.core.client.form.ComboBoxCell.TriggerAction;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.widget.core.client.FramedPanel;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.BoxLayoutContainer.BoxLayoutData;
import com.sencha.gxt.widget.core.client.container.BoxLayoutContainer.BoxLayoutPack;
import com.sencha.gxt.widget.core.client.container.HBoxLayoutContainer;
import com.sencha.gxt.widget.core.client.container.HBoxLayoutContainer.HBoxLayoutAlign;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.ComboBox;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.form.FieldSet;
import com.sencha.gxt.widget.core.client.form.TextArea;
import com.sencha.gxt.widget.core.client.form.TextField;
import com.sencha.gxt.widget.core.client.info.Info;

/**
 * 
 * @author "Giancarlo Panichi" <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 * 
 */
public class ColumnExpressionPanel extends FramedPanel {
	private static final String RULE_DESCRIPTION_HEIGHT = "44px";
	private static final String RULE_PLACE_HOLDER_ID = "Column";
	private static final String WIDTH = "658px";
	private static final String HEIGHT = "364px";
	private static final String RULE_HEIGHT = "388px";
	
	private enum ColumnExpressionPanelType {
		ColumnFilter, RowDeleteByExpression, Template, RuleOnColumn;
	}

	private ColumnExpressionPanelType type;

	private TemplateColumnExpressionDialog parentTemplateDialog;
	private ColumnFilterDialog parentFilterDialog;
	private RowsDeleteByExpressionDialog parentRowsDeleteByExpressionDialog;

	private ColumnData column;
	private ArrayList<ColumnData> columns;

	private TextButton btnApply;
	private TextButton btnClose;

	private FieldSet conditionsField;
	private ConditionWidget conditionWidget;
	private ComboBox<ColumnData> comboCols;
	private RuleOnColumnCreateDialog parentRuleOnColumnCreateDialog;

	private RuleDescriptionData initialRuleDescriptionData;

	private TextField ruleName;
	private TextArea ruleDescription;

	private ComboBox<ColumnDataTypeElement> comboDataType;

	private FieldLabel comboDataTypeLabel;

	/**
	 * 
	 * @param parentFilterDialog
	 * @param column
	 * @param columns
	 * @param eventBus
	 */
	public ColumnExpressionPanel(ColumnFilterDialog parentFilterDialog,
			ColumnData column, ArrayList<ColumnData> columns, EventBus eventBus) {
		super();
		setWidth(WIDTH);
		setHeight(HEIGHT);
		type = ColumnExpressionPanelType.ColumnFilter;
		this.parentFilterDialog = parentFilterDialog;
		this.column = column;
		this.columns = columns;
		Log.debug("Column:" + column);
		createOnFilter();
	}

	/**
	 * 
	 * @param parentRowsDeleteByExpressionDialog
	 * @param column
	 * @param columns
	 * @param eventBus
	 */
	public ColumnExpressionPanel(
			RowsDeleteByExpressionDialog parentRowsDeleteByExpressionDialog,
			ColumnData column, ArrayList<ColumnData> columns, EventBus eventBus) {
		super();
		setWidth(WIDTH);
		setHeight(RULE_HEIGHT);
		type = ColumnExpressionPanelType.RowDeleteByExpression;
		this.parentRowsDeleteByExpressionDialog = parentRowsDeleteByExpressionDialog;
		this.column = column;
		this.columns = columns;
		Log.debug("Column:" + column);
		createOnRowsDeleteByExpression();
	}

	/**
	 * 
	 * @param parentColumnExpressionDialog
	 * @param column
	 * @param eventBus
	 */
	public ColumnExpressionPanel(
			TemplateColumnExpressionDialog parentColumnExpressionDialog,
			ColumnData column, EventBus eventBus) {
		super();
		setWidth(WIDTH);
		setHeight(HEIGHT);
		type = ColumnExpressionPanelType.Template;
		this.parentTemplateDialog = parentColumnExpressionDialog;
		this.column = column;
		this.columns = null;
		Log.debug("Column:" + column);

		createOnTemplate();
	}

	/**
	 * 
	 * @param parent
	 * @param eventBus
	 */
	public ColumnExpressionPanel(RuleOnColumnCreateDialog parent,
			EventBus eventBus) {
		super();
		setWidth(WIDTH);
		setHeight(HEIGHT);
		type = ColumnExpressionPanelType.RuleOnColumn;
		this.parentRuleOnColumnCreateDialog = parent;
		this.column = null;
		this.columns = null;
		Log.debug("Column:" + column);

		createOnRule();
	}

	protected void createOnRule() {
		forceLayoutOnResize = true;

		setBodyBorder(false);
		setHeaderVisible(false);

		VerticalLayoutContainer basicLayout = new VerticalLayoutContainer();
		basicLayout.setAdjustForScroll(true);

		FieldSet properties = null;
		VerticalLayoutContainer propertiesLayout;

		HBoxLayoutContainer flowButton = new HBoxLayoutContainer();
		flowButton.setHBoxLayoutAlign(HBoxLayoutAlign.MIDDLE);
		flowButton.setPack(BoxLayoutPack.CENTER);

		properties = new FieldSet();
		properties.setHeadingText("Properties");
		properties.setCollapsible(false);

		propertiesLayout = new VerticalLayoutContainer();
		properties.add(propertiesLayout);

		createColumnMockUp(propertiesLayout);

		conditionsField = new FieldSet();
		conditionsField.setHeadingText("Conditions");
		conditionsField.setCollapsible(false);

		conditionWidget = new ConditionWidget(column);
		Log.debug("ConditionWidget" + conditionWidget);
		conditionsField.add(conditionWidget);

		btnApply = new TextButton("Save");
		btnApply.setIcon(ExpressionResources.INSTANCE.ruleColumnAdd());
		btnApply.setIconAlign(IconAlign.RIGHT);
		btnApply.setToolTip("Save rule");
		btnApply.addSelectHandler(new SelectHandler() {

			public void onSelect(SelectEvent event) {
				Log.debug("Pressed Save");
				applySeleceted();

			}
		});

		btnClose = new TextButton("Close");
		btnClose.setIcon(ExpressionResources.INSTANCE.close());
		btnClose.setIconAlign(IconAlign.RIGHT);
		btnClose.setToolTip("Cancel rule");
		btnClose.addSelectHandler(new SelectHandler() {

			public void onSelect(SelectEvent event) {
				Log.debug("Pressed Close");
				close();
			}
		});

		flowButton.add(btnApply, new BoxLayoutData(new Margins(2, 4, 2, 4)));

		flowButton.add(btnClose, new BoxLayoutData(new Margins(2, 4, 2, 4)));

		basicLayout.add(properties, new VerticalLayoutData(1, -1,
				new Margins(1)));
		basicLayout.add(conditionsField, new VerticalLayoutData(1, -1,
				new Margins(1)));
		basicLayout.add(flowButton, new VerticalLayoutData(1, 36, new Margins(
				5, 2, 5, 2)));
		add(basicLayout);

		if (initialRuleDescriptionData == null) {
			conditionWidget.disable();
		} else {
			updateCondition();
		}

	}

	private void createColumnMockUp(VerticalLayoutContainer propertiesLayout) {
		ruleName = new TextField();
		ruleName.setToolTip("Rule Name");
		if (initialRuleDescriptionData != null) {
			ruleName.setValue(initialRuleDescriptionData.getName());
		}
		FieldLabel ruleNameLabel = new FieldLabel(ruleName, "Rule Name");

		ruleDescription = new TextArea();
		ruleDescription.setHeight(RULE_DESCRIPTION_HEIGHT);
		ruleDescription.setToolTip("Rule Description");
		if (initialRuleDescriptionData != null) {
			ruleDescription.setValue(initialRuleDescriptionData
					.getDescription());
		}
		FieldLabel ruleDescriptionLabel = new FieldLabel(ruleDescription,
				"Rule Description");

		// comboDataType
		ColumnDataTypeProperties propsDataType = GWT
				.create(ColumnDataTypeProperties.class);
		ListStore<ColumnDataTypeElement> storeComboDataType = new ListStore<ColumnDataTypeElement>(
				propsDataType.id());
		storeComboDataType.addAll(ColumnDataTypeStore.getAttributeType());

		comboDataType = new ComboBox<ColumnDataTypeElement>(storeComboDataType,
				propsDataType.label());
		Log.trace("ComboDataType created");

		addHandlersForComboAttributeType(propsDataType.label());

		comboDataType.setEmptyText("Select a column type...");
		comboDataType.setWidth(191);
		comboDataType.setTypeAhead(true);
		comboDataType.setTriggerAction(TriggerAction.ALL);

		if (initialRuleDescriptionData != null) {
			Log.debug("Initial RuleDescriptionData: "
					+ initialRuleDescriptionData);
			ColumnDataType cdt = retrieveColumnDataType();
			if (cdt != null) {
				Log.debug("Retrieved column data type: " + cdt);
				ColumnDataTypeElement cdte = ColumnDataTypeStore
						.selectedAttributeElement(cdt);
				if (cdte != null) {
					comboDataType.setValue(cdte);

				}
			} else {
				Log.debug("Retrieved column data type null");
			}
		}

		comboDataTypeLabel = new FieldLabel(comboDataType, "Data Type");

		propertiesLayout.add(ruleNameLabel, new VerticalLayoutData(1, -1,
				new Margins(0)));
		propertiesLayout.add(ruleDescriptionLabel, new VerticalLayoutData(1,
				-1, new Margins(0)));

		propertiesLayout.add(comboDataTypeLabel, new VerticalLayoutData(1, -1,
				new Margins(0)));

	}

	private ColumnDataType retrieveColumnDataType() {
		TDRuleType tdRuleColumnType = initialRuleDescriptionData
				.getTdRuleType();
		if (tdRuleColumnType instanceof TDBaseColumnRuleType) {
			TDBaseColumnRuleType tdBaseColumnRuleType = (TDBaseColumnRuleType) tdRuleColumnType;
			return tdBaseColumnRuleType.getColumnDataType();
		}

		return null;

	}

	protected void addHandlersForComboAttributeType(
			final LabelProvider<ColumnDataTypeElement> labelProvider) {
		comboDataType
				.addSelectionHandler(new SelectionHandler<ColumnDataTypeElement>() {
					public void onSelection(
							SelectionEvent<ColumnDataTypeElement> event) {
						Info.display(
								"Attribute Type Selected",
								"You selected "
										+ (event.getSelectedItem() == null ? "nothing"
												: labelProvider.getLabel(event
														.getSelectedItem())
														+ "!"));
						Log.debug("ComboAttributeType selected: "
								+ event.getSelectedItem());
						ColumnDataTypeElement attributeType = event
								.getSelectedItem();
						updateDataType(attributeType.getType());
					}

				});
	}

	protected void updateCondition() {
		ColumnMockUp columnMockUp = retrieveColumnMockUp();
		if (columnMockUp != null) {
			column = new ColumnData();
			column.setId(columnMockUp.getId());
			column.setColumnId(columnMockUp.getColumnId());
			column.setLabel(columnMockUp.getLabel());
			column.setDataTypeName(columnMockUp.getColumnDataType().toString());
			conditionWidget.update(column);
			conditionWidget.enable();
		} else {
			conditionWidget.disable();
		}
		forceLayout();
	}

	protected ColumnMockUp retrieveColumnMockUp() {
		ColumnMockUp columnMockUp = null;

		ColumnDataTypeElement columnDataTypeElement = comboDataType
				.getCurrentValue();
		if (columnDataTypeElement != null) {
			ColumnDataType dataType = columnDataTypeElement.getType();
			if (dataType != null) {
				columnMockUp = new ColumnMockUp(null, RULE_PLACE_HOLDER_ID,
						dataType, RULE_PLACE_HOLDER_ID);

			} else {

			}
		} else {

		}

		return columnMockUp;
	}

	protected ColumnMockUp checkEnterData() {
		ColumnMockUp columnMockUp = null;

		String ruleNameS = ruleName.getCurrentValue();

		if (ruleNameS != null && !ruleNameS.isEmpty()) {

			String ruleDescriptionS = ruleDescription.getCurrentValue();

			if (ruleDescriptionS != null && !ruleDescriptionS.isEmpty()) {
				ColumnDataTypeElement columnDataTypeElement = comboDataType
						.getCurrentValue();
				if (columnDataTypeElement != null) {
					ColumnDataType dataType = columnDataTypeElement.getType();
					if (dataType != null) {
						columnMockUp = new ColumnMockUp(null,
								RULE_PLACE_HOLDER_ID, dataType,
								RULE_PLACE_HOLDER_ID);

					} else {
						UtilsGXT3.alert("Attention",
								"Column data type not selected!");
					}
				} else {
					UtilsGXT3.alert("Attention",
							"Column data type not selected!");
				}

			} else {
				UtilsGXT3.alert("Attention",
						"Enter a valid description for the rule!");
			}
		} else {
			UtilsGXT3.alert("Attention", "Enter a valid name for the rule!");
		}
		return columnMockUp;

	}

	protected RuleDescriptionData retrieveRuleDescriptionData(C_Expression exp) {
		ColumnMockUp columnMockUp = checkEnterData();
		if (columnMockUp != null) {
			if (exp != null) {
				String ruleNameS = ruleName.getCurrentValue();
				String ruleDescriptionS = ruleDescription.getCurrentValue();
				TDBaseColumnRuleType tdBaseColumnRule = new TDBaseColumnRuleType(
						columnMockUp.getColumnDataType());

				RuleDescriptionData ruleDescriptionData = new RuleDescriptionData(
						0, ruleNameS, ruleDescriptionS, new Date(),
						null, null, RuleScopeType.COLUMN, exp, tdBaseColumnRule);
				return ruleDescriptionData;
			} else {
				UtilsGXT3.alert("Attention", "Enter a valid condition!");
				return null;
			}
		} else {
			return null;
		}
	}

	protected void updateDataType(ColumnDataType type) {
		Log.debug("Update ColumnDataType: " + type);
		updateCondition();

	}

	protected void createOnTemplate() {
		// Important: fixed rendering of widgets
		forceLayoutOnResize = true;

		setBodyBorder(false);
		setHeaderVisible(false);

		VerticalLayoutContainer basicLayout = new VerticalLayoutContainer();
		basicLayout.setAdjustForScroll(true);
		// basicLayout.setScrollMode(ScrollMode.AUTO); Set In GXT 3.0.1

		FieldSet properties = null;
		VerticalLayoutContainer propertiesLayout;

		HBoxLayoutContainer flowButton = new HBoxLayoutContainer();
		flowButton.setHBoxLayoutAlign(HBoxLayoutAlign.MIDDLE);
		flowButton.setPack(BoxLayoutPack.CENTER);

		properties = new FieldSet();
		properties.setHeadingText("Properties");
		properties.setCollapsible(false);

		propertiesLayout = new VerticalLayoutContainer();
		properties.add(propertiesLayout);

		TextField columnType = new TextField();
		columnType.setToolTip("The type of column");
		columnType.setReadOnly(true);
		columnType.setValue(column.getTypeCode());
		propertiesLayout.add(new FieldLabel(columnType, "Column Type"),
				new VerticalLayoutData(1, -1));

		TextField dataType = new TextField();
		dataType.setToolTip("The data type");
		dataType.setReadOnly(true);
		dataType.setValue(column.getDataTypeName());
		propertiesLayout.add(new FieldLabel(dataType, "Data Type"),
				new VerticalLayoutData(1, -1));

		conditionsField = new FieldSet();
		conditionsField.setHeadingText("Conditions");
		conditionsField.setCollapsible(false);

		conditionWidget = new ConditionWidget(column);
		Log.debug("ConditionWidget" + conditionWidget);
		conditionsField.add(conditionWidget);

		btnApply = new TextButton("Add");
		btnApply.setIcon(ExpressionResources.INSTANCE.ruleColumnAdd());
		btnApply.setIconAlign(IconAlign.RIGHT);
		btnApply.setToolTip("Add");
		btnApply.addSelectHandler(new SelectHandler() {

			public void onSelect(SelectEvent event) {
				Log.debug("Pressed Add");
				applySeleceted();

			}
		});

		btnClose = new TextButton("Close");
		btnClose.setIcon(ExpressionResources.INSTANCE.close());
		btnClose.setIconAlign(IconAlign.RIGHT);
		btnClose.setToolTip("Cancel rule");
		btnClose.addSelectHandler(new SelectHandler() {

			public void onSelect(SelectEvent event) {
				Log.debug("Pressed Close");
				close();
			}
		});

		flowButton.add(btnApply, new BoxLayoutData(new Margins(2, 4, 2, 4)));

		flowButton.add(btnClose, new BoxLayoutData(new Margins(2, 4, 2, 4)));

		basicLayout.add(properties, new VerticalLayoutData(1, -1,
				new Margins(1)));
		basicLayout.add(conditionsField, new VerticalLayoutData(1, -1,
				new Margins(1)));
		basicLayout.add(flowButton, new VerticalLayoutData(1, 36, new Margins(
				5, 2, 5, 2)));
		add(basicLayout);

	}

	protected void createOnFilter() {
		forceLayoutOnResize = true;

		setBodyBorder(false);
		setHeaderVisible(false);

		VerticalLayoutContainer basicLayout = new VerticalLayoutContainer();
		basicLayout.setAdjustForScroll(true);

		FieldSet properties = null;
		VerticalLayoutContainer propertiesLayout;

		HBoxLayoutContainer flowButton = new HBoxLayoutContainer();
		flowButton.setHBoxLayoutAlign(HBoxLayoutAlign.MIDDLE);
		flowButton.setPack(BoxLayoutPack.CENTER);

		properties = new FieldSet();
		properties.setHeadingText("Properties");
		properties.setCollapsible(false);

		propertiesLayout = new VerticalLayoutContainer();
		properties.add(propertiesLayout);

		// Combo Column
		ColumnDataPropertiesCombo propsCols = GWT
				.create(ColumnDataPropertiesCombo.class);
		Log.debug("Props: " + propsCols);
		final ListStore<ColumnData> storeCols = new ListStore<ColumnData>(
				propsCols.id());
		Log.debug("Store Col: " + storeCols);
		storeCols.addAll(columns);

		Log.debug("StoreCol created");
		comboCols = new ComboBox<ColumnData>(storeCols, propsCols.label());

		Log.debug("Combo Threshold created");

		comboCols.addSelectionHandler(comboColsSelection());

		comboCols.setEmptyText("Select a column...");
		comboCols.setEditable(false);
		comboCols.setTriggerAction(TriggerAction.ALL);

		setSelectedColumn();

		propertiesLayout.add(new FieldLabel(comboCols, "Column"),
				new VerticalLayoutData(1, -1));

		//
		conditionsField = new FieldSet();
		conditionsField.setHeadingText("Conditions");
		conditionsField.setCollapsible(false);

		conditionWidget = new ConditionWidget(column);
		Log.debug("ConditionWidget" + conditionWidget);
		conditionsField.add(conditionWidget);

		btnApply = new TextButton("Apply");
		btnApply.setIcon(ExpressionResources.INSTANCE.applyFilter());
		btnApply.setIconAlign(IconAlign.RIGHT);
		btnApply.setToolTip("Apply Filter");
		btnApply.addSelectHandler(new SelectHandler() {

			public void onSelect(SelectEvent event) {
				Log.debug("Pressed Apply");
				applySeleceted();

			}
		});
		if (column == null) {
			btnApply.disable();
		}

		btnClose = new TextButton("Close");
		btnClose.setIcon(ExpressionResources.INSTANCE.close());
		btnClose.setIconAlign(IconAlign.RIGHT);
		btnClose.setToolTip("Cancel filter");
		btnClose.addSelectHandler(new SelectHandler() {

			public void onSelect(SelectEvent event) {
				Log.debug("Pressed Close");
				close();
			}
		});
		flowButton.add(btnApply, new BoxLayoutData(new Margins(2, 4, 2, 4)));
		flowButton.add(btnClose, new BoxLayoutData(new Margins(2, 4, 2, 4)));

		basicLayout.add(properties, new VerticalLayoutData(1, -1,
				new Margins(1)));
		basicLayout.add(conditionsField, new VerticalLayoutData(1, -1,
				new Margins(1)));
		basicLayout.add(flowButton, new VerticalLayoutData(1, 36, new Margins(
				5, 2, 5, 2)));

		add(basicLayout);

	}

	protected void createOnRowsDeleteByExpression() {
		forceLayoutOnResize = true;

		setBodyBorder(false);
		setHeaderVisible(false);

		VerticalLayoutContainer basicLayout = new VerticalLayoutContainer();
		basicLayout.setAdjustForScroll(true);

		FieldSet properties = null;
		VerticalLayoutContainer propertiesLayout;

		HBoxLayoutContainer flowButton = new HBoxLayoutContainer();
		flowButton.setHBoxLayoutAlign(HBoxLayoutAlign.MIDDLE);
		flowButton.setPack(BoxLayoutPack.CENTER);

		properties = new FieldSet();
		properties.setHeadingText("Properties");
		properties.setCollapsible(false);

		propertiesLayout = new VerticalLayoutContainer();
		properties.add(propertiesLayout);

		// Combo Column
		ColumnDataPropertiesCombo propsCols = GWT
				.create(ColumnDataPropertiesCombo.class);
		Log.debug("Props: " + propsCols);
		final ListStore<ColumnData> storeCols = new ListStore<ColumnData>(
				propsCols.id());
		Log.debug("Store Col: " + storeCols);
		storeCols.addAll(columns);

		Log.debug("StoreCol created");
		comboCols = new ComboBox<ColumnData>(storeCols, propsCols.label());

		Log.debug("Combo Threshold created");

		comboCols.addSelectionHandler(comboColsSelection());

		comboCols.setEmptyText("Select a column...");
		comboCols.setEditable(false);
		comboCols.setTriggerAction(TriggerAction.ALL);

		setSelectedColumn();

		propertiesLayout.add(new FieldLabel(comboCols, "Column"),
				new VerticalLayoutData(1, -1));

		//
		conditionsField = new FieldSet();
		conditionsField.setHeadingText("Conditions");
		conditionsField.setCollapsible(false);

		conditionWidget = new ConditionWidget(column);
		Log.debug("ConditionWidget" + conditionWidget);
		conditionsField.add(conditionWidget);

		btnApply = new TextButton("Delete");
		btnApply.setIcon(ExpressionResources.INSTANCE
				.tableRowDeleteByExpression());
		btnApply.setIconAlign(IconAlign.RIGHT);
		btnApply.setToolTip("Delete rows");
		btnApply.addSelectHandler(new SelectHandler() {

			public void onSelect(SelectEvent event) {
				Log.debug("Pressed Delete");
				applySeleceted();

			}
		});
		if (column == null) {
			btnApply.disable();
		}

		btnClose = new TextButton("Close");
		btnClose.setIcon(ExpressionResources.INSTANCE.close());
		btnClose.setIconAlign(IconAlign.RIGHT);
		btnClose.setToolTip("Cancel filter");
		btnClose.addSelectHandler(new SelectHandler() {

			public void onSelect(SelectEvent event) {
				Log.debug("Pressed Close");
				close();
			}
		});
		flowButton.add(btnApply, new BoxLayoutData(new Margins(2, 4, 2, 4)));
		flowButton.add(btnClose, new BoxLayoutData(new Margins(2, 4, 2, 4)));

		basicLayout.add(properties, new VerticalLayoutData(1, -1,
				new Margins(1)));
		basicLayout.add(conditionsField, new VerticalLayoutData(1, -1,
				new Margins(1)));
		basicLayout.add(flowButton, new VerticalLayoutData(1, 36, new Margins(
				5, 2, 5, 2)));

		add(basicLayout);

	}

	protected void setSelectedColumn() {
		if (column == null) {

		} else {
			ColumnTypeCode typeCode = ColumnTypeCode
					.getColumnTypeCodeFromId(column.getTypeCode());
			HTML errorMessage;
			if (typeCode == null) {
				errorMessage = new HTML(
						"This column has column data type null!");
				UtilsGXT3.alert("Error",
						"This column has column data type null!!");
				return;
			}

			switch (typeCode) {
			case ANNOTATION:
			case ATTRIBUTE:
			case CODE:
			case CODEDESCRIPTION:
			case CODENAME:
			case MEASURE:
				comboCols.setValue(column);
				break;
			case DIMENSION:
			case TIMEDIMENSION:
				errorMessage = new HTML(
						"This type of column is not supported for now!");
				add(errorMessage);
				UtilsGXT3.alert("Error",
						"This type of column is not supported for now!");
				break;
			default:
				errorMessage = new HTML(
						"This type of column is not supported for now!");
				add(errorMessage);
				UtilsGXT3.alert("Error",
						"This type of column is not supported for now!");
				break;

			}
		}
	}

	protected SelectionHandler<ColumnData> comboColsSelection() {
		SelectionHandler<ColumnData> selectionHandler = new SelectionHandler<ColumnData>() {

			@Override
			public void onSelection(SelectionEvent<ColumnData> event) {
				if (event.getSelectedItem() != null) {
					ColumnData col = event.getSelectedItem();
					Log.debug("Col selected:" + col.toString());
					column = col;
					btnApply.enable();
					conditionWidget.update(col);
				} else {
					column = null;
					btnApply.disable();
					conditionWidget.update(null);
				}
			}
		};

		return selectionHandler;
	}

	protected void createOnMultiColumnFilter() {
		// Important: fixed rendering of widgets
		forceLayoutOnResize = true;

		setBodyBorder(false);
		setHeaderVisible(false);

		VerticalLayoutContainer basicLayout = new VerticalLayoutContainer();
		basicLayout.setAdjustForScroll(true);
		// basicLayout.setScrollMode(ScrollMode.AUTO); Set In GXT 3.0.1

		FieldSet properties = null;
		VerticalLayoutContainer propertiesLayout;
		TextField columnName;

		HBoxLayoutContainer flowButton = new HBoxLayoutContainer();
		flowButton.setHBoxLayoutAlign(HBoxLayoutAlign.MIDDLE);
		flowButton.setPack(BoxLayoutPack.CENTER);

		properties = new FieldSet();
		properties.setHeadingText("Properties");
		properties.setCollapsible(false);

		propertiesLayout = new VerticalLayoutContainer();
		properties.add(propertiesLayout);

		columnName = new TextField();
		columnName.setToolTip("Column");
		columnName.setReadOnly(true);
		columnName.setValue(column.getLabel());
		propertiesLayout.add(new FieldLabel(columnName, "Column"),
				new VerticalLayoutData(1, -1));

		conditionsField = new FieldSet();
		conditionsField.setHeadingText("Conditions");
		conditionsField.setCollapsible(false);

		conditionWidget = new ConditionWidget(column, "612px", "110px");
		Log.debug("ConditionWidget" + conditionWidget);
		conditionsField.add(conditionWidget);

		btnApply = new TextButton("Add");
		btnApply.setIcon(ExpressionResources.INSTANCE.applyFilter());
		btnApply.setIconAlign(IconAlign.RIGHT);
		btnApply.setToolTip("Add Filter");
		btnApply.addSelectHandler(new SelectHandler() {

			public void onSelect(SelectEvent event) {
				Log.debug("Pressed Add Filter");
				applySeleceted();

			}
		});

		flowButton.add(btnApply, new BoxLayoutData(new Margins(2, 4, 2, 4)));

		basicLayout.add(properties, new VerticalLayoutData(1, -1,
				new Margins(1)));
		basicLayout.add(conditionsField, new VerticalLayoutData(1, -1,
				new Margins(1)));
		basicLayout.add(flowButton, new VerticalLayoutData(1, 36, new Margins(
				5, 2, 5, 2)));
		add(basicLayout);

	}

	protected void applySeleceted() {
		Log.debug("Apply: " + column);

		C_Expression exp;
		C_ExpressionContainer condContainer = null;
		ExpressionWrapper exWrapper = null;

		switch (type) {
		case Template:
			try {
				exp = conditionWidget.getExpression();
			} catch (ConditionTypeMapException e) {
				Log.debug(e.getLocalizedMessage());
				UtilsGXT3.alert("Attention", e.getLocalizedMessage());
				return;
			}
			condContainer = new C_ExpressionContainer();
			condContainer.setId(C_ExpressionContainer.Contains.C_Expression);
			condContainer.setExp(exp);
			exWrapper = new ExpressionWrapper(column.getTrId(), column,
					condContainer);

			ExpressionWrapperNotification expressionWrapperNotification = new ExpressionWrapperNotification(
					exWrapper);
			Log.debug("Notification: " + expressionWrapperNotification);
			parentTemplateDialog.onExpression(expressionWrapperNotification);
			break;
		case RuleOnColumn:
			if (!conditionWidget.isEnabled()) {
				UtilsGXT3.alert("Attention", "Fill all field!");
				return;
			}
			try {
				exp = conditionWidget.getExpression();
			} catch (ConditionTypeMapException e) {
				Log.debug(e.getLocalizedMessage());
				UtilsGXT3.alert("Attention", e.getLocalizedMessage());
				return;
			}
			RuleDescriptionData ruleDescriptionData = retrieveRuleDescriptionData(exp);
			if (ruleDescriptionData != null) {
				parentRuleOnColumnCreateDialog.addRule(ruleDescriptionData);
			}
			break;
		case ColumnFilter:
			try {
				exp = conditionWidget.getExpression();
			} catch (ConditionTypeMapException e) {
				Log.debug(e.getLocalizedMessage());
				UtilsGXT3.alert("Attention", e.getLocalizedMessage());
				return;
			}
			parentFilterDialog.applyFilter(column, exp);
			break;
		case RowDeleteByExpression:
			try {
				exp = conditionWidget.getExpression();
			} catch (ConditionTypeMapException e) {
				Log.debug(e.getLocalizedMessage());
				UtilsGXT3.alert("Attention", e.getLocalizedMessage());
				return;
			}
			C_Expression notExp = new C_Not(exp);
			parentRowsDeleteByExpressionDialog.deleteRowsByExpression(column,
					notExp);

			break;

		default:
			break;
		}

	}

	protected void close() {
		switch (type) {
		case Template:
			parentTemplateDialog.close();
			break;
		case RuleOnColumn:
			parentRuleOnColumnCreateDialog.close();
			break;
		case ColumnFilter:
			parentFilterDialog.close();
			break;
		case RowDeleteByExpression:
			parentRowsDeleteByExpressionDialog.close();
			break;
		default:
			break;
		}

	}

}
