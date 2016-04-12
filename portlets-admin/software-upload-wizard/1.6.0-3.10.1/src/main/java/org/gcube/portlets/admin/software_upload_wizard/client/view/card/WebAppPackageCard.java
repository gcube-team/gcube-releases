package org.gcube.portlets.admin.software_upload_wizard.client.view.card;

import net.customware.gwt.dispatch.client.DispatchAsync;

import org.gcube.portlets.admin.software_upload_wizard.client.event.GoAheadEvent;
import org.gcube.portlets.admin.software_upload_wizard.client.event.GoBackEvent;
import org.gcube.portlets.admin.software_upload_wizard.client.util.Resources;
import org.gcube.portlets.admin.software_upload_wizard.client.util.Util;
import org.gcube.portlets.admin.software_upload_wizard.client.view.WizardWindow;
import org.gcube.portlets.admin.software_upload_wizard.client.view.widget.InfoPanel;
import org.gcube.portlets.admin.software_upload_wizard.client.view.widget.WebAppInfoFieldSet;
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

public class WebAppPackageCard extends WizardCard {
	private DispatchAsync dispatchAsync = Util.getDispatcher();
	private WizardWindow window = Util.getWindow();
	private HandlerManager eventBus = Util.getEventBus();

	private FormButtonBinding binding = new FormButtonBinding(this);

	private WebAppInfoFieldSet webAppInfoFieldSet = new WebAppInfoFieldSet();

	private String packageId;

	public WebAppPackageCard(String packageId) {
		super("Edit Service Profile Data - Web App Package");

		this.packageId = packageId;

		this.buildUI();
	}

	private void buildUI() {
		FormData formData = new FormData("100%");

		

		InfoPanel stepInfo = new InfoPanel();
		stepInfo.setText(Resources.INSTANCE
				.stepInfo_WebAppPackage().getText());

		this.add(stepInfo);
		this.add(webAppInfoFieldSet, formData);
	}

	@Override
	public void setup() {
		binding.addButton(window.getNextButton());
		window.setBackButtonEnabled(true);
		loadData();
	}

	private void loadData() {
		Log.debug("Loading data...");
		this.mask();
		dispatchAsync.execute(new GetPackageData(
						packageId),
				new AsyncCallback<GetPackageDataResult>() {

					@Override
					public void onFailure(Throwable caught) {
					}

					@Override
					public void onSuccess(GetPackageDataResult result) {
					
						PackageData packageData = result.getData();

						webAppInfoFieldSet.setName(packageData.getName());
						webAppInfoFieldSet.setDescription(packageData
								.getDescription());
						webAppInfoFieldSet.setVersion(packageData.getVersion());
						webAppInfoFieldSet.setEntryPoints(packageData
								.getEntrypoints());
						
						WebAppPackageCard.this.unmask();
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

	@Override
	public boolean isValid(boolean preventMark) {
		return super.isValid(preventMark) && webAppInfoFieldSet.isValid();
	}

	private <T extends GwtEvent<H>, H extends EventHandler> void saveDataAndFireEvent(
			final T event) {

		PackageData packageData = new PackageData(PackageType.Software);
		packageData.setName(webAppInfoFieldSet.getName());
		packageData.setDescription(webAppInfoFieldSet.getDescription());
		packageData.setVersion(webAppInfoFieldSet.getVersion());
		for (String entryPoint : webAppInfoFieldSet.getEntryPoints()) {
			packageData.getEntrypoints().add(entryPoint);
		}

		Log.debug("Saving data...");
		dispatchAsync.execute(new SetPackageData(packageId, packageData),
				new AsyncCallback<SetPackageDataResult>() {

					@Override
					public void onFailure(Throwable caught) {
					}

					@Override
					public void onSuccess(SetPackageDataResult result) {
						Log.debug("Data saved.");
						binding.removeButton(window.getNextButton());
						eventBus.fireEvent(event);
					}
				});

	}

	@Override
	public String getHelpContent() {
		return Resources.INSTANCE.stepHelp_WebAppPackage().getText();
	}
}
