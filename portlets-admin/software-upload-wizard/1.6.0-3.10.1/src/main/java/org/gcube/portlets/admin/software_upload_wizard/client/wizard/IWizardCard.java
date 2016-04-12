package org.gcube.portlets.admin.software_upload_wizard.client.wizard;

public interface IWizardCard {

	public void setup();

	public void performNextStepLogic();

	public void performBackStepLogic();
		
}
