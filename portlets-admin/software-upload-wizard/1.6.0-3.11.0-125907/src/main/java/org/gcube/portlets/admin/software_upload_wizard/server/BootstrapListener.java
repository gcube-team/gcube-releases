package org.gcube.portlets.admin.software_upload_wizard.server;

import net.customware.gwt.dispatch.server.guice.ServerDispatchModule;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;

public class BootstrapListener extends GuiceServletContextListener {

	@Override
	protected Injector getInjector() {
		return Guice.createInjector(new ServerDispatchModule(),
				 new ManagersModule(), new ActionsModule(),	new DispatchServletModule());
	}

}
