package org.gcube.portlets.admin.software_upload_wizard.client.wizard;

import java.util.ArrayList;
import java.util.Arrays;

import org.gcube.portlets.admin.software_upload_wizard.client.view.card.FileUploadCard;
import org.gcube.portlets.admin.software_upload_wizard.client.view.card.GenericLibraryPackageDataCard;
import org.gcube.portlets.admin.software_upload_wizard.client.view.card.MavenDependenciesCard;
import org.gcube.portlets.admin.software_upload_wizard.client.view.card.PackageArtifactCoordinatesCard;
import org.gcube.portlets.admin.software_upload_wizard.client.view.card.SubmitCard;
import org.gcube.portlets.admin.software_upload_wizard.client.view.card.gcubewebservice.GCubeWebServiceMainPackageDataCard;
import org.gcube.portlets.admin.software_upload_wizard.client.view.card.gcubewebservice.GCubeWebServiceServiceDataCard;
import org.gcube.portlets.admin.software_upload_wizard.client.view.card.generalinfo.GeneralSoftwareInfoCard;
import org.gcube.portlets.admin.software_upload_wizard.client.view.card.generalinfo.InstallNotesCard;
import org.gcube.portlets.admin.software_upload_wizard.client.view.card.generalinfo.LicenseCard;
import org.gcube.portlets.admin.software_upload_wizard.client.view.card.generalinfo.MaintainersAndChangesCard;

public class GCubeWebServiceStaticWizard extends StrategyWizard {

	public GCubeWebServiceStaticWizard(ArrayList<String> packageIds) {
		super(new ArrayWizard(Arrays.asList(
				new GCubeWebServiceServiceDataCard(),
				new GCubeWebServiceMainPackageDataCard(packageIds.get(0)),
				new FileUploadCard(packageIds.get(0), "Main Package"),
				new PackageArtifactCoordinatesCard(packageIds.get(0), "Main Package"),
				new MavenDependenciesCard(packageIds.get(0), "Main Package"),
				new GenericLibraryPackageDataCard(packageIds.get(1), "Stubs Package"), 
				new FileUploadCard(packageIds.get(1),
						"Stubs Package"),
				new PackageArtifactCoordinatesCard(packageIds.get(1), "Stubs Package"),
				new MavenDependenciesCard(packageIds.get(1), "Stubs Package"),
				new GeneralSoftwareInfoCard(packageIds.get(0)),
				new MaintainersAndChangesCard(packageIds.get(0)),
				new InstallNotesCard(), 
				new LicenseCard(), 
				new SubmitCard())));
	}

}
