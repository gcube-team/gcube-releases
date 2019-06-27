package org.gcube.portlets.user.td.expressionwidget.client.expression;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Stack;

import org.gcube.portlets.user.td.expressionwidget.client.custom.IconButton;
import org.gcube.portlets.user.td.expressionwidget.client.multicolumn.ArgType;
import org.gcube.portlets.user.td.expressionwidget.client.multicolumn.ArgTypePropertiesCombo;
import org.gcube.portlets.user.td.expressionwidget.client.multicolumn.DepthOfExpressionElement;
import org.gcube.portlets.user.td.expressionwidget.client.multicolumn.DepthOfExpressionElementPropertiesCombo;
import org.gcube.portlets.user.td.expressionwidget.client.multicolumn.DepthOfExpressionStore;
import org.gcube.portlets.user.td.expressionwidget.client.multicolumn.DepthOfExpressionType;
import org.gcube.portlets.user.td.expressionwidget.client.multicolumn.LogicalDepth;
import org.gcube.portlets.user.td.expressionwidget.client.operation.Operation;
import org.gcube.portlets.user.td.expressionwidget.client.operation.OperationProperties;
import org.gcube.portlets.user.td.expressionwidget.client.operation.OperationsStore;
import org.gcube.portlets.user.td.expressionwidget.client.properties.ColumnDataPropertiesCombo;
import org.gcube.portlets.user.td.expressionwidget.client.resources.ExpressionResources;
import org.gcube.portlets.user.td.expressionwidget.client.threshold.Threshold;
import org.gcube.portlets.user.td.expressionwidget.client.threshold.ThresholdProperties;
import org.gcube.portlets.user.td.expressionwidget.client.threshold.ThresholdStore;
import org.gcube.portlets.user.td.expressionwidget.shared.condition.ConditionOnMultiColumnTypeMap;
import org.gcube.portlets.user.td.expressionwidget.shared.exception.ConditionTypeMapException;
import org.gcube.portlets.user.td.monitorwidget.client.utils.UtilsGXT3;
import org.gcube.portlets.user.td.widgetcommonevent.shared.expression.C_Expression;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnData;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnDataType;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.cell.core.client.form.ComboBoxCell.TriggerAction;
import com.sencha.gxt.core.client.dom.ScrollSupport.ScrollMode;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.widget.core.client.container.BoxLayoutContainer.BoxLayoutData;
import com.sencha.gxt.widget.core.client.container.HBoxLayoutContainer;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.event.BeforeShowEvent;
import com.sencha.gxt.widget.core.client.form.ComboBox;
import com.sencha.gxt.widget.core.client.form.DateField;
import com.sencha.gxt.widget.core.client.form.TextField;

/**
 * 
 * @author Giancarlo Panichi
 *
 * 
 */
public class ConditionOnMultiColumnWidget extends SimpleContainer {

	private static final String SIGN = "ConditionOnMultiColumns";
	private static final String HEIGHT = "268px";
	private static final String WIDTH = "890px";
	private static final String COMBO_DEPTH_WIDTH = "64px";
	private static final String COMBO_FIRST_ELEMENT_COLUMN_WIDTH = "150px";
	private static final String COMBO_OPERATOR_WIDTH = "130px";
	private static final String COMBO_FIRST_ARG_TYPE_WIDTH = "80px";
	private static final String COMBO_FIRST_ARG_COLUMN_WIDTH = "150px";
	private static final String COMBO_SECOND_ARG_TYPE_WIDTH = "80px";
	private static final String COMBO_SECOND_ARG_COLUMN_WIDTH = "150px";
	private static final String COMBO_THRESHOLD_WIDTH = "50px";

	private String itemIdComboDepth;
	private String itemIdFirstElementColumn;

	private String itemIdComboOperation;

	private String itemIdFirstArgType;
	private String itemIdFirstArgColumn;
	private String itemIdFirstArgValue;
	private String itemIdFirstArgDate;

	private String itemIdSecondArgType;
	private String itemIdSecondArgColumn;
	private String itemIdSecondArgValue;
	private String itemIdSecondArgDate;

	private String itemIdComboThreshold;

	private String itemIdBtnAdd;
	private String itemIdBtnDel;
	private VerticalLayoutContainer vert;
	private ArrayList<ColumnData> columns;

	// private String readableExpression;

	// private ConditionOnMultiColumnWidget thisCont;

	public ConditionOnMultiColumnWidget(ArrayList<ColumnData> columns) {
		super();
		init(WIDTH, HEIGHT);
		create(columns);
	}

	public ConditionOnMultiColumnWidget(ArrayList<ColumnData> columns,
			String width, String height) {
		super();
		init(width, height);
		create(columns);
	}

	public void update(ArrayList<ColumnData> newColumns) {
		vert.clear();
		this.columns = newColumns;
		setup();
	}
	
	public void resetCondition(){
		vert.clear();
		setup();
	}

	protected void init(String width, String height) {
		setWidth(width);
		setHeight(height);
		setBorders(true);
		forceLayoutOnResize = true;
		// thisCont = this;

	}

	protected void create(ArrayList<ColumnData> columns) {
		this.columns = columns;

		itemIdComboDepth = "ComboDepth" + SIGN;
		itemIdFirstElementColumn = "FirstElementColumn" + SIGN;
		itemIdComboOperation = "ComboOperation" + SIGN;

		itemIdFirstArgType = "FirstArgType" + SIGN;
		itemIdFirstArgColumn = "FirstArgColumn" + SIGN;
		itemIdFirstArgValue = "FirstArgValue" + SIGN;
		itemIdFirstArgDate = "FirstArgDate" + SIGN;

		itemIdSecondArgType = "SecondArgType" + SIGN;

		itemIdSecondArgColumn = "SecondArgColumn" + SIGN;
		itemIdSecondArgValue = "SecondArgValue" + SIGN;
		itemIdSecondArgDate = "SecondArgDate" + SIGN;

		itemIdComboThreshold = "ComboThreshold" + SIGN;
		itemIdBtnAdd = "BtnAdd" + SIGN;
		itemIdBtnDel = "BtnDel" + SIGN;

		vert = new VerticalLayoutContainer();
		vert.setScrollMode(ScrollMode.AUTO);// Set In GXT 3.0.1
		vert.setAdjustForScroll(true);

		setup();

		add(vert);

	}

	protected void setup() {
		final HBoxLayoutContainer horiz = new HBoxLayoutContainer();

		// Combo Depth
		DepthOfExpressionElementPropertiesCombo propsDepth = GWT
				.create(DepthOfExpressionElementPropertiesCombo.class);
		Log.debug("Props: " + propsDepth);
		final ListStore<DepthOfExpressionElement> storeDepth = new ListStore<DepthOfExpressionElement>(
				propsDepth.id());
		storeDepth.addAll(DepthOfExpressionStore.getDepthFirstRow());
		Log.debug("StoreDepth: " + storeDepth);

		Log.debug("StoreDepth created");
		final ComboBox<DepthOfExpressionElement> comboDepth = new ComboBox<DepthOfExpressionElement>(
				storeDepth, propsDepth.label());

		Log.debug("Combo Depth created");

		comboDepth.setEmptyText("");
		comboDepth.setItemId(itemIdComboDepth);
		comboDepth.setWidth(COMBO_DEPTH_WIDTH);
		comboDepth.setEditable(false);
		comboDepth.setAllowBlank(true);

		comboDepth.setTriggerAction(TriggerAction.ALL);

		// Combo FirstElementColumn
		ColumnDataPropertiesCombo propsFirstElementColumn = GWT
				.create(ColumnDataPropertiesCombo.class);
		Log.debug("Props: " + propsFirstElementColumn);
		final ListStore<ColumnData> storeFirstElementColumn = new ListStore<ColumnData>(
				propsFirstElementColumn.id());
		Log.debug("StoreFirstElementColumn: " + storeFirstElementColumn);

		Log.debug("StoreFirstElementColumn created");
		final ComboBox<ColumnData> comboFirstElementColumn = new ComboBox<ColumnData>(
				storeFirstElementColumn, propsFirstElementColumn.label());

		Log.debug("Combo FirstElementColumn created");

		comboFirstElementColumn.setEmptyText("Select a column...");
		comboFirstElementColumn.setItemId(itemIdFirstElementColumn);
		comboFirstElementColumn.setWidth(COMBO_FIRST_ELEMENT_COLUMN_WIDTH);
		comboFirstElementColumn.setEditable(false);
		comboFirstElementColumn.setTriggerAction(TriggerAction.ALL);

		// Combo Operator
		OperationProperties props = GWT.create(OperationProperties.class);
		Log.debug("Props: " + props);
		final ListStore<Operation> storeOperator = new ListStore<Operation>(
				props.id());
		Log.debug("Store: " + storeOperator);

		Log.debug("Store Operator created");
		final ComboBox<Operation> comboOperator = new ComboBox<Operation>(
				storeOperator, props.label());

		Log.debug("Combo Operator created");

		comboOperator.setEmptyText("Select a condition...");
		comboOperator.setItemId(itemIdComboOperation);
		comboOperator.setWidth(COMBO_OPERATOR_WIDTH);
		comboOperator.setEditable(false);
		comboOperator.setTriggerAction(TriggerAction.ALL);

		// Combo FirstArgType
		ArgTypePropertiesCombo propsFirstArgType = GWT
				.create(ArgTypePropertiesCombo.class);
		Log.debug("Props: " + propsFirstArgType);
		final ListStore<ArgType> storeFirstArgType = new ListStore<ArgType>(
				propsFirstArgType.id());
		storeFirstArgType.addAll(ArgType.asList());
		Log.debug("StoreFirstArgType: " + storeFirstArgType);

		Log.debug("StoreFirstArgType created");
		final ComboBox<ArgType> comboFirstArgType = new ComboBox<ArgType>(
				storeFirstArgType, propsFirstArgType.label());

		Log.debug("Combo FirstArgType created");

		comboFirstArgType.setEmptyText("Type...");
		comboFirstArgType.setItemId(itemIdFirstArgType);
		comboFirstArgType.setWidth(COMBO_FIRST_ARG_TYPE_WIDTH);
		comboFirstArgType.setEditable(false);
		comboFirstArgType.setTriggerAction(TriggerAction.ALL);

		// Combo FirstArgColumn
		ColumnDataPropertiesCombo propsFirstArgColumn = GWT
				.create(ColumnDataPropertiesCombo.class);
		Log.debug("Props: " + propsFirstArgColumn);
		final ListStore<ColumnData> storeFirstArgColumn = new ListStore<ColumnData>(
				propsFirstArgColumn.id());
		Log.debug("StoreFirstArgColumn: " + storeFirstArgColumn);

		Log.debug("StoreFirstArgColumn created");
		final ComboBox<ColumnData> comboFirstArgColumn = new ComboBox<ColumnData>(
				storeFirstArgColumn, propsFirstArgColumn.label());

		Log.debug("Combo FirstArgColumn created");

		comboFirstArgColumn.setEmptyText("Select a column...");
		comboFirstArgColumn.setItemId(itemIdFirstArgColumn);
		comboFirstArgColumn.setWidth(COMBO_FIRST_ARG_COLUMN_WIDTH);
		comboFirstArgColumn.setEditable(false);
		comboFirstArgColumn.setTriggerAction(TriggerAction.ALL);

		final TextField firstArgValue = new TextField();
		firstArgValue.setItemId(itemIdFirstArgValue);

		final DateField firstArgDate = new DateField();
		firstArgDate.setItemId(itemIdFirstArgDate);

		// And
		final HTML andText = new HTML(
				"<div style='vertical-align:middle; margin-left:2px;margin-right:2px;margin-top:4px;'>and</div>");

		// Combo SecondArgType
		ArgTypePropertiesCombo propsSecondArgType = GWT
				.create(ArgTypePropertiesCombo.class);
		Log.debug("Props: " + propsSecondArgType);
		final ListStore<ArgType> storeSecondArgType = new ListStore<ArgType>(
				propsSecondArgType.id());
		storeSecondArgType.addAll(ArgType.asList());
		Log.debug("StoreSecondArgType: " + storeSecondArgType);

		Log.debug("StoreSecondArgType created");
		final ComboBox<ArgType> comboSecondArgType = new ComboBox<ArgType>(
				storeSecondArgType, propsSecondArgType.label());

		Log.debug("Combo SecondArgType created");

		comboSecondArgType.setEmptyText("Type...");
		comboSecondArgType.setItemId(itemIdSecondArgType);
		comboSecondArgType.setWidth(COMBO_SECOND_ARG_TYPE_WIDTH);
		comboSecondArgType.setEditable(false);
		comboSecondArgType.setTriggerAction(TriggerAction.ALL);

		// Combo SecondArgColumn
		ColumnDataPropertiesCombo propsSecondArgColumn = GWT
				.create(ColumnDataPropertiesCombo.class);
		Log.debug("Props: " + propsSecondArgColumn);
		final ListStore<ColumnData> storeSecondArgColumn = new ListStore<ColumnData>(
				propsSecondArgColumn.id());
		Log.debug("StoreSecondArgColumn: " + storeSecondArgColumn);

		Log.debug("StoreSecondArgColumn created");
		final ComboBox<ColumnData> comboSecondArgColumn = new ComboBox<ColumnData>(
				storeSecondArgColumn, propsSecondArgColumn.label());

		Log.debug("Combo SecondArgColumn created");

		comboSecondArgColumn.setEmptyText("Select a column...");
		comboSecondArgColumn.setItemId(itemIdSecondArgColumn);
		comboSecondArgColumn.setWidth(COMBO_SECOND_ARG_COLUMN_WIDTH);
		comboSecondArgColumn.setEditable(false);
		comboSecondArgColumn.setTriggerAction(TriggerAction.ALL);

		final TextField secondArgValue = new TextField();
		secondArgValue.setItemId(itemIdSecondArgValue);

		final DateField secondArgDate = new DateField();
		secondArgDate.setItemId(itemIdSecondArgDate);

		// Combo Similarity and Levenshtein threshold
		ThresholdProperties propsThreshold = GWT
				.create(ThresholdProperties.class);
		Log.debug("Props: " + propsThreshold);
		final ListStore<Threshold> storeThreshold = new ListStore<Threshold>(
				propsThreshold.id());
		Log.debug("StoreThreshold: " + storeThreshold);
		storeThreshold.addAll(ThresholdStore.thresholdsLevenshtein);

		Log.debug("StoreThreshold created");
		final ComboBox<Threshold> comboThreshold = new ComboBox<Threshold>(
				storeThreshold, propsThreshold.label());

		Log.debug("Combo Threshold created");

		comboThreshold.setEmptyText("Select a threshold...");
		comboThreshold.setItemId(itemIdComboThreshold);
		comboThreshold.setWidth(COMBO_THRESHOLD_WIDTH);
		comboThreshold.setEditable(false);
		comboThreshold.setTriggerAction(TriggerAction.ALL);

		final HTML thresholdText = new HTML(
				"<div style='vertical-align:middle; margin-left:2px;margin-right:2px;margin-top:4px;'>Threshold:</div>");

		// Button
		final IconButton btnAdd = new IconButton();
		btnAdd.setItemId(itemIdBtnAdd);
		btnAdd.setIcon(ExpressionResources.INSTANCE.add());
		btnAdd.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				Log.debug("Clicked btnAdd");
				DepthOfExpressionElement depthElement = comboDepth
						.getCurrentValue();

				if (depthElement == null) {
					Log.debug("Depth Selected: null");
					Log.debug("No Add possible, if And or Or are not set");
					UtilsGXT3.alert("Attention",
							"Select And or Or if you want add a condition!");
					return;
				}

				DepthOfExpressionType depth = depthElement.getType();

				if (depth == null) {
					Log.debug("Depth Selected: null");
					Log.debug("No Add possible, if And or Or are not set");
					UtilsGXT3.alert("Attention",
							"Select And or Or if you want add a condition!");
				} else {
					Log.debug("Depth Selected: " + depth.getLabel());
					switch (depth) {
					case BOTTOM:
					case COMMA:
					case ENDAND:
					case ENDOR:
						Log.debug("No Add possible, if And or Or are not set");
						UtilsGXT3
								.alert("Attention",
										"Select And or Or if you want add a condition!");
						break;
					case STARTAND:
					case STARTOR:
						addCondition();
						vert.forceLayout();
						break;
					default:
						Log.debug("No Add possible, if And or Or are not set");
						UtilsGXT3
								.alert("Attention",
										"Select And or Or if you want add a condition!");
						break;
					}

				}

			}
		});

		final IconButton btnDel = new IconButton();
		btnDel.setItemId(itemIdBtnDel);
		btnDel.setIcon(ExpressionResources.INSTANCE.delete());
		btnDel.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				Log.debug("Clicked btnDel");
				vert.remove(horiz);
				if (vert.getWidgetCount() == 0) {
					setup();
				} else {
					if (vert.getWidgetCount() == 1) {
					} else {
						// TODO

					}
				}
				vert.forceLayout();

			}
		});

		// Handlers
		comboDepth
				.addSelectionHandler(new SelectionHandler<DepthOfExpressionElement>() {

					@Override
					public void onSelection(
							SelectionEvent<DepthOfExpressionElement> event) {
						if (event.getSelectedItem() != null) {
							@SuppressWarnings("unchecked")
							ComboBox<DepthOfExpressionElement> source = (ComboBox<DepthOfExpressionElement>) event
									.getSource();

							DepthOfExpressionElement depthElement = event
									.getSelectedItem();

							DepthOfExpressionType depth = depthElement
									.getType();

							if (depth == null) {
								Log.debug("Depth Selected: null");
							} else {
								Log.debug("Depth Selected: " + depth.getLabel());
								switch (depth) {
								case BOTTOM:
									storeFirstElementColumn.clear();
									storeFirstElementColumn.addAll(columns);
									storeFirstElementColumn.commitChanges();
									comboFirstElementColumn.clear();
									comboFirstElementColumn.reset();

									comboDepth.setVisible(true);
									comboFirstElementColumn.setVisible(true);
									comboOperator.setVisible(false);
									comboFirstArgType.setVisible(false);
									comboFirstArgColumn.setVisible(false);
									firstArgValue.setVisible(false);
									firstArgDate.setVisible(false);
									andText.setVisible(false);
									comboSecondArgType.setVisible(false);
									comboSecondArgColumn.setVisible(false);
									secondArgValue.setVisible(false);
									secondArgDate.setVisible(false);
									thresholdText.setVisible(false);
									comboThreshold.setVisible(false);
									btnAdd.setVisible(false);
									btnDel.setVisible(false);
									removeCondition(source);
									break;
								case COMMA:
								case STARTAND:
								case STARTOR:
									storeFirstElementColumn.clear();
									storeFirstElementColumn.addAll(columns);
									storeFirstElementColumn.commitChanges();
									comboFirstElementColumn.clear();
									comboFirstElementColumn.reset();

									comboDepth.setVisible(true);
									comboFirstElementColumn.setVisible(true);
									comboOperator.setVisible(false);
									comboFirstArgType.setVisible(false);
									comboFirstArgColumn.setVisible(false);
									firstArgValue.setVisible(false);
									firstArgDate.setVisible(false);
									andText.setVisible(false);
									comboSecondArgType.setVisible(false);
									comboSecondArgColumn.setVisible(false);
									secondArgValue.setVisible(false);
									secondArgDate.setVisible(false);
									thresholdText.setVisible(false);
									comboThreshold.setVisible(false);
									btnAdd.setVisible(true);
									btnDel.setVisible(false);
									if (!existCondition(source)) {
										addCondition();
									}
									break;
								case ENDAND:
								case ENDOR:
									comboDepth.setVisible(true);
									comboFirstElementColumn.setVisible(false);
									comboOperator.setVisible(false);
									comboFirstArgType.setVisible(false);
									comboFirstArgColumn.setVisible(false);
									firstArgValue.setVisible(false);
									firstArgDate.setVisible(false);
									andText.setVisible(false);
									comboSecondArgType.setVisible(false);
									comboSecondArgColumn.setVisible(false);
									secondArgValue.setVisible(false);
									secondArgDate.setVisible(false);
									thresholdText.setVisible(false);
									comboThreshold.setVisible(false);
									btnAdd.setVisible(true);
									btnDel.setVisible(false);
									removeCondition(source);
									break;
								default:
									break;

								}

								vert.forceLayout();
							}

						}

					}
				});

		comboFirstElementColumn
				.addSelectionHandler(new SelectionHandler<ColumnData>() {

					@Override
					public void onSelection(SelectionEvent<ColumnData> event) {
						if (event.getSelectedItem() != null) {
							ColumnData column = event.getSelectedItem();

							if (column == null) {
								Log.debug("ComboFirstElement selected: null");
								storeOperator.clear();
								storeOperator.commitChanges();
								comboOperator.clear();
								comboOperator.reset();

								comboOperator.setVisible(false);
								comboFirstArgType.setVisible(false);
								comboFirstArgColumn.setVisible(false);
								firstArgValue.setVisible(false);
								firstArgDate.setVisible(false);
								andText.setVisible(false);
								comboSecondArgType.setVisible(false);
								comboSecondArgColumn.setVisible(false);
								secondArgValue.setVisible(false);
								secondArgDate.setVisible(false);
								thresholdText.setVisible(false);
								comboThreshold.setVisible(false);
							

							} else {
								Log.debug("ComboFirstElement selected: "
										+ column.getLabel());
								ColumnDataType columnDataType = ColumnDataType
										.getColumnDataTypeFromId(column
												.getDataTypeName());
								if (columnDataType == null) {
									storeOperator.clear();
									storeOperator.commitChanges();
									comboOperator.clear();
									comboOperator.reset();

									comboOperator.setVisible(false);
									comboFirstArgType.setVisible(false);
									comboFirstArgColumn.setVisible(false);
									firstArgValue.setVisible(false);
									firstArgDate.setVisible(false);
									andText.setVisible(false);
									comboSecondArgType.setVisible(false);
									comboSecondArgColumn.setVisible(false);
									secondArgValue.setVisible(false);
									secondArgDate.setVisible(false);
									thresholdText.setVisible(false);
									comboThreshold.setVisible(false);
									
								} else {
									OperationsStore operationStore = new OperationsStore();
									storeOperator.clear();
									storeOperator.addAll(operationStore
											.getAll(column));
									storeOperator.commitChanges();
									comboOperator.clear();
									comboOperator.reset();

									comboOperator.setVisible(true);
									comboFirstArgType.setVisible(false);
									comboFirstArgColumn.setVisible(false);
									firstArgValue.setVisible(false);
									firstArgDate.setVisible(false);
									andText.setVisible(false);
									comboSecondArgType.setVisible(false);
									comboSecondArgColumn.setVisible(false);
									secondArgValue.setVisible(false);
									secondArgDate.setVisible(false);
									thresholdText.setVisible(false);
									comboThreshold.setVisible(false);
									

								}

							}
							vert.forceLayout();
						}
					}
				});

		comboOperator.addSelectionHandler(new SelectionHandler<Operation>() {

			public void onSelection(SelectionEvent<Operation> event) {
				if (event.getSelectedItem() != null) {
					Operation op = event.getSelectedItem();
					if (op == null) {
						Log.debug("Operator selected: null");
						comboFirstArgType.setVisible(false);
						comboFirstArgColumn.setVisible(false);
						firstArgValue.setVisible(false);
						firstArgDate.setVisible(false);
						andText.setVisible(false);
						comboSecondArgType.setVisible(false);
						comboSecondArgColumn.setVisible(false);
						secondArgValue.setVisible(false);
						secondArgDate.setVisible(false);
						thresholdText.setVisible(false);
						comboThreshold.setVisible(false);
						
					} else {
						Log.debug("Operator selected:" + op.toString());
						switch (op.getOperatorType()) {
						case EQUALS:
						case GREATER:
						case GREATER_OR_EQUALS:
						case LESSER:
						case LESSER_OR_EQUALS:
						case NOT_EQUALS:
						case NOT_GREATER:
						case NOT_LESSER:
						case BEGINS_WITH:
						case ENDS_WITH:
						case CONTAINS:
						case NOT_BEGINS_WITH:
						case NOT_ENDS_WITH:
						case NOT_CONTAINS:
							comboFirstArgType.reset();

							comboFirstArgType.setVisible(true);
							comboFirstArgColumn.setVisible(false);
							firstArgValue.setVisible(false);
							firstArgDate.setVisible(false);
							andText.setVisible(false);
							comboSecondArgType.setVisible(false);
							comboSecondArgColumn.setVisible(false);
							secondArgValue.setVisible(false);
							secondArgDate.setVisible(false);
							thresholdText.setVisible(false);
							comboThreshold.setVisible(false);
							
							break;
						case BETWEEN:
						case NOT_BETWEEN:
							comboFirstArgType.reset();
							comboSecondArgType.reset();

							comboFirstArgType.setVisible(true);
							comboFirstArgColumn.setVisible(false);
							firstArgValue.setVisible(false);
							firstArgDate.setVisible(false);
							andText.setVisible(true);
							comboSecondArgType.setVisible(true);
							comboSecondArgColumn.setVisible(false);
							secondArgValue.setVisible(false);
							secondArgDate.setVisible(false);
							thresholdText.setVisible(false);
							comboThreshold.setVisible(false);
							
							break;
						case IN:
						case NOT_IN:	
						case MATCH_REGEX:	
						case NOT_MATCH_REGEX:
						case SOUNDEX:
							comboFirstArgType.reset();
							comboFirstArgType.setValue(ArgType.VALUE, true);
							firstArgValue.reset();
							
							comboFirstArgType.setVisible(false);
							comboFirstArgColumn.setVisible(false);
							firstArgValue.setVisible(true);
							firstArgDate.setVisible(false);
							andText.setVisible(false);
							comboSecondArgType.setVisible(false);
							comboSecondArgColumn.setVisible(false);
							secondArgValue.setVisible(false);
							secondArgDate.setVisible(false);
							thresholdText.setVisible(false);
							comboThreshold.setVisible(false);
							
							break;
						case LEVENSHTEIN:
							comboFirstArgType.reset();
							comboFirstArgType.setValue(ArgType.VALUE, true);
							firstArgValue.reset();
							
							storeThreshold.clear();
							storeThreshold
									.addAll(ThresholdStore.thresholdsLevenshtein);
							storeThreshold.commitChanges();
							comboThreshold.clear();
							comboThreshold.reset();

							comboFirstArgType.setVisible(false);
							comboFirstArgColumn.setVisible(false);
							firstArgValue.setVisible(true);
							firstArgDate.setVisible(false);
							andText.setVisible(false);
							comboSecondArgType.setVisible(false);
							comboSecondArgColumn.setVisible(false);
							secondArgValue.setVisible(false);
							secondArgDate.setVisible(false);
							thresholdText.setVisible(true);
							comboThreshold.setVisible(true);

							comboThreshold.setValue(ThresholdStore
									.defaultThresholdLevenshtein());

							break;
						case SIMILARITY:
							comboFirstArgType.reset();
							comboFirstArgType.setValue(ArgType.VALUE, true);
							firstArgValue.reset();
							
							storeThreshold.clear();
							storeThreshold
									.addAll(ThresholdStore.thresholdsSimilarity);
							storeThreshold.commitChanges();
							comboThreshold.clear();
							comboThreshold.reset();

							comboFirstArgType.setVisible(false);
							comboFirstArgColumn.setVisible(false);
							firstArgValue.setVisible(true);
							firstArgDate.setVisible(false);
							andText.setVisible(false);
							comboSecondArgType.setVisible(false);
							comboSecondArgColumn.setVisible(false);
							secondArgValue.setVisible(false);
							secondArgDate.setVisible(false);
							thresholdText.setVisible(true);
							comboThreshold.setVisible(true);

							comboThreshold.setValue(ThresholdStore
									.defaultThresholdSimilarity());
							break;
						case IS_NULL:
						case IS_NOT_NULL:
							comboFirstArgType.setVisible(false);
							comboFirstArgColumn.setVisible(false);
							firstArgValue.setVisible(false);
							firstArgDate.setVisible(false);
							andText.setVisible(false);
							comboSecondArgType.setVisible(false);
							comboSecondArgColumn.setVisible(false);
							secondArgValue.setVisible(false);
							secondArgDate.setVisible(false);
							thresholdText.setVisible(false);
							comboThreshold.setVisible(false);
							break;
						default:
							comboFirstArgType.setVisible(false);
							comboFirstArgColumn.setVisible(false);
							firstArgValue.setVisible(false);
							firstArgDate.setVisible(false);
							andText.setVisible(false);
							comboSecondArgType.setVisible(false);
							comboSecondArgColumn.setVisible(false);
							secondArgValue.setVisible(false);
							secondArgDate.setVisible(false);
							thresholdText.setVisible(false);
							comboThreshold.setVisible(false);
							break;
						}
					}
					vert.forceLayout();
				}
			}

		});

		comboFirstArgType.addSelectionHandler(new SelectionHandler<ArgType>() {

			@Override
			public void onSelection(SelectionEvent<ArgType> event) {
				if (event.getSelectedItem() != null) {
					ArgType argType = event.getSelectedItem();
					if (argType == null) {
						Log.debug("Operator selected: null");
						comboFirstArgColumn.setVisible(false);
						firstArgValue.setVisible(false);
						firstArgDate.setVisible(false);
					} else {
						Log.debug("Operator selected:" + argType.getLabel());
						ColumnData firstElementColumn = comboFirstElementColumn
								.getCurrentValue();
						if (firstElementColumn == null) {
							Log.debug("FirstElementColumn selected: null");
							comboFirstArgColumn.setVisible(false);
							firstArgValue.setVisible(false);
							firstArgDate.setVisible(false);
						} else {
							Log.debug("FirstElementColumn selected: "
									+ firstElementColumn.getLabel());
							ColumnDataType columnDataType = ColumnDataType
									.getColumnDataTypeFromId(firstElementColumn
											.getDataTypeName());
							switch (argType) {
							case COLUMN:
								storeFirstArgColumn.clear();
								storeFirstArgColumn
										.addAll(getSanitizedColumns(firstElementColumn));
								storeFirstArgColumn.commitChanges();
								comboFirstArgColumn.clear();
								comboFirstArgColumn.reset();

								comboFirstArgColumn.setVisible(true);
								firstArgValue.setVisible(false);
								firstArgDate.setVisible(false);
								break;
							case VALUE:
								comboFirstArgColumn.setVisible(false);
								if (columnDataType
										.compareTo(ColumnDataType.Date) == 0) {
									firstArgDate.reset();
									firstArgValue.setVisible(false);
									firstArgDate.setVisible(true);	
								} else {
									firstArgValue.reset();
									firstArgValue.setVisible(true);
									firstArgDate.setVisible(false);
								}
								break;
							default:
								comboFirstArgColumn.setVisible(false);
								firstArgValue.setVisible(false);
								firstArgDate.setVisible(false);
								break;
							}
						}
					}
					vert.forceLayout();
				}

			}
		});

		comboSecondArgType.addSelectionHandler(new SelectionHandler<ArgType>() {

			@Override
			public void onSelection(SelectionEvent<ArgType> event) {
				if (event.getSelectedItem() != null) {
					ArgType argType = event.getSelectedItem();
					if (argType == null) {
						Log.debug("Operator selected: null");
						comboSecondArgColumn.setVisible(false);
						secondArgValue.setVisible(false);
						secondArgDate.setVisible(false);
					} else {
						Log.debug("Operator selected:" + argType.getLabel());
						ColumnData firstElementColumn = comboFirstElementColumn
								.getCurrentValue();
						if (firstElementColumn == null) {
							Log.debug("FirstElementColumn selected: null");
							comboSecondArgColumn.setVisible(false);
							secondArgValue.setVisible(false);
							secondArgDate.setVisible(false);
						} else {
							Log.debug("FirstElementColumn selected: "
									+ firstElementColumn.getLabel());
							ColumnDataType columnDataType = ColumnDataType
									.getColumnDataTypeFromId(firstElementColumn
											.getDataTypeName());

							switch (argType) {
							case COLUMN:
								storeSecondArgColumn.clear();
								storeSecondArgColumn
										.addAll(getSanitizedColumns(firstElementColumn));
								storeSecondArgColumn.commitChanges();
								comboSecondArgColumn.clear();
								comboSecondArgColumn.reset();

								comboSecondArgColumn.setVisible(true);
								secondArgValue.setVisible(false);
								secondArgDate.setVisible(false);
								break;
							case VALUE:
								comboSecondArgColumn.setVisible(false);
								if (columnDataType
										.compareTo(ColumnDataType.Date) == 0) {
									secondArgDate.reset();
									secondArgValue.setVisible(false);
									secondArgDate.setVisible(true);
								} else {
									secondArgValue.reset();
									secondArgValue.setVisible(true);
									secondArgDate.setVisible(false);
								}
								break;
							default:
								comboSecondArgColumn.setVisible(false);
								secondArgValue.setVisible(false);
								secondArgDate.setVisible(false);
								break;
							}
						}
					}
					vert.forceLayout();
				}

			}
		});
		
	
		
		
		comboDepth.setVisible(true);
		comboDepth.setValue(DepthOfExpressionStore.bottomFirstElement, true);
		
		storeFirstElementColumn.clear();
		storeFirstElementColumn.addAll(columns);
		storeFirstElementColumn.commitChanges();
		comboFirstElementColumn.clear();
		comboFirstElementColumn.reset();
		
		comboFirstElementColumn.setVisible(true);
		comboOperator.setVisible(false);
		comboFirstArgType.setVisible(false);
		comboFirstArgColumn.setVisible(false);
		firstArgValue.setVisible(false);
		firstArgDate.setVisible(false);
		andText.setVisible(false);
		comboSecondArgType.setVisible(false);
		comboSecondArgColumn.setVisible(false);
		secondArgValue.setVisible(false);
		secondArgDate.setVisible(false);
		thresholdText.setVisible(false);
		comboThreshold.setVisible(false);
		btnAdd.setVisible(false);
		btnDel.setVisible(false);

		horiz.add(comboDepth, new BoxLayoutData(new Margins(0)));
		horiz.add(comboFirstElementColumn, new BoxLayoutData(new Margins(0)));
		horiz.add(comboOperator, new BoxLayoutData(new Margins(0)));
		horiz.add(comboFirstArgType, new BoxLayoutData(new Margins(0)));
		horiz.add(comboFirstArgColumn, new BoxLayoutData(new Margins(0)));
		horiz.add(firstArgValue, new BoxLayoutData(new Margins(0)));
		horiz.add(firstArgDate, new BoxLayoutData(new Margins(0)));
		horiz.add(andText, new BoxLayoutData(new Margins(0)));
		horiz.add(comboSecondArgType, new BoxLayoutData(new Margins(0)));
		horiz.add(comboSecondArgColumn, new BoxLayoutData(new Margins(0)));
		horiz.add(secondArgValue, new BoxLayoutData(new Margins(0)));
		horiz.add(secondArgDate, new BoxLayoutData(new Margins(0)));
		horiz.add(thresholdText, new BoxLayoutData(new Margins(0)));
		horiz.add(comboThreshold, new BoxLayoutData(new Margins(0)));
		horiz.add(btnAdd, new BoxLayoutData(new Margins(2, 0, 2, 0)));
		horiz.add(btnDel, new BoxLayoutData(new Margins(2, 0, 2, 0)));

		vert.add(horiz, new VerticalLayoutData(1, -1, new Margins(1)));

		addBeforeShowHandler(new BeforeShowEvent.BeforeShowHandler() {

			@Override
			public void onBeforeShow(BeforeShowEvent event) {
				forceLayout();

			}
		});

	}

	protected void addCondition() {
		final HBoxLayoutContainer horiz = new HBoxLayoutContainer();

		// Combo Depth
		DepthOfExpressionElementPropertiesCombo propsDepth = GWT
				.create(DepthOfExpressionElementPropertiesCombo.class);
		Log.debug("Props: " + propsDepth);
		final ListStore<DepthOfExpressionElement> storeDepth = new ListStore<DepthOfExpressionElement>(
				propsDepth.id());
		storeDepth.addAll(DepthOfExpressionStore.getDepthOtherRows());
		Log.debug("StoreDepth: " + storeDepth);

		Log.debug("StoreDepth created");
		final ComboBox<DepthOfExpressionElement> comboDepth = new ComboBox<DepthOfExpressionElement>(
				storeDepth, propsDepth.label());

		Log.debug("Combo Depth created");

		comboDepth.setEmptyText("Select a type...");
		comboDepth.setItemId(itemIdComboDepth);
		comboDepth.setWidth(COMBO_DEPTH_WIDTH);
		comboDepth.setEditable(false);
		comboDepth.setTriggerAction(TriggerAction.ALL);

		// Combo FirstElementColumn
		ColumnDataPropertiesCombo propsFirstElementColumn = GWT
				.create(ColumnDataPropertiesCombo.class);
		Log.debug("Props: " + propsFirstElementColumn);
		final ListStore<ColumnData> storeFirstElementColumn = new ListStore<ColumnData>(
				propsFirstElementColumn.id());
		Log.debug("StoreFirstElementColumn: " + storeFirstElementColumn);

		Log.debug("StoreFirstElementColumn created");
		final ComboBox<ColumnData> comboFirstElementColumn = new ComboBox<ColumnData>(
				storeFirstElementColumn, propsFirstElementColumn.label());

		Log.debug("Combo FirstElementColumn created");

		comboFirstElementColumn.setEmptyText("Select a column...");
		comboFirstElementColumn.setItemId(itemIdFirstElementColumn);
		comboFirstElementColumn.setWidth(COMBO_FIRST_ELEMENT_COLUMN_WIDTH);
		comboFirstElementColumn.setEditable(false);
		comboFirstElementColumn.setTriggerAction(TriggerAction.ALL);

		// Combo Operator
		OperationProperties props = GWT.create(OperationProperties.class);
		Log.debug("Props: " + props);
		final ListStore<Operation> storeOperator = new ListStore<Operation>(
				props.id());
		Log.debug("Store: " + storeOperator);

		Log.debug("Store Operator created");
		final ComboBox<Operation> comboOperator = new ComboBox<Operation>(
				storeOperator, props.label());

		Log.debug("Combo Operator created");

		comboOperator.setEmptyText("Select a condition...");
		comboOperator.setItemId(itemIdComboOperation);
		comboOperator.setWidth(COMBO_OPERATOR_WIDTH);
		comboOperator.setEditable(false);
		comboOperator.setTriggerAction(TriggerAction.ALL);

		// Combo FirstArgType
		ArgTypePropertiesCombo propsFirstArgType = GWT
				.create(ArgTypePropertiesCombo.class);
		Log.debug("Props: " + propsFirstArgType);
		final ListStore<ArgType> storeFirstArgType = new ListStore<ArgType>(
				propsFirstArgType.id());
		storeFirstArgType.addAll(ArgType.asList());
		Log.debug("StoreFirstArgType: " + storeFirstArgType);

		Log.debug("StoreFirstArgType created");
		final ComboBox<ArgType> comboFirstArgType = new ComboBox<ArgType>(
				storeFirstArgType, propsFirstArgType.label());

		Log.debug("Combo FirstArgType created");

		comboFirstArgType.setEmptyText("Type...");
		comboFirstArgType.setItemId(itemIdFirstArgType);
		comboFirstArgType.setWidth(COMBO_FIRST_ARG_TYPE_WIDTH);
		comboFirstArgType.setEditable(false);
		comboFirstArgType.setTriggerAction(TriggerAction.ALL);

		// Combo FirstArgColumn
		ColumnDataPropertiesCombo propsFirstArgColumn = GWT
				.create(ColumnDataPropertiesCombo.class);
		Log.debug("Props: " + propsFirstArgColumn);
		final ListStore<ColumnData> storeFirstArgColumn = new ListStore<ColumnData>(
				propsFirstArgColumn.id());
		Log.debug("StoreFirstArgColumn: " + storeFirstArgColumn);

		Log.debug("StoreFirstArgColumn created");
		final ComboBox<ColumnData> comboFirstArgColumn = new ComboBox<ColumnData>(
				storeFirstArgColumn, propsFirstArgColumn.label());

		Log.debug("Combo FirstArgColumn created");

		comboFirstArgColumn.setEmptyText("Select a column...");
		comboFirstArgColumn.setItemId(itemIdFirstArgColumn);
		comboFirstArgColumn.setWidth(COMBO_FIRST_ARG_COLUMN_WIDTH);
		comboFirstArgColumn.setEditable(false);
		comboFirstArgColumn.setTriggerAction(TriggerAction.ALL);

		final TextField firstArgValue = new TextField();
		firstArgValue.setItemId(itemIdFirstArgValue);

		final DateField firstArgDate = new DateField();
		firstArgDate.setItemId(itemIdFirstArgDate);

		// And
		final HTML andText = new HTML(
				"<div style='vertical-align:middle; margin-left:2px;margin-right:2px;margin-top:4px;'>and</div>");

		// Combo SecondArgType
		ArgTypePropertiesCombo propsSecondArgType = GWT
				.create(ArgTypePropertiesCombo.class);
		Log.debug("Props: " + propsSecondArgType);
		final ListStore<ArgType> storeSecondArgType = new ListStore<ArgType>(
				propsSecondArgType.id());
		storeSecondArgType.addAll(ArgType.asList());
		Log.debug("StoreSecondArgType: " + storeSecondArgType);

		Log.debug("StoreSecondArgType created");
		final ComboBox<ArgType> comboSecondArgType = new ComboBox<ArgType>(
				storeSecondArgType, propsSecondArgType.label());

		Log.debug("Combo SecondArgType created");

		comboSecondArgType.setEmptyText("Type...");
		comboSecondArgType.setItemId(itemIdSecondArgType);
		comboSecondArgType.setWidth(COMBO_SECOND_ARG_TYPE_WIDTH);
		comboSecondArgType.setEditable(false);
		comboSecondArgType.setTriggerAction(TriggerAction.ALL);

		// Combo SecondArgColumn
		ColumnDataPropertiesCombo propsSecondArgColumn = GWT
				.create(ColumnDataPropertiesCombo.class);
		Log.debug("Props: " + propsSecondArgColumn);
		final ListStore<ColumnData> storeSecondArgColumn = new ListStore<ColumnData>(
				propsSecondArgColumn.id());
		Log.debug("StoreSecondArgColumn: " + storeSecondArgColumn);

		Log.debug("StoreSecondArgColumn created");
		final ComboBox<ColumnData> comboSecondArgColumn = new ComboBox<ColumnData>(
				storeSecondArgColumn, propsSecondArgColumn.label());

		Log.debug("Combo SecondArgColumn created");

		comboSecondArgColumn.setEmptyText("Select a column...");
		comboSecondArgColumn.setItemId(itemIdSecondArgColumn);
		comboSecondArgColumn.setWidth(COMBO_SECOND_ARG_COLUMN_WIDTH);
		comboSecondArgColumn.setEditable(false);
		comboSecondArgColumn.setTriggerAction(TriggerAction.ALL);

		final TextField secondArgValue = new TextField();
		secondArgValue.setItemId(itemIdSecondArgValue);

		final DateField secondArgDate = new DateField();
		secondArgDate.setItemId(itemIdSecondArgDate);

		// Combo Similarity and Levenshtein threshold
		ThresholdProperties propsThreshold = GWT
				.create(ThresholdProperties.class);
		Log.debug("Props: " + propsThreshold);
		final ListStore<Threshold> storeThreshold = new ListStore<Threshold>(
				propsThreshold.id());
		Log.debug("StoreThreshold: " + storeThreshold);
		storeThreshold.addAll(ThresholdStore.thresholdsLevenshtein);

		Log.debug("StoreThreshold created");
		final ComboBox<Threshold> comboThreshold = new ComboBox<Threshold>(
				storeThreshold, propsThreshold.label());

		Log.debug("Combo Threshold created");

		comboThreshold.setEmptyText("Select a threshold...");
		comboThreshold.setItemId(itemIdComboThreshold);
		comboThreshold.setWidth(COMBO_THRESHOLD_WIDTH);
		comboThreshold.setEditable(false);
		comboThreshold.setTriggerAction(TriggerAction.ALL);

		final HTML thresholdText = new HTML(
				"<div style='vertical-align:middle; margin-left:2px;margin-right:2px;margin-top:4px;'>Threshold:</div>");

		// Button
		final IconButton btnAdd = new IconButton();
		btnAdd.setItemId(itemIdBtnAdd);
		btnAdd.setIcon(ExpressionResources.INSTANCE.add());
		btnAdd.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				Log.debug("Clicked btnAdd");
				addCondition();
				vert.forceLayout();

			}
		});

		final IconButton btnDel = new IconButton();
		btnDel.setItemId(itemIdBtnDel);
		btnDel.setIcon(ExpressionResources.INSTANCE.delete());
		btnDel.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				Log.debug("Clicked btnDel");
				vert.remove(horiz);
				if (vert.getWidgetCount() == 0) {
					setup();
				} else {
					if (vert.getWidgetCount() == 1) {
					} else {
						// TODO

					}
				}
				vert.forceLayout();

			}
		});

		// Handlers
		comboDepth
				.addSelectionHandler(new SelectionHandler<DepthOfExpressionElement>() {

					@Override
					public void onSelection(
							SelectionEvent<DepthOfExpressionElement> event) {
						if (event.getSelectedItem() != null) {
							@SuppressWarnings("unchecked")
							ComboBox<DepthOfExpressionElement> source = (ComboBox<DepthOfExpressionElement>) event
									.getSource();

							DepthOfExpressionElement depthElement = event
									.getSelectedItem();

							DepthOfExpressionType depth = depthElement
									.getType();

							if (depth == null) {
								Log.debug("Depth Selected: null");
							} else {
								Log.debug("Depth Selected: " + depth.getLabel());
								switch (depth) {
								case BOTTOM:
									storeFirstElementColumn.clear();
									storeFirstElementColumn.addAll(columns);
									storeFirstElementColumn.commitChanges();
									comboFirstElementColumn.clear();
									comboFirstElementColumn.reset();

									comboDepth.setVisible(true);
									comboFirstElementColumn.setVisible(true);
									comboOperator.setVisible(false);
									comboFirstArgType.setVisible(false);
									comboFirstArgColumn.setVisible(false);
									firstArgValue.setVisible(false);
									firstArgDate.setVisible(false);
									andText.setVisible(false);
									comboSecondArgType.setVisible(false);
									comboSecondArgColumn.setVisible(false);
									secondArgValue.setVisible(false);
									secondArgDate.setVisible(false);
									thresholdText.setVisible(false);
									comboThreshold.setVisible(false);
									btnAdd.setVisible(true);
									btnDel.setVisible(true);
									removeCondition(source);
									break;
								case COMMA:
								case STARTAND:
								case STARTOR:
									storeFirstElementColumn.clear();
									storeFirstElementColumn.addAll(columns);
									storeFirstElementColumn.commitChanges();
									comboFirstElementColumn.clear();
									comboFirstElementColumn.reset();

									comboDepth.setVisible(true);
									comboFirstElementColumn.setVisible(true);
									comboOperator.setVisible(false);
									comboFirstArgType.setVisible(false);
									comboFirstArgColumn.setVisible(false);
									firstArgValue.setVisible(false);
									firstArgDate.setVisible(false);
									andText.setVisible(false);
									comboSecondArgType.setVisible(false);
									comboSecondArgColumn.setVisible(false);
									secondArgValue.setVisible(false);
									secondArgDate.setVisible(false);
									thresholdText.setVisible(false);
									comboThreshold.setVisible(false);
									btnAdd.setVisible(true);
									btnDel.setVisible(true);
									if (!existCondition(source)) {
										addCondition();
									}
									break;
								case ENDAND:
								case ENDOR:
									comboDepth.setVisible(true);
									comboFirstElementColumn.setVisible(false);
									comboOperator.setVisible(false);
									comboFirstArgType.setVisible(false);
									comboFirstArgColumn.setVisible(false);
									firstArgValue.setVisible(false);
									firstArgDate.setVisible(false);
									andText.setVisible(false);
									comboSecondArgType.setVisible(false);
									comboSecondArgColumn.setVisible(false);
									secondArgValue.setVisible(false);
									secondArgDate.setVisible(false);
									thresholdText.setVisible(false);
									comboThreshold.setVisible(false);
									btnAdd.setVisible(true);
									btnDel.setVisible(true);
									removeCondition(source);
									break;
								default:
									break;

								}

								vert.forceLayout();
							}

						}

					}
				});

		comboFirstElementColumn
				.addSelectionHandler(new SelectionHandler<ColumnData>() {

					@Override
					public void onSelection(SelectionEvent<ColumnData> event) {
						if (event.getSelectedItem() != null) {
							ColumnData column = event.getSelectedItem();

							if (column == null) {
								Log.debug("ComboFirstElement selected: null");
								storeOperator.clear();
								storeOperator.commitChanges();
								comboOperator.clear();
								comboOperator.reset();

								comboOperator.setVisible(false);
								comboFirstArgType.setVisible(false);
								comboFirstArgColumn.setVisible(false);
								firstArgValue.setVisible(false);
								firstArgDate.setVisible(false);
								andText.setVisible(false);
								comboSecondArgType.setVisible(false);
								comboSecondArgColumn.setVisible(false);
								secondArgValue.setVisible(false);
								secondArgDate.setVisible(false);
								thresholdText.setVisible(false);
								comboThreshold.setVisible(false);
								btnAdd.setVisible(false);
								btnDel.setVisible(false);

							} else {
								Log.debug("ComboFirstElement selected: "
										+ column.getLabel());
								ColumnDataType columnDataType = ColumnDataType
										.getColumnDataTypeFromId(column
												.getDataTypeName());
								if (columnDataType == null) {
									storeOperator.clear();
									storeOperator.commitChanges();
									comboOperator.clear();
									comboOperator.reset();

									comboOperator.setVisible(false);
									comboFirstArgType.setVisible(false);
									comboFirstArgColumn.setVisible(false);
									firstArgValue.setVisible(false);
									firstArgDate.setVisible(false);
									andText.setVisible(false);
									comboSecondArgType.setVisible(false);
									comboSecondArgColumn.setVisible(false);
									secondArgValue.setVisible(false);
									secondArgDate.setVisible(false);
									thresholdText.setVisible(false);
									comboThreshold.setVisible(false);
									btnAdd.setVisible(false);
									btnDel.setVisible(false);
								} else {
									OperationsStore operationStore = new OperationsStore();
									storeOperator.clear();
									storeOperator.addAll(operationStore
											.getAll(column));
									storeOperator.commitChanges();
									comboOperator.clear();
									comboOperator.reset();

									comboOperator.setVisible(true);
									comboFirstArgType.setVisible(false);
									comboFirstArgColumn.setVisible(false);
									firstArgValue.setVisible(false);
									firstArgDate.setVisible(false);
									andText.setVisible(false);
									comboSecondArgType.setVisible(false);
									comboSecondArgColumn.setVisible(false);
									secondArgValue.setVisible(false);
									secondArgDate.setVisible(false);
									thresholdText.setVisible(false);
									comboThreshold.setVisible(false);
									btnAdd.setVisible(true);
									btnDel.setVisible(true);

								}

							}
							vert.forceLayout();
						}
					}
				});

		comboOperator.addSelectionHandler(new SelectionHandler<Operation>() {

			public void onSelection(SelectionEvent<Operation> event) {
				if (event.getSelectedItem() != null) {
					Operation op = event.getSelectedItem();
					if (op == null) {
						Log.debug("Operator selected: null");
						comboFirstArgType.setVisible(false);
						comboFirstArgColumn.setVisible(false);
						firstArgValue.setVisible(false);
						firstArgDate.setVisible(false);
						andText.setVisible(false);
						comboSecondArgType.setVisible(false);
						comboSecondArgColumn.setVisible(false);
						secondArgValue.setVisible(false);
						secondArgDate.setVisible(false);
						thresholdText.setVisible(false);
						comboThreshold.setVisible(false);
						btnAdd.setVisible(false);
						btnDel.setVisible(false);
					} else {
						Log.debug("Operator selected:" + op.toString());
						switch (op.getOperatorType()) {
						case EQUALS:
						case GREATER:
						case GREATER_OR_EQUALS:
						case LESSER:
						case LESSER_OR_EQUALS:
						case NOT_EQUALS:
						case NOT_GREATER:
						case NOT_LESSER:
						case BEGINS_WITH:
						case ENDS_WITH:
						case CONTAINS:
						case NOT_BEGINS_WITH:
						case NOT_ENDS_WITH:
						case NOT_CONTAINS:
							comboFirstArgType.reset();

							comboFirstArgType.setVisible(true);
							comboFirstArgColumn.setVisible(false);
							firstArgValue.setVisible(false);
							firstArgDate.setVisible(false);
							andText.setVisible(false);
							comboSecondArgType.setVisible(false);
							comboSecondArgColumn.setVisible(false);
							secondArgValue.setVisible(false);
							secondArgDate.setVisible(false);
							thresholdText.setVisible(false);
							comboThreshold.setVisible(false);
							btnAdd.setVisible(true);
							btnDel.setVisible(true);
							break;
						case BETWEEN:
						case NOT_BETWEEN:
							comboFirstArgType.reset();
							comboSecondArgType.reset();

							comboFirstArgType.setVisible(true);
							comboFirstArgColumn.setVisible(false);
							firstArgValue.setVisible(false);
							firstArgDate.setVisible(false);
							andText.setVisible(true);
							comboSecondArgType.setVisible(true);
							comboSecondArgColumn.setVisible(false);
							secondArgValue.setVisible(false);
							secondArgDate.setVisible(false);
							thresholdText.setVisible(false);
							comboThreshold.setVisible(false);
							btnAdd.setVisible(true);
							btnDel.setVisible(true);
							break;
						case IN:
						case NOT_IN:	
						case MATCH_REGEX:	
						case NOT_MATCH_REGEX:	
						case SOUNDEX:
							comboFirstArgType.reset();
							comboFirstArgType.setValue(ArgType.VALUE, true);
							firstArgValue.reset();
							
							comboFirstArgType.setVisible(false);
							comboFirstArgColumn.setVisible(false);
							firstArgValue.setVisible(true);
							firstArgDate.setVisible(false);
							andText.setVisible(false);
							comboSecondArgType.setVisible(false);
							comboSecondArgColumn.setVisible(false);
							secondArgValue.setVisible(false);
							secondArgDate.setVisible(false);
							thresholdText.setVisible(false);
							comboThreshold.setVisible(false);
							btnAdd.setVisible(true);
							btnDel.setVisible(true);
							break;
						case LEVENSHTEIN:
							comboFirstArgType.reset();
							comboFirstArgType.setValue(ArgType.VALUE, true);
							firstArgValue.reset();
							
							storeThreshold.clear();
							storeThreshold
									.addAll(ThresholdStore.thresholdsLevenshtein);
							storeThreshold.commitChanges();
							comboThreshold.clear();
							comboThreshold.reset();

							comboFirstArgType.setVisible(false);
							comboFirstArgColumn.setVisible(false);
							firstArgValue.setVisible(true);
							firstArgDate.setVisible(false);
							andText.setVisible(false);
							comboSecondArgType.setVisible(false);
							comboSecondArgColumn.setVisible(false);
							secondArgValue.setVisible(false);
							secondArgDate.setVisible(false);
							thresholdText.setVisible(true);
							comboThreshold.setVisible(true);
							btnAdd.setVisible(true);
							btnDel.setVisible(true);

							comboThreshold.setValue(ThresholdStore
									.defaultThresholdLevenshtein());

							break;
						case SIMILARITY:
							comboFirstArgType.reset();
							comboFirstArgType.setValue(ArgType.VALUE, true);
							firstArgValue.reset();
							
							storeThreshold.clear();
							storeThreshold
									.addAll(ThresholdStore.thresholdsSimilarity);
							storeThreshold.commitChanges();
							comboThreshold.clear();
							comboThreshold.reset();

							comboFirstArgType.setVisible(false);
							comboFirstArgColumn.setVisible(false);
							firstArgValue.setVisible(true);
							firstArgDate.setVisible(false);
							andText.setVisible(false);
							comboSecondArgType.setVisible(false);
							comboSecondArgColumn.setVisible(false);
							secondArgValue.setVisible(false);
							secondArgDate.setVisible(false);
							thresholdText.setVisible(true);
							comboThreshold.setVisible(true);
							btnAdd.setVisible(true);
							btnDel.setVisible(true);

							comboThreshold.setValue(ThresholdStore
									.defaultThresholdSimilarity());

							break;
						case IS_NULL:
						case IS_NOT_NULL:
							comboFirstArgType.setVisible(false);
							comboFirstArgColumn.setVisible(false);
							firstArgValue.setVisible(false);
							firstArgDate.setVisible(false);
							andText.setVisible(false);
							comboSecondArgType.setVisible(false);
							comboSecondArgColumn.setVisible(false);
							secondArgValue.setVisible(false);
							secondArgDate.setVisible(false);
							thresholdText.setVisible(false);
							comboThreshold.setVisible(false);
							btnAdd.setVisible(true);
							btnDel.setVisible(true);
							break;
						default:
							comboFirstArgType.setVisible(false);
							comboFirstArgColumn.setVisible(false);
							firstArgValue.setVisible(false);
							firstArgDate.setVisible(false);
							andText.setVisible(false);
							comboSecondArgType.setVisible(false);
							comboSecondArgColumn.setVisible(false);
							secondArgValue.setVisible(false);
							secondArgDate.setVisible(false);
							thresholdText.setVisible(false);
							comboThreshold.setVisible(false);
							btnAdd.setVisible(false);
							btnDel.setVisible(false);
							break;
						}
					}
					vert.forceLayout();
				}
			}

		});

		comboFirstArgType.addSelectionHandler(new SelectionHandler<ArgType>() {

			@Override
			public void onSelection(SelectionEvent<ArgType> event) {
				if (event.getSelectedItem() != null) {
					ArgType argType = event.getSelectedItem();
					if (argType == null) {
						Log.debug("Operator selected: null");
						comboFirstArgColumn.setVisible(false);
						firstArgValue.setVisible(false);
						firstArgDate.setVisible(false);
					} else {
						Log.debug("Operator selected:" + argType.getLabel());
						ColumnData firstElementColumn = comboFirstElementColumn
								.getCurrentValue();
						if (firstElementColumn == null) {
							Log.debug("FirstElementColumn selected: null");
							comboFirstArgColumn.setVisible(false);
							firstArgValue.setVisible(false);
							firstArgDate.setVisible(false);
						} else {
							Log.debug("FirstElementColumn selected: "
									+ firstElementColumn.getLabel());
							ColumnDataType columnDataType = ColumnDataType
									.getColumnDataTypeFromId(firstElementColumn
											.getDataTypeName());
							switch (argType) {
							case COLUMN:
								storeFirstArgColumn.clear();
								storeFirstArgColumn
										.addAll(getSanitizedColumns(firstElementColumn));
								storeFirstArgColumn.commitChanges();
								comboFirstArgColumn.clear();
								comboFirstArgColumn.reset();

								comboFirstArgColumn.setVisible(true);
								firstArgValue.setVisible(false);
								firstArgDate.setVisible(false);
								break;
							case VALUE:
								comboFirstArgColumn.setVisible(false);
								if (columnDataType
										.compareTo(ColumnDataType.Date) == 0) {
									firstArgDate.reset();
									firstArgValue.setVisible(false);
									firstArgDate.setVisible(true);
								} else {
									firstArgValue.reset();
									firstArgValue.setVisible(true);
									firstArgDate.setVisible(false);
								}
								break;
							default:
								comboFirstArgColumn.setVisible(false);
								firstArgValue.setVisible(false);
								firstArgDate.setVisible(false);
								break;
							}
						}
					}
					vert.forceLayout();
				}

			}
		});

		comboSecondArgType.addSelectionHandler(new SelectionHandler<ArgType>() {

			@Override
			public void onSelection(SelectionEvent<ArgType> event) {
				if (event.getSelectedItem() != null) {
					ArgType argType = event.getSelectedItem();
					if (argType == null) {
						Log.debug("Operator selected: null");
						comboSecondArgColumn.setVisible(false);
						secondArgValue.setVisible(false);
						secondArgDate.setVisible(false);
					} else {
						Log.debug("Operator selected:" + argType.getLabel());
						ColumnData firstElementColumn = comboFirstElementColumn
								.getCurrentValue();
						if (firstElementColumn == null) {
							Log.debug("FirstElementColumn selected: null");
							comboSecondArgColumn.setVisible(false);
							secondArgValue.setVisible(false);
							secondArgDate.setVisible(false);
						} else {
							Log.debug("FirstElementColumn selected: "
									+ firstElementColumn.getLabel());
							ColumnDataType columnDataType = ColumnDataType
									.getColumnDataTypeFromId(firstElementColumn
											.getDataTypeName());

							switch (argType) {
							case COLUMN:
								storeSecondArgColumn.clear();
								storeSecondArgColumn
										.addAll(getSanitizedColumns(firstElementColumn));
								storeSecondArgColumn.commitChanges();
								comboSecondArgColumn.clear();
								comboSecondArgColumn.reset();

								comboSecondArgColumn.setVisible(true);
								secondArgValue.setVisible(false);
								secondArgDate.setVisible(false);
								break;
							case VALUE:
								comboSecondArgColumn.setVisible(false);
								if (columnDataType
										.compareTo(ColumnDataType.Date) == 0) {
									secondArgDate.reset();
									secondArgValue.setVisible(false);
									secondArgDate.setVisible(true);
								} else {
									secondArgValue.reset();
									secondArgValue.setVisible(true);
									secondArgDate.setVisible(false);
								}
								break;
							default:
								comboSecondArgColumn.setVisible(false);
								secondArgValue.setVisible(false);
								secondArgDate.setVisible(false);
								break;
							}
						}
					}
					vert.forceLayout();
				}

			}
		});

		comboDepth.setVisible(true);
		comboFirstElementColumn.setVisible(false);
		comboOperator.setVisible(false);
		comboFirstArgType.setVisible(false);
		comboFirstArgColumn.setVisible(false);
		firstArgValue.setVisible(false);
		firstArgDate.setVisible(false);
		andText.setVisible(false);
		comboSecondArgType.setVisible(false);
		comboSecondArgColumn.setVisible(false);
		secondArgValue.setVisible(false);
		secondArgDate.setVisible(false);
		thresholdText.setVisible(false);
		comboThreshold.setVisible(false);
		btnAdd.setVisible(true);
		btnDel.setVisible(true);

		horiz.add(comboDepth, new BoxLayoutData(new Margins(0)));
		horiz.add(comboFirstElementColumn, new BoxLayoutData(new Margins(0)));
		horiz.add(comboOperator, new BoxLayoutData(new Margins(0)));
		horiz.add(comboFirstArgType, new BoxLayoutData(new Margins(0)));
		horiz.add(comboFirstArgColumn, new BoxLayoutData(new Margins(0)));
		horiz.add(firstArgValue, new BoxLayoutData(new Margins(0)));
		horiz.add(firstArgDate, new BoxLayoutData(new Margins(0)));
		horiz.add(andText, new BoxLayoutData(new Margins(0)));
		horiz.add(comboSecondArgType, new BoxLayoutData(new Margins(0)));
		horiz.add(comboSecondArgColumn, new BoxLayoutData(new Margins(0)));
		horiz.add(secondArgValue, new BoxLayoutData(new Margins(0)));
		horiz.add(secondArgDate, new BoxLayoutData(new Margins(0)));
		horiz.add(thresholdText, new BoxLayoutData(new Margins(0)));
		horiz.add(comboThreshold, new BoxLayoutData(new Margins(0)));
		horiz.add(btnAdd, new BoxLayoutData(new Margins(2, 0, 2, 0)));
		horiz.add(btnDel, new BoxLayoutData(new Margins(2, 0, 2, 0)));

		vert.add(horiz, new VerticalLayoutData(1, -1, new Margins(1)));

	}

	private ArrayList<ColumnData> getSanitizedColumns(ColumnData column) {
		ArrayList<ColumnData> sanitizedColumns = new ArrayList<ColumnData>(
				columns);
		sanitizedColumns.remove(column);
		return sanitizedColumns;

	}

	private void removeCondition(ComboBox<DepthOfExpressionElement> source) {
		HBoxLayoutContainer horiz = (HBoxLayoutContainer) source.getParent();
		int index = vert.getWidgetIndex(horiz);
		Log.debug("No concat for index: " + index);
		index++;
		for (int i = index; i < vert.getWidgetCount();) {
			Log.debug("Remove horiz index: " + i);
			vert.remove(i);
		}
	}

	private boolean existCondition(ComboBox<DepthOfExpressionElement> source) {
		boolean exist = false;
		HBoxLayoutContainer horiz = (HBoxLayoutContainer) source.getParent();
		int index = vert.getWidgetIndex(horiz);
		Log.debug("No concat for index: " + index);
		index++;
		if (index < vert.getWidgetCount()) {
			exist = true;
		} else {
			exist = false;
		}
		return exist;
	}

	@SuppressWarnings("unchecked")
	public C_Expression getExpression() throws ConditionTypeMapException {
		C_Expression exp = null;
		boolean expSet = false;

		Stack<LogicalDepth> depthStack = new Stack<LogicalDepth>();
		ComboBox<DepthOfExpressionElement> comboDepth;

		Iterator<Widget> iteratorVert = vert.iterator();
		HBoxLayoutContainer horiz;
		while (iteratorVert.hasNext()) {
			horiz = (HBoxLayoutContainer) iteratorVert.next();

			comboDepth = (ComboBox<DepthOfExpressionElement>) horiz
					.getItemByItemId(itemIdComboDepth);

			DepthOfExpressionElement depthOfExpressionElement = comboDepth
					.getCurrentValue();

			if (depthOfExpressionElement == null) {
				throw new ConditionTypeMapException("Fill all field and creates a valid expression!");
			}

			DepthOfExpressionType depth = depthOfExpressionElement.getType();

			if (depth == null) {
				throw new ConditionTypeMapException("Fill all field and creates a valid expression!");
			}

			if (expSet) {
				throw new ConditionTypeMapException("Expression is invalid!");
			}

			switch (depth) {
			case BOTTOM:
				if (vert.getWidgetCount() != 1) {
					throw new ConditionTypeMapException("Expression is invalid!");
				}
				C_Expression singleCondition = calcExpression(horiz);
				exp = singleCondition;
				expSet = true;
				break;
			case COMMA:
				if (depthStack.isEmpty()) {
					return null;
				} else {
					LogicalDepth logicalDepth = depthStack.peek();
					if (logicalDepth != null) {
						ArrayList<C_Expression> args = logicalDepth
								.getArguments();
						C_Expression commaArg = calcExpression(horiz);
						args.add(commaArg);
					} else {
						return null;
					}
				}
				break;
			case ENDAND:
				if (depthStack.isEmpty()) {
					throw new ConditionTypeMapException("Expression is invalid!");
				} else {
					LogicalDepth endAndPopped = depthStack.pop();
					if (endAndPopped.getType().compareTo(
							DepthOfExpressionType.STARTAND) == 0) {
						ConditionOnMultiColumnTypeMap mapOp = new ConditionOnMultiColumnTypeMap();
						C_Expression cAnd = mapOp.createC_And(endAndPopped.getArguments());
						if (depthStack.isEmpty()) {
							exp = cAnd;
							expSet = true;
						} else {
							LogicalDepth logicalDepth = depthStack.peek();
							if (logicalDepth != null) {
								ArrayList<C_Expression> args = logicalDepth
										.getArguments();
								args.add(cAnd);
							} else {
								throw new ConditionTypeMapException("Expression is invalid!");
							}
						}
					} else {
						throw new ConditionTypeMapException("Expression is invalid, brackets do not match!");
					}
				}
				break;
			case ENDOR:
				if (depthStack.isEmpty()) {
					throw new ConditionTypeMapException("Expression is invalid!");
				} else {
					LogicalDepth endOrPopped = depthStack.pop();
					if (endOrPopped.getType().compareTo(
							DepthOfExpressionType.STARTOR) == 0) {
						ConditionOnMultiColumnTypeMap mapOp = new ConditionOnMultiColumnTypeMap();
						C_Expression cOr = mapOp.createC_Or(endOrPopped.getArguments());
						if (depthStack.isEmpty()) {
							exp = cOr;
							expSet = true;
						} else {
							LogicalDepth logicalDepth = depthStack.peek();
							if (logicalDepth != null) {
								ArrayList<C_Expression> args = logicalDepth
										.getArguments();
								args.add(cOr);
							} else {
								throw new ConditionTypeMapException("Expression is invalid!");
							}
						}
					} else {
						throw new ConditionTypeMapException("Expression is invalid, brackets do not match!");
					}
				}
				break;
			case STARTAND:
				C_Expression startAndExp = calcExpression(horiz);
				ArrayList<C_Expression> andArgs = new ArrayList<C_Expression>();
				andArgs.add(startAndExp);
				LogicalDepth andDepth = new LogicalDepth(depth, andArgs);
				depthStack.add(andDepth);
				break;
			case STARTOR:
				C_Expression startOrExp = calcExpression(horiz);
				ArrayList<C_Expression> orArgs = new ArrayList<C_Expression>();
				orArgs.add(startOrExp);
				LogicalDepth orDepth = new LogicalDepth(depth, orArgs);
				depthStack.add(orDepth);
				break;
			default:
				break;

			}

		}

		Log.debug("C_Expression:" + exp);
		return exp;
	}

	protected C_Expression calcExpression(HBoxLayoutContainer horiz)
			throws ConditionTypeMapException {
		C_Expression expression = null;
		
		DateTimeFormat sdf = DateTimeFormat.getFormat("yyyy-MM-dd");

		@SuppressWarnings("unchecked")
		ComboBox<ColumnData> comboFirstElementColumn = (ComboBox<ColumnData>) horiz
				.getItemByItemId(itemIdFirstElementColumn);
		ColumnData column = comboFirstElementColumn.getCurrentValue();

		if (column == null) {
			throw new ConditionTypeMapException("Fill all arguments!");
		} else {
			@SuppressWarnings("unchecked")
			ComboBox<Operation> comboOp = (ComboBox<Operation>) horiz
					.getItemByItemId(itemIdComboOperation);
			Operation op = comboOp.getCurrentValue();
			if (op == null) {
				throw new ConditionTypeMapException("Fill all arguments!");
			} else {
				Log.debug("Op.: " + comboOp.getCurrentValue());
				
				ColumnData firstArgColumn=null;
				String firstArg=null;
				ColumnData secondArgColumn=null;
				String secondArg=null;
				
				
				@SuppressWarnings("unchecked")
				ComboBox<ArgType>comboFirstArgType=(ComboBox<ArgType>)horiz.getItemByItemId(itemIdFirstArgType);
				ArgType firstArgType=comboFirstArgType.getCurrentValue();
				if(firstArgType!=null){
					if(firstArgType.compareTo(ArgType.COLUMN)==0){
						@SuppressWarnings("unchecked")
						ComboBox<ColumnData> comboFirstArgColumn=(ComboBox<ColumnData>)
								horiz.getItemByItemId(itemIdFirstArgColumn);
						firstArgColumn=comboFirstArgColumn.getCurrentValue();
					} else {
						if(firstArgType.compareTo(ArgType.VALUE)==0){
							if (column.getDataTypeName().compareTo(
									ColumnDataType.Date.toString()) == 0) {
								DateField firstArgDate = (DateField) horiz
									.getItemByItemId(itemIdFirstArgDate);
								Date firstDate = firstArgDate.getCurrentValue();
								if(firstDate==null){
									throw new ConditionTypeMapException("Set a valid date and fill all arguments!");
								} else {
									firstArg=sdf.format(firstDate);
									if(firstArg==null){
										throw new ConditionTypeMapException("Set a valid date and fill all arguments!");
									}
								}
							} else {
								TextField firstArgText = (TextField) horiz
										.getItemByItemId(itemIdFirstArgValue);
								firstArg=firstArgText.getCurrentValue();
							}
						} else {
							
						}
					}
				}
				
				
				@SuppressWarnings("unchecked")
				ComboBox<ArgType>comboSecondArgType=(ComboBox<ArgType>)horiz.getItemByItemId(itemIdSecondArgType);
				ArgType secondArgType=comboSecondArgType.getCurrentValue();
				if(secondArgType!=null){
					if(secondArgType.compareTo(ArgType.COLUMN)==0){
						@SuppressWarnings("unchecked")
						ComboBox<ColumnData> comboSecondArgColumn=(ComboBox<ColumnData>)
								horiz.getItemByItemId(itemIdSecondArgColumn);
						secondArgColumn=comboSecondArgColumn.getCurrentValue();
					} else {
						if(secondArgType.compareTo(ArgType.VALUE)==0){
							if (column.getDataTypeName().compareTo(
									ColumnDataType.Date.toString()) == 0) {
								DateField secondArgDate = (DateField) horiz
										.getItemByItemId(itemIdSecondArgDate);
								Date secondDate = secondArgDate.getCurrentValue();
								if(secondDate==null){
								
								} else {
									secondArg=sdf.format(secondDate);
								}
							} else {
								TextField secondArgText = (TextField) horiz
										.getItemByItemId(itemIdSecondArgValue);
								secondArg=secondArgText.getCurrentValue();
							}
							
						} else {
							
						}
					}
				}
				
				@SuppressWarnings("unchecked")
				ComboBox<Threshold> comboThreshold = (ComboBox<Threshold>) horiz
						.getItemByItemId(itemIdComboThreshold);
				
				Threshold threshold=comboThreshold.getCurrentValue();
				
				ConditionOnMultiColumnTypeMap mapOp = new ConditionOnMultiColumnTypeMap();
				
				
				expression = mapOp.map(
						column,
						comboOp.getCurrentValue().getOperatorType(),
						firstArgType, firstArgColumn,
						firstArg,
						secondArgType, secondArgColumn,
						secondArg, threshold);
				
				
				
				
				
			}
		}
		return expression;
	}

}
