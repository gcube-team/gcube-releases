package org.gcube.portlets.user.td.columnwidget.client.replace;

import java.util.Date;

import org.gcube.portlets.user.td.columnwidget.client.dimension.DimensionRowSelectionDialog;
import org.gcube.portlets.user.td.columnwidget.client.dimension.DimensionRowSelectionListener;
import org.gcube.portlets.user.td.columnwidget.client.dimension.DimensionRowsProperties;
import org.gcube.portlets.user.td.columnwidget.client.resources.ResourceBundle;
import org.gcube.portlets.user.td.columnwidget.client.utils.UtilsGXT3;
import org.gcube.portlets.user.td.gwtservice.client.rpc.TDGWTServiceAsync;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTIsFinalException;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTIsLockedException;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTSessionExpiredException;
import org.gcube.portlets.user.td.gwtservice.shared.tr.DimensionRow;
import org.gcube.portlets.user.td.gwtservice.shared.tr.column.ReplaceColumnSession;
import org.gcube.portlets.user.td.monitorwidget.client.MonitorDialog;
import org.gcube.portlets.user.td.monitorwidget.client.MonitorDialogListener;
import org.gcube.portlets.user.td.widgetcommonevent.client.CommonMessages;
import org.gcube.portlets.user.td.widgetcommonevent.client.event.ChangeTableRequestEvent;
import org.gcube.portlets.user.td.widgetcommonevent.client.event.SessionExpiredEvent;
import org.gcube.portlets.user.td.widgetcommonevent.client.type.ChangeTableRequestType;
import org.gcube.portlets.user.td.widgetcommonevent.client.type.ChangeTableWhy;
import org.gcube.portlets.user.td.widgetcommonevent.client.type.SessionExpiredType;
import org.gcube.portlets.user.td.widgetcommonevent.shared.CellData;
import org.gcube.portlets.user.td.widgetcommonevent.shared.OperationResult;
import org.gcube.portlets.user.td.widgetcommonevent.shared.TRId;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnData;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.rpc.AsyncCallback;
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
import com.sencha.gxt.widget.core.client.event.TriggerClickEvent;
import com.sencha.gxt.widget.core.client.event.TriggerClickEvent.TriggerClickHandler;
import com.sencha.gxt.widget.core.client.form.ComboBox;
import com.sencha.gxt.widget.core.client.form.DateField;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.form.TextField;

/**
 * 
 * @author "Giancarlo Panichi" <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 * 
 */
public class ReplaceAllPanel extends FramedPanel implements
		DimensionRowSelectionListener, MonitorDialogListener {
	private DateTimeFormat sdf = DateTimeFormat.getFormat("yyyy-MM-dd");

	private String WIDTH = "500px";
	private String HEIGHT = "150px";
	private EventBus eventBus;
	private TRId trId;
	private CellData cellData;
	private ReplaceAllDialog parent;
	private ColumnData column;

	private DimensionRow dimensionRow;

	private ReplaceColumnSession replaceColumnSession;
	private ComboBox<DimensionRow> comboDimensionType;
	private FieldLabel comboDimensionTypeLabel;

	private TextField value;
	private DateField valueDate;
	private TextField replaceValue;
	private DateField replaceValueDate;
	private TextButton btnReplace;
	private TextButton btnClose;
	private boolean isDimension;
	private ReplaceAllMessages msgs;
	private CommonMessages msgsCommon;

	public ReplaceAllPanel(ReplaceAllDialog parent, TRId trId, CellData cellData,
			EventBus eventBus) {
		this.parent = parent;
		this.cellData = cellData;
		this.trId = trId;
		this.eventBus = eventBus;
		dimensionRow = null;
		Log.debug("ReplacePanel:[" + trId + ", CellData:" + cellData + "]");
		initMessages();
		initPanel();
		retrieveColumn();

	}
	
	protected void initMessages(){
		msgs = GWT.create(ReplaceAllMessages.class);
		msgsCommon = GWT.create(CommonMessages.class);
	}

	protected void initPanel() {
		setWidth(WIDTH);
		setHeight(HEIGHT);
		setHeaderVisible(false);
		setBodyBorder(false);
	}

	protected void retrieveColumn() {
		TDGWTServiceAsync.INSTANCE.getColumn(trId, cellData.getColumnName(),
				new AsyncCallback<ColumnData>() {

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
									Log.error("Error retrieving column: "
											+ caught.getMessage());
									UtilsGXT3.alert(msgsCommon.error(),
											caught.getMessage());
								}
							}
						}
					}

					public void onSuccess(ColumnData result) {
						Log.debug("Retrived column: " + result);
						column = result;

						if (result.isViewColumn()) {
							isDimension = true;
							createForDimension();
						} else {
							isDimension = false;
							create();
						}
					}

				});

	}

	protected void create() {
		if (column.getDataTypeName().compareTo("Date") == 0) {
			valueDate = new DateField();
			Date d = null;
			Log.debug("Date value: " + cellData.getValue());
			try {
				d = sdf.parse(cellData.getValue());
			} catch (Exception e) {
				Log.error("Unparseable using " + sdf);
			}
			if (d != null) {
				valueDate.setValue(d);
			}
			valueDate.setReadOnly(true);

			replaceValueDate = new DateField();

		} else {
			value = new TextField();
			value.setValue(cellData.getValue());
			value.setReadOnly(true);

			replaceValue = new TextField();
		}

		btnReplace = new TextButton(msgs.btnReplaceText());
		btnReplace.setIcon(ResourceBundle.INSTANCE.replaceAll());
		btnReplace.setIconAlign(IconAlign.RIGHT);
		btnReplace.setToolTip(msgs.btnReplaceToolTip());
		btnReplace.addSelectHandler(new SelectHandler() {

			public void onSelect(SelectEvent event) {
				Log.debug("Pressed Apply");
				replaceValue();

			}
		});

		btnClose = new TextButton(msgs.btnCloseText());
		btnClose.setIcon(ResourceBundle.INSTANCE.close());
		btnClose.setIconAlign(IconAlign.RIGHT);
		btnClose.setToolTip(msgs.btnCloseToolTip());
		btnClose.addSelectHandler(new SelectHandler() {

			public void onSelect(SelectEvent event) {
				Log.debug("Pressed Close");
				close();

			}
		});

		HBoxLayoutContainer flowButton = new HBoxLayoutContainer();
		flowButton.setHBoxLayoutAlign(HBoxLayoutAlign.MIDDLE);
		flowButton.setPack(BoxLayoutPack.CENTER);
		
		BoxLayoutData boxLayoutData=new BoxLayoutData(new Margins(2, 4, 2, 4));
		
		flowButton.add(btnReplace, boxLayoutData);
		flowButton.add(btnClose, boxLayoutData);

		VerticalLayoutContainer v = new VerticalLayoutContainer();
		if (column.getDataTypeName().compareTo("Date") == 0) {
			v.add(new FieldLabel(valueDate, msgs.currentValue()), new VerticalLayoutData(1,
					-1));
			v.add(new FieldLabel(replaceValueDate, msgs.replacement()),
					new VerticalLayoutData(1, -1));
		} else {
			v.add(new FieldLabel(value, msgs.currentValue()), new VerticalLayoutData(1, -1));
			v.add(new FieldLabel(replaceValue, msgs.replacement()),
					new VerticalLayoutData(1, -1));
		}
		v.add(flowButton, new VerticalLayoutData(1, 36,
				new Margins(5, 2, 5, 2)));
		add(v);

	}

	protected void createForDimension() {
		value = new TextField();
		value.setValue(cellData.getValue());
		value.setReadOnly(true);

		// comboDimensionType
		DimensionRowsProperties propsDimensionType = GWT
				.create(DimensionRowsProperties.class);
		ListStore<DimensionRow> storeComboDimensionType = new ListStore<DimensionRow>(
				propsDimensionType.rowId());

		comboDimensionType = new ComboBox<DimensionRow>(
				storeComboDimensionType, propsDimensionType.value());

		Log.trace("ComboDimensionType created");

		addHandlersForComboDimensionType(propsDimensionType.value());

		comboDimensionType.setEmptyText(msgs.selectAValue());
		comboDimensionType.setWidth(300);
		comboDimensionType.setEditable(false);
		comboDimensionType.setTriggerAction(TriggerAction.ALL);

		comboDimensionTypeLabel = new FieldLabel(comboDimensionType, msgs.replacement());

		//
		btnReplace = new TextButton(msgs.btnReplaceText());
		btnReplace.setIcon(ResourceBundle.INSTANCE.replace());
		btnReplace.setIconAlign(IconAlign.RIGHT);
		btnReplace.setToolTip(msgs.btnCloseToolTip());
		btnReplace.addSelectHandler(new SelectHandler() {

			public void onSelect(SelectEvent event) {
				Log.debug("Pressed Apply For Dimension");
				replaceValueForDimension();

			}
		});

		btnClose = new TextButton(msgs.btnCloseText());
		btnClose.setIcon(ResourceBundle.INSTANCE.close());
		btnClose.setIconAlign(IconAlign.RIGHT);
		btnClose.setTitle(msgs.btnCloseToolTip());
		btnClose.addSelectHandler(new SelectHandler() {

			public void onSelect(SelectEvent event) {
				Log.debug("Pressed Close For Dimension");
				close();

			}
		});

		HBoxLayoutContainer flowButton = new HBoxLayoutContainer();
		flowButton.setHBoxLayoutAlign(HBoxLayoutAlign.MIDDLE);
		flowButton.setPack(BoxLayoutPack.CENTER);
		
		BoxLayoutData boxLayoutData=new BoxLayoutData(new Margins(2, 4, 2, 4));
		flowButton.add(btnReplace, boxLayoutData);
		flowButton.add(btnClose, boxLayoutData);

		VerticalLayoutContainer v = new VerticalLayoutContainer();
		v.add(new FieldLabel(value, msgs.currentValue()), new VerticalLayoutData(1, -1));
		v.add(comboDimensionTypeLabel, new VerticalLayoutData(1, -1));
		v.add(flowButton, new VerticalLayoutData(1, 36,
				new Margins(5, 2, 5, 2)));
		add(v);

	}

	protected void replaceValue() {
		String rValue;

		if (column.getDataTypeName().compareTo("Date") == 0) {
			Date d = replaceValueDate.getCurrentValue();
			if (d == null) {
				UtilsGXT3.alert(msgsCommon.attention(), msgs.insertAValidReplaceValue());
				return;
			} else {
				String dateS = sdf.format(d);
				rValue = dateS;
			}

		} else {
			rValue = replaceValue.getCurrentValue();
			if (rValue == null) {
				UtilsGXT3.alert(msgsCommon.attention(), msgs.insertAValidReplaceValue());
				return;
			} else {
				String checkedValue = checkTypeData(rValue);
				if (checkedValue == null || checkedValue.isEmpty()) {
					UtilsGXT3.alert(msgsCommon.attention(),
							msgs.insertAValidReplaceValueForThisColumn());
					return;
				}
			}
		}
		callReplaceValue(rValue);

	}

	protected void replaceValueForDimension() {
		Log.debug("Current Dimension Row in combo: " + dimensionRow);
		if (dimensionRow == null) {
			UtilsGXT3.alert(msgsCommon.attention(), msgs.selectAValidValue());
		} else {
			callReplaceValue(dimensionRow.getRowId());
		}

	}

	protected String checkTypeData(String rValue) {
		String checked = null;
		try {
			if (column.getDataTypeName().compareTo("Boolean") == 0) {
				Boolean b = new Boolean(rValue);
				checked = b.toString();
			} else {
				if (column.getDataTypeName().compareTo("Date") == 0) {
					Date d = null;
					try {
						d = sdf.parse(value.getValue());
					} catch (Exception e) {
						Log.error("Unparseable using " + sdf);
						return null;
					}
					if (d != null) {
						String dateS = sdf.format(d);
						checked = dateS;

					}
				} else {
					if (column.getDataTypeName().compareTo("Geometry") == 0) {
						checked = rValue;
					} else {
						if (column.getDataTypeName().compareTo("Integer") == 0) {
							Integer in = new Integer(rValue);
							checked = in.toString();
						} else {
							if (column.getDataTypeName().compareTo("Numeric") == 0) {
								Double fl = new Double(rValue);
								checked = fl.toString();
							} else {
								if (column.getDataTypeName().compareTo("Text") == 0) {
									checked = rValue;
								} else {

								}
							}
						}
					}
				}

			}

		} catch (Throwable e) {
			Log.debug("Error no valid type data: " + e.getLocalizedMessage());
		}

		return checked;

	}

	protected void callReplaceValue(String rValue) {
		Log.debug("callRepalceValue is Dimension: " + isDimension);
		if (isDimension) {
			replaceColumnSession = new ReplaceColumnSession(
					value.getCurrentValue(), rValue, trId, column,
					cellData.getRowId(), true);
			Log.debug(replaceColumnSession.toString());
		} else {

			replaceColumnSession = new ReplaceColumnSession(
					value.getCurrentValue(), rValue, trId, column,
					cellData.getRowId());
			Log.debug(replaceColumnSession.toString());
		}
		TDGWTServiceAsync.INSTANCE.startReplaceColumn(replaceColumnSession,
				new AsyncCallback<String>() {
					@Override
					public void onSuccess(String taskId) {
						Log.debug("Submitted replace column value");
						openMonitorDialog(taskId);

					}

					@Override
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
									Log.error("Error submitting replace column value: "
											+ caught.getMessage()
											+ " "
											+ caught.getCause());
									caught.printStackTrace();
									UtilsGXT3
											.alert(msgsCommon.error(),
													caught.getMessage());
								}
							}
						}
					}
				});

	}

	protected void addHandlersForComboDimensionType(
			final LabelProvider<DimensionRow> labelProvider) {

		comboDimensionType.addTriggerClickHandler(new TriggerClickHandler() {

			@Override
			public void onTriggerClick(TriggerClickEvent event) {
				Log.debug("ComboDimensionRows TriggerClickEvent");
				callDialogDimensionRowSelection();
				comboDimensionType.collapse();

			}

		});

	}

	protected void callDialogDimensionRowSelection() {
		DimensionRowSelectionDialog dialogDimensionRowSelection = new DimensionRowSelectionDialog(
				column, cellData, eventBus);
		dialogDimensionRowSelection.addListener(this);
		dialogDimensionRowSelection.show();
	}

	@Override
	public void selectedDimensionRow(DimensionRow dimRow) {
		Log.debug("Selected dimension row: " + dimRow);
		dimensionRow = dimRow;
		comboDimensionType.setValue(dimRow);
	}

	@Override
	public void abortedDimensionRowSelection() {
		Log.debug("Aborted");
		comboDimensionType.setValue(null);
		dimensionRow = null;

	}

	@Override
	public void failedDimensionRowSelection(String reason, String detail) {
		Log.debug("Failed: " + reason + " " + detail);
		comboDimensionType.setValue(null);
		dimensionRow = null;

	}

	public void close() {
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
				ChangeTableRequestType.COLUMNREPLACE, operationResult.getTrId(), why);
		eventBus.fireEvent(changeTableRequestEvent);
		close();
	}

	@Override
	public void operationFailed(Throwable caught, String reason, String details) {
		UtilsGXT3.alert(reason, details);
		close();

	}

	@Override
	public void operationStopped(OperationResult operationResult, String reason, String details) {
		ChangeTableWhy why = ChangeTableWhy.TABLECURATION;
		ChangeTableRequestEvent changeTableRequestEvent = new ChangeTableRequestEvent(
				ChangeTableRequestType.COLUMNREPLACE, operationResult.getTrId(), why);
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
