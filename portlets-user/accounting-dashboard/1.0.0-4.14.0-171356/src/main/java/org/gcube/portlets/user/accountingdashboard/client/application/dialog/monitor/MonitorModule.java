package org.gcube.portlets.user.accountingdashboard.client.application.dialog.monitor;


import com.gwtplatform.mvp.client.gin.AbstractPresenterModule;

/**
 * 
 * @author Giancarlo Panichi
 *
 */
public class MonitorModule extends AbstractPresenterModule {
	@Override
	protected void configure() {
		bindPresenterWidget(MonitorPresenter.class, MonitorPresenter.MonitorPresenterView.class, MonitorView.class);
	}
}
