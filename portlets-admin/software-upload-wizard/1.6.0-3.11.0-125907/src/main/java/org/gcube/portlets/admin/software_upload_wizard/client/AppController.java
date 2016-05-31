package org.gcube.portlets.admin.software_upload_wizard.client;

import net.customware.gwt.dispatch.client.DispatchAsync;

import org.gcube.portlets.admin.software_upload_wizard.client.event.GoAheadEvent;
import org.gcube.portlets.admin.software_upload_wizard.client.event.GoAheadEventHandler;
import org.gcube.portlets.admin.software_upload_wizard.client.event.GoBackEvent;
import org.gcube.portlets.admin.software_upload_wizard.client.event.GoBackEventHandler;
import org.gcube.portlets.admin.software_upload_wizard.client.event.SoftwareTypeSelectedEvent;
import org.gcube.portlets.admin.software_upload_wizard.client.event.SoftwareTypeSelectedEventHandler;
import org.gcube.portlets.admin.software_upload_wizard.client.event.WizardCompleteEvent;
import org.gcube.portlets.admin.software_upload_wizard.client.event.WizardCompleteEventHandler;
import org.gcube.portlets.admin.software_upload_wizard.client.util.Util;
import org.gcube.portlets.admin.software_upload_wizard.client.view.SoftwareUploadWizardWindow;
import org.gcube.portlets.admin.software_upload_wizard.client.view.WizardWindow;
import org.gcube.portlets.admin.software_upload_wizard.client.view.card.SoftwareTypeSelectionCard;
import org.gcube.portlets.admin.software_upload_wizard.client.view.card.SubmitCompletedCard;
import org.gcube.portlets.admin.software_upload_wizard.client.view.card.WizardCard;
import org.gcube.portlets.admin.software_upload_wizard.client.wizard.IWizard;
import org.gcube.portlets.admin.software_upload_wizard.client.wizard.factory.DefaultWizardFactory;
import org.gcube.portlets.admin.software_upload_wizard.client.wizard.factory.WizardFactory;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.GetPackageIds;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.GetPackageIdsResult;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.SetScope;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.SetScopeResult;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class AppController {

	private final DispatchAsync dispatchAsync = Util.getDispatcher();
	private final HandlerManager eventBus;
	private IWizard wizard;
	private WizardFactory factory = new DefaultWizardFactory();
	private WizardWindow wizardWindow;
	private String scope;

	public AppController(HandlerManager eventBus, String scope) {
		this.scope = scope;
		this.eventBus = eventBus;
		Util.setEventBus(eventBus);
		bind();
	}

	private void bind() {
		// Handle wizard navigation - going back
		eventBus.addHandler(GoBackEvent.TYPE, new GoBackEventHandler() {

			@Override
			public void onBackButtonPressed(GoBackEvent event) {
				try {
					WizardCard prevCard = wizard.goToPreviousCard();
					
					Log.trace("Setting card: " + prevCard.getClass().getName().substring(prevCard.getClass().getName().lastIndexOf(".")+1));
					wizardWindow.setCard(prevCard);

					if (!wizard.isOnLastCard())
						wizardWindow.getNextButton().show();
				} catch (Exception ex) {
					Log.trace("Exception caught while going back one step in wizard.",ex);
					resetWizard();
					return;
				}
			}
		});

		// Handle wizard navigation - going forward
		eventBus.addHandler(GoAheadEvent.TYPE, new GoAheadEventHandler() {

			@Override
			public void onNextButtonPressed(GoAheadEvent event) {
				// WizardCard card = event.getRelativeCard();

				if (wizard.isOnLastCard()) {
					MessageBox.alert("Software Management Wizard",
							"Wizard completed", null);
					return;
				}

				try {
					WizardCard nextCard = wizard.goToNextCard();
					if (wizard.isOnLastCard())
						wizardWindow.getNextButton().hide();
					else
						wizardWindow
								.getNextButton()
								.setText(
										SoftwareUploadWizardWindow.NEXT_BUTTON_DEFAULT_TEXT);
					
					Log.trace("Setting card: " + nextCard.getClass().getName().substring(nextCard.getClass().getName().lastIndexOf(".")+1));
					wizardWindow.setCard(nextCard);
				} catch (Exception ex) {
					Log.trace("Exception caught while going ahead one step in wizard.",ex);
					ex.printStackTrace();
				}

			}
		});

		// Handle selection of software type
		eventBus.addHandler(SoftwareTypeSelectedEvent.TYPE,
				new SoftwareTypeSelectedEventHandler() {

					@Override
					public void onSoftwareTypeSelected(
							final SoftwareTypeSelectedEvent event) {
						Log.trace("Selected software type " + event.getSoftwareTypeCode());
						dispatchAsync.execute(new GetPackageIds(),
								new AsyncCallback<GetPackageIdsResult>() {

									@Override
									public void onFailure(Throwable caught) {
									}

									@Override
									public void onSuccess(
											GetPackageIdsResult result) {
										try {
											IWizard wizard = factory
													.getWizard(
															event.getSoftwareTypeCode(),
															result.getIds());
											AppController.this.wizard = wizard;
											Util.setWizard(wizard);
											AppController.this.startWizard();
										} catch (Exception e) {
											Log.error("An error occurred while initializing the wizard", e);
										}
									}
								});

					}
				});

		// Handle wizard completion
		eventBus.addHandler(WizardCompleteEvent.TYPE,
				new WizardCompleteEventHandler() {

					@Override
					public void onWizardCompleted(WizardCompleteEvent event) {
						wizardWindow.setNavigationToolbarVisible(false);
						wizardWindow.setCard(new SubmitCompletedCard());
					}
				});
	}

	private void startWizard() {
		wizardWindow.setCard(wizard.getCurrentCard());
		wizardWindow.getBackButton().setVisible(true);
	}

	public void go() {
		dispatchAsync.execute(new SetScope(scope), new AsyncCallback<SetScopeResult>() {

			@Override
			public void onFailure(Throwable caught) {
				Log.error("Unable to save scope for SoftwareManagementWidget execution");
				MessageBox.alert("Software Management Wizard error", "Unable to save scope.", null);
			}

			@Override
			public void onSuccess(SetScopeResult result) {
				Log.debug("Scope correctly set, starting wizard...");
				wizardWindow = new SoftwareUploadWizardWindow();
				Util.setWindow(wizardWindow);
				resetWizard();
			}
		});
		
	}

	public void resetWizard() {
		Util.setWizard(null);
		wizardWindow.setCard(new SoftwareTypeSelectionCard());
		wizardWindow.show();
	}

	public void showErrorAndHide(String message) {
		MessageBox.alert("Error", message, null);
	}

}
