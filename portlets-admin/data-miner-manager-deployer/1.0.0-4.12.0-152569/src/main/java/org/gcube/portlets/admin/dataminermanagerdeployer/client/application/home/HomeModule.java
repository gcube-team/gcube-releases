package org.gcube.portlets.admin.dataminermanagerdeployer.client.application.home;


import com.gwtplatform.mvp.client.gin.AbstractPresenterModule;

/**
 * 
 * @author Giancarlo Panichi
 *
 */
public class HomeModule extends AbstractPresenterModule {
	@Override
	protected void configure() {
		bindPresenter(HomePresenter.class, HomePresenter.PresenterView.class, HomeView.class, HomePresenter.PresenterProxy.class);
	}
}
