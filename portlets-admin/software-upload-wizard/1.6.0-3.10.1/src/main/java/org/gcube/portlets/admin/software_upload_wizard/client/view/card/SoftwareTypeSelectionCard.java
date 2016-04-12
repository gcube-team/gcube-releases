package org.gcube.portlets.admin.software_upload_wizard.client.view.card;

import java.util.ArrayList;

import net.customware.gwt.dispatch.client.DispatchAsync;
import net.customware.gwt.dispatch.shared.BatchAction;
import net.customware.gwt.dispatch.shared.BatchAction.OnException;
import net.customware.gwt.dispatch.shared.BatchResult;

import org.gcube.portlets.admin.software_upload_wizard.client.event.SoftwareTypeSelectedEvent;
import org.gcube.portlets.admin.software_upload_wizard.client.util.Resources;
import org.gcube.portlets.admin.software_upload_wizard.client.util.Util;
import org.gcube.portlets.admin.software_upload_wizard.client.view.WizardWindow;
import org.gcube.portlets.admin.software_upload_wizard.client.view.widget.InfoPanel;
import org.gcube.portlets.admin.software_upload_wizard.shared.ImportSessionId;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.CreateImportSession;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.CreateImportSessionResult;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.GetAvailableSoftwareTypes;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.GetAvailableSoftwareTypesResult;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.SetSoftwareType;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.SetSoftwareTypeResult;
import org.gcube.portlets.admin.software_upload_wizard.shared.softwaretypes.ISoftwareTypeInfo;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class SoftwareTypeSelectionCard extends WizardCard {

	private SoftwareWizardComboBox softwareComboBox = new SoftwareWizardComboBox();
	private InfoPanel infoPanel = new InfoPanel();

	private HandlerManager eventBus = Util.getEventBus();
	private final DispatchAsync dispatchAsync = Util.getDispatcher();
	private WizardWindow window = Util.getWindow();

	public SoftwareTypeSelectionCard() {
		super("Software type selection");

		// Assemble UI
		InfoPanel stepDescription = new InfoPanel();
		stepDescription.setText(Resources.INSTANCE
				.stepInfo_SoftwareTypeSelection().getText());

		this.add(stepDescription);
		this.add(softwareComboBox, new FormData("-20"));
		this.add(infoPanel, new FormData());
	}

	public void setup() {		
		window.getBackButton().hide();
		window.setNextButtonEnabled(false);
		loadData();
	}

	private void loadData() {
		Log.debug("Creating session and loading data...");
		dispatchAsync.execute(new BatchAction(OnException.CONTINUE,new CreateImportSession(), new GetAvailableSoftwareTypes()), new AsyncCallback<BatchResult>() {

			@Override
			public void onFailure(Throwable caught) {
			}

			@Override
			public void onSuccess(BatchResult result) {
				ImportSessionId sessionId = result.getResult(0,CreateImportSessionResult.class).getId();
				ArrayList<ISoftwareTypeInfo> types = result.getResult(1, GetAvailableSoftwareTypesResult.class).getTypes();
				
				Log.debug("New import session created with id " + sessionId.getId());
				
				softwareComboBox.setSoftwareTypes(types);
				softwareComboBox.setEmptyText("");
				softwareComboBox.enable();
				Log.debug("Data loaded");
			}
		});

	}

	@Override
	public void performNextStepLogic() {
		eventBus.fireEvent(new SoftwareTypeSelectedEvent(softwareComboBox
				.getValue().getType().getCode()));
	}

	@Override
	public void performBackStepLogic() {
		// This cannot happen
	}

	

	private void doSoftwareTypeSelectionChanged(final ISoftwareTypeInfo info) {

		infoPanel.setText(info.getDescription());
		Log.debug("Setting software type");
		dispatchAsync.execute(new SetSoftwareType(info.getCode()), new AsyncCallback<SetSoftwareTypeResult>() {

			@Override
			public void onFailure(Throwable caught) {
			}

			@Override
			public void onSuccess(SetSoftwareTypeResult result) {
				Log.debug("Software type was set correctly");
				window.setNextButtonEnabled(true);
			}
		});

	}

	private class SoftwareWizardComboBox extends
			ComboBox<SoftwareWizardComboBox.SoftwareTypeModelData> {

		ListStore<SoftwareTypeModelData> store = new ListStore<SoftwareTypeModelData>();

		public SoftwareWizardComboBox() {
			// Edit properties
			this.setFieldLabel("Software type");
			this.setAllowBlank(false);
			this.setForceSelection(true);
			this.setDisplayField(SoftwareTypeModelData.NAME);
			this.setValueField(SoftwareTypeModelData.TYPE);
			this.setTypeAhead(true);
			this.setTriggerAction(TriggerAction.ALL);
			
			this.setEmptyText("Loading software types...");
			this.disable();

			// Attach store
			this.setStore(store);

			bind();
		}

		private void bind() {
			this.addSelectionChangedListener(new SelectionChangedListener<SoftwareTypeModelData>() {

				@Override
				public void selectionChanged(

				SelectionChangedEvent<SoftwareTypeModelData> se) {
					SoftwareTypeSelectionCard.this
							.doSoftwareTypeSelectionChanged(se
									.getSelectedItem().getType());
				}

			});
		}

		public void setSoftwareTypes(ArrayList<ISoftwareTypeInfo> types) {
			store.removeAll();
			for (ISoftwareTypeInfo softwareTypeInfo : types) {
				store.add(new SoftwareTypeModelData(softwareTypeInfo));
			}
		}

		public class SoftwareTypeModelData extends BaseModelData {

			private static final long serialVersionUID = -4583790526707822917L;

			public static final String NAME = "NAME";
			public static final String TYPE = "TYPE";

			public SoftwareTypeModelData(ISoftwareTypeInfo info) {
				set(NAME, info.getName());
				set(TYPE, info);
			}

			public ISoftwareTypeInfo getType() {
				return get(TYPE);
			}
		}
	}

	@Override
	public String getHelpContent() {
		return Resources.INSTANCE.stepHelp_SoftwareTypeSelection().getText();
	}

}
