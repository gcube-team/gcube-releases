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
import org.gcube.portlets.admin.software_upload_wizard.client.view.widget.ChangesFieldSet;
import org.gcube.portlets.admin.software_upload_wizard.client.view.widget.InfoPanel;
import org.gcube.portlets.admin.software_upload_wizard.client.view.widget.MaintainersFieldset;
import org.gcube.portlets.admin.software_upload_wizard.shared.GeneralInfo;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.GetGeneralInfo;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.GetGeneralInfoResult;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.GetPackageData;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.GetPackageDataResult;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.SetGeneralInfo;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.SetGeneralInfoResult;
import org.gcube.portlets.admin.software_upload_wizard.shared.softwareprofile.PackageData;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.widget.form.FormButtonBinding;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class MaintainersAndChangesCard extends WizardCard {

	private static final String STEP_INFO_TEXT = Resources.INSTANCE
			.stepInfo_MaintainersAndChanges().getText();

	private WizardWindow window = Util.getWindow();
	private DispatchAsync dispatchAsync = Util.getDispatcher();
	private HandlerManager eventBus = Util.getEventBus();

	private FormButtonBinding binding = new FormButtonBinding(this);

	private MaintainersFieldset maintainersFieldSet = new MaintainersFieldset();
	private ChangesFieldSet changesFieldSet = new ChangesFieldSet();

	private String packageId;

	public MaintainersAndChangesCard(String packageId) {
		super("Maintaners and Changes");

		this.packageId = packageId;

		InfoPanel stepInfo = new InfoPanel();
		stepInfo.setText(STEP_INFO_TEXT);

		this.add(stepInfo);
		this.add(maintainersFieldSet, new FormData("100%"));
		this.add(changesFieldSet, new FormData("100%"));
	}

	@Override
	public void setup() {
		binding.addButton(window.getNextButton());
		window.setBackButtonEnabled(true);
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

	@Override
	public boolean isValid(boolean preventMark) {
		return super.isValid(preventMark) && maintainersFieldSet.isValid()
				&& changesFieldSet.isValid();
	}

	private void loadData() {
		Log.debug("Loading data..");
		this.mask();
		dispatchAsync.execute(new BatchAction(OnException.CONTINUE,
				new GetGeneralInfo(), new GetPackageData(packageId)),
				new AsyncCallback<BatchResult>() {

					@Override
					public void onFailure(Throwable caught) {
					}

					@Override
					public void onSuccess(BatchResult result) {
						GeneralInfo generalInfo = result.getResult(0, GetGeneralInfoResult.class).getGeneralInfo();
						
						
						if (generalInfo.getMaintainers() != null
								&& !generalInfo.getMaintainers().equals(""))
							maintainersFieldSet.setMaintainers(generalInfo
									.getMaintainers());

						if (generalInfo.getComponentName() == null
								|| generalInfo.getComponentName().equals("")){
							PackageData packageData = result.getResult(1, GetPackageDataResult.class).getData();

							changesFieldSet.setComponentName(packageData.getName()
									+ "-" + packageData.getVersion().toString());
						}

						changesFieldSet.setChanges(generalInfo.getChanges());
						
						MaintainersAndChangesCard.this.unmask();
						
						Log.debug("Data loaded");
					}
				});
	}

	private <T extends GwtEvent<H>, H extends EventHandler> void saveDataAndFireEvent(
			final T event) {
		Log.debug("Saving data..");
		dispatchAsync.execute(new GetGeneralInfo(),
				new AsyncCallback<GetGeneralInfoResult>() {

			@Override
			public void onFailure(Throwable caught) {
			}

			@Override
			public void onSuccess(GetGeneralInfoResult result) {
				GeneralInfo generalInfo = result.getGeneralInfo();

				generalInfo.setComponentName(changesFieldSet.getComponentName());
				generalInfo.setChanges(changesFieldSet.getChanges());

				generalInfo.setMaintainers(maintainersFieldSet.getMaintainers());
				
				dispatchAsync.execute(new SetGeneralInfo(generalInfo), new AsyncCallback<SetGeneralInfoResult>() {

					@Override
					public void onFailure(Throwable caught) {
					}

					@Override
					public void onSuccess(SetGeneralInfoResult result) {
						Log.info("Data saved");
						eventBus.fireEvent(event);
					}
				});
			}
			
		});

	}

	@Override
	public String getHelpContent() {
		return Resources.INSTANCE.stepHelp_MaintainersAndChanges().getText();
	}

}
