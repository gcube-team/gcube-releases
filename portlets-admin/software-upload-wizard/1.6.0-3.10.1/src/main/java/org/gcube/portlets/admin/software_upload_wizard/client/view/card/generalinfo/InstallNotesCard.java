package org.gcube.portlets.admin.software_upload_wizard.client.view.card.generalinfo;

import net.customware.gwt.dispatch.client.DispatchAsync;

import org.gcube.portlets.admin.software_upload_wizard.client.event.GoAheadEvent;
import org.gcube.portlets.admin.software_upload_wizard.client.event.GoBackEvent;
import org.gcube.portlets.admin.software_upload_wizard.client.util.Resources;
import org.gcube.portlets.admin.software_upload_wizard.client.util.Util;
import org.gcube.portlets.admin.software_upload_wizard.client.view.WizardWindow;
import org.gcube.portlets.admin.software_upload_wizard.client.view.card.WizardCard;
import org.gcube.portlets.admin.software_upload_wizard.client.view.widget.InfoPanel;
import org.gcube.portlets.admin.software_upload_wizard.shared.GeneralInfo;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.GetGeneralInfo;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.GetGeneralInfoResult;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.SetGeneralInfo;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.SetGeneralInfoResult;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.widget.form.FormButtonBinding;
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class InstallNotesCard extends WizardCard {

	private static final String STEP_INFO_TEXT = Resources.INSTANCE
			.stepInfo_InstallNotes().getText();

	WizardWindow window = Util.getWindow();
	DispatchAsync dispatchAsync = Util.getDispatcher();

	FormButtonBinding binding = new FormButtonBinding(this);

	private InstallTextArea installation = new InstallTextArea(
			"Installation notes",80);
	private InstallTextArea configuration = new InstallTextArea(
			"Configuration notes",80);
	private InstallTextArea dependencies = new InstallTextArea(
			"Software dependencies",80);
	private InstallTextArea uninstallation = new InstallTextArea(
			"Uninstallation notes",80);

	public InstallNotesCard() {
		super("Edit install notes");

		FormData formData = new FormData("-20");

		this.setLabelAlign(LabelAlign.TOP);

		InfoPanel stepInfo = new InfoPanel();
		stepInfo.setText(STEP_INFO_TEXT);

		this.add(stepInfo);
		this.add(installation, formData);
		this.add(configuration, formData);
		this.add(dependencies, formData);
		this.add(uninstallation, formData);
	}

	@Override
	public void setup() {
		window.setBackButtonEnabled(true);
		binding.addButton(window.getNextButton());
		loadData();
	}

	@Override
	public void performNextStepLogic() {
		saveDataAndFireEvent(new GoAheadEvent(this));
	}

	@Override
	public void performBackStepLogic() {
		saveDataAndFireEvent(new GoBackEvent(this));
	}

	private <T extends GwtEvent<H>, H extends EventHandler> void saveDataAndFireEvent(
			final T event) {
		Log.debug("Retrieving general info");

		dispatchAsync.execute(new GetGeneralInfo(),
				new AsyncCallback<GetGeneralInfoResult>() {

					@Override
					public void onFailure(Throwable caught) {
					}

					@Override
					public void onSuccess(GetGeneralInfoResult result) {
						Log.debug("General info retrieved");

						GeneralInfo generalInfo = result.getGeneralInfo();

						generalInfo.setInstallationNotes(installation
								.getValue());
						generalInfo.setConfigurationNotes(configuration
								.getValue());
						generalInfo.setDependenciesNotes(dependencies
								.getValue());
						generalInfo.setUninstallationNotes(uninstallation
								.getValue());

						Log.debug("Saving general info");
						dispatchAsync.execute(new SetGeneralInfo(generalInfo),
								new AsyncCallback<SetGeneralInfoResult>() {

									@Override
									public void onSuccess(
											SetGeneralInfoResult result) {
										Log.info("General info saved");
										binding.removeButton(window
												.getNextButton());
										Util.getEventBus().fireEvent(event);
									}

									@Override
									public void onFailure(Throwable caught) {
									}
								});
					}
				});
	}

	private void loadData() {
		Log.debug("Loading data...");
		this.mask();

		dispatchAsync.execute(new GetGeneralInfo(),
				new AsyncCallback<GetGeneralInfoResult>() {

					@Override
					public void onFailure(Throwable caught) {
					}

					@Override
					public void onSuccess(GetGeneralInfoResult result) {
						GeneralInfo generalInfo = result.getGeneralInfo();
						installation.setValue(generalInfo
								.getInstallationNotes());
						configuration.setValue(generalInfo
								.getConfigurationNotes());
						dependencies.setValue(generalInfo
								.getDependenciesNotes());
						uninstallation.setValue(generalInfo
								.getUninstallationNotes());
						
						InstallNotesCard.this.unmask();
						
						Log.debug("Data loaded.");
					}
				});

	}

	private class InstallTextArea extends TextArea {

		public InstallTextArea(String fieldLabel) {
			this(fieldLabel,110);
		}
		
		public InstallTextArea(String fieldLabel, int height){
			this.setFieldLabel(fieldLabel);
			this.setAllowBlank(true);
			this.setHeight(height);
		}
	}

	@Override
	public String getHelpContent() {
		return Resources.INSTANCE.stepHelp_InstallNotes().getText();
	}

}
