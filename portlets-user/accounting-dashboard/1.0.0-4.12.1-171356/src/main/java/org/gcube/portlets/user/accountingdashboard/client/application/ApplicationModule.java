package org.gcube.portlets.user.accountingdashboard.client.application;


import org.gcube.portlets.user.accountingdashboard.client.application.controller.ControllerModule;
import org.gcube.portlets.user.accountingdashboard.client.application.dialog.error.ErrorModule;
import org.gcube.portlets.user.accountingdashboard.client.application.dialog.info.InfoModule;
import org.gcube.portlets.user.accountingdashboard.client.application.dialog.monitor.MonitorModule;
import org.gcube.portlets.user.accountingdashboard.client.application.mainarea.MainAreaModule;
import org.gcube.portlets.user.accountingdashboard.client.application.menu.MenuModule;
import org.gcube.portlets.user.accountingdashboard.client.application.providers.DataProviderModule;

import com.gwtplatform.mvp.client.gin.AbstractPresenterModule;



/**
 * 
 * @author Giancarlo Panichi
 *
 */
public class ApplicationModule extends AbstractPresenterModule {
	@Override
	protected void configure() {
		install(new MonitorModule());
		install(new InfoModule());
		install(new ErrorModule());
		install(new ControllerModule());
		install(new DataProviderModule());
		install(new MenuModule());
		install(new MainAreaModule());
		

		bindPresenter(ApplicationPresenter.class, ApplicationPresenter.PresenterView.class, ApplicationView.class,
				ApplicationPresenter.ApplicationPresenterProxy.class);
	}
}
