package org.gcube.portlets.admin.software_upload_wizard.client.view.card.gcubewebservice;

import net.customware.gwt.dispatch.client.DispatchAsync;

import org.gcube.portlets.admin.software_upload_wizard.client.event.GoAheadEvent;
import org.gcube.portlets.admin.software_upload_wizard.client.event.GoBackEvent;
import org.gcube.portlets.admin.software_upload_wizard.client.util.Resources;
import org.gcube.portlets.admin.software_upload_wizard.client.util.Util;
import org.gcube.portlets.admin.software_upload_wizard.client.view.WizardWindow;
import org.gcube.portlets.admin.software_upload_wizard.client.view.card.WizardCard;
import org.gcube.portlets.admin.software_upload_wizard.client.view.widget.InfoPanel;
import org.gcube.portlets.admin.software_upload_wizard.client.view.widget.ServiceInfoFieldSet;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.GetServiceData;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.GetServiceDataResult;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.SetServiceData;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.SetServiceDataResult;
import org.gcube.portlets.admin.software_upload_wizard.shared.softwareprofile.ServiceData;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.widget.form.FormButtonBinding;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class GCubeWebServiceServiceDataCard extends WizardCard {

	private DispatchAsync dispatchAsync = Util.getDispatcher();
	private WizardWindow window = Util.getWindow();
	private HandlerManager eventBus = Util.getEventBus();

	private FormButtonBinding binding = new FormButtonBinding(this);

	private ServiceInfoFieldSet serviceFieldSet = new ServiceInfoFieldSet();

	public GCubeWebServiceServiceDataCard() {
		super("Edit Service Profile Data - Service");

		FormData formData = new FormData("100%");

		InfoPanel stepInfo = new InfoPanel();
		stepInfo.setText(Resources.INSTANCE
				.stepInfo_GCubeWebServiceServiceData().getText());

		this.setLabelWidth(150);
		this.add(stepInfo, formData);
		this.add(serviceFieldSet, formData);
	}

	@Override
	public void setup() {
		this.disable();
		binding.addButton(window.getNextButton());
		window.setBackButtonEnabled(true);
		loadData();
	}

	private void loadData() {
		Log.debug("Loading data...");
		this.mask();
		dispatchAsync.execute(new GetServiceData(),
				new AsyncCallback<GetServiceDataResult>() {

					@Override
					public void onFailure(Throwable caught) {
					}

					@Override
					public void onSuccess(GetServiceDataResult result) {
						ServiceData serviceData = result.getData();

						serviceFieldSet.setName(serviceData.getName());
						serviceFieldSet.setDescription(serviceData
								.getDescription());
						serviceFieldSet.setVersion(serviceData.getVersion());
						serviceFieldSet.setClassName(serviceData.getClazz());

						
						GCubeWebServiceServiceDataCard.this.enable();
						GCubeWebServiceServiceDataCard.this.unmask();
						
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

	private <T extends GwtEvent<H>, H extends EventHandler> void saveDataAndFireEvent(
			final T event) {
		ServiceData serviceData = new ServiceData();
		serviceData.setName(serviceFieldSet.getName());
		serviceData.setDescription(serviceFieldSet.getDescription());
		serviceData.setVersion(serviceFieldSet.getVersion());
		serviceData.setClazz(serviceFieldSet.getClazz());
		
		dispatchAsync.execute(new SetServiceData(serviceData), new AsyncCallback<SetServiceDataResult>() {

			@Override
			public void onFailure(Throwable caught) {
				
			}

			@Override
			public void onSuccess(SetServiceDataResult result) {
				binding.removeButton(window.getNextButton());
				Log.debug("Data saved.");
				eventBus.fireEvent(event);
			}
		});

	}

	@Override
	public String getHelpContent() {
		return Resources.INSTANCE.stepHelp_GCubeWebServiceServiceData()
				.getText();
	}

}
