package org.gcube.portlets.user.td.statisticalwidget.client.stat;

import org.gcube.portlets.user.td.gwtservice.client.rpc.TDGWTServiceAsync;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTIsLockedException;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTSessionExpiredException;
import org.gcube.portlets.user.td.gwtservice.shared.statistical.DataMinerOperationSession;
import org.gcube.portlets.user.td.monitorwidget.client.MonitorDialog;
import org.gcube.portlets.user.td.monitorwidget.client.MonitorDialogListener;
import org.gcube.portlets.user.td.monitorwidget.client.utils.UtilsGXT3;
import org.gcube.portlets.user.td.statisticalwidget.client.DataMinerWidget;
import org.gcube.portlets.user.td.widgetcommonevent.client.event.ChangeTableRequestEvent;
import org.gcube.portlets.user.td.widgetcommonevent.client.event.SessionExpiredEvent;
import org.gcube.portlets.user.td.widgetcommonevent.client.type.ChangeTableRequestType;
import org.gcube.portlets.user.td.widgetcommonevent.client.type.ChangeTableWhy;
import org.gcube.portlets.user.td.widgetcommonevent.client.type.SessionExpiredType;
import org.gcube.portlets.user.td.widgetcommonevent.shared.OperationResult;
import org.gcube.portlets.user.td.widgetcommonevent.shared.TRId;
import org.gcube.portlets.widgets.dataminermanagerwidget.client.events.ExternalExecutionEvent;
import org.gcube.portlets.widgets.dataminermanagerwidget.client.events.ExternalExecutionEvent.ExternalExecutionEventHandler;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.event.shared.EventBus;

/**
 * 
 * @author giancarlo email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class TDSubmissionHandler implements ExternalExecutionEventHandler,
		MonitorDialogListener {

	private EventBus eventBus;
	private TRId trId;
	private DataMinerWidget dataMinerWidget;
	private boolean submitted;

	public TDSubmissionHandler(DataMinerWidget dataMinerWidget, TRId trId,
			EventBus eventBus) {
		this.dataMinerWidget = dataMinerWidget;
		this.trId = trId;
		this.eventBus = eventBus;
		submitted = false;
	}

	@Override
	public void onSubmit(ExternalExecutionEvent event) {
		Log.debug("SUBMITTED :" + event);
		if (event == null || event.getOp() == null) {
			Log.error("Invalid params null");
			UtilsGXT3.alert("Error", "Invalid params null");
			return;
		}

		if (!submitted) {
			Log.info("ExternalExecutionEvent submitted");
			submitted=true;
			dataMinerWidget.closeDataMinerWidget();

			DataMinerOperationSession statisticalOperationSession = new DataMinerOperationSession(
					trId, event.getOp());

			callDataMinerOperation(statisticalOperationSession);
		}
	}

	protected void callDataMinerOperation(
			DataMinerOperationSession dataMinerOperationSession) {

		TDGWTServiceAsync.INSTANCE.startDataMinerOperation(
				dataMinerOperationSession, new AsyncCallback<String>() {

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
								Log.error("Error in DataMiner operation: "
										+ caught.getLocalizedMessage());
								UtilsGXT3.alert(
										"Error in DataMiner operation",
										"Error: "
												+ caught.getLocalizedMessage());
							}

						}
					}

					public void onSuccess(String taskId) {
						Log.debug("Statistical Operation started");
						openMonitorDialog(taskId);

					}

				});
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
				ChangeTableRequestType.STATISTICALOPERATION,
				operationResult.getTrId(), why);
		eventBus.fireEvent(changeTableRequestEvent);

	}

	@Override
	public void operationFailed(Throwable caught, String reason, String details) {
		UtilsGXT3.alert(reason, details);

	}

	@Override
	public void operationStopped(OperationResult operationResult,
			String reason, String details) {
		ChangeTableWhy why = ChangeTableWhy.TABLECURATION;
		ChangeTableRequestEvent changeTableRequestEvent = new ChangeTableRequestEvent(
				ChangeTableRequestType.STATISTICALOPERATION,
				operationResult.getTrId(), why);
		eventBus.fireEvent(changeTableRequestEvent);

	}

	@Override
	public void operationAborted() {

	}

	@Override
	public void operationPutInBackground() {

	}

}
