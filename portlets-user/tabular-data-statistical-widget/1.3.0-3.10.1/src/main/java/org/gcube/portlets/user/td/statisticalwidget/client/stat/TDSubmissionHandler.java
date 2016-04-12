package org.gcube.portlets.user.td.statisticalwidget.client.stat;

import org.gcube.portlets.user.td.gwtservice.client.rpc.TDGWTServiceAsync;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTIsLockedException;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTSessionExpiredException;
import org.gcube.portlets.user.td.gwtservice.shared.statistical.StatisticalOperationSession;
import org.gcube.portlets.user.td.monitorwidget.client.MonitorDialog;
import org.gcube.portlets.user.td.monitorwidget.client.MonitorDialogListener;
import org.gcube.portlets.user.td.monitorwidget.client.utils.UtilsGXT3;
import org.gcube.portlets.user.td.statisticalwidget.client.StatisticalWidget;
import org.gcube.portlets.user.td.widgetcommonevent.client.event.ChangeTableRequestEvent;
import org.gcube.portlets.user.td.widgetcommonevent.client.event.SessionExpiredEvent;
import org.gcube.portlets.user.td.widgetcommonevent.client.type.ChangeTableRequestType;
import org.gcube.portlets.user.td.widgetcommonevent.client.type.ChangeTableWhy;
import org.gcube.portlets.user.td.widgetcommonevent.client.type.SessionExpiredType;
import org.gcube.portlets.user.td.widgetcommonevent.shared.OperationResult;
import org.gcube.portlets.user.td.widgetcommonevent.shared.TRId;
import org.gcube.portlets.widgets.StatisticalManagerAlgorithmsWidget.client.SubmissionHandler;
import org.gcube.portlets.widgets.StatisticalManagerAlgorithmsWidget.client.SubmissionParameters;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.event.shared.EventBus;

/**
 * 
 * @author giancarlo email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class TDSubmissionHandler implements SubmissionHandler,
		MonitorDialogListener {

	protected EventBus eventBus;
	protected TRId trId;
	protected StatisticalWidget statisticalWidget;
	
	public TDSubmissionHandler(StatisticalWidget statisticalWidget, TRId trId, EventBus eventBus) {
		this.statisticalWidget=statisticalWidget;
		this.trId = trId;
		this.eventBus = eventBus;
	}

	@Override
	public void onSubmit(SubmissionParameters params) {
		Log.debug("SUBMITTED :" + params);
		if (params == null) {
			Log.error("Invalid params null");
			UtilsGXT3.alert("Error", "Invalid params null");
			return;
		}
		statisticalWidget.closeStatisticalWidget();
		
		StatisticalOperationSession statisticalOperationSession = new StatisticalOperationSession(
				trId, params.getParametersMap(), params.getDescription(),
				params.getTitle(), params.getOp().getId(), params.getOp()
						.getName(), params.getOp().getBriefDescription());
		
		callStatisticalOperation(statisticalOperationSession);
		
	}

	protected void callStatisticalOperation(
			StatisticalOperationSession statisticalOperationSession) {

		TDGWTServiceAsync.INSTANCE.startStatisticalOperation(
				statisticalOperationSession, new AsyncCallback<String>() {

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
								Log.error("Error in statistical operation: "
										+ caught.getLocalizedMessage());
								UtilsGXT3.alert(
										"Error in statistical operation",
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
				ChangeTableRequestType.STATISTICALOPERATION, operationResult.getTrId(), why);
		eventBus.fireEvent(changeTableRequestEvent);

	}

	@Override
	public void operationFailed(Throwable caught, String reason, String details) {
		UtilsGXT3.alert(reason, details);

	}

	@Override
	public void operationStopped(OperationResult operationResult, String reason, String details) {
		ChangeTableWhy why = ChangeTableWhy.TABLECURATION;
		ChangeTableRequestEvent changeTableRequestEvent = new ChangeTableRequestEvent(
				ChangeTableRequestType.STATISTICALOPERATION, operationResult.getTrId(), why);
		eventBus.fireEvent(changeTableRequestEvent);

	}

	@Override
	public void operationAborted() {

	}

	@Override
	public void operationPutInBackground() {

	}

}
