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
import org.gcube.portlets.admin.software_upload_wizard.client.view.widget.InfoPanel;
import org.gcube.portlets.admin.software_upload_wizard.client.view.widget.ServiceInfoFieldSet;
import org.gcube.portlets.admin.software_upload_wizard.client.view.widget.ThirdPartyContainer;
import org.gcube.portlets.admin.software_upload_wizard.client.view.widget.WebAppInfoFieldSet;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.GetPackageData;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.GetPackageDataResult;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.GetServiceData;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.GetServiceDataResult;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.GetThirdParty;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.GetThirdPartyCapability;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.GetThirdPartyCapabilityResult;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.GetThirdPartyResult;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.SetPackageData;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.SetServiceData;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.SetThirdParty;
import org.gcube.portlets.admin.software_upload_wizard.shared.softwareprofile.PackageData;
import org.gcube.portlets.admin.software_upload_wizard.shared.softwareprofile.ServiceData;
import org.gcube.portlets.admin.software_upload_wizard.shared.softwareprofile.PackageData.PackageType;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.widget.form.FormButtonBinding;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class WebAppDataCard extends WizardCard {

	private DispatchAsync dispatchAsync = Util.getDispatcher();
	private WizardWindow window = Util.getWindow();
	private HandlerManager eventBus = Util.getEventBus();

	private FormButtonBinding binding = new FormButtonBinding(this);

	private ThirdPartyContainer thirdPartyContainer = new ThirdPartyContainer();
	private ServiceInfoFieldSet serviceFieldSet = new ServiceInfoFieldSet();
	private WebAppInfoFieldSet webAppInfoFieldSet = new WebAppInfoFieldSet();

	private String packageId;

	public WebAppDataCard(String packageId) {
		super("Web App configuration");

		this.packageId = packageId;

		this.buildUI();
	}

	private void buildUI() {
		FormData formData = new FormData("100%");

		serviceFieldSet.setEnableClassField(false);

		InfoPanel stepInfo = new InfoPanel();
		stepInfo.setText(Resources.INSTANCE
				.stepInfo_WebAppData().getText());

		this.add(stepInfo);
		this.add(thirdPartyContainer);
		this.add(serviceFieldSet, formData);
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
		dispatchAsync.execute(new BatchAction(OnException.CONTINUE,
				new GetThirdParty(), new GetServiceData(), new GetPackageData(
						packageId), new GetThirdPartyCapability()),
				new AsyncCallback<BatchResult>() {

					@Override
					public void onFailure(Throwable caught) {
					}

					@Override
					public void onSuccess(BatchResult result) {
						boolean thirdParty = result.getResult(0,
								GetThirdPartyResult.class).isThirdParty();
						ServiceData serviceData = result.getResult(1,
								GetServiceDataResult.class).getData();
						PackageData packageData = result.getResult(2,
								GetPackageDataResult.class).getData();
						boolean thirdPartyCapability = result.getResult(3,
								GetThirdPartyCapabilityResult.class)
								.allowsThirdParty();

						thirdPartyContainer.setCheckboxValue(thirdParty);

						serviceFieldSet.setName(serviceData.getName());
						serviceFieldSet.setDescription(serviceData
								.getDescription());
						serviceFieldSet.setVersion(serviceData.getVersion());
						serviceFieldSet.setClassName(serviceData.getClazz());

						webAppInfoFieldSet.setName(packageData.getName());
						webAppInfoFieldSet.setDescription(packageData
								.getDescription());
						webAppInfoFieldSet.setVersion(packageData.getVersion());
						webAppInfoFieldSet.setEntryPoints(packageData
								.getEntrypoints());

						thirdPartyContainer
								.setCheckboxEnabled(thirdPartyCapability);
						if (!thirdPartyCapability)
							thirdPartyContainer.setCheckboxValue(false);
						WebAppDataCard.this.unmask();
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

		boolean thirdParty = thirdPartyContainer.getValue();

		ServiceData serviceData = new ServiceData();
		serviceData.setName(serviceFieldSet.getName());
		serviceData.setDescription(serviceFieldSet.getDescription());
		serviceData.setVersion(serviceFieldSet.getVersion());
		serviceData.setClazz(serviceFieldSet.getClazz());

		PackageData packageData = new PackageData(PackageType.Software);
		packageData.setName(webAppInfoFieldSet.getName());
		packageData.setDescription(webAppInfoFieldSet.getDescription());
		packageData.setVersion(webAppInfoFieldSet.getVersion());
		for (String entryPoint : webAppInfoFieldSet.getEntryPoints()) {
			packageData.getEntrypoints().add(entryPoint);
		}

		Log.debug("Saving data");
		dispatchAsync.execute(new BatchAction(OnException.ROLLBACK,
				new SetThirdParty(thirdParty), new SetServiceData(serviceData),
				new SetPackageData(packageId, packageData)),
				new AsyncCallback<BatchResult>() {

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
		return Resources.INSTANCE.stepHelp_WebAppData().getText();
	}
}
