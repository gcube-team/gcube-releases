package org.gcube.portlets.admin.software_upload_wizard.client.view.card;

import java.util.ArrayList;

import net.customware.gwt.dispatch.client.DispatchAsync;

import org.gcube.portlets.admin.software_upload_wizard.client.event.GoBackEvent;
import org.gcube.portlets.admin.software_upload_wizard.client.event.WizardCompleteEvent;
import org.gcube.portlets.admin.software_upload_wizard.client.util.Resources;
import org.gcube.portlets.admin.software_upload_wizard.client.util.Util;
import org.gcube.portlets.admin.software_upload_wizard.client.view.WizardWindow;
import org.gcube.portlets.admin.software_upload_wizard.client.view.widget.InfoPanel;
import org.gcube.portlets.admin.software_upload_wizard.shared.Deliverable;
import org.gcube.portlets.admin.software_upload_wizard.shared.IOperationProgress;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.GetDeliverables;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.GetDeliverablesResult;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.GetSubmitProgress;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.GetSubmitProgressResult;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.SubmitSoftwareRegistration;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.SubmitSoftwareRegistrationResult;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.ProgressBar;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FormButtonBinding;
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class SubmitCard extends WizardCard {

	DispatchAsync dispatchAsync = Util.getDispatcher();
	WizardWindow window = Util.getWindow();

	FormButtonBinding binding = new FormButtonBinding(this);

	DeliverablesTab deliverablesTab = new DeliverablesTab();
	Button submitButton = new Button("Submit");
	SubmitProgressBar submitProgressBar = new SubmitProgressBar();

	public SubmitCard() {
		super("Submit software");

		this.setButtonAlign(HorizontalAlignment.CENTER);

		InfoPanel stepInfo = new InfoPanel();
		stepInfo.setText(Resources.INSTANCE.stepInfo_Submit().getText());

		this.add(stepInfo);
		this.add(deliverablesTab, new FormData("100%"));
		this.add(submitButton);
		this.add(submitProgressBar);

		bind();
	}

	private void bind() {
		submitButton.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				SubmitCard.this.submitSoftware();
			}
		});
	}

	@Override
	public void setup() {
		window.setBackButtonEnabled(true);
		window.setNextButtonEnabled(false);
		binding.addButton(window.getNextButton());
		loadData();
	}

	@Override
	public void performNextStepLogic() {
	}

	@Override
	public void performBackStepLogic() {
		Util.getEventBus().fireEvent(new GoBackEvent(this));
	}

	private void loadData() {
		Log.debug("Loading deliverables...");
		this.mask();
		deliverablesTab.removeAll();

		dispatchAsync.execute(new GetDeliverables(),
				new AsyncCallback<GetDeliverablesResult>() {

					@Override
					public void onFailure(Throwable caught) {
					}

					@Override
					public void onSuccess(GetDeliverablesResult result) {
						ArrayList<Deliverable> files = result.getDeliverables();
							
						for (Deliverable mf : files)
							deliverablesTab.addTabItem(mf.getName(),
									mf.getContent());
						SubmitCard.this.unmask();
						
						Log.debug("Deliverables loaded");
					}
				});
	}

	private void submitSoftware() {
		window.setBackButtonEnabled(false);
		submitButton.disable();
		Log.debug("Submitting software");
		dispatchAsync.execute(new SubmitSoftwareRegistration(),
				new AsyncCallback<SubmitSoftwareRegistrationResult>() {

					@Override
					public void onFailure(Throwable caught) {
					}

					@Override
					public void onSuccess(
							SubmitSoftwareRegistrationResult result) {
						Log.debug("Software submission started");
					}
				});

		SubmitStatusUpdater submitStatusUpdater = new SubmitStatusUpdater();
		submitStatusUpdater.scheduleRepeating();
	}

	private void softwareRegistrationCompleted() {
		
		Timer timer = new Timer() {
			
			@Override
			public void run() {
				Util.getEventBus().fireEvent(new WizardCompleteEvent());
			}
		};
		
		timer.schedule(3000);
		
//		MessageBox.alert("Operation succesful",
//				"Software was registered successfully.",
//				new Listener<MessageBoxEvent>() {
//
//					@Override
//					public void handleEvent(MessageBoxEvent be) {
//						
//					}
//				});
	}
	
	private void softwareRegistrationFailed(){
		submitButton.enable();
		window.setBackButtonEnabled(true);
	}

	public class DeliverablesTab extends TabPanel {

		public DeliverablesTab() {
			this.setHeight(300);
			this.setTabScroll(true);
		}

		public void addTabItem(String title, String content) {
			DeliverableTabItem item = new DeliverableTabItem(title, content);
			this.add(item);
			if (this.getItems().size() == 1)
				this.setSelection(this.getItem(0));
		}

		private class DeliverableTabItem extends TabItem {
			public DeliverableTabItem(String title, String content) {
				this.setAutoHeight(true);
				this.setText(title);
				this.setLayout(new FitLayout());
				TextArea textArea = new TextArea();
				textArea.setValue(content);
				textArea.setReadOnly(true);
				this.add(textArea);
				this.setScrollMode(Scroll.AUTO);
			}
		}

	}

	private class SubmitProgressBar extends ProgressBar {
		public SubmitProgressBar() {

		}

	}

	private class SubmitStatusUpdater extends Timer {

		private static final int STATUS_POLLING_DELAY = 1500;

		private boolean executing = false;

		public void scheduleRepeating() {
			super.scheduleRepeating(STATUS_POLLING_DELAY);
		}

		@Override
		public synchronized void run() {
			if (executing == true)
				return;
			executing = true;
			Log.trace("requesting operation progress");

			dispatchAsync.execute(new GetSubmitProgress(),
					new AsyncCallback<GetSubmitProgressResult>() {

						@Override
						public void onFailure(Throwable caught) {
						}

						@Override
						public void onSuccess(GetSubmitProgressResult result) {
							IOperationProgress progress = result.getProgress();
							Log.trace("retrieved OperationProgress:\t" + progress.getElaboratedLenght() + "/" + progress.getTotalLenght() + "\t" + progress.getState());
							submitProgressBar.updateProgress(
									progress.getProgress(),
									progress.getDetails());
							switch (progress.getState()) {
							case COMPLETED: {
								SubmitCard.this.softwareRegistrationCompleted();
								cancel();
								break;
							}
							case FAILED: {
								SubmitCard.this.softwareRegistrationFailed();
								cancel();
								break;
							}
							}
							executing = false;
						}

					});
		}
	}

	@Override
	public String getHelpContent() {
		return Resources.INSTANCE.stepHelp_Submit().getText();
	}
}
