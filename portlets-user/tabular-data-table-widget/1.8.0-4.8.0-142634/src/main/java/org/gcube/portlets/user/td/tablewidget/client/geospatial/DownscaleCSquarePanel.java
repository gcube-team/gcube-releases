package org.gcube.portlets.user.td.tablewidget.client.geospatial;

import java.util.ArrayList;

import org.gcube.portlets.user.td.expressionwidget.client.properties.ColumnDataPropertiesCombo;
import org.gcube.portlets.user.td.gwtservice.client.rpc.TDGWTServiceAsync;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTIsFinalException;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTIsLockedException;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTSessionExpiredException;
import org.gcube.portlets.user.td.gwtservice.shared.geospatial.GeospatialDownscaleCSquareSession;
import org.gcube.portlets.user.td.monitorwidget.client.MonitorDialog;
import org.gcube.portlets.user.td.monitorwidget.client.MonitorDialogListener;
import org.gcube.portlets.user.td.tablewidget.client.TableWidgetMessages;
import org.gcube.portlets.user.td.tablewidget.client.resources.ResourceBundle;
import org.gcube.portlets.user.td.tablewidget.client.util.UtilsGXT3;
import org.gcube.portlets.user.td.widgetcommonevent.client.CommonMessages;
import org.gcube.portlets.user.td.widgetcommonevent.client.event.ChangeTableRequestEvent;
import org.gcube.portlets.user.td.widgetcommonevent.client.event.SessionExpiredEvent;
import org.gcube.portlets.user.td.widgetcommonevent.client.type.ChangeTableRequestType;
import org.gcube.portlets.user.td.widgetcommonevent.client.type.ChangeTableWhy;
import org.gcube.portlets.user.td.widgetcommonevent.client.type.SessionExpiredType;
import org.gcube.portlets.user.td.widgetcommonevent.shared.OperationResult;
import org.gcube.portlets.user.td.widgetcommonevent.shared.TRId;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnData;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnDataType;

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

/**
 * 
 * @author "Giancarlo Panichi" <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 * 
 */
public class DownscaleCSquarePanel extends FramedPanel implements
		MonitorDialogListener {
	private static final String WIDTH = "640px";
	private static final String HEIGHT = "520px";

	private TRId trId;
	private EventBus eventBus;
	private boolean created;

	private ArrayList<ColumnData> columns;
	private VerticalLayoutContainer vl;

	private TextButton btnDownscale;
	private ComboBox<ColumnData> comboCSquareColumn;
	private ListStore<ColumnData> storeComboCSquare;

	private ComboBox<Resolution> comboDownscale;
	private ListStore<Resolution> storeComboDownscale;
	private String columnLocalId;
	
	private CommonMessages msgsCommon;
	private TableWidgetMessages msgs;
	
	public DownscaleCSquarePanel(TRId trId, String columnLocalId,
			EventBus eventBus) {
		super();
		this.trId = trId;
		this.columnLocalId = columnLocalId;
		this.eventBus = eventBus;
		this.created = false;
		forceLayoutOnResize = true;
		initMessages();
		retrieveColumns();

	}
	
	protected void initMessages() {
		msgsCommon = GWT.create(CommonMessages.class);
		msgs = GWT.create(TableWidgetMessages.class);

	}
	
	protected void testCreated() {
		if (created) {
			updateCombo();
		} else {
			created = true;
			create();
		}
		setComboStatus();
	}

	protected void updateCombo() {
		storeComboCSquare.clear();
		storeComboCSquare.addAll(columns);
		storeComboCSquare.commitChanges();
		comboCSquareColumn.reset();
		comboCSquareColumn.clear();
		
		
		storeComboDownscale.clear();
		storeComboDownscale.addAll(ResolutionStore.getStoreCSquareResolution());
		storeComboDownscale.commitChanges();
		comboDownscale.reset();
		comboDownscale.clear();

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

		// CSquare column
		storeComboCSquare = new ListStore<ColumnData>(propsColumnData.id());
		storeComboCSquare.addAll(columns);

		comboCSquareColumn = new ComboBox<ColumnData>(storeComboCSquare,
				propsColumnData.label());
		Log.trace("Combo ColumnData created");

		addHandlersForComboCSquare(propsColumnData.label());

		comboCSquareColumn.setEmptyText(msgs.comboCSquareColumnEmptyText());
		comboCSquareColumn.setWidth(191);
		comboCSquareColumn.setTypeAhead(false);
		comboCSquareColumn.setEditable(false);
		comboCSquareColumn.setTriggerAction(TriggerAction.ALL);
		
		FieldLabel comboCSquareColumnLabel=new FieldLabel(comboCSquareColumn, msgs.comboCSquareColumnLabel());
		
		// Downscale combo
		ResolutionPropertiesCombo propsDownscale = GWT
				.create(ResolutionPropertiesCombo.class);
		
		
		storeComboDownscale = new ListStore<Resolution>(propsDownscale.id());
		storeComboDownscale.addAll(ResolutionStore.getStoreCSquareResolution());

		comboDownscale = new ComboBox<Resolution>(storeComboDownscale,
				propsDownscale.value());
		Log.trace("Combo Downscale created");

		addHandlersForComboDownscale(propsDownscale.value());

		comboDownscale.setEmptyText(msgs.comboDownscaleEmptyText());
		comboDownscale.setWidth(191);
		comboDownscale.setTypeAhead(false);
		comboDownscale.setEditable(false);
		comboDownscale.setTriggerAction(TriggerAction.ALL);

		FieldLabel comboDownscaleLabel=new FieldLabel(comboDownscale, msgs.comboDownscaleLabel());

		// Create
		btnDownscale = new TextButton(msgs.btnDownscaleText());
		btnDownscale.setIcon(ResourceBundle.INSTANCE.downscaleCSquare());
		btnDownscale.setIconAlign(IconAlign.RIGHT);
		btnDownscale.setToolTip(msgs.btnDownscaleToolTip());

		btnDownscale.addSelectHandler(new SelectHandler() {

			public void onSelect(SelectEvent event) {
				onGeometryCreatePoint();

			}
		});

		vl = new VerticalLayoutContainer();
		vl.setScrollMode(ScrollMode.AUTO);
		vl.setAdjustForScroll(true);

		vl.add(comboCSquareColumnLabel,
				new VerticalLayoutData(1, -1));
		vl.add(comboDownscaleLabel,
				new VerticalLayoutData(1, -1));
		
		vl.add(btnDownscale, new VerticalLayoutData(-1, -1, new Margins(10,
				0, 10, 0)));

		add(vl);
		
		
		
		onResize();
		
	}
	
	
	
	protected void setComboStatus() {
		Log.debug("columnLocalId: " + columnLocalId);
		if (columnLocalId != null) {
			for (ColumnData cd : columns) {
				Log.debug("Column:" + cd.getColumnId());
				if (cd.getColumnId().compareTo(columnLocalId) == 0) {
					if (cd.isViewColumn()) {
						
					} else {
						comboCSquareColumn.setValue(cd);
					}
					return;
				}
			}
		} else {
			
		}
	}
	

	private void addHandlersForComboCSquare(
			final LabelProvider<ColumnData> labelProvider) {
		comboCSquareColumn
				.addSelectionHandler(new SelectionHandler<ColumnData>() {

					@Override
					public void onSelection(SelectionEvent<ColumnData> event) {
						Log.debug("Latitude selected: "
								+ event.getSelectedItem());
						ColumnData csquareColumn = event.getSelectedItem();
						updatedCSquareColumn(csquareColumn);

					}

				});

	}

	protected void updatedCSquareColumn(ColumnData csquareColumn) {
		// TODO Auto-generated method stub

	}

	private void addHandlersForComboDownscale(
			final LabelProvider<Resolution> labelProvider) {
		comboDownscale.addSelectionHandler(new SelectionHandler<Resolution>() {

			@Override
			public void onSelection(SelectionEvent<Resolution> event) {
				Log.debug("Resolution selected: " + event.getSelectedItem());
				Resolution resolution = event.getSelectedItem();
				updatedResolution(resolution);

			}

		});

	}

	protected void updatedResolution(Resolution resolution) {
		// TODO Auto-generated method stub

	}

	protected void onGeometryCreatePoint() {
		ColumnData csquareColumn = comboCSquareColumn.getCurrentValue();
		if (csquareColumn != null) {
			Resolution downscale = comboDownscale.getCurrentValue();
			if (downscale != null) {
					GeospatialDownscaleCSquareSession geospatialDownscaleCSquareSession = new GeospatialDownscaleCSquareSession (
							trId, csquareColumn, downscale.getStringValue());
					callGeospatialDownscaleCSquare(geospatialDownscaleCSquareSession);
				
			} else {
				UtilsGXT3.alert(msgsCommon.attention(), msgs.attentionSelectResolution());
			}
		} else {
			UtilsGXT3.alert(msgsCommon.attention(), msgs.attentionSelectCSquareColumn());
		}
	}

	private void callGeospatialDownscaleCSquare(
			GeospatialDownscaleCSquareSession geospatialDownscaleCSquareSession) {
		TDGWTServiceAsync.INSTANCE.startGeospatialDownscaleCSquare(geospatialDownscaleCSquareSession
				, new AsyncCallback<String>() {

					public void onFailure(Throwable caught) {
						if (caught instanceof TDGWTSessionExpiredException) {
							eventBus.fireEvent(new SessionExpiredEvent(
									SessionExpiredType.EXPIREDONSERVER));
						} else {
							if (caught instanceof TDGWTIsLockedException) {
								Log.error(caught.getLocalizedMessage());
								UtilsGXT3.alert(msgsCommon.errorLocked(),
										caught.getLocalizedMessage());
							} else {
								if (caught instanceof TDGWTIsFinalException) {
									Log.error(caught.getLocalizedMessage());
									UtilsGXT3.alert(msgsCommon.errorFinal(),
											caught.getLocalizedMessage());
								} else {
									Log.debug("Geospatial Downscale C-Square: "
											+ caught.getLocalizedMessage());
									UtilsGXT3
											.alert(msgsCommon.error(),
												caught.getLocalizedMessage());
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
								UtilsGXT3.alert(msgsCommon.errorLocked(),
										caught.getLocalizedMessage());
							} else {
								if (caught instanceof TDGWTIsFinalException) {
									Log.error(caught.getLocalizedMessage());
									UtilsGXT3.alert(msgsCommon.errorFinal(),
											caught.getLocalizedMessage());
								} else {
									Log.error("load combo failure:"
											+ caught.getLocalizedMessage());
									UtilsGXT3.alert(msgsCommon.error(),
											msgs.errorRetrievingColumsOfTabularResourceFixed()
													+ trId.getId());
								}
							}
						}

					}

					public void onSuccess(ArrayList<ColumnData> result) {
						Log.trace("loaded " + result.size() + " ColumnData");
						columns = new ArrayList<ColumnData>();

						for (ColumnData column : result) {
							ColumnDataType columnDataType = ColumnDataType
									.getColumnDataTypeFromId(column
											.getDataTypeName());
							if (columnDataType
									.compareTo(ColumnDataType.Text) == 0) {
								columns.add(column);

							}

						}
						if (columns.size() < 1) {
							Log.debug("Attention no text column is present in the tabular resource. C-Square column is a text column data type!");
							UtilsGXT3
									.alert(msgsCommon.attention(),
											msgs.attentionNoTextColumnIsPresentCSquareIsTextColumn());
						}
						testCreated();

					}

				});

	}

	public void update(TRId trId, String columnLocalId) {
		this.trId = trId;
		this.columnLocalId=columnLocalId;
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
				ChangeTableRequestType.DOWNSCALECSQUARE,
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
				ChangeTableRequestType.DOWNSCALECSQUARE,
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
