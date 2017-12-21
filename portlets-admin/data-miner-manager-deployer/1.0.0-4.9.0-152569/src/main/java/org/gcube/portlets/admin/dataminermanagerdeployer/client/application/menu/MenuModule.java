package org.gcube.portlets.admin.dataminermanagerdeployer.client.application.menu;


import com.gwtplatform.mvp.client.gin.AbstractPresenterModule;

/**
 * 
 * @author Giancarlo Panichi
 *
 */
public class MenuModule extends AbstractPresenterModule {
	@Override
	protected void configure() {
		bindSingletonPresenterWidget(MenuPresenter.class, MenuPresenter.PresenterView.class, MenuView.class);
	}
}