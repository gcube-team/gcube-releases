package org.gcube.portlets.admin.software_upload_wizard.server;

import net.customware.gwt.dispatch.server.guice.GuiceStandardDispatchServlet;

import org.gcube.portlets.admin.software_upload_wizard.server.servlet.FileUploadServlet;

import com.google.inject.servlet.ServletModule;

public class DispatchServletModule extends ServletModule {

	@Override
	protected void configureServlets() {
		/** TODO: Switch to "softwareuploadwizard when testing in standalone mode **/
		
//		serve("/softwareuploadwizard/dispatch").with(GuiceStandardDispatchServlet.class);
//		serve("/softwareuploadwizard/FileUploadServlet").with(FileUploadServlet.class);
		
		serve("/resourcemanagementportlet/dispatch").with(GuiceStandardDispatchServlet.class);
		serve("/resourcemanagementportlet/FileUploadServlet").with(FileUploadServlet.class);

	}

}
