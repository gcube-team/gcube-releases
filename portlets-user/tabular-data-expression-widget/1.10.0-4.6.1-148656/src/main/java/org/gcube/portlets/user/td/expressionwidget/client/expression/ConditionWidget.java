package org.gcube.portlets.user.td.expressionwidget.client.expression;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.gcube.portlets.user.td.expressionwidget.client.custom.IconButton;
import org.gcube.portlets.user.td.expressionwidget.client.operation.Operation;
import org.gcube.portlets.user.td.expressionwidget.client.operation.OperationProperties;
import org.gcube.portlets.user.td.expressionwidget.client.operation.OperationsStore;
import org.gcube.portlets.user.td.expressionwidget.client.resources.ExpressionResources;
import org.gcube.portlets.user.td.expressionwidget.client.threshold.Threshold;
import org.gcube.portlets.user.td.expressionwidget.client.threshold.ThresholdProperties;
import org.gcube.portlets.user.td.expressionwidget.client.threshold.ThresholdStore;
import org.gcube.portlets.user.td.expressionwidget.shared.condition.ConditionTypeMap;
import org.gcube.portlets.user.td.expressionwidget.shared.exception.ConditionTypeMapException;
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
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.cell.core.client.form.ComboBoxCell.TriggerAction;
import com.sencha.gxt.core.client.dom.ScrollSupport.ScrollMode;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.core.client.util.ToggleGroup;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.widget.core.client.container.BoxLayoutContainer.BoxLayoutData;
import com.sencha.gxt.widget.core.client.container.HBoxLayoutContainer;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.event.BeforeShowEvent;
import com.sencha.gxt.widget.core.client.form.ComboBox;
import com.sencha.gxt.widget.core.client.form.DateField;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.form.Radio;
import com.sencha.gxt.widget.core.client.form.TextField;

/**
 * 
 * @author Giancarlo Panichi
 *
 * 
 */
public class ConditionWidget extends SimpleContainer {
	
	private static final String HEIGHT = "210px";
	private static final String WIDTH = "612px";
	private ConditionWidget thisCont;

	private FieldLabel matchLabel;	
	private ToggleGroup groupMatch;

	private String itemIdCombo;
	private String itemIdFirstArg;
	private String itemIdSecondArg;
	private String itemIdFirstArgDate;
	private String itemIdSecondArgDate;
	private String itemIdComboThreshold;
	
	private String itemIdBtnAdd;
	private String itemIdBtnDel;
	private VerticalLayoutContainer vert;
	private ColumnData column;

	private String readableExpression;
	private Radio radioAll;
	private Radio radioAny;

	public ConditionWidget(ColumnData column) {
		super();
		create(column, WIDTH, HEIGHT);
	}

	public ConditionWidget(ColumnData column, String width, String height) {
		super();
		create(column, width, height);
	}
	
	public void update(ColumnData newColumn){
		vert.clear();
		this.column=newColumn;
		radioAll.setValue(true);
		radioAny.setValue(false);
		groupMatch.setValue(radioAll);
		matchLabel.setVisible(false);
		setup();
	}
	

	protected void create(ColumnData column, String width, String height) {
		this.column = column;
		setBorders(true);
		setWidth(width);
		setHeight(height);
		forceLayoutOnResize = true;

		thisCont = this;
		
		String sign="columnX";
		
		if(column!=null&& column.getName()!=null){
			sign=column.getName();
		} 

		itemIdCombo = "ComboConditions" + sign;
		itemIdFirstArg = "FirstArg" + sign;
		itemIdSecondArg = "SecondArg" + sign;
		itemIdFirstArgDate = "FirstArgDate" + sign;
		itemIdSecondArgDate = "SecondArgDate" + sign;
		itemIdComboThreshold = "ComboThreshold" + sign;
		itemIdBtnAdd = "BtnAdd" + sign;
		itemIdBtnDel = "BtnDel" + sign;

		VerticalLayoutContainer baseLayout = new VerticalLayoutContainer();

	    radioAll = new Radio();
		radioAll.setName("All");
		radioAll.setBoxLabel("All conditions");
		radioAll.setValue(true);

	    radioAny = new Radio();
		radioAny.setName("Any");
		radioAny.setBoxLabel("Any condition");

		HorizontalPanel matchPanel = new HorizontalPanel();
		matchPanel.add(radioAll);
		matchPanel.add(radioAny);

		matchLabel = new FieldLabel(matchPanel, "Match");
		matchLabel.setVisible(false);

		baseLayout.add(matchLabel, new VerticalLayoutData(-1, -1, new Margins(
				2, 1, 2, 1)));

		groupMatch = new ToggleGroup();
		groupMatch.add(radioAll);
		groupMatch.add(radioAny);
		groupMatch.setValue(radioAll);


		vert = new VerticalLayoutContainer();
		vert.setScrollMode(ScrollMode.AUTO);// Set In GXT 3.0.1
		vert.setAdjustForScroll(true);
		
		
		setup();

		baseLayout.add(vert, new VerticalLayoutData(1, 1, new Margins(0)));

		add(baseLayout);

	}

	protected void setup() {
		final HBoxLayoutContainer horiz = new HBoxLayoutContainer();

		final TextField firstArg = new TextField();
		firstArg.setItemId(itemIdFirstArg);
		firstArg.setVisible(false);

		final DateField firstArgDate = new DateField();
		firstArgDate.setItemId(itemIdFirstArgDate);
		firstArgDate.setVisible(false);

		final HTML andText = new HTML(
				"<div style='vertical-align:middle; margin-left:2px;margin-right:2px;margin-top:4px;'>and</div>");
		andText.setVisible(false);

		final TextField secondArg = new TextField();
		secondArg.setItemId(itemIdSecondArg);
		secondArg.setVisible(false);

		final DateField secondArgDate = new DateField();
		secondArgDate.setItemId(itemIdSecondArgDate);
		secondArgDate.setVisible(false);

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
		comboThreshold.setWidth("100px");
		comboThreshold.setEditable(false);
		comboThreshold.setTriggerAction(TriggerAction.ALL);
		comboThreshold.setVisible(false);

		final HTML thresholdText = new HTML(
				"<div style='vertical-align:middle; margin-left:2px;margin-right:2px;margin-top:4px;'>Threshold:</div>");
		thresholdText.setVisible(false);
		
		// Button
		final IconButton btnAdd = new IconButton();
		btnAdd.setItemId(itemIdBtnAdd);
		btnAdd.setIcon(ExpressionResources.INSTANCE.add());
		btnAdd.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				Log.debug("Clicked btnAdd");
				matchLabel.setVisible(true);
				addCondition();
				thisCont.forceLayout();
				vert.forceLayout();

			}
		});
		btnAdd.setVisible(false);

		final IconButton btnDel = new IconButton();
		btnDel.setItemId(itemIdBtnDel);
		btnDel.setIcon(ExpressionResources.INSTANCE.delete());
		btnDel.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				Log.debug("Clicked btnDel");
				vert.remove(horiz);
				if (vert.getWidgetCount() == 0) {
					setup();
					matchLabel.setVisible(false);
				} else {
					if (vert.getWidgetCount() == 1) {
						matchLabel.setVisible(false);
					}
				}
				thisCont.forceLayout();
				vert.forceLayout();

			}
		});
		btnDel.setVisible(false);

		// Operation
		OperationProperties props = GWT.create(OperationProperties.class);
		Log.debug("Props: " + props);
		ListStore<Operation> storeOp = new ListStore<Operation>(props.id());
		Log.debug("Store: " + storeOp);
		OperationsStore factory = new OperationsStore();
		storeOp.addAll(factory.getAll(column));

		Log.debug("Store created");
		ComboBox<Operation> comboOp = new ComboBox<Operation>(storeOp,
				props.label());

		Log.debug("Combo created");

		comboOp.addSelectionHandler(new SelectionHandler<Operation>() {

			public void onSelection(SelectionEvent<Operation> event) {
				if (event.getSelectedItem() != null) {
					Operation op = event.getSelectedItem();
					Log.debug("Condition selected:" + op.toString());
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
					case MATCH_REGEX:
					case CONTAINS:
					case NOT_BEGINS_WITH:
					case NOT_ENDS_WITH:
					case NOT_CONTAINS:
					case NOT_MATCH_REGEX:
					case IN:
					case NOT_IN:
					case SOUNDEX:
						if (column.getDataTypeName().compareTo("Date") == 0) {
							firstArgDate.setVisible(true);
							andText.setVisible(false);
							secondArgDate.setVisible(false);
							comboThreshold.setVisible(false);
							thresholdText.setVisible(false);
						} else {
							firstArg.setVisible(true);
							andText.setVisible(false);
							secondArg.setVisible(false);
							thresholdText.setVisible(false);
							comboThreshold.setVisible(false);
						}
						btnAdd.setVisible(true);
						btnDel.setVisible(true);
						break;
					case BETWEEN:
					case NOT_BETWEEN:
						if (column.getDataTypeName().compareTo("Date") == 0) {
							firstArgDate.setVisible(false);
							andText.setVisible(false);
							secondArgDate.setVisible(false);
							thresholdText.setVisible(false);
							comboThreshold.setVisible(false);
						} else {
							firstArg.setVisible(true);
							andText.setVisible(true);
							secondArg.setVisible(true);
							thresholdText.setVisible(false);
							comboThreshold.setVisible(false);
						}
						btnAdd.setVisible(true);
						btnDel.setVisible(true);
						break;
					case LEVENSHTEIN:
						if (column.getDataTypeName().compareTo("Date") == 0) {
							firstArgDate.setVisible(false);
							andText.setVisible(false);
							secondArgDate.setVisible(false);
							thresholdText.setVisible(false);
							comboThreshold.setVisible(false);
						} else {
							firstArg.setVisible(true);
							andText.setVisible(false);
							secondArg.setVisible(false);
							thresholdText.setVisible(true);
							storeThreshold.clear();
							storeThreshold
									.addAll(ThresholdStore.thresholdsLevenshtein);
							storeThreshold.commitChanges();
							comboThreshold.clear();
							comboThreshold.reset();
							comboThreshold.setVisible(true);
							comboThreshold.setValue(ThresholdStore
									.defaultThresholdLevenshtein());

						}
						btnAdd.setVisible(true);
						btnDel.setVisible(true);
						break;
					case SIMILARITY:
						if (column.getDataTypeName().compareTo("Date") == 0) {
							firstArgDate.setVisible(false);
							andText.setVisible(false);
							secondArgDate.setVisible(false);
							thresholdText.setVisible(false);
							comboThreshold.setVisible(false);
						} else {
							firstArg.setVisible(true);
							andText.setVisible(false);
							secondArg.setVisible(false);
							thresholdText.setVisible(true);
							storeThreshold.clear();
							storeThreshold
									.addAll(ThresholdStore.thresholdsSimilarity);
							storeThreshold.commitChanges();
							comboThreshold.clear();
							comboThreshold.reset();
							comboThreshold.setVisible(true);
							comboThreshold.setValue(ThresholdStore
									.defaultThresholdSimilarity());

						}
						btnAdd.setVisible(true);
						btnDel.setVisible(true);
						break;
					case IS_NULL:
					case IS_NOT_NULL:
						if (column.getDataTypeName().compareTo("Date") == 0) {
							firstArgDate.setVisible(false);
							andText.setVisible(false);
							secondArgDate.setVisible(false);
							thresholdText.setVisible(false);
							comboThreshold.setVisible(false);
						} else {
							firstArg.setVisible(false);
							andText.setVisible(false);
							secondArg.setVisible(false);
							thresholdText.setVisible(false);
							comboThreshold.setVisible(false);
						}
						btnAdd.setVisible(true);
						btnDel.setVisible(true);
						break;
					default:
						break;
					}

					thisCont.forceLayout();

				}
			}

		});

		comboOp.setEmptyText("Select a condition...");
		comboOp.setItemId(itemIdCombo);
		comboOp.setWidth("230px");
		comboOp.setEditable(false);
		comboOp.setTriggerAction(TriggerAction.ALL);

		horiz.add(comboOp, new BoxLayoutData(new Margins(0)));
		if (column==null|| column.getDataTypeName()==null|| column.getDataTypeName().compareTo("Date") != 0) {
			horiz.add(firstArg, new BoxLayoutData(new Margins(0)));
			horiz.add(andText, new BoxLayoutData(new Margins(0)));
			horiz.add(secondArg, new BoxLayoutData(new Margins(0)));
			horiz.add(thresholdText, new BoxLayoutData(new Margins(0)));
			horiz.add(comboThreshold, new BoxLayoutData(new Margins(0)));
		} else {
			horiz.add(firstArgDate, new BoxLayoutData(new Margins(0)));
			horiz.add(andText, new BoxLayoutData(new Margins(0)));
			horiz.add(secondArgDate, new BoxLayoutData(new Margins(0)));
			horiz.add(thresholdText, new BoxLayoutData(new Margins(0)));
			horiz.add(comboThreshold, new BoxLayoutData(new Margins(0)));
		}
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

		final TextField firstArg = new TextField();
		firstArg.setItemId(itemIdFirstArg);
		firstArg.setVisible(false);

		final DateField firstArgDate = new DateField();
		firstArgDate.setItemId(itemIdFirstArgDate);
		firstArgDate.setVisible(false);

		final HTML andText = new HTML(
				"<div style='vertical-align:middle; margin-left:2px;margin-right:2px;margin-top:4px;'>and</div>");
		andText.setVisible(false);

		final TextField secondArg = new TextField();
		secondArg.setItemId(itemIdSecondArg);
		secondArg.setVisible(false);

		final DateField secondArgDate = new DateField();
		secondArgDate.setItemId(itemIdSecondArgDate);
		secondArgDate.setVisible(false);

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
		comboThreshold.setWidth("100px");
		comboThreshold.setEditable(false);
		comboThreshold.setTriggerAction(TriggerAction.ALL);
		comboThreshold.setVisible(false);
		

		final HTML thresholdText = new HTML(
				"<div style='vertical-align:middle; margin-left:2px;margin-right:2px;margin-top:4px;'>Threshold:</div>");
		thresholdText.setVisible(false);

		
		// Button
		final IconButton btnAdd = new IconButton();
		btnAdd.setItemId(itemIdBtnAdd);
		btnAdd.setIcon(ExpressionResources.INSTANCE.add());
		btnAdd.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				Log.debug("Clicked btnAdd");
				matchLabel.setVisible(true);
				addCondition();
				thisCont.forceLayout();
				vert.forceLayout();
			}
		});
		btnAdd.setVisible(false);

		final IconButton btnDel = new IconButton();
		btnAdd.setItemId(itemIdBtnDel);
		btnDel.setIcon(ExpressionResources.INSTANCE.delete());
		btnDel.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				Log.debug("Clicked btnDel");
				vert.remove(horiz);
				if (vert.getWidgetCount() == 0) {
					setup();
					matchLabel.setVisible(false);
				} else {
					if (vert.getWidgetCount() == 1) {
						matchLabel.setVisible(false);
					}
				}
				thisCont.forceLayout();
				vert.forceLayout();
			}
		});

		OperationProperties props = GWT.create(OperationProperties.class);
		Log.debug("Props: " + props);
		ListStore<Operation> storeOp = new ListStore<Operation>(props.id());
		Log.debug("Store: " + storeOp);
		OperationsStore factory = new OperationsStore();
		storeOp.addAll(factory.getAll(column));

		Log.trace("Store created");

		final ComboBox<Operation> comboOp = new ComboBox<Operation>(storeOp,
				props.label());

		Log.trace("ComboOperation created");

		comboOp.addSelectionHandler(new SelectionHandler<Operation>() {

			public void onSelection(SelectionEvent<Operation> event) {
				if (event.getSelectedItem() != null) {
					Operation op = event.getSelectedItem();
					Log.debug("Condition selected:" + op.toString());
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
					case MATCH_REGEX:
					case CONTAINS:
					case NOT_BEGINS_WITH:
					case NOT_ENDS_WITH:
					case NOT_CONTAINS:
					case NOT_MATCH_REGEX:
					case IN:
					case NOT_IN:
					case SOUNDEX:
						if (column.getDataTypeName().compareTo("Date") == 0) {
							firstArgDate.setVisible(true);
							andText.setVisible(false);
							secondArgDate.setVisible(false);
							thresholdText.setVisible(false);
							comboThreshold.setVisible(false);
						} else {
							firstArg.setVisible(true);
							andText.setVisible(false);
							secondArg.setVisible(false);
							thresholdText.setVisible(false);
							comboThreshold.setVisible(false);
						}
						btnAdd.setVisible(true);
						btnDel.setVisible(true);
						break;
					case BETWEEN:
					case NOT_BETWEEN:
						if (column.getDataTypeName().compareTo("Date") == 0) {
							firstArgDate.setVisible(false);
							andText.setVisible(false);
							secondArgDate.setVisible(false);
							thresholdText.setVisible(false);
							comboThreshold.setVisible(false);
						} else {
							firstArg.setVisible(true);
							andText.setVisible(true);
							secondArg.setVisible(true);
							thresholdText.setVisible(false);
							comboThreshold.setVisible(false);
						}
						btnAdd.setVisible(true);
						btnDel.setVisible(true);
						break;
					case LEVENSHTEIN:
						if (column.getDataTypeName().compareTo("Date") == 0) {
							firstArgDate.setVisible(false);
							andText.setVisible(false);
							secondArgDate.setVisible(false);
							thresholdText.setVisible(false);
							comboThreshold.setVisible(false);
						} else {
							firstArg.setVisible(true);
							andText.setVisible(false);
							secondArg.setVisible(false);
							thresholdText.setVisible(true);
							storeThreshold.clear();
							storeThreshold
									.addAll(ThresholdStore.thresholdsLevenshtein);
							storeThreshold.commitChanges();
							comboThreshold.clear();
							comboThreshold.reset();
							comboThreshold.setVisible(true);
							comboThreshold.setValue(ThresholdStore
									.defaultThresholdLevenshtein());

						}
						btnAdd.setVisible(true);
						btnDel.setVisible(true);
						break;
					case SIMILARITY:
						if (column.getDataTypeName().compareTo("Date") == 0) {
							firstArgDate.setVisible(false);
							andText.setVisible(false);
							secondArgDate.setVisible(false);
							thresholdText.setVisible(false);
							comboThreshold.setVisible(false);
						} else {
							firstArg.setVisible(true);
							andText.setVisible(false);
							secondArg.setVisible(false);
							thresholdText.setVisible(true);
							storeThreshold.clear();
							storeThreshold
									.addAll(ThresholdStore.thresholdsSimilarity);
							storeThreshold.commitChanges();
							comboThreshold.clear();
							comboThreshold.reset();
							comboThreshold.setVisible(true);
							comboThreshold.setValue(ThresholdStore
									.defaultThresholdSimilarity());

						}
						btnAdd.setVisible(true);
						btnDel.setVisible(true);
						break;	
					case IS_NULL:
					case IS_NOT_NULL:
						if (column.getDataTypeName().compareTo("Date") == 0) {
							firstArgDate.setVisible(false);
							andText.setVisible(false);
							secondArgDate.setVisible(false);
							thresholdText.setVisible(false);
							comboThreshold.setVisible(false);
						} else {
							firstArg.setVisible(false);
							andText.setVisible(false);
							secondArg.setVisible(false);
							thresholdText.setVisible(false);
							comboThreshold.setVisible(false);
						}
						btnAdd.setVisible(true);
						btnDel.setVisible(true);
						break;
					default:
						break;
					}

					thisCont.forceLayout();

				}

			}

		});
		comboOp.setEmptyText("Select a condition...");
		comboOp.setItemId(itemIdCombo);
		comboOp.setWidth("230px");
		comboOp.setEditable(false);
		comboOp.setTriggerAction(TriggerAction.ALL);

		horiz.add(comboOp, new BoxLayoutData(new Margins(0)));
		if (column==null|| column.getDataTypeName().compareTo("Date") != 0) {
			horiz.add(firstArg, new BoxLayoutData(new Margins(0)));
			horiz.add(andText, new BoxLayoutData(new Margins(0)));
			horiz.add(secondArg, new BoxLayoutData(new Margins(0)));
			horiz.add(thresholdText, new BoxLayoutData(new Margins(0)));
			horiz.add(comboThreshold, new BoxLayoutData(new Margins(0)));
		} else {
			horiz.add(firstArgDate, new BoxLayoutData(new Margins(0)));
			horiz.add(andText, new BoxLayoutData(new Margins(0)));
			horiz.add(secondArgDate, new BoxLayoutData(new Margins(0)));
			horiz.add(thresholdText, new BoxLayoutData(new Margins(0)));
			horiz.add(comboThreshold, new BoxLayoutData(new Margins(0)));
		}
		horiz.add(btnAdd, new BoxLayoutData(new Margins(2, 0, 2, 0)));
		horiz.add(btnDel, new BoxLayoutData(new Margins(2, 0, 2, 0)));

		vert.add(horiz, new VerticalLayoutData(1, -1, new Margins(1)));

	}

	
	@SuppressWarnings("unchecked")
	public C_Expression getExpression() throws ConditionTypeMapException {
		C_Expression exp = null;
		readableExpression = new String();
		List<C_Expression> arguments = new ArrayList<C_Expression>();
		List<String> readableExpressionList = new ArrayList<String>();
		TextField firstArg;
		TextField secondArg;
		DateField firstArgDate;
		DateField secondArgDate;
		ComboBox<Threshold> comboThreshold;
		
		C_Expression expression=null;
		
		if(column==null){
			throw new ConditionTypeMapException("No column selected!");
		}
		
		DateTimeFormat sdf = DateTimeFormat.getFormat("yyyy-MM-dd");

		ConditionTypeMap mapOp = new ConditionTypeMap();
		Iterator<Widget> iteratorVert = vert.iterator();
		HBoxLayoutContainer horiz;
		while (iteratorVert.hasNext()) {
			horiz = (HBoxLayoutContainer) iteratorVert.next();
			ComboBox<Operation> comboOp = (ComboBox<Operation>) horiz
					.getItemByItemId(itemIdCombo);
			Log.debug("combo: " + comboOp.getCurrentValue());
			
			if(comboOp.getCurrentValue()==null){
				throw new ConditionTypeMapException("Fill all conditions!");
			}
			
			if (column.getDataTypeName().compareTo(
					ColumnDataType.Date.toString()) == 0) {
				firstArgDate = (DateField) horiz
						.getItemByItemId(itemIdFirstArgDate);
				secondArgDate = (DateField) horiz
						.getItemByItemId(itemIdSecondArgDate);
				Log.debug("argLeft: " + firstArgDate + " argRight:"
						+ secondArgDate);
				Date firstDate = firstArgDate.getCurrentValue();
				Date secondDate = secondArgDate.getCurrentValue();

				expression = mapOp.map(column, comboOp.getCurrentValue()
						.getOperatorType(),
						firstDate == null ? null : sdf.format(firstDate),
						secondDate == null ? null : sdf.format(secondDate)
						,null);
			} else {

				firstArg = (TextField) horiz.getItemByItemId(itemIdFirstArg);
				secondArg = (TextField) horiz.getItemByItemId(itemIdSecondArg);
				comboThreshold =(ComboBox<Threshold>) horiz.getItemByItemId(itemIdComboThreshold);
				
				Log.debug("argLeft: " + firstArg.getCurrentValue()
						+ " argRight: " + secondArg.getCurrentValue());

				expression = mapOp.map(column, comboOp.getCurrentValue()
						.getOperatorType(),
						firstArg == null ? null : firstArg.getCurrentValue(),
						secondArg == null ? null : secondArg.getCurrentValue(),
						comboThreshold.getCurrentValue()==null?null:comboThreshold.getCurrentValue());
			}
			readableExpressionList.add(expression.getReadableExpression());
			Log.debug(expression.toString());
			arguments.add(expression);
		}
		Log.debug("Expression Arguments Calculated: " + arguments.size());
		if (arguments.size() > 0) {
			if (arguments.size() == 1) {
				exp = arguments.get(0);
				readableExpression = readableExpressionList.get(0);
			} else {
				Radio radio = (Radio) groupMatch.getValue();
				Log.debug("Match:" + radio);
				if (radio.getName().compareTo("All") == 0) {
					exp = mapOp.createC_And(arguments);
					readableExpression = "And(";
					boolean first = true;
					for (String read : readableExpressionList) {
						if (first) {
							readableExpression += read;
						} else {
							readableExpression += ", " + read;
						}
					}
					readableExpression += ")";

				} else {
					if (radio.getName().compareTo("Any") == 0) {
						exp = mapOp.createC_Or(arguments);
						readableExpression = "Or(";
						boolean first = true;
						for (String read : readableExpressionList) {
							if (first) {
								readableExpression += read;
							} else {
								readableExpression += ", " + read;
							}
						}
						readableExpression += ")";
					} else {
						Log.error("No All or Any set!");
					}
				}
			}

		}
		Log.debug("C_Expression:" + exp.toString());
		return exp;
	}

	public String getReadableExpression() {
		return readableExpression;
	}
}
