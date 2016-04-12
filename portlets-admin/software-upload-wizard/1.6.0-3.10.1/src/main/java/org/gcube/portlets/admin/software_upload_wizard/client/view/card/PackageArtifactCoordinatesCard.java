package org.gcube.portlets.admin.software_upload_wizard.client.view.card;

import net.customware.gwt.dispatch.client.DispatchAsync;

import org.gcube.portlets.admin.software_upload_wizard.client.event.GoAheadEvent;
import org.gcube.portlets.admin.software_upload_wizard.client.event.GoBackEvent;
import org.gcube.portlets.admin.software_upload_wizard.client.util.Resources;
import org.gcube.portlets.admin.software_upload_wizard.client.util.Util;
import org.gcube.portlets.admin.software_upload_wizard.client.view.WizardWindow;
import org.gcube.portlets.admin.software_upload_wizard.client.view.widget.InfoPanel;
import org.gcube.portlets.admin.software_upload_wizard.client.view.widget.MavenCoordinatesFieldSet;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.GetPackageArtifactCoordinates;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.GetPackageArtifactCoordinatesResult;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.SetPackageArtifactCoordinates;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.SetPackageArtifactCoordinatesResult;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.maven.MavenCoordinates;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.widget.form.FormButtonBinding;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class PackageArtifactCoordinatesCard extends WizardCard {

	private DispatchAsync dispatchAsync = Util.getDispatcher();
	private WizardWindow window = Util.getWindow();
	private HandlerManager eventBus = Util.getEventBus();
	
	private FormButtonBinding binding = new FormButtonBinding(this);

	private String packageId;
	
	private MavenCoordinatesFieldSet artifactCoordinatesFieldSet = new MavenCoordinatesFieldSet();
	private MavenCoordinates loadedMavenCoordinates = null;

	public PackageArtifactCoordinatesCard(String packageId, String additionalHeader) {
		super("Edit Package Artifact Maven coordinates - " + additionalHeader );

		this.packageId = packageId;
		
		buildUI();
	}

	private void buildUI() {
		InfoPanel stepInfo = new InfoPanel();
		stepInfo.setText(Resources.INSTANCE
				.stepInfo_PackageArtifactCoordinates().getText());
		
		this.add(stepInfo,new FormData("100%"));
		this.add(artifactCoordinatesFieldSet, new FormData("100%"));
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
		dispatchAsync.execute(new GetPackageArtifactCoordinates(packageId),
				new AsyncCallback<GetPackageArtifactCoordinatesResult>() {

					@Override
					public void onFailure(Throwable caught) {

					}

					@Override
					public void onSuccess(
							GetPackageArtifactCoordinatesResult result) {
						loadedMavenCoordinates = result
								.getArtifactCoordinates();

						artifactCoordinatesFieldSet
								.setGroupId(loadedMavenCoordinates.getGroupId());
						artifactCoordinatesFieldSet
								.setArtifactId(loadedMavenCoordinates
										.getArtifactId());
						artifactCoordinatesFieldSet
								.setVersion(loadedMavenCoordinates.getVersion());
						artifactCoordinatesFieldSet
								.setVersionFieldBehavior(result
										.getVersionRule());

						PackageArtifactCoordinatesCard.this.unmask();
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

		loadedMavenCoordinates = new MavenCoordinates(artifactCoordinatesFieldSet.getGroupId(),artifactCoordinatesFieldSet.getArtifactId(),artifactCoordinatesFieldSet.getVersion(),loadedMavenCoordinates.getPackaging());
		
		Log.debug("Saving data");
		dispatchAsync.execute(new SetPackageArtifactCoordinates(packageId, loadedMavenCoordinates),
				new AsyncCallback<SetPackageArtifactCoordinatesResult>() {

					@Override
					public void onFailure(Throwable caught) {
					}

					@Override
					public void onSuccess(SetPackageArtifactCoordinatesResult result) {
						Log.debug("Data saved succesfully");
						binding.removeButton(window.getNextButton());
						eventBus.fireEvent(event);
					}
				});

	}

	@Override
	public String getHelpContent() {
		return Resources.INSTANCE
				.stepHelp_PackageArtifactCoordinates().getText();
	}

}
