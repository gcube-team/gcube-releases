package org.gcube.portlets.admin.software_upload_wizard.client.view.card.gcubewebservice;

import java.util.ArrayList;

import net.customware.gwt.dispatch.client.DispatchAsync;
import net.customware.gwt.dispatch.shared.Action;
import net.customware.gwt.dispatch.shared.BatchAction;
import net.customware.gwt.dispatch.shared.BatchAction.OnException;
import net.customware.gwt.dispatch.shared.BatchResult;

import org.gcube.portlets.admin.software_upload_wizard.client.event.GoAheadEvent;
import org.gcube.portlets.admin.software_upload_wizard.client.event.GoBackEvent;
import org.gcube.portlets.admin.software_upload_wizard.client.event.NumberOfPackagesUpdatedEvent;
import org.gcube.portlets.admin.software_upload_wizard.client.util.Resources;
import org.gcube.portlets.admin.software_upload_wizard.client.util.Util;
import org.gcube.portlets.admin.software_upload_wizard.client.view.WizardWindow;
import org.gcube.portlets.admin.software_upload_wizard.client.view.card.WizardCard;
import org.gcube.portlets.admin.software_upload_wizard.client.view.widget.InfoPanel;
import org.gcube.portlets.admin.software_upload_wizard.client.view.widget.ServiceInfoFieldSet;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.AddPackage;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.GetPackageIds;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.GetPackageIdsResult;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.GetServiceData;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.GetServiceDataResult;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.RemovePackage;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.SetServiceData;
import org.gcube.portlets.admin.software_upload_wizard.shared.softwareprofile.ServiceData;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.widget.form.FormButtonBinding;
import com.extjs.gxt.ui.client.widget.form.SpinnerField;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class GCubeWebServiceMultiPackServiceDataCard extends WizardCard {

	private DispatchAsync dispatchAsync = Util.getDispatcher();
	private WizardWindow window = Util.getWindow();
	private HandlerManager eventBus = Util.getEventBus();

	private FormButtonBinding binding = new FormButtonBinding(this);

	private ServiceInfoFieldSet serviceFieldSet = new ServiceInfoFieldSet();
	private AdditionalPackagesField additionalPackagesField = new AdditionalPackagesField();

	private ArrayList<String> currentPackageIds;

	public GCubeWebServiceMultiPackServiceDataCard() {
		super("Edit Service Profile Data - Service");

		FormData formData = new FormData("100%");

		InfoPanel stepInfo = new InfoPanel();
		stepInfo.setText(Resources.INSTANCE
				.stepInfo_GCubeWebServiceServiceData().getText());

		this.setLabelWidth(150);
		this.add(stepInfo, formData);
		this.add(serviceFieldSet, formData);
		this.add(additionalPackagesField);
	}

	@Override
	public void setup() {
		binding.addButton(window.getNextButton());
		window.setBackButtonEnabled(true);
		loadData();
	}

	private void loadData() {
		Log.debug("Loading data");
		this.mask();
		dispatchAsync.execute(new BatchAction(OnException.CONTINUE,
				new GetServiceData(), new GetPackageIds()),
				new AsyncCallback<BatchResult>() {

					@Override
					public void onFailure(Throwable caught) {
					}

					@Override
					public void onSuccess(BatchResult result) {
						ServiceData serviceData = result.getResult(0,
								GetServiceDataResult.class).getData();
						ArrayList<String> packageIds = result.getResult(1,
								GetPackageIdsResult.class).getIds();

						serviceFieldSet.setName(serviceData.getName());
						serviceFieldSet.setDescription(serviceData
								.getDescription());
						serviceFieldSet.setVersion(serviceData.getVersion());
						serviceFieldSet.setClassName(serviceData.getClazz());

						currentPackageIds = packageIds;
						additionalPackagesField.setValue(packageIds.size() - 2);
						GCubeWebServiceMultiPackServiceDataCard.this.unmask();
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

		int currentAdditionalPackagesNumber = currentPackageIds.size() - 2;
		int newAdditionalPackagesNumber = additionalPackagesField.getValue()
				.intValue();
		Action[] actions;
		if (currentAdditionalPackagesNumber != newAdditionalPackagesNumber) {
			Log.debug("Updating packages");
			if (newAdditionalPackagesNumber > currentAdditionalPackagesNumber) {
				Log.debug("Adding packages");

				int packagesNumToAdd = newAdditionalPackagesNumber
						- currentAdditionalPackagesNumber;
				actions = new Action[packagesNumToAdd + 1];
				actions[0] = new SetServiceData(serviceData);
				for (int i = 1; i < packagesNumToAdd + 1; i++)
					actions[i] = new AddPackage();
			} else {
				Log.debug("Removing packages");
				int packagesNumToRemove = currentAdditionalPackagesNumber
						- newAdditionalPackagesNumber;
				actions = new Action[packagesNumToRemove + 1];
				actions[0] = new SetServiceData(serviceData);
				for (int i = 0; i < packagesNumToRemove ; i++)
					actions[i] = new RemovePackage(
							currentPackageIds.get(currentPackageIds.size() - 1
									- i));

			}
			
		} else
			actions = new Action[] { new SetServiceData(serviceData) };
		dispatchAsync.execute(new BatchAction(OnException.CONTINUE, actions),
				new AsyncCallback<BatchResult>() {

					@Override
					public void onFailure(Throwable caught) {
					}

					@Override
					public void onSuccess(BatchResult result) {
						binding.removeButton(window.getNextButton());
						Log.debug("Data saved.");
						eventBus.fireEvent(new NumberOfPackagesUpdatedEvent());
						eventBus.fireEvent(event);
					}
				});

	}

	@Override
	public String getHelpContent() {
		return Resources.INSTANCE.stepHelp_GCubeWebServiceServiceData()
				.getText();
	}

	private class AdditionalPackagesField extends SpinnerField {
		public AdditionalPackagesField() {
			this.setFieldLabel("Number of additional packages");
			this.setIncrement(1);
			this.setPropertyEditorType(Integer.class);
			this.setMinValue(0);
			this.setMaxValue(10);
			this.setFormat(NumberFormat.getFormat("0"));
			this.setAllowBlank(false);
			this.setAllowDecimals(false);
			this.setAllowNegative(false);
			this.setWidth(30);
		}
	}

}
