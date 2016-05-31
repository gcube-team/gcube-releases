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
import org.gcube.portlets.admin.software_upload_wizard.client.view.widget.ReadmeInfoFieldSet;
import org.gcube.portlets.admin.software_upload_wizard.shared.GeneralInfo;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.GetGeneralInfo;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.GetGeneralInfoResult;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.GetPackageData;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.GetPackageDataResult;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.GetServiceData;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.GetServiceDataResult;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.SetGeneralInfo;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.SetGeneralInfoResult;
import org.gcube.portlets.admin.software_upload_wizard.shared.softwareprofile.PackageData;
import org.gcube.portlets.admin.software_upload_wizard.shared.softwareprofile.ServiceData;
import org.gcube.portlets.admin.software_upload_wizard.shared.softwareprofile.Version;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.widget.form.FormButtonBinding;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class GeneralSoftwareInfoCard extends WizardCard {

	private static final String STEP_INFO_TEXT = Resources.INSTANCE.stepInfo_ApplicationInfo().getText();
	private DispatchAsync dispatchAsync = Util.getDispatcher();

	private WizardWindow window = Util.getWindow();
	private HandlerManager eventBus = Util.getEventBus();

	private FormButtonBinding binding = new FormButtonBinding(this);

	private ReadmeInfoFieldSet softwareFieldSet = new ReadmeInfoFieldSet();
	
	private String packageId;

	public GeneralSoftwareInfoCard(String packageId) {
		super("Application Information");

		this.packageId = packageId;
		
		InfoPanel stepInfo = new InfoPanel();
		stepInfo.setText(STEP_INFO_TEXT);

		this.add(stepInfo);
		this.add(softwareFieldSet, new FormData("100%"));
	}

	@Override
	public void setup() {
		binding.addButton(window.getNextButton());
		loadData();
	}

	@Override
	public void performNextStepLogic() {
		binding.removeButton(window.getNextButton());
		saveDataAndFireEvent(new GoAheadEvent(this));
	}

	@Override
	public void performBackStepLogic() {
		binding.removeButton(window.getNextButton());
		saveDataAndFireEvent(new GoBackEvent(this));
	}

	@Override
	public boolean isValid(boolean preventMark) {
		return super.isValid(preventMark) && softwareFieldSet.isValid();
	}

	private void loadData() {
		Log.debug("Loading data...");
		
		this.mask();
		
		dispatchAsync.execute(new BatchAction(OnException.CONTINUE, new GetGeneralInfo(), new GetServiceData(), new GetPackageData(packageId)), new AsyncCallback<BatchResult>() {

			@Override
			public void onFailure(Throwable caught) {
			}

			@Override
			public void onSuccess(BatchResult result) {
				GeneralInfo generalInfo = result.getResult(0, GetGeneralInfoResult.class).getGeneralInfo();
				
				softwareFieldSet.setName(generalInfo.getApplicationName());
				softwareFieldSet.setDescription(generalInfo
						.getApplicationDescription());
				softwareFieldSet.setVersion(generalInfo.getApplicationVersion());
				softwareFieldSet.setReleaseDate(generalInfo.getReleaseDate());
				softwareFieldSet.setUrls(generalInfo.getUrls());
				Log.debug("General info data loaded");
				
				boolean invalidName = softwareFieldSet.getName() == null
						|| softwareFieldSet.getName().equals("");
				boolean invalidDescription = softwareFieldSet.getDescription() == null
						|| softwareFieldSet.getDescription().equals("");
				boolean invalidVersion = softwareFieldSet.getVersion() == null
						|| softwareFieldSet.getVersion().equals(new Version());
				
				if (invalidName && invalidDescription && invalidVersion){
					ServiceData serviceData = result.getResult(1, GetServiceDataResult.class).getData();
					PackageData mainPackage = result.getResult(2, GetPackageDataResult.class).getData();
					
					softwareFieldSet.setName(serviceData.getName() + "-"
							+ mainPackage.getVersion().toString());
					softwareFieldSet.setDescription(mainPackage.getDescription());
					softwareFieldSet.setVersion(mainPackage.getVersion());
				}				
				
				GeneralSoftwareInfoCard.this.unmask();
				Log.debug("Data loaded.");
			}
		});
	}

	private <T extends GwtEvent<H>, H extends EventHandler> void saveDataAndFireEvent(
			final T event) {
		Log.debug("Saving data...");
		
		dispatchAsync.execute(new GetGeneralInfo(), new AsyncCallback<GetGeneralInfoResult>() {

			@Override
			public void onFailure(Throwable caught) {
			}

			@Override
			public void onSuccess(GetGeneralInfoResult result) {
				GeneralInfo generalInfo = result.getGeneralInfo();
				generalInfo.setApplicationName(softwareFieldSet.getName());
				generalInfo.setApplicationDescription(softwareFieldSet
						.getDescription());
				generalInfo.setApplicationVersion(softwareFieldSet.getVersion());
				generalInfo.setReleaseDate(softwareFieldSet.getReleaseDate());
				generalInfo.setUrls(softwareFieldSet.getUrls());

				// Save data to server
				dispatchAsync.execute(new SetGeneralInfo(generalInfo), new AsyncCallback<SetGeneralInfoResult>() {

					@Override
					public void onFailure(Throwable caught) {
					}

					@Override
					public void onSuccess(SetGeneralInfoResult result) {
						Log.debug("General software information has been saved succesfully");
						eventBus.fireEvent(event);
					}
				});
			}
		});
	}

	@Override
	public String getHelpContent() {
		return Resources.INSTANCE.stepHelp_ApplicationInfo().getText();
	}

}
