package org.gcube.portlets.admin.software_upload_wizard.client.view.card;

import java.util.Iterator;

import net.customware.gwt.dispatch.client.DispatchAsync;
import net.customware.gwt.dispatch.shared.BatchAction;
import net.customware.gwt.dispatch.shared.BatchAction.OnException;
import net.customware.gwt.dispatch.shared.BatchResult;
import net.customware.gwt.dispatch.shared.Result;

import org.gcube.portlets.admin.software_upload_wizard.client.event.GoAheadEvent;
import org.gcube.portlets.admin.software_upload_wizard.client.event.GoBackEvent;
import org.gcube.portlets.admin.software_upload_wizard.client.util.Resources;
import org.gcube.portlets.admin.software_upload_wizard.client.util.Util;
import org.gcube.portlets.admin.software_upload_wizard.client.view.WizardWindow;
import org.gcube.portlets.admin.software_upload_wizard.client.view.widget.InfoPanel;
import org.gcube.portlets.admin.software_upload_wizard.client.view.widget.PackageInfoFieldSet;
import org.gcube.portlets.admin.software_upload_wizard.client.view.widget.ServiceInfoFieldSet;
import org.gcube.portlets.admin.software_upload_wizard.client.view.widget.ThirdPartyContainer;
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
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.form.FormButtonBinding;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.rpc.AsyncCallback;

public final class LibraryDataCard extends WizardCard {

	private WizardWindow window = Util.getWindow();
	private DispatchAsync dispatchAsync = Util.getDispatcher();
	private HandlerManager eventBus = Util.getEventBus();

	private FormButtonBinding binding = new FormButtonBinding(this);

	private ThirdPartyContainer thirdPartyContainer = new ThirdPartyContainer();
	private ServiceInfoFieldSet serviceFieldSet = new ServiceInfoFieldSet();
	private PackageInfoFieldSet applicationFieldSet = new PackageInfoFieldSet(
			"Library Package");

	private String packageId;

	public LibraryDataCard(String packageId) {
		super("Edit Library Data");

		this.packageId = packageId;

		FormData formData = new FormData("100%");

		InfoPanel stepInfo = new InfoPanel();
		stepInfo.setText(Resources.INSTANCE.stepInfo_LibraryData().getText());

		this.add(stepInfo);
		this.add(thirdPartyContainer, new FormData());
		this.add(serviceFieldSet, formData);
		this.add(applicationFieldSet, formData);
		
		bind();
	}

	private void bind() {
		thirdPartyContainer.getCheckBox().addListener(Events.Change, new Listener<FieldEvent>() {

			@Override
			public void handleEvent(FieldEvent be) {
				if (thirdPartyContainer.getValue()){
					serviceFieldSet.setClassName("External");
					serviceFieldSet.setEnableClassField(false);
				} else {
					serviceFieldSet.setClassName("");
					serviceFieldSet.setEnableClassField(true);
				}
				
			}
		});
		
	}

	@Override
	public void setup() {
		binding.addButton(window.getNextButton());
		loadData();
	}

	private void loadData() {
		loadProfileData();
		setThirdPartyUploadCapability();
	}

	private void loadProfileData() {
		Log.debug("Loading data from software profile");
		this.mask();

		dispatchAsync.execute(new BatchAction(OnException.CONTINUE,
				new GetThirdParty(), new GetServiceData(), new GetPackageData(
						packageId)), new AsyncCallback<BatchResult>() {

			@Override
			public void onFailure(Throwable caught) {
			}

			@Override
			public void onSuccess(BatchResult result) {
				Iterator<Result> i = result.iterator();

				GetThirdPartyResult tpResult = (GetThirdPartyResult) i.next();
				boolean thirdParty = tpResult.isThirdParty();
				thirdPartyContainer.setCheckboxValue(thirdParty);

				GetServiceDataResult sdResult = (GetServiceDataResult) i.next();
				ServiceData serviceData = sdResult.getData();

				serviceFieldSet.setName(serviceData.getName());
				serviceFieldSet.setDescription(serviceData.getDescription());
				serviceFieldSet.setVersion(serviceData.getVersion());

				if (thirdParty)
					serviceFieldSet.setClassName(serviceData.getClazz());

				GetPackageDataResult pdResult = (GetPackageDataResult) i.next();
				PackageData packageData = pdResult.getData();

				applicationFieldSet.setName(packageData.getName());
				applicationFieldSet.setDescription(packageData.getDescription());
				applicationFieldSet.setVersion(packageData.getVersion());
				
				LibraryDataCard.this.unmask();
			}
		});

	}

	private void setThirdPartyUploadCapability() {
		dispatchAsync.execute(new GetThirdPartyCapability(), new AsyncCallback<GetThirdPartyCapabilityResult>() {

			@Override
			public void onFailure(Throwable caught) {
			}

			@Override
			public void onSuccess(GetThirdPartyCapabilityResult result) {
				boolean allowsThirdParty = result.allowsThirdParty();
				Log.trace("Allows third party software upload: " + allowsThirdParty);
				thirdPartyContainer.setCheckboxEnabled(allowsThirdParty);
				if (!allowsThirdParty) {
					thirdPartyContainer.setCheckboxValue(false);
				}
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
		
		boolean thirdParty = thirdPartyContainer.getValue();
		
		ServiceData serviceData = new ServiceData();
		serviceData.setName(serviceFieldSet.getName());
		serviceData.setDescription(serviceFieldSet.getDescription());
		serviceData.setVersion(serviceFieldSet.getVersion());
		serviceData.setClazz(serviceFieldSet.getClazz());
		
		PackageData packageData = new PackageData(PackageType.Software);
		
		packageData.setName(applicationFieldSet.getName());
		packageData.setDescription(applicationFieldSet
				.getDescription());
		packageData.setVersion(applicationFieldSet.getVersion());
		
		Log.debug("Saving data");
		dispatchAsync.execute(new BatchAction(OnException.ROLLBACK,new SetThirdParty(thirdParty),new SetServiceData(serviceData),new SetPackageData(packageId, packageData)), new AsyncCallback<BatchResult>() {

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
		return Resources.INSTANCE.stepHelp_LibraryData().getText();
	}

}
