package org.gcube.portlets.user.td.rulewidget.client;


import org.gcube.portlets.user.td.expressionwidget.client.rpc.ExpressionServiceAsync;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTIsFinalException;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTIsLockedException;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTSessionExpiredException;
import org.gcube.portlets.user.td.gwtservice.shared.rule.ApplyAndDetachColumnRulesSession;
import org.gcube.portlets.user.td.monitorwidget.client.MonitorDialog;
import org.gcube.portlets.user.td.monitorwidget.client.MonitorDialogListener;
import org.gcube.portlets.user.td.rulewidget.client.resources.ResourceBundle;
import org.gcube.portlets.user.td.widgetcommonevent.client.CommonMessages;
import org.gcube.portlets.user.td.widgetcommonevent.client.event.ChangeTableRequestEvent;
import org.gcube.portlets.user.td.widgetcommonevent.client.event.SessionExpiredEvent;
import org.gcube.portlets.user.td.widgetcommonevent.client.type.ChangeTableRequestType;
import org.gcube.portlets.user.td.widgetcommonevent.client.type.ChangeTableWhy;
import org.gcube.portlets.user.td.widgetcommonevent.client.type.SessionExpiredType;
import org.gcube.portlets.user.td.widgetcommonevent.shared.OperationResult;
import org.gcube.portlets.user.td.widgetcommonevent.shared.TRId;
import org.gcube.portlets.user.td.wizardwidget.client.util.UtilsGXT3;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.event.shared.EventBus;
import com.sencha.gxt.widget.core.client.Window;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;

/**
 * 
 * @author "Giancarlo Panichi" <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class RuleOnColumnApplyDialog extends Window implements
		MonitorDialogListener {
	private static final String WIDTH = "780px";
	private static final String HEIGHT = "530px";
	private EventBus eventBus;
	private RuleOnColumnApplyMessages msgs;
	private CommonMessages msgsCommon;

	public RuleOnColumnApplyDialog(TRId trId, EventBus eventBus) {
		this.eventBus = eventBus;
		initMessages();
		initWindow();

		RuleOnColumnApplyPanel ruleApplyPanel = new RuleOnColumnApplyPanel(this,
				trId, eventBus);
		add(ruleApplyPanel);
	}
	
	protected void initMessages(){
		msgs= GWT.create(RuleOnColumnApplyMessages.class);
		msgsCommon = GWT.create(CommonMessages.class);
	}

	protected void initWindow() {
		setWidth(WIDTH);
		setHeight(HEIGHT);
		setBodyBorder(false);
		setResizable(false);
		setHeadingText(msgs.dialogRuleOnColumnHead());
		setClosable(true);
		setModal(true);
		forceLayoutOnResize = true;
		getHeader().setIcon(ResourceBundle.INSTANCE.ruleColumnApply());

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void initTools() {
		super.initTools();

		closeBtn.addSelectHandler(new SelectHandler() {

			public void onSelect(SelectEvent event) {
				close();
			}
		});

	}

	protected void close() {
		hide();
	}

	protected void applyRules(ApplyAndDetachColumnRulesSession applyColumnRulesSession) {
		ExpressionServiceAsync.INSTANCE.startApplyAndDetachColumnRules(applyColumnRulesSession, new AsyncCallback<String>() {

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
							Log.debug("Apply Rules On Column Error: "
									+ caught.getLocalizedMessage());
							UtilsGXT3
									.alert(msgs.applyRulesOnColumnHead(),
											caught.getLocalizedMessage());
						}
					}
				}
				
			}

			@Override
			public void onSuccess(String taskId) {
				if(taskId!=null){
					openMonitorDialog(taskId);
				} else {
					close();
				}
				
			}
		
		
		
		});

	}

	protected void openMonitorDialog(String taskId) {
		MonitorDialog monitorDialog = new MonitorDialog(taskId, eventBus);
		monitorDialog.addProgressDialogListener(this);
		monitorDialog.show();
	}

	@Override
	public void operationComplete(OperationResult operationResult) {
		ChangeTableWhy why = ChangeTableWhy.TABLEUPDATED;
		ChangeTableRequestEvent changeTableRequestEvent = new ChangeTableRequestEvent(
				ChangeTableRequestType.RULESONCOLUMNAPPLY, operationResult.getTrId(), why);
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
				ChangeTableRequestType.RULESONCOLUMNAPPLY, operationResult.getTrId(), why);
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
