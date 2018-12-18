package org.gcube.portlets.user.accountingdashboard.client.application.dialog.error;


import com.gwtplatform.mvp.client.gin.AbstractPresenterModule;

/**
 * 
 * @author Giancarlo Panichi
 *
 */
public class ErrorModule extends AbstractPresenterModule {
	@Override
	protected void configure() {
		bindPresenterWidget(ErrorPresenter.class, ErrorPresenter.ErrorPresenterView.class, ErrorView.class);
	}
}
