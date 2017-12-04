/**
 * 
 */
package org.gcube.portlets.user.td.codelistmappingimportwidget.client;

import org.gcube.portlets.user.td.gwtservice.client.rpc.TDGWTServiceAsync;
import org.gcube.portlets.user.td.gwtservice.shared.codelisthelper.CodelistMappingSession;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTIsFinalException;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTIsLockedException;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTSessionExpiredException;
import org.gcube.portlets.user.td.monitorwidget.client.MonitorDialog;
import org.gcube.portlets.user.td.monitorwidget.client.MonitorDialogListener;
import org.gcube.portlets.user.td.widgetcommonevent.client.CommonMessages;
import org.gcube.portlets.user.td.widgetcommonevent.client.event.SessionExpiredEvent;
import org.gcube.portlets.user.td.widgetcommonevent.client.type.SessionExpiredType;
import org.gcube.portlets.user.td.widgetcommonevent.shared.OperationResult;
import org.gcube.portlets.user.td.widgetcommonevent.shared.TRId;
import org.gcube.portlets.user.td.wizardwidget.client.WizardCard;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlexTable;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.widget.core.client.FramedPanel;
import com.sencha.gxt.widget.core.client.container.BoxLayoutContainer.BoxLayoutData;
import com.sencha.gxt.widget.core.client.container.HtmlLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VBoxLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VBoxLayoutContainer.VBoxLayoutAlign;

/**
 * 
 * @author "Giancarlo Panichi" <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 * 
 */
public class CodelistMappingOperationInProgressCard extends WizardCard
		implements MonitorDialogListener {
	private static CommonMessages msgsCommon= GWT.create(CommonMessages.class);
	private CodelistMappingMessages msgs;
	private CodelistMappingSession codelistMappingSession;
	private TRId newTrId;
	private HtmlLayoutContainer resultField;
	

	public CodelistMappingOperationInProgressCard(
			final CodelistMappingSession codelistMappingSession) {
		super(msgsCommon.operationInProgress(), "");
		this.codelistMappingSession = codelistMappingSession;
		initMessages();
		VBoxLayoutContainer operationInProgressPanel = new VBoxLayoutContainer();
		operationInProgressPanel.setVBoxLayoutAlign(VBoxLayoutAlign.CENTER);

		final FlexTable description = new FlexTable();
		description.setCellSpacing(10);
		description.setCellPadding(4);
		description.setBorderWidth(0);

		description.setHTML(0, 0,
				"<span style=\"font-weight:bold;\";>"+msgs.documentFixed()+"</span>");
		description.setText(0, 1, "Codelist Mapping");
		description.setHTML(1, 0,
				"<span style=\"font-weight:bold;\";>"+msgs.sourceFixed()+"</span>");
		description.setText(1, 1, codelistMappingSession.getSource().getName());

		description.setHTML(2, 0,
				"<span style=\"font-weight:bold;\";>"+msgs.nameFixed()+"</span>");
		description.setText(2, 1, codelistMappingSession
				.getResourceTDDescriptor().getName());

		FramedPanel summary = new FramedPanel();
		summary.setHeadingText(msgs.summaryImport());
		summary.setWidth(400);
		summary.add(description);
		operationInProgressPanel.add(summary, new BoxLayoutData(new Margins(20,
				5, 10, 5)));

		resultField = new HtmlLayoutContainer("<div></div>");

		operationInProgressPanel.add(resultField, new BoxLayoutData(
				new Margins(10, 5, 10, 5)));

		setContent(operationInProgressPanel);
		resultField.setVisible(false);

	}
	
	protected void initMessages(){
		msgs = GWT.create(CodelistMappingMessages.class);
	}
	
	
	public void importCodelistMapping() {
		TDGWTServiceAsync.INSTANCE.startCodelistMappingImport(
				codelistMappingSession, new AsyncCallback<String>() {

					public void onSuccess(String taskId) {
						openMonitorDialog(taskId);
					}

					public void onFailure(Throwable caught) {
						if (caught instanceof TDGWTSessionExpiredException) {
							getEventBus()
									.fireEvent(
											new SessionExpiredEvent(
													SessionExpiredType.EXPIREDONSERVER));
						} else {
							if (caught instanceof TDGWTIsLockedException) {
								Log.error(caught.getLocalizedMessage());
								showErrorAndHide(msgsCommon.errorLocked(),
										caught.getLocalizedMessage(), "",
										caught);
							} else {
								if (caught instanceof TDGWTIsFinalException) {
									Log.error(caught.getLocalizedMessage());
									showErrorAndHide(msgsCommon.errorFinal(),
											caught.getLocalizedMessage(), "",
											caught);
								} else {
									showErrorAndHide(
											msgsCommon.error(),
											msgs.errorAnErrorOccurredInImportCodelistMappingFixed(),
											caught.getLocalizedMessage(),
											caught);
								}
							}
						}
					}
				});
	}

	@Override
	public void setup() {
		getWizardWindow().setEnableBackButton(false);
		setBackButtonVisible(false);
		getWizardWindow().setEnableNextButton(false);
		getWizardWindow().setNextButtonToFinish();
		importCodelistMapping();
	}

	//
	protected void openMonitorDialog(String taskId) {
		MonitorDialog monitorDialog = new MonitorDialog(taskId, getEventBus());
		monitorDialog.addProgressDialogListener(this);
		monitorDialog.show();
	}

	@Override
	public void operationComplete(OperationResult operationResult) {
		newTrId = operationResult.getTrId();
		SafeHtmlBuilder safeHtmlBuilder = new SafeHtmlBuilder();
		safeHtmlBuilder
				.appendHtmlConstant("<div style='text-align:center;font-size:large;font-weight:bold; color:#009900;'>"+msgsCommon.operationCompleted()+"</div>");
		resultField.setHTML(safeHtmlBuilder.toSafeHtml());
		resultField.setVisible(true);

		Command sayComplete = new Command() {
			public void execute() {
				try {
					getWizardWindow().close(false);
					Log.info("fire Complete: " + newTrId);

					getWizardWindow().fireCompleted(newTrId);

				} catch (Exception e) {
					Log.error("fire Complete :" + e.getLocalizedMessage());
				}
			}
		};

		getWizardWindow().setNextButtonCommand(sayComplete);

		setNextButtonVisible(true);
		getWizardWindow().setEnableNextButton(true);

		forceLayout();

	}

	@Override
	public void operationFailed(Throwable caught, String reason, String details) {
		SafeHtmlBuilder safeHtmlBuilder = new SafeHtmlBuilder();
		safeHtmlBuilder
				.appendHtmlConstant("<div style='text-align:center;font-size:large;font-weight:bold;color:red;'>"+msgsCommon.operationFailed()+"</div>");
		resultField.setHTML(safeHtmlBuilder.toSafeHtml());
		resultField.setVisible(true);
		if (caught instanceof TDGWTSessionExpiredException) {
			getEventBus()
					.fireEvent(
							new SessionExpiredEvent(
									SessionExpiredType.EXPIREDONSERVER));
		} else {
			if (caught instanceof TDGWTIsLockedException) {
				Log.error(caught.getLocalizedMessage());
				showErrorAndHide(msgsCommon.errorLocked(), caught.getLocalizedMessage(),
						"", caught);
			} else {
				if (caught instanceof TDGWTIsFinalException) {
					Log.error(caught.getLocalizedMessage());
					showErrorAndHide(msgsCommon.errorFinal(),
							caught.getLocalizedMessage(), "", caught);
				} else {
					showErrorAndHide(msgs.errorInCodelistMappingImportHead(),
							reason, details, caught);
				}
			}
		}

		forceLayout();
	}

	@Override
	public void operationStopped(OperationResult operationResult, String reason, String details) {
		newTrId = operationResult.getTrId();
		SafeHtmlBuilder safeHtmlBuilder = new SafeHtmlBuilder();
		safeHtmlBuilder
				.appendHtmlConstant("<div style='text-align:center;font-size:large;font-weight:bold;color: #FF9900;'>"+msgsCommon.operationProblem()+"</div>");
		resultField.setHTML(safeHtmlBuilder.toSafeHtml());
		resultField.setVisible(true);

		Command sayComplete = new Command() {
			public void execute() {
				try {
					getWizardWindow().close(false);
					Log.info("fire Complete: tabular resource "
							+ newTrId.getId());
					Log.info("fire Complete: tableId " + newTrId.getTableId());

					getWizardWindow().fireCompleted(newTrId);

				} catch (Exception e) {
					Log.error("fire Complete :" + e.getLocalizedMessage());
				}
			}
		};

		getWizardWindow().setNextButtonCommand(sayComplete);

		setNextButtonVisible(true);
		getWizardWindow().setEnableNextButton(true);

		forceLayout();
	}

	@Override
	public void operationAborted() {
		SafeHtmlBuilder safeHtmlBuilder = new SafeHtmlBuilder();
		safeHtmlBuilder
				.appendHtmlConstant("<div style='text-align:center;font-size:large;font-weight:bold;color: #AA00AA;'>"+msgsCommon.operationAborted()+"</div>");
		resultField.setHTML(safeHtmlBuilder.toSafeHtml());
		resultField.setVisible(true);

		Command sayComplete = new Command() {
			public void execute() {
				try {
					getWizardWindow().close(false);
					Log.info("fire Aborted");

					getWizardWindow().fireAborted();

				} catch (Exception e) {
					Log.error("fire Aborted :" + e.getLocalizedMessage());
				}
			}
		};

		getWizardWindow().setNextButtonCommand(sayComplete);

		setNextButtonVisible(true);
		getWizardWindow().setEnableNextButton(true);
		forceLayout();

	}

	@Override
	public void operationPutInBackground() {
		SafeHtmlBuilder safeHtmlBuilder = new SafeHtmlBuilder();
		safeHtmlBuilder
				.appendHtmlConstant("<div style='text-align:center;font-size:large;font-weight:bold;color: #00AAAA;'>"+msgsCommon.operationInBackground()+"</div>");
		resultField.setHTML(safeHtmlBuilder.toSafeHtml());
		resultField.setVisible(true);

		Command sayComplete = new Command() {
			public void execute() {
				try {
					getWizardWindow().close(false);
					Log.info("fire Operation In Background");

					getWizardWindow().firePutInBackground();

				} catch (Exception e) {
					Log.error("fire Operation In Background :"
							+ e.getLocalizedMessage());
				}
			}
		};

		getWizardWindow().setNextButtonCommand(sayComplete);

		setNextButtonVisible(true);
		getWizardWindow().setEnableNextButton(true);
		forceLayout();

	}

}
