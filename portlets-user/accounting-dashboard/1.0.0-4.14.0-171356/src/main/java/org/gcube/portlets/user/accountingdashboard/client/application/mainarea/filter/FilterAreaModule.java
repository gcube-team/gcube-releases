package org.gcube.portlets.user.accountingdashboard.client.application.mainarea.filter;


import com.gwtplatform.mvp.client.gin.AbstractPresenterModule;

/**
 * 
 * @author Giancarlo Panichi
 *
 */
public class FilterAreaModule extends AbstractPresenterModule {
	@Override
	protected void configure() {
		bindPresenterWidget(FilterAreaPresenter.class, FilterAreaPresenter.FilterAreaView.class, FilterAreaView.class);
	}
}
