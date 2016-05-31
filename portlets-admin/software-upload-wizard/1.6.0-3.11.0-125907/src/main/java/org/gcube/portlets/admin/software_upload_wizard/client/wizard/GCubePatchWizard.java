package org.gcube.portlets.admin.software_upload_wizard.client.wizard;

import java.util.ArrayList;
import java.util.Arrays;

import org.gcube.portlets.admin.software_upload_wizard.client.view.card.FileUploadCard;
import org.gcube.portlets.admin.software_upload_wizard.client.view.card.GCubePatchDataCard;
import org.gcube.portlets.admin.software_upload_wizard.client.view.card.SubmitCard;
import org.gcube.portlets.admin.software_upload_wizard.client.view.card.generalinfo.GeneralSoftwareInfoCard;
import org.gcube.portlets.admin.software_upload_wizard.client.view.card.generalinfo.InstallNotesCard;
import org.gcube.portlets.admin.software_upload_wizard.client.view.card.generalinfo.LicenseCard;
import org.gcube.portlets.admin.software_upload_wizard.client.view.card.generalinfo.MaintainersAndChangesCard;

public class GCubePatchWizard extends StrategyWizard {
public GCubePatchWizard(ArrayList<String> packageIds) {
	super(new ArrayWizard(Arrays.asList(
			new GCubePatchDataCard(packageIds.get(0)),
			new FileUploadCard(packageIds.get(0)),
			new GeneralSoftwareInfoCard(packageIds.get(0)),
			new MaintainersAndChangesCard(packageIds.get(0)),
			new InstallNotesCard(),
			new LicenseCard(),
			new SubmitCard())));
}
}
