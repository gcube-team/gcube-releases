package org.gcube.portlets.admin.software_upload_wizard.client.view.card.generalinfo;

import net.customware.gwt.dispatch.client.DispatchAsync;
import net.customware.gwt.dispatch.shared.BatchAction;
import net.customware.gwt.dispatch.shared.BatchAction.OnException;
import net.customware.gwt.dispatch.shared.BatchResult;

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
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.GetThirdParty;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.GetThirdPartyResult;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.SetGeneralInfo;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.SetGeneralInfoResult;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.widget.form.FormButtonBinding;
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class LicenseCard extends WizardCard {

	private static final String STEP_INFO_TEXT = Resources.INSTANCE
			.stepInfo_License().getText();

	private WizardWindow window = Util.getWindow();
	private DispatchAsync dispatchAsync = Util.getDispatcher();

	private FormButtonBinding binding = new FormButtonBinding(this);

	private LicenseTextArea licenseTextArea = new LicenseTextArea();

	public LicenseCard() {
		super("Edit License");

		this.setLabelAlign(LabelAlign.TOP);

		InfoPanel stepInfo = new InfoPanel();
		stepInfo.setText(STEP_INFO_TEXT);

		this.add(stepInfo);

		this.add(licenseTextArea, new FormData("-20"));
	}

	@Override
	public void setup() {
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
		Log.debug("Saving data");
		dispatchAsync.execute(new GetGeneralInfo(),
				new AsyncCallback<GetGeneralInfoResult>() {

					@Override
					public void onFailure(Throwable caught) {
					}

					@Override
					public void onSuccess(GetGeneralInfoResult result) {
						GeneralInfo generalInfo = result.getGeneralInfo();

						generalInfo.setLicense(licenseTextArea.getValue());

						dispatchAsync.execute(new SetGeneralInfo(generalInfo),
								new AsyncCallback<SetGeneralInfoResult>() {

									@Override
									public void onFailure(Throwable caught) {
									}

									@Override
									public void onSuccess(
											SetGeneralInfoResult result) {
										Log.debug("Data saved");
										binding.removeButton(window
												.getNextButton());
										Util.getEventBus().fireEvent(event);
									}
								});
					}
				});

	}

	private void loadData() {
		Log.debug("Loading data...");
		this.mask();
		dispatchAsync.execute(new BatchAction(OnException.CONTINUE,
				new GetGeneralInfo(), new GetThirdParty()),
				new AsyncCallback<BatchResult>() {

					@Override
					public void onFailure(Throwable caught) {
					}

					@Override
					public void onSuccess(BatchResult result) {
						GeneralInfo generalInfo = result.getResult(0,
								GetGeneralInfoResult.class).getGeneralInfo();
						boolean isThirdParty = result.getResult(1,
								GetThirdPartyResult.class).isThirdParty();

						if (!generalInfo.getLicense().equals("")) {
							licenseTextArea.setRawValue(generalInfo
									.getLicense());
						} else if (isThirdParty) {
							licenseTextArea.setRawValue("");
							return;
						} else if (!isThirdParty) {
							licenseTextArea
									.setValue(GeneralInfo.LICENSE_DEFAULT);
							licenseTextArea.setReadOnly(true);
						}

						LicenseCard.this.unmask();
						Log.debug("Data loaded.");
					}

				});
	}

	public class LicenseTextArea extends TextArea {
		public LicenseTextArea() {
			this.setFieldLabel("License notes*");
			this.setAllowBlank(false);
			this.setHeight(400);
		}
	}

	@Override
	public String getHelpContent() {
		return Resources.INSTANCE.stepHelp_License().getText();
	}

}
