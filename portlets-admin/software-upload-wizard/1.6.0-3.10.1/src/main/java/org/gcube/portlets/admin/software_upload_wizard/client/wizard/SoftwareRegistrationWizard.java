package org.gcube.portlets.admin.software_upload_wizard.client.wizard;

import java.util.ArrayList;
import java.util.Arrays;

import org.gcube.portlets.admin.software_upload_wizard.client.view.card.SoftwareRegistrationCard;
import org.gcube.portlets.admin.software_upload_wizard.client.view.card.SubmitCard;

public class SoftwareRegistrationWizard extends StrategyWizard {

	public SoftwareRegistrationWizard(ArrayList<String> packageIds) {
		super(new ArrayWizard(Arrays.asList(
				new SoftwareRegistrationCard(),
				new SubmitCard())));
	}

}
