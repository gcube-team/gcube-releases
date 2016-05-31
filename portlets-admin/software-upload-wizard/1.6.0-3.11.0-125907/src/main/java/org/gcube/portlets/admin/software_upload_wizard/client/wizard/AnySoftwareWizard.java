package org.gcube.portlets.admin.software_upload_wizard.client.wizard;

import java.util.ArrayList;
import java.util.Arrays;

import org.gcube.portlets.admin.software_upload_wizard.client.view.card.AnySoftwareDataCard;
import org.gcube.portlets.admin.software_upload_wizard.client.view.card.FileUploadCard;
import org.gcube.portlets.admin.software_upload_wizard.client.view.card.MavenDependenciesCard;
import org.gcube.portlets.admin.software_upload_wizard.client.view.card.SubmitCard;
import org.gcube.portlets.admin.software_upload_wizard.client.view.card.generalinfo.GeneralSoftwareInfoCard;
import org.gcube.portlets.admin.software_upload_wizard.client.view.card.generalinfo.InstallNotesCard;
import org.gcube.portlets.admin.software_upload_wizard.client.view.card.generalinfo.LicenseCard;
import org.gcube.portlets.admin.software_upload_wizard.client.view.card.generalinfo.MaintainersAndChangesCard;

public class AnySoftwareWizard extends StrategyWizard {
	
	public AnySoftwareWizard(ArrayList<String> packageIds) {
		super(new ArrayWizard(Arrays.asList(
			 	new AnySoftwareDataCard(packageIds.get(0)),
				new FileUploadCard(packageIds.get(0)),
				new MavenDependenciesCard(packageIds.get(0)),
				new GeneralSoftwareInfoCard(packageIds.get(0)),
				new MaintainersAndChangesCard(packageIds.get(0)), 
				new InstallNotesCard(),
				new LicenseCard(), 
				new SubmitCard())));
	}

}
