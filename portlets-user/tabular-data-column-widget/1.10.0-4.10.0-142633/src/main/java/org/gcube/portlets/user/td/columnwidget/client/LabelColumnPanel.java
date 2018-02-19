package org.gcube.portlets.user.td.columnwidget.client;

import java.util.ArrayList;
import java.util.HashMap;

import org.gcube.portlets.user.td.columnwidget.client.resources.ResourceBundle;
import org.gcube.portlets.user.td.columnwidget.client.utils.UtilsGXT3;
import org.gcube.portlets.user.td.gwtservice.client.rpc.TDGWTServiceAsync;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTIsFinalException;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTIsLockedException;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTSessionExpiredException;
import org.gcube.portlets.user.td.gwtservice.shared.tr.column.LabelColumnSession;
import org.gcube.portlets.user.td.monitorwidget.client.MonitorDialog;
import org.gcube.portlets.user.td.monitorwidget.client.MonitorDialogListener;
import org.gcube.portlets.user.td.widgetcommonevent.client.CommonMessages;
import org.gcube.portlets.user.td.widgetcommonevent.client.event.ChangeTableRequestEvent;
import org.gcube.portlets.user.td.widgetcommonevent.client.event.SessionExpiredEvent;
import org.gcube.portlets.user.td.widgetcommonevent.client.type.ChangeTableRequestType;
import org.gcube.portlets.user.td.widgetcommonevent.client.type.ChangeTableWhy;
import org.gcube.portlets.user.td.widgetcommonevent.client.type.SessionExpiredType;
import org.gcube.portlets.user.td.widgetcommonevent.shared.OperationResult;
import org.gcube.portlets.user.td.widgetcommonevent.shared.TRId;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnData;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.event.shared.EventBus;
import com.sencha.gxt.cell.core.client.ButtonCell.IconAlign;
import com.sencha.gxt.core.client.dom.ScrollSupport.ScrollMode;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.widget.core.client.FramedPanel;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.form.TextField;

/**
 * 
 * LabelColumnPanel is the panel for change columns labels
 * 
 * 
 * @author "Giancarlo Panichi" <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 * 
 */
public class LabelColumnPanel extends FramedPanel implements
		MonitorDialogListener {
	private static final String WIDTH = "640px";
	private static final String HEIGHT = "520px";
	private static final int LABELWIDTH = 120;

	private EventBus eventBus;
	private ChangeColumnTypeDialog parent;
	private TRId trId;
	
	private ArrayList<ColumnData> columns;
	private VerticalLayoutContainer columnsLayoutContainer;

	private TextButton changeBtn;

	private LabelColumnSession labelColumnSession;
	private boolean updateStatus;
	private VerticalLayoutContainer v;
	private LabelColumnMessages msgs;
	private CommonMessages msgsCommon;

	public LabelColumnPanel(TRId trId, String columnName, EventBus eventBus) {
		this.trId = trId;
		this.eventBus = eventBus;
		updateStatus = false;
		Log.debug("LabelColumnPanel(): [" + trId + " columnName: " + columnName
				+ "]");
		initMessages();
		init();
		retrieveColumns();

	}
	
	protected void initMessages(){
		msgs = GWT.create(LabelColumnMessages.class);
		msgsCommon = GWT.create(CommonMessages.class);
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
											msgs.errorRetrievingColumnsOfTabularResource());
								}
							}
						}

					}

					public void onSuccess(ArrayList<ColumnData> result) {
						Log.trace("loaded " + result.size() + " ColumnData");
						ArrayList<ColumnData> removables = new ArrayList<ColumnData>();
						for (ColumnData c : result) {
							if (c.getTypeCode().compareTo("DIMENSION") == 0
									|| c.getTypeCode().compareTo(
											"TIMEDIMENSION") == 0) {
								removables.add(c);
							}
						}
						if (removables.size() > 0) {
							result.removeAll(removables);
						}
						columns = result;
						startCreate();

					}

				});
	}

	protected void startCreate() {
		if (updateStatus) {
			updatePanel();
		} else {
			create();
		}
	}

	protected void create() {
		Log.debug("Create LabelColunmPanel");
		SimpleContainer columnsContainer = new SimpleContainer();
		// columnsContainer.setHeight(getOffsetHeight(true)-50);

		columnsLayoutContainer = new VerticalLayoutContainer();
		columnsLayoutContainer.setScrollMode(ScrollMode.AUTOY);
		columnsLayoutContainer.setAdjustForScroll(true);
		columnsContainer.add(columnsLayoutContainer);

		addFields();
		
		columnsContainer.forceLayout();

		changeBtn = new TextButton(msgs.changeBtnText());
		changeBtn.setIcon(ResourceBundle.INSTANCE.columnLabel());
		changeBtn.setIconAlign(IconAlign.RIGHT);
		changeBtn.setToolTip(msgs.changeBtnToolTip());

		changeBtn.addSelectHandler(new SelectHandler() {

			public void onSelect(SelectEvent event) {
				changeLabelColumns();

			}
		});

		v=new VerticalLayoutContainer();
		v.setScrollMode(ScrollMode.AUTOY);
		v.setAdjustForScroll(true);
		v.add(columnsContainer, new VerticalLayoutData(1, -1, new Margins(0)));
		v.add(changeBtn, new VerticalLayoutData(-1, -1, new Margins(10, 0, 10, 0)));
		add(v, new VerticalLayoutData(1, -1, new Margins(0)));

		forceLayout();

	}

	protected void addFields() {
		VerticalLayoutData layoutData = new VerticalLayoutData(1, -1,
				new Margins(0));

		ArrayList<FieldLabel> fields = generateFields();
		for (FieldLabel fl : fields) {
			columnsLayoutContainer.add(fl, layoutData);

		}
		columnsLayoutContainer.onResize();
	
	}

	protected ArrayList<FieldLabel> generateFields() {
		ArrayList<FieldLabel> fields = new ArrayList<FieldLabel>();

		for (ColumnData col : columns) {
			if (col != null) {
				FieldLabel textLabel;
				if (col.getLabel() != null && !col.getLabel().isEmpty()) {
					TextField text = new TextField();
					text.setValue(col.getLabel());
					String lab=col.getLabel();
					if(lab.length()>19){
						lab=lab.substring(0, 17);
						lab=lab+"...";
					}
					textLabel = new FieldLabel(text, lab);
					textLabel.setLabelWidth(LABELWIDTH);
					textLabel.setId(col.getColumnId());
				} else {
					TextField text = new TextField();
					text.setValue(msgs.nolabelText());
					textLabel = new FieldLabel(text, msgs.nolabelTextLabel());
					textLabel.setLabelWidth(LABELWIDTH);
					textLabel.setId(col.getColumnId());
				}
				fields.add(textLabel);
			}

		}
		Log.debug("Fields: " + fields.size());
		return fields;
	}

	protected void init() {
		setWidth(WIDTH);
		setHeight(HEIGHT);
		setHeaderVisible(false);
		setBodyBorder(false);
	}

	public void update(TRId trId, String columnName) {
		this.trId = trId;
		updateStatus = true;
		retrieveColumns();
	}

	protected void updatePanel() {
		columnsLayoutContainer.clear();
		addFields();
		forceLayout();
	}

	protected void changeLabelColumns() {
		int i = 0;
		int lenght = columnsLayoutContainer.getWidgetCount();
		HashMap<ColumnData, String> maps = new HashMap<ColumnData, String>();

		for (; i < lenght; i++) {
			FieldLabel fieldLabel = (FieldLabel) columnsLayoutContainer
					.getWidget(i);
			String columnId = fieldLabel.getId();
			ColumnData colCurrent = null;
			for (ColumnData col : columns) {
				if (col.getColumnId().compareTo(columnId) == 0) {
					colCurrent = col;
					break;
				}
			}
			if (colCurrent == null) {
				continue;
			}

			TextField text = (TextField) fieldLabel.getWidget();
			String val = text.getCurrentValue();
			if (val == null || val.isEmpty()) {
				continue;
			}
			maps.put(colCurrent, val);

		}

		if (maps.size() > 0) {
			labelColumnSession = new LabelColumnSession(trId, maps);
			callLabelColumn();
		} else {
			UtilsGXT3.alert(msgsCommon.attention(), msgs.insertValidLabels());
		}

	}

	private void callLabelColumn() {
		TDGWTServiceAsync.INSTANCE.startLabelColumn(labelColumnSession,
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
									Log.debug("Change The Column Label Error: "
											+ caught.getLocalizedMessage());
									UtilsGXT3
											.alert(msgs.errorChangingTheColumnLabelHead(),
													msgs.errorChangingTheColumnLabel());
								}
							}
						}
					}

					public void onSuccess(String taskId) {
						UtilsGXT3
						.info(msgsCommon.success(), msgs.updatedLabels());
						syncOpComplete();
						
						//openMonitorDialog(taskId);
					}

				});

	}
	
	protected void syncOpComplete(){
		ChangeTableWhy why = ChangeTableWhy.TABLEUPDATED;
		ChangeTableRequestEvent changeTableRequestEvent = new ChangeTableRequestEvent(
				ChangeTableRequestType.CHANGECOLUMNLABEL, trId, why);
		eventBus.fireEvent(changeTableRequestEvent);
		close();
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
				ChangeTableRequestType.CHANGECOLUMNLABEL, operationResult.getTrId(), why);
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
				ChangeTableRequestType.CHANGECOLUMNLABEL, operationResult.getTrId(), why);
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
