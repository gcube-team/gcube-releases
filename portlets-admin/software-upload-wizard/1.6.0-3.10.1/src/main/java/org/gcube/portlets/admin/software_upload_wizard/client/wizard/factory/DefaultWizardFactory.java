package org.gcube.portlets.admin.software_upload_wizard.client.wizard.factory;

import java.util.ArrayList;

import org.gcube.portlets.admin.software_upload_wizard.client.wizard.AnySoftwareWizard;
import org.gcube.portlets.admin.software_upload_wizard.client.wizard.GCubePatchWizard;
import org.gcube.portlets.admin.software_upload_wizard.client.wizard.GCubePluginWizard;
import org.gcube.portlets.admin.software_upload_wizard.client.wizard.GCubeWebServiceStaticWizard;
import org.gcube.portlets.admin.software_upload_wizard.client.wizard.IWizard;
import org.gcube.portlets.admin.software_upload_wizard.client.wizard.LibraryWizard;
import org.gcube.portlets.admin.software_upload_wizard.client.wizard.SoftwareRegistrationWizard;
import org.gcube.portlets.admin.software_upload_wizard.client.wizard.WebAppWizard;
import org.gcube.portlets.admin.software_upload_wizard.shared.softwaretypes.SoftwareTypeCode;

public class DefaultWizardFactory implements WizardFactory  {

	@Override
	public IWizard getWizard(SoftwareTypeCode code, ArrayList<String> packageIds) throws Exception {
		switch (code) {
		case WebApp:
			return new WebAppWizard(packageIds);
		case Library:
			return new LibraryWizard(packageIds);
		case SoftwareRegistration:
			return new SoftwareRegistrationWizard(packageIds);
		case AnySoftware:
			return new AnySoftwareWizard(packageIds);
		case gCubePatch:
			return new GCubePatchWizard(packageIds);
		case gCubePlugin:
			return new GCubePluginWizard(packageIds);
		case gCubeWebService:
			return new GCubeWebServiceStaticWizard(packageIds);
		default:
			throw new Exception("Wizard not implemented for code: " + code );
		}
	}

}
