package org.gcube.portlets.user.td.tablewidget.client.geometry;

import java.util.ArrayList;

import org.gcube.portlets.user.td.expressionwidget.client.properties.ColumnDataPropertiesCombo;
import org.gcube.portlets.user.td.gwtservice.client.rpc.TDGWTServiceAsync;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTIsFinalException;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTIsLockedException;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTSessionExpiredException;
import org.gcube.portlets.user.td.gwtservice.shared.geometry.GeometryCreatePointSession;
import org.gcube.portlets.user.td.monitorwidget.client.MonitorDialog;
import org.gcube.portlets.user.td.monitorwidget.client.MonitorDialogListener;
import org.gcube.portlets.user.td.tablewidget.client.resources.ResourceBundle;
import org.gcube.portlets.user.td.tablewidget.client.util.UtilsGXT3;
import org.gcube.portlets.user.td.widgetcommonevent.client.event.ChangeTableRequestEvent;
import org.gcube.portlets.user.td.widgetcommonevent.client.event.SessionExpiredEvent;
import org.gcube.portlets.user.td.widgetcommonevent.client.type.ChangeTableRequestType;
import org.gcube.portlets.user.td.widgetcommonevent.client.type.ChangeTableWhy;
import org.gcube.portlets.user.td.widgetcommonevent.client.type.SessionExpiredType;
import org.gcube.portlets.user.td.widgetcommonevent.shared.OperationResult;
import org.gcube.portlets.user.td.widgetcommonevent.shared.TRId;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnData;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnDataType;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnTypeCode;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.event.shared.EventBus;
import com.sencha.gxt.cell.core.client.ButtonCell.IconAlign;
import com.sencha.gxt.cell.core.client.form.ComboBoxCell.TriggerAction;
import com.sencha.gxt.core.client.dom.ScrollSupport.ScrollMode;
import com.sencha.gxt.core.client.util.Margins;
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
import com.sencha.gxt.widget.core.client.form.TextField;
import com.sencha.gxt.widget.core.client.info.Info;

/**
 * 
 * @author "Giancarlo Panichi" <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 * 
 */
public class GeometryCreatePointPanel extends FramedPanel implements
		MonitorDialogListener {
	private static final String WIDTH = "640px";
	private static final String HEIGHT = "520px";

	private TRId trId;
	private EventBus eventBus;
	private boolean created;

	private ArrayList<ColumnData> columns;
	private VerticalLayoutContainer vl;

	private TextButton createPointButton;
	private ComboBox<ColumnData> comboLatitude;
	private ComboBox<ColumnData> comboLongitude;
	private ListStore<ColumnData> storeComboLatitude;
	private ListStore<ColumnData> storeComboLongitude;
	private TextField columnLabelField;

	public GeometryCreatePointPanel(TRId trId, EventBus eventBus) {
		super();
		this.trId = trId;

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

		onResize();
		forceLayout();

	}

	protected void create() {

		setWidth(WIDTH);
		setHeight(HEIGHT);
		setHeaderVisible(false);
		setBodyBorder(false);

		Log.debug("Create GeometryPointPanel(): [" + trId.toString() + "]");

		// Column Propierties
		ColumnDataPropertiesCombo propsColumnData = GWT
				.create(ColumnDataPropertiesCombo.class);

		// Latitude
		storeComboLatitude = new ListStore<ColumnData>(propsColumnData.id());
		storeComboLatitude.addAll(columns);

		comboLatitude = new ComboBox<ColumnData>(storeComboLatitude,
				propsColumnData.label());
		Log.trace("Combo ColumnData created");

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
		Log.trace("Combo ColumnData created");

		addHandlersForComboColumnLongitude(propsColumnData.label());

		comboLongitude.setEmptyText("Select a column...");
		comboLongitude.setWidth(191);
		comboLongitude.setTypeAhead(false);
		comboLongitude.setEditable(false);
		comboLongitude.setTriggerAction(TriggerAction.ALL);

		// Column Label
		columnLabelField = new TextField();
		columnLabelField.setValue("Points");
		FieldLabel columnLab = new FieldLabel(columnLabelField, "Column Label");

		// Create
		createPointButton = new TextButton("Create");
		createPointButton.setIcon(ResourceBundle.INSTANCE.geometryPoint());
		createPointButton.setIconAlign(IconAlign.RIGHT);
		createPointButton.setTitle("Create Point");

		createPointButton.addSelectHandler(new SelectHandler() {

			public void onSelect(SelectEvent event) {
				onGeometryCreatePoint();

			}
		});

		vl = new VerticalLayoutContainer();
		vl.setScrollMode(ScrollMode.AUTO);
		vl.setAdjustForScroll(true);

		vl.add(new FieldLabel(comboLongitude, "Longitude"),
				new VerticalLayoutData(1, -1));
		vl.add(new FieldLabel(comboLatitude, "Latitude"),
				new VerticalLayoutData(1, -1));
		
		vl.add(columnLab, new VerticalLayoutData(1, -1));

		vl.add(createPointButton, new VerticalLayoutData(-1, -1, new Margins(
				10, 0, 10, 0)));

		add(vl);

		onResize();
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

	protected void onGeometryCreatePoint() {
		ColumnData longitude = comboLongitude.getCurrentValue();
		if (longitude != null) {
			ColumnData latitude = comboLatitude.getCurrentValue();
			if (latitude != null) {
				String columnLab = columnLabelField.getCurrentValue();
				if (columnLab != null && !columnLab.isEmpty()) {
					GeometryCreatePointSession geoCreatePointSession = new GeometryCreatePointSession(
							trId, latitude, longitude, columnLab);
					callGeometryCreatePoint(geoCreatePointSession);
				} else {
					UtilsGXT3.alert("Attention", "Select a column label!");
				}
			} else {
				UtilsGXT3.alert("Attention", "Select Latitude!");
			}
		} else {
			UtilsGXT3.alert("Attention", "Select Longitude!");
		}
	}

	private void callGeometryCreatePoint(
			GeometryCreatePointSession geometryCreatPointSession) {
		TDGWTServiceAsync.INSTANCE.startGeometryCreatePoint(
				geometryCreatPointSession, new AsyncCallback<String>() {

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
									Log.debug("Create a Point Error: "
											+ caught.getLocalizedMessage());
									UtilsGXT3
											.alert("Error Creating a point",
													"Error creating a point: "
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
										.compareTo(ColumnDataType.Integer) == 0
										|| columnDataType
												.compareTo(ColumnDataType.Numeric) == 0) {
									columns.add(column);

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

	public void update(TRId trId) {
		this.trId = trId;

		retrieveColumns();
	}

	protected void close() {
		/*
		 * if (parent != null) { parent.close(); }
		 */
	}

	//
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
