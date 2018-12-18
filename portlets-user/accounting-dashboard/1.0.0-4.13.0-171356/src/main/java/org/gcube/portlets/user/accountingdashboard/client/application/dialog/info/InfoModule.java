package org.gcube.portlets.user.accountingdashboard.client.application.dialog.info;


import com.gwtplatform.mvp.client.gin.AbstractPresenterModule;

/**
 * 
 * @author Giancarlo Panichi
 *
 */
public class InfoModule extends AbstractPresenterModule {
	@Override
	protected void configure() {
		bindPresenterWidget(InfoPresenter.class, InfoPresenter.InfoPresenterView.class, InfoView.class);
	}
}
