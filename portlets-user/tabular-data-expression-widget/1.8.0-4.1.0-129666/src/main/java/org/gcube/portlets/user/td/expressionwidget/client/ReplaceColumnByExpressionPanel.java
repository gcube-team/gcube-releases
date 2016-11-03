package org.gcube.portlets.user.td.expressionwidget.client;

import java.util.ArrayList;
import java.util.Date;

import org.gcube.portlets.user.td.expressionwidget.client.expression.ConditionWidget;
import org.gcube.portlets.user.td.expressionwidget.client.expression.ReplaceWidget;
import org.gcube.portlets.user.td.expressionwidget.client.properties.ColumnDataPropertiesCombo;
import org.gcube.portlets.user.td.expressionwidget.client.resources.ExpressionResources;
import org.gcube.portlets.user.td.expressionwidget.client.type.ReplaceExpressionType;
import org.gcube.portlets.user.td.expressionwidget.shared.exception.ConditionTypeMapException;
import org.gcube.portlets.user.td.expressionwidget.shared.exception.ReplaceTypeMapException;
import org.gcube.portlets.user.td.monitorwidget.client.utils.UtilsGXT3;
import org.gcube.portlets.user.td.widgetcommonevent.client.event.ExpressionWrapperEvent;
import org.gcube.portlets.user.td.widgetcommonevent.client.expression.C_ExpressionContainer;
import org.gcube.portlets.user.td.widgetcommonevent.client.expression.ExpressionWrapper;
import org.gcube.portlets.user.td.widgetcommonevent.shared.expression.C_Expression;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnData;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnDataType;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnTypeCode;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.web.bindery.event.shared.EventBus;
import com.sencha.gxt.cell.core.client.ButtonCell.IconAlign;
import com.sencha.gxt.cell.core.client.form.ComboBoxCell.TriggerAction;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.core.client.util.ToggleGroup;
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
import com.sencha.gxt.widget.core.client.form.DateField;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.form.FieldSet;
import com.sencha.gxt.widget.core.client.form.Radio;
import com.sencha.gxt.widget.core.client.form.TextField;

/**
 * 
 * @author "Giancarlo Panichi" <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 * 
 */
public class ReplaceColumnByExpressionPanel extends FramedPanel {
	private static final String GEOMETRY_REGEXPR = "(\\s*POINT\\s*\\(\\s*(-)?\\d+(\\.\\d+)?\\s+(-)?\\d+(\\.\\d+)?\\s*\\)\\s*$)"
			+ "|(\\s*LINESTRING\\s*\\((\\s*(-)?\\d+(\\.\\d+)?\\s+(-)?\\d+(\\.\\d+)?\\s*,)+\\s*((-)?\\d+(\\.\\d+)?\\s+(-)?\\d+(\\.\\d+)?\\s*)\\)\\s*$)";

	private static final String WIDTH = "888px";
	private static final String HEIGHT = "454px";
	private static final String HEIGHT_REDUCE = "368px";
	private static final String PARENT_HEIGHT = "490px";
	private static final String PARENT_HEIGHT_REDUCE = "404px";

	private static final String CONDITIONWIDTH = "828px";
	private static final String CONDITIONHEIGHT = "120px";
	private static final String CONDITION_LAYOUT_WIDTH = "828px";
	private static final String REPLACEWIDTH = "852px";
	private static final String REPLACEHEIGHT = "120px";
	private static final String ALL_ROWS_FIELD_WIDTH = "842px";

	private static final String RADIO_LABEL_BY_CONDITION = "By Condition";
	private static final String RADIO_LABEL_ALL_ROWS = "All Rows";

	private EventBus eventBus;

	private ReplaceColumnByExpressionDialog parent;

	private ColumnData column;
	private ArrayList<ColumnData> columns;

	private ReplaceExpressionType replaceExpressionType;

	private DateTimeFormat sdf = DateTimeFormat.getFormat("yyyy-MM-dd");

	private TextButton btnApply;
	private TextButton btnClose;

	private ComboBox<ColumnData> comboCols;

	private FieldSet conditionsFieldSet;
	private ConditionWidget conditionWidget;

	private ReplaceWidget replaceWidget;
	private TextField replaceValue;
	private DateField replaceValueDate;

	private VerticalLayoutContainer conditionsVerticalLayout;
	private FieldLabel allRowsField;
	private boolean allRows;

	private FieldSet replaceValueFieldSet;

	public ReplaceColumnByExpressionPanel(
			ReplaceColumnByExpressionDialog parent, ColumnData column,
			ArrayList<ColumnData> columns, EventBus eventBus,
			ReplaceExpressionType replaceExpressionType) {
		super();
		this.parent = parent;
		this.column = column;
		this.columns = columns;
		this.eventBus = eventBus;
		this.replaceExpressionType = replaceExpressionType;
		Log.debug("Column: " + column);
		Log.debug("Columns: " + columns);
		init();
		create();
	}

	protected void init() {
		setWidth(WIDTH);
		calcHeight();

		setBodyBorder(false);
		setHeaderVisible(false);
		// Important: fixed rendering of widgets
		forceLayoutOnResize = true;

	}

	protected void calcHeight() {
		if (column != null && column.getDataTypeName()!=null && !column.getDataTypeName().isEmpty()
				&& (column.getDataTypeName().compareTo(
						ColumnDataType.Text.toString()) == 0
						|| column.getDataTypeName().compareTo(
								ColumnDataType.Integer.toString()) == 0
						|| column.getDataTypeName().compareTo(
								ColumnDataType.Numeric.toString()) == 0 || column
						.getDataTypeName().compareTo(
								ColumnDataType.Geometry.toString()) == 0)) {
			parent.setHeight(PARENT_HEIGHT);
			setHeight(HEIGHT);

		} else {
			parent.setHeight(PARENT_HEIGHT_REDUCE);
			setHeight(HEIGHT_REDUCE);
		}
	}

	protected void create() {
		VerticalLayoutContainer basicLayout = new VerticalLayoutContainer();
		basicLayout.setAdjustForScroll(true);
		// basicLayout.setScrollMode(ScrollMode.AUTO);

		HBoxLayoutContainer flowButton = new HBoxLayoutContainer();
		flowButton.setHBoxLayoutAlign(HBoxLayoutAlign.MIDDLE);
		flowButton.setPack(BoxLayoutPack.CENTER);

		// Properties
		FieldSet properties = new FieldSet();
		properties.setHeadingText("Properties");
		properties.setCollapsible(false);

		VerticalLayoutContainer propertiesLayout = new VerticalLayoutContainer();
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

		// Conditions
		conditionsFieldSet = new FieldSet();
		conditionsFieldSet.setHeadingText("Conditions");
		conditionsFieldSet.setCollapsible(false);

		conditionsVerticalLayout = new VerticalLayoutContainer();
		conditionsVerticalLayout.setWidth(CONDITION_LAYOUT_WIDTH);

		Radio radioAllRowsTrue = new Radio();
		radioAllRowsTrue.setBoxLabel(RADIO_LABEL_ALL_ROWS);

		Radio radioAllRowsFalse = new Radio();
		radioAllRowsFalse.setBoxLabel(RADIO_LABEL_BY_CONDITION);

		radioAllRowsTrue.setValue(true);
		allRows = true;

		ToggleGroup toggleGroup = new ToggleGroup();
		toggleGroup.add(radioAllRowsTrue);
		toggleGroup.add(radioAllRowsFalse);

		toggleGroup
				.addValueChangeHandler(new ValueChangeHandler<HasValue<Boolean>>() {

					@Override
					public void onValueChange(
							ValueChangeEvent<HasValue<Boolean>> event) {
						callAllRowChange(event);

					}
				});

		HorizontalPanel hp = new HorizontalPanel();
		hp.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		hp.add(radioAllRowsTrue);
		hp.add(radioAllRowsFalse);
		hp.setWidth("140px");

		allRowsField = new FieldLabel(hp, "Select");
		allRowsField.setWidth(ALL_ROWS_FIELD_WIDTH);

		conditionWidget = new ConditionWidget(column, CONDITIONWIDTH,
				CONDITIONHEIGHT);
		conditionWidget.setEnabled(false);

		conditionsVerticalLayout.add(allRowsField, new VerticalLayoutData(100,
				-1, new Margins(0)));
		conditionsVerticalLayout.add(conditionWidget, new VerticalLayoutData(
				-1, -1, new Margins(0)));

		conditionsFieldSet.add(conditionsVerticalLayout);

		// Value
		replaceValueFieldSet = new FieldSet();
		replaceValueFieldSet.setHeadingText("Replace Value");
		replaceValueFieldSet.setCollapsible(false);

		calcReplaceValue();

		//
		btnApply = new TextButton("Apply");
		btnApply.setIcon(ExpressionResources.INSTANCE
				.columnReplaceByExpression());
		btnApply.setIconAlign(IconAlign.RIGHT);
		btnApply.setToolTip("Apply replace by expression");
		btnApply.addSelectHandler(new SelectHandler() {

			public void onSelect(SelectEvent event) {
				Log.debug("Pressed Apply");
				applyReplaceColumnByExpression();

			}
		});
		if (column == null) {
			btnApply.disable();
		}

		btnClose = new TextButton("Close");
		btnClose.setIcon(ExpressionResources.INSTANCE.close());
		btnClose.setIconAlign(IconAlign.RIGHT);
		btnClose.setToolTip("Close");
		btnClose.addSelectHandler(new SelectHandler() {

			public void onSelect(SelectEvent event) {
				Log.debug("Pressed Close");
				close();
			}
		});
		flowButton.add(btnApply, new BoxLayoutData(new Margins(2, 4, 2, 4)));
		flowButton.add(btnClose, new BoxLayoutData(new Margins(2, 4, 2, 4)));

		// Add to basic layout
		basicLayout.add(properties, new VerticalLayoutData(1, -1,
				new Margins(1)));
		basicLayout.add(conditionsFieldSet, new VerticalLayoutData(1, -1,
				new Margins(1)));
		basicLayout.add(replaceValueFieldSet, new VerticalLayoutData(1, -1,
				new Margins(1)));

		basicLayout.add(flowButton, new VerticalLayoutData(1, 36, new Margins(
				5, 2, 5, 2)));
		add(basicLayout);

	}

	protected void calcReplaceValue() {
		replaceValueFieldSet.clear();
		VerticalLayoutContainer replaceValueFieldSetLayout = new VerticalLayoutContainer();
		replaceValueFieldSet.add(replaceValueFieldSetLayout);

		if (column == null || column.getDataTypeName()==null ||column.getDataTypeName().isEmpty()) {
			replaceValue = new TextField();
			replaceValue.setToolTip("Replace Value");
			replaceValue.setValue("");
			replaceValueFieldSetLayout.add(new FieldLabel(replaceValue,
					"Replace Value"), new VerticalLayoutData(1, -1));
		} else {
			if (column.getDataTypeName().compareTo(
					ColumnDataType.Date.toString()) == 0) {
				replaceValueDate = new DateField();
				replaceValueDate.setToolTip("Replace Value");
				replaceValueFieldSetLayout.add(new FieldLabel(replaceValueDate,
						"Replace Value"), new VerticalLayoutData(1, -1));

			} else {
				if (column.getDataTypeName().compareTo(
						ColumnDataType.Text.toString()) == 0
						|| column.getDataTypeName().compareTo(
								ColumnDataType.Integer.toString()) == 0
						|| column.getDataTypeName().compareTo(
								ColumnDataType.Numeric.toString()) == 0
						|| column.getDataTypeName().compareTo(
								ColumnDataType.Geometry.toString()) == 0) {
					switch (replaceExpressionType) {	
					case Replace:
						replaceWidget = new ReplaceWidget(column, REPLACEWIDTH,
								REPLACEHEIGHT, eventBus);
						break;
					case Template:
						replaceWidget = new ReplaceWidget(column, columns,
								REPLACEWIDTH, REPLACEHEIGHT, eventBus);
						break;
					default:
						Log.debug("Attention Replace Widget have not a valid type");
						UtilsGXT3
								.alert("Attention",
										"Attention Replace Widget have not a valid type");
						return;

					}
					replaceValueFieldSetLayout.add(replaceWidget,
							new VerticalLayoutData(1, -1));
				} else {
					replaceValue = new TextField();
					replaceValue.setToolTip("Replace Value");
					replaceValue.setValue("");
					replaceValueFieldSetLayout.add(new FieldLabel(replaceValue,
							"Replace Value"), new VerticalLayoutData(1, -1));
				}
			}
		}
	}

	protected void setSelectedColumn() {
		if (column == null) {

		} else {
			
			ColumnTypeCode typeCode = ColumnTypeCode
					.getColumnTypeCodeFromId(column.getTypeCode());
			HTML errorMessage;
			if (typeCode == null) {
				Log.debug(
						"This column has column type code null, Dimension and TimeDimesion not supported for now!");
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
					calcReplaceValue();

				} else {
					column = null;
					btnApply.disable();
					conditionWidget.update(null);
					calcReplaceValue();
				}
				calcHeight();
				parent.forceLayout();
			}
		};

		return selectionHandler;
	}

	protected void callAllRowChange(ValueChangeEvent<HasValue<Boolean>> event) {
		ToggleGroup group = (ToggleGroup) event.getSource();
		Radio radio = (Radio) group.getValue();
		if (radio.getBoxLabel().compareTo(RADIO_LABEL_ALL_ROWS) == 0) {
			conditionWidget.setEnabled(false);
			allRows = true;
		} else {
			conditionWidget.setEnabled(true);
			allRows = false;
		}

	}

	protected void applyReplaceColumnByExpression() {
		C_Expression cConditionExpression = null;
		if (allRows) {

		} else {

			try {
				cConditionExpression = conditionWidget.getExpression();
			} catch (ConditionTypeMapException e) {
				Log.debug(e.getLocalizedMessage());
				UtilsGXT3.alert("Attention", e.getLocalizedMessage());
				return;
			}

		}
		String value = null;
		if (column.getDataTypeName().compareTo(ColumnDataType.Date.toString()) == 0) {

			Date valueDate = replaceValueDate.getCurrentValue();
			if (valueDate == null) {
				UtilsGXT3.alert("Error replace value",
						"Select a valid date as replace value!");
				return;
			} else {
				try {
					value = sdf.format(valueDate);
				} catch (Throwable e) {
					UtilsGXT3.alert("Error replace value",
							"Select a valid date as replace value!");
					return;
				}
				callApplyReplaceColumnByExpression(cConditionExpression, value);

			}

		} else {
			if (column.getDataTypeName().compareTo(
					ColumnDataType.Text.toString()) == 0
					|| column.getDataTypeName().compareTo(
							ColumnDataType.Integer.toString()) == 0
					|| column.getDataTypeName().compareTo(
							ColumnDataType.Numeric.toString()) == 0
					|| column.getDataTypeName().compareTo(
							ColumnDataType.Geometry.toString()) == 0) {
				C_Expression cReplaceExpression = null;
				try {
					cReplaceExpression = replaceWidget.getExpression();
					if (cReplaceExpression == null) {
						UtilsGXT3.alert("Attention",
								"Replace expression is not valid!");
						return;
					}
				} catch (ReplaceTypeMapException e) {
					UtilsGXT3.alert("Attention", e.getLocalizedMessage());
					return;
				}
				callApplyReplaceColumnByExpression(cConditionExpression,
						cReplaceExpression);
			} else {
				value = replaceValue.getCurrentValue();
				if (checkValue(value)) {
					callApplyReplaceColumnByExpression(cConditionExpression,
							value);
				} else {
					return;
				}
			}
		}

	}

	protected void callApplyReplaceColumnByExpression(
			C_Expression cConditionExpression, String replaceValue) {
		if (column == null) {
			UtilsGXT3.alert("Attention", "Select a valid column!");
			return;
		}

		switch (replaceExpressionType) {
		case Template:
			C_ExpressionContainer conditionExpressionContainer;
			if (allRows) {
				conditionExpressionContainer = new C_ExpressionContainer(
						C_ExpressionContainer.Contains.C_Expression, true,
						null);
			} else {
				conditionExpressionContainer = new C_ExpressionContainer(
						C_ExpressionContainer.Contains.C_Expression,
						cConditionExpression);
			}
			
			ExpressionWrapper exWrapper = new ExpressionWrapper(
					column.getTrId(),column,
					conditionExpressionContainer, replaceValue);
			ExpressionWrapperEvent expressionEvent = new ExpressionWrapperEvent(
					exWrapper);
			Log.debug(expressionEvent.toString());
			parent.hide();
			eventBus.fireEvent(expressionEvent);
			break;
		case Replace:
			parent.applyReplaceColumnByExpression(column, allRows,
					cConditionExpression, replaceValue);
			break;
		default:
			break;
		}

	}

	protected void callApplyReplaceColumnByExpression(
			C_Expression cConditionExpression, C_Expression cReplaceExpression) {
		if (column == null) {
			UtilsGXT3.alert("Attention", "Select a valid column!");
			return;
		}

		switch (replaceExpressionType) {
		case Template:
			C_ExpressionContainer conditionExpressionContainer;
			if (allRows) {
				conditionExpressionContainer = new C_ExpressionContainer(
						C_ExpressionContainer.Contains.C_Expression, true,
						null);
			} else {
				conditionExpressionContainer = new C_ExpressionContainer(
						C_ExpressionContainer.Contains.C_Expression,
						cConditionExpression);
			}
			C_ExpressionContainer replaceExpressionContainer = new C_ExpressionContainer(
					C_ExpressionContainer.Contains.C_Expression,
					cReplaceExpression);

			ExpressionWrapper exWrapper = new ExpressionWrapper(
					column.getTrId(), column,
					conditionExpressionContainer, replaceExpressionContainer);
			ExpressionWrapperEvent expressionEvent = new ExpressionWrapperEvent(
					exWrapper);

			Log.debug(expressionEvent.toString());
			parent.hide();
			eventBus.fireEvent(expressionEvent);
			break;
		case Replace:
			parent.applyReplaceColumnByExpression(column, allRows,
					cConditionExpression, cReplaceExpression);
			break;
		default:
			break;
		}
	}

	private boolean checkValue(String value) {
		try {
			boolean ok = false;
			ColumnDataType columnDataType = ColumnDataType
					.getColumnDataTypeFromId(column.getDataTypeName());
			switch (columnDataType) {
			case Boolean:
				Boolean.valueOf(value);
				ok = true;
				break;
			case Date:
				ok = true;
				break;
			case Geometry:
				try {
					RegExp regExp = RegExp.compile(GEOMETRY_REGEXPR);
					MatchResult matcher = regExp.exec(value);
					boolean matchFound = matcher != null;
					if (matchFound) {
						ok = true;
					} else {
						ok = false;
					}

				} catch (Throwable e) {
					UtilsGXT3.alert("Error replace value",
							"Insert a valid replace value for Geometry type! ");
					e.printStackTrace();
					return false;
				}
				break;
			case Integer:
				try {
					Integer.parseInt(value);
				} catch (Throwable e) {
					e.printStackTrace();
					UtilsGXT3.alert("Error replace value",
							"Insert a valid replace value for Integer type! ");
					return false;
				}
				ok = true;
				break;
			case Numeric:
				try {
					Double.parseDouble(value);
				} catch (Throwable e) {
					e.printStackTrace();
					UtilsGXT3.alert("Error replace value",
							"Insert a valid replace value for Numeric type! ");
					return false;
				}
				ok = true;
				break;
			case Text:
				ok = true;
				break;
			default:
				break;
			}
			return ok;

		} catch (Throwable e) {
			e.printStackTrace();
			UtilsGXT3.alert("Error replace value",
					"Insert a valid replace value! " + e.getLocalizedMessage());

			return false;
		}
	}

	protected void close() {
		switch (replaceExpressionType) {
		case Template:
			ExpressionWrapper exWrapper=new ExpressionWrapper();
			ExpressionWrapperEvent expressionEvent = new ExpressionWrapperEvent(
					exWrapper);
			Log.debug(expressionEvent.toString());
			parent.close();
			eventBus.fireEvent(expressionEvent);
			break;
		case Replace:
			if (parent != null) {
				parent.close();
			}
			break;
		default:
			break;

		}

	}

}
