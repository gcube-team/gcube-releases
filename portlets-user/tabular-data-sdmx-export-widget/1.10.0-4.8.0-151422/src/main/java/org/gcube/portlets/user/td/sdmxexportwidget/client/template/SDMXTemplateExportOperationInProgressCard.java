/**
 * 
 */
package org.gcube.portlets.user.td.sdmxexportwidget.client.template;

import org.gcube.portlets.user.td.gwtservice.client.rpc.TDGWTServiceAsync;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTIsLockedException;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTSessionExpiredException;
import org.gcube.portlets.user.td.gwtservice.shared.sdmx.SDMXTemplateExportSession;
import org.gcube.portlets.user.td.monitorwidget.client.MonitorDialog;
import org.gcube.portlets.user.td.monitorwidget.client.MonitorDialogListener;
import org.gcube.portlets.user.td.widgetcommonevent.client.event.SessionExpiredEvent;
import org.gcube.portlets.user.td.widgetcommonevent.client.type.SessionExpiredType;
import org.gcube.portlets.user.td.widgetcommonevent.shared.OperationResult;
import org.gcube.portlets.user.td.widgetcommonevent.shared.TRId;
import org.gcube.portlets.user.td.wizardwidget.client.WizardCard;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlexTable;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.widget.core.client.FramedPanel;
import com.sencha.gxt.widget.core.client.container.BoxLayoutContainer.BoxLayoutData;
import com.sencha.gxt.widget.core.client.container.HtmlLayoutContainer;
import com.sencha.gxt.widget.core.client.container.MarginData;
import com.sencha.gxt.widget.core.client.container.VBoxLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VBoxLayoutContainer.VBoxLayoutAlign;

/**
 * 
 * @author Giancarlo Panichi
 *
 * 
 */
public class SDMXTemplateExportOperationInProgressCard extends WizardCard implements
		MonitorDialogListener {

	protected SDMXTemplateExportOperationInProgressCard thisCard;
	protected SDMXTemplateExportSession sdmxTemplateExportSession;
	protected TRId newTrId;
	protected HtmlLayoutContainer resultField;

	public SDMXTemplateExportOperationInProgressCard(final SDMXTemplateExportSession sdmxTemplateExportSession) {
		super("Operation In Progress", "");

		this.sdmxTemplateExportSession = sdmxTemplateExportSession;
		thisCard = this;
		String urlRegistry;
		if (sdmxTemplateExportSession.getRegistryBaseUrl() == null
				|| sdmxTemplateExportSession.getRegistryBaseUrl().isEmpty()) {
			urlRegistry = "Internal";
		} else {
			urlRegistry = sdmxTemplateExportSession.getRegistryBaseUrl();
		}

		VBoxLayoutContainer operationInProgressPanel = new VBoxLayoutContainer();
		operationInProgressPanel.setVBoxLayoutAlign(VBoxLayoutAlign.CENTER);

		final FlexTable description = new FlexTable();
		// FlexCellFormatter cellFormatter = description.getFlexCellFormatter();
		description.setCellSpacing(10);
		description.setCellPadding(4);
		description.setBorderWidth(0);

		// display:block;vertical-align:text-top;
		description.setHTML(0, 0,
				"<span style=\"font-weight:bold;\";>Destination: </span>");
		description.setText(0, 1, sdmxTemplateExportSession.getSource().getName());

		description.setHTML(1, 0,
				"<span style=\"font-weight:bold;\";>Id: </span>");
		description.setText(1, 1, sdmxTemplateExportSession.getId());

		description.setHTML(2, 0,
				"<span style=\"font-weight:bold;\";>Agency Id: </span>");
		description.setText(2, 1, sdmxTemplateExportSession.getAgencyId());

		description.setHTML(3, 0,
				"<span style=\"font-weight:bold;\";>Registry: </span>");
		description.setText(3, 1, urlRegistry);

		description.setHTML(4, 0,
				"<span style=\"font-weight:bold;\";>Version: </span>");
		description.setText(4, 1, sdmxTemplateExportSession.getVersion());

		FramedPanel summary = new FramedPanel();
		summary.setHeadingText("Export Summary");
		summary.setWidth(400);
		summary.add(description);
		operationInProgressPanel.add(summary, new BoxLayoutData(new Margins(20,
				5, 10, 5)));

		resultField = new HtmlLayoutContainer("<div></div>");

		operationInProgressPanel.add(resultField, new BoxLayoutData(
				new Margins(10, 5, 10, 5)));

		setCenterWidget(operationInProgressPanel, new MarginData(0));
		resultField.setVisible(false);

	}

	// columnToImportMask

	public void exportSDMX() {
		TDGWTServiceAsync.INSTANCE.startSDMXTemplateExport(sdmxTemplateExportSession,
				new AsyncCallback<String>() {

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
								showErrorAndHide("Error Locked",
										caught.getLocalizedMessage(), "",
										caught);
							} else {
								showErrorAndHide("Error in template export SDMX",
										"An error occured in template export SDMX.",
										caught.getLocalizedMessage(), caught);
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
		exportSDMX();
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
				.appendHtmlConstant("<div style='text-align:center;font-size:large;font-weight:bold; color:#009900;'>Operation Completed<br>Check The Resources</div>");
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
				.appendHtmlConstant("<div style='text-align:center;font-size:large;font-weight:bold;color:red;'>Operation Failed</div>");
		resultField.setHTML(safeHtmlBuilder.toSafeHtml());
		resultField.setVisible(true);
		if (caught instanceof TDGWTSessionExpiredException) {
			getEventBus()
					.fireEvent(
							new SessionExpiredEvent(
									SessionExpiredType.EXPIREDONSERVER));
		} else {
			showErrorAndHide("Error in SDMX Export", reason, details, caught);
		}
		forceLayout();
	}

	@Override
	public void operationStopped(OperationResult operationResult, String reason, String details) {
		newTrId = operationResult.getTrId();
		SafeHtmlBuilder safeHtmlBuilder = new SafeHtmlBuilder();
		safeHtmlBuilder
				.appendHtmlConstant("<div style='text-align:center;font-size:large;font-weight:bold;color: #FF9900;'>Problems in the Operation</div>");
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
				.appendHtmlConstant("<div style='text-align:center;font-size:large;font-weight:bold;color: #AA00AA;'>Operation Aborted</div>");
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
				.appendHtmlConstant("<div style='text-align:center;font-size:large;font-weight:bold;color: #00AAAA;'>Operation in Background</div>");
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
