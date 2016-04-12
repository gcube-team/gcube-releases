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
import org.gcube.portlets.admin.software_upload_wizard.client.view.widget.TargetServiceFieldSet;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.GetPackageData;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.GetPackageDataResult;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.GetServiceData;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.GetServiceDataResult;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.SetPackageData;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.SetServiceData;
import org.gcube.portlets.admin.software_upload_wizard.shared.softwareprofile.PackageData;
import org.gcube.portlets.admin.software_upload_wizard.shared.softwareprofile.ServiceData;
import org.gcube.portlets.admin.software_upload_wizard.shared.softwareprofile.TargetService;
import org.gcube.portlets.admin.software_upload_wizard.shared.softwareprofile.PackageData.PackageType;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.widget.form.FormButtonBinding;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class GCubePluginDataCard extends WizardCard {

	private WizardWindow window = Util.getWindow();
	private DispatchAsync dispatchAsync = Util.getDispatcher();
	private HandlerManager eventBus = Util.getEventBus();

	private FormButtonBinding binding = new FormButtonBinding(this);

	private ServiceInfoFieldSet softwareFieldSet = new ServiceInfoFieldSet(
			"Software");
	private TargetServiceFieldSet targetServiceFieldSet = new TargetServiceFieldSet();

	private String packageId;

	public GCubePluginDataCard(String packageId) {
		super("Edit Plugin data");
		this.packageId = packageId;

		InfoPanel stepInfo = new InfoPanel();
		stepInfo.setText(Resources.INSTANCE.stepInfo_GCubePluginData()
				.getText());

		this.add(stepInfo);
		this.add(softwareFieldSet, new FormData("100%"));
		this.add(targetServiceFieldSet);
	}

	@Override
	public void setup() {
		binding.addButton(window.getNextButton());
		loadData();
	}

	private void loadData() {
		Log.debug("Loading data...");
		this.mask();
		dispatchAsync.execute(new BatchAction(OnException.CONTINUE,
				new GetServiceData(), new GetPackageData(packageId)),
				new AsyncCallback<BatchResult>() {

					@Override
					public void onFailure(Throwable caught) {
					}

					@Override
					public void onSuccess(BatchResult result) {
						ServiceData serviceData = result.getResult(0,
								GetServiceDataResult.class).getData();
						PackageData packageData = result.getResult(1,
								GetPackageDataResult.class).getData();
						TargetService targetService = packageData.getTargetService();

						softwareFieldSet.setName(packageData.getName());
						softwareFieldSet.setDescription(packageData
								.getDescription());
						softwareFieldSet.setVersion(packageData.getVersion());
						softwareFieldSet.setClassName(serviceData.getClazz());

						targetServiceFieldSet.setServiceName(targetService
								.getServiceName());
						targetServiceFieldSet.setServiceName(targetService
								.getServiceName());
						targetServiceFieldSet.setServiceVersion(targetService
								.getServiceVersion());
						targetServiceFieldSet.setPackagename(targetService
								.getPackageName());
						targetServiceFieldSet.setPackageVersion(targetService
								.getPackageVersion());

						GCubePluginDataCard.this.unmask();
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
		serviceData.setClazz(softwareFieldSet.getClazz());

		PackageData packageData = new PackageData(PackageType.Plugin);

		packageData.setName(softwareFieldSet.getName());
		packageData.setDescription(softwareFieldSet.getDescription());
		packageData.setVersion(softwareFieldSet.getVersion());

		TargetService targetService = new TargetService(
				targetServiceFieldSet.getServiceClass(),
				targetServiceFieldSet.getServiceName(),
				targetServiceFieldSet.getServiceVersion(),
				targetServiceFieldSet.getPackageName(),
				targetServiceFieldSet.getPackageVersion());
		packageData.setTargetService(targetService);

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
		return Resources.INSTANCE.stepHelp_GCubePluginData().getText();
	}

}
