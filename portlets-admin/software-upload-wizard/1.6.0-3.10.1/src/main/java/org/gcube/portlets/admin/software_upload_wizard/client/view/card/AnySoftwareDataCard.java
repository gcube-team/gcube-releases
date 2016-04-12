package org.gcube.portlets.admin.software_upload_wizard.client.view.card;

import net.customware.gwt.dispatch.client.DispatchAsync;
import net.customware.gwt.dispatch.shared.BatchAction;
import net.customware.gwt.dispatch.shared.BatchAction.OnException;
import net.customware.gwt.dispatch.shared.BatchResult;

import org.gcube.portlets.admin.software_upload_wizard.client.event.GoAheadEvent;
import org.gcube.portlets.admin.software_upload_wizard.client.event.GoBackEvent;
import org.gcube.portlets.admin.software_upload_wizard.client.util.Resources;
import org.gcube.portlets.admin.software_upload_wizard.client.util.Util;
import org.gcube.portlets.admin.software_upload_wizard.client.view.WizardWindow;
import org.gcube.portlets.admin.software_upload_wizard.client.view.widget.GenericComponentInfoFieldSet;
import org.gcube.portlets.admin.software_upload_wizard.client.view.widget.InfoPanel;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.GetPackageData;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.GetPackageDataResult;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.SetPackageData;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.SetServiceData;
import org.gcube.portlets.admin.software_upload_wizard.shared.softwareprofile.PackageData;
import org.gcube.portlets.admin.software_upload_wizard.shared.softwareprofile.ServiceData;
import org.gcube.portlets.admin.software_upload_wizard.shared.softwareprofile.PackageData.PackageType;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.widget.form.FormButtonBinding;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.rpc.AsyncCallback;

public final class AnySoftwareDataCard extends WizardCard {

	private WizardWindow window = Util.getWindow();
	private DispatchAsync dispatchAsync = Util.getDispatcher();
	private HandlerManager eventBus = Util.getEventBus();

	private FormButtonBinding binding = new FormButtonBinding(this);

	private GenericComponentInfoFieldSet softwareFieldSet = new GenericComponentInfoFieldSet(
			"Software");

	private String packageId;

	public AnySoftwareDataCard(String packageId) {
		super("Edit AnySoftware service and package data");

		this.packageId = packageId;

		InfoPanel stepInfo = new InfoPanel();
		stepInfo.setText(Resources.INSTANCE.stepInfo_AnySoftwareData()
				.getText());

		this.add(stepInfo);
		this.add(softwareFieldSet);
	}

	@Override
	public void setup() {
		window.setBackButtonEnabled(true);
		binding.addButton(window.getNextButton());
		loadData();
	}

	private void loadData() {
		Log.debug("Loading data...");
		this.mask();
		dispatchAsync.execute(new GetPackageData(packageId),
				new AsyncCallback<GetPackageDataResult>() {

					@Override
					public void onFailure(Throwable caught) {
					}

					@Override
					public void onSuccess(GetPackageDataResult result) {
						PackageData packageData = result.getData();

						softwareFieldSet.setName(packageData.getName());
						softwareFieldSet.setDescription(packageData
								.getDescription());
						softwareFieldSet.setVersion(packageData.getVersion());
						
						AnySoftwareDataCard.this.unmask();
						Log.debug("Data loaded");
					}
				});
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

		ServiceData serviceData = new ServiceData();
		serviceData.setName(softwareFieldSet.getName());
		serviceData.setDescription(softwareFieldSet.getDescription());

		PackageData packageData = new PackageData(PackageType.Software);

		packageData.setName(softwareFieldSet.getName());
		packageData.setDescription(softwareFieldSet.getDescription());
		packageData.setVersion(softwareFieldSet.getVersion());

		Log.debug("Saving data...");
		dispatchAsync.execute(new BatchAction(OnException.ROLLBACK,
				new SetServiceData(serviceData), new SetPackageData(packageId,
						packageData)), new AsyncCallback<BatchResult>() {

			@Override
			public void onFailure(Throwable caught) {
			}

			@Override
			public void onSuccess(BatchResult result) {
				Log.debug("Data saved succesfully");
				binding.removeButton(window.getNextButton());
				eventBus.fireEvent(event);
			}
		});

	}

	@Override
	public String getHelpContent() {
		return Resources.INSTANCE.stepHelp_AnySoftwareData().getText();
	}

}
