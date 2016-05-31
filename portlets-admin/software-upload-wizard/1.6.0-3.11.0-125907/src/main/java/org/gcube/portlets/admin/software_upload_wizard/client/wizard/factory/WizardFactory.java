package org.gcube.portlets.admin.software_upload_wizard.client.wizard.factory;

import java.util.ArrayList;

import org.gcube.portlets.admin.software_upload_wizard.client.wizard.IWizard;
import org.gcube.portlets.admin.software_upload_wizard.shared.softwaretypes.SoftwareTypeCode;

public interface WizardFactory {
	
	public IWizard getWizard(SoftwareTypeCode code, ArrayList<String> packageIds) throws Exception;

}
