package org.gcube.portlets.admin.dataminermanagerdeployer.client.application;


import org.gcube.portlets.admin.dataminermanagerdeployer.client.application.deployconfig.DeployConfModule;
import org.gcube.portlets.admin.dataminermanagerdeployer.client.application.help.HelpModule;
import org.gcube.portlets.admin.dataminermanagerdeployer.client.application.home.HomeModule;
import org.gcube.portlets.admin.dataminermanagerdeployer.client.application.menu.MenuModule;

import com.gwtplatform.mvp.client.gin.AbstractPresenterModule;



/**
 * 
 * @author Giancarlo Panichi
 *
 */
public class ApplicationModule extends AbstractPresenterModule {
	@Override
	protected void configure() {
		install(new MenuModule());
		install(new HomeModule());
		install(new HelpModule());
		install(new DeployConfModule());
		
		bindPresenter(ApplicationPresenter.class, ApplicationPresenter.PresenterView.class, ApplicationView.class,
				ApplicationPresenter.PresenterProxy.class);
	}
}
