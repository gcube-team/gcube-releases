package org.gcube.portlets.user.td.tablewidget.client;

import org.gcube.portlets.user.td.gwtservice.client.rpc.TDGWTServiceAsync;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTIsFinalException;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTIsLockedException;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTSessionExpiredException;
import org.gcube.portlets.user.td.gwtservice.shared.tr.TableData;
import org.gcube.portlets.user.td.gwtservice.shared.tr.table.ChangeTableTypeSession;
import org.gcube.portlets.user.td.monitorwidget.client.MonitorDialog;
import org.gcube.portlets.user.td.monitorwidget.client.MonitorDialogListener;
import org.gcube.portlets.user.td.tablewidget.client.resources.ResourceBundle;
import org.gcube.portlets.user.td.tablewidget.client.type.TableTypeElement;
import org.gcube.portlets.user.td.tablewidget.client.type.TableTypeProperties;
import org.gcube.portlets.user.td.tablewidget.client.type.TableTypeStore;
import org.gcube.portlets.user.td.tablewidget.client.util.UtilsGXT3;
import org.gcube.portlets.user.td.widgetcommonevent.client.CommonMessages;
import org.gcube.portlets.user.td.widgetcommonevent.client.event.ChangeTableRequestEvent;
import org.gcube.portlets.user.td.widgetcommonevent.client.event.SessionExpiredEvent;
import org.gcube.portlets.user.td.widgetcommonevent.client.type.ChangeTableRequestType;
import org.gcube.portlets.user.td.widgetcommonevent.client.type.ChangeTableWhy;
import org.gcube.portlets.user.td.widgetcommonevent.client.type.SessionExpiredType;
import org.gcube.portlets.user.td.widgetcommonevent.shared.OperationResult;
import org.gcube.portlets.user.td.widgetcommonevent.shared.TRId;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.event.shared.EventBus;
import com.sencha.gxt.cell.core.client.ButtonCell.IconAlign;
import com.sencha.gxt.cell.core.client.form.ComboBoxCell.TriggerAction;
import com.sencha.gxt.core.client.dom.ScrollSupport.ScrollMode;
import com.sencha.gxt.core.client.util.Margins;
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
public class ChangeTableTypePanel extends FramedPanel implements
		MonitorDialogListener {
	private static final String WIDTH = "640px";
	private static final String HEIGHT = "520px";

	private TRId trId;
	private TableData table;
	private VerticalLayoutContainer vl;
	private EventBus eventBus;
	private ChangeTableTypeSession changeTableTypeSession;

	private ComboBox<TableTypeElement> comboTableType;
	private TextButton btnApply;
	private CommonMessages msgsCommon;
	private TableWidgetMessages msgs;

	public ChangeTableTypePanel(TRId trId, EventBus eventBus) {
		super();
		this.trId = trId;
		this.eventBus = eventBus;
		forceLayoutOnResize = true;
		initMessages();
		create();
	}

	protected void initMessages() {
		msgsCommon = GWT.create(CommonMessages.class);
		msgs = GWT.create(TableWidgetMessages.class);

	}

	protected void create() {
		setWidth(WIDTH);
		setHeight(HEIGHT);
		setHeaderVisible(false);
		setBodyBorder(false);

		TableTypeProperties props = GWT.create(TableTypeProperties.class);
		ListStore<TableTypeElement> storeTableType = new ListStore<TableTypeElement>(
				props.id());

		storeTableType.addAll(TableTypeStore.getTableTypes());

		Log.trace("Store created");

		comboTableType = new ComboBox<TableTypeElement>(storeTableType,
				props.label());

		Log.trace("ComboTableType created");

		comboTableType.setEmptyText(msgs.comboTableTypeEmptyText());
		comboTableType.setItemId("ComboTableType");
		comboTableType.setWidth("200px");
		comboTableType.setEditable(false);
		comboTableType.setTriggerAction(TriggerAction.ALL);

		FieldLabel comboTableTypeLabel = new FieldLabel(comboTableType,
				msgs.comboTableTypeLabel());

		btnApply = new TextButton(msgs.btnApplyText());
		btnApply.setIcon(ResourceBundle.INSTANCE.tableType());
		btnApply.setIconAlign(IconAlign.RIGHT);
		btnApply.setToolTip(msgs.btnApplyToolTip());

		btnApply.addSelectHandler(new SelectHandler() {

			public void onSelect(SelectEvent event) {
				onChangeTableType();

			}
		});

		vl = new VerticalLayoutContainer();
		vl.setScrollMode(ScrollMode.AUTO);
		vl.setAdjustForScroll(true);

		vl.add(comboTableTypeLabel, new VerticalLayoutData(1, -1));
		vl.add(btnApply, new VerticalLayoutData(-1, -1, new Margins(10, 0, 10,
				0)));

		add(vl);
		show();
		load();

	}

	protected void onChangeTableType() {
		TableTypeElement tableTypeElement = comboTableType.getCurrentValue();
		if (tableTypeElement != null) {
			if (tableTypeElement.getTableType() != null) {
				changeTableTypeSession = new ChangeTableTypeSession(trId,
						tableTypeElement.getTableType());
				callChangeTableType();
			} else {
				UtilsGXT3.alert(msgsCommon.error(),
						msgs.errorInvalidTableType());
			}
		} else {
			UtilsGXT3.alert(msgsCommon.attention(),
					msgs.attentionSelectATableType());
		}
	}

	private void callChangeTableType() {
		TDGWTServiceAsync.INSTANCE.startChangeTableType(changeTableTypeSession,
				new AsyncCallback<String>() {

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
									Log.debug("Change The Table Type Error: "
											+ caught.getLocalizedMessage());
									UtilsGXT3.alert(
											msgsCommon.error(),
											msgs.errorInChangeTableTypeOperationFixed()
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

	protected void setCurrentType() {
		TableTypeElement tElement = TableTypeStore.getTableTypeElement(table
				.getTypeName());
		comboTableType.setValue(tElement);

	}

	private void load() {
		TDGWTServiceAsync.INSTANCE.getTable(trId,
				new AsyncCallback<TableData>() {

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
								Log.error("Error retrieving table: "
										+ caught.getLocalizedMessage());
								UtilsGXT3.alert(msgsCommon.error(),
										caught.getLocalizedMessage());
							}
						}
					}

					public void onSuccess(TableData result) {
						Log.debug("Retrieved table: " + result.toString());
						table = result;
						setCurrentType();

					}

				});

	}

	public void update(TRId trId) {
		this.trId = trId;
		load();
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
				ChangeTableRequestType.CHANGETABLETYPE,
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
				ChangeTableRequestType.CHANGETABLETYPE,
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
