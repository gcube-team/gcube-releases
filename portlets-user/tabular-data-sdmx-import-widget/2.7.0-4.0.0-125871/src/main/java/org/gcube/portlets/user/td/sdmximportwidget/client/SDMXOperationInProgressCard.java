/**
 * 
 */
package org.gcube.portlets.user.td.sdmximportwidget.client;

import org.gcube.portlets.user.td.gwtservice.client.rpc.TDGWTServiceAsync;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTIsLockedException;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTSessionExpiredException;
import org.gcube.portlets.user.td.gwtservice.shared.sdmx.SDMXImportSession;
import org.gcube.portlets.user.td.gwtservice.shared.source.SDMXRegistrySource;
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
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.widget.core.client.FramedPanel;
import com.sencha.gxt.widget.core.client.container.BoxLayoutContainer.BoxLayoutData;
import com.sencha.gxt.widget.core.client.container.HtmlLayoutContainer;
import com.sencha.gxt.widget.core.client.container.MarginData;
import com.sencha.gxt.widget.core.client.container.VBoxLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VBoxLayoutContainer.VBoxLayoutAlign;

/**
 * 
 * @author "Giancarlo Panichi" <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 * 
 */
public class SDMXOperationInProgressCard extends WizardCard implements
		MonitorDialogListener {

	protected SDMXOperationInProgressCard thisCard;
	protected SDMXImportSession importSession;
	protected TRId newTrId;
	protected HtmlLayoutContainer resultField;

	public SDMXOperationInProgressCard(final SDMXImportSession importSession) {
		super("Operation In Progress", "");

		this.importSession = importSession;
		thisCard = this;

		VBoxLayoutContainer operationInProgressPanel = new VBoxLayoutContainer();
		operationInProgressPanel.setVBoxLayoutAlign(VBoxLayoutAlign.CENTER);

		final FlexTable description = new FlexTable();
		FlexCellFormatter cellFormatter = description.getFlexCellFormatter();
		description.setCellSpacing(10);
		description.setCellPadding(4);
		description.setBorderWidth(0);

		// display:block;vertical-align:text-top;
		description.setHTML(0, 0,
				"<span style=\"font-weight:bold;\";>Document: </span>");
		description.setText(0, 1, importSession.getSDMXDocument().getName());
		description.setHTML(1, 0,
				"<span style=\"font-weight:bold;\";>Source: </span>");
		description.setText(1, 1, importSession.getSource().getName());

		if (importSession.getSource().getId().compareTo("SDMXRegistry") == 0) {
			description.setHTML(2, 0,
					"<span style=\"font-weight:bold;\";>Url: </span>");
			description
					.setText(
							2,
							1,
							((SDMXRegistrySource) importSession.getSource())
									.getUrl() == null ? "Internal"
									: ((SDMXRegistrySource) importSession
											.getSource()).getUrl());
			if (importSession.getSDMXDocument().getId().compareTo("codelist") == 0) {
				cellFormatter.setVerticalAlignment(3, 0,
						HasVerticalAlignment.ALIGN_TOP);
				description
						.setHTML(3, 0,
								"<span style=\"font-weight:bold;\";>Codelist Selected: </span>");
				final FlexTable codelistDescription = new FlexTable();
				codelistDescription.setBorderWidth(0);
				codelistDescription.setCellPadding(4);
				codelistDescription.setCellSpacing(10);

				codelistDescription
						.setHTML(0, 0,
								"<span style=\"text-decoration:underline;margin-right:5px;\";>Id: </span>");
				codelistDescription.setText(0, 1, importSession
						.getSelectedCodelist().getId());
				codelistDescription
						.setHTML(1, 0,
								"<span style=\"text-decoration:underline;margin-right:5px;\";>Name: </span>");
				codelistDescription.setText(1, 1, importSession
						.getSelectedCodelist().getName());
				codelistDescription
						.setHTML(2, 0,
								"<span style=\"text-decoration:underline;margin-right:5px;\";>Agency: </span>");
				codelistDescription.setText(2, 1, importSession
						.getSelectedCodelist().getAgencyId());
				codelistDescription
						.setHTML(3, 0,
								"<span style=\"text-decoration:underline;margin-right:5px;\";>Version: </span>");
				codelistDescription.setText(3, 1, importSession
						.getSelectedCodelist().getVersion());

				description.setWidget(3, 1, codelistDescription);
			}
		}

		FramedPanel summary = new FramedPanel();
		summary.setHeadingText("Import Summary");
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

	public void importSDMX() {
		TDGWTServiceAsync.INSTANCE.startSDMXImport(importSession,
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
										caught.getLocalizedMessage(), "", caught);
							} else {

								showErrorAndHide("Error in importSDMX",
										"An error occured in importSDMX.",
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
		importSDMX();
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
				.appendHtmlConstant("<div style='text-align:center;font-size:large;font-weight:bold; color:#009900;'>Operation Completed</div>");
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
			showErrorAndHide("Error in SDMX Import",
					reason,details, caught);
			
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
