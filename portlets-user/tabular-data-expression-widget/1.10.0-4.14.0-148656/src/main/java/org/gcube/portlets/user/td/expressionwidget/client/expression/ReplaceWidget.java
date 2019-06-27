package org.gcube.portlets.user.td.expressionwidget.client.expression;

import java.util.ArrayList;

import org.gcube.portlets.user.td.expressionwidget.client.properties.ColumnDataPropertiesCombo;
import org.gcube.portlets.user.td.expressionwidget.client.properties.ReplaceElement;
import org.gcube.portlets.user.td.expressionwidget.client.properties.ReplaceElementProperties;
import org.gcube.portlets.user.td.expressionwidget.client.properties.ReplaceElementStore;
import org.gcube.portlets.user.td.expressionwidget.client.type.ReplaceExpressionType;
import org.gcube.portlets.user.td.expressionwidget.client.utils.UtilsGXT3;
import org.gcube.portlets.user.td.expressionwidget.shared.exception.ReplaceTypeMapException;
import org.gcube.portlets.user.td.expressionwidget.shared.model.composite.arithmetic.C_ArithmeticExpression;
import org.gcube.portlets.user.td.expressionwidget.shared.replace.ReplaceType;
import org.gcube.portlets.user.td.expressionwidget.shared.replace.ReplaceTypeMap;
import org.gcube.portlets.user.td.gwtservice.client.rpc.TDGWTServiceAsync;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTIsFinalException;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTIsLockedException;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTSessionExpiredException;
import org.gcube.portlets.user.td.widgetcommonevent.client.event.SessionExpiredEvent;
import org.gcube.portlets.user.td.widgetcommonevent.client.type.SessionExpiredType;
import org.gcube.portlets.user.td.widgetcommonevent.shared.TRId;
import org.gcube.portlets.user.td.widgetcommonevent.shared.expression.C_Expression;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnData;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnDataType;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.event.shared.EventBus;
import com.sencha.gxt.cell.core.client.form.ComboBoxCell.TriggerAction;
import com.sencha.gxt.core.client.dom.ScrollSupport.ScrollMode;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.widget.core.client.container.BoxLayoutContainer.BoxLayoutData;
import com.sencha.gxt.widget.core.client.container.HBoxLayoutContainer;
import com.sencha.gxt.widget.core.client.container.MarginData;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.event.BeforeShowEvent;
import com.sencha.gxt.widget.core.client.form.ComboBox;
import com.sencha.gxt.widget.core.client.form.TextField;

/**
 * 
 * @author Giancarlo Panichi
 *
 * 
 */
public class ReplaceWidget extends SimpleContainer {

	private static final String EMPTY_TEXT_TO_STRING = "to string...";
	private static final String EMPTY_TEXT_FROM_STRING = "from string...";
	private static final String EMPTY_TEXT_TO_INDEX = "to index...";
	private static final String EMPTY_TEXT_FROM_INDEX = "from index...";
	private static final String EMPTY_TEXT_REGEXP = "regexp...";
	private static final String EMPTY_TEXT_INSERT_A_STRING = "insert a string...";
	private static final String EMPTY_TEXT_REPLACE_REGEXP = "regexp...";
	private static final String EMPTY_TEXT_REPLACE_REPLACING = "replace with...";

	
	private ReplaceWidget thisCont;

	private static final String HEIGHT = "210px";
	private static final String WIDTH = "832px";
	private static final String COMBO_WIDTH = "170px";

	private EventBus eventBus;
	private TRId trId;
	private ColumnData column;
	private ArrayList<ColumnData> columns;
	private ArrayList<ColumnData> arithmeticColumns;

	// private FieldLabel matchLabel;
	// private ToggleGroup groupMatch;

	private VerticalLayoutContainer vert;
	// protected ColumnData column;

	private String readableExpression;
	private String itemIdComboConcat;
	private String itemIdComboLeaf;
	private String itemIdComboColumns;
	private String itemIdFirstArg;
	private String itemIdSecondArg;
	private String itemIdHoriz;
	private ReplaceExpressionType replaceExpressionType;
	private ColumnDataType targetColumnType;
	

	protected class ExpressionContainer {
		private C_Expression expression;
		private String readableExpression;

		public ExpressionContainer(C_Expression expression,
				String readableExpression) {
			super();
			this.expression = expression;
			this.readableExpression = readableExpression;
		}

		public C_Expression getExpression() {
			return expression;
		}

		public void setExpression(C_Expression expression) {
			this.expression = expression;
		}

		public String getReadableExpression() {
			return readableExpression;
		}

		public void setReadableExpression(String readableExpression) {
			this.readableExpression = readableExpression;
		}

	}

	public ReplaceWidget(ColumnData column, EventBus eventBus) {
		super();
		this.column=column;
		replaceExpressionType = ReplaceExpressionType.Replace;
		ColumnDataType targetType = ColumnDataType
				.getColumnDataTypeFromId(column.getDataTypeName());
		create(column.getTrId(), targetType, WIDTH, HEIGHT, eventBus);
		retrieveColumns();
	}

	public ReplaceWidget(ColumnData column, String width, String height,
			EventBus eventBus) {
		super();
		this.column=column;
		replaceExpressionType = ReplaceExpressionType.Replace;
		ColumnDataType targetType = ColumnDataType
				.getColumnDataTypeFromId(column.getDataTypeName());
		create(column.getTrId(), targetType, width, height, eventBus);
		retrieveColumns();
	}

	public ReplaceWidget(ColumnData column, ArrayList<ColumnData> columns,
			EventBus eventBus) {
		super();
		this.column=column;
		replaceExpressionType = ReplaceExpressionType.Template;
		ColumnDataType targetType = ColumnDataType
				.getColumnDataTypeFromId(column.getDataTypeName());
		create(column.getTrId(), targetType, WIDTH, HEIGHT, eventBus);
		columnsConfig(columns);
	}

	public ReplaceWidget(ColumnData column, ArrayList<ColumnData> columns,
			String width, String height, EventBus eventBus) {
		super();
		this.column=column;
		replaceExpressionType = ReplaceExpressionType.Template;
		ColumnDataType targetType = ColumnDataType
				.getColumnDataTypeFromId(column.getDataTypeName());
		create(column.getTrId(), targetType, width, height, eventBus);
		columnsConfig(columns);
	}
	
	public ReplaceWidget(ColumnData column, ArrayList<ColumnData> columns,
			EventBus eventBus, ReplaceExpressionType type) {
		super();
		this.column=column;
		replaceExpressionType = type;
		ColumnDataType targetType = ColumnDataType
				.getColumnDataTypeFromId(column.getDataTypeName());
		create(column.getTrId(), targetType, WIDTH, HEIGHT, eventBus);
		columnsConfig(columns);
	}

	public ReplaceWidget(ColumnData column, ArrayList<ColumnData> columns,
			String width, String height, EventBus eventBus,ReplaceExpressionType type) {
		super();
		this.column=column;
		replaceExpressionType = type;
		ColumnDataType targetType = ColumnDataType
				.getColumnDataTypeFromId(column.getDataTypeName());
		create(column.getTrId(), targetType, width, height, eventBus);
		columnsConfig(columns);
	}
	

	protected void create(TRId trId, ColumnDataType targetColumnType,
			String width, String height, EventBus eventBus) {
		this.trId = trId;
		this.targetColumnType = targetColumnType;
		setBorders(true);
		setWidth(width);
		setHeight(height);
		forceLayoutOnResize = true;
		thisCont = this;

		addBeforeShowHandler(new BeforeShowEvent.BeforeShowHandler() {

			@Override
			public void onBeforeShow(BeforeShowEvent event) {
				forceLayout();

			}
		});

	}

	protected void retrieveColumns() {
		TDGWTServiceAsync.INSTANCE.getColumns(trId,
				new AsyncCallback<ArrayList<ColumnData>>() {

					public void onFailure(Throwable caught) {
						if (caught instanceof TDGWTSessionExpiredException) {
							eventBus.fireEvent(new SessionExpiredEvent(
									SessionExpiredType.EXPIREDONSERVER));
						} else {
							if (caught instanceof TDGWTIsLockedException) {
								Log.error(caught.getLocalizedMessage());
								UtilsGXT3.alert("Error Locked",
										caught.getLocalizedMessage());
							} else {
								if (caught instanceof TDGWTIsFinalException) {
									Log.error(caught.getLocalizedMessage());
									UtilsGXT3.alert("Error Final",
											caught.getLocalizedMessage());
								} else {
									Log.error("load combo failure:"
											+ caught.getLocalizedMessage());
									UtilsGXT3.alert("Error",
											"Error retrieving columns of tabular resource:"
													+ trId.getId());
								}
							}
						}
					}

					public void onSuccess(ArrayList<ColumnData> result) {
						Log.trace("loaded " + result.size() + " ColumnData");
						columnsConfig(result);
					}

				});
	}

	protected void columnsConfig(ArrayList<ColumnData> result) {
		columns = result;
		arithmeticColumns = new ArrayList<ColumnData>();
		for (ColumnData col : result) {
			ColumnDataType colDataType = ColumnDataType
					.getColumnDataTypeFromId(col.getDataTypeName());
			if (C_ArithmeticExpression.isAccepted(colDataType)) {
				arithmeticColumns.add(col);
			}

		}
		setup();
	}

	protected void setup() {
		String sign="columnX";
		
		if(column!=null&& column.getName()!=null){
			sign=column.getName();
		} 
		
		itemIdComboConcat = "ComboConcat" + sign;
		itemIdComboLeaf = "ComboLeaf" + sign;
		itemIdComboColumns = "ComboColumns" + sign;
		itemIdFirstArg = "FirstArg" + sign;
		itemIdSecondArg = "SecondArg" + sign;
		itemIdHoriz = "Horiz" + sign;

		vert = new VerticalLayoutContainer();
		vert.setScrollMode(ScrollMode.AUTO);// Set In GXT 3.0.1
		vert.setAdjustForScroll(true);
		
		
		final HBoxLayoutContainer horiz = new HBoxLayoutContainer();

		final TextField firstArg = new TextField();
		firstArg.setEmptyText("Insert a string");
		firstArg.setItemId(itemIdFirstArg);

		final TextField secondArg = new TextField();
		secondArg.setEmptyText("");
		secondArg.setItemId(itemIdSecondArg);

		// Combo Column
		ColumnDataPropertiesCombo propsColumnData = GWT
				.create(ColumnDataPropertiesCombo.class);
		final ListStore<ColumnData> storeColumns = new ListStore<ColumnData>(
				propsColumnData.id());
		Log.debug("Store Columns: " + storeColumns);
		storeColumns.addAll(columns);
		
		final ComboBox<ColumnData> comboColumns = new ComboBox<ColumnData>(
				storeColumns, propsColumnData.label());

		Log.debug("Combo Columns created");

		comboColumns.setEmptyText("Select Column...");
		comboColumns.setItemId(itemIdComboColumns);
		comboColumns.setWidth(COMBO_WIDTH);
		comboColumns.setEditable(false);

		comboColumns.setTriggerAction(TriggerAction.ALL);

		// Replace Elemet Store
		final ReplaceElementStore factory = new ReplaceElementStore();

		ReplaceElementProperties props = GWT
				.create(ReplaceElementProperties.class);
		Log.debug("Props: " + props);

		// Combo Leaf
		ListStore<ReplaceElement> storeReplaceElementsLeaf = new ListStore<ReplaceElement>(
				props.id());
		Log.debug("Store Leaf: " + storeReplaceElementsLeaf);
		storeReplaceElementsLeaf.addAll(factory.replaceElements);

		Log.debug("Store created");
		final ComboBox<ReplaceElement> comboReplaceElementsLeaf = new ComboBox<ReplaceElement>(
				storeReplaceElementsLeaf, props.label());

		Log.debug("Combo created");

		comboReplaceElementsLeaf
				.addSelectionHandler(new SelectionHandler<ReplaceElement>() {

					public void onSelection(SelectionEvent<ReplaceElement> event) {
						if (event.getSelectedItem() != null) {
							ReplaceElement re = event.getSelectedItem();
							Log.debug("Condition selected:" + re.toString());
							switch (re.getReplaceType()) {
							case Value:
								comboColumns.clear();
								comboColumns.setVisible(false);
								firstArg.setVisible(true);
								firstArg.setEmptyText(EMPTY_TEXT_INSERT_A_STRING);
								secondArg.setVisible(false);
								secondArg.setEmptyText("");
								break;
							case ColumnValue:
							case Upper:
							case Lower:
							case Trim:
							case MD5:
								comboColumns.clear();
								comboColumns.setVisible(true);
								firstArg.setVisible(false);
								firstArg.setEmptyText("");
								secondArg.setVisible(false);
								secondArg.setEmptyText("");
								break;
							case Concat:
							case Addition:
							case Subtraction:
							case Modulus:
							case Multiplication:
							case Division:
								break;
							case SubstringByRegex:
								comboColumns.clear();
								comboColumns.setVisible(true);
								firstArg.setVisible(true);
								firstArg.setEmptyText(EMPTY_TEXT_REGEXP);
								secondArg.setVisible(false);
								secondArg.setEmptyText("");
								break;
							case SubstringByIndex:
								comboColumns.clear();
								comboColumns.setVisible(true);
								firstArg.setVisible(true);
								firstArg.setEmptyText(EMPTY_TEXT_FROM_INDEX);
								secondArg.setVisible(true);
								secondArg.setEmptyText(EMPTY_TEXT_TO_INDEX);
								break;
							case SubstringByCharSeq:
								comboColumns.clear();
								comboColumns.setVisible(true);
								firstArg.setVisible(true);
								firstArg.setEmptyText(EMPTY_TEXT_FROM_STRING);
								secondArg.setVisible(true);
								secondArg.setEmptyText(EMPTY_TEXT_TO_STRING);
								break;
							case TextReplaceMatchingRegex:
								comboColumns.clear();
								comboColumns.setVisible(true);
								firstArg.setVisible(true);
								firstArg.setEmptyText(EMPTY_TEXT_REPLACE_REGEXP);
								secondArg.setVisible(true);
								secondArg
										.setEmptyText(EMPTY_TEXT_REPLACE_REPLACING);
								break;
							default:
								break;
							}

							vert.forceLayout();
							thisCont.forceLayout();

						}
					}

				});

		comboReplaceElementsLeaf.setEmptyText("Select...");
		comboReplaceElementsLeaf.setItemId(itemIdComboLeaf);
		comboReplaceElementsLeaf.setWidth(COMBO_WIDTH);
		comboReplaceElementsLeaf.setEditable(false);

		comboReplaceElementsLeaf.setTriggerAction(TriggerAction.ALL);

		// ComboOperations
		ListStore<ReplaceElement> storeReplaceElementsOperations = new ListStore<ReplaceElement>(
				props.id());
		Log.debug("Store Operations: " + storeReplaceElementsOperations);
		if (arithmeticColumns == null || arithmeticColumns.size() < 1) {
			storeReplaceElementsOperations
					.addAll(factory.replaceElementsOperationsNoArithmetic);
		} else {
			storeReplaceElementsOperations
					.addAll(factory.replaceElementsOperations);
		}
		Log.debug("Store created");
		final ComboBox<ReplaceElement> comboReplaceElementsOperations = new ComboBox<ReplaceElement>(
				storeReplaceElementsOperations, props.label());

		Log.debug("Combo created");

		comboReplaceElementsOperations
				.addSelectionHandler(new SelectionHandler<ReplaceElement>() {

					public void onSelection(SelectionEvent<ReplaceElement> event) {
						if (event.getSelectedItem() != null) {
							@SuppressWarnings("unchecked")
							ComboBox<ReplaceElement> source = (ComboBox<ReplaceElement>) event
									.getSource();
							ReplaceElement re = event.getSelectedItem();
							Log.debug("Condition selected:" + re.toString());
							switch (re.getReplaceType()) {
							case Value:
								comboReplaceElementsLeaf.setVisible(false);
								comboColumns.clear();
								comboColumns.setVisible(false);
								firstArg.setEmptyText(EMPTY_TEXT_INSERT_A_STRING);
								firstArg.setVisible(true);
								secondArg.setVisible(false);
								secondArg.setEmptyText("");
								removeOperation(source);
								break;
							case ColumnValue:
							case Upper:
							case Lower:
							case Trim:
							case MD5:
								comboReplaceElementsLeaf.setVisible(false);
								storeColumns.clear();
								storeColumns.addAll(columns);
								storeColumns.commitChanges();
								comboColumns.clear();
								comboColumns.setVisible(true);
								firstArg.setVisible(false);
								firstArg.setEmptyText("");
								secondArg.setVisible(false);
								secondArg.setEmptyText("");
								removeOperation(source);
								break;
							case Concat:
								comboReplaceElementsLeaf.clear();
								comboReplaceElementsLeaf.reset();
								comboReplaceElementsLeaf.getStore().clear();
								comboReplaceElementsLeaf.getStore().addAll(
										factory.replaceElements);
								comboReplaceElementsLeaf.getStore()
										.commitChanges();
								comboReplaceElementsLeaf.setVisible(true);
								comboReplaceElementsLeaf.redraw();
								storeColumns.clear();
								storeColumns.addAll(columns);
								storeColumns.commitChanges();
								comboColumns.clear();
								comboColumns.setVisible(false);
								firstArg.setVisible(false);
								firstArg.setEmptyText("");
								secondArg.setVisible(false);
								secondArg.setEmptyText("");
								if (!existOperation(source)) {
									addOperation();
								}
								break;
							case Addition:
							case Subtraction:
							case Modulus:
							case Multiplication:
							case Division:
								comboReplaceElementsLeaf.clear();
								comboReplaceElementsLeaf.reset();
								comboReplaceElementsLeaf.getStore().clear();
								comboReplaceElementsLeaf.getStore().addAll(
										factory.replaceElementsArithmetic);
								comboReplaceElementsLeaf.getStore()
										.commitChanges();
								comboReplaceElementsLeaf.setVisible(true);
								comboReplaceElementsLeaf.redraw();

								storeColumns.clear();
								storeColumns.addAll(arithmeticColumns);
								storeColumns.commitChanges();
								comboColumns.clear();
								comboColumns.setVisible(false);
								firstArg.setVisible(false);
								firstArg.setEmptyText("");
								secondArg.setVisible(false);
								secondArg.setEmptyText("");
								if (!existOperation(source)) {
									addOperation();
								}
								break;
							case SubstringByRegex:
								comboReplaceElementsLeaf.setVisible(false);
								storeColumns.clear();
								storeColumns.addAll(columns);
								storeColumns.commitChanges();
								comboColumns.clear();
								comboColumns.setVisible(true);
								firstArg.setVisible(true);
								firstArg.setEmptyText(EMPTY_TEXT_REGEXP);
								secondArg.setVisible(false);
								secondArg.setEmptyText("");
								removeOperation(source);
								break;
							case SubstringByIndex:
								comboReplaceElementsLeaf.setVisible(false);
								storeColumns.clear();
								storeColumns.addAll(columns);
								storeColumns.commitChanges();
								comboColumns.clear();
								comboColumns.setVisible(true);
								firstArg.setVisible(true);
								firstArg.setEmptyText(EMPTY_TEXT_FROM_INDEX);
								secondArg.setVisible(true);
								secondArg.setEmptyText(EMPTY_TEXT_TO_INDEX);
								removeOperation(source);
								break;
							case SubstringByCharSeq:
								comboReplaceElementsLeaf.setVisible(false);
								storeColumns.clear();
								storeColumns.addAll(columns);
								storeColumns.commitChanges();
								comboColumns.clear();
								comboColumns.setVisible(true);
								firstArg.setVisible(true);
								firstArg.setEmptyText(EMPTY_TEXT_FROM_STRING);
								secondArg.setVisible(true);
								secondArg.setEmptyText(EMPTY_TEXT_TO_STRING);
								removeOperation(source);
								break;
							case TextReplaceMatchingRegex:
								comboReplaceElementsLeaf.setVisible(false);
								storeColumns.clear();
								storeColumns.addAll(columns);
								storeColumns.commitChanges();
								comboColumns.clear();
								comboColumns.setVisible(true);
								firstArg.setVisible(true);
								firstArg.setEmptyText(EMPTY_TEXT_REPLACE_REGEXP);
								secondArg.setVisible(true);
								secondArg
										.setEmptyText(EMPTY_TEXT_REPLACE_REPLACING);
								removeOperation(source);
								break;
							default:
								break;
							}
							vert.forceLayout();
							thisCont.forceLayout();

						}
					}

				});

		comboReplaceElementsOperations.setEmptyText("Select...");
		comboReplaceElementsOperations.setItemId(itemIdComboConcat);
		comboReplaceElementsOperations.setWidth(COMBO_WIDTH);
		comboReplaceElementsOperations.setEditable(false);
		comboReplaceElementsOperations.setTriggerAction(TriggerAction.ALL);

		comboReplaceElementsOperations.setValue(
				storeReplaceElementsOperations.get(0), true);

		//
		horiz.add(comboReplaceElementsOperations, new BoxLayoutData(
				new Margins(0)));
		horiz.add(comboReplaceElementsLeaf, new BoxLayoutData(new Margins(0)));
		horiz.add(comboColumns, new BoxLayoutData(new Margins(0)));
		horiz.add(firstArg, new BoxLayoutData(new Margins(0)));
		horiz.add(secondArg, new BoxLayoutData(new Margins(0)));
		horiz.setItemId(itemIdHoriz);
		vert.add(horiz, new VerticalLayoutData(1, -1, new Margins(1)));

		add(vert, new MarginData(0));

		firstArg.setVisible(true);
		secondArg.setVisible(false);
		comboColumns.setVisible(false);
		comboReplaceElementsLeaf.setVisible(false);
		comboReplaceElementsOperations.setVisible(true);
		forceLayout();
	}

	protected void addOperation() {
		final HBoxLayoutContainer horiz = new HBoxLayoutContainer();

		final TextField firstArg = new TextField();
		firstArg.setItemId(itemIdFirstArg);

		final TextField secondArg = new TextField();
		secondArg.setItemId(itemIdSecondArg);

		// Combo Column
		ColumnDataPropertiesCombo propsColumnData = GWT
				.create(ColumnDataPropertiesCombo.class);
		final ListStore<ColumnData> storeColumns = new ListStore<ColumnData>(
				propsColumnData.id());
		Log.debug("Store Columns: " + storeColumns);
		storeColumns.addAll(columns);

		final ComboBox<ColumnData> comboColumns = new ComboBox<ColumnData>(
				storeColumns, propsColumnData.label());

		Log.debug("Combo Columns created");

		comboColumns.setEmptyText("Select Column...");
		comboColumns.setItemId(itemIdComboColumns);
		comboColumns.setWidth(COMBO_WIDTH);
		comboColumns.setEditable(false);

		comboColumns.setTriggerAction(TriggerAction.ALL);

		//
		final ReplaceElementStore factory = new ReplaceElementStore();

		ReplaceElementProperties props = GWT
				.create(ReplaceElementProperties.class);
		Log.debug("Props: " + props);

		// Combo Leaf
		ListStore<ReplaceElement> storeReplaceElementsLeaf = new ListStore<ReplaceElement>(
				props.id());
		Log.debug("Store Leaf: " + storeReplaceElementsLeaf);
		storeReplaceElementsLeaf.addAll(factory.replaceElements);

		Log.debug("Store created");
		final ComboBox<ReplaceElement> comboReplaceElementsLeaf = new ComboBox<ReplaceElement>(
				storeReplaceElementsLeaf, props.label());

		Log.debug("Combo created");

		comboReplaceElementsLeaf
				.addSelectionHandler(new SelectionHandler<ReplaceElement>() {

					public void onSelection(SelectionEvent<ReplaceElement> event) {
						if (event.getSelectedItem() != null) {
							ReplaceElement re = event.getSelectedItem();
							Log.debug("Condition selected:" + re.toString());
							switch (re.getReplaceType()) {
							case Value:
								comboColumns.clear();
								comboColumns.setVisible(false);
								firstArg.setVisible(true);
								firstArg.setEmptyText(EMPTY_TEXT_INSERT_A_STRING);
								secondArg.setVisible(false);
								secondArg.setEmptyText("");
								break;
							case ColumnValue:
							case Upper:
							case Lower:
							case Trim:	
							case MD5:		
								comboColumns.clear();
								comboColumns.setVisible(true);
								firstArg.setVisible(false);
								firstArg.setEmptyText("");
								secondArg.setVisible(false);
								secondArg.setEmptyText("");
								break;
							case Concat:
							case Addition:
							case Subtraction:
							case Modulus:
							case Multiplication:
							case Division:
								break;
							case SubstringByRegex:
								comboColumns.clear();
								comboColumns.setVisible(true);
								firstArg.setVisible(true);
								firstArg.setEmptyText(EMPTY_TEXT_REGEXP);
								secondArg.setVisible(false);
								secondArg.setEmptyText("");
								break;
							case SubstringByIndex:
								comboColumns.clear();
								comboColumns.setVisible(true);
								firstArg.setVisible(true);
								firstArg.setEmptyText(EMPTY_TEXT_FROM_INDEX);
								secondArg.setVisible(true);
								secondArg.setEmptyText(EMPTY_TEXT_TO_INDEX);
								break;
							case SubstringByCharSeq:
								comboColumns.clear();
								comboColumns.setVisible(true);
								firstArg.setVisible(true);
								firstArg.setEmptyText(EMPTY_TEXT_FROM_STRING);
								secondArg.setVisible(true);
								secondArg.setEmptyText(EMPTY_TEXT_TO_STRING);
								break;
							case TextReplaceMatchingRegex:
								comboColumns.clear();
								comboColumns.setVisible(true);
								firstArg.setVisible(true);
								firstArg.setEmptyText(EMPTY_TEXT_REPLACE_REGEXP);
								secondArg.setVisible(true);
								secondArg
										.setEmptyText(EMPTY_TEXT_REPLACE_REPLACING);
								break;
							default:
								break;
							}
							vert.forceLayout();
							thisCont.forceLayout();

						}
					}

				});

		comboReplaceElementsLeaf.setEmptyText("Select...");
		comboReplaceElementsLeaf.setItemId(itemIdComboLeaf);
		comboReplaceElementsLeaf.setWidth(COMBO_WIDTH);
		comboReplaceElementsLeaf.setEditable(false);

		comboReplaceElementsLeaf.setTriggerAction(TriggerAction.ALL);

		// ComboOperations
		ListStore<ReplaceElement> storeReplaceElementsOperations = new ListStore<ReplaceElement>(
				props.id());
		Log.debug("Store Concat: " + storeReplaceElementsOperations);
		if (arithmeticColumns == null || arithmeticColumns.size() < 1) {
			storeReplaceElementsOperations
					.addAll(factory.replaceElementsOperationsNoArithmetic);
		} else {
			storeReplaceElementsOperations
					.addAll(factory.replaceElementsOperations);
		}
		
		Log.debug("Store created");
		final ComboBox<ReplaceElement> comboReplaceElementsOperations = new ComboBox<ReplaceElement>(
				storeReplaceElementsOperations, props.label());

		Log.debug("Combo created");

		comboReplaceElementsOperations
				.addSelectionHandler(new SelectionHandler<ReplaceElement>() {

					public void onSelection(SelectionEvent<ReplaceElement> event) {

						if (event.getSelectedItem() != null) {
							@SuppressWarnings("unchecked")
							ComboBox<ReplaceElement> source = (ComboBox<ReplaceElement>) event
									.getSource();
							ReplaceElement re = event.getSelectedItem();
							Log.debug("Condition selected:" + re.toString());
							switch (re.getReplaceType()) {
							case Value:
								comboReplaceElementsLeaf.setVisible(false);
								comboColumns.clear();
								comboColumns.setVisible(false);
								firstArg.setEmptyText(EMPTY_TEXT_INSERT_A_STRING);
								firstArg.setVisible(true);
								secondArg.setVisible(false);
								secondArg.setEmptyText("");
								removeOperation(source);
								break;
							case ColumnValue:
							case Upper:
							case Lower:
							case Trim:	
							case MD5:		
								comboReplaceElementsLeaf.setVisible(false);
								storeColumns.clear();
								storeColumns.addAll(columns);
								storeColumns.commitChanges();
								comboColumns.clear();
								comboColumns.setVisible(true);
								firstArg.setVisible(false);
								firstArg.setEmptyText("");
								secondArg.setVisible(false);
								secondArg.setEmptyText("");
								removeOperation(source);
								break;
							case Concat:
								comboReplaceElementsLeaf.clear();
								comboReplaceElementsLeaf.reset();
								comboReplaceElementsLeaf.getStore().clear();
								comboReplaceElementsLeaf.getStore().addAll(
										factory.replaceElements);
								comboReplaceElementsLeaf.getStore()
										.commitChanges();
								comboReplaceElementsLeaf.setVisible(true);
								comboReplaceElementsLeaf.redraw();
								
								storeColumns.clear();
								storeColumns.addAll(columns);
								storeColumns.commitChanges();
								comboColumns.clear();
								comboColumns.setVisible(false);
								firstArg.setVisible(false);
								firstArg.setEmptyText("");
								secondArg.setVisible(false);
								secondArg.setEmptyText("");
								if (!existOperation(source)) {
									addOperation();
								}
								break;
							case Addition:
							case Subtraction:
							case Modulus:
							case Multiplication:
							case Division:
								comboReplaceElementsLeaf.clear();
								comboReplaceElementsLeaf.reset();
								comboReplaceElementsLeaf.getStore().clear();
								comboReplaceElementsLeaf.getStore().addAll(
										factory.replaceElementsArithmetic);
								comboReplaceElementsLeaf.getStore()
										.commitChanges();
								comboReplaceElementsLeaf.setVisible(true);
								comboReplaceElementsLeaf.redraw();
								
								storeColumns.clear();
								storeColumns.addAll(arithmeticColumns);
								storeColumns.commitChanges();
								comboColumns.clear();
								comboColumns.setVisible(false);
								firstArg.setVisible(false);
								firstArg.setEmptyText("");
								secondArg.setVisible(false);
								secondArg.setEmptyText("");
								if (!existOperation(source)) {
									addOperation();
								}
								break;
							case SubstringByRegex:
								comboReplaceElementsLeaf.setVisible(false);
								storeColumns.clear();
								storeColumns.addAll(columns);
								storeColumns.commitChanges();
								comboColumns.clear();
								comboColumns.setVisible(true);
								firstArg.setVisible(true);
								firstArg.setEmptyText(EMPTY_TEXT_REGEXP);
								secondArg.setVisible(false);
								secondArg.setEmptyText("");
								removeOperation(source);
								break;
							case SubstringByIndex:
								comboReplaceElementsLeaf.setVisible(false);
								storeColumns.clear();
								storeColumns.addAll(columns);
								storeColumns.commitChanges();
								comboColumns.clear();
								comboColumns.setVisible(true);
								firstArg.setVisible(true);
								firstArg.setEmptyText(EMPTY_TEXT_FROM_INDEX);
								secondArg.setVisible(true);
								secondArg.setEmptyText(EMPTY_TEXT_TO_INDEX);
								removeOperation(source);
								break;
							case SubstringByCharSeq:
								comboReplaceElementsLeaf.setVisible(false);
								storeColumns.clear();
								storeColumns.addAll(columns);
								storeColumns.commitChanges();
								comboColumns.clear();
								comboColumns.setVisible(true);
								firstArg.setVisible(true);
								firstArg.setEmptyText(EMPTY_TEXT_FROM_STRING);
								secondArg.setVisible(true);
								secondArg.setEmptyText(EMPTY_TEXT_TO_STRING);
								removeOperation(source);
								break;
							case TextReplaceMatchingRegex:
								comboReplaceElementsLeaf.setVisible(false);
								storeColumns.clear();
								storeColumns.addAll(columns);
								storeColumns.commitChanges();
								comboColumns.clear();
								comboColumns.setVisible(true);
								firstArg.setVisible(true);
								firstArg.setEmptyText(EMPTY_TEXT_REPLACE_REGEXP);
								secondArg.setVisible(true);
								secondArg
										.setEmptyText(EMPTY_TEXT_REPLACE_REPLACING);
								removeOperation(source);
								break;
							default:
								break;
							}
							vert.forceLayout();
							thisCont.forceLayout();

						}

					}

				});

		comboReplaceElementsOperations.setEmptyText("Select...");
		comboReplaceElementsOperations.setItemId(itemIdComboConcat);
		comboReplaceElementsOperations.setWidth(COMBO_WIDTH);
		comboReplaceElementsOperations.setEditable(false);
		comboReplaceElementsOperations.setTriggerAction(TriggerAction.ALL);

		comboReplaceElementsOperations.setValue(
				storeReplaceElementsOperations.get(0), true);

		//
		horiz.add(comboReplaceElementsOperations, new BoxLayoutData(
				new Margins(0)));
		horiz.add(comboReplaceElementsLeaf, new BoxLayoutData(new Margins(0)));
		horiz.add(comboColumns, new BoxLayoutData(new Margins(0)));
		horiz.add(firstArg, new BoxLayoutData(new Margins(0)));
		horiz.add(secondArg, new BoxLayoutData(new Margins(0)));
		horiz.setItemId(itemIdHoriz);
		vert.add(horiz, new VerticalLayoutData(1, -1, new Margins(1)));

		firstArg.setVisible(true);
		secondArg.setVisible(false);
		comboColumns.setVisible(false);
		comboReplaceElementsLeaf.setVisible(false);
		comboReplaceElementsOperations.setVisible(true);
		forceLayout();
	}

	private void removeOperation(ComboBox<ReplaceElement> source) {
		HBoxLayoutContainer horiz = (HBoxLayoutContainer) source.getParent();
		int index = vert.getWidgetIndex(horiz);
		Log.debug("No concat for index: " + index);
		index++;
		for (int i = index; i < vert.getWidgetCount();) {
			Log.debug("Remove horiz index: " + i);
			vert.remove(i);
		}
	}

	private boolean existOperation(ComboBox<ReplaceElement> source) {
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

	public C_Expression getExpression() throws ReplaceTypeMapException {
		ExpressionContainer expressionContainer = null;
		readableExpression = new String();
		C_Expression expression = null;

		if (vert.getWidgetCount() > 0) {
			int index = 0;
			ColumnDataType targetType = targetColumnType;
			expressionContainer = calcCExpression(index, targetType);
			if (expressionContainer != null) {
				readableExpression = expressionContainer
						.getReadableExpression();
				expression = expressionContainer.getExpression();
			} else {

			}
		}

		Log.debug("ReadableExpression: " + readableExpression);
		Log.debug("C_Expression:" + expression);
		return expression;
	}

	public String getReadableExpression() {
		return readableExpression;
	}

	protected ExpressionContainer calcCExpression(int index,
			ColumnDataType targetType) throws ReplaceTypeMapException {
		ExpressionContainer expressionContainer = null;
		ExpressionContainer expContainerOperation = null;
		C_Expression exp = null;
		C_Expression expLeaf = null;
		String readableExp = "";
		String readableExpLeaf = "";
		TextField firstArg;
		TextField secondArg;
		HBoxLayoutContainer horiz;
		ReplaceTypeMap mapReplace = new ReplaceTypeMap();

		horiz = (HBoxLayoutContainer) vert.getWidget(index);
		@SuppressWarnings("unchecked")
		ComboBox<ReplaceElement> comboOperations = (ComboBox<ReplaceElement>) horiz
				.getItemByItemId(itemIdComboConcat);
		Log.debug("combo Concat: " + comboOperations.getCurrentValue());
		@SuppressWarnings("unchecked")
		ComboBox<ReplaceElement> comboLeaf = (ComboBox<ReplaceElement>) horiz
				.getItemByItemId(itemIdComboLeaf);
		Log.debug("combo Leaf: " + comboLeaf.getCurrentValue());

		@SuppressWarnings("unchecked")
		ComboBox<ColumnData> comboColumns = (ComboBox<ColumnData>) horiz
				.getItemByItemId(itemIdComboColumns);
		Log.debug("combo columns: " + comboColumns.getCurrentValue());
		ColumnData columnOfCombo = comboColumns.getCurrentValue();

		firstArg = (TextField) horiz.getItemByItemId(itemIdFirstArg);
		secondArg = (TextField) horiz.getItemByItemId(itemIdSecondArg);
		Log.debug("[combo column: " + columnOfCombo + ", firstArg: "
				+ firstArg.getCurrentValue() + ", secondArg: "
				+ secondArg.getCurrentValue() + "]");

		ReplaceType comboOperationsReplaceType = null;
		ReplaceType comboLeafReplaceType = null;

		if (comboOperations.getCurrentValue() == null) {
			throw new ReplaceTypeMapException("Fill all field!");
		} else {
			comboOperationsReplaceType = comboOperations.getCurrentValue()
					.getReplaceType();
			if (comboOperationsReplaceType.compareTo(ReplaceType.Concat) == 0) {
				if (comboLeaf.getCurrentValue() == null) {
					throw new ReplaceTypeMapException("Fill all field!");
				} else {
					comboLeafReplaceType = comboLeaf.getCurrentValue()
							.getReplaceType();
					/*
					 * ColumnDataType cType = ColumnDataType
					 * .getColumnDataTypeFromId(column.getDataTypeName());
					 */
					switch (replaceExpressionType) {
					case Replace:
					case AddColumn:	
						expLeaf = mapReplace.map(
								columnOfCombo,
								ColumnDataType.Text,
								comboLeafReplaceType,
								firstArg == null ? null : firstArg
										.getCurrentValue(),
								secondArg == null ? null : secondArg
										.getCurrentValue(), false);
						break;
					case Template:
						expLeaf = mapReplace.map(
								columnOfCombo,
								ColumnDataType.Text,
								comboLeafReplaceType,
								firstArg == null ? null : firstArg
										.getCurrentValue(),
								secondArg == null ? null : secondArg
										.getCurrentValue(), true);
						break;
					default:
						expLeaf = mapReplace.map(
								columnOfCombo,
								ColumnDataType.Text,
								comboLeafReplaceType,
								firstArg == null ? null : firstArg
										.getCurrentValue(),
								secondArg == null ? null : secondArg
										.getCurrentValue(), false);
						break;

					}

					readableExpLeaf = expLeaf.getReadableExpression();
				}

				index++;
				if (index < vert.getWidgetCount()) {
					expContainerOperation = calcCExpression(index,
							ColumnDataType.Text);
					if (expContainerOperation == null) {
						return null;
					} else {
						switch (replaceExpressionType) {
						case Replace:
						case AddColumn:	
							exp = mapReplace.map(
									columnOfCombo,
									targetType,
									comboOperationsReplaceType,
									firstArg == null ? null : firstArg
											.getCurrentValue(),
									secondArg == null ? null : secondArg
											.getCurrentValue(), false, expLeaf,
									expContainerOperation.getExpression(),
									readableExpLeaf, expContainerOperation
											.getReadableExpression());
							break;
						case Template:
							exp = mapReplace.map(
									columnOfCombo,
									targetType,
									comboOperationsReplaceType,
									firstArg == null ? null : firstArg
											.getCurrentValue(),
									secondArg == null ? null : secondArg
											.getCurrentValue(), true, expLeaf,
									expContainerOperation.getExpression(),
									readableExpLeaf, expContainerOperation
											.getReadableExpression());
							break;
						default:
							exp = mapReplace.map(
									columnOfCombo,
									targetType,
									comboOperationsReplaceType,
									firstArg == null ? null : firstArg
											.getCurrentValue(),
									secondArg == null ? null : secondArg
											.getCurrentValue(), false, expLeaf,
									expContainerOperation.getExpression(),
									readableExpLeaf, expContainerOperation
											.getReadableExpression());
							break;

						}
						readableExp = exp.getReadableExpression();
						expressionContainer = new ExpressionContainer(exp,
								readableExp);
					}
				} else {
					return null;
				}

			} else {
				if (comboOperationsReplaceType.compareTo(ReplaceType.Addition) == 0
						|| comboOperationsReplaceType
								.compareTo(ReplaceType.Subtraction) == 0
						|| comboOperationsReplaceType
								.compareTo(ReplaceType.Modulus) == 0
						|| comboOperationsReplaceType
								.compareTo(ReplaceType.Multiplication) == 0
						|| comboOperationsReplaceType
								.compareTo(ReplaceType.Division) == 0) {
					ColumnDataType cType;
					if (comboLeaf.getCurrentValue() == null) {
						throw new ReplaceTypeMapException("Fill all field!");
					} else {

						comboLeafReplaceType = comboLeaf.getCurrentValue()
								.getReplaceType();
						
						if(columnOfCombo==null){
							throw new ReplaceTypeMapException("Selected a valid column!");
						}
						cType = ColumnDataType.getColumnDataTypeFromId(columnOfCombo
								.getDataTypeName());

						switch (replaceExpressionType) {
						case Replace:
						case AddColumn:	
							expLeaf = mapReplace.map(
									columnOfCombo,
									cType,
									comboLeafReplaceType,
									firstArg == null ? null : firstArg
											.getCurrentValue(),
									secondArg == null ? null : secondArg
											.getCurrentValue(), false);
							break;
						case Template:
							expLeaf = mapReplace.map(
									columnOfCombo,
									cType,
									comboLeafReplaceType,
									firstArg == null ? null : firstArg
											.getCurrentValue(),
									secondArg == null ? null : secondArg
											.getCurrentValue(), true);
							break;
						default:
							expLeaf = mapReplace.map(
									columnOfCombo,
									cType,
									comboLeafReplaceType,
									firstArg == null ? null : firstArg
											.getCurrentValue(),
									secondArg == null ? null : secondArg
											.getCurrentValue(), false);
							break;

						}

						readableExpLeaf = expLeaf.getReadableExpression();
					}

					index++;
					if (index < vert.getWidgetCount()) {
						expContainerOperation = calcCExpression(index, cType);
						if (expContainerOperation == null) {
							return null;
						} else {
							switch (replaceExpressionType) {
							case Replace:
							case AddColumn:	
								exp = mapReplace.map(
										columnOfCombo,
										targetType,
										comboOperationsReplaceType,
										firstArg == null ? null : firstArg
												.getCurrentValue(),
										secondArg == null ? null : secondArg
												.getCurrentValue(), false,
										expLeaf, expContainerOperation
												.getExpression(),
										readableExpLeaf, expContainerOperation
												.getReadableExpression());
								break;
							case Template:
								exp = mapReplace.map(
										columnOfCombo,
										targetType,
										comboOperationsReplaceType,
										firstArg == null ? null : firstArg
												.getCurrentValue(),
										secondArg == null ? null : secondArg
												.getCurrentValue(), true,
										expLeaf, expContainerOperation
												.getExpression(),
										readableExpLeaf, expContainerOperation
												.getReadableExpression());
								break;
							default:
								exp = mapReplace.map(
										columnOfCombo,
										targetType,
										comboOperationsReplaceType,
										firstArg == null ? null : firstArg
												.getCurrentValue(),
										secondArg == null ? null : secondArg
												.getCurrentValue(), false,
										expLeaf, expContainerOperation
												.getExpression(),
										readableExpLeaf, expContainerOperation
												.getReadableExpression());
								break;

							}
							readableExp = exp.getReadableExpression();
							expressionContainer = new ExpressionContainer(exp,
									readableExp);
						}
					} else {
						return null;
					}

				} else {
					switch (replaceExpressionType) {
					case Replace:
					case AddColumn:	
						exp = mapReplace.map(
								columnOfCombo,
								targetType,
								comboOperationsReplaceType,
								firstArg == null ? null : firstArg
										.getCurrentValue(),
								secondArg == null ? null : secondArg
										.getCurrentValue(), false);
						break;
					case Template:
						exp = mapReplace.map(
								columnOfCombo,
								targetType,
								comboOperationsReplaceType,
								firstArg == null ? null : firstArg
										.getCurrentValue(),
								secondArg == null ? null : secondArg
										.getCurrentValue(), true);
						break;
					default:
						exp = mapReplace.map(
								columnOfCombo,
								targetType,
								comboOperationsReplaceType,
								firstArg == null ? null : firstArg
										.getCurrentValue(),
								secondArg == null ? null : secondArg
										.getCurrentValue(), false);
						break;

					}
					readableExp = exp.getReadableExpression();
					expressionContainer = new ExpressionContainer(exp,
							readableExp);
				}
			}
		}
		return expressionContainer;

	}
}
