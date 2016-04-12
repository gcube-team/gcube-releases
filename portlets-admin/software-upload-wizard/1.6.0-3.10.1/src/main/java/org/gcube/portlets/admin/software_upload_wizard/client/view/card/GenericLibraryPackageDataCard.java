package org.gcube.portlets.admin.software_upload_wizard.client.view.card;

import net.customware.gwt.dispatch.client.DispatchAsync;

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
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.SetPackageDataResult;
import org.gcube.portlets.admin.software_upload_wizard.shared.softwareprofile.PackageData;
import org.gcube.portlets.admin.software_upload_wizard.shared.softwareprofile.PackageData.PackageType;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.widget.form.FormButtonBinding;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class GenericLibraryPackageDataCard extends WizardCard {

	protected DispatchAsync dispatchAsync = Util.getDispatcher();
	protected WizardWindow window = Util.getWindow();
	protected HandlerManager eventBus = Util.getEventBus();

	protected GenericComponentInfoFieldSet packageInfoFieldSet = new GenericComponentInfoFieldSet(
			"Package");

	private FormButtonBinding binding = new FormButtonBinding(this);

	private String packageId;

	private String stepHelp ;

	public GenericLibraryPackageDataCard(String packageId,
			String additionalTitle) {
		this(packageId, additionalTitle, Resources.INSTANCE
				.stepInfo_GenericLibraryPackageData().getText(),
				Resources.INSTANCE.stepHelp_GenericLibraryPackageData()
						.getText());
	}

	public GenericLibraryPackageDataCard(String packageId,
			String additionalTitle, String stepInfo, String stepHelp) {
		super("Edit Package Service Profile Data - " + additionalTitle);

		this.packageId = packageId;
		this.stepHelp = stepHelp;

		FormData formData = new FormData("100%");

		InfoPanel stepInfoPanel = new InfoPanel();
		stepInfoPanel.setText(stepInfo);

		this.add(stepInfoPanel);
		this.add(packageInfoFieldSet, formData);
	}

	@Override
	public void setup() {
		binding.addButton(window.getNextButton());
		window.setBackButtonEnabled(true);
		loadData();
	}

	protected void loadData() {
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

						packageInfoFieldSet.setName(packageData.getName());
						packageInfoFieldSet.setDescription(packageData
								.getDescription());
						packageInfoFieldSet.setVersion(packageData.getVersion());

						GenericLibraryPackageDataCard.this.unmask();
						Log.debug("Data loaded.");
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

	protected <T extends GwtEvent<H>, H extends EventHandler> void saveDataAndFireEvent(
			final T event) {

		PackageData packageData = new PackageData(PackageType.Software);
		packageData.setName(packageInfoFieldSet.getName());
		packageData.setDescription(packageInfoFieldSet.getDescription());
		packageData.setVersion(packageInfoFieldSet.getVersion());

		Log.debug("Saving data");
		dispatchAsync.execute(new SetPackageData(packageId, packageData),
				new AsyncCallback<SetPackageDataResult>() {

					@Override
					public void onFailure(Throwable caught) {
					}

					@Override
					public void onSuccess(SetPackageDataResult result) {
						Log.debug("Data saved succesfully");
						binding.removeButton(window.getNextButton());
						eventBus.fireEvent(event);
					}
				});

	}

	@Override
	public String getHelpContent() {
		return stepHelp;
	}

}
