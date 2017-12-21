package org.gcube.portlets.user.td.tablewidget.client.history;

import org.gcube.portlets.user.td.gwtservice.client.rpc.TDGWTServiceAsync;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTIsFinalException;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTIsLockedException;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTSessionExpiredException;
import org.gcube.portlets.user.td.monitorwidget.client.MonitorDialog;
import org.gcube.portlets.user.td.monitorwidget.client.MonitorDialogListener;
import org.gcube.portlets.user.td.tablewidget.client.TableWidgetMessages;
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

/**
 * 
 * @author giancarlo
 * email: <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public class HistoryDiscard implements MonitorDialogListener {
	private TRId trId;
	private EventBus eventBus;
	
	private CommonMessages msgsCommon;
	private TableWidgetMessages msgs;
	
	
	public HistoryDiscard(EventBus eventBus) {
		this.eventBus = eventBus;
		initMessages();
	}

	public void discard() {
		retrieveCurrentTR();
	}
	
	protected void initMessages() {
		msgsCommon = GWT.create(CommonMessages.class);
		msgs = GWT.create(TableWidgetMessages.class);

	}

	protected void retrieveCurrentTR() {
		TDGWTServiceAsync.INSTANCE.getCurrentTRId(new AsyncCallback<TRId>() {

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
						Log.error("Error retrieving trId: "
								+ caught.getLocalizedMessage());
						UtilsGXT3.alert(msgsCommon.error(),
								msgs.errorRetrievingCurrentTabularResourceId());
					}
				}
			}

			public void onSuccess(TRId result) {
				Log.debug("retrieved " + result);
				trId = result;
				callDiscard();
			}

		});
	}

	protected void callDiscard() {
		TDGWTServiceAsync.INSTANCE.startDiscard(trId,
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
									Log.error("Error in discard: "
											+ caught.getLocalizedMessage());
									UtilsGXT3.alert(msgsCommon.error(),
											caught.getLocalizedMessage());
								}
							}
						}
					}

					public void onSuccess(String taskId) {
						Log.debug("Discard Session taskId: " + taskId);
						if (taskId == null) {
							Log.info("Attention: undo not applicable");
							UtilsGXT3.info(msgsCommon.error(), msgs.attentionUndoNotApplicable());
						} else {
							openMonitorDialog(taskId);
						}
					}

				});
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
				ChangeTableRequestType.ROLLBACK, operationResult.getTrId(), why);
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
				ChangeTableRequestType.ROLLBACK, operationResult.getTrId(), why);
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
