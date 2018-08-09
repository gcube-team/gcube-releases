/**
 * 
 */
package org.gcube.portlets.user.td.resourceswidget.client.save;

import org.gcube.portlets.user.td.gwtservice.client.rpc.TDGWTServiceAsync;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTIsLockedException;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTSessionExpiredException;
import org.gcube.portlets.user.td.gwtservice.shared.tr.resources.SaveResourceSession;
import org.gcube.portlets.user.td.widgetcommonevent.client.CommonMessages;
import org.gcube.portlets.user.td.widgetcommonevent.client.event.SessionExpiredEvent;
import org.gcube.portlets.user.td.widgetcommonevent.client.type.SessionExpiredType;
import org.gcube.portlets.user.td.wizardwidget.client.WizardCard;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.shared.GWT;
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
public class OperationInProgressCard extends WizardCard {
	private static SaveResourceMessages msgs=GWT.create(SaveResourceMessages.class);
	private SaveResourceSession saveResourceSession;
	private HtmlLayoutContainer resultField;
	private CommonMessages msgsCommon;

	public OperationInProgressCard(final SaveResourceSession saveResourceSession) {
		super(msgs.operationInProgressCardHead(), "");
		this.saveResourceSession = saveResourceSession;
		
		initMessages();
	
		VBoxLayoutContainer operationInProgressPanel = new VBoxLayoutContainer();
		operationInProgressPanel.setVBoxLayoutAlign(VBoxLayoutAlign.CENTER);

		final FlexTable description = new FlexTable();
		// FlexCellFormatter cellFormatter = description.getFlexCellFormatter();
		description.setCellSpacing(10);
		description.setCellPadding(4);
		description.setBorderWidth(0);

		// display:block;vertical-align:text-top;
		description.setHTML(0, 0,
				"<span style=\"font-weight:bold;\";>"+msgs.destinationFixed()+"</span>");
		description.setText(0, 1, saveResourceSession.getDestination()
				.getName());
		description.setHTML(1, 0,
				"<span style=\"font-weight:bold;\";>"+msgs.fileNameFixed()+"</span>");
		description.setText(1, 1, saveResourceSession.getFileName());

		description.setHTML(2, 0,
				"<span style=\"font-weight:bold;\";>"+msgs.fileDescriptionFixed()+"</span>");
		description.setText(2, 1, saveResourceSession.getFileDescription());

		FramedPanel summary = new FramedPanel();
		summary.setHeadingText(msgs.summarySave());
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
	
	protected void initMessages(){
		msgsCommon=GWT.create(CommonMessages.class);
	}

	public void saveResource() {
		TDGWTServiceAsync.INSTANCE.saveResource(saveResourceSession,
				new AsyncCallback<Void>() {

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
								Log.error("Error saving the resource: "
										+ caught.getLocalizedMessage());
								showErrorAndHide(msgsCommon.error(),
										msgs.errorSavingTheResource(),
										caught.getLocalizedMessage(), caught);

							}
						}
						operationFailed(caught, caught.getLocalizedMessage(),
								caught.getLocalizedMessage());
					}

					public void onSuccess(Void v) {
						Log.debug("Resource saved");
						operationComplete();
					}

				});

	}

	@Override
	public void setup() {
		getWizardWindow().setEnableBackButton(false);
		setBackButtonVisible(false);
		getWizardWindow().setEnableNextButton(false);
		getWizardWindow().setNextButtonToFinish();
		saveResource();
	}

	public void operationComplete() {
		SafeHtmlBuilder safeHtmlBuilder = new SafeHtmlBuilder();
		safeHtmlBuilder
				.appendHtmlConstant("<div style='display:block;margin:auto;text-align:center;font-size:large;font-weight:bold; color:#009900;'>"+msgsCommon.operationCompleted()+"</div>");
		resultField.setHTML(safeHtmlBuilder.toSafeHtml());
		
		resultField.setVisible(true);

		Command sayComplete = new Command() {
			public void execute() {
				try {
					getWizardWindow().close(false);
					getWizardWindow().fireCompleted(null);

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

	public void operationFailed(Throwable caught, String reason, String details) {
		SafeHtmlBuilder safeHtmlBuilder = new SafeHtmlBuilder();
		safeHtmlBuilder
				.appendHtmlConstant("<div style='display:block;margin:auto;text-align:center;font-size:large;font-weight:bold;color:red;'>"+msgsCommon.operationFailed()+"</div>");
		resultField.setHTML(safeHtmlBuilder.toSafeHtml());
	
		resultField.setVisible(true);
		if (caught instanceof TDGWTSessionExpiredException) {
			getEventBus()
					.fireEvent(
							new SessionExpiredEvent(
									SessionExpiredType.EXPIREDONSERVER));
		} else {
			showErrorAndHide(msgsCommon.error(), reason, details, caught);
		}

		forceLayout();
	}

}
