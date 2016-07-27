package org.gcube.portlets.user.td.tablewidget.client.geospatial;

import java.util.ArrayList;

import org.gcube.portlets.user.td.expressionwidget.client.properties.ColumnDataPropertiesCombo;
import org.gcube.portlets.user.td.gwtservice.client.rpc.TDGWTServiceAsync;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTIsFinalException;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTIsLockedException;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTSessionExpiredException;
import org.gcube.portlets.user.td.gwtservice.shared.geospatial.GeospatialCreateCoordinatesSession;
import org.gcube.portlets.user.td.monitorwidget.client.MonitorDialog;
import org.gcube.portlets.user.td.monitorwidget.client.MonitorDialogListener;
import org.gcube.portlets.user.td.columnwidget.client.geospatial.GeospatialCoordinatesTypePropertiesCombo;
import org.gcube.portlets.user.td.tablewidget.client.resources.ResourceBundle;
import org.gcube.portlets.user.td.tablewidget.client.util.UtilsGXT3;
import org.gcube.portlets.user.td.widgetcommonevent.client.event.ChangeTableRequestEvent;
import org.gcube.portlets.user.td.widgetcommonevent.client.event.SessionExpiredEvent;
import org.gcube.portlets.user.td.widgetcommonevent.client.type.ChangeTableRequestType;
import org.gcube.portlets.user.td.widgetcommonevent.client.type.ChangeTableWhy;
import org.gcube.portlets.user.td.widgetcommonevent.client.type.SessionExpiredType;
import org.gcube.portlets.user.td.widgetcommonevent.shared.OperationResult;
import org.gcube.portlets.user.td.widgetcommonevent.shared.RequestProperties;
import org.gcube.portlets.user.td.widgetcommonevent.shared.RequestPropertiesParameterType;
import org.gcube.portlets.user.td.widgetcommonevent.shared.TRId;
import org.gcube.portlets.user.td.widgetcommonevent.shared.geospatial.GeospatialCoordinatesType;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnData;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnDataType;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnTypeCode;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.web.bindery.event.shared.EventBus;
import com.sencha.gxt.cell.core.client.ButtonCell.IconAlign;
import com.sencha.gxt.cell.core.client.form.ComboBoxCell.TriggerAction;
import com.sencha.gxt.core.client.dom.ScrollSupport.ScrollMode;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.core.client.util.ToggleGroup;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.widget.core.client.FramedPanel;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.ComboBox;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.form.Radio;
import com.sencha.gxt.widget.core.client.info.Info;

/**
 * 
 * @author "Giancarlo Panichi" <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 * 
 */
public class GeospatialCreateCoordinatesPanel extends FramedPanel implements
		MonitorDialogListener {
	private static final String WIDTH = "640px";
	private static final String HEIGHT = "520px";

	private TRId trId;
	private EventBus eventBus;
	private GeospatialCoordinatesType gsCoordinatesType;
	private boolean created;

	private ArrayList<ColumnData> columns;
	private ArrayList<ColumnData> quadrantColumns;
	private VerticalLayoutContainer vl;

	private TextButton createCoordinatesButton;
	private ComboBox<ColumnData> comboLatitude;
	private ComboBox<ColumnData> comboLongitude;
	private ComboBox<GeospatialCoordinatesType> comboGsCoordinatesType;
	private ListStore<ColumnData> storeComboLatitude;
	private ListStore<ColumnData> storeComboLongitude;
	private ListStore<ColumnData> storeComboQuadrant;
	private ComboBox<ColumnData> comboQuadrant;
	private Radio hasQuadrantTrue;
	private Radio hasQuadrantFalse;
	private FieldLabel comboQuadrantLabel;
	private FieldLabel hasQuadrantLabel;

	private ComboBox<Resolution> comboResolution;
	private ListStore<Resolution> storeComboResolution;

	public GeospatialCreateCoordinatesPanel(TRId trId,
			RequestProperties requestProperties, EventBus eventBus) {
		super();
		this.trId = trId;

		this.gsCoordinatesType = (GeospatialCoordinatesType) requestProperties
				.getMap().get(RequestPropertiesParameterType.Coordinates);

		this.eventBus = eventBus;
		this.created = false;
		forceLayoutOnResize = true;
		retrieveColumns();

	}

	protected void testCreated() {
		if (created) {
			updateCombo();
		} else {
			created = true;
			create();
		}
	}

	protected void updateCombo() {
		storeComboLatitude.clear();
		storeComboLatitude.addAll(columns);
		storeComboLatitude.commitChanges();
		comboLatitude.reset();
		comboLatitude.clear();

		storeComboLongitude.clear();
		storeComboLongitude.addAll(columns);
		storeComboLongitude.commitChanges();
		comboLongitude.reset();
		comboLongitude.clear();

		storeComboQuadrant.clear();
		storeComboQuadrant.addAll(quadrantColumns);
		storeComboQuadrant.commitChanges();
		comboQuadrant.reset();
		comboQuadrant.clear();

		updateForCoordinatesType();

	}

	protected void create() {

		setWidth(WIDTH);
		setHeight(HEIGHT);
		setHeaderVisible(false);
		setBodyBorder(false);

		Log.debug("Create GeospatialCreateCoordinatesPanel(): ["
				+ trId.toString() + " GeospatialCoordinatesType: "
				+ gsCoordinatesType + "]");

		// Column Propierties
		ColumnDataPropertiesCombo propsColumnData = GWT
				.create(ColumnDataPropertiesCombo.class);

		// Latitude
		storeComboLatitude = new ListStore<ColumnData>(propsColumnData.id());
		storeComboLatitude.addAll(columns);

		comboLatitude = new ComboBox<ColumnData>(storeComboLatitude,
				propsColumnData.label());
		Log.trace("Combo Latide created");

		addHandlersForComboColumnLatitude(propsColumnData.label());

		comboLatitude.setEmptyText("Select a column...");
		comboLatitude.setWidth(191);
		comboLatitude.setTypeAhead(false);
		comboLatitude.setEditable(false);
		comboLatitude.setTriggerAction(TriggerAction.ALL);

		// Longitude
		storeComboLongitude = new ListStore<ColumnData>(propsColumnData.id());
		storeComboLongitude.addAll(columns);

		comboLongitude = new ComboBox<ColumnData>(storeComboLongitude,
				propsColumnData.label());
		Log.trace("Combo Longitude created");

		addHandlersForComboColumnLongitude(propsColumnData.label());

		comboLongitude.setEmptyText("Select a column...");
		comboLongitude.setWidth(191);
		comboLongitude.setTypeAhead(false);
		comboLongitude.setEditable(false);
		comboLongitude.setTriggerAction(TriggerAction.ALL);

		// Geospatial Column Type
		GeospatialCoordinatesTypePropertiesCombo propsGeospatialCoordinatesType = GWT
				.create(GeospatialCoordinatesTypePropertiesCombo.class);

		ListStore<GeospatialCoordinatesType> storeComboGsCoordinatesType = new ListStore<GeospatialCoordinatesType>(
				propsGeospatialCoordinatesType.id());
		storeComboGsCoordinatesType.addAll(GeospatialCoordinatesType.getList());

		comboGsCoordinatesType = new ComboBox<GeospatialCoordinatesType>(
				storeComboGsCoordinatesType,
				propsGeospatialCoordinatesType.label());
		Log.trace("Combo Geospatial Column Type created");

		addHandlersForComboGsCoordinatesType(propsGeospatialCoordinatesType
				.label());

		comboGsCoordinatesType.setEmptyText("Select a type...");
		comboGsCoordinatesType.setWidth(191);
		comboGsCoordinatesType.setTypeAhead(false);
		comboGsCoordinatesType.setEditable(false);
		comboGsCoordinatesType.setTriggerAction(TriggerAction.ALL);

		// Resolution
		ResolutionPropertiesCombo propsResolution = GWT
				.create(ResolutionPropertiesCombo.class);

		storeComboResolution = new ListStore<Resolution>(propsResolution.id());
		storeComboResolution
				.addAll(ResolutionStore.getStoreCSquareResolution());

		comboResolution = new ComboBox<Resolution>(storeComboResolution,
				propsResolution.value());
		Log.trace("Combo Resolution created");

		addHandlersForComboResolution(propsResolution.value());

		comboResolution.setEmptyText("Select a resolution...");
		comboResolution.setValue(ResolutionStore
				.getStoreCSquareResolutionDefault());
		comboResolution.setWidth(191);
		comboResolution.setTypeAhead(false);
		comboResolution.setEditable(false);
		comboResolution.setTriggerAction(TriggerAction.ALL);

		FieldLabel resolutionLabel = new FieldLabel(comboResolution,
				"Resolution");

		// Has Quadrant
		hasQuadrantTrue = new Radio();
		hasQuadrantTrue.setBoxLabel("True");
		hasQuadrantTrue.setValue(true);

		hasQuadrantFalse = new Radio();
		hasQuadrantFalse.setBoxLabel("False");

		ToggleGroup hasQuadrantGroup = new ToggleGroup();
		hasQuadrantGroup.add(hasQuadrantTrue);
		hasQuadrantGroup.add(hasQuadrantFalse);

		hasQuadrantGroup
				.addValueChangeHandler(new ValueChangeHandler<HasValue<Boolean>>() {

					@Override
					public void onValueChange(
							ValueChangeEvent<HasValue<Boolean>> event) {
						try {
							if (hasQuadrantTrue.getValue()) {
								if (quadrantColumns == null
										|| quadrantColumns.size() < 1) {
									Log.debug("Attention no Integer column is present in the tabular resource");
									UtilsGXT3
											.alert("Attention",
													"No Integer column is present in the tabular resource!");

								}
								comboQuadrantLabel.setVisible(true);

							} else {
								comboQuadrantLabel.setVisible(false);
							}

							forceLayout();

						} catch (Exception e) {
							Log.error("ToggleGroup: onValueChange "
									+ e.getLocalizedMessage());
						}

					}
				});

		HorizontalPanel hasQuadrantPanel = new HorizontalPanel();
		hasQuadrantPanel.add(hasQuadrantTrue);
		hasQuadrantPanel.add(hasQuadrantFalse);

		hasQuadrantLabel = new FieldLabel(hasQuadrantPanel, "Has Quadrant");
		hasQuadrantLabel
				.setToolTip("Select true if you want select quadrant column");

		// Quadrant
		storeComboQuadrant = new ListStore<ColumnData>(propsColumnData.id());
		storeComboQuadrant.addAll(quadrantColumns);

		comboQuadrant = new ComboBox<ColumnData>(storeComboQuadrant,
				propsColumnData.label());
		Log.trace("Combo Quadrant created");

		addHandlersForComboQuadrant(propsColumnData.label());

		comboQuadrant.setEmptyText("Select a column...");
		comboQuadrant.setWidth(191);
		comboQuadrant.setTypeAhead(false);
		comboQuadrant.setEditable(false);
		comboQuadrant.setTriggerAction(TriggerAction.ALL);

		comboQuadrantLabel = new FieldLabel(comboQuadrant, "Quadrant");

		// Create
		createCoordinatesButton = new TextButton("Create");
		createCoordinatesButton.setIcon(ResourceBundle.INSTANCE
				.geospatialCoordinates());
		createCoordinatesButton.setIconAlign(IconAlign.RIGHT);
		createCoordinatesButton.setTitle("Create Geospatial Coordinates");

		createCoordinatesButton.addSelectHandler(new SelectHandler() {

			public void onSelect(SelectEvent event) {
				onGeospatialCreateCoordinates();

			}
		});

		vl = new VerticalLayoutContainer();
		vl.setScrollMode(ScrollMode.AUTO);
		vl.setAdjustForScroll(true);

		vl.add(new FieldLabel(comboLongitude, "Longitude"),
				new VerticalLayoutData(1, -1));
		vl.add(new FieldLabel(comboLatitude, "Latitude"),
				new VerticalLayoutData(1, -1));

		vl.add(new FieldLabel(comboGsCoordinatesType, "Type"),
				new VerticalLayoutData(1, -1));

		vl.add(resolutionLabel, new VerticalLayoutData(1, -1));

		vl.add(hasQuadrantLabel, new VerticalLayoutData(-1, -1));

		vl.add(comboQuadrantLabel, new VerticalLayoutData(1, -1));

		vl.add(createCoordinatesButton, new VerticalLayoutData(-1, -1,
				new Margins(10, 0, 10, 0)));

		add(vl);

		updateForCoordinatesType();

	}

	private void addHandlersForComboResolution(
			final LabelProvider<Resolution> value) {
		comboResolution.addSelectionHandler(new SelectionHandler<Resolution>() {

			@Override
			public void onSelection(SelectionEvent<Resolution> event) {
				Info.display(
						"Resolution Selected",
						"You selected "
								+ (event.getSelectedItem() == null ? "nothing"
										: value.getLabel(event
												.getSelectedItem()) + "!"));
				Log.debug("Reolution selected: " + event.getSelectedItem());
				Resolution resolution = event.getSelectedItem();
				updatedResolution(resolution);

			}

		});

	}

	protected void updatedResolution(Resolution resolution) {

	}

	private void addHandlersForComboColumnLatitude(
			final LabelProvider<ColumnData> labelProvider) {
		comboLatitude.addSelectionHandler(new SelectionHandler<ColumnData>() {

			@Override
			public void onSelection(SelectionEvent<ColumnData> event) {
				Info.display(
						"Latitude Selected",
						"You selected "
								+ (event.getSelectedItem() == null ? "nothing"
										: labelProvider.getLabel(event
												.getSelectedItem()) + "!"));
				Log.debug("Latitude selected: " + event.getSelectedItem());
				ColumnData latitude = event.getSelectedItem();
				updatedLatitude(latitude);

			}

		});

	}

	protected void updatedLatitude(ColumnData latitude) {
		// TODO Auto-generated method stub

	}

	private void addHandlersForComboColumnLongitude(
			final LabelProvider<ColumnData> labelProvider) {
		comboLongitude.addSelectionHandler(new SelectionHandler<ColumnData>() {

			@Override
			public void onSelection(SelectionEvent<ColumnData> event) {
				Info.display(
						"Longitude Selected",
						"You selected "
								+ (event.getSelectedItem() == null ? "nothing"
										: labelProvider.getLabel(event
												.getSelectedItem()) + "!"));
				Log.debug("Longitude selected: " + event.getSelectedItem());
				ColumnData longitude = event.getSelectedItem();
				updatedLongitude(longitude);

			}

		});

	}

	protected void updatedLongitude(ColumnData longitude) {
		// TODO Auto-generated method stub

	}

	private void addHandlersForComboGsCoordinatesType(
			final LabelProvider<GeospatialCoordinatesType> labelProvider) {
		comboGsCoordinatesType
				.addSelectionHandler(new SelectionHandler<GeospatialCoordinatesType>() {

					@Override
					public void onSelection(
							SelectionEvent<GeospatialCoordinatesType> event) {
						Info.display(
								"Type Selected",
								"You selected "
										+ (event.getSelectedItem() == null ? "nothing"
												: labelProvider.getLabel(event
														.getSelectedItem())
														+ "!"));
						Log.debug("Type selected: " + event.getSelectedItem());
						GeospatialCoordinatesType type = event
								.getSelectedItem();
						updatedComboGsCoordinatesType(type);

					}

				});

	}

	protected void updatedComboGsCoordinatesType(GeospatialCoordinatesType type) {
		gsCoordinatesType = type;
		updateForCoordinatesType();

	}

	protected void updateForCoordinatesType() {
		switch (gsCoordinatesType) {
		case C_SQUARE:
			hasQuadrantLabel.setVisible(false);
			comboQuadrantLabel.setVisible(false);
			comboGsCoordinatesType.setValue(gsCoordinatesType);
			storeComboResolution.clear();
			storeComboResolution.addAll(ResolutionStore
					.getStoreCSquareResolution());
			storeComboResolution.commitChanges();
			comboResolution.clear();
			comboResolution.reset();
			comboResolution.setValue(ResolutionStore
					.getStoreCSquareResolutionDefault());
			createCoordinatesButton.setIcon(ResourceBundle.INSTANCE
					.geospatialCSquare());

			break;
		case OCEAN_AREA:
			if (quadrantColumns == null || quadrantColumns.size() < 1) {
				hasQuadrantTrue.setValue(false);
				hasQuadrantFalse.setValue(true);
				comboQuadrantLabel.setVisible(false);
			} else {
				hasQuadrantTrue.setValue(true);
				hasQuadrantFalse.setValue(false);
				comboQuadrantLabel.setVisible(true);

			}
			storeComboResolution.clear();
			storeComboResolution.addAll(ResolutionStore
					.getStoreOceanAreaResolution());
			storeComboResolution.commitChanges();
			comboResolution.clear();
			comboResolution.reset();
			comboResolution.setValue(ResolutionStore
					.getStoreOceanAreaResolutionDefault());
			hasQuadrantLabel.setVisible(true);
			comboGsCoordinatesType.setValue(gsCoordinatesType);
			createCoordinatesButton.setIcon(ResourceBundle.INSTANCE
					.geospatialOceanArea());
			break;
		default:
			storeComboResolution.clear();
			storeComboResolution.addAll(ResolutionStore
					.getStoreCSquareResolution());
			storeComboResolution.commitChanges();
			comboResolution.clear();
			comboResolution.reset();
			comboResolution.setValue(ResolutionStore
					.getStoreCSquareResolutionDefault());
			hasQuadrantLabel.setVisible(false);
			comboQuadrantLabel.setVisible(false);
			createCoordinatesButton.setIcon(ResourceBundle.INSTANCE
					.geospatialCoordinates());
			break;
		}

		onResize();
		forceLayout();
	}

	private void addHandlersForComboQuadrant(
			final LabelProvider<ColumnData> labelProvider) {
		comboQuadrant.addSelectionHandler(new SelectionHandler<ColumnData>() {

			@Override
			public void onSelection(SelectionEvent<ColumnData> event) {
				Info.display(
						"Quadrant Selected",
						"You selected "
								+ (event.getSelectedItem() == null ? "nothing"
										: labelProvider.getLabel(event
												.getSelectedItem()) + "!"));
				Log.debug("Quadrant selected: " + event.getSelectedItem());
				ColumnData quadrant = event.getSelectedItem();
				updatedQuadrant(quadrant);

			}

		});

	}

	protected void updatedQuadrant(ColumnData quadrant) {
		// TODO Auto-generated method stub

	}

	protected boolean hasQuadrant() {
		if (hasQuadrantTrue.getValue()) {
			return true;
		} else {
			return false;
		}
	}

	protected void onGeospatialCreateCoordinates() {
		ColumnData longitude = comboLongitude.getCurrentValue();
		if (longitude != null) {
			ColumnData latitude = comboLatitude.getCurrentValue();
			if (latitude != null) {
				Resolution resolution = comboResolution.getCurrentValue();
				if (resolution != null) {

					GeospatialCoordinatesType type = comboGsCoordinatesType
							.getCurrentValue();
					if (type != null) {
						GeospatialCreateCoordinatesSession gsCreateCoordinatesSession;
						switch (type) {
						case C_SQUARE:
							gsCreateCoordinatesSession = new GeospatialCreateCoordinatesSession(
									trId, latitude, longitude, type, false,
									null, resolution.getValue());
							callGeospatialCreateCoordinates(gsCreateCoordinatesSession);
							break;
						case OCEAN_AREA:
							if (hasQuadrant()) {
								ColumnData quadrant = comboQuadrant
										.getCurrentValue();
								if (quadrant != null) {
									gsCreateCoordinatesSession = new GeospatialCreateCoordinatesSession(
											trId, latitude, longitude, type,
											true, quadrant, resolution.getValue());
									callGeospatialCreateCoordinates(gsCreateCoordinatesSession);
								} else {
									UtilsGXT3.alert("Attention",
											"Select Quadrant column!");
									break;
								}

							} else {
								gsCreateCoordinatesSession = new GeospatialCreateCoordinatesSession(
										trId, latitude, longitude, type, false,
										null, resolution.getValue());
								callGeospatialCreateCoordinates(gsCreateCoordinatesSession);
							}

							break;
						default:
							UtilsGXT3
									.alert("Attention",
											"Select valid geospatial coordinates type!");
							break;

						}

					} else {
						UtilsGXT3.alert("Attention",
								"Invalid Geospatial Coordinates Type!");
					}
				} else {
					UtilsGXT3.alert("Attention", "Select Resolution!");
				}

			} else {
				UtilsGXT3.alert("Attention", "Select Latitude!");
			}
		} else {
			UtilsGXT3.alert("Attention", "Select Longitude!");
		}
	}

	private void callGeospatialCreateCoordinates(
			GeospatialCreateCoordinatesSession gsCreateCoordinatesSession) {
		TDGWTServiceAsync.INSTANCE.startGeospatialCreateCoordinates(
				gsCreateCoordinatesSession, new AsyncCallback<String>() {

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
									Log.debug("Create Geospatial Coordinates Error: "
											+ caught.getLocalizedMessage());
									UtilsGXT3
											.alert("Error Creating Geospatial Coordinates",
													"Error creating geospatial coordinates: "
															+ caught.getLocalizedMessage());
								}
							}
						}
					}

					public void onSuccess(String taskId) {
						openMonitorDialog(taskId);

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
						columns = new ArrayList<ColumnData>();
						quadrantColumns = new ArrayList<ColumnData>();

						for (ColumnData column : result) {
							ColumnTypeCode columnTypeCode = ColumnTypeCode
									.getColumnTypeCodeFromId(column
											.getTypeCode());
							if (columnTypeCode
									.compareTo(ColumnTypeCode.ATTRIBUTE) == 0
									|| columnTypeCode
											.compareTo(ColumnTypeCode.MEASURE) == 0) {

								ColumnDataType columnDataType = ColumnDataType
										.getColumnDataTypeFromId(column
												.getDataTypeName());
								if (columnDataType
										.compareTo(ColumnDataType.Numeric) == 0) {
									columns.add(column);
								} else {
									if (columnDataType
											.compareTo(ColumnDataType.Integer) == 0) {
										columns.add(column);
										quadrantColumns.add(column);
									}
								}
							}

						}
						if (columns.size() < 1) {
							Log.debug("Attention no Integer or Numeric column is present in the tabular resource");
							UtilsGXT3
									.alert("Attention",
											"No Integer or Numeric column is present in the tabular resource!");
						}
						testCreated();

					}

				});

	}

	public void update(TRId trId, RequestProperties requestProperties) {
		this.trId = trId;
		this.gsCoordinatesType = (GeospatialCoordinatesType) requestProperties
				.getMap().get(RequestPropertiesParameterType.Coordinates);

		retrieveColumns();
	}

	protected void close() {
		/*
		 * if (parent != null) { parent.close(); }
		 */
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
				ChangeTableRequestType.GEOSPATIALCREATECOORDINATES,
				operationResult.getTrId(), why);
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
				ChangeTableRequestType.GEOSPATIALCREATECOORDINATES,
				operationResult.getTrId(), why);
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
