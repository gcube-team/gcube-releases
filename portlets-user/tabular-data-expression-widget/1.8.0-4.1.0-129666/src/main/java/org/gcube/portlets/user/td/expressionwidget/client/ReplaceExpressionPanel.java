package org.gcube.portlets.user.td.expressionwidget.client;

import java.util.ArrayList;
import java.util.Date;

import org.gcube.portlets.user.td.expressionwidget.client.expression.ReplaceWidget;
import org.gcube.portlets.user.td.expressionwidget.client.notification.ExpressionWrapperNotification;
import org.gcube.portlets.user.td.expressionwidget.client.resources.ExpressionResources;
import org.gcube.portlets.user.td.expressionwidget.client.type.ReplaceExpressionType;
import org.gcube.portlets.user.td.expressionwidget.shared.exception.ReplaceTypeMapException;
import org.gcube.portlets.user.td.monitorwidget.client.utils.UtilsGXT3;
import org.gcube.portlets.user.td.widgetcommonevent.client.expression.C_ExpressionContainer;
import org.gcube.portlets.user.td.widgetcommonevent.client.expression.ExpressionWrapper;
import org.gcube.portlets.user.td.widgetcommonevent.shared.expression.C_Expression;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnData;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnDataType;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnTypeCode;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.user.client.ui.HTML;
import com.google.web.bindery.event.shared.EventBus;
import com.sencha.gxt.cell.core.client.ButtonCell.IconAlign;
import com.sencha.gxt.core.client.util.Margins;
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
import com.sencha.gxt.widget.core.client.form.DateField;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.form.FieldSet;
import com.sencha.gxt.widget.core.client.form.TextField;

/**
 * 
 * @author "Giancarlo Panichi" <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 * 
 */
public class ReplaceExpressionPanel extends FramedPanel {
	private static final String GEOMETRY_REGEXPR = "(\\s*POINT\\s*\\(\\s*(-)?\\d+(\\.\\d+)?\\s+(-)?\\d+(\\.\\d+)?\\s*\\)\\s*$)"
			+ "|(\\s*LINESTRING\\s*\\((\\s*(-)?\\d+(\\.\\d+)?\\s+(-)?\\d+(\\.\\d+)?\\s*,)+\\s*((-)?\\d+(\\.\\d+)?\\s+(-)?\\d+(\\.\\d+)?\\s*)\\)\\s*$)";

	private static final String WIDTH = "888px";
	private static final String HEIGHT = "324px";
	private static final String HEIGHT_REDUCE = "238px";
	private static final String PARENT_HEIGHT = "360px";
	private static final String PARENT_HEIGHT_REDUCE = "274px";

	private static final String REPLACEWIDTH = "852px";
	private static final String REPLACEHEIGHT = "120px";

	private EventBus eventBus;

	private ReplaceExpressionDialog parent;

	private ColumnData column;
	private ArrayList<ColumnData> columns;

	private DateTimeFormat sdf = DateTimeFormat.getFormat("yyyy-MM-dd");

	private TextButton btnApply;
	private TextButton btnClose;

	private TextField columnLabel;
	private TextField columnType;
	private TextField dataType;

	private ReplaceWidget replaceWidget;
	private TextField replaceValue;
	private DateField replaceValueDate;

	private FieldSet replaceValueFieldSet;

	private ReplaceExpressionType type;

	

	public ReplaceExpressionPanel(ReplaceExpressionDialog parent,
			ColumnData column, ArrayList<ColumnData> columns,
			ReplaceExpressionType type, EventBus eventBus) {
		super();
		this.parent = parent;
		this.column = column;
		this.columns = columns;
		this.type = type;
		this.eventBus = eventBus;
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
		if (column != null
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

		columnLabel = new TextField();
		columnLabel.setToolTip("The label of column");
		columnLabel.setReadOnly(true);
		propertiesLayout.add(new FieldLabel(columnLabel, "Label"),
				new VerticalLayoutData(1, -1));
		
		
		columnType = new TextField();
		columnType.setToolTip("The type of column");
		columnType.setReadOnly(true);
		propertiesLayout.add(new FieldLabel(columnType, "Column Type"),
				new VerticalLayoutData(1, -1));

		dataType = new TextField();
		dataType.setToolTip("The data type");
		dataType.setReadOnly(true);
		propertiesLayout.add(new FieldLabel(dataType, "Data Type"),
				new VerticalLayoutData(1, -1));

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
		btnApply.setTitle("Apply replace by expression");
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
		btnClose.setTitle("Close");
		btnClose.addSelectHandler(new SelectHandler() {

			public void onSelect(SelectEvent event) {
				Log.debug("Pressed Close");
				parent.fireAborted();
				close();
			}
		});
		flowButton.add(btnApply, new BoxLayoutData(new Margins(2, 4, 2, 4)));
		flowButton.add(btnClose, new BoxLayoutData(new Margins(2, 4, 2, 4)));

		// Add to basic layout
		basicLayout.add(properties, new VerticalLayoutData(1, -1,
				new Margins(1)));
		basicLayout.add(replaceValueFieldSet, new VerticalLayoutData(1, -1,
				new Margins(1)));

		basicLayout.add(flowButton, new VerticalLayoutData(1, 36, new Margins(
				5, 2, 5, 2)));
		add(basicLayout);

		setSelectedColumn();

	}

	protected void calcReplaceValue() {
		replaceValueFieldSet.clear();
		VerticalLayoutContainer replaceValueFieldSetLayout = new VerticalLayoutContainer();
		replaceValueFieldSet.add(replaceValueFieldSetLayout);

		if (column == null) {
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
					switch(type){
					case AddColumn:
						replaceWidget = new ReplaceWidget(column, columns,
								REPLACEWIDTH, REPLACEHEIGHT, eventBus,type);
						break;
					case Replace:
						replaceWidget = new ReplaceWidget(column, columns,
								REPLACEWIDTH, REPLACEHEIGHT, eventBus, type);
						break;
					case Template:
						replaceWidget = new ReplaceWidget(column, columns,
								REPLACEWIDTH, REPLACEHEIGHT, eventBus);
						break;
					default:
						break;
					
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
				columnLabel.setValue(column.getLabel());
				columnType.setValue(column.getTypeCode());
				dataType.setValue(column.getDataTypeName());
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

	protected void applyReplaceColumnByExpression() {
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
				callApplyReplaceColumnByExpression(value);
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
				callApplyReplaceColumnByExpression(cReplaceExpression);
			} else {
				value = replaceValue.getCurrentValue();
				if (checkValue(value)) {
					callApplyReplaceColumnByExpression(value);
				} else {
					return;
				}
			}
		}

	}

	protected void callApplyReplaceColumnByExpression(String replaceValue) {
		if (column == null) {
			UtilsGXT3.alert("Attention", "Select a valid column!");
			return;
		}

		if (type == null) {
			UtilsGXT3.alert("Attention", "Type is null!");
			return;
		}
		
		ExpressionWrapper exWrapper;
		ExpressionWrapperNotification expressionWrapperNotification;
		
		switch (type) {
		case Template:
			exWrapper = new ExpressionWrapper(
					replaceValue, column.getTrId(), column);
			expressionWrapperNotification=new ExpressionWrapperNotification(exWrapper);
			parent.applyReplaceColumnByExpression(expressionWrapperNotification);
			close();
			break;
		case Replace:
		case AddColumn:	
			exWrapper = new ExpressionWrapper(
					replaceValue, column.getTrId(), column);
			expressionWrapperNotification=new ExpressionWrapperNotification(exWrapper);
			parent.applyReplaceColumnByExpression(expressionWrapperNotification);
			break;
		default:
			break;
		}
	}

	protected void callApplyReplaceColumnByExpression(
			C_Expression cReplaceExpression) {
		if (column == null) {
			UtilsGXT3.alert("Attention", "Select a valid column!");
			return;
		}

		if (type == null) {
			UtilsGXT3.alert("Attention", "Type is null!");
			return;
		}
		
		ExpressionWrapper exWrapper;
		ExpressionWrapperNotification expressionWrapperNotification;
		C_ExpressionContainer replaceExpressionContainer;
		switch (type) {
		case Template:
			replaceExpressionContainer = new C_ExpressionContainer(
					C_ExpressionContainer.Contains.C_Expression,
					cReplaceExpression);			
		    exWrapper = new ExpressionWrapper(
					replaceExpressionContainer, column.getTrId(), column);
			expressionWrapperNotification=new ExpressionWrapperNotification(exWrapper);
			parent.applyReplaceColumnByExpression(expressionWrapperNotification);
			break;
		case Replace:
		case AddColumn:	
			replaceExpressionContainer = new C_ExpressionContainer(
					C_ExpressionContainer.Contains.C_Expression,
					cReplaceExpression);			
		    exWrapper = new ExpressionWrapper(
					replaceExpressionContainer, column.getTrId(), column);
			expressionWrapperNotification=new ExpressionWrapperNotification(exWrapper);
			parent.applyReplaceColumnByExpression(expressionWrapperNotification);
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
		if (parent != null) {
			parent.close();
		}

	}

}
