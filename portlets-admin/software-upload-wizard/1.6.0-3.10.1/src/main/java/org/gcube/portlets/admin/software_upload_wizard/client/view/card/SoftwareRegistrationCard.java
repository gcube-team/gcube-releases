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
import org.gcube.portlets.admin.software_upload_wizard.client.view.widget.ExtendedMavenCoordinatesFieldSet;
import org.gcube.portlets.admin.software_upload_wizard.shared.DataDictionary;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.GetStringData;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.GetStringDataResult;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.SetStringData;
import org.gcube.portlets.admin.software_upload_wizard.shared.softwareprofile.Version;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.widget.form.FormButtonBinding;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class SoftwareRegistrationCard extends WizardCard {

	FormButtonBinding binding = new FormButtonBinding(this);

	private DispatchAsync dispatchAsync = Util.getDispatcher();
	private HandlerManager eventBus = Util.getEventBus();
	private WizardWindow window = Util.getWindow();

	private ExtendedMavenCoordinatesFieldSet mavenCoordinatesPanel = new ExtendedMavenCoordinatesFieldSet();

	public SoftwareRegistrationCard() {
		super("Edit Maven artifact coordinates");

		binding.addButton(window.getNextButton());

		InfoPanel stepInfo = new InfoPanel();
		stepInfo.setText(Resources.INSTANCE.stepInfo_SoftwareRegistrationData().getText());
		this.add(stepInfo);
		this.add(mavenCoordinatesPanel, new FormData("100%"));
	}

	@Override
	public void setup() {
		loadData();
	}

	private void loadData() {
		Log.debug("Loading data...");
		this.mask();
		
		dispatchAsync.execute(new BatchAction(OnException.CONTINUE,
				new GetStringData(DataDictionary.ARTIFACT_ID),
				new GetStringData(DataDictionary.ARTIFACT_GROUPID),
				new GetStringData(DataDictionary.ARTIFACT_VERSION),
				new GetStringData(DataDictionary.ARTIFACT_ISSNAPSHOT),
				new GetStringData(DataDictionary.ARTIFACT_FILENAME)),
				new AsyncCallback<BatchResult>() {

					@Override
					public void onFailure(Throwable caught) {
					}

					@Override
					public void onSuccess(BatchResult result) {

						String artifactId = result.getResult(0,
								GetStringDataResult.class).getValue();
						String groupId = (String) result.getResult(1,
								GetStringDataResult.class).getValue();
						Version version = Version.valueOf(result.getResult(2,
								GetStringDataResult.class).getValue());
						Boolean isSnapshot = Boolean.valueOf(result.getResult(
								3, GetStringDataResult.class).getValue());
						String filename = result.getResult(4,
								GetStringDataResult.class).getValue();

						if (artifactId != null)
							mavenCoordinatesPanel.setArtifactId(artifactId);
						if (groupId != null)
							mavenCoordinatesPanel.setGroupId(groupId);
						if (version != null)
							mavenCoordinatesPanel.setVersion(version);
						if (isSnapshot != null)
							mavenCoordinatesPanel.setSnapshot(isSnapshot
									.booleanValue());
						if (filename != null)
							mavenCoordinatesPanel.setFilename(filename);

						
						SoftwareRegistrationCard.this.unmask();
						Log.debug("Data loaded");
					}
				});

	}

	@Override
	public void performNextStepLogic() {
		saveDataAndFireEvent(new GoAheadEvent(this));
	}

	private <T extends GwtEvent<H>, H extends EventHandler> void saveDataAndFireEvent(
			final T event) {
		String artifactId = mavenCoordinatesPanel.getArtifactId();
		String groupId = mavenCoordinatesPanel.getGroupId();
		String version = mavenCoordinatesPanel.getVersion().toString();
		String isSnapshot = new Boolean(mavenCoordinatesPanel.getSnapshot())
				.toString();
		String filename = mavenCoordinatesPanel.getFilename();

		Log.debug("Saving data...");
		dispatchAsync.execute(new BatchAction(OnException.CONTINUE,
				new SetStringData(DataDictionary.ARTIFACT_ID, artifactId),
				new SetStringData(DataDictionary.ARTIFACT_GROUPID, groupId),
				new SetStringData(DataDictionary.ARTIFACT_VERSION, version),
				new SetStringData(DataDictionary.ARTIFACT_ISSNAPSHOT,
						isSnapshot), new SetStringData(
						DataDictionary.ARTIFACT_FILENAME, filename)),
				new AsyncCallback<BatchResult>() {

					@Override
					public void onFailure(Throwable caught) {
					}

					@Override
					public void onSuccess(BatchResult result) {
						Log.debug("Data saved");
						eventBus.fireEvent(event);
					}
				});
	}

	@Override
	public void performBackStepLogic() {
		saveDataAndFireEvent(new GoBackEvent(this));
	}

	@Override
	public String getHelpContent() {
		return Resources.INSTANCE.stepHelp_SoftwareRegistrationData().getText();
	}

}
