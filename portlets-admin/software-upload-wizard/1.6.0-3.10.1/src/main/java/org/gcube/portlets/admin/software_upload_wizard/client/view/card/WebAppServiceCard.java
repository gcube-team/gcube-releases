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
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.GetServiceData;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.GetServiceDataResult;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.GetThirdParty;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.GetThirdPartyCapability;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.GetThirdPartyCapabilityResult;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.GetThirdPartyResult;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.SetServiceData;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.SetThirdParty;
import org.gcube.portlets.admin.software_upload_wizard.shared.softwareprofile.ServiceData;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.widget.form.FormButtonBinding;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class WebAppServiceCard extends WizardCard {

	private DispatchAsync dispatchAsync = Util.getDispatcher();
	private WizardWindow window = Util.getWindow();
	private HandlerManager eventBus = Util.getEventBus();

	private FormButtonBinding binding = new FormButtonBinding(this);

	private ThirdPartyContainer thirdPartyContainer = new ThirdPartyContainer();
	private ServiceInfoFieldSet serviceFieldSet = new ServiceInfoFieldSet();
	
	public WebAppServiceCard() {
		super("Edit Service Profile Data - Service");

		this.buildUI();
	}
	
	private void buildUI() {
		FormData formData = new FormData("100%");

		InfoPanel stepInfo = new InfoPanel();
		stepInfo.setText(Resources.INSTANCE
				.stepInfo_WebAppService().getText());

		this.add(stepInfo);
		this.add(thirdPartyContainer);
		this.add(serviceFieldSet, formData);
		
//		serviceFieldSet.setEnableClassField(false);
	}

	@Override
	public void setup() {
		binding.addButton(window.getNextButton());
		window.setBackButtonEnabled(true);
		loadData();
	}

	private void loadData() {
		this.mask();
		dispatchAsync.execute(new BatchAction(OnException.CONTINUE,
				new GetThirdParty(), new GetServiceData(), new GetThirdPartyCapability()),
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
						boolean thirdPartyCapability = result.getResult(2,
								GetThirdPartyCapabilityResult.class)
								.allowsThirdParty();

						thirdPartyContainer.setCheckboxValue(thirdParty);

						serviceFieldSet.setName(serviceData.getName());
						serviceFieldSet.setDescription(serviceData
								.getDescription());
						serviceFieldSet.setVersion(serviceData.getVersion());
						serviceFieldSet.setClassName(serviceData.getClazz());

						thirdPartyContainer
								.setCheckboxEnabled(thirdPartyCapability);
						if (!thirdPartyCapability)
							thirdPartyContainer.setCheckboxValue(false);
						
						WebAppServiceCard.this.unmask();
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

		Log.debug("Saving data");
		dispatchAsync.execute(new BatchAction(OnException.ROLLBACK,
				new SetThirdParty(thirdParty), new SetServiceData(serviceData)),
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
		return Resources.INSTANCE.stepHelp_WebAppService().getText();
	}

}
