package org.gcube.portlets.admin.software_upload_wizard.server.softwaremanagers;

import java.util.ArrayList;
import java.util.Arrays;

import net.customware.gwt.dispatch.server.guice.ServerDispatchModule;

import org.gcube.portlets.admin.software_upload_wizard.server.ActionsModule;
import org.gcube.portlets.admin.software_upload_wizard.server.DispatchServletModule;
import org.gcube.portlets.admin.software_upload_wizard.server.ManagersModule;
import org.gcube.portlets.admin.software_upload_wizard.server.softwaremanagers.exceptions.SoftwareTypeNotFoundException;
import org.gcube.portlets.admin.software_upload_wizard.shared.softwaretypes.SoftwareTypeCode;

import com.google.inject.Guice;
import com.google.inject.Injector;

//TODO replace with a more elegant solution
public class SoftwareTypeFactory {

	private static Injector injector = Guice.createInjector(new ServerDispatchModule(), new ManagersModule(),
			new ActionsModule(), new DispatchServletModule());

	public static ISoftwareTypeManager getSoftwareManager(SoftwareTypeCode code) throws Exception {

		ISoftwareTypeManager result = null;

		switch (code) {
		case WebApp:
			result = new WebAppSoftwareManager();
			break;
		case Library:
			result = new LibrarySoftwareManager();
			break;
		case SoftwareRegistration:
			result = new SoftwareRegistrationSoftwareManager();
			break;
		case AnySoftware:
			result = new AnySoftwareSoftwareManager();
			break;
		case gCubeWebService:
			result = new GCubeWebServiceSoftwareManager();
			break;
		case gCubePatch:
			result = new GCubePatchSoftwareManager();
			break;
		case gCubePlugin:
			result = new GCubePluginSoftwareManager();
			break;
		}
		if (result == null)
			throw new SoftwareTypeNotFoundException(code);
		injector.injectMembers(result);
		return result;
	}

	public static ArrayList<ISoftwareTypeManager> getAvailableSoftwareTypes() {
		return new ArrayList<ISoftwareTypeManager>(Arrays.asList(injector.getInstance(WebAppSoftwareManager.class),
				injector.getInstance(LibrarySoftwareManager.class),
				injector.getInstance(SoftwareRegistrationSoftwareManager.class),
				injector.getInstance(AnySoftwareSoftwareManager.class),
				injector.getInstance(GCubePatchSoftwareManager.class),
				injector.getInstance(GCubePluginSoftwareManager.class),
				injector.getInstance(GCubeWebServiceSoftwareManager.class)));
	}
}
