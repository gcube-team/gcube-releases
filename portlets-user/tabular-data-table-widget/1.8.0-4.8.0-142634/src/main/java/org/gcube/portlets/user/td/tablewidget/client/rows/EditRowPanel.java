package org.gcube.portlets.user.td.tablewidget.client.rows;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.gcube.portlets.user.td.gwtservice.client.rpc.TDGWTServiceAsync;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTIsFinalException;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTIsLockedException;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTSessionExpiredException;
import org.gcube.portlets.user.td.gwtservice.shared.tr.DimensionRow;
import org.gcube.portlets.user.td.gwtservice.shared.tr.rows.EditRowSession;
import org.gcube.portlets.user.td.monitorwidget.client.MonitorDialog;
import org.gcube.portlets.user.td.monitorwidget.client.MonitorDialogListener;
import org.gcube.portlets.user.td.columnwidget.client.dimension.DimensionRowSelectionDialog;
import org.gcube.portlets.user.td.columnwidget.client.dimension.DimensionRowSelectionListener;
import org.gcube.portlets.user.td.columnwidget.client.dimension.DimensionRowsProperties;
import org.gcube.portlets.user.td.tablewidget.client.resources.ResourceBundle;
import org.gcube.portlets.user.td.tablewidget.client.util.UtilsGXT3;
import org.gcube.portlets.user.td.widgetcommonevent.client.event.ChangeTableRequestEvent;
import org.gcube.portlets.user.td.widgetcommonevent.client.event.SessionExpiredEvent;
import org.gcube.portlets.user.td.widgetcommonevent.client.type.ChangeTableRequestType;
import org.gcube.portlets.user.td.widgetcommonevent.client.type.ChangeTableWhy;
import org.gcube.portlets.user.td.widgetcommonevent.client.type.SessionExpiredType;
import org.gcube.portlets.user.td.widgetcommonevent.shared.CellData;
import org.gcube.portlets.user.td.widgetcommonevent.shared.OperationResult;
import org.gcube.portlets.user.td.widgetcommonevent.shared.TRId;
import org.gcube.portlets.user.td.widgetcommonevent.shared.grid.model.RowRaw;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnData;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnDataType;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnTypeCode;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.web.bindery.event.shared.EventBus;
import com.sencha.gxt.cell.core.client.ButtonCell.IconAlign;
import com.sencha.gxt.cell.core.client.form.ComboBoxCell.TriggerAction;
import com.sencha.gxt.core.client.dom.ScrollSupport.ScrollMode;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.core.client.util.ToggleGroup;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.widget.core.client.FramedPanel;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.BoxLayoutContainer.BoxLayoutData;
import com.sencha.gxt.widget.core.client.container.BoxLayoutContainer.BoxLayoutPack;
import com.sencha.gxt.widget.core.client.container.HBoxLayoutContainer;
import com.sencha.gxt.widget.core.client.container.HBoxLayoutContainer.HBoxLayoutAlign;
import com.sencha.gxt.widget.core.client.container.MarginData;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.event.TriggerClickEvent;
import com.sencha.gxt.widget.core.client.event.TriggerClickEvent.TriggerClickHandler;
import com.sencha.gxt.widget.core.client.form.ComboBox;
import com.sencha.gxt.widget.core.client.form.DateField;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.form.FieldSet;
import com.sencha.gxt.widget.core.client.form.Radio;
import com.sencha.gxt.widget.core.client.form.TextField;
import com.sencha.gxt.widget.core.client.form.validator.RegExValidator;

/**
 * 
 * @author "Giancarlo Panichi"
 * 
 */
public class EditRowPanel extends FramedPanel implements MonitorDialogListener {
	private static final String ITEM_CREATE_ROW = "NewRow";

	private static final String GEOMETRY_REGEXPR = "(\\s*POINT\\s*\\(\\s*(-)?\\d+(\\.\\d+)?\\s+(-)?\\d+(\\.\\d+)?\\s*\\)\\s*$)"
			+ "|(\\s*LINESTRING\\s*\\((\\s*(-)?\\d+(\\.\\d+)?\\s+(-)?\\d+(\\.\\d+)?\\s*,)+\\s*((-)?\\d+(\\.\\d+)?\\s+(-)?\\d+(\\.\\d+)?\\s*)\\)\\s*$)";

	private static final String WIDTH = "600px";
	private static final String HEIGHT = "370px";
	private static final String CONTAINERHEIGHT = "370px";
	private static final int LABELSIZE = 120;
	private static final int LABEL_SIZE_IN_CHAR = 17;
	private static final String FIELDSETWIDTH = "640px";

	private EditRowDialog parent;
	private TRId trId;
	private EventBus eventBus;
	private ArrayList<ColumnData> columns;
	private ArrayList<RowRaw> rowsRaw;
	private boolean editRow;

	private VerticalLayoutContainer v;

	private ArrayList<String> rowsId;

	private TextButton btnSave;
	private TextButton btnClose;

	private DateTimeFormat sdf = DateTimeFormat.getFormat("yyyy-MM-dd");

	/**
	 * Edit Row
	 * 
	 * @param parent
	 * @param trId
	 * @param rowsRaw
	 * @param eventBus
	 */
	public EditRowPanel(EditRowDialog parent, TRId trId,
			ArrayList<RowRaw> rowsRaw, EventBus eventBus) {
		super();
		this.parent = parent;
		this.trId = trId;
		this.rowsRaw = rowsRaw;
		this.eventBus = eventBus;
		this.editRow = true;
		Log.debug("Create EditRowPanel(): [" + trId.toString() + " , RowsRaw:"
				+ rowsRaw + "]");
		if (rowsRaw == null || rowsRaw.isEmpty()) {
			UtilsGXT3.alert("Attentions", "No row selected");
		} else {
			init();
			retrieveColumn();
		}
	}

	/**
	 * Add a new row
	 * 
	 * @param parent
	 * @param trId
	 * @param eventBus
	 */
	public EditRowPanel(EditRowDialog parent, TRId trId, EventBus eventBus) {
		super();
		this.parent = parent;
		this.trId = trId;
		this.eventBus = eventBus;
		this.editRow = false;
		Log.debug("Create For Add Row EditRowPanel(): [" + trId.toString()
				+ "]");
		init();
		retrieveColumn();

	}

	protected void init() {
		setWidth(WIDTH);
		setHeight(HEIGHT);
		setHeaderVisible(false);
		setBodyBorder(false);
	}

	protected void create() {
		SimpleContainer container = new SimpleContainer();
		container.setHeight(CONTAINERHEIGHT);

		v = new VerticalLayoutContainer();
		v.setScrollMode(ScrollMode.AUTO);
		v.setAdjustForScroll(true);

		btnSave = new TextButton("Save");
		btnSave.setIcon(ResourceBundle.INSTANCE.save());
		btnSave.setIconAlign(IconAlign.RIGHT);
		btnSave.setTitle("Save");
		btnSave.addSelectHandler(new SelectHandler() {

			public void onSelect(SelectEvent event) {
				Log.debug("Pressed Save");
				btnSave.disable();
				save();

			}
		});

		if (columns.size() < 1) {
			btnSave.disable();
		}

		btnClose = new TextButton("Close");
		btnClose.setIcon(ResourceBundle.INSTANCE.close());
		btnClose.setIconAlign(IconAlign.RIGHT);
		btnClose.setTitle("Close");
		btnClose.addSelectHandler(new SelectHandler() {

			public void onSelect(SelectEvent event) {
				Log.debug("Pressed Close");
				close();
			}
		});

		HBoxLayoutContainer flowButton = new HBoxLayoutContainer();
		flowButton.setHBoxLayoutAlign(HBoxLayoutAlign.MIDDLE);
		flowButton.setPack(BoxLayoutPack.CENTER);

		flowButton.add(btnSave, new BoxLayoutData(new Margins(2, 4, 2, 4)));
		flowButton.add(btnClose, new BoxLayoutData(new Margins(2, 4, 2, 4)));

		rowsId = new ArrayList<String>();
		if (editRow) {
			for (RowRaw rowRaw : rowsRaw) {
				FieldSet fieldSet = new FieldSet();
				fieldSet.setCollapsible(false);
				fieldSet.setItemId(rowRaw.getRowId());
				fieldSet.setHeadingText(rowRaw.getRowId());
				fieldSet.setWidth(FIELDSETWIDTH);
				rowsId.add(rowRaw.getRowId());

				VerticalLayoutContainer fieldSetLayout = new VerticalLayoutContainer();
				// fieldSetLayout.setScrollMode(ScrollMode.AUTO);
				fieldSet.add(fieldSetLayout, new MarginData(0));

				ArrayList<FieldLabel> fields = generateFields(rowRaw);
				for (FieldLabel fl : fields) {
					fieldSetLayout.add(fl, new VerticalLayoutData(1, -1,
							new Margins(0)));
				}

				v.add(fieldSet, new VerticalLayoutData(1, -1, new Margins(1)));

			}

		} else {
			FieldSet fieldSet = new FieldSet();
			fieldSet.setCollapsible(false);
			fieldSet.setItemId(ITEM_CREATE_ROW);
			fieldSet.setHeadingText("New");
			fieldSet.setWidth(FIELDSETWIDTH);
			rowsId.add(ITEM_CREATE_ROW);

			VerticalLayoutContainer fieldSetLayout = new VerticalLayoutContainer();
			// fieldSetLayout.setScrollMode(ScrollMode.AUTO);
			fieldSet.add(fieldSetLayout);

			ArrayList<FieldLabel> fields = generateFields(null);
			for (FieldLabel fl : fields) {
				fieldSetLayout.add(fl, new VerticalLayoutData(1, -1,
						new Margins(0)));
			}

			v.add(fieldSet, new VerticalLayoutData(1, -1, new Margins(1)));
		}

		container.add(v, new MarginData(0));
		container.forceLayout();

		VerticalLayoutContainer vPanel = new VerticalLayoutContainer();
		vPanel.add(container, new VerticalLayoutData(1, -1));
		vPanel.add(flowButton, new VerticalLayoutData(1, -1, new Margins(1)));
		add(vPanel);

	}

	protected void retrieveColumn() {
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
								Log.error("load columns failure:"
										+ caught.getLocalizedMessage());
								UtilsGXT3.alert("Error retrieving columns",
										"Error retrieving columns");
							}
						}
					}

					public void onSuccess(ArrayList<ColumnData> result) {
						Log.trace("loaded " + result.size() + " ColumnData");
						columns = result;
						create();
					}
				});

	}

	protected ArrayList<FieldLabel> generateFields(RowRaw rowRaw) {
		ArrayList<FieldLabel> fields = new ArrayList<FieldLabel>();

		for (ColumnData col : columns) {
			String label = new String();
			if (col != null && col.getLabel() != null) {
				label = SafeHtmlUtils.htmlEscape(col.getLabel());
				if (label.length() > LABEL_SIZE_IN_CHAR + 2) {
					label = label.substring(0, LABEL_SIZE_IN_CHAR);
					label += "...";
				}
			}
			if (!col.isViewColumn()) {
				if (col.getTypeCode().compareTo(
						ColumnTypeCode.DIMENSION.toString()) == 0
						|| col.getTypeCode().compareTo(
								ColumnTypeCode.TIMEDIMENSION.toString()) == 0) {

					FieldLabel dimensionLabel = retrieveDimensionLabel(rowRaw,
							col, label);
					dimensionLabel.setLabelWidth(LABELSIZE);
					fields.add(dimensionLabel);
				} else {
					if (col.getDataTypeName().compareTo(
							ColumnDataType.Boolean.toString()) == 0) {
						Radio radioTrue = new Radio();
						radioTrue.setBoxLabel("true");
						Radio radioFalse = new Radio();
						radioFalse.setBoxLabel("false");

						String value = "true";
						if (editRow) {
							value = rowRaw.getMap().get(col.getColumnId());
						}

						if (new Boolean(value)) {
							radioTrue.setValue(true);
						} else {
							radioFalse.setValue(true);
						}
						ToggleGroup toggleGroup = new ToggleGroup();
						toggleGroup.add(radioTrue);
						toggleGroup.add(radioFalse);

						HorizontalPanel hp = new HorizontalPanel();
						hp.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
						hp.add(radioTrue);
						hp.add(radioFalse);
						FieldLabel booleanLabel = new FieldLabel(hp, label);
						booleanLabel.setLabelWidth(LABELSIZE);
						booleanLabel.setId(col.getColumnId());
						fields.add(booleanLabel);
					} else {
						if (col.getDataTypeName().compareTo(
								ColumnDataType.Date.toString()) == 0) {
							DateField date = new DateField();
							Date d = null;
							if (editRow) {
								String value = rowRaw.getMap().get(
										col.getColumnId());

								try {
									d = sdf.parse(value);
								} catch (Throwable e) {
									Log.error("Error parsing date string: "
											+ e.getLocalizedMessage());
								}
							}
							if (d != null) {
								date.setValue(d);
							}
							FieldLabel dateLabel = new FieldLabel(date, label);
							dateLabel.setLabelWidth(LABELSIZE);
							dateLabel.setId(col.getColumnId());
							fields.add(dateLabel);
						} else {
							if (col.getDataTypeName().compareTo(
									ColumnDataType.Text.toString()) == 0) {
								TextField text = new TextField();
								String value = "";
								if (editRow) {
									value = rowRaw.getMap().get(
											col.getColumnId());
								}
								text.setValue(value);
								text.addValueChangeHandler(new ValueChangeHandler<String>() {
									@Override
									public void onValueChange(
											ValueChangeEvent<String> event) {

									}
								});

								FieldLabel textLabel = new FieldLabel(text,
										label);
								textLabel.setLabelWidth(LABELSIZE);
								textLabel.setId(col.getColumnId());
								fields.add(textLabel);
							} else {
								if (col.getDataTypeName().compareTo(
										ColumnDataType.Geometry.toString()) == 0) {
									TextField geometry = new TextField();
									geometry.addValidator(new RegExValidator(
											GEOMETRY_REGEXPR,
											"Geometry Type not valid"));
									String value = "";
									if (editRow) {
										value = rowRaw.getMap().get(
												col.getColumnId());
									}
									geometry.setValue(value);
									geometry.addValueChangeHandler(new ValueChangeHandler<String>() {
										@Override
										public void onValueChange(
												ValueChangeEvent<String> event) {

										}
									});

									FieldLabel geometryLabel = new FieldLabel(
											geometry, label);
									geometryLabel.setLabelWidth(LABELSIZE);
									geometryLabel.setId(col.getColumnId());
									fields.add(geometryLabel);
								} else {
									if (col.getDataTypeName().compareTo(
											ColumnDataType.Integer.toString()) == 0) {
										TextField integ = new TextField();
										String value = "";
										if (editRow) {
											value = rowRaw.getMap().get(
													col.getColumnId());
										}
										integ.setValue(value);
										integ.addValueChangeHandler(new ValueChangeHandler<String>() {
											@Override
											public void onValueChange(
													ValueChangeEvent<String> event) {

											}
										});

										FieldLabel integLabel = new FieldLabel(
												integ, label);
										integLabel.setLabelWidth(LABELSIZE);
										integLabel.setId(col.getColumnId());
										fields.add(integLabel);
									} else {
										if (col.getDataTypeName().compareTo(
												ColumnDataType.Numeric
														.toString()) == 0) {
											TextField numeric = new TextField();
											String value = "";
											if (editRow) {
												value = rowRaw.getMap().get(
														col.getColumnId());
											}
											numeric.setValue(value);
											numeric.setId(col.getColumnId());
											numeric.addValueChangeHandler(new ValueChangeHandler<String>() {
												@Override
												public void onValueChange(
														ValueChangeEvent<String> event) {

												}
											});
											FieldLabel numericLabel = new FieldLabel(
													numeric, label);
											numericLabel
													.setLabelWidth(LABELSIZE);
											numericLabel.setId(col
													.getColumnId());
											fields.add(numericLabel);
										} else {

										}
									}
								}
							}
						}
					}
				}
			}
		}
		return fields;

	}

	protected FieldLabel retrieveDimensionLabel(RowRaw rowRaw,
			final ColumnData col, String label) {
		Log.debug("retriveDimensionLabel on:" + col);
		// comboDimension
		DimensionRowsProperties propsDimension = GWT
				.create(DimensionRowsProperties.class);
		ListStore<DimensionRow> storeComboDimensionType = new ListStore<DimensionRow>(
				propsDimension.rowId());

		final ComboBox<DimensionRow> comboDimension = new ComboBox<DimensionRow>(
				storeComboDimensionType, propsDimension.value());

		Log.debug("ComboDimensionType created");

		final DimensionRowSelectionListener listener = new DimensionRowSelectionListener() {

			@Override
			public void selectedDimensionRow(DimensionRow dimensionRow) {
				comboDimension.setValue(dimensionRow, true);

			}

			@Override
			public void failedDimensionRowSelection(String reason, String detail) {
				Log.error("Change Value Failed:" + reason + " " + detail);

			}

			@Override
			public void abortedDimensionRowSelection() {
				Log.debug("Change Value Aborted");

			}
		};

		String viewColumn = col.getRelationship().getTargetColumnId();

		String valueOnViewColumn = null;
		String rowId = null;
		if (editRow) {
			valueOnViewColumn = rowRaw.getMap().get(viewColumn);
			rowId = rowRaw.getMap().get(col.getColumnId());

			DimensionRow dimR = new DimensionRow(rowId, valueOnViewColumn);
			comboDimension.setValue(dimR, true);
		}

		final CellData cellData = new CellData(valueOnViewColumn, "",
				viewColumn, "", rowId, 0, 0);

		comboDimension.addTriggerClickHandler(new TriggerClickHandler() {

			@Override
			public void onTriggerClick(TriggerClickEvent event) {
				Log.debug("ComboDimensionRows TriggerClickEvent");
				comboDimension.collapse();

				DimensionRowSelectionDialog dialogDimensionRowSelection = new DimensionRowSelectionDialog(
						col, cellData, eventBus);
				dialogDimensionRowSelection.addListener(listener);
				dialogDimensionRowSelection.show();

			}

		});

		comboDimension.setEmptyText("Select a Value...");
		comboDimension.setWidth(300);
		comboDimension.setEditable(false);
		comboDimension.setTriggerAction(TriggerAction.ALL);

		FieldLabel dimensionLabel = new FieldLabel(comboDimension, label);
		dimensionLabel.setId(col.getColumnId());
		return dimensionLabel;
	}

	protected void save() {
		int i = 0;
		int lenght = v.getWidgetCount();

		HashMap<String, HashMap<String, String>> rowsMaps = new HashMap<String, HashMap<String, String>>();

		for (; i < lenght; i++) {
			FieldSet fieldSet = (FieldSet) v.getWidget(i);
			VerticalLayoutContainer fieldSetLayout = (VerticalLayoutContainer) fieldSet
					.getWidget();
			int j = 0;
			int fieldSetLayoutLenght = fieldSetLayout.getWidgetCount();
			HashMap<String, String> maps = new HashMap<String, String>();
			for (; j < fieldSetLayoutLenght; j++) {
				FieldLabel fieldLabel = (FieldLabel) fieldSetLayout
						.getWidget(j);
				String columnId = fieldLabel.getId();
				ColumnData colCurrent = null;
				for (ColumnData col : columns) {
					if (col.getColumnId().compareTo(columnId) == 0) {
						colCurrent = col;
						break;
					}
				}
				if (colCurrent == null) {
					Log.debug("Current col is null");
					btnSave.enable();
					return;
				}
				if (colCurrent.getTypeCode().compareTo(
						ColumnTypeCode.DIMENSION.toString()) == 0
						|| colCurrent.getTypeCode().compareTo(
								ColumnTypeCode.TIMEDIMENSION.toString()) == 0) {
					@SuppressWarnings("unchecked")
					ComboBox<DimensionRow> comboDimension = (ComboBox<DimensionRow>) fieldLabel
							.getWidget();
					DimensionRow dimRow = comboDimension.getValue();
					if (dimRow == null
							|| (dimRow != null && (dimRow.getRowId() == null || dimRow
									.getRowId().isEmpty()))) {
						UtilsGXT3.alert(
								"Attentions",
								"Select a valid value for "
										+ colCurrent.getLabel());
						btnSave.enable();
						return;
					}
					maps.put(columnId, dimRow.getRowId());
				} else {
					if (colCurrent.getDataTypeName().compareTo(
							ColumnDataType.Boolean.toString()) == 0) {
						HorizontalPanel hpanel = (HorizontalPanel) fieldLabel
								.getWidget();
						Radio radioTrue = (Radio) hpanel.getWidget(0);
						maps.put(columnId, radioTrue.getValue().toString());
					} else {
						if (colCurrent.getDataTypeName().compareTo(
								ColumnDataType.Date.toString()) == 0) {
							DateField date = (DateField) fieldLabel.getWidget();
							if (date == null || date.getCurrentValue() == null) {
								maps.put(columnId, null);
							} else {
								Date d = date.getCurrentValue();
								String dateS = sdf.format(d);
								maps.put(columnId, dateS);
							}
						} else {
							if (colCurrent.getDataTypeName().compareTo(
									ColumnDataType.Text.toString()) == 0) {
								TextField text = (TextField) fieldLabel
										.getWidget();
								String val = text.getCurrentValue();
								if (val == null) {
									val = "";
								}
								maps.put(columnId, val);
							} else {
								if (colCurrent.getDataTypeName().compareTo(
										ColumnDataType.Geometry.toString()) == 0) {
									TextField geometry = (TextField) fieldLabel
											.getWidget();
									if (geometry.isValid()) {
										String val = geometry.getCurrentValue();
										if (val == null) {
											val = "";
										}
										maps.put(columnId, val);
									} else {
										UtilsGXT3
												.alert("Attentions",
														"The value of "
																+ colCurrent
																		.getLabel()
																+ " is not a valid text representation for geometry type ( e.g. POINT(34 56) or LINESTRING(65 34, 56.43 78.65)!");
										btnSave.enable();
										return;
									}
								} else {
									if (colCurrent.getDataTypeName().compareTo(
											ColumnDataType.Integer.toString()) == 0) {
										TextField integ = (TextField) fieldLabel
												.getWidget();
										@SuppressWarnings("unused")
										Integer intege;
										try {
											intege = new Integer(
													integ.getCurrentValue());
										} catch (NumberFormatException e) {
											UtilsGXT3
													.alert("Attentions",
															colCurrent
																	.getLabel()
																	+ " is no a valid Integer type");
											btnSave.enable();
											return;
										}

										maps.put(columnId,
												integ.getCurrentValue());
									} else {
										if (colCurrent.getDataTypeName()
												.compareTo(
														ColumnDataType.Numeric
																.toString()) == 0) {
											TextField numeric = (TextField) fieldLabel
													.getWidget();
											@SuppressWarnings("unused")
											Double d;
											String val = numeric
													.getCurrentValue();
											if (val == null) {
												Log.debug("Attentions"
														+ colCurrent.getLabel()
														+ " is no a valid Numeric type");
												UtilsGXT3
														.alert("Attentions",
																colCurrent
																		.getLabel()
																		+ " is no a valid Numeric type");
												btnSave.enable();
												return;
											}
											try {
												d = new Double(val);
											} catch (NumberFormatException e) {
												Log.debug("Attentions"
														+ colCurrent.getLabel()
														+ " is no a valid Numeric type");
												UtilsGXT3
														.alert("Attentions",
																colCurrent
																		.getLabel()
																		+ " is no a valid Numeric type");
												btnSave.enable();
												return;
											}
											maps.put(columnId,
													numeric.getCurrentValue());
										} else {

										}
									}
								}
							}
						}
					}
				}
			}

			rowsMaps.put(fieldSet.getItemId(), maps);

		}

		EditRowSession editRowSession;
		if (editRow) {
			editRowSession = new EditRowSession(trId, columns, rowsMaps, rowsId);
		} else {
			editRowSession = new EditRowSession(trId, columns, rowsMaps);
		}

		callEditRow(editRowSession);

	}

	protected void callEditRow(EditRowSession editRowSession) {

		TDGWTServiceAsync.INSTANCE.startEditRow(editRowSession,
				new AsyncCallback<String>() {

					public void onFailure(Throwable caught) {
						Log.debug("EditRow: " + caught.getLocalizedMessage());
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
									UtilsGXT3.alert("Error",
											"Error in operation invocation!");
								}
							}
						}
					}

					public void onSuccess(String taskId) {
						openMonitorDialog(taskId);
					}

				});

	}

	protected void close() {
		if (parent != null) {
			parent.close();
		}
	}

	// /
	protected void openMonitorDialog(String taskId) {
		MonitorDialog monitorDialog = new MonitorDialog(taskId, eventBus);
		monitorDialog.addProgressDialogListener(this);
		monitorDialog.show();
	}

	@Override
	public void operationComplete(OperationResult operationResult) {
		ChangeTableWhy why = ChangeTableWhy.TABLEUPDATED;
		ChangeTableRequestEvent changeTableRequestEvent = new ChangeTableRequestEvent(
				ChangeTableRequestType.EDITROW, operationResult.getTrId(), why);
		eventBus.fireEvent(changeTableRequestEvent);
		close();
	}

	@Override
	public void operationFailed(Throwable caught, String reason, String details) {
		UtilsGXT3.alert(reason, details);
		close();

	}

	@Override
	public void operationStopped(OperationResult operationResult,
			String reason, String details) {
		ChangeTableWhy why = ChangeTableWhy.TABLECURATION;
		ChangeTableRequestEvent changeTableRequestEvent = new ChangeTableRequestEvent(
				ChangeTableRequestType.EDITROW, operationResult.getTrId(), why);
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
